# RAG Fallback 方案实现详解

## 概述

RAG Fallback 方案旨在解决 AI 对通用问题回复过于泛化的问题，通过结合 RAG 知识库检索和 LLM 通用能力，提供更精准的回答。当知识库中有相关内容时，使用 RAG 检索增强回答；当知识库中没有相关内容时，自动 fallback 到通用 LLM 回答。

## 架构设计

### 整体流程

```
用户问题
    │
    ▼
┌─────────────────┐
│ QuestionClassifierService │
│   (问题分类)    │
└─────────────────┘
    │
    ▼
┌─────────────────┐
│  问题类型判断   │
└─────────────────┘
    │
    ├─ SENSITIVE ──────► 拒绝回答
    │
    ├─ LOVE_RELATED ──► │
    ├─ GENERAL    ──► │ 尝试RAG
    └─ UNKNOWN    ──► │
                          │
                          ▼
                   ┌─────────────────┐
                   │  相似度检测      │
                   │  (阈值: 0.6)    │
                   └─────────────────┘
                          │
            ┌─────────────┴─────────────┐
            ▼                           ▼
    相似度 >= 0.6                相似度 < 0.6
            │                           │
            ▼                           ▼
    使用 RAG 回答            fallback 到通用 LLM
```

### 核心组件

| 组件 | 文件位置 | 职责 |
|------|----------|------|
| QuestionClassifierService | `service/QuestionClassifierService.java` | 问题分类，判断问题类型 |
| LoveApp | `app/LoveApp.java` | 核心对话逻辑，RAG 检索与 fallback |
| AiController | `controller/AiController.java` | API 端点暴露 |

## 实现细节

### 1. 问题分类服务 (QuestionClassifierService)

问题分类服务通过关键词匹配的方式，将用户问题分为四类：

```java
public enum QuestionType {
    LOVE_RELATED,  // 恋爱相关问题
    GENERAL,       // 通用问题（天气、新闻、历史等）
    SENSITIVE,     // 敏感问题（暴力、色情、赌博等）
    UNKNOWN        // 未知类型
}
```

#### 分类优先级

1. **敏感词检测** (最高优先级) - 包含暴力、色情、政治等关键词直接拒绝
2. **恋爱关键词检测** - 包含恋爱、表白、分手等关键词
3. **通用关键词检测** - 包含天气、科技、教育等关键词
4. **默认未知** - 无法分类的问题

#### 关键代码

```java
public QuestionType classify(String question) {
    // 1. 首先检测敏感词
    if (containsSensitiveKeyword(question)) {
        return QuestionType.SENSITIVE;
    }

    // 2. 检测恋爱关键词
    if (containsLoveKeyword(question)) {
        return QuestionType.LOVE_RELATED;
    }

    // 3. 检测通用问题关键词
    if (containsGeneralKeyword(question)) {
        return QuestionType.GENERAL;
    }

    // 4. 默认返回未知
    return QuestionType.UNKNOWN;
}
```

### 2. RAG Fallback 对话方法 (LoveApp)

#### 主方法：doChatWithRagFallback

```java
public String doChatWithRagFallback(String message, String chatId) {
    // 1. 先判断问题类型
    QuestionType type = questionClassifierService.classify(message);

    switch (type) {
        case SENSITIVE:
            return "抱歉，我无法回答这个问题。";

        case LOVE_RELATED:
        case GENERAL:
        case UNKNOWN:
        default:
            // 恋爱相关/通用/未知问题，都尝试RAG
            return doChatWithRagOrFallback(message, chatId);
    }
}
```

**核心策略**：
- 敏感问题 → 直接拒绝
- **恋爱相关/通用/未知** → 都尝试 RAG 检索

这个设计允许通用问题（如"你会什么"）也能利用 RAG 知识库进行回答，如果知识库中没有相关内容，再 fallback 到通用 LLM。

#### Fallback 方法：doChatWithRagOrFallback

```java
private String doChatWithRagOrFallback(String message, String chatId) {
    try {
        // 1. 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);

        // 2. 向量检索
        List<Document> docs = pgVectorVectorStore.similaritySearch(
            SearchRequest.builder()
                .query(rewrittenMessage)
                .topK(3)
                .build()
        );

        // 3. 相似度检测
        boolean hasRelevantDocs = docs.stream()
            .anyMatch(doc -> doc.getScore() >= RAG_SIMILARITY_THRESHOLD);

        if (!hasRelevantDocs) {
            // RAG无结果，fallback到通用LLM
            return doChat(message, chatId);
        }

        // 4. 有结果，使用RAG回答
        return doChatWithRag(message, chatId);

    } catch (Exception e) {
        // 异常处理，fallback到通用LLM
        return doChat(message, chatId);
    }
}
```

**关键参数**：
- `RAG_SIMILARITY_THRESHOLD = 0.6` - 相似度阈值
- `topK = 3` - 检索 Top 3 相关文档

### 3. API 端点

```java
@GetMapping("/love_app/chat/rag_fallback")
public String doChatWithRagFallback(String message, String chatId) {
    return loveApp.doChatWithRagFallback(message, chatId);
}
```

**调用示例**：
```
GET /api/ai/love_app/chat/rag_fallback?message=你会什么&chatId=test123
```

## 技术亮点

### 1. 查询重写 (Query Rewriting)

在 RAG 检索前，使用 QueryRewriter 对用户问题进行改写，提升检索效果：

```java
String rewrittenMessage = queryRewriter.doQueryRewrite(message);
```

### 2. 相似度阈值控制

通过设置相似度阈值（0.6），可以灵活控制何时使用 RAG、何时 fallback：

- **高相似度** (>= 0.6) → 使用 RAG，知识库回答更精准
- **低相似度** (< 0.6) → fallback 到 LLM，避免低质量回答

### 3. 异常保护

RAG 检索异常时自动 fallback 到通用 LLM，保证服务可用性：

```java
} catch (Exception e) {
    log.error("RAG调用异常，fallback到通用LLM", e);
    return doChat(message, chatId);
}
```

### 4. 通用问题也走 RAG

方案的一个关键改进是：**通用问题（如"你会什么"）也会尝试 RAG 检索**。这意味着：

- 如果知识库中有相关回答，使用知识库内容
- 如果知识库中没有，fallback 到通用 LLM

这样既利用了知识库的专业性，又保证了通用问题的回答能力。

## 日志示例

```
# 问题分类
INFO  - 问题分类结果: GENERAL, 问题: 你会什么

# RAG 检索有结果
INFO  - RAG检索到3条相关文档，使用RAG回答

# RAG 检索无结果
INFO  - RAG检索无结果（相似度<0.6），fallback到通用LLM
```

## 配置说明

### 向量存储配置

项目使用 PostgreSQL + pgvector 作为向量存储：

```yaml
# application.yaml
spring:
  ai:
    vectorstore:
      pgvector:
        dimensions: 1024  # 向量维度
```

### 数据库要求

需要 PostgreSQL 安装 pgvector 扩展：

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

## 扩展方向

### 1. 动态阈值调整

可以根据问题类型动态调整相似度阈值：

```java
private double getThreshold(QuestionType type) {
    return switch (type) {
        case LOVE_RELATED -> 0.7;  // 恋爱问题需要更精准
        case GENERAL -> 0.5;       // 通用问题可以放宽
        default -> 0.6;
    };
}
```

### 2. 多知识库支持

可以扩展为多知识库，例如：
- 恋爱知识库
- 通用知识库
- 专业领域知识库

### 3. ML 分类器

当前使用关键词匹配，可以升级为 ML 模型分类：

```java
// 后续可接入 BERT 等模型进行更准确的分类
```

## 总结

RAG Fallback 方案通过问题分类 + 相似度检测 + 自动降级的机制，实现了：
1. **精准回答** - 知识库相关问题时使用 RAG 增强回答
2. **兜底能力** - 知识库无相关内容时 fallback 到通用 LLM
3. **服务可用** - 异常情况下保证服务可用
4. **灵活扩展** - 支持多知识库、动态阈值等扩展
