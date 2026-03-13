package com.monuo.superaiagent.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.monuo.superaiagent.advisor.MyLoggerAdvisor;
import com.monuo.superaiagent.agent.model.AgentState;
import com.monuo.superaiagent.chatmemory.DatabaseBasedChatMemory;
import com.monuo.superaiagent.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 *  monuo的 AI 超级智能体（拥有自主规划能力）
 *  支持数据库记忆、手动工具调用和深度思考
 */
@Component
@Slf4j
public class MonuoManus extends ThinkingAgent {

    private DatabaseBasedChatMemory databaseBasedChatMemory;
    private String chatId; // 用于在 think() 中访问当前对话ID
    private boolean historyLoaded = false; // 标记历史消息是否已加载

    @Autowired
    public MonuoManus(ToolCallback[] manusTools, DashScopeChatModel dashScopeChatModel,
                       DatabaseBasedChatMemory databaseBasedChatMemory) {
        super(manusTools);
        this.databaseBasedChatMemory = databaseBasedChatMemory;
        this.setName("monuoManus");
        String SYSTEM_PROMPT = """
                你是 monuoManus，一个会深度思考的全能 AI 助手。
                
                ## 核心原则
                1. 先思考，再行动 - 每次回答前都要深入分析
                2. 简单问题直接回答，复杂问题调用工具
                3. 保持自然、友好、简洁的对话风格
                
                ## 可用工具
                - 文件操作：读取、写入、删除文件
                - 网络搜索：搜索最新信息
                - 网页抓取：获取网页内容
                - 资源下载：下载文件
                - 终端命令：执行系统命令
                - PDF生成：创建PDF文档
                - 邮件发送：发送电子邮件
                
                ## 思考模式（DeepSeek 风格）
                
                ### 对于简单问题（第一次问候、简单提问）
                直接友好回答，无需使用【思考】【回复】格式
                
                示例：
                用户："你好"（第一次）
                你："你好！我是 monuoManus，有什么可以帮你的吗？"
                
                用户："你是谁"
                你："我是 monuoManus，一个全能 AI 助手。我可以帮你搜索信息、操作文件、发送邮件等。"
                
                ### 对于复杂问题或重复问题（需要深度思考）
                必须使用以下格式：
                
                【思考】
                1. 用户意图分析：用户真正想要什么？
                2. 问题类型判断：是否需要工具？需要哪些工具？
                3. 解决方案规划：如何一步步完成任务？
                4. 潜在问题预判：可能遇到什么问题？
                
                【回复】
                给用户的最终答案（清晰、友好、简洁）
                
                示例：
                用户："你好"（第二次或更多次）
                【思考】
                用户再次问候，可能是想重新开始对话或者测试我的反应。
                我应该给出不同的回复，展示我的多样性和智能。
                可以询问用户具体需求，或者介绍我的功能。
                
                【回复】
                又见面了！这次有什么我可以帮你的吗？我可以帮你搜索信息、处理文件、发送邮件等。
                
                用户："帮我搜索今天的新闻"
                【思考】
                用户想了解今日新闻，需要使用 webSearch 工具获取最新信息。
                搜索关键词应该是"今日新闻"或"今天的新闻"。
                
                【回复】
                好的，我来帮你搜索今天的新闻。
                
                ## 重要规则
                
                1. **思考过程不展示给用户**
                   - 【思考】部分是你的内心分析，用户看不到
                   - 可以自由思考，分析各种可能性
                   - 绝对不要在任何地方重复用户的原话
                   - 不要写"用户："或"用户说："这样的内容
                   - 不要提到"系统"、"提示词"、"指令"等技术细节
                   - 保持自然，就像人类在思考一样
                
                2. **回复要简洁友好**
                   - 【回复】部分会展示给用户
                   - 用最少的文字表达完整意思
                   - 避免冗长的解释
                   - 保持友好和专业
                   - 直接回答，不要重复问题
                   - 不要暴露内部工作机制
                
                3. **主动执行，减少询问**
                   - 能直接执行的操作就执行，不要过度询问
                   - 只在真正需要用户确认时才提问（如邮箱地址、文件路径等）
                   - 使用合理的默认值
                
                4. **工具调用时机**
                   - 简单问候、闲聊 → 不调用工具
                   - 需要外部信息（搜索、文件、邮件）→ 调用工具
                   - 不确定时 → 先思考，再决定
                
                5. **回复多样性**
                   - 即使用户重复相同问题，也要给出不同的回复
                   - 可以换个角度、换个例子、换个表达方式
                   - 避免机械重复
                
                ## 错误示例（避免）
                
                ❌ 提到系统或技术细节：
                用户："你好"
                【思考】用户重复发送了相同的系统指令...
                【回复】我已理解您的系统要求...
                
                ✅ 正确做法：
                用户："你好"
                【思考】用户再次问候，可能想重新开始对话...
                【回复】又见面了！有什么可以帮你的吗？
                
                ❌ 重复用户的话：
                用户："你会什么"
                【回复】用户: 你会什么
                
                ✅ 正确做法：
                用户："你会什么"
                【思考】用户想了解我的能力...
                【回复】我可以帮你搜索信息、操作文件、发送邮件、执行命令等。
                
                ❌ 回复过于冗长：
                【回复】好的，我理解了您的需求，现在我将为您执行搜索操作，请稍等片刻，我会尽快为您找到相关信息...
                
                ✅ 正确做法：
                【回复】好的，我来帮你搜索。
                
                ## 记住
                - 你是一个会思考的智能助手，不是机械的工具执行器
                - 简单问题要自然对话，复杂问题要深度思考
                - 用户体验第一，简洁高效
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                基于工具执行结果，继续思考下一步：
                
                【思考】
                1. 工具执行结果分析：成功还是失败？
                2. 是否达成用户目标？还需要什么？
                3. 下一步行动：继续执行、总结结果、还是结束任务？
                
                【回复】
                根据结果给用户清晰的反馈
                
                注意：
                - 如果任务已完成，使用 terminate 工具结束对话
                - 如果需要继续，明确说明下一步
                - 如果失败，分析原因并尝试替代方案
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
     * 重写 act 方法
     * 注意：数据库保存逻辑已在 runStream 方法中统一处理
     */
    @Override
    public String act() {
        // 调用父类的 act 方法
        return super.act();
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

        // 记录当前消息列表的起始位置（必须在添加用户消息之前！）
        // 这样保存时 subList(0, size) 会包含：用户消息 + 助手回复
        // 历史消息被追加到末尾，不会被包含在 subList 中
        final int[] initialMessageCount = new int[1];
        initialMessageCount[0] = 0;

        CompletableFuture.runAsync(() -> {
            try {
                // 添加用户消息
                getMessageList().add(new UserMessage(userPrompt));
                // 不需要发送用户消息到前端，前端已经显示了

                // 执行多步推理和行动循环
                for (int i = 0; i < getMaxSteps(); i++) {
                    // 不再发送 Step 标记到前端，这些是内部调试信息
                    log.info("=== Step {} ===", i + 1);

                    // Think - 使用数据库记忆
                    boolean shouldAct = think();

                    if (!shouldAct) {
                        // 没有工具调用，获取最终响应
                        String finalResponse = getLastAssistantMessageText();
                        sendThinkingAndMessage(sseEmitter, sendSse, finalResponse);
                        break;
                    }

                    // Act - 执行工具
                    log.info("执行工具中...");
                    String actResult = act();
                    log.info("工具执行结果: {}", actResult);

                    // 检查是否结束
                    if (getState() == AgentState.FINISHED) {
                        // 获取最终响应并发送
                        String finalResponse = getLastAssistantMessageText();
                        if (finalResponse != null && !finalResponse.isEmpty()) {
                            sendThinkingAndMessage(sseEmitter, sendSse, finalResponse);
                        }
                        break;
                    }
                }

                // 无论对话如何结束，都保存到数据库（只保存当前轮次新增的消息）
                if (chatId != null && !chatId.isEmpty()) {
                    List<Message> currentMessages = getMessageList();
                    // 只获取当前轮次新增的消息（从 initialMessageCount[0] 开始）
                    List<Message> newMessages = currentMessages.subList(initialMessageCount[0], currentMessages.size());
                    List<Message> filteredMessages = filterMessages(newMessages);
                    databaseBasedChatMemory.add(chatId, filteredMessages);
                }

                sseEmitter.complete();
            } catch (Exception e) {
                log.error("Stream error: " + e.getMessage(), e);
                sendSse.accept("错误: " + e.getMessage());
                // 异常发生时也尝试保存对话（只保存当前轮次新增的消息）
                if (chatId != null && !chatId.isEmpty()) {
                    try {
                        List<Message> currentMessages = getMessageList();
                        List<Message> newMessages = currentMessages.subList(initialMessageCount[0], currentMessages.size());
                        List<Message> filteredMessages = filterMessages(newMessages);
                        databaseBasedChatMemory.add(chatId, filteredMessages);
                    } catch (Exception saveException) {
                        log.error("保存对话失败: {}", saveException.getMessage());
                    }
                }
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

    /**
     * 过滤消息列表，只保留 USER 和 ASSISTANT 类型（用于保存到数据库）
     * 过滤掉：
     * 1. 系统消息、工具响应消息
     * 2. 内部提示（如 nextStepPrompt）
     * 3. 处理 AssistantMessage 中的思考内容，将思考步骤保存到 metadata，只保存回复部分到 content
     * 注意：不过滤历史消息，因为 DatabaseBasedChatMemory.add() 会先删除所有历史再保存完整列表
     */
    private List<Message> filterMessages(List<Message> messages) {
        return messages.stream()
                .map(msg -> {
                    // 过滤掉 TOOL 响应消息（不保存到数据库）
                    if (msg.getMessageType() == MessageType.TOOL) {
                        return null;
                    }
                    
                    if (msg instanceof UserMessage userMsg) {
                        // 过滤掉 nextStepPrompt（内部决策提示）
                        String text = userMsg.getText();
                        if (text != null && (
                                text.contains("基于工具") || 
                                text.contains("继续思考") ||
                                text.contains("基于工具执行结果") ||
                                text.contains("基于工具返回的结果做决策"))) {
                            return null; // 过滤掉内部提示
                        }
                    }
                    if (msg instanceof AssistantMessage assistantMsg) {
                        // 处理 AssistantMessage，提取回复部分和思考部分
                        String text = assistantMsg.getText();
                        if (text != null && text.contains("【思考】") && text.contains("【回复】")) {
                            // 提取思考内容
                            String thinkingContent = extractContent(text, "【思考】", "【回复】");
                            // 提取回复内容
                            String replyContent = extractReplyContent(text);
                            
                            // 将思考内容按行分割，保存到 metadata
                            java.util.Map<String, Object> metadata = new java.util.HashMap<>(assistantMsg.getMetadata());
                            if (thinkingContent != null && !thinkingContent.isEmpty()) {
                                List<String> thinkingSteps = java.util.Arrays.stream(thinkingContent.split("\n"))
                                        .map(String::trim)
                                        .filter(line -> !line.isEmpty())
                                        .collect(Collectors.toList());
                                metadata.put("thinkingSteps", thinkingSteps);
                            }
                            
                            // 创建新的 AssistantMessage，只包含回复内容，但保留 metadata
                            return new AssistantMessage(replyContent, metadata);
                        }
                    }
                    return msg;
                })
                .filter(msg -> msg != null)
                .collect(Collectors.toList());
    }

    /**
     * 解析并发送思考和回复内容（流式输出）
     * 支持 DeepSeek 风格的【思考】和【回复】格式
     * 如果包含这两个标记，分别流式发送 thinking 和 message 事件
     * 否则流式发送 message 事件
     */
    private void sendThinkingAndMessage(SseEmitter sseEmitter, Consumer<String> sendSse, String content) {
        // 检查是否包含思考和回复标记
        if (content != null && content.contains("【思考】") && content.contains("【回复】")) {
            // 解析思考内容
            String thinkingContent = extractContent(content, "【思考】", "【回复】");
            // 解析回复内容（去掉【回复】标记后的内容）
            String replyContent = extractReplyContent(content);

            // 流式发送思考内容（使用 thinking 事件名）
            if (thinkingContent != null && !thinkingContent.isEmpty()) {
                sendStreamContent(sseEmitter, "thinking", thinkingContent);
            }

            // 流式发送回复内容（使用 message 事件名）
            if (replyContent != null && !replyContent.isEmpty()) {
                sendStreamContent(sseEmitter, "message", replyContent);
            }
        } else {
            // 没有思考标记，直接流式发送回复内容（使用 message 事件名）
            if (content != null && !content.isEmpty()) {
                sendStreamContent(sseEmitter, "message", content);
            }
        }
    }

    /**
     * 流式发送内容（逐字发送，模拟打字效果）
     */
    private void sendStreamContent(SseEmitter sseEmitter, String eventName, String content) {
        try {
            // 按行分割内容
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    // 发送每一行
                    sseEmitter.send(SseEmitter.event().name(eventName).data(line).build());
                    // 短暂延迟，模拟流式输出（可选）
                    try {
                        Thread.sleep(50); // 50ms 延迟
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException e) {
            log.error("流式发送内容失败: {}", e.getMessage());
        }
    }

    /**
     * 从内容中提取指定标记之间的内容
     */
    private String extractContent(String content, String startTag, String endTag) {
        int startIndex = content.indexOf(startTag);
        if (startIndex == -1) {
            return null;
        }
        startIndex += startTag.length();
        int endIndex = content.indexOf(endTag, startIndex);
        if (endIndex == -1) {
            return content.substring(startIndex).trim();
        }
        return content.substring(startIndex, endIndex).trim();
    }

    /**
     * 提取【回复】标记后的内容（用于数据库存储，只保存回复部分）
     */
    private String extractReplyContent(String content) {
        int replyIndex = content.indexOf("【回复】");
        if (replyIndex == -1) {
            return content.trim();
        }
        return content.substring(replyIndex + 4).trim();
    }

    public MonuoManus(ToolCallback[] availableTools) {
        super(availableTools);
    }
}
