package com.monuo.superaiagent.controller.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monuo.superaiagent.app.LoveApp;
import com.monuo.superaiagent.app.LoveApp.LoveReport;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 恋爱大师同步接口控制器
 * 处理所有同步（非流式）的API请求
 */
@RestController
@RequestMapping("/ai/love_app/sync")
public class LoveAppSyncController {

    @Resource
    private LoveApp loveApp;

    /**
     * 基础同步对话
     * 普通AI对话，支持多轮对话记忆
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答
     */
    @GetMapping("/chat")
    public String doChat(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    /**
     * RAG知识库问答
     * 基于恋爱知识库进行智能问答
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答（基于知识库）
     */
    @GetMapping("/rag")
    public String doChatWithRag(String message, String chatId) {
        return loveApp.doChatWithRag(message, chatId);
    }

    /**
     * 智能RAG回退模式
     * 先尝试RAG知识库，失败时回退到通用LLM
     * 推荐使用：提供最佳用户体验
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答
     */
    @GetMapping("/smart")
    public String doChatWithSmart(String message, String chatId) {
        return loveApp.doChatWithRagFallback(message, chatId);
    }

    /**
     * 恋爱报告生成
     * 生成结构化恋爱报告，需要用户提供足够的恋爱信息
     *
     * @param message 用户消息（描述恋爱情况）
     * @param chatId  对话ID
     * @return 恋爱报告（JSON格式）
     */
    @GetMapping("/report")
    public String doChatWithReport(String message, String chatId) throws Exception {
        LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        return new ObjectMapper().writeValueAsString(loveReport);
    }

    /**
     * 工具调用功能
     * 支持调用各种工具（如天气查询、搜索等）
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答（包含工具调用结果）
     */
    @GetMapping("/tools")
    public String doChatWithTools(String message, String chatId) {
        return loveApp.doChatWithTools(message, chatId);
    }

    /**
     * MCP服务调用
     * 调用MCP（Model Context Protocol）服务
     *
     * @param message 用户消息
     * @param chatId  对话ID
     * @return AI回答（包含MCP服务结果）
     */
    @GetMapping("/mcp")
    public String doChatWithMcp(String message, String chatId) {
        return loveApp.doChatWithMcp(message, chatId);
    }
}