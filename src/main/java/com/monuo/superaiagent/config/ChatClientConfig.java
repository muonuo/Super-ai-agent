package com.monuo.superaiagent.config;

import com.monuo.superaiagent.advisor.MyLoggerAdvisor;
import com.monuo.superaiagent.chatmemory.DatabaseBasedChatMemory;
import com.monuo.superaiagent.constant.SystemConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient 配置类
 * 负责创建和配置 ChatClient Bean
 */
@Configuration
public class ChatClientConfig {

    /**
     * 创建 ChatClient Bean
     * 配置默认的系统提示词和 Advisor
     *
     * @param chatModel 聊天模型（使用 DashScope）
     * @param chatMemory 对话记忆（基于数据库）
     * @return 配置好的 ChatClient
     */
    @Bean
    public ChatClient chatClient(@Qualifier("dashscopeChatModel") ChatModel chatModel, DatabaseBasedChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(SystemConstants.SYSTEM_PROMPT1)
                .defaultAdvisors(
                        // 自定义 Advisor，可按需开启
                        new MyLoggerAdvisor(),
                        // 自定义 Re2 Advisor，可按需开启
//                        new ReReadingAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}
