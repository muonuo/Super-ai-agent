package com.monuo.superaiagent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.mapper.ChatMessageMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ChatMessageRepository extends ServiceImpl<ChatMessageMapper, ChatMessage> {

    public List<ChatMessage> listByConversationId(String conversationId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                   .orderByAsc(ChatMessage::getCreateTime);
        return this.list(queryWrapper);
    }

    public List<ChatMessage> listLatestByConversationId(String conversationId, int limit) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                   .orderByDesc(ChatMessage::getCreateTime)
                   .last("LIMIT " + limit);
        
        List<ChatMessage> chatMessages = this.list(queryWrapper);
        
        if (!chatMessages.isEmpty()) {
            Collections.reverse(chatMessages);
        }
        
        return chatMessages;
    }

    public void deleteByConversationId(String conversationId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        this.remove(queryWrapper);
    }
}
