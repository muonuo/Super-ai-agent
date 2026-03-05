package com.monuo.superaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TavilyWebSearchToolTest {

    @Value("${search-api.tavily-api-key}")
    private String searchApiKey;

    @Test
    void searchWeb() {
        TavilyWebSearchTool tool = new TavilyWebSearchTool(searchApiKey);
        String query = "2025 年 AI 发展趋势";
        String result = tool.searchWeb(query);
        assertNotNull(result);
    }

    @Test
    void searchWebAdvanced() {
    }
}
