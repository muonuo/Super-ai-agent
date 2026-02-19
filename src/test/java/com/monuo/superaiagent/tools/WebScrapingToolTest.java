package com.monuo.superaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://fanyi.youdao.com/openapi";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
    }
}
