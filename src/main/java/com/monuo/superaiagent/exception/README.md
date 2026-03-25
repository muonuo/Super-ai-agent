# 全局异常处理使用指南

## 📋 概述

本项目实现了完善的全局异常处理机制，特别针对 LLM 调用失败的情况提供友好的用户提示。

**核心组件：**

- `Result` - 统一响应格式
- `BusinessException` - 业务异常类
- `GlobalExceptionHandler` - 全局异常处理器
- **Spring AI 异常处理** - 专门处理 LLM 调用失败
- **参数校验** - 自动校验输入参数
- **流式响应异常处理** - Flux 异常的优雅降级

## 🎯 核心功能

### 1. 参数自动校验

Service 层会自动校验输入参数，提供友好提示：

- 消息内容不能为空
- 对话ID不能为空
- 消息长度限制（10000字符）

### 2. LLM 异常智能识别

自动识别 Spring AI 异常类型，提供针对性提示：

- **频率限制**: "AI服务请求过于频繁，请稍后再试"
- **认证失败**: "AI服务认证失败，请联系管理员"
- **内容过滤**: "您的请求包含不当内容，请修改后重试"
- **服务不可用**: "AI服务暂时不可用，请稍后重试"

### 3. 流式响应优雅降级

流式接口异常时返回友好消息，不会中断连接

## 💡 已完成的改进

✅ 添加了 Spring AI 异常处理（ChatClientException、TransientAiException、NonTransientAiException）  
✅ 添加了超时异常处理（TimeoutException、SocketTimeoutException）  
✅ Service 层添加了参数校验（validateInput方法）  
✅ Service 层添加了异常包装（所有方法都有 try-catch）  
✅ 流式响应添加了 onErrorResume 优雅降级  
✅ 智能识别错误类型，提供针对性的友好提示

## 📊 异常处理流程

```
用户请求 → Controller → Service（参数校验）→ ChatClient 调用 LLM
                                    ↓
                              [异常发生]
                                    ↓
                    Service 层捕获并包装异常
                                    ↓
              GlobalExceptionHandler 统一处理
                                    ↓
                返回标准格式的友好错误响应
```

## 🚀 使用效果

现在当 LLM 无法回复用户时，系统会：

1. 自动捕获异常
2. 记录详细日志
3. 返回友好的错误提示
4. 保持系统稳定运行

**不再出现：**

- ❌ 500 Internal Server Error
- ❌ 堆栈信息暴露给用户
- ❌ 连接中断或超时无提示

**现在会返回：**

- ✅ 清晰的错误码和消息
- ✅ 用户友好的提示
- ✅ 统一的响应格式
- ✅ 详细的后台日志

## 📝 响应示例

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "AI的回答内容...",
  "timestamp": 1711234567890
}
```

**参数错误：**

```json
{
  "code": 400,
  "message": "消息内容不能为空",
  "data": null,
  "timestamp": 1711234567890
}
```

**AI服务异常：**

```json
{
  "code": 503,
  "message": "AI服务暂时不可用，请稍后重试",
  "data": null,
  "timestamp": 1711234567890
}
```

**频率限制：**

```json
{
  "code": 429,
  "message": "AI服务请求过于频繁，请稍后再试",
  "data": null,
  "timestamp": 1711234567890
}
```

## 🎨 代码已完成优化

所有 Service 方法都已添加：

- ✅ 参数校验
- ✅ 异常捕获
- ✅ 友好提示
- ✅ 日志记录

无需额外配置，开箱即用！
