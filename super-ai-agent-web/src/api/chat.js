// 根据环境变量设置 API 基础 URL
const BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  (import.meta.env.PROD ? "/api" : "http://localhost:8123/api");

/**
 * AI恋爱大师 - 基础流式对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @param {boolean} enableTools - 是否启用工具调用
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppSse(
  message,
  chatId,
  enableTools = false,
) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}&enableTools=${enableTools}`,
  );
  return response.body;
}

/**
 * AI恋爱大师 - RAG流式对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @param {boolean} enableTools - 是否启用工具调用
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppRag(
  message,
  chatId,
  enableTools = false,
) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/rag?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}&enableTools=${enableTools}`,
  );
  return response.body;
}

/**
 * AI恋爱大师 - 智能流式对话（推荐）
 * 自动RAG回退 + SSE流式传输，提供最佳用户体验
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @param {boolean} enableTools - 是否启用工具调用
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppSmart(
  message,
  chatId,
  enableTools = false,
) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/smart?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}&enableTools=${enableTools}`,
  );
  return response.body;
}

/**
 * AI恋爱大师 - 恋爱报告功能（同步接口）
 * @param {string} message - 用户消息（描述恋爱情况）
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} 恋爱报告
 */
export async function doChatWithLoveAppReport(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/report?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`,
  );
  if (!response.ok) {
    throw new Error("恋爱报告生成失败");
  }
  return response.text();
}

/**
 * AI恋爱大师 - 工具调用功能（同步接口）
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} AI回答
 */
export async function doChatWithLoveAppTools(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/tools?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`,
  );
  if (!response.ok) {
    throw new Error("工具调用失败");
  }
  return response.text();
}

/**
 * Manus超级智能体 - 流式对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @param {string} userId - 用户ID（可选）
 * @param {string} userName - 用户名称（可选）
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithManus(
  message,
  chatId,
  userId = "",
  userName = "",
) {
  let url = `${BASE_URL}/ai/manus/chat?message=${encodeURIComponent(message)}`;
  if (chatId) {
    url += `&chatId=${encodeURIComponent(chatId)}`;
  }
  if (userId) {
    url += `&userId=${encodeURIComponent(userId)}`;
  }
  if (userName) {
    url += `&userName=${encodeURIComponent(userName)}`;
  }
  const response = await fetch(url);
  return response.body;
}

/**
 * 获取会话列表
 * @param {string} appType - 应用类型：love(恋爱大师) / manus(超级智能体) / 空(全部)
 * @returns {Promise<Array>} 会话列表
 */
export async function getConversations(appType = "") {
  const url = appType
    ? `${BASE_URL}/ai/history?appType=${encodeURIComponent(appType)}`
    : `${BASE_URL}/ai/history`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error("获取会话列表失败");
  }
  return response.json();
}

/**
 * 获取指定会话的消息历史
 * @param {string} chatId - 会话ID
 * @returns {Promise<Array>} 消息列表
 */
export async function getConversationHistory(chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/history/${encodeURIComponent(chatId)}`,
  );
  if (!response.ok) {
    throw new Error("获取消息历史失败");
  }
  return response.json();
}

/**
 * 删除指定会话
 * @param {string} chatId - 会话ID
 * @returns {Promise}
 */
export async function deleteConversation(chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/history/${encodeURIComponent(chatId)}`,
    { method: "DELETE" },
  );
  if (!response.ok) {
    throw new Error("删除会话失败");
  }
  return response.json();
}

/**
 * 生成UUID
 * @param {string} prefix - 会话ID前缀，用于区分不同应用 (love/manus)
 */
export function generateUUID(prefix = "") {
  const uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
    /[xy]/g,
    function (c) {
      const r = (Math.random() * 16) | 0;
      const v = c === "x" ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    },
  );
  return prefix ? `${prefix}_${uuid}` : uuid;
}
