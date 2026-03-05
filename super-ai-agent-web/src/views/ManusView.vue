<template>
  <div class="app-container">
    <!-- 左侧会话历史侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: !sidebarOpen }">
      <div class="sidebar-header">
        <button class="new-chat-btn">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            width="18"
            height="18"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 4.5v15m7.5-7.5h-15"
            />
          </svg>
          新对话
        </button>
      </div>
      <div class="sidebar-content">
        <div class="chat-history-list">
          <!-- 当前会话 -->
          <div class="chat-history-item active">
            <div class="history-avatar assistant">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                width="14"
                height="14"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.75 3.104v5.714a2.25 2.25 0 0 1-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 0 1 4.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.8 15.3M14.25 3.104c.251.023.501.05.75.082M19.8 15.3l-1.57.393A9.065 9.065 0 0 1 12 15a9.065 9.065 0 0 0-6.23.693L5 14.8.8 1.402 1.402.5m14c1.232 1.232.65 3.318-1.067 3.611A48.309 48.309 0 0 1 12 21c-2.773 0-5.491-.235-8.135-.687-1.718-.293-2.3-2.379-1.067-3.61L5 14.5"
                />
              </svg>
            </div>
            <span>当前会话</span>
          </div>

          <!-- 历史对话列表 -->
          <div class="history-section">
            <div class="history-section-title">历史记录</div>
            <div
              v-for="history in chatHistory"
              :key="history.id"
              class="chat-history-item"
              @click="loadHistory(history)"
            >
              <div class="history-avatar" :class="history.role">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  width="14"
                  height="14"
                >
                  <path
                    v-if="history.role === 'assistant'"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M9.75 3.104v5.714a2.25 2.25 0 0 1-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 0 1 4.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.8 15.3M14.25 3.104c.251.023.501.05.75.082M19.8 15.3l-1.57.393A9.065 9.065 0 0 1 12 15a9.065 9.065 0 0 0-6.23.693L5 14.5m14.8.8 1.402 1.402c1.232 1.232.65 3.318-1.067 3.611A48.309 48.309 0 0 1 12 21c-2.773 0-5.491-.235-8.135-.687-1.718-.293-2.3-2.379-1.067-3.61L5 14.5"
                  />
                  <path
                    v-else
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"
                  />
                </svg>
              </div>
              <span class="history-title">{{ history.title }}</span>
              <button
                class="history-delete"
                @click.stop="deleteHistory(history.id)"
                title="删除"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2"
                  stroke="currentColor"
                  width="14"
                  height="14"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M6 18 18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <!-- 右侧主聊天区域 -->
    <main class="chat-main">
      <!-- 顶部导航 -->
      <header class="chat-header">
        <button class="menu-btn" @click="toggleSidebar">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"
            />
          </svg>
        </button>
        <router-link to="/" class="back-btn">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18"
            />
          </svg>
        </router-link>
        <div class="header-info">
          <h1 class="header-title">AI 超级智能体</h1>
          <span class="status-dot"></span>
          <span class="status-text">在线</span>
        </div>
      </header>

      <!-- 聊天记录区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div v-if="!hasMessages" class="empty-state">
          <!-- 顶部装饰 -->
          <div class="empty-header">
            <div class="empty-icon">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.75 3.104v5.714a2.25 2.25 0 0 1-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 0 1 4.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.8 15.3M14.25 3.104c.251.023.501.05.75.082M19.8 15.3l-1.57.393A9.065 9.065 0 0 1 12 15a9.065 9.065 0 0 0-6.23.693L5 14.5m14.8.8 1.402 1.402c1.232 1.232.65 3.318-1.067 3.611A48.309 48.309 0 0 1 12 21c-2.773 0-5.491-.235-8.135-.687-1.718-.293-2.3-2.379-1.067-3.61L5 14.5"
                />
              </svg>
            </div>
            <h2 class="empty-title">有什么我可以帮助你的？</h2>
            <p class="empty-desc">选择以下问题开始对话，或直接输入你的问题</p>
          </div>

          <!-- 苏格拉底式引导问题卡片 -->
          <div class="guide-cards">
            <div
              v-for="item in guideQuestions"
              :key="item.id"
              class="guide-card"
              @click="sendQuestion(item.question)"
            >
              <span class="guide-icon">{{ item.icon }}</span>
              <div class="guide-content">
                <span class="guide-question">{{ item.question }}</span>
                <span class="guide-desc">{{ item.desc }}</span>
              </div>
              <svg
                class="guide-arrow"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="2"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3"
                />
              </svg>
            </div>
          </div>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div class="avatar">
            <img
              v-if="msg.role === 'assistant'"
              src="https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/1c4b1e1de-9ccd-4d41-8f40-ed48d18297c83878.png"
              alt="AI头像"
              class="avatar-img"
            />
            <img
              v-else
              :src="currentUserAvatar"
              alt="用户头像"
              class="avatar-img"
            />
          </div>
          <div class="message-content">
            <!-- 用户消息直接显示 -->
            <div
              v-if="msg.role === 'user'"
              class="message-text"
              v-html="formatMessage(msg.content)"
            ></div>

            <!-- AI消息：包含思考过程和最终回复 -->
            <template v-else>
              <!-- 思考过程/工具执行过程 - 可折叠 -->
              <div
                v-if="msg.thinkingSteps && msg.thinkingSteps.length > 0"
                class="thinking-box"
              >
                <div class="thinking-header" @click="toggleThinking(index)">
                  <span class="thinking-icon" v-html="thinkingIconSvg"></span>
                  <span class="thinking-title"
                    >已思考（用时 {{ msg.thinkingTime || 0 }} 秒）</span
                  >
                  <span class="thinking-toggle">{{
                    msg.thinkingExpanded ? "▼" : "▶"
                  }}</span>
                </div>
                <div v-show="msg.thinkingExpanded" class="thinking-content">
                  <div
                    v-for="(step, stepIndex) in msg.thinkingSteps"
                    :key="stepIndex"
                    class="thinking-step"
                  >
                    {{ step }}
                  </div>
                </div>
              </div>

              <!-- 最终回复 -->
              <div
                class="message-text"
                v-html="getDisplayContent(msg, index)"
              ></div>

              <!-- 三点加载动画 -->
              <span
                v-if="isLoading && index === messages.length - 1"
                class="typing-dots"
              >
                <span></span>
                <span></span>
                <span></span>
              </span>
            </template>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <footer class="chat-input-area">
        <div class="input-wrapper" :style="inputAreaStyle" @click="handleInputAreaClick">
          <textarea
            ref="inputRef"
            v-model="inputMessage"
            placeholder="输入您的问题..."
            @keydown="handleKeydown"
            :disabled="isLoading"
            rows="1"
          ></textarea>
          <button
            class="send-btn"
            @click="sendMessage"
            :disabled="!inputMessage.trim() || isLoading"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="2"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5"
              />
            </svg>
          </button>
        </div>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from "vue";
import { doChatWithManus, generateUUID } from "../api/chat";

const chatId = ref("");
const messages = ref([]);
const inputMessage = ref("");
const isLoading = ref(false);
const messagesContainer = ref(null);
const inputRef = ref(null); // 输入框引用

// 聚焦输入框
const focusInput = () => {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.focus();
    }
  });
};

// 点击输入框区域聚焦
const handleInputAreaClick = () => {
  focusInput();
};
const displayContent = ref(""); // 用于打字机效果的当前显示内容
const fullContent = ref(""); // 完整内容
const sidebarOpen = ref(true); // 侧边栏是否展开
let typeWriterTimer = null; // 打字机定时器

// 计算输入框样式，始终居中显示
const inputAreaStyle = computed(() => {
  return {
    margin: '0 auto',
    width: 'calc(100% - 48px)',
    maxWidth: '900px',
  };
});

// 历史对话列表（硬编码）
const chatHistory = ref([
  {
    id: "1",
    title: "帮我解释什么是机器学习",
    role: "assistant",
    preview: "机器学习是人工智能的一个分支...",
    time: "2小时前",
  },
  {
    id: "2",
    title: "如何用Python写一个排序算法",
    role: "assistant",
    preview: "我来帮你实现几种常见的排序算法...",
    time: "昨天",
  },
  {
    id: "3",
    title: "推荐几本编程入门书籍",
    role: "assistant",
    preview: "根据你的需求，我推荐以下几本书...",
    time: "3天前",
  },
  {
    id: "4",
    title: "帮我分析这段代码的性能",
    role: "assistant",
    preview: "这段代码的时间复杂度是O(n²)...",
    time: "1周前",
  },
  {
    id: "5",
    title: "如何优化数据库查询",
    role: "assistant",
    preview: "数据库优化可以从以下几个维度入手...",
    time: "2周前",
  },
]);

// 加载历史会话
const loadHistory = (history) => {
  console.log("加载历史会话:", history.title);
  // 这里可以添加加载历史消息的逻辑
};

// 删除历史会话
const deleteHistory = (id) => {
  const index = chatHistory.value.findIndex((h) => h.id === id);
  if (index > -1) {
    chatHistory.value.splice(index, 1);
  }
};

// 苏格拉底式引导问题
const guideQuestions = [
  {
    id: 1,
    icon: "💡",
    question: "你能帮我分析这个问题吗？",
    desc: "深入拆解问题核心",
  },
  { id: 2, icon: "📋", question: "请给我一些建议", desc: "提供专业解决方案" },
  {
    id: 3,
    icon: "🔍",
    question: "我想了解更多关于...",
    desc: "探索更多知识领域",
  },
  { id: 4, icon: "📖", question: "帮我解释这个概念", desc: "用简单的方式理解" },
  { id: 5, icon: "🎯", question: "帮我制定一个计划", desc: "清晰的行动步骤" },
  { id: 6, icon: "💬", question: "和我聊聊这个话题", desc: "深入交流讨论" },
];

// 点击引导问题发送消息
const sendQuestion = (question) => {
  inputMessage.value = question;
  sendMessage();
};

// 切换侧边栏
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value;
};

// 思考过程图标
const thinkingIconSvg = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" width="16" height="16">
  <path stroke-linecap="round" stroke-linejoin="round" d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09ZM18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 0 0-2.456 2.456ZM16.894 20.567 16.5 21.75l-.394-1.183a2.25 2.25 0 0 0-1.423-1.423L13.5 18.75l1.183-.394a2.25 2.25 0 0 0 1.423-1.423l.394-1.183.394 1.183a2.25 2.25 0 0 0 1.423 1.423l1.183.394-1.183.394a2.25 2.25 0 0 0-1.423 1.423Z" />
</svg>`;

// 切换思考过程展开/收起
const toggleThinking = (index) => {
  if (messages.value[index]) {
    messages.value[index].thinkingExpanded =
      !messages.value[index].thinkingExpanded;
  }
};

// AI 头像 SVG
const aiAvatarSvg = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
  <path stroke-linecap="round" stroke-linejoin="round" d="M9.75 3.104v5.714a2.25 2.25 0 0 1-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 0 1 4.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.8 15.3M14.25 3.104c.251.023.501.05.75.082M19.8 15.3l-1.57.393A9.065 9.065 0 0 1 12 15a9.065 9.065 0 0 0-6.23.693L5 14.5m14.8.8 1.402 1.402c1.232 1.232.65 3.318-1.067 3.611A48.309 48.309 0 0 1 12 21c-2.773 0-5.491-.235-8.135-.687-1.718-.293-2.3-2.379-1.067-3.61L5 14.5" />
</svg>`;

// 用户头像图片列表
const userAvatarImages = [
  'https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/2c90f3520-aeac-4561-ae0a-531b172cb2892227.png',
  'https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/6c90f3520-aeac-4561-ae0a-531b172cb2899475.png',
  'https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/5c90f3520-aeac-4561-ae0a-531b172cb2896875.png',
  'https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/4c90f3520-aeac-4561-ae0a-531b172cb2894881.png',
];

// 随机选择用户头像
const getRandomUserAvatar = () => {
  const index = Math.floor(Math.random() * userAvatarImages.length);
  return userAvatarImages[index];
};

// 响应式用户头像
const currentUserAvatar = ref(getRandomUserAvatar());

// 计算属性：是否有消息
const hasMessages = computed(() => messages.value.length > 0);

// 打字机效果 - 增量显示新内容
let displayedLength = 0; // 当前已显示的字符数

const startTypeWriter = (text) => {
  fullContent.value = text;

  // 如果已经有定时器在运行，只更新目标文本，不重置
  if (typeWriterTimer) {
    // 继续显示剩余字符（使用动态的 fullContent.value）
    return;
  }

  displayedLength = 0;
  displayContent.value = "";

  typeWriterTimer = setInterval(() => {
    // 每次都获取最新的 fullContent.value，支持增量内容
    const currentText = fullContent.value;
    if (displayedLength < currentText.length) {
      displayContent.value += currentText[displayedLength];
      displayedLength++;
      scrollToBottom();
    } else {
      clearInterval(typeWriterTimer);
      typeWriterTimer = null;
    }
  }, 30); // 30ms 每个字符
};

// 停止打字机
const stopTypeWriter = () => {
  if (typeWriterTimer) {
    clearInterval(typeWriterTimer);
    typeWriterTimer = null;
  }
  // 显示完整内容
  displayContent.value = fullContent.value;
  displayedLength = fullContent.value.length;
};

// 初始化 chatId
onMounted(() => {
  chatId.value = generateUUID();
});

// 组件卸载时清理
onUnmounted(() => {
  stopTypeWriter();
});

// 滚动到底部（使用原生方法优化性能）
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTo({
      top: messagesContainer.value.scrollHeight,
      behavior: "smooth",
    });
  }
};

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) return;

  // 添加用户消息
  messages.value.push({ role: "user", content: message });
  // 添加AI消息占位（用于流式显示），包含思考过程
  messages.value.push({
    role: "assistant",
    content: "",
    thinkingSteps: [],
    thinkingExpanded: true,
    thinkingTime: 0,
  });
  inputMessage.value = "";
  isLoading.value = true;
  displayContent.value = "";
  fullContent.value = "";
  displayedLength = 0; // 重置打字机计数器
  scrollToBottom();

  const startTime = Date.now();

  try {
    const stream = await doChatWithManus(message);
    const reader = stream.getReader();
    const decoder = new TextDecoder();
    let fullText = "";
    let buffer = "";

    // 流式读取并显示打字机效果
    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        // 处理最后剩余的 buffer
        if (buffer.length > 0) {
          processSseLine(buffer);
        }
        break;
      }

      // 解码并添加到 buffer
      const text = decoder.decode(value, { stream: true });
      buffer += text;

      // 按行分割，处理 SSE 格式
      const lines = buffer.split("\n");
      buffer = lines.pop() || ""; // 保留最后一行（可能是不完整的）

      for (const line of lines) {
        processSseLine(line);
      }
    }

    // 计算思考时间
    const aiMsg = messages.value[messages.value.length - 1];
    aiMsg.thinkingTime = Math.round((Date.now() - startTime) / 1000);
  } catch (error) {
    console.error("SSE Error:", error);
    messages.value[messages.value.length - 1].content =
      "抱歉，发生了错误，请稍后重试。";
    displayContent.value = formatMessage("抱歉，发生了错误，请稍后重试。");
  } finally {
    isLoading.value = false;
    scrollToBottom();
    // 发送消息后聚焦输入框，方便下次输入
    focusInput();
  }
};

// 处理 SSE 行的数据
const processSseLine = (line) => {
  if (!line.startsWith("data:")) return;

  const content = line.slice(5).trim();
  if (!content) return;

  const aiMsg = messages.value[messages.value.length - 1];
  if (!aiMsg) return;

  // 检查是否是最终回复（FINAL: 前缀）
  if (content.startsWith("FINAL:")) {
    const finalResponse = content.slice(6).trim();
    // 停止打字机并显示完整内容
    aiMsg.content = finalResponse;
    fullContent.value = finalResponse;
    stopTypeWriter();
    scrollToBottom();
    return;
  }

  // 检查是否是步骤信息（包含 "Step X:"）
  if (content.match(/^Step \d+:/) || content.includes("执行结束")) {
    // 只添加到思考步骤，不添加到主回复内容中
    aiMsg.thinkingSteps.push(content);
  } else {
    // 其他内容直接添加到消息，使用打字机效果
    aiMsg.content += (aiMsg.content ? "\n" : "") + content;
    fullContent.value = aiMsg.content;
    // 启动打字机效果显示新内容
    startTypeWriter(aiMsg.content);
  }

  scrollToBottom();
};

// 格式化消息（使用 innerText 防止 XSS）
const formatMessage = (content) => {
  if (!content) return "";
  // 转义 HTML 特殊字符，防止 XSS
  return content
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/\n/g, "<br>");
};

// 获取显示内容（用于打字机效果）
const getDisplayContent = (msg, index) => {
  // 如果是最后一条消息且正在加载，使用流式显示的内容
  if (index === messages.value.length - 1 && isLoading.value) {
    return displayContent.value;
  }
  // 否则显示完整内容
  return formatMessage(msg.content);
};

// 回车发送
const handleKeydown = (e) => {
  if (e.key === "Enter" && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
};
</script>

<style scoped>
/* 主容器 - 包含侧边栏和主聊天区 */
.app-container {
  display: flex;
  height: 100vh;
  background: #0f0d15;
}

/* 左侧会话历史侧边栏 */
.sidebar {
  width: 260px;
  background: #171717;
  border-right: 1px solid #2d2d2d;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition:
    width 0.3s ease,
    opacity 0.3s ease;
}

.sidebar.collapsed {
  width: 0;
  opacity: 0;
  overflow: hidden;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #2d2d2d;
}

.new-chat-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  background: #1f1f1f;
  border: 1px solid #2d2d2d;
  border-radius: 24px;
  color: #e5e5e5;
  font-family: "Roboto Mono", monospace;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.new-chat-btn:hover {
  background: #2d2d2d;
  border-color: #404040;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px 8px;
}

.chat-history-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  color: #71717a;
  font-family: "Roboto Mono", monospace;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.chat-history-item:hover {
  background: rgba(139, 92, 246, 0.1);
  border-color: rgba(139, 92, 246, 0.2);
  color: #a78bfa;
}

.chat-history-item.active {
  background: rgba(139, 92, 246, 0.15);
  border-color: rgba(139, 92, 246, 0.3);
  color: #e5e5e5;
}

/* 历史记录分区 */
.history-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #2d2d2d;
}

.history-section-title {
  font-family: "Roboto Mono", monospace;
  font-size: 11px;
  color: #52525b;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 0 16px 8px;
}

.history-title {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 13px;
}

.history-delete {
  opacity: 0;
  background: transparent;
  border: none;
  color: #71717a;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.chat-history-item:hover .history-delete {
  opacity: 1;
}

.history-delete:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.history-avatar {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.history-avatar.assistant {
  background: linear-gradient(135deg, #7c3aed 0%, #06b6d4 100%);
  color: white;
}

.history-avatar.user {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
}

/* 右侧主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  height: 100vh;
  overflow-y: auto;
  position: relative;
  background: linear-gradient(180deg, #0f0d15 0%, #1e1b4b 100%);
}

/* 顶部导航 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: rgba(15, 13, 21, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(124, 58, 237, 0.3);
}

.menu-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: #a78bfa;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  margin-right: 8px;
}

.menu-btn:hover {
  background: rgba(124, 58, 237, 0.2);
}

.back-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: #a78bfa;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  margin-right: 8px;
}

.back-btn:hover {
  background: rgba(124, 58, 237, 0.2);
}

.back-btn svg {
  width: 24px;
  height: 24px;
}

.header-info {
  margin-left: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  font-family: "Exo", sans-serif;
  font-size: 20px;
  font-weight: 600;
  color: #e0e7ff;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
  box-shadow: 0 0 8px #10b981;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.status-text {
  font-family: "Roboto Mono", monospace;
  font-size: 12px;
  color: #10b981;
}

/* 聊天记录 */
.chat-messages {
  flex: 1;
  overflow-y: visible;
  padding: 24px 24px 120px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  color: #6b7280;
  padding: 60px 24px;
  overflow-y: auto;
}

/* 顶部 header */
.empty-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
}

.empty-title {
  font-family: "Roboto Mono", monospace;
  font-size: 24px;
  font-weight: 500;
  color: #e5e5e5;
  margin-bottom: 8px;
  text-align: center;
}

.empty-desc {
  font-family: "Roboto Mono", monospace;
  font-size: 14px;
  color: #8e8e8e;
  text-align: center;
  margin-bottom: 0;
}

/* 苏格拉底式引导问题卡片 */
.guide-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  max-width: 800px;
  width: 100%;
}

.guide-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.08) 0%,
    rgba(255, 255, 255, 0.03) 100%
  );
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.guide-card::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(139, 92, 246, 0.15) 0%,
    rgba(6, 212, 182, 0.08) 100%
  );
  opacity: 0;
  transition: opacity 0.3s ease;
}

.guide-card:hover {
  transform: translateY(-4px);
  border-color: rgba(139, 92, 246, 0.4);
  box-shadow:
    0 8px 32px rgba(139, 92, 246, 0.2),
    0 0 0 1px rgba(139, 92, 246, 0.1);
}

.guide-card:hover::before {
  opacity: 1;
}

.guide-icon {
  font-size: 24px;
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(139, 92, 246, 0.15);
  border-radius: 10px;
}

.guide-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.guide-question {
  font-size: 15px;
  font-weight: 500;
  color: #e5e5e5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.guide-desc {
  font-size: 12px;
  color: #71717a;
}

.guide-arrow {
  width: 18px;
  height: 18px;
  color: #71717a;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.guide-card:hover .guide-arrow {
  color: #a78bfa;
  transform: translateX(4px);
}

/* 响应式 */
@media (max-width: 768px) {
  .guide-cards {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .guide-card {
    padding: 14px 16px;
  }

  .empty-state {
    padding: 40px 16px;
  }
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(
    135deg,
    rgba(124, 58, 237, 0.3) 0%,
    rgba(6, 212, 182, 0.2) 100%
  );
  border: 2px solid rgba(124, 58, 237, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  position: relative;
}

.empty-icon::before {
  content: "";
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 1px solid rgba(124, 58, 237, 0.3);
  animation: rotate 4s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.empty-icon svg {
  width: 36px;
  height: 36px;
  color: #a78bfa;
}

.empty-state p {
  font-family: "Roboto Mono", monospace;
  font-size: 14px;
  letter-spacing: 1px;
}

/* 消息气泡 */
.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.message.assistant .avatar {
  background: transparent;
  box-shadow: none;
}

.avatar-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.message.user .avatar {
  background: transparent;
  box-shadow: none;
}

.message.assistant .avatar::before {
  content: "";
  position: absolute;
  inset: -2px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed 0%, #06b6d4 100%);
  z-index: -1;
  opacity: 0.5;
  filter: blur(8px);
}

.avatar svg {
  width: 20px;
  height: 20px;
}

.message-content {
  padding: 16px 20px;
  border-radius: 20px;
  position: relative;
}

.message.assistant .message-content {
  background: rgba(30, 27, 75, 0.8);
  color: #e0e7ff;
  border-bottom-left-radius: 4px;
  border: 1px solid rgba(124, 58, 237, 0.3);
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.15);
}

.message.user .message-content {
  background: linear-gradient(135deg, #7c3aed 0%, #a78bfa 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-text {
  font-family: "Roboto Mono", monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 思考过程/工具执行过程盒子 */
.thinking-box {
  background: rgba(15, 13, 21, 0.8);
  border: 1px solid rgba(124, 58, 237, 0.3);
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
}

.thinking-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  background: rgba(124, 58, 237, 0.1);
  transition: background 0.2s;
}

.thinking-header:hover {
  background: rgba(124, 58, 237, 0.2);
}

.thinking-icon {
  display: flex;
  align-items: center;
  color: #a78bfa;
}

.thinking-title {
  flex: 1;
  font-family: "Roboto Mono", monospace;
  font-size: 12px;
  color: #a78bfa;
}

.thinking-toggle {
  color: #6b7280;
  font-size: 10px;
}

.thinking-content {
  padding: 12px 16px;
  border-top: 1px solid rgba(124, 58, 237, 0.2);
}

.thinking-step {
  font-family: "Roboto Mono", monospace;
  font-size: 12px;
  color: #9ca3af;
  padding: 6px 0;
  border-bottom: 1px dashed rgba(124, 58, 237, 0.1);
}

.thinking-step:last-child {
  border-bottom: none;
}

/* 三点加载动画 */
.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-left: 4px;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #a78bfa;
  animation: typingBounce 1.4s infinite ease-in-out both;
}

.typing-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0s;
}

@keyframes typingBounce {
  0%,
  80%,
  100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 输入提示动画 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed 0%, #06b6d4 100%);
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 输入区域 */
.chat-input-area {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px 24px;
  background: transparent;
  z-index: 100;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 40px;
  padding: 14px 14px 14px 28px;
  border: 1px solid rgba(139, 92, 246, 0.15);
  width: 100%;
  transition: all 0.3s;
}

.input-wrapper:focus-within {
  background: rgba(255, 255, 255, 0.12);
  /* 柔和的蓝紫色光晕效果 */
  box-shadow:
    0 0 0 1px rgba(139, 92, 246, 0.3),
    0 4px 20px rgba(139, 92, 246, 0.2),
    0 0 40px rgba(139, 92, 246, 0.1),
    inset 0 -2px 10px rgba(139, 92, 246, 0.05);
}

.input-wrapper textarea {
  flex: 1;
  border: none;
  outline: none;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 15px;
  resize: none;
  max-height: 100px;
  line-height: 1.6;
  background: transparent;
  color: #ffffff;
}

.input-wrapper textarea::placeholder {
  color: #6b7280;
}

.send-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #7c3aed 0%, #06b6d4 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.send-btn::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #06b6d4 0%, #7c3aed 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.send-btn:hover:not(:disabled)::before {
  opacity: 1;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.5);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.send-btn svg {
  width: 20px;
  height: 20px;
  position: relative;
  z-index: 1;
}

/* 滚动条 - 浏览器右侧 */
.chat-main::-webkit-scrollbar {
  width: 8px;
}

.chat-main::-webkit-scrollbar-track {
  background: rgba(15, 13, 21, 0.5);
}

.chat-main::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.4);
  border-radius: 4px;
}

.chat-main::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 92, 246, 0.6);
}

/* 响应式 - 平板 */
@media (max-width: 1024px) {
  .sidebar {
    width: 240px;
  }
}

/* 响应式 - 手机 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100%;
    z-index: 100;
    width: 280px;
  }

  .sidebar.collapsed {
    transform: translateX(-100%);
    width: 280px;
  }

  .chat-header {
    padding: 12px 16px;
  }

  .header-title {
    font-size: 18px;
  }

  .chat-messages {
    padding: 16px;
  }

  .message {
    max-width: 90%;
  }

  .message-content {
    padding: 12px 16px;
  }

  .chat-input-area {
    padding: 12px 16px 20px;
  }

  .input-wrapper {
    padding: 10px 10px 10px 16px;
    border-radius: 24px;
  }

  .input-wrapper textarea {
    font-size: 14px;
  }

  .empty-state p {
    font-size: 14px;
  }
}

/* 减少动画 */
@media (prefers-reduced-motion: reduce) {
  .message,
  .thinking-box,
  .send-btn,
  .input-wrapper {
    animation: none;
    transition: none;
  }
}
</style>
