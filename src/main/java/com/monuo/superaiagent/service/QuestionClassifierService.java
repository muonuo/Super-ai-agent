package com.monuo.superaiagent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 问题分类服务
 * 用于判断用户问题是恋爱相关、通用问题还是敏感问题
 */
//@Service
@Slf4j
public class QuestionClassifierService {

    /**
     * 问题类型枚举
     */
    public enum QuestionType {
        /**
         * 恋爱相关问题
         */
        LOVE_RELATED,
        /**
         * 通用问题（天气、新闻、历史、科技等）
         */
        GENERAL,
        /**
         * 敏感问题（暴力、色情、赌博、毒品、政治等）
         */
        SENSITIVE,
        /**
         * 未知类型
         */
        UNKNOWN
    }

    /**
     * 敏感词集合
     */
    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
            "暴力", "杀人", "打架", "赌博", "毒品", "吸毒", "色情", "黄色", "裸体",
            "政治", "反政府", "颠覆", "分裂", "恐怖", "炸弹", "武器", "枪支",
            "诈骗", "传销", "邪教", "迷信", "自杀", "自残", "未成年人性行为"
    );

    /**
     * 恋爱关键词集合
     */
    private static final Set<String> LOVE_KEYWORDS = Set.of(
            "恋爱", "感情", "爱情", "分手", "约会", "表白", "婚姻", "相亲",
            "追", "追女生", "追男生", "追女孩", "追男孩", "追求", "撩",
            "暗恋", "告白", "求婚", "离婚", "出轨", "劈腿", "第三者",
            "暧昧", "复合", "挽回", "前任", "现男友", "现女友", "女朋友",
            "男朋友", "老公", "老婆", "恋人", "情侣", "七夕", "情人节",
            "生日礼物", "周年纪念", "表白", "搭讪", "聊天", "沟通"
    );

    /**
     * 通用问题关键词集合
     */
    private static final Set<String> GENERAL_KEYWORDS = Set.of(
            "天气", "温度", "下雨", "下雪", "晴朗", "多云", "雾",
            "新闻", "今日新闻", "最近新闻", "发生了什么",
            "历史", "古代", "近代", "朝代", "历史事件",
            "科技", "技术", "人工智能", "AI", "手机", "电脑", "互联网",
            "生活", "日常", "做饭", "做菜", "旅游", "出行", "交通",
            "教育", "学习", "考试", "学校", "大学", "工作", "职场",
            "健康", "医疗", "锻炼", "运动", "饮食", "减肥",
            "娱乐", "电影", "音乐", "游戏", "综艺", "电视剧",
            "经济", "股票", "投资", "理财", "金融",
            "地理", "国家", "城市", "省份", "地图", "位置",
            "动物", "植物", "科学", "天文", "地理", "物理", "化学"
    );

    /**
     * 分类问题类型
     *
     * @param question 用户问题
     * @return 问题类型
     */
    public QuestionType classify(String question) {
        if (question == null || question.trim().isEmpty()) {
            return QuestionType.UNKNOWN;
        }

        String lowerQuestion = question.toLowerCase();

        // 1. 首先检测敏感词
        if (containsSensitiveKeyword(lowerQuestion)) {
            return QuestionType.SENSITIVE;
        }

        // 2. 检测恋爱关键词
        if (containsLoveKeyword(lowerQuestion)) {
            return QuestionType.LOVE_RELATED;
        }

        // 3. 检测通用问题关键词
        if (containsGeneralKeyword(lowerQuestion)) {
            return QuestionType.GENERAL;
        }

        // 4. 默认返回未知，让RAG尝试处理
        return QuestionType.UNKNOWN;
    }

    /**
     * 检测是否包含敏感词
     */
    private boolean containsSensitiveKeyword(String question) {
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到敏感词: {}", keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含恋爱关键词
     */
    private boolean containsLoveKeyword(String question) {
        for (String keyword : LOVE_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到恋爱关键词: {}", keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含通用问题关键词
     */
    private boolean containsGeneralKeyword(String question) {
        for (String keyword : GENERAL_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到通用问题关键词: {}", keyword);
                return true;
            }
        }
        return false;
    }
}
