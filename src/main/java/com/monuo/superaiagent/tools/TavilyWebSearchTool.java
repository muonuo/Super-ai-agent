package com.monuo.superaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tavily 网页搜索工具
 * 专为 AI Agent 设计的搜索引擎，返回结果已清洗，对 LLM 友好
 */
@Slf4j
public class TavilyWebSearchTool {

    // Tavily Search API 地址
    private static final String TAVILY_API_URL = "https://api.tavily.com/search";

    private final String apiKey;

    // 每次搜索返回的最大结果数
    private final int maxResults;

    public TavilyWebSearchTool(String apiKey) {
        this(apiKey, 5);//默认maxResults = 5
    }

    public TavilyWebSearchTool(String apiKey, int maxResults) {
        this.apiKey = apiKey;
        this.maxResults = maxResults;
    }

    @Tool(description = "Search for information from Tavily AI Search Engine. Use this when you need to find current information, news, or facts from the internet.")
    public String searchWeb(
            @ToolParam(description = "Search query keyword, be specific and clear") String query) {
        return executeSearch(query, "basic", null, this.maxResults);
    }

    /**
     * 高级搜索：支持更多参数定制
     */
    @Tool(description = "Advanced search with custom parameters for Tavily AI Search Engine")
    public String searchWebAdvanced(
            @ToolParam(description = "Search query keyword") String query,
            @ToolParam(description = "Search depth: basic or advanced", required = false) String searchDepth,
            @ToolParam(description = "Time range: day, week, month, year", required = false) String timeRange,
            @ToolParam(description = "Max results (1-10)", required = false) Integer maxResults) {

        // 校验 maxResults 范围
        if (maxResults != null) {
            if (maxResults < 1) {
                log.warn("maxResults {} is too small, using minimum: 1", maxResults);
                maxResults = 1;
            } else if (maxResults > 10) {
                log.warn("maxResults {} exceeds limit, using maximum: 10", maxResults);
                maxResults = 10;
            }
        }

        return executeSearch(query, searchDepth, timeRange, maxResults);
    }

    /**
     * 执行搜索的公共方法
     */
    private String executeSearch(String query, String searchDepth, String timeRange, Integer maxResults) {
        log.debug("Tavily search query: {}, depth: {}, timeRange: {}, maxResults: {}",
                query, searchDepth, timeRange, maxResults);

        // 构建请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("api_key", apiKey);
        paramMap.put("query", query);
        paramMap.put("search_depth", "basic");        // basic 或 advanced
        paramMap.put("max_results", maxResults);
        paramMap.put("include_answer", true);         // 让 Tavily 返回 AI 生成的摘要
        paramMap.put("include_raw_content", false);   // 不需要原始 HTML，节省额度
//        paramMap.put("include_domains", new String[]{}); // 可指定域名白名单
//        paramMap.put("exclude_domains", new String[]{}); // 可指定域名黑名单

        if (timeRange != null) {
            paramMap.put("time_range", timeRange);
        }

        try {
            // POST 请求（Tavily 要求 POST）
            String response = HttpUtil.post(TAVILY_API_URL, JSONUtil.toJsonStr(paramMap));
            log.debug("Tavily API response: {}", response);

            JSONObject jsonObject = JSONUtil.parseObj(response);

            // 检查是否有错误
            if (jsonObject.containsKey("detail")) {
                String error = jsonObject.getStr("detail");
                log.error("Tavily API error: {}", error);
                return "Search error: " + error;
            }

            // 优先返回 AI 生成的答案（如果有）
            String answer = jsonObject.getStr("answer");
            if (answer != null && !answer.trim().isEmpty()) {
                log.info("Tavily search completed with AI answer");
                return "AI Summary:\n" + answer + "\n\n---\n\nSearch Results:\n" +
                        formatResults(jsonObject.getJSONArray("results"));
            }

            // 否则返回搜索结果列表
            JSONArray results = jsonObject.getJSONArray("results");
            if (results == null || results.isEmpty()) {
                log.info("Tavily search completed with no results");
                return "No search results found.";
            }

            log.info("Tavily search completed with {} results", results.size());
            return formatResults(results);

        } catch (Exception e) {
            log.error("Tavily search failed for query: {}", query, e);
            return "Error searching Tavily: " + e.getMessage();
        }
    }

    /**
     * 格式化搜索结果为易读的字符串
     */
    private String formatResults(JSONArray results) {
        if (results == null || results.isEmpty()) {
            return "No search results found.";
        }

        int limit = Math.min(maxResults, results.size());

        return results.stream()
                .limit(limit)
                .map(obj -> {
                    JSONObject item = (JSONObject) obj;
                    String title = item.getStr("title", "无标题");
                    String url = item.getStr("url", "无链接");
                    String content = item.getStr("content", "无内容");

                    return String.format("【%s】\n链接：%s\n摘要：%s", title, url, content);
                })
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
