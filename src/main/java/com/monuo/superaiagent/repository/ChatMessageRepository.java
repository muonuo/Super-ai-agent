package com.monuo.superaiagent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.entity.vo.ConversationVO;
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
        // 直接执行SQL更新 is_delete = 1，与 listConversations 中的逻辑保持一致
        baseMapper.updateDeleteByConversationId(conversationId);
    }

    /**
     * 获取会话列表，按最后消息时间倒序
     * @param appType 应用类型：love(恋爱大师) / manus(超级智能体) / 空字符串(全部)
     */
    public List<ConversationVO> listConversations(String appType) {
        List<ConversationVO> conversations = baseMapper.listConversations();
        
        if (conversations == null) {
            return Collections.emptyList();
        }
        
        // 根据 appType 过滤会话
        if (appType != null && !appType.isEmpty()) {
            final String prefix = appType + "_";
            conversations = conversations.stream()
                    .filter(c -> c.getConversationId() != null 
                            && c.getConversationId().startsWith(prefix))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return conversations;
    }
}
