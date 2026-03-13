package com.monuo.superaiagent.agent;

import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import com.monuo.superaiagent.agent.detector.DeadLoopDetector;
import com.monuo.superaiagent.agent.model.AgentState;
import com.monuo.superaiagent.agent.monitor.ExecutionMonitor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * <p>
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 * 
 * 重构说明：
 * - 防死循环检测逻辑已提取到 DeadLoopDetector
 * - 执行监控逻辑已提取到 ExecutionMonitor
 * - BaseAgent 只保留核心的状态管理和执行循环
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // ====== 核心属性 ======
    private String name;
    private String systemPrompt;
    private String nextStepPrompt;

    // ====== 状态管理 ======
    private AgentState state = AgentState.IDLE;
    private int maxSteps = 10;
    private int currentStep = 0;

    // ====== LLM 和消息 ======
    private ChatClient chatClient;
    private List<Message> messageList = new ArrayList<>();

    // ====== 辅助组件（重构后使用组合模式）======
    private DeadLoopDetector deadLoopDetector = new DeadLoopDetector();
    private ExecutionMonitor executionMonitor = new ExecutionMonitor();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }

        // 初始化
        state = AgentState.RUNNING;
        deadLoopDetector.reset();
        executionMonitor.start();
        messageList.add(new UserMessage(userPrompt));
        List<String> results = new ArrayList<>();

        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                // 检查执行超时
                if (executionMonitor.isTimeout()) {
                    state = AgentState.FINISHED;
                    log.warn("执行超时: {}ms", executionMonitor.getElapsedTime());
                    results.add("执行超时，已达到最大执行时间");
                    break;
                }

                currentStep = i + 1;
                
                // 单步执行
                String stepResult = step();
                results.add("Step " + currentStep + ": " + stepResult);

                // 检查是否陷入循环
                DeadLoopDetector.DetectionResult detection = deadLoopDetector.detect();
                if (detection.getLevel() == DeadLoopDetector.InterventionLevel.TERMINATE) {
                    log.warn("检测到死循环，已终止执行: {}", detection.getMessage());
                    results.add("Agent terminated: " + detection.getMessage());
                    break;
                } else if (detection.getLevel() == DeadLoopDetector.InterventionLevel.WARNING 
                        || detection.getLevel() == DeadLoopDetector.InterventionLevel.PROMPT) {
                    // 添加干预提示
                    if (detection.getSuggestedPrompt() != null) {
                        this.nextStepPrompt = detection.getSuggestedPrompt() + "\n" 
                                + (this.nextStepPrompt != null ? this.nextStepPrompt : "");
                    }
                }
            }

            // 检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }

            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("执行错误: {}", e.getMessage());
            return "执行错误: " + e.getMessage();
        } finally {
            this.cleanup();
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter
     */
    public SseEmitter runStream(String userPrompt) {
        SseEmitter sseEmitter = new SseEmitter(300000L); // 5 分钟超时

        // 辅助方法：发送 SSE 事件
        java.util.function.Consumer<String> sendSse = (data) -> {
            try {
                sseEmitter.send(SseEmitter.event().data(data).build());
            } catch (IOException e) {
                log.error("SSE send error: {}", e.getMessage());
            }
        };

        // 使用线程异步处理
        CompletableFuture.runAsync(() -> {
            if (this.state != AgentState.IDLE) {
                sendSse.accept("错误：无法从状态运行代理：" + this.state);
                sseEmitter.complete();
                return;
            }
            if (StringUtil.isBlank(userPrompt)) {
                sendSse.accept("错误：不能使用空提示词运行代理");
                sseEmitter.complete();
                return;
            }

            // 初始化
            state = AgentState.RUNNING;
            deadLoopDetector.reset();
            executionMonitor.start();
            messageList.add(new UserMessage(userPrompt));
            List<String> results = new ArrayList<>();

            try {
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    // 检查执行超时
                    if (executionMonitor.isTimeout()) {
                        state = AgentState.FINISHED;
                        String timeoutMsg = "执行超时，已达到最大执行时间";
                        results.add(timeoutMsg);
                        sendSse.accept(timeoutMsg);
                        break;
                    }

                    currentStep = i + 1;
                    
                    // 单步执行
                    String stepResult = step();

                    // 检查是否调用了 terminate 工具
                    if (stepResult != null && stepResult.contains("doTerminate")) {
                        // 获取最后一次 AI 回复
                        for (int j = messageList.size() - 1; j >= 0; j--) {
                            Message msg = messageList.get(j);
                            if (msg instanceof AssistantMessage) {
                                String finalResponse = ((AssistantMessage) msg).getText();
                                if (finalResponse != null && !finalResponse.isEmpty()) {
                                    sendSse.accept("FINAL:" + finalResponse);
                                    break;
                                }
                            }
                        }
                    }

                    String result = "Step " + currentStep + ": " + stepResult;
                    results.add(result);
                    sendSse.accept(result);

                    // 检查是否陷入循环
                    DeadLoopDetector.DetectionResult detection = deadLoopDetector.detect();
                    if (detection.getLevel() == DeadLoopDetector.InterventionLevel.TERMINATE) {
                        log.warn("检测到死循环，已终止执行: {}", detection.getMessage());
                        String terminateMsg = "Agent terminated: " + detection.getMessage();
                        results.add(terminateMsg);
                        sendSse.accept(terminateMsg);
                        break;
                    } else if (detection.getLevel() == DeadLoopDetector.InterventionLevel.WARNING 
                            || detection.getLevel() == DeadLoopDetector.InterventionLevel.PROMPT) {
                        // 添加干预提示
                        if (detection.getSuggestedPrompt() != null) {
                            this.nextStepPrompt = detection.getSuggestedPrompt() + "\n" 
                                    + (this.nextStepPrompt != null ? this.nextStepPrompt : "");
                        }
                    }
                }

                // 检查是否超出步骤限制
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    String maxStepsMsg = "执行结束，达到最大步骤 (" + maxSteps + ")";
                    results.add("Terminated: Reached max steps (" + maxSteps + ")");
                    sendSse.accept(maxStepsMsg);
                }

                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("流式执行错误: {}", e.getMessage());
                sendSse.accept("执行错误：" + e.getMessage());
                sseEmitter.complete();
            } finally {
                this.cleanup();
            }
        });

        // 设置响应超时
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });

        // 设置完成回调
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return sseEmitter;
    }

    /**
     * 执行单个步骤（子类必须实现）
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源（子类可以重写）
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }

    // ====== 辅助方法（委托给检测器和监控器）======

    /**
     * 记录响应（用于循环检测）
     */
    protected void recordResponse(String response) {
        deadLoopDetector.recordResponse(response);
    }

    /**
     * 记录工具调用（用于循环检测）
     */
    protected void recordToolCall(String toolName, String arguments) {
        deadLoopDetector.recordToolCall(toolName, arguments);
    }

    /**
     * 记录已尝试的工具
     */
    protected void recordAttemptedTool(String toolName) {
        // 委托给 deadLoopDetector
        deadLoopDetector.recordToolCall(toolName, null);
    }

    /**
     * 记录失败的尝试
     */
    protected void recordFailedAttempt() {
        deadLoopDetector.recordFailure();
    }

    /**
     * 获取执行已用时间
     */
    protected long getExecutionElapsedTime() {
        return executionMonitor.getElapsedTime();
    }

    /**
     * 检查执行是否超时
     */
    protected boolean isExecutionTimeout() {
        return executionMonitor.isTimeout();
    }

    /**
     * 设置最大执行时间
     */
    public void setMaxExecutionTimeMs(long maxExecutionTimeMs) {
        executionMonitor.setMaxExecutionTimeMs(maxExecutionTimeMs);
    }

    /**
     * 设置相似度阈值
     */
    public void setSimilarityThreshold(double similarityThreshold) {
        deadLoopDetector.setSimilarityThreshold(similarityThreshold);
    }

    /**
     * 获取死循环检测器（供子类使用）
     */
    protected DeadLoopDetector getDeadLoopDetector() {
        return deadLoopDetector;
    }

    /**
     * 获取执行监控器（供子类使用）
     */
    protected ExecutionMonitor getExecutionMonitor() {
        return executionMonitor;
    }
}
