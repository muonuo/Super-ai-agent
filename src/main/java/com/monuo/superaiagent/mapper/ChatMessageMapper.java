package com.monuo.superaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.entity.vo.ConversationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 查询所有会话列表，按最后消息时间倒序
     */
    @Select("SELECT " +
            "t1.conversation_id AS conversationId, " +
            "t1.content AS lastMessage, " +
            "t1.create_time AS lastMessageTime " +
            "FROM chat_message t1 " +
            "INNER JOIN (" +
            "    SELECT conversation_id, MAX(create_time) AS max_create_time " +
            "    FROM chat_message " +
            "    WHERE is_delete = 0 " +
            "    GROUP BY conversation_id" +
            ") t2 ON t1.conversation_id = t2.conversation_id AND t1.create_time = t2.max_create_time " +
            "WHERE t1.is_delete = 0 " +
            "ORDER BY t1.create_time DESC")
    List<ConversationVO> listConversations();
}
