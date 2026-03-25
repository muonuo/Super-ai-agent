package com.monuo.superaiagent.service;

import com.monuo.superaiagent.constant.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * ChatClient 服务
 * 负责 ChatClient 的构建、配置和调用
 * 统一管理工具调用、RAG、对话记忆等功能
 */
@Service
@Slf4j
public class ChatClientService {

    private final ChatClient chatClient;

    @Resource
    private ToolCallback[] loveAppTools;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private RagService ragService;

    public ChatClientService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 同步对话
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具
     * @param useRag 是否使用RAG
     * @return AI回答
     */
    public String chat(String message, String chatId, boolean enableTools, boolean useRag) {
        // 如果使用 RAG，先进行查询重写
        String queryMessage = useRag ? ragService.rewriteQuery(message) : message;
        
        var promptSpec = chatClient
                .prompt()
                .user(queryMessage)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId));

        // 条件性添加工具回调
        if (enableTools && loveAppTools != null && loveAppTools.length > 0) {
            promptSpec = promptSpec.toolCallbacks(loveAppTools);
            log.debug("已启用工具调用，共{}个工具", loveAppTools.length);
        }

        // 条件性添加 RAG advisor
        if (useRag && pgVectorVectorStore != null) {
            promptSpec = promptSpec.advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build());
            log.debug("已启用 RAG 检索增强");
        }

        return promptSpec.call().content();
    }

    /**
     * 流式对话
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具
     * @param useRag 是否使用RAG
     * @return 流式AI回答
     */
    public Flux<String> chatStream(String message, String chatId, boolean enableTools, boolean useRag) {
        // 如果使用 RAG，先进行查询重写
        String queryMessage = useRag ? ragService.rewriteQuery(message) : message;
        
        var promptSpec = chatClient
                .prompt()
                .user(queryMessage)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId));

        // 条件性添加工具回调
        if (enableTools && loveAppTools != null && loveAppTools.length > 0) {
            promptSpec = promptSpec.toolCallbacks(loveAppTools);
            log.debug("已启用工具调用，共{}个工具", loveAppTools.length);
        }

        // 条件性添加 RAG advisor
        if (useRag && pgVectorVectorStore != null) {
            promptSpec = promptSpec.advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build());
            log.debug("已启用 RAG 检索增强");
        }

        return promptSpec.stream().content();
    }

    /**
     * 生成结构化报告
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param reportClass 报告类型
     * @return 结构化报告对象
     */
    public <T> T generateReport(String message, String chatId, Class<T> reportClass) {
        return chatClient
                .prompt()
                .system(SystemConstants.SYSTEM_PROMPT1 + SystemConstants.REPORT_REQUIREMENT)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(reportClass);
    }
}
