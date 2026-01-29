package com.monuo.superaiagent.converter;

import com.monuo.superaiagent.entity.ChatMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Map;

public class MessageConverter {

    // Message -> ChatMessage
    public static ChatMessage toChatMessage(Message message, String conversationId) {
        return ChatMessage.builder()
                .conversationId(conversationId)
                .messageType(convertMessageType(message))
                .content(getMessageText(message))
                .metadata(message.getMetadata())
                .build();
    }

    // ChatMessage -> Message
    public static Message toMessage(ChatMessage chatMessage) {
        ChatMessage.MessageType messageType = chatMessage.getMessageType();
        String text = chatMessage.getContent();
        Map<String, Object> metadata = chatMessage.getMetadata();
        
        return switch (messageType) {
            case USER -> new UserMessage(text);
            case ASSISTANT -> new AssistantMessage(text);
            case SYSTEM -> new SystemMessage(text);
            case TOOL -> new UserMessage(text);
        };
    }

    private static ChatMessage.MessageType convertMessageType(Message message) {
        if (message instanceof UserMessage) {
            return ChatMessage.MessageType.USER;
        } else if (message instanceof AssistantMessage) {
            return ChatMessage.MessageType.ASSISTANT;
        } else if (message instanceof SystemMessage) {
            return ChatMessage.MessageType.SYSTEM;
        } else {
            return ChatMessage.MessageType.USER;
        }
    }

    private static String getMessageText(Message message) {
        if (message instanceof UserMessage userMessage) {
            return userMessage.getText();
        } else if (message instanceof AssistantMessage assistantMessage) {
            return assistantMessage.getText();
        } else if (message instanceof SystemMessage systemMessage) {
            return systemMessage.getText();
        } else {
            return "";
        }
    }
}
