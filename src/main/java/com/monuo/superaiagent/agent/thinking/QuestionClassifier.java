package com.monuo.superaiagent.agent.thinking;

import java.util.Arrays;
import java.util.List;

/**
 * 问题分类器
 * 快速判断问题是简单还是复杂
 */
public class QuestionClassifier {

    // 简单问题关键词（精确匹配）
    private static final List<String> SIMPLE_KEYWORDS = Arrays.asList(
            "你好", "您好", "hi", "hello", "嗨",
            "谢谢", "感谢", "thank",
            "再见", "拜拜", "bye"
    );

    // 复杂问题关键词（需要工具）
    private static final List<String> COMPLEX_KEYWORDS = Arrays.asList(
            "搜索", "查找", "下载", "保存", "写入", "读取",
            "发送", "邮件", "文件", "创建", "删除",
            "执行", "运行", "命令", "终端",
            "生成", "PDF", "抓取", "网页",
            "会什么", "能做什么", "有什么功能", "可以做什么"
    );

    /**
     * 分类问题
     */
    public static ThinkingChain.QuestionType classify(String question) {
        if (question == null || question.trim().isEmpty()) {
            return ThinkingChain.QuestionType.UNKNOWN;
        }

        String lowerQuestion = question.toLowerCase();

        // 检查是否包含复杂问题关键词
        for (String keyword : COMPLEX_KEYWORDS) {
            if (lowerQuestion.contains(keyword.toLowerCase())) {
                return ThinkingChain.QuestionType.COMPLEX;
            }
        }

        // 检查是否包含简单问题关键词
        for (String keyword : SIMPLE_KEYWORDS) {
            if (lowerQuestion.contains(keyword.toLowerCase())) {
                return ThinkingChain.QuestionType.SIMPLE;
            }
        }

        // 简短问题（少于10个字）通常是简单问题
        if (question.length() < 10) {
            return ThinkingChain.QuestionType.SIMPLE;
        }

        // 默认为复杂问题（保守策略）
        return ThinkingChain.QuestionType.COMPLEX;
    }

    /**
     * 是否是简单问题
     */
    public static boolean isSimple(String question) {
        return classify(question) == ThinkingChain.QuestionType.SIMPLE;
    }

    /**
     * 是否是复杂问题
     */
    public static boolean isComplex(String question) {
        return classify(question) == ThinkingChain.QuestionType.COMPLEX;
    }
}
