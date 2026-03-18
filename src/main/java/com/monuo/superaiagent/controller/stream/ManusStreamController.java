package com.monuo.superaiagent.controller.stream;

import com.monuo.superaiagent.agent.MonuoManus;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Manus超级智能体流式接口控制器
 */
@RestController
@RequestMapping("/ai/manus")
public class ManusStreamController {

    @Resource
    private MonuoManus monuoManus;

    /**
     * 流式调用 Manus 超级智能体
     * 使用SseEmitter进行流式传输
     *
     * @param message 用户消息
     * @param chatId  对话ID（用于数据库持久化）
     * @return SseEmitter流式数据
     */
    @GetMapping("/chat")
    public SseEmitter doChatWithManus(String message, String chatId) {
        return monuoManus.runStream(message, chatId);
    }
}