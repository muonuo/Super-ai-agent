package com.monuo.superaiagent.chatmemory;

import com.monuo.superaiagent.converter.MessageConverter;
import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor// 构造函数注入
public class DatabaseBasedChatMemory implements ChatMemory {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            log.debug("No messages to save for conversation {}", conversationId);
            return;
        }

        // 获取数据库中已有的消息
        List<ChatMessage> existingMessages = chatMessageRepository.listByConversationId(conversationId);
        
        // 构建已存在消息的指纹集合（用于去重）
        // 指纹格式：messageType + "|" + content的前100个字符
        java.util.Set<String> existingFingerprints = existingMessages.stream()
                .map(msg -> {
                    String contentPrefix = msg.getContent() != null && msg.getContent().length() > 100 
                            ? msg.getContent().substring(0, 100) 
                            : msg.getContent();
                    return msg.getMessageType() + "|" + contentPrefix;
                })
                .collect(Collectors.toSet());

        // 过滤出真正需要保存的新消息
        List<ChatMessage> newChatMessages = messages.stream()
                .map(message -> MessageConverter.toChatMessage(message, conversationId))
                .filter(chatMsg -> {
                    String contentPrefix = chatMsg.getContent() != null && chatMsg.getContent().length() > 100 
                            ? chatMsg.getContent().substring(0, 100) 
                            : chatMsg.getContent();
                    String fingerprint = chatMsg.getMessageType() + "|" + contentPrefix;
                    return !existingFingerprints.contains(fingerprint);
                })
                .collect(Collectors.toList());

        if (newChatMessages.isEmpty()) {
            log.debug("No new messages to save for conversation {} (all messages already exist)", conversationId);
            return;
        }

        // 批量保存新消息
        chatMessageRepository.saveBatch(newChatMessages, newChatMessages.size());
        log.debug("Saved {} new messages for conversation {} (filtered from {} total messages)", 
                newChatMessages.size(), conversationId, messages.size());
    }

    @Override
    public List<Message> get(String conversationId) {
        List<ChatMessage> chatMessages = chatMessageRepository.listByConversationId(conversationId);
        List<Message> messages = chatMessages.stream()
                .map(MessageConverter::toMessage)
                .collect(Collectors.toList());
        
        log.debug("Retrieved {} messages for conversation {}", messages.size(), conversationId);
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        chatMessageRepository.deleteByConversationId(conversationId);
        log.debug("Cleared all messages for conversation {}", conversationId);
    }
}
