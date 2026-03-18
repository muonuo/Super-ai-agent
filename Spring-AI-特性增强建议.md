# Spring AI 项目特性增强建议

本文档列出当前项目已使用的特性，以及建议添加的Spring AI特性，以增强项目竞争力。

---

## 一、项目已使用的Spring AI特性

### 1. ChatClient (对话客户端)
- 文件位置: `src/main/java/com/monuo/superaiagent/app/LoveApp.java`
- 使用方式: 基于DashScope ChatModel构建对话客户端

### 2. ChatMemory (对话记忆)
- 文件位置: `src/main/java/com/monuo/superaiagent/config/MemoryConfig.java`
- 实现方式:
  - InMemoryChatMemoryRepository (内存存储)
  - MessageWindowChatMemory (窗口消息记忆，最多20条)
  - DatabaseBasedChatMemory (MySQL数据库存储)
  - FileBasedChatMemory (文件存储)

### 3. DocumentReader (文档读取器)
- 文件位置: `src/main/java/com/monuo/superaiagent/rag/`
- 使用方式:
  - MarkdownDocumentReader: 读取本地Markdown文档
  - GitHubDocumentReader: 自定义实现，从GitHub仓库读取文档

### 4. VectorStore (向量存储)
- 配置位置:
  - `LoveAppVectorStoreConfig.java` - SimpleVectorStore内存向量存储
  - `LoveAppStoreConfig.java` - 带文件缓存的向量存储
  - `PgVectorVectorStoreConfig.java` - PostgreSQL PGVector向量数据库

### 5. EmbeddingModel (嵌入模型)
- 使用: 阿里云百练DashScopeEmbeddingModel

### 6. RAG检索增强
- 文件位置: `src/main/java/com/monuo/superaiagent/rag/`
- 实现方式:
  - QuestionAnswerAdvisor: 基于向量存储的问答增强
  - RetrievalAugmentationAdvisor: 自定义检索增强
  - VectorStoreDocumentRetriever: 向量存储文档检索器

### 7. QueryTransformer (查询转换器)
- 文件位置:
  - `QueryRewriter.java` - 查询重写
  - `translation/TranslationQueryTransformer.java` - 翻译查询转换(百度翻译API)

### 8. MultiQueryExpander (查询扩展)
- 文件位置: `src/main/java/com/monuo/superaiagent/demo/rag/MultiQueryExpanderDemo.java`

### 9. KeywordMetadataEnricher (关键词元信息增强)
- 文件位置: `src/main/java/com/monuo/superaiagent/rag/MyKeywordEnricher.java`

### 10. ContextualQueryAugmenter (上下文查询增强)
- 文件位置: `src/main/java/com/monuo/superaiagent/rag/LoveAppContextualQueryAugmenterFactory.java`

### 11. 自定义Advisor
- 文件位置: `src/main/java/com/monuo/superaiagent/advisor/`
- 实现: MyLoggerAdvisor、ReReadingAdvisor、MessageChatMemoryAdvisor

---

## 12. ToolContext (工具上下文) — 优先级: ⭐⭐

**特性说明**: 在调用工具时传递上下文信息，使工具能够感知会话、用户等上下文。

**应用场景**:
- 多用户文件隔离: 每个用户的文件存放在独立目录
- 操作审计: 记录谁在什么时间做了什么操作
- 个性化服务: 根据用户偏好调整工具行为

**代码示例**:

```java
// 在 LoveApp.java 中添加工具上下文
Map<String, Object> toolContext = new HashMap<>();
toolContext.put("chatId", chatId);
toolContext.put("timestamp", System.currentTimeMillis());

chatClient.prompt()
    .user(message)
    .toolCallbacks(allTools)
    .toolContext(toolContext)  // 传递上下文
    .call()
    .content();

// 工具接收上下文
public class FileOperationTool {

    @Tool(description = "Read content from a file")
    public String readFile(
            @ToolParam(description = "Name of the file to read") String fileName,
            ToolExecutionContext context) {
        // 可以获取 chatId，实现用户隔离
        String chatId = (String) context.get("chatId");
        String filePath = FILE_DIR + "/" + chatId + "/" + fileName;
        // ...
    }
}
```

---

## 二、已集成的AI Provider

| Provider | 用途 | 状态 |
|----------|------|------|
| 阿里云百练DashScope | 聊天、嵌入、向量存储、云知识库 | 主要 |
| Ollama | 本地模型调用 | 示例 |
| LangChain4j | 社区版DashScope | 示例 |
| 阿里云RAG云服务 | 云知识库检索 | 集成 |

---

## 三、建议添加的Spring AI特性

### 1. Function Calling (工具调用) — 优先级: ⭐⭐⭐

**特性说明**: 允许AI在对话中调用自定义函数，实现真正的"行动型"AI助手。

**应用场景**:
- 天气查询: 用户问"明天北京天气怎么样"，AI自动调用天气API
- 纪念日提醒: 查询恋爱纪念日倒计时
- 情感分析: 调用情感分析模型
- 数据库操作: 读取/写入用户数据

**代码示例**:

```java
@Component
public class LoveTools {

    @Tool(name = "get_weather", description = "获取指定城市的天气情况")
    public String getWeather(@ToolParam(name = "city", description = "城市名称") String city) {
        // 调用天气API
        return "天气晴，25度";
    }

    @Tool(name = "calculate_love_days", description = "计算恋爱天数")
    public int calculateLoveDays(@ToolParam(name = "startDate", description = "恋爱开始日期，格式yyyy-MM-dd") String startDate) {
        LocalDate start = LocalDate.parse(startDate);
        return (int) ChronoUnit.DAYS.between(start, LocalDate.now());
    }

    @Tool(name = "send_love_message", description = "发送甜蜜消息")
    public String sendLoveMessage(@ToolParam(name = "message", description = "消息内容") String message) {
        // 发送消息逻辑
        return "消息已发送: " + message;
    }
}
```

**配置方式**:

```java
List<ToolCallback> tools = ToolCallback.from(loveTools);
ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
    .defaultTools(tools)
    .build();
```

---

### 2. Structured Output (结构化输出) — 优先级: ⭐⭐⭐

**特性说明**: 直接输出结构化JSON数据，而非自由文本。

**应用场景**:
- 生成恋爱报告(双方匹配度分析)
- 输出约会计划(时间、地点、注意事项)
- 情感状态报表

**代码示例**:

```java
// 定义输出结构
public record LoveReport(
    String coupleName,
    int matchScore,
    List<String> strengths,
    List<String> suggestions,
    String conclusion
) {}

// 使用结构化输出
LoveReport report = chatClient.prompt()
    .system("你是一个恋爱顾问，分析情侣的匹配度")
    .user(userInput)
    .output(OpenAiJsonOutputOptions.builder()
        .responseFormat(LoveReport.class)
        .build())
    .call()
    .entity(LoveReport.class);
```

---

### 3. Image Generation (图像生成) — 优先级: ⭐⭐

**特性说明**: 根据文本描述生成图像。

**应用场景**:
- 生成情侣头像
- 浪漫约会场景配图
- 情书/情话配图
- 纪念日海报

**添加依赖**:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

**配置**:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: dall-e-3
```

**代码示例**:

```java
@Service
public class ImageGenerationService {

    @Autowired
    private OpenAiImageModel imageModel;

    public byte[] generateLoveImage(String prompt) {
        ImageResponse response = imageModel.call(
            new ImagePrompt(
                prompt,
                OpenAiImageOptions.builder()
                    .quality("standard")
                    .size("1024x1024")
                    .style("vivid")
                    .build()
            )
        );
        return response.getResult().getOutput().getImage();
    }
}
```

**使用示例**:
```java
// 生成情侣头像
byte[] avatar = imageGenerationService.generateLoveImage(
    "A cute couple avatar, anime style, pink background, hearts"
);

// 生成浪漫场景
byte[] scene = imageGenerationService.generateLoveImage(
    "A romantic candlelight dinner on a rooftop, stars in the sky, Paris"
);
```

---

### 4. Audio (语音处理) — 优先级: ⭐⭐

**特性说明**: 支持语音输入和输出，打造语音AI助手。

**应用场景**:
- 语音输入问题: 用户可以直接说话提问
- 语音回复: AI语音回复，支持粤语/普通话
- 语音情书: 将文字情书转为语音

**添加依赖**:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

**Speech-to-Text (语音转文字)**:

```java
@Service
public class TranscriptionService {

    @Autowired
    private OpenAiTranscriptionModel transcriptionModel;

    public String transcribe(MultipartFile audioFile) throws IOException {
        return transcriptionModel.call(
            new TranscriptionPrompt(
                audioFile.getBytes(),
                new Media(MimeTypeUtilsUtils.parseMimeType(audioFile.getContentType()))
            )
        ).getResult().getOutput();
    }
}
```

**Text-to-Speech (文字转语音)**:

```java
@Service
public class SpeechService {

    @Autowired
    private OpenAiSpeechModel speechModel;

    public byte[] textToSpeech(String text) {
        SpeechResponse response = speechModel.call(
            new SpeechPrompt(
                text,
                OpenAiSpeechOptions.builder()
                    .voice(OpenAiSpeechOptions.Voice.NOVA)
                    .speed(1.0f)
                    .responseFormat(OpenAiSpeechOptions.ResponseFormat.MP3)
                    .build()
            )
        );
        return response.getResult().getOutput();
    }
}
```

---

### 5. Multi-modal (多模态 - 图片理解) — 优先级: ⭐⭐

**特性说明**: 让AI分析用户上传的图片内容。

**应用场景**:
- 分析情侣合照的情感
- 解读截图内容
- 分析用户发的礼物图片

**代码示例**:

```java
@Service
public class ImageAnalysisService {

    @Autowired
    private ChatModel chatModel;

    public String analyzeImage(byte[] imageBytes, String question) {
        UserMessage userMessage = new UserMessage(
            question,
            List.of(new Media("image/jpeg", imageBytes))
        );

        ChatResponse response = chatModel.call(
            new Prompt(userMessage)
        );

        return response.getResult().getOutput().getText();
    }
}
```

**使用示例**:

```java
// 分析情侣合照
String analysis = imageAnalysisService.analyzeImage(
    photoBytes,
    "分析这张照片中两人的情感状态，从表情、姿势、氛围等方面评价"
);
```

---

### 6. Agents (智能体) — 优先级: ⭐⭐

**特性说明**: Spring AI Agent框架，支持AI自主规划和执行多步骤任务。

**应用场景**:
- 自动制定约会计划(查天气→选餐厅→预约→发送提醒)
- 智能情感咨询流程
- 多轮任务型对话

**代码示例**:

```java
@Service
public class LoveAgentService {

    @Autowired
    private ChatModel chatModel;

    public AgentExecutor<AgentState> buildDatePlannerAgent() {
        Agent agent = Agent.builder()
            .name("约会规划师")
            .description("帮助用户规划浪漫约会")
            .chatModel(chatModel)
            .tools(ToolCallback.from(loveTools))
            .instruction("""
                你是一个专业的约会规划师。
                1. 先询问用户的偏好
                2. 根据天气、预算推荐约会方案
                3. 制定详细的时间安排
                4. 提供注意事项
                """)
            .build();

        return AgentExecutor.builder()
            .agent(agent)
            .toolExecutor(toolExecutor)
            .build();
    }
}
```

---

### 7. Observability (可观测性) — 优先级: ⭐

**特性说明**: 监控AI调用情况，包括Token消耗、延迟、调用次数等。

**应用场景**:
- 监控API调用成本
- 分析用户使用行为
- 性能优化

**添加依赖**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**配置**:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,metrics,health
  observations:
    enable: true
  metrics:
    tags:
      application: ${spring.application.name}
```

**可获取的指标**:
- `spring.ai.chat.client.calls.total`: Chat调用总次数
- `spring.ai.chat.client.tokens.total`: Token消耗
- `spring.ai.chat.client.duration`: 调用延迟
- `spring.ai.vectorstore.search.duration`: 向量检索延迟

---

### 8. 更多AI Provider集成 — 优先级: ⭐

| Provider | 特性 | 集成建议 |
|----------|------|----------|
| **OpenAI** | GPT-4o多模态、Whisper语音、DALL-E图像 | 推荐集成，生态最全 |
| **Anthropic Claude** | 超长上下文(200K)、Claude Code | 适合长对话场景 |
| **Google Gemini** | 1M Token上下文、视频理解 | 适合多模态需求 |
| **Azure OpenAI** | 企业级安全、合规 | 适合企业部署 |

---

## 四、特性增强路线图

### 第一阶段: 核心能力提升

1. **Function Calling** - 让AI能够调用工具，真正"行动"起来
2. **Structured Output** - 规范化输出格式，提升用户体验

### 第二阶段: 体验升级

3. **Image Generation** - 生成恋爱主题图片
4. **Audio** - 支持语音对话
5. **Multi-modal** - 图片理解能力

### 第三阶段: 智能化

6. **Agents** - 打造智能约会规划师
7. **Observability** - 完善监控和成本控制

---

## 五、总结

当前项目已经全面使用了Spring AI的核心特性，包括:
- ✅ ChatClient对话客户端
- ✅ ChatMemory对话记忆
- ✅ RAG检索增强
- ✅ VectorStore向量存储
- ✅ QueryTransformer查询转换
- ✅ MultiQueryExpander查询扩展
- ✅ 多Provider集成

建议优先添加:
1. **Function Calling** - 最具价值的特性，让AI从"回答问题"升级到"解决问题"
2. **Structured Output** - 规范化输出，便于后续处理
3. **语音/图像能力** - 打造差异化的恋爱AI助手体验

---

## 六、常用扩展工具建议

基于项目现有的 6 个工具，以下是适合 **AI 恋爱助手** 场景的扩展工具建议：

### 现有工具

| 工具 | 文件位置 | 功能 |
|------|----------|------|
| FileOperationTool | `src/main/java/com/monuo/superaiagent/tools/` | 文件读写 |
| WebSearchTool | | 网页搜索 |
| WebScrapingTool | | 网页抓取 |
| TerminalOperationTool | | 终端执行 |
| ResourceDownloadTool | | 资源下载 |
| PDFGenerationTool | | PDF生成 |

### 推荐新增工具

#### 1. 天气查询工具 🌤️

```java
@Tool(description = "获取指定城市的天气信息")
public String getWeather(@ToolParam(description = "城市名称") String city) {
    // 调用天气API（如心知天气、OpenWeatherMap）
    return "今天" + city + "天气晴，25度，适合约会";
}
```

**场景**: 用户问"今天天气怎么样"

---

#### 2. 提醒/日程工具 ⏰

```java
@Tool(description = "设置提醒")
public String setReminder(
    @ToolParam(description = "提醒内容") String content,
    @ToolParam(description = "提醒时间，格式yyyy-MM-dd HH:mm") String time) {
    // 存储到数据库或发送通知
    return "已设置提醒：" + content + "，时间：" + time;
}
```

**场景**: 用户说"记得明天提醒我打电话"

---

#### 3. 邮件发送工具 📧

```java
@Tool(description = "发送电子邮件")
public String sendEmail(
    @ToolParam(description = "收件人邮箱") String to,
    @ToolParam(description = "邮件主题") String subject,
    @ToolParam(description = "邮件正文") String body) {
    // 调用邮件服务发送
    return "邮件已发送给：" + to;
}
```

**场景**: 用户说"帮我发个邮件"

---

#### 4. 翻译工具 🌐

项目已集成百度翻译 API，可扩展为工具：

```java
@Tool(description = "翻译文本到指定语言")
public String translate(
    @ToolParam(description = "要翻译的文本") String text,
    @ToolParam(description = "目标语言：en-英语，ja-日语，ko-韩语") String toLang) {
    // 调用 BaiduTranslateUtil
    return translateUtil.translate(text, "auto", toLang);
}
```

**场景**: 用户说"帮我翻译一下这句话"

---

#### 5. 语音合成工具 🎤

```java
@Tool(description = "将文本转换为语音")
public String textToSpeech(
    @ToolParam(description = "要转换的文本") String text,
    @ToolParam(description = "语速，0.5-2.0") Float speed) {
    // 调用 TTS API 生成语音
    return "语音文件已生成：" + audioFilePath;
}
```

**场景**: 用户想让AI"说话"，生成甜言蜜语语音

---

#### 6. 图片生成工具 🎨

```java
@Tool(description = "根据描述生成图片")
public String generateImage(@ToolParam(description = "图片描述") String prompt) {
    // 调用 DALL-E 或 Stable Diffusion API
    return "图片已生成：" + imageUrl;
}
```

**场景**: 用户说"帮我画一幅画"

---

#### 7. 数据库查询工具 💾

```java
@Tool(description = "查询用户信息")
public String queryUser(
    @ToolParam(description = "查询类型：profile-用户资料，preferences-偏好设置") String type,
    @ToolParam(description = "用户ID") String userId) {
    // 查询数据库返回用户信息
}
```

**场景**: 需要查询用户历史记录、偏好设置

---

### 适合恋爱场景的工具优先级

| 工具 | 优先级 | 理由 |
|------|--------|------|
| 天气查询 | ⭐⭐⭐⭐⭐ | 关心式对话常用 |
| 翻译工具 | ⭐⭐⭐⭐⭐ | 项目已有百度翻译，易集成 |
| 提醒工具 | ⭐⭐⭐⭐ | 恋爱纪念日提醒 |
| 语音合成 | ⭐⭐⭐ | 生成甜蜜语音 |
| 图片生成 | ⭐⭐⭐ | 生成浪漫配图 |
| 邮件发送 | ⭐⭐ | 需要邮箱服务支持 |
| 数据库查询 | ⭐⭐ | 需要配合用户系统 |

---

*文档生成日期: 2026-02-17*
*最后更新: 2026-02-19 - 添加 ToolContext 和扩展工具建议*
