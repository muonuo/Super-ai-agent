package com.monuo.superaiagent.exception;

import com.monuo.superaiagent.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种异常，返回标准格式的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", errorMessage);
        return Result.error(400, "参数校验失败: " + errorMessage);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", errorMessage);
        return Result.error(400, "参数绑定失败: " + errorMessage);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getParameterName());
        return Result.error(400, "缺少必需参数: " + e.getParameterName());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: name={}, value={}, requiredType={}", 
                e.getName(), e.getValue(), e.getRequiredType());
        return Result.error(400, String.format("参数类型错误: %s 应为 %s 类型", 
                e.getName(), e.getRequiredType().getSimpleName()));
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error(500, "系统内部错误: 空指针异常");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return Result.error(400, "参数错误: " + e.getMessage());
    }

    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleIllegalStateException(IllegalStateException e) {
        log.error("非法状态异常", e);
        return Result.error(500, "系统状态异常: " + e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error(500, "系统运行异常: " + e.getMessage());
    }

    /**
     * 处理 Spring AI 瞬时异常（可重试）
     */
    @ExceptionHandler(TransientAiException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Result<Void> handleTransientAiException(TransientAiException e) {
        log.warn("AI服务瞬时异常（可重试）: {}", e.getMessage());
        return Result.error(503, "AI服务繁忙，请稍后重试");
    }

    /**
     * 处理 Spring AI 非瞬时异常（不可重试）
     */
    @ExceptionHandler(NonTransientAiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleNonTransientAiException(NonTransientAiException e) {
        log.error("AI服务非瞬时异常（不可重试）", e);
        String message = e.getMessage();
        // 提供更友好的错误提示
        if (message != null) {
            if (message.contains("rate limit") || message.contains("quota")) {
                return Result.error(429, "AI服务请求过于频繁，请稍后再试");
            } else if (message.contains("authentication") || message.contains("api key")) {
                return Result.error(401, "AI服务认证失败，请联系管理员");
            } else if (message.contains("content filter") || message.contains("policy")) {
                return Result.error(400, "您的请求包含不当内容，请修改后重试");
            }
        }
        return Result.error(400, "AI服务请求失败: " + (message != null ? message : "未知错误"));
    }

    /**
     * 处理超时异常
     */
    @ExceptionHandler({TimeoutException.class, SocketTimeoutException.class})
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public Result<Void> handleTimeoutException(Exception e) {
        log.warn("请求超时: {}", e.getMessage());
        return Result.error(408, "AI响应超时，请稍后重试");
    }

    /**
     * 处理网络连接异常
     */
    @ExceptionHandler({java.net.ConnectException.class, java.net.UnknownHostException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Result<Void> handleNetworkException(Exception e) {
        log.error("网络连接异常: {}", e.getMessage());
        return Result.error(503, "AI服务连接失败，请检查网络或稍后重试");
    }

    /**
     * 处理JSON解析异常（结构化输出失败）
     */
    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleJsonProcessingException(com.fasterxml.jackson.core.JsonProcessingException e) {
        log.error("JSON解析异常", e);
        return Result.error(500, "AI返回数据格式异常，请重试或联系管理员");
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return Result.error(500, "系统错误，请联系管理员");
    }
}
