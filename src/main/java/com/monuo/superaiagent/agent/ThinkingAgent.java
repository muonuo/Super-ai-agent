package com.monuo.superaiagent.agent;

import com.monuo.superaiagent.agent.thinking.QuestionClassifier;
import com.monuo.superaiagent.agent.thinking.ThinkingChain;
import com.monuo.superaiagent.agent.thinking.ThinkingChainParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;

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
            List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>(getMessageList());
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
