package com.monuo.superaiagent.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.monuo.superaiagent.agent.model.AgentState;
import com.monuo.superaiagent.chatmemory.DatabaseBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 *  monuo的 AI 超级智能体（拥有自主规划能力）
 *  支持数据库记忆和手动工具调用
 */
@Component
@Slf4j
public class MonuoManus extends ToolCallAgent {

    private DatabaseBasedChatMemory databaseBasedChatMemory;
    private DashScopeChatModel dashScopeChatModel;
    private String chatId; // 用于在 think() 中访问当前对话ID
    private boolean historyLoaded = false; // 标记历史消息是否已加载

    @Autowired
    public MonuoManus(ToolCallback[] manusTools, DashScopeChatModel dashScopeChatModel,
                       DatabaseBasedChatMemory databaseBasedChatMemory) {
        super(manusTools);
        this.databaseBasedChatMemory = databaseBasedChatMemory;
        this.dashScopeChatModel = dashScopeChatModel;
        this.setName("monuoManus");
        String SYSTEM_PROMPT = """
                你是一个全能型AI助手 monuoManus，帮助用户解决各种问题。

                ## 核心能力
                文件操作、网络搜索、网页抓取、资源下载、终端命令、PDF生成、邮件发送。

                ## 主动性原则
                - 能执行的操作直接执行，不要询问用户
                - 需要用户确认的信息（如邮箱地址）才明确提问
                - 复杂任务拆分为多个步骤逐步完成

                ## 邮箱处理
                - 用户明确提供邮箱 → 直接使用
                - 对话历史中有邮箱 → 提取使用
                - 没有提供 → 明确询问用户
                - 绝对不要猜测邮箱地址

                ## 回答风格（参考 Claude Code）
                简洁直接，用最少的文字表达完整意思。

                ### 示例
                用户："帮我搜索今天的新闻"
                助手：[调用搜索工具] → "已为您找到今日新闻要点：1. xxx 2. xxx..."

                用户："把这个内容保存到文件"
                助手：[调用文件写入工具] → "已保存到 xxx 文件"

                用户："帮我查下北京天气"
                助手：[调用天气工具] → "北京今天晴，15-28°C，适合出行"

                用户："发送邮件给 xxx"
                助手：[先确认邮箱] "请提供收件人邮箱地址"
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                基于工具返回的结果做决策：
                1. 如果工具成功执行 → 总结结果，继续下一步或结束
                2. 如果工具失败 → 分析原因，尝试替代方案
                3. 如果需要用户确认 → 明确提问
                4. 如果任务完成 → 使用 terminate 结束对话
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }

    /**
     * 重写 think 方法，手动集成数据库记忆
     * 使用 ToolCallAgent 的手动工具管理方式
     */
    @Override
    public boolean think() {
        // 1. 从数据库加载历史消息（只在第一次加载）
        if (!historyLoaded && chatId != null && !chatId.isEmpty()) {
            List<Message> historyMessages = databaseBasedChatMemory.get(chatId);
            if (historyMessages != null && !historyMessages.isEmpty()) {
                // 追加到本地消息列表
                getMessageList().addAll(historyMessages);
                log.info("从数据库加载了 {} 条历史消息", historyMessages.size());
            }
            historyLoaded = true;
        }

        // 2. 添加 nextStepPrompt（如果有）
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }

        // 3. 使用父类的 think 方法（会禁用 Spring AI 内置工具执行）
        return super.think();
    }

    /**
     * 重写 act 方法，在执行后保存对话到数据库
     */
    @Override
    public String act() {
        // 调用父类的 act 方法
        String result = super.act();

        // 保存对话到数据库
        if (chatId != null && !chatId.isEmpty()) {
            databaseBasedChatMemory.add(chatId, getMessageList());
            log.info("已将对话保存到数据库，chatId: {}", chatId);
        }

        return result;
    }

    /**
     * 流式运行（带数据库记忆）
     * 不使用 Spring AI 的自动工具调用，而是使用手动工具管理
     *
     * @param userPrompt 用户提示词
     * @param chatId     对话ID
     * @return SseEmitter
     */
    public SseEmitter runStream(String userPrompt, String chatId) {
        SseEmitter sseEmitter = new SseEmitter(300000L);
        this.chatId = chatId; // 保存 chatId 供 think() 使用
        this.historyLoaded = false; // 重置历史消息加载标志
        getMessageList().clear(); // 清空消息列表

        Consumer<String> sendSse = (data) -> {
            try {
                sseEmitter.send(SseEmitter.event().data(data).build());
            } catch (IOException e) {
                log.error("SSE send error: " + e.getMessage());
            }
        };

        CompletableFuture.runAsync(() -> {
            try {
                // 添加用户消息
                getMessageList().add(new UserMessage(userPrompt));
                sendSse.accept("用户: " + userPrompt);

                // 执行多步推理和行动循环
                for (int i = 0; i < getMaxSteps(); i++) {
                    sendSse.accept("=== Step " + (i + 1) + " ===");

                    // Think - 使用数据库记忆
                    boolean shouldAct = think();

                    if (!shouldAct) {
                        // 没有工具调用，获取最终响应
                        String finalResponse = getLastAssistantMessageText();
                        sendSse.accept("FINAL:" + finalResponse);
                        // 保存对话到数据库
                        if (chatId != null && !chatId.isEmpty()) {
                            databaseBasedChatMemory.add(chatId, getMessageList());
                        }
                        break;
                    }

                    // Act - 执行工具
                    sendSse.accept("执行工具中...");
                    String actResult = act();
                    sendSse.accept("工具执行结果: " + actResult);

                    // 检查是否结束
                    if (getState() == AgentState.FINISHED) {
                        // 获取最终响应并发送
                        String finalResponse = getLastAssistantMessageText();
                        if (finalResponse != null && !finalResponse.isEmpty()) {
                            sendSse.accept("FINAL:" + finalResponse);
                        }
                        break;
                    }
                }

                sseEmitter.complete();
            } catch (Exception e) {
                log.error("Stream error: " + e.getMessage(), e);
                sendSse.accept("错误: " + e.getMessage());
                sseEmitter.completeWithError(e);
            }
        });

        return sseEmitter;
    }

    /**
     * 获取最后一条助手消息的文本
     */
    private String getLastAssistantMessageText() {
        List<Message> messages = getMessageList();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (msg instanceof AssistantMessage) {
                return ((AssistantMessage) msg).getText();
            }
        }
        return null;
    }

    public MonuoManus(ToolCallback[] availableTools) {
        super(availableTools);
    }
}
