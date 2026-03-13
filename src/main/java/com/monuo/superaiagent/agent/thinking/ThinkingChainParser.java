package com.monuo.superaiagent.agent.thinking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 思考链解析器
 * 负责解析 AI 输出的思考链格式
 */
@Slf4j
public class ThinkingChainParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解析思考链
     * 支持格式：
     * 【思考】...
     * 【工具调用】...
     * 【回复】...
     */
    public static ThinkingChain parse(String response) {
        ThinkingChain chain = new ThinkingChain();
        chain.setRawResponse(response);

        if (response == null || response.isEmpty()) {
            chain.setQuestionType(ThinkingChain.QuestionType.UNKNOWN);
            return chain;
        }

        try {
            // 提取思考内容
            String thinking = extractContent(response, "【思考】", "【");
            if (thinking != null) {
                chain.setThinking(thinking);
            }

            // 提取工具调用
            String toolCallContent = extractContent(response, "【工具调用】", "【");
            if (toolCallContent != null && !toolCallContent.trim().isEmpty()) {
                parseToolCalls(chain, toolCallContent);
            }

            // 提取回复内容
            String reply = extractContent(response, "【回复】", null);
            if (reply != null) {
                chain.setDirectReply(reply);
            }

            // 判断问题类型
            if (chain.hasToolCalls()) {
                chain.setQuestionType(ThinkingChain.QuestionType.COMPLEX);
            } else {
                chain.setQuestionType(ThinkingChain.QuestionType.SIMPLE);
            }

        } catch (Exception e) {
            log.error("解析思考链失败: {}", e.getMessage());
            chain.setQuestionType(ThinkingChain.QuestionType.UNKNOWN);
            chain.setDirectReply(response);
        }

        return chain;
    }

    /**
     * 提取标记之间的内容
     */
    private static String extractContent(String text, String startTag, String endTag) {
        int startIndex = text.indexOf(startTag);
        if (startIndex == -1) {
            return null;
        }
        startIndex += startTag.length();

        int endIndex;
        if (endTag == null) {
            endIndex = text.length();
        } else {
            endIndex = text.indexOf(endTag, startIndex);
            if (endIndex == -1) {
                endIndex = text.length();
            }
        }

        return text.substring(startIndex, endIndex).trim();
    }

    /**
     * 解析工具调用
     * 支持格式：
     * - 工具名称：xxx，参数：{...}
     * - JSON 格式：[{"tool": "xxx", "args": {...}}]
     */
    private static void parseToolCalls(ThinkingChain chain, String toolCallContent) {
        try {
            // 尝试解析 JSON 格式
            if (toolCallContent.trim().startsWith("[") || toolCallContent.trim().startsWith("{")) {
                parseJsonToolCalls(chain, toolCallContent);
            } else {
                // 解析文本格式
                parseTextToolCalls(chain, toolCallContent);
            }
        } catch (Exception e) {
            log.warn("解析工具调用失败，将作为普通文本处理: {}", e.getMessage());
        }
    }

    /**
     * 解析 JSON 格式的工具调用
     */
    private static void parseJsonToolCalls(ThinkingChain chain, String jsonContent) {
        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String toolName = node.get("tool").asText();
                    String args = node.get("args").toString();
                    chain.addToolCall(toolName, args);
                }
            } else {
                String toolName = root.get("tool").asText();
                String args = root.get("args").toString();
                chain.addToolCall(toolName, args);
            }
        } catch (Exception e) {
            log.error("解析 JSON 工具调用失败: {}", e.getMessage());
        }
    }

    /**
     * 解析文本格式的工具调用
     */
    private static void parseTextToolCalls(ThinkingChain chain, String textContent) {
        // 简单解析：工具名称：xxx，参数：xxx
        String[] lines = textContent.split("\n");
        for (String line : lines) {
            if (line.contains("工具") || line.contains("tool")) {
                // 提取工具名称和参数
                String toolName = extractToolName(line);
                String args = extractToolArgs(line);
                if (toolName != null) {
                    chain.addToolCall(toolName, args != null ? args : "{}");
                }
            }
        }
    }

    /**
     * 提取工具名称
     */
    private static String extractToolName(String line) {
        // 匹配：工具名称：xxx 或 tool: xxx
        if (line.contains("：")) {
            String[] parts = line.split("：");
            if (parts.length > 1) {
                return parts[1].split("[,，]")[0].trim();
            }
        } else if (line.contains(":")) {
            String[] parts = line.split(":");
            if (parts.length > 1) {
                return parts[1].split(",")[0].trim();
            }
        }
        return null;
    }

    /**
     * 提取工具参数
     */
    private static String extractToolArgs(String line) {
        // 匹配：参数：{...} 或 args: {...}
        int startIndex = line.indexOf("{");
        int endIndex = line.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return line.substring(startIndex, endIndex + 1);
        }
        return null;
    }
}
