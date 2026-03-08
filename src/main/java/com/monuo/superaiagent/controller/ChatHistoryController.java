package com.monuo.superaiagent.controller;

import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.entity.vo.ConversationVO;
import com.monuo.superaiagent.entity.vo.MessageVO;
import com.monuo.superaiagent.repository.ChatMessageRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {

    @Resource
    private ChatMessageRepository chatMessageRepository;

    /**
     * 获取会话列表
     * 根据 appType 参数过滤不同应用的历史会话
     *
     * @param appType 应用类型：love(恋爱大师) / manus(超级智能体) / null(全部)
     * @return 会话列表
     */
    @GetMapping
    public List<ConversationVO> listConversations(
            @RequestParam(required = false, defaultValue = "") String appType) {
        return chatMessageRepository.listConversations(appType);
    }

    /**
     * 获取指定会话的消息历史
     *
     * @param chatId 会话ID
     * @return 消息列表
     */
    @GetMapping("/{chatId}")
    public List<MessageVO> getConversationHistory(@PathVariable("chatId") String chatId) {
        List<ChatMessage> messages = chatMessageRepository.listByConversationId(chatId);
        return messages.stream()
                .map(msg -> MessageVO.builder()
                        .role(msg.getMessageType() != null ? msg.getMessageType().name().toLowerCase() : "user")
                        .content(msg.getContent())
                        .createTime(msg.getCreateTime())
                        .build())
                .collect(Collectors.toList());
    }
}
