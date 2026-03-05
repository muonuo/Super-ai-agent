const BASE_URL = 'http://localhost:8123/api'

/**
 * AI恋爱大师 SSE 对话
 * @param {string} message - 用户消息
 * @param {string} chatId - 会话ID
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithLoveAppSse(message, chatId) {
  const response = await fetch(
    `${BASE_URL}/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId)}`
  )
  return response.body
}

/**
 * AI超级智能体 SSE 对话
 * @param {string} message - 用户消息
 * @returns {Promise<ReadableStream>}
 */
export async function doChatWithManus(message) {
  const response = await fetch(
    `${BASE_URL}/ai/manus/chat?message=${encodeURIComponent(message)}`
  )
  return response.body
}

/**
 * 生成UUID
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}
