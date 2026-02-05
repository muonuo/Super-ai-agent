package com.monuo.superaiagent.app;

import com.monuo.superaiagent.advisor.MyLoggerAdvisor;
import com.monuo.superaiagent.advisor.ReReadingAdvisor;
import com.monuo.superaiagent.chatmemory.DatabaseBasedChatMemory;
import com.monuo.superaiagent.chatmemory.FileBasedChatMemory;
import com.monuo.superaiagent.constant.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;


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
                .defaultSystem(SystemConstants.SYSTEM_PROMPT)
                .defaultAdvisors(
                        // 自定义 Advisor，可按需开启
                        new MyLoggerAdvisor(),
                        // 自定义 Re2 Advisor，可按需开启
                        new ReReadingAdvisor(),
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

    record LoveReport(String title, List<String> suggestions) {
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
                .system(SystemConstants.SYSTEM_PROMPT + SystemConstants.REPORT_REQUIREMENT)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    // AI 恋爱知识库问答功能
    @Resource
    private VectorStore loveAppVectorStore;

    /**
     * 和 RAG 知识库进行问答
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        String content = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                .call()
                .content();
        log.info("content: {}", content);
        return content;
    }

}
