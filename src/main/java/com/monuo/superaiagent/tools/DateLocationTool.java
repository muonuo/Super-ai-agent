package com.monuo.superaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 约会地点规划工具
 *
 * 提供恋爱场景专用的接口：
 * - 约会路线规划
 * - 浪漫地点推荐
 * - 餐厅/咖啡厅/电影院搜索
 * - 天气查询（适合约会吗）
 * 
 * 注意：此工具类仅提供工具定义，实际的地图功能由 MCP 工具（高德地图）提供
 * 如需使用地图功能，请确保在 LoveAppToolRegistration 中注册了 MCP 工具
 */
@Component
@Slf4j
public class DateLocationTool {

    /**
     * 规划约会路线
     *
     * @param from 出发地
     * @param to 目的地
     * @param mode 交通方式（driving/walking/transit/bicycling）
     * @return 路线规划结果
     */
    @Tool(description = "规划约会路线，可指定交通方式。交通方式可选：driving(驾车)、walking(步行)、transit(公交/地铁)、bicycling(骑行)")
    public String planDateRoute(String from, String to, String mode) {
        log.info("规划约会路线：从{}到{}，交通方式：{}", from, to, mode);
        
        String modeDesc = switch (mode.toLowerCase()) {
            case "driving", "驾车" -> "驾车";
            case "walking", "步行" -> "步行";
            case "transit", "公交", "地铁" -> "公交/地铁";
            case "bicycling", "骑行", "骑车" -> "骑行";
            default -> "驾车";
        };
        
        return String.format("建议使用%s方式从【%s】到【%s】。请使用地图工具查询详细路线。", modeDesc, from, to);
    }

    /**
     * 搜索约会地点
     *
     * @param location 搜索区域（如"北京朝阳区"）
     * @param type 地点类型（restaurant/cafe/cinema/park）
     * @return 地点列表
     */
    @Tool(description = "搜索适合约会的地点，如餐厅、咖啡厅、电影院、公园等。地点类型可选：restaurant(餐厅)、cafe(咖啡厅)、cinema(电影院)、park(公园)、mall(商场)")
    public String searchDatePlaces(String location, String type) {
        log.info("搜索约会地点：区域={}，类型={}", location, type);
        
        String searchType = switch (type.toLowerCase()) {
            case "restaurant", "餐厅" -> "餐厅";
            case "cafe", "咖啡厅", "咖啡" -> "咖啡厅";
            case "cinema", "电影院", "影城" -> "电影院";
            case "park", "公园" -> "公园";
            case "mall", "商场", "购物中心" -> "购物中心";
            default -> type;
        };
        
        return String.format("建议在【%s】搜索【%s】。请使用地图搜索工具查询具体地点。", location, searchType);
    }

    /**
     * 查询约会地点周边的设施
     *
     * @param location 中心位置（经纬度或地址）
     * @param radius 搜索半径（米）
     * @param type 设施类型
     * @return 周边设施列表
     */
    @Tool(description = "查询某个地点周边的设施，如停车场、地铁站、洗手间等")
    public String searchAroundDatePlace(String location, String radius, String type) {
        log.info("查询周边设施：位置={}，半径={}m，类型={}", location, radius, type);
        
        return String.format("建议在【%s】周边%s米范围内搜索【%s】。请使用地图周边搜索工具查询。", location, radius, type);
    }

    /**
     * 查询约会地点的天气
     *
     * @param city 城市名
     * @return 天气信息
     */
    @Tool(description = "查询城市天气，判断是否适合户外约会")
    public String checkDateWeather(String city) {
        log.info("查询约会天气：城市={}", city);
        
        return String.format("建议查询【%s】的天气情况。请使用天气查询工具获取详细信息，以便判断是否适合户外约会。", city);
    }

    /**
     * 获取两个约会地点之间的距离
     *
     * @param origin 起点
     * @param destination 终点
     * @return 距离信息
     */
    @Tool(description = "获取两个约会地点之间的距离，帮助规划行程时间")
    public String getDistanceBetweenPlaces(String origin, String destination) {
        log.info("查询距离：从{}到{}", origin, destination);
        
        return String.format("建议查询从【%s】到【%s】的距离。请使用地图距离查询工具获取详细信息。", origin, destination);
    }
}
