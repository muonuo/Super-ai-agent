package com.monuo.superaiagent.agent;

import com.monuo.superaiagent.agent.thinking.QuestionClassifier;
import com.monuo.superaiagent.agent.thinking.ThinkingChain;
import com.monuo.superaiagent.agent.thinking.ThinkingChainParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 思考链智能体
 * 实现 DeepSeek 风格的思考模式：先思考，再决策，最后执行
 */
@Slf4j
public class ThinkingAgent extends ToolCallAgent {

    // 简单问题的系统提示词
    private static final String SIMPLE_PROMPT = """
            你是一个友好的 AI 助手。
            用户的问题很简单，请直接回答，无需调用工具。
            保持自然、友好的对话风格。
            """;

    // 复杂问题的系统提示词（带思考链）
    private static final String COMPLEX_PROMPT = """
            你是一个会深度思考的 AI 助手。
            
            对于复杂问题，你必须按以下格式输出：
            
            【思考】
            分析用户意图和问题类型，思考解决方案
            
            【工具调用】（如果需要工具）
            列出需要调用的工具和参数
            
            【回复】
            给出最终回复内容
            
            注意：
            1. 如果不需要工具，可以省略【工具调用】部分
            2. 【思考】部分不会展示给用户，可以自由思考
            3. 【回复】部分会展示给用户，要清晰友好
            """;

    public ThinkingAgent(ToolCallback[] availableTools) {
        super(availableTools);
    }

    /**
     * 重写 think 方法，实现混合思考模式
     */
    @Override
    public boolean think() {
        // 获取最后一条用户消息
        String userInput = getLastUserMessage();
        if (userInput == null) {
            log.warn("没有找到用户消息");
            return false;
        }

        // 1. 快速分类问题
        ThinkingChain.QuestionType type = QuestionClassifier.classify(userInput);
        log.info("问题分类: {}", type);

        // 2. 根据问题类型选择不同的思考策略
        if (type == ThinkingChain.QuestionType.SIMPLE) {
            return simpleThink(userInput);
        } else {
            return deepThink(userInput);
        }
    }

    /**
     * 简单思考：直接回答，不调用工具
     */
    private boolean simpleThink(String userInput) {
        try {
            // 使用简化的提示词
            List<Message> messages = new ArrayList<>(getMessageList());
            Prompt prompt = new Prompt(messages, getChatOptions());

            ChatResponse response = getChatClient().prompt(prompt)
                    .system(SIMPLE_PROMPT)
                    .call()
                    .chatResponse();

            AssistantMessage assistantMessage = response.getResult().getOutput();
            String content = assistantMessage.getText();

            log.info("简单回答: {}", content);

            // 记录响应
            getMessageList().add(assistantMessage);
            recordResponse(content);

            return false; // 不需要执行工具
        } catch (Exception e) {
            log.error("简单思考失败: {}", e.getMessage());
            getMessageList().add(new AssistantMessage("抱歉，我遇到了一些问题: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 深度思考：使用思考链，可能调用工具
     */
    private boolean deepThink(String userInput) {
        try {
            // 添加 nextStepPrompt（如果有）
            if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
                UserMessage userMessage = new UserMessage(getNextStepPrompt());
                getMessageList().add(userMessage);
            }

            // 使用完整的思考链提示词
            List<Message> messages = new ArrayList<>(getMessageList());
            Prompt prompt = new Prompt(messages, getChatOptions());

            ChatResponse response = getChatClient().prompt(prompt)
                    .system(getSystemPrompt() + "\n\n" + COMPLEX_PROMPT)
                    .toolCallbacks(getAvailableTools())
                    .call()
                    .chatResponse();

            // 解析思考链
            AssistantMessage assistantMessage = response.getResult().getOutput();
            String content = assistantMessage.getText();
            ThinkingChain chain = ThinkingChainParser.parse(content);

            log.info("思考内容: {}", chain.getThinking());
            log.info("是否需要工具: {}", chain.hasToolCalls());

            // 记录响应
            recordResponse(content);

            // 检查是否有工具调用
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            if (!toolCalls.isEmpty()) {
                // 有工具调用，记录并准备执行
                log.info("检测到 {} 个工具调用", toolCalls.size());
                for (AssistantMessage.ToolCall toolCall : toolCalls) {
                    recordAttemptedTool(toolCall.name());
                    recordToolCall(toolCall.name(), toolCall.arguments());
                }

                // 保存响应和工具调用信息
                this.setToolCallChatResponse(response);
                getMessageList().add(assistantMessage);
                return true; // 需要执行工具
            } else {
                // 没有工具调用，直接返回回复
                getMessageList().add(assistantMessage);
                return false; // 不需要执行工具
            }

        } catch (Exception e) {
            log.error("深度思考失败: {}", e.getMessage());
            getMessageList().add(new AssistantMessage("抱歉，我在思考时遇到了问题: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 流式深度思考：逐步输出思考过程
     * @param userInput 用户输入
     * @param stepCallback 思考步骤回调函数
     * @return 是否需要执行工具
     */
    public boolean deepThinkStream(String userInput, Consumer<String> stepCallback) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 步骤1：分析用户意图
            stepCallback.accept("🎯 正在分析用户意图...");
            Thread.sleep(300); // 模拟思考延迟
            
            // 调用 LLM 分析用户意图
            String intentPrompt = String.format("""
                简要分析用户的意图（30字以内）：
                用户输入：%s
                
                只需要一句话说明用户想要什么。
                """, userInput);
            
            String intent = getChatClient().prompt(intentPrompt).call().content();
            stepCallback.accept("用户意图：" + intent.trim());
            
            // 步骤2：判断问题复杂度
            stepCallback.accept("💡 正在评估问题复杂度...");
            Thread.sleep(200);
            
            ThinkingChain.QuestionType type = QuestionClassifier.classify(userInput);
            stepCallback.accept("问题类型：" + (type == ThinkingChain.QuestionType.SIMPLE ? "简单问题" : "复杂问题"));
            
            // 步骤3：生成解决方案
            stepCallback.accept("🔍 正在生成解决方案...");
            Thread.sleep(300);
            
            String solutionPrompt = String.format("""
                针对用户的问题，提供1-2个解决方案（每个方案20字以内）：
                用户输入：%s
                
                格式：方案1: xxx
                """, userInput);
            
            String solutions = getChatClient().prompt(solutionPrompt).call().content();
            String[] solutionLines = solutions.split("\n");
            for (String line : solutionLines) {
                if (line.trim().startsWith("方案")) {
                    stepCallback.accept(line.trim());
                    Thread.sleep(200);
                }
            }
            
            // 步骤4：选择工具
            stepCallback.accept("🔧 正在选择合适的工具...");
            Thread.sleep(200);
            
            // 添加 nextStepPrompt（如果有）
            if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
                UserMessage userMessage = new UserMessage(getNextStepPrompt());
                getMessageList().add(userMessage);
            }

            // 使用完整的思考链提示词
            List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>(getMessageList());
            Prompt prompt = new Prompt(messages, getChatOptions());

            ChatResponse response = getChatClient().prompt(prompt)
                    .system(getSystemPrompt() + "\n\n" + COMPLEX_PROMPT)
                    .toolCallbacks(getAvailableTools())
                    .call()
                    .chatResponse();

            // 解析思考链
            AssistantMessage assistantMessage = response.getResult().getOutput();
            String content = assistantMessage.getText();

            // 记录响应
            recordResponse(content);

            // 检查是否有工具调用
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            if (!toolCalls.isEmpty()) {
                // 有工具调用
                for (AssistantMessage.ToolCall toolCall : toolCalls) {
                    stepCallback.accept("将调用工具: " + toolCall.name());
                    Thread.sleep(200);
                    recordAttemptedTool(toolCall.name());
                    recordToolCall(toolCall.name(), toolCall.arguments());
                }

                // 保存响应和工具调用信息
                this.setToolCallChatResponse(response);
                getMessageList().add(assistantMessage);
                
                // 步骤5：预测结果
                stepCallback.accept("✅ 准备执行工具获取结果...");
                
                long endTime = System.currentTimeMillis();
                double thinkingTime = (endTime - startTime) / 1000.0;
                stepCallback.accept(String.format("思考完成，耗时 %.1f 秒", thinkingTime));
                
                return true; // 需要执行工具
            } else {
                // 没有工具调用
                stepCallback.accept("✅ 无需调用工具，直接回答");
                getMessageList().add(assistantMessage);
                
                long endTime = System.currentTimeMillis();
                double thinkingTime = (endTime - startTime) / 1000.0;
                stepCallback.accept(String.format("思考完成，耗时 %.1f 秒", thinkingTime));
                
                return false; // 不需要执行工具
            }

        } catch (Exception e) {
            log.error("流式思考失败: {}", e.getMessage());
            stepCallback.accept("❌ 思考过程出现错误: " + e.getMessage());
            getMessageList().add(new AssistantMessage("抱歉，我在思考时遇到了问题: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 获取最后一条用户消息
     */
    private String getLastUserMessage() {
        for (int i = getMessageList().size() - 1; i >= 0; i--) {
            org.springframework.ai.chat.messages.Message msg = getMessageList().get(i);
            if (msg instanceof UserMessage) {
                return ((UserMessage) msg).getText();
            }
        }
        return null;
    }
}
