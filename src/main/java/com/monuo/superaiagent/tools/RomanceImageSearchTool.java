package com.monuo.superaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 恋爱场景图片搜索工具
 *
 * 提供恋爱场景专用的图片搜索接口：
 * - 情侣头像搜索
 * - 浪漫背景图搜索
 * - 约会地点参考图搜索
 * - 礼物推荐图搜索
 * 
 * 注意：此工具类仅提供工具定义，实际的图片搜索功能由 MCP 工具提供
 * 如需使用图片搜索功能，请确保在工具注册中配置了图片搜索 MCP 服务
 */
//@Component  // 暂时禁用，需要时取消注释
@Slf4j
public class RomanceImageSearchTool {

    /**
     * 搜索情侣头像
     *
     * @param style 风格（如：可爱、浪漫、简约、动漫）
     * @return 图片搜索建议
     */
    @Tool(description = "搜索情侣头像图片，风格可选：可爱、浪漫、简约、动漫、写实等")
    public String searchCoupleAvatar(@ToolParam(description = "头像风格") String style) {
        log.info("搜索情侣头像，风格：{}", style);
        return String.format("建议搜索【情侣头像 %s】风格的图片。请使用图片搜索工具查询。", style);
    }

    /**
     * 搜索浪漫背景图
     *
     * @param theme 主题（如：星空、樱花、海边、日落）
     * @return 图片搜索建议
     */
    @Tool(description = "搜索浪漫背景图，主题可选：星空、樱花、海边、日落、雪山、花海等")
    public String searchRomanticBackground(@ToolParam(description = "背景图主题") String theme) {
        log.info("搜索浪漫背景图，主题：{}", theme);
        return String.format("建议搜索【浪漫背景图 %s】主题的图片。请使用图片搜索工具查询。", theme);
    }

    /**
     * 搜索约会穿搭参考
     *
     * @param gender 性别（male/female）
     * @param occasion 场合（如：第一次约会、纪念日、日常约会）
     * @return 图片搜索建议
     */
    @Tool(description = "搜索约会穿搭参考图")
    public String searchDateOutfit(
            @ToolParam(description = "性别：male(男) 或 female(女)") String gender,
            @ToolParam(description = "场合：第一次约会、纪念日、日常约会等") String occasion
    ) {
        log.info("搜索约会穿搭参考：性别={}，场合={}", gender, occasion);
        String genderStr = gender.equals("male") ? "男生" : "女生";
        return String.format("建议搜索【%s约会穿搭 %s】的图片。请使用图片搜索工具查询。", genderStr, occasion);
    }

    /**
     * 搜索礼物推荐
     *
     * @param recipient 接收者（boyfriend/girlfriend）
     * @param occasion 场合（如：生日、纪念日、情人节、圣诞节）
     * @return 图片搜索建议
     */
    @Tool(description = "搜索礼物推荐图片")
    public String searchGiftIdea(
            @ToolParam(description = "接收者：boyfriend(男友) 或 girlfriend(女友)") String recipient,
            @ToolParam(description = "场合：生日、纪念日、情人节、圣诞节等") String occasion
    ) {
        log.info("搜索礼物推荐：接收者={}，场合={}", recipient, occasion);
        String recipientStr = recipient.equals("boyfriend") ? "送男友" : "送女友";
        return String.format("建议搜索【%s礼物 %s】的图片。请使用图片搜索工具查询。", recipientStr, occasion);
    }

    /**
     * 搜索约会地点参考图
     *
     * @param locationType 地点类型（如：餐厅、咖啡厅、公园、海边、游乐园）
     * @return 图片搜索建议
     */
    @Tool(description = "搜索约会地点参考图，地点类型可选：餐厅、咖啡厅、公园、海边、游乐园、美术馆等")
    public String searchDateLocationPhoto(
            @ToolParam(description = "地点类型") String locationType
    ) {
        log.info("搜索约会地点参考图：类型={}", locationType);
        return String.format("建议搜索【浪漫约会地点 %s】的图片。请使用图片搜索工具查询。", locationType);
    }

    /**
     * 搜索纪念日庆祝创意
     *
     * @param type 类型（如：布置、蛋糕、礼物包装、拍照姿势）
     * @return 图片搜索建议
     */
    @Tool(description = "搜索纪念日庆祝创意图片")
    public String searchAnniversaryIdea(
            @ToolParam(description = "类型：布置、蛋糕、礼物包装、拍照姿势等") String type
    ) {
        log.info("搜索纪念日庆祝创意：类型={}", type);
        return String.format("建议搜索【纪念日庆祝 %s】的图片。请使用图片搜索工具查询。", type);
    }
}
