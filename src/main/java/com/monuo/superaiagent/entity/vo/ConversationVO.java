package com.monuo.superaiagent.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationVO {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 最后一条消息的内容
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    private Date lastMessageTime;
}
