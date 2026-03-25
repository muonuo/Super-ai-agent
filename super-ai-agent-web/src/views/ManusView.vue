<template>
  <div class="manus-view-root">
    <!-- 删除确认弹窗 -->
    <div
      v-if="showDeleteModal"
      class="modal-overlay"
      @click="showDeleteModal = false"
    >
      <div class="modal-content" @click.stop>
        <div class="modal-header">确认删除</div>
        <div class="modal-body">
          <p>永久删除对话，删除后，该对话将不可恢复</p>
        </div>
        <div class="modal-footer">
          <button class="modal-btn cancel" @click="showDeleteModal = false">
            取消
          </button>
          <button class="modal-btn confirm" @click="deleteHistory">删除</button>
        </div>
      </div>
    </div>

    <!-- 重命名弹窗 -->
    <div
      v-if="showRenameModal"
      class="modal-overlay"
      @click="showRenameModal = false"
    >
      <div class="modal-content" @click.stop>
        <div class="modal-header">重命名对话</div>
        <div class="modal-body">
          <input
            v-model="renameValue"
            class="rename-input"
            placeholder="请输入新名称"
            @keyup.enter="submitRename"
          />
        </div>
        <div class="modal-footer">
          <button class="modal-btn cancel" @click="showRenameModal = false">
            取消
          </button>
          <button class="modal-btn confirm" @click="submitRename">确定</button>
        </div>
      </div>
    </div>

    <div class="app-container">
      <!-- 左侧会话历史侧边栏 -->
      <aside class="sidebar" :class="{ collapsed: !sidebarOpen }">
        <div class="sidebar-header">
          <span class="sidebar-title">历史记录</span>
        </div>
        <div class="sidebar-content">
          <div class="chat-history-list">
            <!-- 新对话按钮 -->
            <button class="new-chat-btn" @click="startNewChat">
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

            <!-- 历史对话列表 -->
            <div class="history-section">
              <div class="history-section-title">历史记录</div>
              <div
                v-for="history in chatHistory"
                :key="history.id"
                class="chat-history-item"
                :class="{ active: history.id === chatId }"
                @click="loadHistory(history)"
              >
                <span class="history-title">{{ history.title }}</span>
                <div class="history-actions">
                  <button
                    class="history-more"
                    @click.stop="toggleHistoryMenu(history.id)"
                    title="更多"
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
                        d="M12 6.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 12.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 18.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5Z"
                      />
                    </svg>
                  </button>
                  <!-- 下拉菜单 -->
                  <div
                    v-if="activeHistoryMenu === history.id"
                    class="history-menu"
                  >
                    <div
                      class="history-menu-item"
                      @click.stop="renameHistory(history)"
                    >
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
                          d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10"
                        />
                      </svg>
                      重命名
                    </div>
                    <div
                      class="history-menu-item delete"
                      @click.stop="confirmDeleteHistory(history.id)"
                    >
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
                          d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
                        />
                      </svg>
                      删除
                    </div>
                  </div>
                </div>
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
          <button class="back-btn" @click="goHome">
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
          </button>
          <div class="header-info">
            <h1 class="header-title">AI 超级智能体</h1>
            <span class="status-dot"></span>
            <span class="status-text">在线</span>
          </div>
        </header>

        <!-- 聊天记录区域 -->
        <div class="chat-messages" ref="messagesContainer" @scroll="handleUserScroll">
          <div v-if="!hasMessages" class="empty-state">
            <!-- 顶部标题 -->
            <div class="empty-header">
              <h2 class="empty-title">你好，有什么我可以帮助你的？</h2>
            </div>
          </div>

          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-content">
              <!-- 用户消息直接显示 -->
              <div
                v-if="msg.role === 'user'"
                class="message-text"
                v-html="formatMessage(msg.content)"
              ></div>

              <!-- AI消息：包含思考过程和最终回复 -->
              <template v-else>
                <!-- 思考过程 - Gemini 风格，默认收起 -->
                <div
                  v-if="
                    (msg.thinkingSteps && msg.thinkingSteps.length > 0) ||
                    (isLoading && index === messages.length - 1)
                  "
                  class="gemini-thinking-box"
                >
                  <div
                    class="gemini-thinking-header"
                    @click="toggleThinking(index)"
                  >
                    <div class="thinking-header-left">
                      <svg
                        class="thinking-icon"
                        :class="{
                          spinning: isLoading && index === messages.length - 1,
                        }"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="1.5"
                        stroke="currentColor"
                        width="16"
                        height="16"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09ZM18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 0 0-2.456 2.456ZM16.894 20.567 16.5 21.75l-.394-1.183a2.25 2.25 0 0 0-1.423-1.423L13.5 18.75l1.183-.394a2.25 2.25 0 0 0 1.423-1.423l.394-1.183.394 1.183a2.25 2.25 0 0 0 1.423 1.423l1.183.394-1.183.394a2.25 2.25 0 0 0-1.423 1.423Z"
                        />
                      </svg>
                      <span class="thinking-text">
                        {{
                          isLoading && index === messages.length - 1
                            ? "思考中"
                            : "显示思路"
                        }}
                      </span>
                      <!-- 三个点动画 -->
                      <span
                        v-if="isLoading && index === messages.length - 1"
                        class="thinking-dots-inline"
                      >
                        <span class="dot"></span>
                        <span class="dot"></span>
                        <span class="dot"></span>
                      </span>
                      <span v-if="msg.thinkingTime > 0" class="thinking-time"
                        >{{ msg.thinkingTime }}s</span
                      >
                    </div>
                    <!-- 展开/收起按钮 -->
                    <svg
                      class="thinking-chevron"
                      :class="{ expanded: msg.thinkingExpanded }"
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
                        d="m19.5 8.25-7.5 7.5-7.5-7.5"
                      />
                    </svg>
                  </div>
                  <!-- 思考内容（默认隐藏） -->
                  <div
                    v-show="msg.thinkingExpanded"
                    class="gemini-thinking-content"
                    :ref="
                      (el) => {
                        if (el && index === messages.length - 1)
                          thinkingContentRef = el;
                      }
                    "
                  >
                    <!-- 实时显示思考步骤 - Gemini 风格 -->
                    <div
                      v-for="(step, stepIndex) in msg.thinkingSteps"
                      :key="stepIndex"
                      class="thinking-step-gemini"
                      v-html="formatThinkingStep(step)"
                    ></div>
                    <!-- 正在思考时显示加载提示 -->
                    <div
                      v-if="isLoading && index === messages.length - 1"
                      class="thinking-step-gemini thinking-loading"
                    >
                      <div class="thinking-loader">
                        <span class="loader-dot"></span>
                        <span class="loader-dot"></span>
                        <span class="loader-dot"></span>
                      </div>
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
                  v-if="
                    isLoading && index === messages.length - 1 && msg.content
                  "
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

        <!-- 没有消息时的输入框（独立画布） -->
        <div v-if="!hasMessages" class="empty-input-container">
          <div class="empty-input-wrapper">
            <textarea
              ref="inputRef"
              v-model="inputMessage"
              placeholder="输入您的问题..."
              @keydown.enter.exact.prevent="sendMessage"
              @input="autoResize"
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
        </div>

        <!-- 输入区域 -->
        <footer v-if="hasMessages" class="chat-input-area">
          <div
            class="input-wrapper"
            :style="inputAreaStyle"
            @click="handleInputAreaClick"
          >
            <textarea
              ref="inputRef"
              v-model="inputMessage"
              placeholder="输入您的问题..."
              @keydown.enter.exact.prevent="sendMessage"
              @input="autoResize"
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import {
  doChatWithManus,
  generateUUID,
  getConversations,
  getConversationHistory,
  deleteConversation,
} from "../api/chat";

const router = useRouter();
const chatId = ref("");
const messages = ref([]);
const inputMessage = ref("");
const isLoading = ref(false);
const messagesContainer = ref(null);
const inputRef = ref(null); // 输入框引用
const thinkingContentRef = ref(null); // 思考框内容引用
const displayedContent = ref({}); // 打字机效果显示的内容
const isTyping = ref({}); // 打字机状态

// 用户滚动控制：检测用户是否手动滚动过
const userHasScrolled = ref(false); // 用户是否手动滚动过
const SCROLL_THRESHOLD = 100; // 距离底部多少像素以内认为是"在底部"

// 检查用户是否在底部附近
const checkIsUserNearBottom = () => {
  if (!messagesContainer.value) return true;
  const container = messagesContainer.value;
  const distanceFromBottom =
    container.scrollHeight - container.scrollTop - container.clientHeight;
  return distanceFromBottom < SCROLL_THRESHOLD;
};

// 监听用户滚动事件
const handleUserScroll = () => {
  if (!messagesContainer.value) return;
  const distanceFromBottom =
    messagesContainer.value.scrollHeight -
    messagesContainer.value.scrollTop -
    messagesContainer.value.clientHeight;
  // 如果用户滚动到距离底部较远的位置，标记为用户已手动滚动
  userHasScrolled.value = distanceFromBottom >= SCROLL_THRESHOLD;
};

// 滚动到底部（使用原生方法优化性能）
// 添加智能滚动：只有用户在底部时才自动滚动
const scrollToBottom = (force = false) => {
  if (messagesContainer.value) {
    // 如果强制滚动，或者用户没有滚动过，或者用户已经在底部，才自动滚动
    if (force || !userHasScrolled.value || checkIsUserNearBottom()) {
      messagesContainer.value.scrollTo({
        top: messagesContainer.value.scrollHeight,
        behavior: "smooth",
      });
    }
  }
};

// 加载会话列表（超级智能体）
const loadChatHistory = async () => {
  try {
    const data = await getConversations("manus");
    // 按会话ID分组，只保留每个会话的最后一条消息
    const groupedData = {};
    data.forEach((conversation) => {
      const convId = conversation.conversationId;
      if (!groupedData[convId]) {
        groupedData[convId] = conversation;
      } else {
        // 保留最后一条消息（按更新时间排序）
        const existingTime = groupedData[convId].updatedAt || 0;
        const newTime = conversation.updatedAt || 0;
        if (newTime > existingTime) {
          groupedData[convId] = conversation;
        }
      }
    });

    // 为每个会话获取标题
    const historyList = await Promise.all(
      Object.values(groupedData).map(async (conversation) => {
        let title = conversation.lastMessage || "新会话";
        let lastMessageTime = conversation.updatedAt || 0;

        // 如果最后一条消息是 JSON 格式的报告，需要获取倒数第二条消息
        if (title.trim().startsWith("{")) {
          try {
            const parsed = JSON.parse(title);
            // 如果是报告格式（包含 title 和 suggestions），需要获取更早的消息
            if (parsed.title && parsed.suggestions) {
              // 获取完整的对话历史，找到最后一条非报告消息
              const history = await getConversationHistory(
                conversation.conversationId,
              );
              // 从后往前找，找到第一条非 JSON 报告的消息
              for (let i = history.length - 1; i >= 0; i--) {
                const msg = history[i];
                if (
                  msg.role === "assistant" &&
                  msg.content &&
                  msg.content.trim().startsWith("{")
                ) {
                  // 是 JSON 报告，跳过
                  try {
                    const msgParsed = JSON.parse(msg.content);
                    if (msgParsed.title && msgParsed.suggestions) {
                      continue;
                    }
                  } catch (e) {
                    // 不是有效的 JSON 报告，使用这条消息
                    title = msg.content.substring(0, 30);
                    lastMessageTime = msg.timestamp || lastMessageTime;
                    break;
                  }
                } else if (msg.role === "user") {
                  // 是用户消息，使用这条
                  title = msg.content.substring(0, 30);
                  lastMessageTime = msg.timestamp || lastMessageTime;
                  break;
                } else if (msg.content && !msg.content.trim().startsWith("{")) {
                  // 是普通 AI 回复
                  title = msg.content.substring(0, 30);
                  lastMessageTime = msg.timestamp || lastMessageTime;
                  break;
                }
              }
              // 如果所有消息都是报告，显示为"新会话"
              if (title.trim().startsWith("{")) {
                title = "新会话";
              }
            }
          } catch (e) {
            // 不是有效的 JSON，保留原文
          }
        }

        return {
          id: conversation.conversationId,
          title: title,
          lastMessageTime: lastMessageTime,
          role: "assistant",
        };
      }),
    );

    chatHistory.value = historyList.sort(
      (a, b) => b.lastMessageTime - a.lastMessageTime,
    );
  } catch (error) {
    console.error("加载会话列表失败:", error);
    chatHistory.value = [];
  }
};

// 解析 AI 回复中的【思考】和【回复】部分
const parseThinkingAndReply = (content) => {
  if (!content || typeof content !== "string") {
    return {
      thinking: "",
      reply: content || "",
      hasThinking: false,
    };
  }

  const thinkingStart = content.indexOf("【思考】");
  const thinkingEnd = content.indexOf("【回复】");
  const replyStart = content.indexOf("【回复】");

  // 检查是否有完整的【思考】【回复】格式
  if (thinkingStart !== -1 && thinkingEnd !== -1 && replyStart !== -1) {
    try {
      // 提取思考内容（去掉【思考】标记）
      const thinkingContent = content
        .substring(thinkingStart + 4, thinkingEnd)
        .trim();

      // 提取回复内容（去掉【回复】标记）
      const replyContent = content.substring(replyStart + 4).trim();

      // 将思考内容按行分割，过滤空行和【工具调用】等标记
      const thinkingLines = thinkingContent
        .split("\n")
        .map((line) => line.trim())
        .filter((line) => {
          // 过滤空行
          if (!line) return false;
          // 过滤掉【开头的标记行（如【工具调用】）
          if (line.startsWith("[")) return false;
          if (line.startsWith("[")) return false; // 兼容全角括号
          return true;
        });

      return {
        thinking: thinkingContent,
        reply: replyContent,
        thinkingLines: thinkingLines,
        hasThinking: thinkingLines.length > 0,
      };
    } catch (error) {
      console.error("解析思考和回复失败:", error);
      return {
        thinking: "",
        reply: content,
        hasThinking: false,
      };
    }
  }

  // 没有【思考】【回复】标记，直接返回内容作为回复
  return {
    thinking: "",
    reply: content,
    hasThinking: false,
  };
};

// 加载指定会话的历史消息
const loadHistoryMessages = async (sessionId) => {
  try {
    console.log("加载会话历史，sessionId:", sessionId);
    const history = await getConversationHistory(sessionId);
    console.log("原始历史记录:", history);

    messages.value = history.map((msg) => {
      // 基础消息对象
      const baseMessage = {
        role: msg.role,
        thinkingSteps: [],
        thinkingTime: msg.thinkingTime || 0,
        thinkingExpanded: false, // 默认折叠思考框
      };

      // 如果是助手消息，需要处理思考内容
      if (msg.role === "assistant") {
        // 优先使用后端返回的 thinkingSteps（新格式）
        if (
          msg.thinkingSteps &&
          Array.isArray(msg.thinkingSteps) &&
          msg.thinkingSteps.length > 0
        ) {
          baseMessage.thinkingSteps = msg.thinkingSteps;
          baseMessage.content = msg.content || "";
          // 历史消息默认折叠思考框，保持简洁
          baseMessage.thinkingExpanded = false;
        } else {
          // 降级处理：解析 content 中的【思考】【回复】标记（旧格式）
          const parsed = parseThinkingAndReply(msg.content);

          // 如果解析出了思考内容，将其放入 thinkingSteps
          if (parsed.hasThinking && parsed.thinkingLines) {
            baseMessage.thinkingSteps = parsed.thinkingLines;
            // 历史消息默认折叠思考框，保持简洁
            baseMessage.thinkingExpanded = false;
          }

          // 只显示回复内容，不显示【思考】【回复】标记
          baseMessage.content = parsed.reply;
        }
      } else if (msg.role === "user") {
        // 用户消息直接使用 content
        baseMessage.content = msg.content || "";
        console.log("加载用户消息:", baseMessage.content);
      } else {
        // 未知角色，尝试使用 content
        console.warn("未知消息角色:", msg.role, "内容:", msg.content);
        baseMessage.content = msg.content || "";
        baseMessage.role = "user"; // 默认为用户消息
      }

      return baseMessage;
    });

    console.log("处理后的消息列表:", messages.value);

    displayedContent.value = {};
    isTyping.value = {};

    // 加载完成后滚动到底部
    await nextTick();
    scrollToBottom();
  } catch (error) {
    console.error("加载会话历史失败:", error);
    messages.value = [];
  }
};

// 聚焦输入框
const focusInput = () => {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.focus();
      // 确保光标可见
      inputRef.value.setSelectionRange(
        inputRef.value.value.length,
        inputRef.value.value.length,
      );
    }
  });
};

// 自动调整输入框高度
const autoResize = () => {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.style.height = 'auto';
      inputRef.value.style.height = Math.min(inputRef.value.scrollHeight, 150) + 'px';
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
const activeHistoryMenu = ref(null); // 当前展开的历史菜单
const showDeleteModal = ref(false); // 删除确认弹窗
const showRenameModal = ref(false); // 重命名弹窗
const deleteTargetId = ref(null); // 待删除的会话ID
const renameTarget = ref(null); // 待重命名的会话
const renameValue = ref(""); // 重命名输入框的值
let typeWriterTimer = null; // 打字机定时器

// 计算输入框样式
const inputAreaStyle = computed(() => {
  return {
    margin: "0 auto",
    width: "calc(100% - 48px)",
    maxWidth: "900px",
  };
});

// 历史对话列表（从后端获取）
const chatHistory = ref([]);

// 加载历史会话
const loadHistory = async (history) => {
  console.log("加载历史会话:", history.title);
  chatId.value = history.id;
  await loadHistoryMessages(history.id);
  focusInput();
};

// 切换历史菜单显示
const toggleHistoryMenu = (id) => {
  activeHistoryMenu.value = activeHistoryMenu.value === id ? null : id;
};

// 确认删除历史会话（显示弹窗）
const confirmDeleteHistory = (id) => {
  deleteTargetId.value = id;
  showDeleteModal.value = true;
  activeHistoryMenu.value = null;
};

// 删除历史会话（弹窗确认后）
const deleteHistory = async () => {
  if (deleteTargetId.value) {
    try {
      // 调用后端API删除数据库中的记录
      await deleteConversation(deleteTargetId.value);
    } catch (error) {
      console.error("删除会话失败:", error);
    }
    // 删除本地数据
    const index = chatHistory.value.findIndex(
      (h) => h.id === deleteTargetId.value,
    );
    if (index > -1) {
      chatHistory.value.splice(index, 1);
    }
  }
  showDeleteModal.value = false;
  deleteTargetId.value = null;
};

// 重命名历史会话（显示弹窗）
const renameHistory = (history) => {
  renameTarget.value = history;
  renameValue.value = history.title;
  showRenameModal.value = true;
  activeHistoryMenu.value = null;
};

// 提交重命名
const submitRename = () => {
  if (renameTarget.value && renameValue.value.trim()) {
    renameTarget.value.title = renameValue.value.trim();
  }
  showRenameModal.value = false;
  renameTarget.value = null;
  renameValue.value = "";
};

// 返回首页
const goHome = () => {
  router.push("/");
};

// 切换侧边栏
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value;
};

// 切换思考过程展开/收起
const toggleThinking = (index) => {
  if (messages.value[index]) {
    messages.value[index].thinkingExpanded =
      !messages.value[index].thinkingExpanded;
  }
};

// 用户头像图片列表
const userAvatarImages = [
  "https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/2c90f3520-aeac-4561-ae0a-531b172cb2892227.png",
  "https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/6c90f3520-aeac-4561-ae0a-531b172cb2899475.png",
  "https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/5c90f3520-aeac-4561-ae0a-531b172cb2896875.png",
  "https://image.qwenlm.ai/public_source/4b091f95-d494-4287-8d12-89bad63cdfa8/4c90f3520-aeac-4561-ae0a-531b172cb2894881.png",
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
      scrollToBottom(); // 不强制，智能判断
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

// 开始新对话
const startNewChat = () => {
  // 如果当前有对话内容，将其添加到历史列表
  if (messages.value.length > 0 && chatId.value) {
    // 获取第一条用户消息作为标题
    const firstUserMsg = messages.value.find((msg) => msg.role === "user");
    const title = firstUserMsg
      ? firstUserMsg.content.substring(0, 30)
      : "新会话";

    // 检查是否已经在历史列表中
    const existingIndex = chatHistory.value.findIndex(
      (h) => h.id === chatId.value,
    );
    if (existingIndex === -1) {
      // 添加到历史列表顶部
      chatHistory.value.unshift({
        id: chatId.value,
        title: title,
        role: "assistant",
      });
    } else {
      // 更新标题
      chatHistory.value[existingIndex].title = title;
    }
  }

  // 创建新对话
  chatId.value = generateUUID("manus");
  messages.value = [];
  inputMessage.value = "";
  focusInput();
};

// 发送消息后更新当前对话在历史列表中的显示
const updateCurrentChatInHistory = () => {
  if (messages.value.length > 0 && chatId.value) {
    // 获取第一条用户消息作为标题
    const firstUserMsg = messages.value.find((msg) => msg.role === "user");
    const title = firstUserMsg
      ? firstUserMsg.content.substring(0, 30)
      : "新会话";

    // 检查是否已经在历史列表中
    const existingIndex = chatHistory.value.findIndex(
      (h) => h.id === chatId.value,
    );
    if (existingIndex === -1) {
      // 添加到历史列表顶部
      chatHistory.value.unshift({
        id: chatId.value,
        title: title,
        role: "assistant",
      });
    } else {
      // 更新标题并移到顶部
      const item = chatHistory.value.splice(existingIndex, 1)[0];
      item.title = title;
      chatHistory.value.unshift(item);
    }
  }
};

// 初始化 chatId
onMounted(async () => {
  chatId.value = generateUUID("manus");
  await loadChatHistory();
  // 等待 DOM 更新后再聚焦
  await nextTick();
  focusInput();
});

// 组件卸载时清理
onUnmounted(() => {
  stopTypeWriter();
});

// 滚动思考框内容到底部
const scrollThinkingToBottom = () => {
  if (thinkingContentRef.value) {
    nextTick(() => {
      thinkingContentRef.value.scrollTo({
        top: thinkingContentRef.value.scrollHeight,
        behavior: "smooth",
      });
    });
  }
};

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) return;

  // 添加用户消息
  messages.value.push({ role: "user", content: message });
  // 添加AI消息占位（用于流式显示），包含思考过程，默认展开
  messages.value.push({
    role: "assistant",
    content: "",
    thinkingSteps: [],
    thinkingExpanded: true, // 默认展开，实时显示思考过程
    thinkingTime: 0,
  });
  inputMessage.value = "";
  isLoading.value = true;
  displayContent.value = "";
  fullContent.value = "";
  displayedLength = 0; // 重置打字机计数器
  scrollToBottom(true); // 发送消息时强制滚动到底部

  const startTime = Date.now();
  const aiMsg = messages.value[messages.value.length - 1];

  try {
    const stream = await doChatWithManus(message, chatId.value);
    const reader = stream.getReader();
    const decoder = new TextDecoder();
    let buffer = "";

    // 流式读取并显示打字机效果
    while (true) {
      const { done, value } = await reader.read();

      if (done) {
        // 处理最后剩余的 buffer
        if (buffer.length > 0) {
          processSseEvent(buffer, aiMsg);
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
        processSseEvent(line, aiMsg);
      }
    }

    // 计算思考时间
    aiMsg.thinkingTime = Math.round((Date.now() - startTime) / 1000);
  } catch (error) {
    console.error("SSE Error:", error);
    aiMsg.content = "抱歉，发生了错误，请稍后重试。";
    displayContent.value = formatMessage("抱歉，发生了错误，请稍后重试。");
  } finally {
    isLoading.value = false;
    scrollToBottom(true); // 完成时强制滚动到底部
    focusInput();
    // 更新当前对话在历史列表中的显示
    updateCurrentChatInHistory();
  }
};

// 处理 SSE 事件
let currentEventType = null; // 用于存储当前事件类型

const processSseEvent = (line, aiMsg) => {
  if (!line.trim()) return;
  if (!aiMsg) return;

  try {
    // 解析 SSE 格式：event: eventName 或 data: content
    if (line.startsWith("event:")) {
      // 暂存事件类型（下一行的 data 会用到）
      currentEventType = line.slice(6).trim();
      return;
    }

    if (line.startsWith("data:")) {
      const content = line.slice(5).trim();
      if (!content) return;

      const eventType = currentEventType || "message";

      // 根据事件类型处理
      switch (eventType) {
        case "thinking_start":
          // 思考开始
          console.log("思考开始");
          // 【修复】不清空思考步骤，让思考内容累积在同一个消息中
          // 只在第一次思考时初始化数组
          if (!aiMsg.thinkingSteps) {
            aiMsg.thinkingSteps = [];
          }
          aiMsg.thinkingExpanded = true; // 自动展开
          // 思考开始时强制滚动到底部
          nextTick(() => {
            scrollToBottom(true);
          });
          break;

        case "thinking_step":
          // 实时接收思考步骤
          try {
            const stepData = JSON.parse(content);
            if (stepData.step) {
              // 添加到思考步骤数组
              aiMsg.thinkingSteps.push(stepData.step);
              // 智能滚动：只有用户在底部时才滚动
              nextTick(() => {
                scrollThinkingToBottom();
                scrollToBottom(); // 不强制，智能判断
              });
            }
          } catch (e) {
            // 如果不是 JSON 格式，直接添加
            if (content && !aiMsg.thinkingSteps.includes(content)) {
              aiMsg.thinkingSteps.push(content);
              nextTick(() => {
                scrollThinkingToBottom();
                scrollToBottom(); // 不强制，智能判断
              });
            }
          }
          break;

        case "thinking_end":
          // 思考结束
          try {
            const endData = JSON.parse(content);
            aiMsg.thinkingTime = endData.time || 0;
            console.log("思考完成，耗时:", aiMsg.thinkingTime, "s");
          } catch (e) {
            console.error("解析思考结束事件失败:", e);
          }
          break;

        case "thinking":
          // 旧格式的思考内容（兼容）
          // 过滤掉【思考】标记本身
          let thinkingText = content.replace(/[\u3010\u3011]/g, "").trim();
          if (
            thinkingText &&
            !thinkingText.startsWith("思考") &&
            !aiMsg.thinkingSteps.includes(thinkingText)
          ) {
            aiMsg.thinkingSteps.push(thinkingText);
            nextTick(() => {
              scrollThinkingToBottom();
              scrollToBottom(); // 不强制，智能判断
            });
          }
          break;

        case "message":
          // 回复内容 - 使用打字机效果
          // 过滤掉【思考】和【回复】标记，只保留实际回复内容
          let filteredContent = content;
          if (filteredContent.includes("【思考】")) {
            // 如果内容包含【思考】标记，提取【回复】之后的内容
            const replyStart = filteredContent.indexOf("【回复】");
            if (replyStart !== -1) {
              filteredContent = filteredContent.substring(replyStart + 4);
            } else {
              // 没有【回复】标记，跳过包含【思考】的内容
              filteredContent = "";
            }
          }
          // 过滤掉单独的【工具调用】等标记行
          if (
            filteredContent.trim().startsWith("【") &&
            filteredContent.trim().includes("】")
          ) {
            filteredContent = "";
          }
          if (filteredContent.trim()) {
            aiMsg.content += (aiMsg.content ? "\n" : "") + filteredContent;
            fullContent.value = aiMsg.content;
            startTypeWriter(aiMsg.content);
          }
          break;

        default:
          console.log("未知事件类型:", eventType, "内容:", content);
      }

      scrollToBottom(); // 不强制，智能判断
      currentEventType = null; // 重置事件类型
    }
  } catch (error) {
    console.error("处理SSE事件失败:", error, line);
  }
};

// 格式化消息（支持简单 Markdown 格式）
const formatMessage = (content) => {
  if (!content) return "";

  // 先转义 HTML 特殊字符，防止 XSS
  let escaped = content
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");

  // 处理粗体 **text** 或 __text__
  escaped = escaped.replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>");
  escaped = escaped.replace(/__(.+?)__/g, "<strong>$1</strong>");

  // 处理斜体 *text* 或 _text_
  escaped = escaped.replace(/\*(.+?)\*/g, "<em>$1</em>");
  escaped = escaped.replace(/_(.+?)_/g, "<em>$1</em>");

  // 处理标题 # text
  escaped = escaped.replace(/^### (.+)$/gm, "<h3>$1</h3>");
  escaped = escaped.replace(/^## (.+)$/gm, "<h2>$1</h2>");
  escaped = escaped.replace(/^# (.+)$/gm, "<h1>$1</h1>");

  // 处理无序列表 - text 或 * text
  escaped = escaped.replace(/^[-*]\s+(.+)$/gm, "<li>$1</li>");

  // 处理有序列表 1. text
  escaped = escaped.replace(/^\d+\.\s+(.+)$/gm, "<li>$1</li>");

  // 处理代码块 ```code```
  escaped = escaped.replace(/```([\s\S]*?)```/g, "<pre><code>$1</code></pre>");

  // 处理行内代码 `code`
  escaped = escaped.replace(/`(.+?)`/g, "<code>$1</code>");

  // 处理换行
  escaped = escaped.replace(/\n/g, "<br>");

  // 清理多余的 <br>
  escaped = escaped.replace(/(<br>){3,}/g, "<br><br>");

  return escaped;
};

// 格式化思考步骤（Gemini 风格）
const formatThinkingStep = (step) => {
  if (!step) return "";

  // 检测是否是标题（以数字开头，如 "1. "、"2. "）
  const titleMatch = step.match(/^(\d+)\.\s*(.+?)[:：](.*)$/);
  if (titleMatch) {
    const [, number, title, content] = titleMatch;
    return `
      <div class="thinking-section">
        <div class="thinking-title">${title}</div>
        <div class="thinking-content">${content.trim()}</div>
      </div>
    `;
  }

  // 检测是否是子标题（以 "- " 或 "• " 开头）
  if (step.match(/^[-•]\s+/)) {
    const content = step.replace(/^[-•]\s+/, "");
    return `<div class="thinking-bullet">• ${content}</div>`;
  }

  // 检测是否是代码或工具调用（包含特殊字符）
  if (step.includes("searchWeb") || step.includes("(") || step.includes("{")) {
    return `<div class="thinking-code">${step}</div>`;
  }

  // 普通段落
  return `<div class="thinking-paragraph">${step}</div>`;
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
  background: #171717;
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

.sidebar-title {
  font-family: "Roboto Mono", monospace;
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
  justify-content: space-between;
}

.chat-history-item:hover {
  background: rgba(139, 92, 246, 0.1);
  border-color: rgba(139, 92, 246, 0.2);
  color: #a78bfa;
}

.chat-history-item.active {
  background: rgba(63, 63, 70, 0.6);
  border-color: rgba(82, 82, 91, 0.5);
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
  text-align: left;
}

/* 历史记录操作按钮 */
.history-actions {
  position: relative;
}

.history-more {
  opacity: 0.6;
  background: transparent;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.chat-history-item:hover .history-more {
  opacity: 1;
}

.history-more:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #e5e5e5;
}

/* 历史记录下拉菜单 */
.history-menu {
  position: absolute;
  right: 0;
  top: 100%;
  background: #27272a;
  border: 1px solid #3f3f46;
  border-radius: 8px;
  padding: 4px;
  min-width: 100px;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.history-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #e5e5e5;
  transition: background 0.2s;
}

.history-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.history-menu-item.delete {
  color: #ef4444;
}

.history-menu-item.delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal-content {
  background: #27272a;
  border-radius: 12px;
  padding: 20px;
  width: 90%;
  max-width: 360px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
}

.modal-header {
  font-size: 16px;
  font-weight: 600;
  color: #e5e5e5;
  margin-bottom: 16px;
}

.modal-body {
  margin-bottom: 20px;
}

.modal-body p {
  color: #a1a1aa;
  font-size: 14px;
  line-height: 1.5;
}

.rename-input {
  width: 100%;
  padding: 10px 12px;
  background: #18181b;
  border: 1px solid #3f3f46;
  border-radius: 8px;
  color: #e5e5e5;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.rename-input:focus {
  border-color: #7c3aed;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.modal-btn.cancel {
  background: #3f3f46;
  color: #e5e5e5;
}

.modal-btn.cancel:hover {
  background: #52525b;
}

.modal-btn.confirm {
  background: #ef4444;
  color: white;
}

.modal-btn.confirm:hover {
  background: #dc2626;
}

/* 右侧主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  height: 100vh;
  position: relative;
  background: #171717;
  overflow: hidden;
}

/* 顶部导航 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: #171717;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
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
  overflow-y: auto;
  padding: 24px 20px 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  box-sizing: border-box;
  scrollbar-width: thin;
  scrollbar-color: rgba(139, 92, 246, 0.3) transparent;
  min-height: 0;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  min-height: 500px;
}

/* 顶部 header */
.empty-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.empty-title {
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 25px;
  font-weight: 500;
  color: #e5e5e5;
  text-align: center;
}

.empty-desc {
  font-family: "Roboto Mono", monospace;
  font-size: 14px;
  color: #8e8e8e;
  text-align: center;
  margin-bottom: 0;
}

/* 没有消息时的输入框容器（独立画布） */
.empty-input-container {
  position: absolute;
  top: 53%;
  left: 50%;
  transform: translate(-50%, 0);
  width: calc(100% - 48px);
  max-width: 900px;
  padding: 0 24px;
  box-sizing: border-box;
}

/* 没有消息时的输入框样式 */
.empty-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 40px;
  padding: 14px 14px 14px 28px;
  border: 1px solid rgba(139, 92, 246, 0.15);
  width: 100%;
  transition: all 0.3s;
  box-sizing: border-box;
}

.empty-input-wrapper:focus-within {
  background: rgba(255, 255, 255, 0.12);
  box-shadow:
    0 0 0 1px rgba(139, 92, 246, 0.3),
    0 4px 20px rgba(139, 92, 246, 0.2),
    0 0 40px rgba(139, 92, 246, 0.1),
    inset 0 -2px 10px rgba(139, 92, 246, 0.05);
}

.empty-input-wrapper textarea {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #e5e5e5;
  font-size: 15px;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  resize: none;
  line-height: 1.5;
  caret-color: #8b5cf6; /* 明显的紫色光标 */
  cursor: text; /* 确保显示文本光标 */
  overflow-y: auto;
  min-height: 20px;
}

.empty-input-wrapper textarea:focus {
  caret-color: #a78bfa; /* 聚焦时更亮的光标 */
}

.empty-input-wrapper textarea::placeholder {
  color: #9ca3af;
}

.empty-input-wrapper .send-btn {
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
  flex-shrink: 0;
}

.empty-input-wrapper .send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.5);
}

.empty-input-wrapper .send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-input-wrapper .send-btn svg {
  width: 20px;
  height: 20px;
  position: relative;
  z-index: 1;
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

/* 消息气泡 - 居中简洁风格 */
.message {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 70%;
  width: 100%;
}

.message.user {
  align-items: flex-end;
}

.message.assistant {
  align-items: flex-start;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
  max-width: 100%;
  width: fit-content;
}

.message.assistant .message-content {
  background: transparent;
  color: #e0e7ff;
  padding-left: 0;
}

.message.user .message-content {
  background: #4f46e5;
  color: white;
}

.message-text {
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

/* DeepSeek 风格思考过程盒子 */
.deepseek-thinking-box {
  background: linear-gradient(
    135deg,
    rgba(99, 102, 241, 0.08) 0%,
    rgba(139, 92, 246, 0.08) 100%
  );
  border: 1.5px solid rgba(139, 92, 246, 0.25);
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.1);
  backdrop-filter: blur(10px);
}

.deepseek-thinking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  cursor: pointer;
  background: rgba(139, 92, 246, 0.12);
  transition: all 0.3s ease;
  border-bottom: 1px solid rgba(139, 92, 246, 0.15);
}

.deepseek-thinking-header:hover {
  background: rgba(139, 92, 246, 0.18);
}

.thinking-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.thinking-icon-animated {
  display: flex;
  align-items: center;
  color: #a78bfa;
  animation: sparkle 2s ease-in-out infinite;
}

.thinking-icon-animated.spinning {
  animation:
    spin 2s linear infinite,
    pulse 1.5s ease-in-out infinite;
}

@keyframes sparkle {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.thinking-label {
  font-family: "Roboto Mono", monospace;
  font-size: 13px;
  font-weight: 600;
  color: #c4b5fd;
  letter-spacing: 0.5px;
}

.thinking-time {
  font-family: "Roboto Mono", monospace;
  font-size: 11px;
  color: #9ca3af;
  background: rgba(139, 92, 246, 0.15);
  padding: 2px 8px;
  border-radius: 10px;
}

.thinking-chevron {
  color: #9ca3af;
  transition: transform 0.3s ease;
}

.thinking-chevron.expanded {
  transform: rotate(180deg);
}

.deepseek-thinking-content {
  padding: 16px;
  background: rgba(17, 24, 39, 0.4);
  max-height: 400px;
  overflow-y: auto;
}

.deepseek-thinking-content::-webkit-scrollbar {
  width: 6px;
}

.deepseek-thinking-content::-webkit-scrollbar-track {
  background: rgba(139, 92, 246, 0.05);
}

.deepseek-thinking-content::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.3);
  border-radius: 3px;
}

.deepseek-thinking-step {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
  animation: fadeInStep 0.4s ease-out;
}

.deepseek-thinking-step:last-child {
  border-bottom: none;
}

@keyframes fadeInStep {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.step-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  margin-top: 6px;
  flex-shrink: 0;
  box-shadow: 0 0 8px rgba(139, 92, 246, 0.5);
}

.step-indicator.pulsing {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
    box-shadow: 0 0 8px rgba(139, 92, 246, 0.5);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.3);
    box-shadow: 0 0 16px rgba(139, 92, 246, 0.8);
  }
}

.step-text {
  flex: 1;
  font-family: "Roboto Mono", monospace;
  font-size: 13px;
  color: #e5e7eb;
  line-height: 1.6;
  word-break: break-word;
}

.thinking-in-progress {
  opacity: 0.8;
}

.thinking-dots {
  display: flex;
  align-items: center;
  gap: 2px;
}

.dot-animation {
  display: inline-flex;
  margin-left: 2px;
}

.dot-animation span {
  animation: dotBlink 1.4s infinite;
  opacity: 0;
}

.dot-animation span:nth-child(1) {
  animation-delay: 0s;
}

.dot-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.dot-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dotBlink {
  0%,
  20% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
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
  flex-shrink: 0;
  padding: 16px 24px 24px;
  background: #171717;
  z-index: 100;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: transparent;
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
  line-height: 1.6;
  background: transparent;
  color: #ffffff;
  caret-color: #8b5cf6; /* 明显的紫色光标 */
  cursor: text; /* 确保显示文本光标 */
  overflow-y: auto;
  min-height: 20px;
}

.input-wrapper textarea:focus {
  caret-color: #a78bfa; /* 聚焦时更亮的光标 */
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

/* 滚动条 - 聊天消息区域右侧 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.4);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
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

/* ==================== DeepSeek 风格思考过程样式优化 ==================== */

/* 思考框容器 */
.deepseek-thinking-box {
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
  border: 1px solid #e0e7ff;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.08);
  transition: all 0.3s ease;
}

.deepseek-thinking-box:hover {
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.12);
  border-color: #c7d2fe;
}

/* 思考框头部 */
.deepseek-thinking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  cursor: pointer;
  user-select: none;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  transition: background 0.2s ease;
}

.deepseek-thinking-header:hover {
  background: rgba(255, 255, 255, 0.8);
}

.thinking-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 思考图标动画 */
.thinking-icon-animated {
  color: #6366f1;
  transition: transform 0.3s ease;
}

.thinking-icon-animated.spinning {
  animation: spin 2s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 思考标签 */
.thinking-label {
  font-weight: 600;
  font-size: 14px;
  color: #4f46e5;
  letter-spacing: 0.3px;
}

/* 思考时间标签 */
.thinking-time {
  font-size: 12px;
  color: #6b7280;
  background: rgba(255, 255, 255, 0.9);
  padding: 3px 10px;
  border-radius: 12px;
  font-weight: 500;
  border: 1px solid #e5e7eb;
}

/* 展开/收起箭头 */
.thinking-chevron {
  color: #9ca3af;
  transition:
    transform 0.3s ease,
    color 0.2s ease;
}

.thinking-chevron.expanded {
  transform: rotate(180deg);
  color: #6366f1;
}

/* 思考内容区域 */
.deepseek-thinking-content {
  padding: 16px 18px;
  background: rgba(255, 255, 255, 0.5);
  border-top: 1px solid #e0e7ff;
  max-height: 500px;
  overflow-y: auto;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 自定义滚动条 */
.deepseek-thinking-content::-webkit-scrollbar {
  width: 6px;
}

.deepseek-thinking-content::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.deepseek-thinking-content::-webkit-scrollbar-thumb {
  background: rgba(99, 102, 241, 0.3);
  border-radius: 3px;
}

.deepseek-thinking-content::-webkit-scrollbar-thumb:hover {
  background: rgba(99, 102, 241, 0.5);
}

/* ==================== Gemini 风格思考步骤样式 ==================== */

/* 思考步骤 - Gemini 风格 */
.thinking-step-gemini {
  margin-bottom: 16px;
  line-height: 1.6;
  color: #6b7280;
  font-size: 13px;
  font-style: italic;
  animation: fadeInUp 0.4s ease;
}

.thinking-step-gemini:last-child {
  margin-bottom: 0;
}

/* 思考章节（带标题） */
.thinking-section {
  margin-bottom: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  border-left: 3px solid #6366f1;
  transition: all 0.2s ease;
}

.thinking-section:hover {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.1);
}

.thinking-title {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  font-style: italic;
  margin-bottom: 8px;
  letter-spacing: 0.3px;
}

.thinking-content {
  font-size: 13px;
  color: #6b7280;
  font-style: italic;
  line-height: 1.7;
}

/* 思考段落 */
.thinking-paragraph {
  font-size: 13px;
  color: #6b7280;
  font-style: italic;
  line-height: 1.7;
  margin-bottom: 12px;
}

/* 思考列表项 */
.thinking-bullet {
  font-size: 13px;
  color: #6b7280;
  font-style: italic;
  line-height: 1.6;
  margin-left: 16px;
  margin-bottom: 8px;
  position: relative;
}

.thinking-bullet::before {
  content: "";
  position: absolute;
  left: -12px;
  top: 10px;
  width: 4px;
  height: 4px;
  background: #6366f1;
  border-radius: 50%;
}

/* 思考代码/工具调用 */
.thinking-code {
  font-family: "Roboto Mono", "Courier New", monospace;
  font-size: 13px;
  color: #059669;
  background: rgba(16, 185, 129, 0.1);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 8px 0;
  border-left: 2px solid #10b981;
  overflow-x: auto;
}

/* 加载动画 */
.thinking-loading {
  display: flex;
  align-items: center;
  padding: 12px 0;
  color: #6b7280;
  font-style: italic;
}

.thinking-loader {
  display: flex;
  gap: 6px;
  align-items: center;
}

.loader-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #6366f1;
  animation: loaderPulse 1.4s infinite ease-in-out both;
}

.loader-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loader-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loaderPulse {
  0%,
  80%,
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}

/* 思考步骤 */
.deepseek-thinking-step {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border-left: 3px solid #6366f1;
  animation: fadeInUp 0.4s ease;
  transition: all 0.2s ease;
}

.deepseek-thinking-step:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateX(2px);
  box-shadow: 0 2px 6px rgba(99, 102, 241, 0.1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.deepseek-thinking-step:last-child {
  margin-bottom: 0;
}

/* 步骤指示器 */
.step-indicator {
  flex-shrink: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  margin-top: 6px;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

/* 正在思考的步骤 */
.thinking-in-progress {
  border-left-color: #f59e0b;
  background: rgba(255, 251, 235, 0.7);
}

.thinking-in-progress .step-indicator {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.2);
}

.thinking-in-progress .step-indicator.pulsing {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.3);
    opacity: 0.7;
  }
}

/* 步骤文本 */
.step-text {
  flex: 1;
  font-size: 14px;
  line-height: 1.6;
  color: #9ca3af;
  word-break: break-word;
}

/* 思考中的点点点动画 */
.thinking-dots {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.dot-animation {
  display: inline-flex;
  gap: 2px;
  margin-left: 2px;
}

.dot-animation span {
  animation: dotBounce 1.4s infinite;
  opacity: 0;
}

.dot-animation span:nth-child(1) {
  animation-delay: 0s;
}

.dot-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.dot-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dotBounce {
  0%,
  60%,
  100% {
    opacity: 0;
    transform: translateY(0);
  }
  30% {
    opacity: 1;
    transform: translateY(-4px);
  }
}

/* 响应式优化 - 思考框 */
@media (max-width: 768px) {
  .deepseek-thinking-box {
    border-radius: 8px;
    margin-bottom: 12px;
  }

  .deepseek-thinking-header {
    padding: 12px 14px;
  }

  .deepseek-thinking-content {
    padding: 12px 14px;
  }

  .deepseek-thinking-step {
    padding: 8px;
    gap: 10px;
  }

  .step-text {
    font-size: 13px;
  }
}

/* 暗色模式支持（可选） */
@media (prefers-color-scheme: dark) {
  .deepseek-thinking-box {
    background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
    border-color: #334155;
  }

  .deepseek-thinking-header {
    background: rgba(30, 41, 59, 0.6);
  }

  .deepseek-thinking-header:hover {
    background: rgba(30, 41, 59, 0.8);
  }

  .thinking-label {
    color: #818cf8;
  }

  .thinking-time {
    background: rgba(30, 41, 59, 0.9);
    border-color: #475569;
  }

  .deepseek-thinking-content {
    background: rgba(15, 23, 42, 0.5);
    border-top-color: #334155;
  }

  .deepseek-thinking-step {
    background: rgba(30, 41, 59, 0.7);
    border-left-color: #818cf8;
  }

  .deepseek-thinking-step:hover {
    background: rgba(30, 41, 59, 0.9);
  }

  .step-text {
    color: #e2e8f0;
  }

  .thinking-in-progress {
    background: rgba(45, 35, 20, 0.7);
  }
}

/* ========== Gemini 内联思考样式（无框框） ========== */

/* 思考区域 - 只有左侧竖线，无框框 */
.gemini-thinking-box {
  margin-bottom: 16px;
  border-left: 2px solid;
  border-image: linear-gradient(to bottom, #9ca3af, #4b5563) 1;
  padding-left: 16px;
  background: transparent;
}

/* 思考头部 - 可点击展开/收起 */
.gemini-thinking-header {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 0;
  background: transparent;
  cursor: pointer;
  transition: opacity 0.2s ease;
  user-select: none;
  margin-bottom: 8px;
}

.gemini-thinking-header:hover {
  background: rgba(0, 0, 0, 0.05);
}

.thinking-header-left {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

/* 思考图标 */
.thinking-icon {
  color: #5f6368;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.thinking-icon.spinning {
  animation: gemini-spin 2s linear infinite;
}

@keyframes gemini-spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 思考文本 */
.thinking-text {
  font-size: 13px;
  color: #5f6368;
  font-weight: 500;
}

/* 三个点动画（内联显示） */
.thinking-dots-inline {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  margin-left: 4px;
}

.thinking-dots-inline .dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: #5f6368;
  animation: gemini-dotPulse 1.4s infinite;
}

.thinking-dots-inline .dot:nth-child(1) {
  animation-delay: 0s;
}

.thinking-dots-inline .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.thinking-dots-inline .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes gemini-dotPulse {
  0%,
  60%,
  100% {
    opacity: 0.3;
    transform: scale(1);
  }
  30% {
    opacity: 1;
    transform: scale(1.2);
  }
}

/* 思考时间 */
.thinking-time {
  font-size: 12px;
  color: #80868b;
  margin-left: 4px;
}

/* 展开/收起箭头 */
.thinking-chevron {
  color: #8b5cf6;
  flex-shrink: 0;
  transition: transform 0.3s ease;
  width: 14px;
  height: 14px;
}

.thinking-chevron.expanded {
  transform: rotate(180deg);
}

/* 思考内容区域（默认隐藏） */
.gemini-thinking-content {
  padding: 0;
  background: transparent;
  max-height: 400px;
  overflow-y: auto;
  animation: gemini-slideDown 0.2s ease;
}

/* 隐藏滚动条 */
.gemini-thinking-content::-webkit-scrollbar {
  display: none;
}

.gemini-thinking-content {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

@keyframes gemini-slideDown {
  from {
    opacity: 0;
    max-height: 0;
  }
  to {
    opacity: 1;
    max-height: 400px;
  }
}

/* 自定义滚动条 */
.gemini-thinking-content::-webkit-scrollbar {
  width: 6px;
}

.gemini-thinking-content::-webkit-scrollbar-track {
  background: transparent;
}

.gemini-thinking-content::-webkit-scrollbar-thumb {
  background: #dadce0;
  border-radius: 3px;
}

.gemini-thinking-content::-webkit-scrollbar-thumb:hover {
  background: #bdc1c6;
}

/* 思考步骤 - 使用等宽字体和斜体 */
.thinking-step {
  font-size: 14px;
  line-height: 1.8;
  color: #1f2937;
  margin-bottom: 10px;
  padding: 8px 0;
  word-break: break-word;
  animation: gemini-fadeIn 0.3s ease;
  font-family: "SF Mono", "Consolas", "Monaco", "Courier New", monospace;
  font-style: italic;
  font-weight: 400;
}

@keyframes gemini-fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.thinking-step:last-child {
  margin-bottom: 0;
}

/* 正在思考的加载提示 */
.thinking-loading {
  color: #80868b;
  font-style: italic;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .gemini-thinking-box {
    border-left-width: 3px;
  }

  .gemini-thinking-header {
    padding: 6px 10px;
  }

  .thinking-text {
    font-size: 12px;
  }

  .gemini-thinking-content {
    padding: 10px 12px;
  }

  .thinking-step {
    font-size: 13px;
  }
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .gemini-thinking-box {
    border-image: linear-gradient(to bottom, #6b7280, #374151) 1;
  }

  .gemini-thinking-header:hover {
    background: rgba(255, 255, 255, 0.05);
  }

  .thinking-icon,
  .thinking-text,
  .thinking-chevron {
    color: #9aa0a6;
  }

  .thinking-dots-inline .dot {
    background-color: #9aa0a6;
  }

  .thinking-time {
    color: #80868b;
  }

  .gemini-thinking-content {
    background: transparent;
  }

  .thinking-step {
    color: #9ca3af;
  }

  .thinking-loading {
    color: #9aa0a6;
  }

  .gemini-thinking-content::-webkit-scrollbar-thumb {
    background: #5f6368;
  }

  .gemini-thinking-content::-webkit-scrollbar-thumb:hover {
    background: #80868b;
  }
}
</style>
