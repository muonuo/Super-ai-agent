// 新版API - 使用重构后的控制器路径
// 根据环境变量设置 API 基础 URL
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  (import.meta.env.PROD ? '/api' : 'http://localhost:8123/api')

// ==================== 流式接口 (推荐使用) ====================

/**
 * AI恋爱大师 - 基础流式对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppStream(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/stream/chat?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  return response.body
}

/**
 * AI恋爱大师 - RAG流式对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppRagStream(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/stream/rag?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  return response.body
}

/**
 * AI恋爱大师 - 智能流式对话（推荐）
 * 自动RAG回退 + SSE流式传输，提供最佳用户体验
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppSmartStream(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/stream/smart?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  return response.body
}

/**
 * Manus超级智能体 - 流式对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithManusStream(message, chatId) {
  const url = chatId 
    ? `${BASE_URL}/ai/manus/stream/chat?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
    : `${BASE_URL}/ai/manus/stream/chat?message=${encodeURIComponent(message)}`
  const response = await fetch(url)
  return response.body
}

// ==================== 同步接口 ====================

/**
 * AI恋爱大师 - 基础同步对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} AI回答
 */
export async function doChatWithLoveAppSync(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/chat?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  if (!response.ok) {
    throw new Error('基础对话失败')
  }
  return response.text()
}

/**
 * AI恋爱大师 - RAG知识库问答
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} AI回答
 */
export async function doChatWithLoveAppRagSync(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/rag?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  if (!response.ok) {
    throw new Error('RAG问答失败')
  }
  return response.text()
}

/**
 * AI恋爱大师 - 智能同步对话（推荐）
 * 自动选择最佳回答方式：RAG知识库 -> 通用LLM
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} AI回答
 */
export async function doChatWithLoveAppSmartSync(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/smart?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  if (!response.ok) {
    throw new Error('智能问答失败')
  }
  return response.text()
}

/**
 * AI恋爱大师 - 恋爱报告功能
 * @param {string} message - 用户消息（描述恋爱情况）
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} 恋爱报告
 */
export async function doChatWithLoveAppReportSync(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/report?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  if (!response.ok) {
    throw new Error('恋爱报告生成失败')
  }
  return response.text()
}

/**
 * AI恋爱大师 - 工具调用功能
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} AI回答
 */
export async function doChatWithLoveAppToolsSync(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/tools?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  if (!response.ok) {
    throw new Error('工具调用失败')
  }
  return response.text()
}

/**
 * AI恋爱大师 - MCP服务调用
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<string>} AI回答
 */
export async function doChatWithLoveAppMcpSync(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/sync/mcp?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  if (!response.ok) {
    throw new Error('MCP服务调用失败')
  }
  return response.text()
}

// ==================== 通用功能 ====================

/**
 * 获取会话列表
 * @param {string} appType - 应用类型：love(恋爱大师) / manus(超级智能体) / 空(全部)
 * @returns {Promise<Array>} 会话列表
 */
export async function getConversations(appType = '') {
  const url = appType 
    ? `${BASE_URL}/ai/history?appType=${encodeURIComponent(appType)}`
    : `${BASE_URL}/ai/history`
  const response = await fetch(url)
  if (!response.ok) {
    throw new Error('获取会话列表失败')
  }
  return response.json()
}

/**
 * 获取指定会话的消息历史
 * @param {string} chatId - 会话ID
 * @returns {Promise<Array>} 消息列表
 */
export async function getConversationHistory(chatId) {
  const response = await fetch(`${BASE_URL}/ai/history/${encodeURIComponent(chatId)}`)
  if (!response.ok) {
    throw new Error('获取消息历史失败')
  }
  return response.json()
}

/**
 * 删除指定会话
 * @param {string} chatId - 会话ID
 * @returns {Promise}
 */
export async function deleteConversation(chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/history/${encodeURIComponent(chatId)}`,
    { method: 'DELETE' }
  )
  if (!response.ok) {
    throw new Error('删除会话失败')
  }
  return response.json()
}

/**
 * 生成UUID
 * @param {string} prefix - 会话ID前缀，用于区分不同应用 (love/manus)
 */
export function generateUUID(prefix = '') {
  const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
  return prefix ? `${prefix}_${uuid}` : uuid
}

// ==================== 推荐的API组合 ====================

/**
 * 推荐的流式API组合
 * 根据不同场景选择最合适的API
 */
export const RecommendedStreamAPI = {
  // 智能模式（推荐）- 自动RAG回退
  smart: doChatWithLoveAppSmartStream,
  // 基础对话
  basic: doChatWithLoveAppStream,
  // RAG知识库对话
  rag: doChatWithLoveAppRagStream,
  // Manus智能体
  manus: doChatWithManusStream
}

/**
 * 推荐的同步API组合
 * 适用于不需要流式传输的场景
 */
export const RecommendedSyncAPI = {
  // 智能模式（推荐）- 自动RAG回退
  smart: doChatWithLoveAppSmartSync,
  // 基础对话
  basic: doChatWithLoveAppSync,
  // RAG知识库对话
  rag: doChatWithLoveAppRagSync,
  // 恋爱报告
  report: doChatWithLoveAppReportSync,
  // 工具调用
  tools: doChatWithLoveAppToolsSync,
  // MCP服务
  mcp: doChatWithLoveAppMcpSync
}