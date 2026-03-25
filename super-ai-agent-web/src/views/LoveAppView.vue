<template>
  <div class="love-app-root">
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
            <div class="history-actions">
              <button
                class="history-more"
                @click.stop="toggleSessionMenu(session.id)"
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
              <div v-if="activeSessionMenu === session.id" class="history-menu">
                <div
                  class="history-menu-item"
                  @click.stop="renameHistory(session)"
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
                  @click.stop="confirmDeleteHistory(session.id)"
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
          <div class="chat-messages" ref="messagesContainer" @scroll="handleUserScroll">
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

              <!-- 功能说明 -->
              <div class="feature-tips">
                <div class="tip-item">
                  <span class="tip-icon">💬</span>
                  <span class="tip-text">选择对话模式，开始咨询</span>
                </div>
                <div class="tip-item">
                  <span class="tip-icon">📊</span>
                  <span class="tip-text"
                    >勾选"恋爱报告"，对话后点击按钮生成报告</span
                  >
                </div>
                <div class="tip-item">
                  <span class="tip-icon">🔧</span>
                  <span class="tip-text">勾选"工具调用"，获取实时信息</span>
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

                  <!-- 报告操作按钮 -->
                  <div
                    v-if="msg.type === 'report' && msg.reportData"
                    class="report-actions"
                  >
                    <button
                      class="report-action-btn download-btn"
                      @click="downloadReport(msg.reportData)"
                      title="下载报告"
                    >
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
                          d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5M16.5 12 12 16.5m0 0L7.5 12m4.5 4.5V3"
                        />
                      </svg>
                      下载报告
                    </button>
                    <button
                      class="report-action-btn copy-btn"
                      @click="copyReport(msg.reportData)"
                      title="复制报告"
                    >
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
                          d="M15.666 3.888A2.25 2.25 0 0 0 13.5 2.25h-3c-1.03 0-1.9.693-2.166 1.638m7.332 0c.055.194.084.4.084.612v0a.75.75 0 0 1-.75.75H9a.75.75 0 0 1-.75-.75v0c0-.212.03-.418.084-.612m7.332 0c.646.049 1.288.11 1.927.184 1.1.128 1.907 1.077 1.907 2.185V19.5a2.25 2.25 0 0 1-2.25 2.25H6.75A2.25 2.25 0 0 1 4.5 19.5V6.257c0-1.108.806-2.057 1.907-2.185a48.208 48.208 0 0 1 1.927-.184"
                        />
                      </svg>
                      复制报告
                    </button>
                  </div>
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

          <!-- 生成报告按钮区域 -->
          <div v-if="showGenerateReportButton" class="generate-report-section">
            <button
              class="generate-report-btn"
              @click="handleGenerateReport"
              :disabled="isGeneratingReport"
            >
              <svg
                v-if="!isGeneratingReport"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z"
                />
              </svg>
              <svg
                v-else
                class="spinning"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"
                />
              </svg>
              {{ isGeneratingReport ? "生成中..." : "生成报告" }}
            </button>
            <p class="generate-report-tip">点击生成结构化的恋爱分析报告</p>
          </div>
        </div>

        <!-- 输入区域 -->
        <footer class="chat-input-area">
          <div
            class="input-wrapper"
            :style="inputAreaStyle"
            @click="handleInputAreaClick"
          >
            <!-- 新的矩形输入框 -->
            <div class="modern-input-container">
              <!-- 文本输入区域 -->
              <div class="input-section">
                <textarea
                  ref="inputRef"
                  v-model="inputMessage"
                  placeholder="给恋爱大师发消息"
                  @keydown.enter.exact.prevent="sendMessage"
                  @input="autoResize"
                  rows="1"
                  class="modern-input"
                  :disabled="isLoading"
                ></textarea>

                <!-- 右侧发送按钮 -->
                <button
                  class="send-btn-modern"
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

              <!-- 模式选择区域 -->
              <div class="mode-selection">
                <div class="mode-buttons">
                  <!-- 对话模式下拉选择器 -->
                  <div
                    class="mode-dropdown"
                    :class="{
                      active: ['basic', 'smart', 'rag'].includes(
                        selectedChatMode,
                      ),
                    }"
                    @click.stop
                  >
                    <button
                      class="mode-btn dropdown-trigger"
                      @click.stop="toggleChatModeDropdown"
                      :disabled="isLoading"
                    >
                      <!-- 根据当前选择显示不同图标 -->
                      <svg
                        v-if="selectedChatMode === 'basic'"
                        class="mode-icon"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="1.5"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M8.625 12a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H8.25m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H12m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 0 1-2.555-.337A5.972 5.972 0 0 1 5.41 20.97a5.969 5.969 0 0 1-.474-.065 4.48 4.48 0 0 0 .978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25Z"
                        />
                      </svg>
                      <svg
                        v-else-if="selectedChatMode === 'smart'"
                        class="mode-icon"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="1.5"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.847a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.847.813a4.5 4.5 0 0 0-3.09 3.09ZM18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 0 0-2.456 2.456Z"
                        />
                      </svg>
                      <svg
                        v-else-if="selectedChatMode === 'rag'"
                        class="mode-icon"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="1.5"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M12 6.042A8.967 8.967 0 0 0 6 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 0 1 6 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 0 1 6-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0 0 18 18a8.967 8.967 0 0 0-6 2.292m0-14.25v14.25"
                        />
                      </svg>
                      <span>{{ getChatModeName() }}</span>
                      <!-- 上拉箭头 -->
                      <svg
                        class="dropdown-arrow"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="2"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="m4.5 15.75 7.5-7.5 7.5 7.5"
                        />
                      </svg>
                    </button>

                    <!-- 下拉菜单 -->
                    <div v-if="showChatModeDropdown" class="dropdown-menu">
                      <div
                        v-for="mode in chatModeOptions"
                        :key="mode.id"
                        :class="[
                          'dropdown-item',
                          { active: selectedChatMode === mode.id },
                        ]"
                        @click.stop="selectChatMode(mode.id)"
                      >
                        <svg
                          v-if="mode.icon === 'ChatIcon'"
                          class="mode-icon"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke-width="1.5"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            d="M8.625 12a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H8.25m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H12m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 0 1-2.555-.337A5.972 5.972 0 0 1 5.41 20.97a5.969 5.969 0 0 1-.474-.065 4.48 4.48 0 0 0 .978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25Z"
                          />
                        </svg>
                        <svg
                          v-else-if="mode.icon === 'SparklesIcon'"
                          class="mode-icon"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke-width="1.5"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.847a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.847.813a4.5 4.5 0 0 0-3.09 3.09ZM18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 0 0-2.456 2.456Z"
                          />
                        </svg>
                        <svg
                          v-else-if="mode.icon === 'BookOpenIcon'"
                          class="mode-icon"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke-width="1.5"
                          stroke="currentColor"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            d="M12 6.042A8.967 8.967 0 0 0 6 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 0 1 6 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 0 1 6-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0 0 18 18a8.967 8.967 0 0 0-6 2.292m0-14.25v14.25"
                          />
                        </svg>
                        <div class="dropdown-item-content">
                          <span class="dropdown-item-name">{{
                            mode.name
                          }}</span>
                          <span class="dropdown-item-desc">{{
                            mode.description
                          }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 功能增强按钮（多选） -->
                  <button
                    v-for="mode in enhancementOptions"
                    :key="mode.id"
                    :class="[
                      'mode-btn',
                      { active: isEnhancementSelected(mode.id) },
                    ]"
                    @click="toggleEnhancement(mode.id)"
                    :disabled="isLoading"
                  >
                    <!-- 文档图标 -->
                    <svg
                      v-if="mode.icon === 'DocumentTextIcon'"
                      class="mode-icon"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke-width="1.5"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z"
                      />
                    </svg>

                    <!-- 工具图标 -->
                    <svg
                      v-else-if="mode.icon === 'WrenchScrewdriverIcon'"
                      class="mode-icon"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke-width="1.5"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M11.42 15.17L17.25 21A2.652 2.652 0 0 0 21 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 1 1-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 0 0 4.486-6.336l-3.276 3.277a3.004 3.004 0 0 1-2.25-2.25l3.276-3.276a4.5 4.5 0 0 0-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008Z"
                      />
                    </svg>

                    <span>{{ mode.name }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </footer>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from "vue";
import {
  doChatWithLoveAppSse,
  doChatWithLoveAppReport,
  doChatWithLoveAppTools,
  doChatWithLoveAppRag,
  doChatWithLoveAppSmart,
  generateUUID,
  getConversations,
  getConversationHistory,
  deleteConversation,
} from "../api/chat";

const chatId = ref("");
const messages = ref([]);
const inputMessage = ref("");
const isLoading = ref(false);
const messagesContainer = ref(null);
const inputRef = ref(null);

// 删除和重命名相关状态
const showDeleteModal = ref(false);
const showRenameModal = ref(false);
const deleteTargetId = ref(null);
const renameTarget = ref(null);
const renameValue = ref("");
const activeSessionMenu = ref(null);

// 聊天模式相关状态
const selectedChatMode = ref("smart"); // 对话模式（单选）
const selectedEnhancements = ref([]); // 功能增强（多选）
const showChatModeDropdown = ref(false);

// 报告生成相关状态
const showGenerateReportButton = computed(() => {
  // 当选择了恋爱报告功能，且有对话消息时，显示生成按钮
  return (
    selectedEnhancements.value.includes("report") &&
    messages.value.length >= 2 && // 至少有一轮对话
    !isLoading.value
  ); // 不在加载中
});
const isGeneratingReport = ref(false);
const lastUserMessage = ref(""); // 保存最后一条用户消息

// 对话模式选项（单选，必选其一）
const chatModeOptions = ref([
  {
    id: "basic",
    name: "基础对话",
    icon: "ChatIcon",
    description: "普通AI对话",
  },
  {
    id: "smart",
    name: "智能模式",
    icon: "SparklesIcon",
    description: "自动RAG回退，推荐",
  },
  {
    id: "rag",
    name: "RAG问答",
    icon: "BookOpenIcon",
    description: "基于知识库回答",
  },
]);

// 功能增强选项（多选，可选）
const enhancementOptions = ref([
  {
    id: "report",
    name: "恋爱报告",
    icon: "DocumentTextIcon",
    description: "生成结构化恋爱报告",
  },
  {
    id: "tools",
    name: "工具调用",
    icon: "WrenchScrewdriverIcon",
    description: "调用外部工具",
  },
]);

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

// 聚焦输入框
const focusInput = () => {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.focus();
    }
  });
};

// 自动调整输入框高度
const autoResize = () => {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.style.height = 'auto';
      inputRef.value.style.height = Math.min(inputRef.value.scrollHeight, 200) + 'px';
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
  return {
    margin: "0 auto",
    width: "calc(100% - 48px)",
    maxWidth: "900px",
  };
});

const sessions = ref([]);

// 加载会话列表（恋爱大师）
const loadSessions = async () => {
  try {
    const data = await getConversations("love");
    // 按会话 ID 分组
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
    const sessionsList = await Promise.all(Object.values(groupedData).map(async (conversation) => {
      let title = conversation.lastMessage || "新会话";
      let lastMessageTime = conversation.updatedAt || 0;

      // 如果最后一条消息是 JSON 格式的报告，需要获取倒数第二条消息
      if (title.trim().startsWith('{')) {
        try {
          const parsed = JSON.parse(title);
          // 如果是报告格式（包含 title 和 suggestions），需要获取更早的消息
          if (parsed.title && parsed.suggestions) {
            // 获取完整的对话历史，找到最后一条非报告消息
            const history = await getConversationHistory(conversation.conversationId);
            // 从后往前找，找到第一条非 JSON 报告的消息
            for (let i = history.length - 1; i >= 0; i--) {
              const msg = history[i];
              if (msg.role === 'assistant' && msg.content && msg.content.trim().startsWith('{')) {
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
              } else if (msg.role === 'user') {
                // 是用户消息，使用这条
                title = msg.content.substring(0, 30);
                lastMessageTime = msg.timestamp || lastMessageTime;
                break;
              } else if (msg.content && !msg.content.trim().startsWith('{')) {
                // 是普通 AI 回复
                title = msg.content.substring(0, 30);
                lastMessageTime = msg.timestamp || lastMessageTime;
                break;
              }
            }
            // 如果所有消息都是报告，显示为"新会话"
            if (title.trim().startsWith('{')) {
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
      };
    }));

    // 按最后消息时间排序
    sessions.value = sessionsList.sort((a, b) => b.lastMessageTime - a.lastMessageTime);
  } catch (error) {
    console.error("加载会话列表失败:", error);
    sessions.value = [];
  }
};

// 加载指定会话的历史消息
const loadSessionHistory = async (sessionId) => {
  try {
    const history = await getConversationHistory(sessionId);
    messages.value = history.map((msg) => {
      // 尝试解析报告格式的消息
      if (msg.role === "assistant" && msg.content) {
        try {
          // 检查是否是 JSON 格式的报告（排除已经格式化过的内容）
          // 如果内容已经包含📊 emoji，说明已经是格式化过的报告，不再重复解析
          if (msg.content.trim().startsWith('{') && !msg.content.includes('📊')) {
            const parsed = JSON.parse(msg.content);
            if (parsed.title && parsed.suggestions) {
              // 格式化报告内容
              let reportContent = `\n📊 ${parsed.title}\n\n`;
              reportContent += "💡 建议：\n\n";
              parsed.suggestions.forEach((suggestion, index) => {
                reportContent += `${index + 1}. ${suggestion}\n\n`;
              });

              return {
                role: msg.role,
                content: reportContent,
                type: "report",
                reportData: parsed,
              };
            }
          }
        } catch (e) {
          // 不是 JSON 格式，使用原始内容
        }
      }

      return {
        role: msg.role,
        content: msg.content,
      };
    });
    displayedContent.value = {};
    isTyping.value = {};
    userHasScrolled.value = false; // 加载历史时重置用户滚动状态
    await nextTick();
    scrollToBottom();
  } catch (error) {
    console.error("加载会话历史失败:", error);
    messages.value = [];
  }
};

const displayedContent = ref({});
const isTyping = ref({});

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

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

const startNewChat = () => {
  if (messages.value.length > 0 && chatId.value) {
    const firstUserMsg = messages.value.find((msg) => msg.role === "user");
    const title = firstUserMsg
      ? firstUserMsg.content.substring(0, 30)
      : "新会话";

    const existingIndex = sessions.value.findIndex(
      (s) => s.id === chatId.value,
    );
    if (existingIndex === -1) {
      sessions.value.unshift({
        id: chatId.value,
        title: title,
      });
    } else {
      sessions.value[existingIndex].title = title;
    }
  }

  chatId.value = generateUUID("love");
  messages.value = [];
  displayedContent.value = {};
  isTyping.value = {};
  userHasScrolled.value = false; // 重置用户滚动状态
  focusInput();
};

// 发送消息后更新当前对话在会话列表中的显示
const updateCurrentSessionInHistory = () => {
  if (messages.value.length > 0 && chatId.value) {
    const firstUserMsg = messages.value.find((msg) => msg.role === "user");
    const title = firstUserMsg
      ? firstUserMsg.content.substring(0, 30)
      : "新会话";

    const existingIndex = sessions.value.findIndex(
      (s) => s.id === chatId.value,
    );
    if (existingIndex === -1) {
      sessions.value.unshift({
        id: chatId.value,
        title: title,
      });
    } else {
      const item = sessions.value.splice(existingIndex, 1)[0];
      item.title = title;
      sessions.value.unshift(item);
    }
  }
};

const switchSession = (sessionId) => {
  chatId.value = sessionId;
  loadSessionHistory(sessionId);
  focusInput();
};

// 切换会话菜单显示
const toggleSessionMenu = (id) => {
  activeSessionMenu.value = activeSessionMenu.value === id ? null : id;
};

// 确认删除历史会话（显示弹窗）
const confirmDeleteHistory = (id) => {
  deleteTargetId.value = id;
  showDeleteModal.value = true;
  activeSessionMenu.value = null;
};

// 删除历史会话（弹窗确认后）
const deleteHistory = async () => {
  if (deleteTargetId.value) {
    try {
      await deleteConversation(deleteTargetId.value);
    } catch (error) {
      console.error("删除会话失败:", error);
    }
    const index = sessions.value.findIndex(
      (s) => s.id === deleteTargetId.value,
    );
    if (index > -1) {
      sessions.value.splice(index, 1);
    }
  }
  showDeleteModal.value = false;
  deleteTargetId.value = null;
};

// 重命名历史会话（显示弹窗）
const renameHistory = (session) => {
  renameTarget.value = session;
  renameValue.value = session.title;
  showRenameModal.value = true;
  activeSessionMenu.value = null;
};

// 提交重命名
const submitRename = () => {
  if (renameTarget.value && renameValue.value.trim()) {
    const index = sessions.value.findIndex(
      (s) => s.id === renameTarget.value.id,
    );
    if (index > -1) {
      sessions.value[index].title = renameValue.value.trim();
    }
  }
  showRenameModal.value = false;
  renameTarget.value = null;
  renameValue.value = "";
};

onMounted(async () => {
  chatId.value = generateUUID("love");
  await loadSessions();
  focusInput();
});

const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    // 如果强制滚动，或者用户没有滚动过，或者用户已经在底部，才自动滚动
    if (!userHasScrolled.value || checkIsUserNearBottom()) {
      messagesContainer.value.scrollTo({
        top: messagesContainer.value.scrollHeight,
        behavior: "smooth",
      });
    }
  }
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

const getDisplayContent = (msg, index) => {
  if (msg.role === "assistant" && displayedContent.value[index] !== undefined) {
    return displayedContent.value[index];
  }
  return msg.content;
};

const getIsTyping = (index) => {
  return isTyping.value[index] === true;
};

// 选择对话模式（单选）
const selectChatMode = (modeId) => {
  console.log("Select chat mode:", modeId);
  selectedChatMode.value = modeId;
  showChatModeDropdown.value = false;
};

// 切换功能增强（多选）
const toggleEnhancement = (enhancementId) => {
  const index = selectedEnhancements.value.indexOf(enhancementId);
  if (index > -1) {
    selectedEnhancements.value.splice(index, 1);
  } else {
    selectedEnhancements.value.push(enhancementId);
  }
  console.log("Selected enhancements:", selectedEnhancements.value);
};

// 检查功能增强是否被选中
const isEnhancementSelected = (enhancementId) => {
  return selectedEnhancements.value.includes(enhancementId);
};

// 切换对话模式下拉菜单
const toggleChatModeDropdown = () => {
  console.log(
    "Toggle dropdown clicked, current state:",
    showChatModeDropdown.value,
  );
  showChatModeDropdown.value = !showChatModeDropdown.value;
  console.log("New state:", showChatModeDropdown.value);
};

// 获取当前对话模式名称
const getChatModeName = () => {
  const mode = chatModeOptions.value.find(
    (m) => m.id === selectedChatMode.value,
  );
  return mode ? mode.name : "智能模式";
};

const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) return;

  messages.value.push({ role: "user", content: message });
  lastUserMessage.value = message; // 保存用户消息
  inputMessage.value = "";
  isLoading.value = true;
  await scrollToBottom();

  try {
    // 根据对话模式选择基础API
    switch (selectedChatMode.value) {
      case "basic":
        await sendBasicMessage(message);
        break;
      case "smart":
        await sendSmartMessage(message);
        break;
      case "rag":
        await sendRagMessage(message);
        break;
      default:
        await sendSmartMessage(message);
    }

    // 如果选择了功能增强,追加处理
    // 注意：恋爱报告改为手动触发，不自动生成
    if (selectedEnhancements.value.includes("tools")) {
      console.log("启用工具调用");
      // 工具调用已在对话API中自动处理，无需额外操作
    }
  } catch (error) {
    console.error("发送消息失败:", error);
    messages.value.push({
      role: "assistant",
      content: "抱歉，发生了错误，请稍后重试。",
    });
  } finally {
    isLoading.value = false;
    await scrollToBottom();
    focusInput();
    updateCurrentSessionInHistory();
  }
};

// 基础对话模式
const sendBasicMessage = async (message) => {
  messages.value.push({ role: "assistant", content: "" });
  const aiIndex = messages.value.length - 1;
  displayedContent.value[aiIndex] = "";
  isTyping.value[aiIndex] = true;

  const stream = await doChatWithLoveAppSse(message, chatId.value);
  await processStreamResponse(stream, aiIndex);
};

// 智能模式（推荐）
const sendSmartMessage = async (message) => {
  messages.value.push({ role: "assistant", content: "" });
  const aiIndex = messages.value.length - 1;
  displayedContent.value[aiIndex] = "";
  isTyping.value[aiIndex] = true;

  const stream = await doChatWithLoveAppSmart(message, chatId.value);
  await processStreamResponse(stream, aiIndex);
};

// RAG问答模式
const sendRagMessage = async (message) => {
  // 添加 AI 消息占位
  messages.value.push({ role: "assistant", content: "" });
  const aiIndex = messages.value.length - 1;
  displayedContent.value[aiIndex] = "";
  isTyping.value[aiIndex] = true;

  try {
    const stream = await doChatWithLoveAppRag(message, chatId.value);
    await processStreamResponse(stream, aiIndex);
  } catch (error) {
    console.error("RAG查询失败:", error);
    messages.value[aiIndex].content = "抱歉，知识库查询失败，请稍后重试。";
    isTyping.value[aiIndex] = false;
  }
};

// 恋爱报告模式
const sendReportMessage = async (message) => {
  messages.value.push({
    role: "assistant",
    content: "正在生成恋爱报告，请稍候...",
  });
  const aiIndex = messages.value.length - 1;

  try {
    const result = await doChatWithLoveAppReport(message, chatId.value);

    let reportContent = result;
    try {
      const report = JSON.parse(result);
      if (report.title && report.suggestions) {
        reportContent = `📊 ${report.title}\n\n`;
        report.suggestions.forEach((suggestion, index) => {
          reportContent += `${index + 1}. ${suggestion}\n`;
        });
      }
    } catch (e) {
      console.log("报告解析失败，使用原始内容");
    }

    messages.value[aiIndex].content = reportContent;
  } catch (error) {
    console.error("恋爱报告生成失败:", error);
    messages.value[aiIndex].content = "抱歉，恋爱报告生成失败，请稍后重试。";
  }
};

// 对话后生成恋爱报告
const generateReportAfterChat = async (message) => {
  messages.value.push({
    role: "assistant",
    content: "📊 正在为您生成恋爱分析报告...",
    type: "report", // 标记为报告类型
    reportData: null, // 存储原始报告数据
  });
  const aiIndex = messages.value.length - 1;
  await scrollToBottom();

  try {
    const result = await doChatWithLoveAppReport(message, chatId.value);
    const report = JSON.parse(result);

    let reportContent = `\n📊 ${report.title}\n\n`;
    reportContent += "💡 建议：\n\n";
    report.suggestions.forEach((suggestion, index) => {
      reportContent += `${index + 1}. ${suggestion}\n\n`;
    });

    messages.value[aiIndex].content = reportContent;
    messages.value[aiIndex].reportData = report; // 保存原始报告数据供下载使用
  } catch (error) {
    console.error("恋爱报告生成失败:", error);
    messages.value[aiIndex].content = "抱歉，恋爱报告生成失败，请稍后重试。";
    messages.value[aiIndex].type = "error";
  }

  await scrollToBottom();
};

// 手动触发生成报告
const handleGenerateReport = async () => {
  if (isGeneratingReport.value) return;

  isGeneratingReport.value = true;

  try {
    // 使用最后一条用户消息或整个对话历史
    const message = lastUserMessage.value || "请根据我们的对话生成恋爱报告";
    await generateReportAfterChat(message);
  } finally {
    isGeneratingReport.value = false;
  }
};

// 工具调用模式
const sendToolsMessage = async (message) => {
  messages.value.push({ role: "assistant", content: "正在调用工具..." });
  const aiIndex = messages.value.length - 1;

  try {
    const result = await doChatWithLoveAppTools(message, chatId.value);
    messages.value[aiIndex].content = result;
  } catch (error) {
    console.error("工具调用失败:", error);
    messages.value[aiIndex].content = "抱歉，工具调用失败，请稍后重试。";
  }
};

// 处理流式响应
const processStreamResponse = async (stream, aiIndex) => {
  const reader = stream.getReader();
  const decoder = new TextDecoder();

  let fullContent = "";
  let buffer = "";

  while (true) {
    const { done, value } = await reader.read();

    if (done) {
      if (buffer.length > 0) {
        const lines = buffer.split("\n");
        for (const line of lines) {
          if (line.startsWith("data:")) {
            const content = line.slice(5).trim();
            if (content && !content.startsWith("FINAL:")) {
              fullContent += content;
            }
          }
        }
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
          if (content.startsWith("FINAL:")) {
            fullContent = content.slice(6).trim();
          } else {
            fullContent += content;
          }
          displayedContent.value[aiIndex] = fullContent;
          await scrollToBottom();
        }
      }
    }
  }

  isTyping.value[aiIndex] = false;
  messages.value[aiIndex].content = fullContent;
};

const formatMessage = (content) => {
  if (!content) return "";

  // 先替换换行符为 <br>
  let formatted = content.replace(/\n/g, "<br>");

  // 识别数字列表格式（如 "1. xxx" "2. xxx" "3. xxx"）
  // 在每个数字列表项前添加空行
  formatted = formatted.replace(/(\d+\.\s+)/g, '<br><br>$1');

  // 清理开头多余的 <br>
  formatted = formatted.replace(/^(<br>)+/, '');

  // 清理连续超过2个的 <br>
  formatted = formatted.replace(/(<br>){3,}/g, '<br><br>');

  return formatted;
};

// 下载恋爱报告
const downloadReport = (reportData) => {
  if (!reportData || !reportData.title || !reportData.suggestions) {
    console.error("报告数据不完整");
    return;
  }

  // 生成报告文本内容
  let reportText = `${reportData.title}\n`;
  reportText += `生成时间：${new Date().toLocaleString("zh-CN")}\n`;
  reportText += `${"=".repeat(50)}\n\n`;
  reportText += `💡 建议：\n\n`;

  reportData.suggestions.forEach((suggestion, index) => {
    reportText += `${index + 1}. ${suggestion}\n\n`;
  });

  reportText += `\n${"=".repeat(50)}\n`;
  reportText += `本报告由 AI 恋爱大师生成\n`;
  reportText += `仅供参考，请结合实际情况灵活运用\n`;

  // 创建 Blob 对象
  const blob = new Blob([reportText], { type: "text/plain;charset=utf-8" });

  // 创建下载链接
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;

  // 生成文件名（使用报告标题和时间戳）
  const timestamp = new Date().getTime();
  const filename = `恋爱报告_${timestamp}.txt`;
  link.download = filename;

  // 触发下载
  document.body.appendChild(link);
  link.click();

  // 清理
  document.body.removeChild(link);
  URL.revokeObjectURL(url);

  console.log("报告下载成功:", filename);
};

// 复制报告到剪贴板
const copyReport = (reportData) => {
  if (!reportData || !reportData.title || !reportData.suggestions) {
    console.error("报告数据不完整");
    return;
  }

  let reportText = `${reportData.title}\n\n`;
  reportText += `💡 建议：\n\n`;

  reportData.suggestions.forEach((suggestion, index) => {
    reportText += `${index + 1}. ${suggestion}\n\n`;
  });

  // 复制到剪贴板
  navigator.clipboard
    .writeText(reportText)
    .then(() => {
      console.log("报告已复制到剪贴板");
      // 可以添加一个提示消息
      alert("报告已复制到剪贴板！");
    })
    .catch((err) => {
      console.error("复制失败:", err);
      alert("复制失败，请手动复制");
    });
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
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  margin-bottom: 4px;
  color: #4e5969;
  background: transparent;
  position: relative;
}

.session-item:hover {
  background: #e8f3ff;
  color: #165dff;
}

.session-item.active {
  background: #e5e6eb;
  color: #1d2129;
}

.session-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.history-actions {
  position: relative;
}

.history-more {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  opacity: 0;
  transition: all 0.2s;
}

.session-item:hover .history-more {
  opacity: 1;
}

.history-more:hover {
  background: rgba(0, 0, 0, 0.1);
}

.history-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 100;
  min-width: 120px;
  overflow: hidden;
}

.history-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background 0.2s;
}

.history-menu-item:hover {
  background: #f5f5f5;
}

.history-menu-item.delete {
  color: #e74c3c;
}

.history-menu-item.delete:hover {
  background: #fef2f2;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: relative;
  z-index: 5;
  overflow: hidden;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  position: relative;
  overflow: hidden;
  min-height: 0;
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
  overflow-y: auto;
  padding: 24px 24px 100px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  scrollbar-width: thin;
  scrollbar-color: rgba(236, 72, 153, 0.4) transparent;
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
  margin: 0 0 24px 0;
}

/* 功能提示 */
.feature-tips {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(139, 92, 246, 0.05);
  border-radius: 12px;
  max-width: 400px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #4b5563;
}

.tip-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.tip-text {
  text-align: left;
  line-height: 1.5;
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

/* 报告操作按钮 */
.report-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.report-action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.report-action-btn svg {
  width: 16px;
  height: 16px;
}

.report-action-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.report-action-btn:active {
  transform: translateY(0);
}

.download-btn:hover {
  background: linear-gradient(
    135deg,
    rgba(236, 72, 153, 0.1) 0%,
    rgba(168, 85, 247, 0.1) 100%
  );
  border-color: #ec4899;
  color: #ec4899;
}

.copy-btn:hover {
  background: linear-gradient(
    135deg,
    rgba(139, 92, 246, 0.1) 0%,
    rgba(99, 102, 241, 0.1) 100%
  );
  border-color: #8b5cf6;
  color: #8b5cf6;
}

/* 生成报告按钮区域 */
.generate-report-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding: 12px 16px;
  margin: 12px 0;
  max-width: 100%;
  background: transparent;
  border-radius: 0;
  border: none;
}

.generate-report-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid rgba(236, 72, 153, 0.3);
  background: rgba(236, 72, 153, 0.05);
  color: #ec4899;
  font-size: 13px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: none;
}

.generate-report-btn svg {
  width: 14px;
  height: 14px;
}

.generate-report-btn:hover:not(:disabled) {
  background: rgba(236, 72, 153, 0.1);
  border-color: rgba(236, 72, 153, 0.5);
  transform: none;
  box-shadow: none;
}

.generate-report-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.generate-report-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.generate-report-btn .spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.generate-report-tip {
  font-size: 11px;
  color: #9ca3af;
  margin: 0;
  text-align: left;
}

/* 输入区域 */
.chat-input-area {
  flex-shrink: 0;
  padding: 16px 24px 28px;
  background: #faf9f6;
  z-index: 100;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: transparent;
  max-width: 800px;
  width: 100%;
  box-sizing: border-box;
  position: relative;
}

/* 新的现代输入框容器 */
.modern-input-container {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  overflow: visible;
}

.modern-input-container:focus-within {
  border-color: #8b5cf6;
  box-shadow: 0 2px 12px rgba(139, 92, 246, 0.15);
}

/* 输入区域 */
.input-section {
  display: flex;
  align-items: flex-end;
  padding: 16px;
  gap: 12px;
}

.love-app-root .modern-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  resize: none;
  line-height: 1.6;
  background: transparent;
  color: #111827;
  font-family: inherit;
  overflow-y: auto;
  min-height: 24px;
}

.modern-input::placeholder {
  color: #9ca3af;
}

.send-btn-modern {
  width: 36px;
  height: 36px;
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
  box-shadow: 0 2px 8px rgba(236, 72, 153, 0.3);
}

.send-btn-modern:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(236, 72, 153, 0.5);
}

.send-btn-modern:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.send-btn-modern svg {
  width: 18px;
  height: 18px;
}

/* 模式选择区域 */
.mode-selection {
  border-top: 1px solid #f3f4f6;
  padding: 12px 16px;
  background: #fafafa;
  position: relative;
  z-index: 200;
}

.mode-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.mode-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 20px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.mode-btn:hover:not(:disabled):not(.active) {
  background: #f3f4f6;
  border-color: #d1d5db;
  color: #374151;
}

.mode-btn.active {
  background: linear-gradient(135deg, #ec4899 0%, #a855f7 100%);
  border-color: #ec4899;
  color: white;
  box-shadow: 0 2px 8px rgba(236, 72, 153, 0.3);
}

.mode-btn.active:hover:not(:disabled) {
  background: linear-gradient(135deg, #f472b6 0%, #c084fc 100%);
  box-shadow: 0 4px 12px rgba(236, 72, 153, 0.4);
  transform: translateY(-1px);
}

.mode-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.mode-icon {
  width: 16px;
  height: 16px;
}

/* 下拉菜单样式 */
.mode-dropdown {
  position: relative;
  z-index: 300;
}

.dropdown-trigger {
  position: relative;
  padding-right: 32px;
}

.dropdown-arrow {
  width: 14px;
  height: 14px;
  position: absolute;
  right: 10px;
  transition: transform 0.2s ease;
}

.mode-dropdown.active .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 0;
  min-width: 180px;
  max-width: 200px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 10000;
  overflow: hidden;
  animation: dropdownFadeIn 0.2s ease;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  cursor: pointer;
  transition: background 0.2s ease;
  border-bottom: 1px solid #f3f4f6;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: #f9fafb;
}

.dropdown-item.active {
  background: linear-gradient(
    135deg,
    rgba(236, 72, 153, 0.1) 0%,
    rgba(168, 85, 247, 0.1) 100%
  );
}

.dropdown-item.active .mode-icon {
  color: #ec4899;
}

.dropdown-item .mode-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.dropdown-item-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  min-width: 0;
}

.dropdown-item-name {
  font-size: 13px;
  font-weight: 500;
  color: #1f2937;
  white-space: nowrap;
}

.dropdown-item-desc {
  font-size: 11px;
  color: #9ca3af;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-item.active .dropdown-item-name {
  color: #ec4899;
}

/* 滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(236, 72, 153, 0.4);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
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
    max-width: 100%;
  }

  .modern-input-container {
    border-radius: 12px;
  }

  .input-section {
    padding: 12px;
  }

  .modern-input {
    font-size: 14px;
  }

  .mode-selection {
    padding: 10px 12px;
  }

  .mode-buttons {
    gap: 6px;
  }

  .mode-btn {
    padding: 6px 10px;
    font-size: 12px;
  }

  .mode-icon {
    width: 14px;
    height: 14px;
  }

  .send-btn-modern {
    width: 32px;
    height: 32px;
  }

  .send-btn-modern svg {
    width: 16px;
    height: 16px;
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

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.modal-body {
  margin-bottom: 24px;
}

.modal-body p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.modal-btn {
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.modal-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.modal-btn.cancel:hover {
  background: #e8e8e8;
}

.modal-btn.confirm {
  background: #e74c3c;
  color: white;
}

.modal-btn.confirm:hover {
  background: #c0392b;
}

.rename-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.rename-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

/* 减少动画 */
@media (prefers-reduced-motion: reduce) {
  .message,
  .suggestion-chip,
  .input-wrapper,
  .send-btn-modern {
    animation: none;
    transition: none;
  }
}
</style>
