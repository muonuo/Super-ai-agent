<template>
  <div class="app-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h2>恋爱对话</h2>
        <button class="new-chat-btn" @click="startNewChat">
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
              d="M12 4.5v15m7.5-7.5h-15"
            />
          </svg>
        </button>
      </div>
      <div class="session-list">
        <div
          v-for="session in sessions"
          :key="session.id"
          :class="['session-item', { active: session.id === chatId }]"
          @click="switchSession(session.id)"
        >
          <span class="session-title">{{ session.title }}</span>
        </div>
      </div>
    </aside>

    <!-- 主聊天区域 -->
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
          <h1 class="header-title">AI 恋爱大师</h1>
        </div>
      </header>

      <!-- 聊天内容区域 -->
      <div class="chat-content">
        <!-- 聊天记录区域 -->
        <div class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="empty-state">
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
                  d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z"
                />
              </svg>
            </div>
            <p>开始您的恋爱咨询之旅</p>

            <!-- 常见问题引导卡片 -->
            <div class="suggestions-container">
              <div
                v-for="suggestion in suggestions"
                :key="suggestion"
                class="suggestion-chip"
                @click="sendSuggestion(suggestion)"
              >
                {{ suggestion }}
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
                src="https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/1cf5e4088-29a2-4d5f-948a-ad6897b13cad9326.png"
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
              <template v-if="msg.content || displayedContent[index]">
                <div
                  class="message-text"
                  v-html="formatMessage(getDisplayContent(msg, index))"
                ></div>
                <span v-if="getIsTyping(index)" class="typing-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </span>
              </template>
              <div
                v-else-if="msg.role === 'assistant' && isLoading"
                class="typing-indicator"
              >
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 - 在 chat-content 外面 -->
      <footer class="chat-input-area">
        <div class="input-wrapper" :style="inputAreaStyle" @click="handleInputAreaClick">
          <textarea
            ref="inputRef"
            v-model="inputMessage"
            placeholder="输入您的问题..."
            @keydown.enter.exact.prevent="sendMessage"
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
import { ref, onMounted, nextTick, computed } from "vue";
import { doChatWithLoveAppSse, generateUUID } from "../api/chat";

const chatId = ref("");
const messages = ref([]);
const inputMessage = ref("");
const isLoading = ref(false);
const messagesContainer = ref(null);
const inputRef = ref(null); // 输入框引用

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

const sidebarCollapsed = ref(false);

// 计算输入框样式，根据侧边栏状态动态调整
const inputAreaStyle = computed(() => {
  // 始终居中显示
  return {
    margin: '0 auto',
    width: 'calc(100% - 48px)',
    maxWidth: '900px',
  };
});

const sessions = ref([
  { id: "1", title: "如何追喜欢的女生" },
  { id: "2", title: "恋爱中如何沟通" },
  { id: "3", title: "第一次约会要注意什么" },
]);

const displayedContent = ref({});
const isTyping = ref({});

const suggestions = ref([
  "如何追喜欢的女生",
  "第一次约会应该聊什么",
  "怎么判断她喜不喜欢我",
  "表白被拒绝了怎么办",
  "恋爱中如何保持新鲜感",
  "如何制造浪漫惊喜",
]);

const sendSuggestion = async (suggestion) => {
  inputMessage.value = suggestion;
  await sendMessage();
};

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

const startNewChat = () => {
  chatId.value = generateUUID();
  messages.value = [];
  displayedContent.value = {};
  isTyping.value = {};
};

const switchSession = (sessionId) => {
  chatId.value = sessionId;
  messages.value = [];
  displayedContent.value = {};
  isTyping.value = {};
};

onMounted(() => {
  chatId.value = generateUUID();
});

const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const startTypingEffect = (index, fullContent) => {
  if (!displayedContent.value[index]) {
    displayedContent.value[index] = "";
  }
  if (!isTyping.value[index]) {
    isTyping.value[index] = true;
  }

  let currentIndex = 0;
  const typingSpeed = 30;

  const typeChar = () => {
    if (currentIndex < fullContent.length) {
      displayedContent.value[index] += fullContent[currentIndex];
      currentIndex++;
      scrollToBottom();
      setTimeout(typeChar, typingSpeed);
    } else {
      isTyping.value[index] = false;
    }
  };

  typeChar();
};

const getDisplayContent = (msg, index) => {
  if (msg.role === "assistant" && displayedContent.value[index] !== undefined) {
    return displayedContent.value[index];
  }
  return msg.content;
};

const getIsTyping = (index) => {
  return isTyping.value[index] === true;
};

const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) return;

  messages.value.push({ role: "user", content: message });
  inputMessage.value = "";
  isLoading.value = true;
  await scrollToBottom();

  try {
    const stream = await doChatWithLoveAppSse(message, chatId.value);
    const reader = stream.getReader();
    const decoder = new TextDecoder();

    messages.value.push({ role: "assistant", content: "" });
    const aiIndex = messages.value.length - 1;
    displayedContent.value[aiIndex] = "";
    isTyping.value[aiIndex] = true;

    let fullContent = "";
    let buffer = "";

    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        if (buffer.length > 0) {
          fullContent += buffer;
          displayedContent.value[aiIndex] = fullContent;
        }
        break;
      }

      const text = decoder.decode(value, { stream: true });
      buffer += text;

      const lines = buffer.split("\n");
      buffer = lines.pop() || "";

      for (const line of lines) {
        if (line.startsWith("data:")) {
          const content = line.slice(5).trim();
          if (content) {
            fullContent += content;
            displayedContent.value[aiIndex] = fullContent;
            await scrollToBottom();
          }
        }
      }
    }

    isTyping.value[aiIndex] = false;
    messages.value[aiIndex].content = fullContent;
  } catch (error) {
    console.error("SSE Error:", error);
    messages.value.push({
      role: "assistant",
      content: "抱歉，发生了错误，请稍后重试。",
    });
  } finally {
    isLoading.value = false;
    await scrollToBottom();
    // 发送消息后聚焦输入框，方便下次输入
    focusInput();
  }
};

const formatMessage = (content) => {
  if (!content) return "";
  return content.replace(/\n/g, "<br>");
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;500;600&display=swap");

/* 应用容器 */
.app-container {
  display: flex;
  height: 100vh;
  background: #faf9f6;
  position: relative;
  overflow: hidden;
  font-family:
    "Noto Sans SC",
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
}

/* 侧边栏 */
.sidebar {
  width: 280px;
  background: #f2f3f5;
  border-right: 1px solid #e5e6eb;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  position: relative;
  z-index: 10;
}

.sidebar.collapsed {
  width: 0;
  overflow: hidden;
  border-right: none;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid #e5e6eb;
}

.sidebar-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #165dff;
  margin: 0;
}

.new-chat-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #ec4899 0%, #a855f7 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(236, 72, 153, 0.4);
}

.new-chat-btn:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 6px 25px rgba(236, 72, 153, 0.6);
}

.new-chat-btn svg {
  width: 20px;
  height: 20px;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  margin-bottom: 4px;
  color: #4e5969;
  background: transparent;
}

.session-item:hover {
  background: #e8f3ff;
  color: #165dff;
}

.session-item.active {
  background: #e5e6eb;
  color: #1d2129;
}

.session-item svg {
  width: 18px;
  height: 18px;
  color: #6b7280;
  flex-shrink: 0;
}

.session-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow-y: auto;
  position: relative;
  z-index: 5;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  position: relative;
  padding-bottom: 80px; /* 为输入框留出空间 */
}

/* 顶部导航 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.menu-btn,
.back-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: #8b5cf6;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.menu-btn:hover,
.back-btn:hover {
  background: rgba(139, 92, 246, 0.1);
  transform: scale(1.05);
}

.menu-btn svg,
.back-btn svg {
  width: 24px;
  height: 24px;
}

.header-info {
  margin-left: 16px;
}

.header-title {
  font-size: 24px;
  font-weight: 600;
  color: #8b5cf6;
}

/* 聊天记录 */
.chat-messages {
  flex: 1;
  overflow-y: visible;
  padding: 24px 24px 120px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #a1a1aa;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(
    135deg,
    rgba(236, 72, 153, 0.3) 0%,
    rgba(168, 85, 247, 0.3) 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 8px 32px rgba(236, 72, 153, 0.3);
  animation: pulse-glow 2s ease-in-out infinite;
}

@keyframes pulse-glow {
  0%,
  100% {
    box-shadow: 0 8px 32px rgba(236, 72, 153, 0.3);
  }
  50% {
    box-shadow: 0 8px 40px rgba(236, 72, 153, 0.5);
  }
}

.empty-icon svg {
  width: 40px;
  height: 40px;
  color: #f472b6;
}

.empty-state p {
  font-size: 18px;
  color: #6b7280;
  margin: 0;
}

/* 建议卡片 */
.suggestions-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
  max-width: 600px;
  margin-top: 32px;
}

.suggestion-chip {
  padding: 12px 20px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  font-size: 14px;
  color: #111827;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.suggestion-chip:hover {
  background: #e5e7eb;
  border-color: #d1d5db;
  color: #111827;
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* 消息气泡 */
.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
  animation: message-fade-in 0.4s ease-out;
}

@keyframes message-fade-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
}

.message.assistant .avatar {
  background: transparent;
  box-shadow: none;
  border: none;
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
  border: none;
}

.avatar svg {
  width: 20px;
  height: 20px;
}

.message-content {
  padding: 16px 20px;
  border-radius: 20px;
  position: relative;
  transition: all 0.3s ease;
}

.message.assistant .message-content {
  background: #f9fafb;
  color: #374151;
  border-bottom-left-radius: 4px;
  border: 1px solid #e5e7eb;
}

.message.user .message-content {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}

.message-text {
  font-size: 15px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 打字光标 */
.typing-dots {
  display: inline-flex;
  gap: 3px;
  align-items: center;
  margin-left: 4px;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f472b6;
  animation: dot-bounce 1.4s infinite ease-in-out both;
}

.typing-dots span:nth-child(1) {
  animation-delay: -0.32s;
}
.typing-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes dot-bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 12px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f472b6;
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
  padding: 16px 24px 28px;
  background: transparent;
  z-index: 100;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f3f4f6;
  border-radius: 40px;
  padding: 12px 12px 12px 28px;
  border: 1px solid rgba(6, 182, 212, 0.1);
  transition: all 0.3s ease;
  max-width: 800px;
  width: 100%;
  box-sizing: border-box;
}

.input-wrapper:focus-within {
  background: #ffffff;
  /* 柔和的淡青色光晕效果 */
  box-shadow:
    0 0 0 1px rgba(6, 182, 212, 0.3),
    0 4px 20px rgba(6, 182, 212, 0.15),
    0 0 40px rgba(6, 182, 212, 0.1),
    inset 0 -2px 10px rgba(6, 182, 212, 0.05);
  transform: scale(1.01);
}

.input-wrapper textarea {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  resize: none;
  max-height: 100px;
  line-height: 1.6;
  background: transparent;
  color: #111827;
  width: 100%;
}

.input-wrapper textarea::placeholder {
  color: #9ca3af;
}

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #ec4899 0%, #a855f7 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(236, 72, 153, 0.4);
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 6px 25px rgba(236, 72, 153, 0.6);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.send-btn svg {
  width: 20px;
  height: 20px;
}

/* 滚动条 - 浏览器右侧 */
.chat-main::-webkit-scrollbar {
  width: 8px;
}

.chat-main::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.5);
}

.chat-main::-webkit-scrollbar-thumb {
  background: rgba(236, 72, 153, 0.4);
  border-radius: 4px;
}

.chat-main::-webkit-scrollbar-thumb:hover {
  background: rgba(236, 72, 153, 0.6);
}

/* 响应式 - 平板 */
@media (max-width: 1024px) {
  .sidebar {
    width: 240px;
  }

  .chat-content {
    max-width: 100%;
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

  .orb-1,
  .orb-2 {
    width: 250px;
    height: 250px;
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

  .suggestions-container {
    gap: 8px;
  }

  .suggestion-chip {
    padding: 10px 16px;
    font-size: 13px;
  }

  .empty-state p {
    font-size: 16px;
  }
}

/* 减少动画 */
@media (prefers-reduced-motion: reduce) {
  .star,
  .heart,
  .gradient-orb,
  .message,
  .suggestion-chip,
  .input-wrapper,
  .send-btn {
    animation: none;
    transition: none;
  }
}
</style>
