# App 层说明

## 📌 重要提示

### LoveApp vs LoveAppService

项目中存在两个类似的类：

1. **`LoveApp`** (app 包) - 原始实现
2. **`LoveAppService`** (service 包) - **推荐使用** ✅

### 为什么有两个类？

- `LoveApp` 是项目早期的实现，直接在应用层处理业务逻辑
- `LoveAppService` 是重构后的版本，遵循更好的分层架构

### 应该使用哪个？

**强烈推荐使用 `LoveAppService`**，原因如下：

| 特性       | LoveApp | LoveAppService          |
| ---------- | ------- | ----------------------- |
| 异常处理   | ❌ 基础 | ✅ 完善（统一异常处理） |
| 参数校验   | ❌ 无   | ✅ 有（长度、空值检查） |
| 日志记录   | ⚠️ 部分 | ✅ 详细                 |
| 错误重试   | ❌ 无   | ✅ 有（智能重试）       |
| 业务分层   | ❌ 混乱 | ✅ 清晰                 |
| 代码维护性 | ⚠️ 一般 | ✅ 优秀                 |

### 代码示例

❌ **不推荐**（使用 LoveApp）：

```java
@RestController
public class MyController {

    @Resource
    private LoveApp loveApp;  // 不推荐

    @GetMapping("/chat")
    public String chat(String message, String chatId) {
        return loveApp.doChat(message, chatId, false);
    }
}
```

✅ **推荐**（使用 LoveAppService）：

```java
@RestController
public class MyController {

    @Resource
    private LoveAppService loveAppService;  // 推荐 ✅

    @GetMapping("/chat")
    public String chat(String message, String chatId) {
        return loveAppService.chat(message, chatId, false);
    }
}
```

### LoveApp 会被删除吗？

**不会**。`LoveApp` 会保留用于：

- 向后兼容（已有代码可能依赖它）
- 学习参考（展示不同的实现方式）
- 演示对比（说明为什么需要重构）

但新功能和新项目应该使用 `LoveAppService`。

### 迁移指南

如果你的代码正在使用 `LoveApp`，可以按以下步骤迁移：

1. **替换注入**：

   ```java
   // 旧代码
   @Resource
   private LoveApp loveApp;

   // 新代码
   @Resource
   private LoveAppService loveAppService;
   ```

2. **替换方法调用**：

   ```java
   // 旧代码
   loveApp.doChat(message, chatId, enableTools);
   loveApp.doChatByStream(message, chatId, enableTools);
   loveApp.doChatWithRag(message, chatId, enableTools);

   // 新代码
   loveAppService.chat(message, chatId, enableTools);
   loveAppService.chatStream(message, chatId, enableTools);
   loveAppService.chatWithRag(message, chatId, enableTools);
   ```

3. **处理异常**：

   ```java
   // 旧代码（需要自己处理异常）
   try {
       String result = loveApp.doChat(message, chatId, false);
   } catch (Exception e) {
       // 自己处理异常
   }

   // 新代码（异常已统一处理）
   String result = loveAppService.chat(message, chatId, false);
   // 异常会被 GlobalExceptionHandler 自动处理
   ```

### 相关文档

- [LoveAppService 源码](../service/LoveAppService.java)
- [GlobalExceptionHandler](../exception/GlobalExceptionHandler.java)
- [架构设计文档](../../../../docs/architecture.md)

---

**总结**：新项目请使用 `LoveAppService`，它提供了更好的异常处理、参数校验和代码维护性。
