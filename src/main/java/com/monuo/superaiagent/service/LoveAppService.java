package com.monuo.superaiagent.service;

import com.monuo.superaiagent.app.LoveApp;
import com.monuo.superaiagent.exception.BusinessException;
import com.monuo.superaiagent.service.QuestionClassifierService.QuestionType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 恋爱大师业务服务（推荐使用）
 * 
 * ✅ 这是推荐的业务服务层实现
 * 
 * 相比 LoveApp，此类提供：
 * - ✅ 完善的异常处理和错误重试
 * - ✅ 统一的参数校验
 * - ✅ 清晰的业务逻辑分层
 * - ✅ 详细的日志记录
 * - ✅ 智能降级策略
 * 
 * 负责业务逻辑编排、对话模式选择、问题分类等
 * 
 * @see com.monuo.superaiagent.app.LoveApp (原始实现，保留用于兼容)
 */
@Service
@Slf4j
public class LoveAppService {

    @Resource
    private ChatClientService chatClientService;

    @Resource
    private RagService ragService;

    @Resource
    private QuestionClassifierService questionClassifierService;

    /**
     * 基础对话（同步）
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具调用
     * @return AI回答
     */
    public String chat(String message, String chatId, boolean enableTools) {
        validateInput(message, chatId);
        try {
            return chatClientService.chat(message, chatId, enableTools, false);
        } catch (Exception e) {
            log.error("基础对话失败: message={}, chatId={}", message, chatId, e);
            throw new BusinessException(500, "AI对话失败，请稍后重试", e);
        }
    }

    /**
     * 基础对话（流式）
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具调用
     * @return 流式AI回答
     */
    public Flux<String> chatStream(String message, String chatId, boolean enableTools) {
        validateInput(message, chatId);
        return chatClientService.chatStream(message, chatId, enableTools, false)
                .onErrorResume(e -> {
                    log.error("流式对话失败: message={}, chatId={}", message, chatId, e);
                    return Flux.just("抱歉，AI服务暂时不可用，请稍后重试。");
                });
    }

    /**
     * RAG 对话（同步）
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具调用
     * @return AI回答（基于知识库）
     */
    public String chatWithRag(String message, String chatId, boolean enableTools) {
        validateInput(message, chatId);
        try {
            return chatClientService.chat(message, chatId, enableTools, true);
        } catch (Exception e) {
            log.error("RAG对话失败: message={}, chatId={}", message, chatId, e);
            throw new BusinessException(500, "知识库查询失败，请稍后重试", e);
        }
    }

    /**
     * RAG 对话（流式）
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具调用
     * @return 流式AI回答（基于知识库）
     */
    public Flux<String> chatWithRagStream(String message, String chatId, boolean enableTools) {
        validateInput(message, chatId);
        return chatClientService.chatStream(message, chatId, enableTools, true)
                .onErrorResume(e -> {
                    log.error("RAG流式对话失败: message={}, chatId={}", message, chatId, e);
                    return Flux.just("抱歉，知识库服务暂时不可用，请稍后重试。");
                });
    }

    /**
     * 智能对话（同步）
     * 核心策略：
     * 1. 先判断问题类型
     * 2. 敏感问题 → 拒绝回答
     * 3. 恋爱相关/通用/未知 → 尝试RAG，相似度低则fallback到通用LLM
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具调用
     * @return AI回答
     */
    public String smartChat(String message, String chatId, boolean enableTools) {
        validateInput(message, chatId);
        
        try {
            // 1. 问题分类
            QuestionType type = questionClassifierService.classify(message);
            log.info("问题分类结果: {}, 问题: {}", type, message);

            // 2. 敏感问题拒绝
            if (type == QuestionType.SENSITIVE) {
                return "抱歉，我无法回答这个问题。";
            }

            // 3. RAG fallback 策略
            if (ragService.hasRelevantDocs(message)) {
                // 有相关文档，使用 RAG
                return chatClientService.chat(message, chatId, enableTools, true);
            } else {
                // 无相关文档，fallback 到基础对话
                log.info("RAG检索无结果，fallback到通用LLM");
                return chatClientService.chat(message, chatId, enableTools, false);
            }
        } catch (Exception e) {
            log.error("智能对话异常: message={}, chatId={}", message, chatId, e);
            throw new BusinessException(500, "智能对话失败，请稍后重试", e);
        }
    }

    /**
     * 智能对话（流式）
     * 自动RAG回退 + SSE流式传输
     * 推荐使用：提供最佳用户体验
     *
     * @param message 用户消息
     * @param chatId 对话ID
     * @param enableTools 是否启用工具调用
     * @return 流式AI回答
     */
    public Flux<String> smartChatStream(String message, String chatId, boolean enableTools) {
        validateInput(message, chatId);
        
        try {
            // 1. 问题分类
            QuestionType type = questionClassifierService.classify(message);
            log.info("问题分类结果: {}, 问题: {}", type, message);

            // 2. 敏感问题拒绝
            if (type == QuestionType.SENSITIVE) {
                return Flux.just("抱歉，我无法回答这个问题。");
            }

            // 3. RAG fallback 策略
            if (ragService.hasRelevantDocs(message)) {
                // 有相关文档，使用 RAG 流式
                return chatClientService.chatStream(message, chatId, enableTools, true)
                        .onErrorResume(e -> {
                            log.error("RAG流式对话失败，fallback到基础对话", e);
                            return chatClientService.chatStream(message, chatId, enableTools, false);
                        });
            } else {
                // 无相关文档，fallback 到基础流式对话
                log.info("RAG检索无结果，fallback到通用流式LLM");
                return chatClientService.chatStream(message, chatId, enableTools, false);
            }
        } catch (Exception e) {
            log.error("智能流式对话异常: message={}, chatId={}", message, chatId, e);
            return Flux.just("抱歉，AI服务暂时不可用，请稍后重试。");
        }
    }

    /**
     * 生成恋爱报告
     *
     * @param message 用户消息（描述恋爱情况）
     * @param chatId 对话ID
     * @return 恋爱报告
     */
    public LoveApp.LoveReport generateReport(String message, String chatId) {
        validateInput(message, chatId);
        try {
            return chatClientService.generateReport(message, chatId, LoveApp.LoveReport.class);
        } catch (Exception e) {
            log.error("生成报告失败: message={}, chatId={}", message, chatId, e);
            throw new BusinessException(500, "报告生成失败，请稍后重试", e);
        }
    }

    /**
     * 校验输入参数
     *
     * @param message 用户消息
     * @param chatId 对话ID
     */
    private void validateInput(String message, String chatId) {
        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException(400, "消息内容不能为空");
        }
        if (chatId == null || chatId.trim().isEmpty()) {
            throw new BusinessException(400, "对话ID不能为空");
        }
        if (message.length() > 10000) {
            throw new BusinessException(400, "消息内容过长，请控制在10000字符以内");
        }
    }
}
