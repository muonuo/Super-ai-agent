package com.monuo.superaiagent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MemoryConfig {

    /**
     * ChatMemory 聊天对话记忆的存储
     */
    @Bean
    public ChatMemory chatMemory() {
        // 当前版本的 MessageWindowChatMemory 是 ChatMemory 的唯一默认实现类
        // 并且构造器已经私有化，只提供Builder模式来创建实例；这点与之前的版本不一样
        return MessageWindowChatMemory.builder()
                // 对话存储的repository存储库层的实现方式，如果不配置，默认也是 Spring 提供的 InMemoryChatMemoryRepository
                .chatMemoryRepository(new InMemoryChatMemoryRepository()) // 有默认
                .maxMessages(20) // 最大消息数
                .build();
    }

}
