package com.monuo.superaiagent.controller.stream;

import com.monuo.superaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * AI 恋爱大师流式接口控制器
 * 统一使用流式（SSE）输出
 */
@RestController
@RequestMapping("/ai/love_app")
public class LoveAppStreamController {

    @Resource
    private LoveApp loveApp;

    /**
     * 基础流式对话
     * 普通AI对话，支持多轮对话记忆，SSE流式传输
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return 流式AI回答
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatByStream(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 AI 恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppServerSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        loveApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
    }

    /**
     * RAG流式对话
     * 基于知识库的AI对话，SSE流式传输
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return 流式AI回答（基于知识库）
     */
    @GetMapping(value = "/rag", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithRagByStream(String message, String chatId) {
        return loveApp.doChatByStreamWithRag(message, chatId);
    }

    /**
     * 智能流式对话
     * 自动RAG回退 + SSE流式传输
     * 推荐使用：提供最佳用户体验
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return 流式AI回答
     */
    @GetMapping(value = "/smart", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatByStreamSmart(String message, String chatId) {
        return loveApp.doChatByStreamSmart(message, chatId);
    }
}