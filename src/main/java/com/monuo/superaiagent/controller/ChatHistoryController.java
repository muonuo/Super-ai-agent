package com.monuo.superaiagent.controller;

import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.entity.vo.ConversationVO;
import com.monuo.superaiagent.entity.vo.MessageVO;
import com.monuo.superaiagent.repository.ChatMessageRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
                .map(msg -> {
                    MessageVO.MessageVOBuilder builder = MessageVO.builder()
                            .role(msg.getMessageType() != null ? msg.getMessageType().name().toLowerCase() : "user")
                            .content(msg.getContent())
                            .createTime(msg.getCreateTime());
                    
                    // 从 metadata 中提取思考步骤和思考时间
                    if (msg.getMetadata() != null) {
                        Object thinkingSteps = msg.getMetadata().get("thinkingSteps");
                        if (thinkingSteps instanceof List) {
                            builder.thinkingSteps((List<String>) thinkingSteps);
                        }
                        
                        Object thinkingTime = msg.getMetadata().get("thinkingTime");
                        if (thinkingTime instanceof Number) {
                            builder.thinkingTime(((Number) thinkingTime).intValue());
                        }
                    }
                    
                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除指定会话
     *
     * @param chatId 会话ID
     * @return 删除结果
     */
    @DeleteMapping("/{chatId}")
    public Map<String, Object> deleteConversation(@PathVariable("chatId") String chatId) {
        Map<String, Object> result = new HashMap<>();
        try {
            chatMessageRepository.deleteByConversationId(chatId);
            result.put("code", 0);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
}
