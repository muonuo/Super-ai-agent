package com.monuo.superaiagent.agent;

import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import com.monuo.superaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * <p>
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性  
    private String name;

    // 提示  
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态  
    private AgentState state = AgentState.IDLE;

    // 执行控制  
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM
    private ChatClient chatClient;

    // Memory（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    // ====== 防死循环相关属性 ======
    // 重复响应阈值，超过此值认为陷入循环
    private int duplicateThreshold = 2;

    // 最大连续失败次数
    private int maxConsecutiveFailures = 3;

    // 当前连续失败计数
    private int consecutiveFailures = 0;

    // 已尝试的工具集合（避免重复调用）
    private Set<String> attemptedTools = new HashSet<>();

    // 最近响应列表（用于检测重复）
    private List<String> recentResponses = new ArrayList<>();

    // 最大记录最近响应数量
    private int maxRecentResponses = 5;
    // ====== 防死循环相关属性 ======

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
        // 更改状态
        state = AgentState.RUNNING;
        // 重置防死循环状态
        resetStuckDetection();
        // 记录消息上下文  
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表  
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);
                // 单步执行  
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制  
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源  
            this.cleanup();
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L); // 3 分钟超时

        // 辅助方法：发送 SSE 事件
        java.util.function.Consumer<String> sendSse = (data) -> {
            try {
                sseEmitter.send(SseEmitter.event().data(data).build());
            } catch (IOException e) {
                log.error("SSE send error: " + e.getMessage());
            }
        };

        //使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            if (this.state != AgentState.IDLE) {
                sendSse.accept("错误：无法从状态运行代理：" + this.state);
                sseEmitter.complete();
                return;
            }
            if (StringUtil.isBlank(userPrompt)) {
                sendSse.accept("错误：不能使用空提示词代理运行代理：");
                sseEmitter.complete();
                return;
            }
            // 更改状态
            state = AgentState.RUNNING;
            // 重置防死循环状态
            resetStuckDetection();
            // 记录消息上下文
            messageList.add(new UserMessage(userPrompt));
            // 保存结果列表
            List<String> results = new ArrayList<>();
            try {
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step " + stepNumber + "/" + maxSteps);
                    // 单步执行
                    String stepResult = step();

                    // 检查是否调用了 terminate 工具，如果是，发送最后一次 AI 回复
                    if (stepResult != null && stepResult.contains("doTerminate")) {
                        // 获取最后一次 AI 回复
                        if (messageList.size() > 0) {
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
                    }

                    String result = "Step " + stepNumber + ": " + stepResult;
                    results.add(result);
                    // 输出当前每一步结构到 SSE
                    sendSse.accept(result);
                }
                // 检查是否超出步骤限制
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    results.add("Terminated: Reached max steps (" + maxSteps + ")");
                    sendSse.accept("执行结束，达到最大步骤 (" + maxSteps + ")");
                }
                // 正常完成
                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                sendSse.accept("执行错误：" + e.getMessage());
                sseEmitter.complete();
            } finally {
                // 清理资源
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
        sseEmitter.onCompletion(() ->{
            if(this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });
        return sseEmitter;
    }


    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }

    // ====== 防死循环相关方法 ======

    /**
     * 检查代理是否陷入循环
     *
     * @return 是否陷入循环
     */
    protected boolean isStuck() {
        if (messageList.size() < 2) {
            return false;
        }

        Message lastMessage = messageList.get(messageList.size() - 1);
        if (lastMessage == null || !(lastMessage instanceof AssistantMessage)) {
            return false;
        }

        String lastContent = ((AssistantMessage) lastMessage).getText();
        if (lastContent == null || lastContent.isEmpty()) {
            return false;
        }

        // 计算重复内容出现次数
        int duplicateCount = 0;

        for (int i = messageList.size() - 2; i >= 0; i--) {
            Message msg = messageList.get(i);
            // 检查是否是助手消息
            if (msg instanceof AssistantMessage) {
                String msgContent = ((AssistantMessage) msg).getText();
                if (lastContent.equals(msgContent)) {
                    duplicateCount++;
                }
            }
        }

        return duplicateCount >= duplicateThreshold;
    }

    /**
     * 检查最近响应是否重复（基于相似度）
     *
     * @return 是否重复
     */
    protected boolean isRecentResponseDuplicate() {
        if (recentResponses.isEmpty()) {
            return false;
        }

        String lastResponse = recentResponses.get(recentResponses.size() - 1);
        if (lastResponse == null || lastResponse.isEmpty()) {
            return false;
        }

        // 检查最近响应是否完全相同
        long matchCount = recentResponses.stream()
                .filter(r -> r.equals(lastResponse))
                .count();

        return matchCount >= duplicateThreshold;
    }

    /**
     * 处理陷入循环的状态
     */
    protected void handleStuckState() {
        String stuckPrompt = "检测到重复响应模式。你可能陷入了循环。请尝试以下策略：\n" +
                "1. 重新分析问题，可能需要不同的解决思路\n" +
                "2. 尝试使用不同的工具或方法\n" +
                "3. 如果任务确实无法完成，明确告知用户并终止\n" +
                "4. 避免重复已尝试过的无效路径";
        this.nextStepPrompt = stuckPrompt + "\n" + (this.nextStepPrompt != null ? this.nextStepPrompt : "");
        log.warn("Agent detected stuck state. Added stuck handling prompt.");
        // 重置连续失败计数
        this.consecutiveFailures = 0;
    }

    /**
     * 记录已尝试的工具
     *
     * @param toolName 工具名称
     */
    protected void recordAttemptedTool(String toolName) {
        if (toolName != null && !toolName.isEmpty()) {
            attemptedTools.add(toolName);
        }
    }

    /**
     * 检查工具是否已尝试过
     *
     * @param toolName 工具名称
     * @return 是否已尝试
     */
    protected boolean isToolAlreadyAttempted(String toolName) {
        return toolName != null && attemptedTools.contains(toolName);
    }

    /**
     * 记录失败的尝试
     */
    protected void recordFailedAttempt() {
        consecutiveFailures++;
        log.warn("Recording failed attempt. Consecutive failures: {}", consecutiveFailures);
    }

    /**
     * 记录响应用于重复检测
     *
     * @param response 响应内容
     */
    protected void recordResponse(String response) {
        if (response != null && !response.isEmpty()) {
            recentResponses.add(response);
            // 保持最近响应列表在限定大小内
            if (recentResponses.size() > maxRecentResponses) {
                recentResponses.remove(0);
            }
        }
    }

    /**
     * 检查是否达到最大连续失败次数
     *
     * @return 是否超过最大失败次数
     */
    protected boolean hasReachedMaxFailures() {
        return consecutiveFailures >= maxConsecutiveFailures;
    }

    /**
     * 处理连续失败的情况
     *
     * @return 失败处理消息
     */
    protected String handleConsecutiveFailures() {
        String failurePrompt = "检测到连续多次失败。可能原因：\n" +
                "1. 当前方法可能不适用于解决此问题\n" +
                "2. 可能缺少必要的信息或权限\n" +
                "3. 请重新评估情况，考虑其他解决路径\n" +
                "如果尝试多次后仍无法解决，请明确告知用户当前困难并请求澄清";
        this.nextStepPrompt = failurePrompt + "\n" + (this.nextStepPrompt != null ? this.nextStepPrompt : "");
        log.error("Agent reached max consecutive failures: {}", maxConsecutiveFailures);
        // 重置失败计数
        this.consecutiveFailures = 0;
        return "已达到最大连续失败次数，已添加引导提示";
    }

    /**
     * 检查并处理陷入循环的状态（在每步执行后调用）
     *
     * @return 是否检测到循环并进行了处理
     */
    protected boolean checkAndHandleStuck() {
        // 检查响应重复
        if (isRecentResponseDuplicate() || isStuck()) {
            handleStuckState();
            return true;
        }

        // 检查连续失败
        if (hasReachedMaxFailures()) {
            handleConsecutiveFailures();
            return true;
        }

        return false;
    }

    /**
     * 重置防死循环状态（在新任务开始时调用）
     */
    protected void resetStuckDetection() {
        this.consecutiveFailures = 0;
        this.attemptedTools.clear();
        this.recentResponses.clear();
    }
    // ====== 防死循环相关方法 ======
}
