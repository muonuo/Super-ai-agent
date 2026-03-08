package com.monuo.superaiagent.app;

import com.monuo.superaiagent.advisor.MyLoggerAdvisor;
import com.monuo.superaiagent.advisor.ReReadingAdvisor;
import com.monuo.superaiagent.chatmemory.DatabaseBasedChatMemory;
import com.monuo.superaiagent.chatmemory.FileBasedChatMemory;
import com.monuo.superaiagent.constant.SystemConstants;
import com.monuo.superaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.monuo.superaiagent.rag.QueryRewriter;
import com.monuo.superaiagent.service.QuestionClassifierService;
import com.monuo.superaiagent.service.QuestionClassifierService.QuestionType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

//    //基于内存的对话记忆
//    public LoveApp(ChatModel dashscopeChatModel, ChatMemory chatMemory) {
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SystemConstants.SYSTEM_PROMPT)
//                .defaultAdvisors(

    /// /                        new SimpleLoggerAdvisor(),
//                        // 自定义 Advisor，可按需开启
//                        new MyLoggerAdvisor(),
//                        // 自定义 Re2 Advisor，可按需开启
//                        new ReReadingAdvisor(),
//                        MessageChatMemoryAdvisor.builder(chatMemory).build()
//                )
//                .build();
//    }

//    //基于文件的对话记忆
//    public LoveApp(ChatModel dashscopeChatModel) {
//        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SystemConstants.SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        // 自定义 Advisor，可按需开启
//                        new MyLoggerAdvisor(),
//                        // 自定义 Re2 Advisor，可按需开启
//                        new ReReadingAdvisor(),
//                        MessageChatMemoryAdvisor.builder(chatMemory).build()
//                )
//                .build();
//    }


    //基于数据库的对话记忆
    public LoveApp(ChatModel dashscopeChatModel, DatabaseBasedChatMemory databaseBasedChatMemory) {
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SystemConstants.SYSTEM_PROMPT1)
                .defaultAdvisors(
                        // 自定义 Advisor，可按需开启
                        new MyLoggerAdvisor(),
                        // 自定义 Re2 Advisor，可按需开启
//                        new ReReadingAdvisor(),
                        MessageChatMemoryAdvisor.builder(databaseBasedChatMemory).build()
                )
                .build();
    }


    /**
     * AI 基础对话 （支持多轮对话）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        String content = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    //方式一
//    /**
//     * 在系统提示词的【输出格式要求】部分后插入报告要求
//     * @param originalPrompt 原始系统提示词
//     * @return 增强后的系统提示词
//     */
//    private String insertReportRequirement(String originalPrompt) {
//        String reportRequirement = SystemConstants.REPORT_REQUIREMENT;
//
//        int insertPosition = originalPrompt.indexOf("【输出格式要求】");
//        if (insertPosition != -1) {
//            return originalPrompt.substring(0, insertPosition)
//                   + reportRequirement
//                   + originalPrompt.substring(insertPosition);
//        }
//
//        return originalPrompt + reportRequirement;
//    }
//
//    /**
//     * AI 恋爱报告功能（实战结构化输出）
//     * AI会自动从用户消息中提取用户名
//     *
//     * @param message 用户消息
//     * @param chatId 对话ID
//     * @return 恋爱报告
//     */
//    public LoveReport doChatWithReport(String message, String chatId) {
//        String enhancedSystemPrompt = insertReportRequirement(SystemConstants.SYSTEM_PROMPT);
//
//        LoveReport loveReport = chatClient
//                .prompt()
//                .system(enhancedSystemPrompt)
//                .user(message)
//                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
//                .call()
//                .entity(LoveReport.class);
//        log.info("loveReport: {}", loveReport);
//        return loveReport;
//    }

    record LoveReport(String title, List<String> suggestions) {

    }

    //方式二
    /**
     * AI 恋爱报告功能（实战结构化输出）
     * AI会自动从用户消息中提取用户名
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return 恋爱报告
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SystemConstants.SYSTEM_PROMPT1 + SystemConstants.REPORT_REQUIREMENT)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    // AI 恋爱知识库问答功能
    @Resource
    @Qualifier("pgVectorVectorStore")
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * 和 RAG 知识库进行问答
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        String content = chatClient
                .prompt()
                // 使用改写后的查询
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                //应用RAG知识库问答
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                // 应用RAG检索增强功能（基于云知识库服务）
//                .advisors(loveAppRagCloudAdvisor)
                // 应用RAG检索增强功能（基于 PgVector 向量存储）
//                .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                //应用自定义的 RAG 检索增强服务（文档查询器 + 上下文增强器）
//                .advisors(
//                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                                loveAppVectorStore, "已婚"
//                        )
//                )
                .call()
                .content();
        log.info("content: {}", content);
        return content;
    }

    //AI 调用工具的能力
    @Resource
    private ToolCallback[] loveAppTools;

    /**
     * AI 恋爱报告功能（支持调用工具）
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return 恋爱报告
     */
    public String doChatWithTools(String message, String chatId) {
        // 构建工具上下文
        Map<String, Object> toolContext = new HashMap<>();
        toolContext.put("chatId", chatId);
        toolContext.put("timestamp", System.currentTimeMillis());

        String content = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(loveAppTools)
                .toolContext(toolContext)
                .call()
                .content();
        log.info("content: {}", content);
        return content;
    }

    //AI 调用MCP服务
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * AI 恋爱报告功能（调用MCP服务）
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return 恋爱报告
     */
    public String doChatWithMcp(String message, String chatId) {
        String content = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(toolCallbackProvider)
                .call()
                .content();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 问题分类服务
     */
//    @Resource
//    private QuestionClassifierService questionClassifierService;

    /**
     * RAG相似度阈值
     */
    private static final double RAG_SIMILARITY_THRESHOLD = 0.6;

    /**
     * 带RAG Fallback的对话方法
     * 核心策略：
     * 1. 先判断问题类型
     * 2. 敏感问题 → 拒绝回答
     * 3. 通用问题 → 直接用通用LLM回答
     * 4. 恋爱相关/未知 → 尝试RAG，失败则fallback到通用LLM
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答
     */
//    public String doChatWithRagFallback(String message, String chatId) {
//        // 1. 先判断问题类型
//        QuestionType type = questionClassifierService.classify(message);
//        log.info("问题分类结果: {}, 问题: {}", type, message);
//
//        switch (type) {
//            case SENSITIVE:
//                return "抱歉，我无法回答这个问题。";
//
//            case GENERAL:
//                // 通用问题，不走RAG，直接用通用知识回答
//                log.info("通用问题，直接使用通用LLM回答");
//                return doChat(message, chatId);
//
//            case LOVE_RELATED:
//            case UNKNOWN:
//            default:
//                // 尝试RAG，失败则fallback到通用回答
//                return doChatWithRagOrFallback(message, chatId);
//        }
//    }

    /**
     * RAG失败时fallback到通用LLM
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答
     */
//    private String doChatWithRagOrFallback(String message, String chatId) {
//        try {
//            // 先尝试RAG
//            String rewrittenMessage = queryRewriter.doQueryRewrite(message);
//
//            // 直接检索看是否有结果（不经过LLM生成）
//            List<Document> docs = loveAppVectorStore.similaritySearch(
//                    SearchRequest.builder().query(rewrittenMessage).topK(3).build()
//            );
//
//            // 检查是否有相关文档（相似度>0.6）
//            boolean hasRelevantDocs = docs.stream()
//                    .anyMatch(doc -> doc.getScore() >= RAG_SIMILARITY_THRESHOLD);
//
//            if (!hasRelevantDocs) {
//                // RAG无结果，fallback到通用LLM
//                log.info("RAG检索无结果（相似度<{}），fallback到通用LLM", RAG_SIMILARITY_THRESHOLD);
//                return doChat(message, chatId);
//            }
//
//            // 有结果，使用RAG回答
//            log.info("RAG检索到{}条相关文档，使用RAG回答", docs.size());
//            return doChatWithRag(message, chatId);
//
//        } catch (Exception e) {
//            log.error("RAG调用异常，fallback到通用LLM", e);
//            return doChat(message, chatId);
//        }
//    }


}
