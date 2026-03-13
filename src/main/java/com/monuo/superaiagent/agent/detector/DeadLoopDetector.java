package com.monuo.superaiagent.agent.detector;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 防死循环检测器
 * 负责检测智能体是否陷入循环状态
 */
@Data
@Slf4j
public class DeadLoopDetector {

    // 重复响应阈值
    private int duplicateThreshold = 2;

    // 最大连续失败次数
    private int maxConsecutiveFailures = 3;

    // 当前连续失败计数
    private int consecutiveFailures = 0;

    // 已尝试的工具集合
    private Set<String> attemptedTools = new HashSet<>();

    // 最近响应列表
    private List<String> recentResponses = new ArrayList<>();

    // 最大记录最近响应数量
    private int maxRecentResponses = 5;

    // 语义相似度阈值
    private double similarityThreshold = 0.8;

    // 工具调用历史
    private List<ToolCallRecord> toolCallHistory = new ArrayList<>();

    /**
     * 工具调用记录
     */
    @Data
    public static class ToolCallRecord {
        private String toolName;
        private String argumentsHash;
        private long timestamp;
    }

    /**
     * 干预级别
     */
    public enum InterventionLevel {
        NONE,      // 无需干预
        PROMPT,    // 级别1: 添加提示
        WARNING,   // 级别2: 强烈警告
        TERMINATE  // 级别3: 强制终止
    }

    /**
     * 检测结果
     */
    @Data
    public static class DetectionResult {
        private InterventionLevel level;
        private String message;
        private String suggestedPrompt;

        public DetectionResult(InterventionLevel level, String message, String suggestedPrompt) {
            this.level = level;
            this.message = message;
            this.suggestedPrompt = suggestedPrompt;
        }
    }

    /**
     * 检查是否陷入循环
     */
    public DetectionResult detect() {
        // 检查语义重复
        if (!recentResponses.isEmpty()) {
            String lastResponse = recentResponses.get(recentResponses.size() - 1);
            if (isSemanticDuplicate(lastResponse)) {
                return new DetectionResult(
                    InterventionLevel.WARNING,
                    "检测到语义重复",
                    getWarningPrompt()
                );
            }
        }

        // 检查完全重复
        if (isRecentResponseDuplicate()) {
            return new DetectionResult(
                InterventionLevel.PROMPT,
                "检测到完全重复",
                getStuckPrompt()
            );
        }

        // 检查工具重复调用
        if (hasTooManyRepeatedToolCalls()) {
            return new DetectionResult(
                InterventionLevel.WARNING,
                "检测到工具重复调用",
                getWarningPrompt()
            );
        }

        // 检查连续失败
        if (hasReachedMaxFailures()) {
            return new DetectionResult(
                InterventionLevel.TERMINATE,
                "达到最大连续失败次数",
                null
            );
        }

        return new DetectionResult(InterventionLevel.NONE, "正常", null);
    }

    /**
     * 记录响应
     */
    public void recordResponse(String response) {
        if (response != null && !response.isEmpty()) {
            recentResponses.add(response);
            if (recentResponses.size() > maxRecentResponses) {
                recentResponses.remove(0);
            }
        }
    }

    /**
     * 记录工具调用
     */
    public void recordToolCall(String toolName, String arguments) {
        if (toolName == null) {
            return;
        }
        ToolCallRecord record = new ToolCallRecord();
        record.setToolName(toolName);
        record.setArgumentsHash(arguments != null ? String.valueOf(arguments.hashCode()) : "");
        record.setTimestamp(System.currentTimeMillis());
        toolCallHistory.add(record);
        attemptedTools.add(toolName);
    }

    /**
     * 记录失败
     */
    public void recordFailure() {
        consecutiveFailures++;
    }

    /**
     * 重置状态
     */
    public void reset() {
        consecutiveFailures = 0;
        attemptedTools.clear();
        recentResponses.clear();
        toolCallHistory.clear();
    }

    /**
     * 检查最近响应是否重复
     */
    private boolean isRecentResponseDuplicate() {
        if (recentResponses.isEmpty()) {
            return false;
        }
        String lastResponse = recentResponses.get(recentResponses.size() - 1);
        if (lastResponse == null || lastResponse.isEmpty()) {
            return false;
        }
        long matchCount = recentResponses.stream()
                .filter(r -> r.equals(lastResponse))
                .count();
        return matchCount >= duplicateThreshold;
    }

    /**
     * 检查语义相似度
     */
    private boolean isSemanticDuplicate(String response) {
        if (recentResponses.isEmpty() || response == null) {
            return false;
        }
        for (String recent : recentResponses) {
            double similarity = calculateJaccardSimilarity(response, recent);
            if (similarity >= similarityThreshold) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算 Jaccard 相似度
     */
    private double calculateJaccardSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) {
            return 0.0;
        }
        Set<String> set1 = new HashSet<>(Arrays.asList(s1.split("")));
        Set<String> set2 = new HashSet<>(Arrays.asList(s2.split("")));
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    /**
     * 检查工具重复调用
     */
    private boolean hasTooManyRepeatedToolCalls() {
        if (toolCallHistory.size() < 3) {
            return false;
        }
        int recentCount = Math.min(toolCallHistory.size(), 5);
        for (int i = toolCallHistory.size() - recentCount; i < toolCallHistory.size() - 1; i++) {
            ToolCallRecord current = toolCallHistory.get(i);
            for (int j = i + 1; j < toolCallHistory.size(); j++) {
                ToolCallRecord next = toolCallHistory.get(j);
                if (current.getToolName().equals(next.getToolName())
                        && current.getArgumentsHash().equals(next.getArgumentsHash())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查是否达到最大失败次数
     */
    private boolean hasReachedMaxFailures() {
        return consecutiveFailures >= maxConsecutiveFailures;
    }

    /**
     * 获取陷入循环的提示
     */
    private String getStuckPrompt() {
        return "检测到重复响应模式。你可能陷入了循环。请尝试以下策略：\n" +
                "1. 重新分析问题，可能需要不同的解决思路\n" +
                "2. 尝试使用不同的工具或方法\n" +
                "3. 如果任务确实无法完成，明确告知用户并终止\n" +
                "4. 避免重复已尝试过的无效路径";
    }

    /**
     * 获取强烈警告提示
     */
    private String getWarningPrompt() {
        return "【严重警告】检测到重复行为模式！\n" +
                "你正在重复相同的思考或行动，这可能导致任务无法完成。\n" +
                "请立即停止这种行为，并尝试以下策略：\n" +
                "1. 重新分析用户需求和当前状态\n" +
                "2. 考虑完全不同的解决思路\n" +
                "3. 如果当前方法不奏效，放弃并尝试新方法\n" +
                "4. 明确告知用户当前困境，不要重复无效尝试";
    }
}
