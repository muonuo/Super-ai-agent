package com.monuo.superaiagent.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 恋爱大师 (LoveApp) 工具注册
 *
 * 包含工具：
 * - 网络搜索（获取恋爱知识）
 * - 邮件发送（发送恋爱报告）
 * - 约会地点规划（基于高德地图 MCP）
 * - 图片搜索（基于图片搜索 MCP）
 *
 * 注意：恋爱大师主要依靠 RAG 知识库回答问题，工具相对较少
 */
@Configuration
public class LoveAppToolRegistration {

    @Value("${search-api.tavily-api-key}")
    private String tavilySearchApiKey;

    @Resource
    private ToolCallbackProvider toolCallbackProvider; // MCP 工具提供者（高德地图、图片搜索等）

    @Bean
    public ToolCallback[] loveAppTools() {
        // 本地工具
        ToolCallback[] localTools = ToolCallbacks.from(
                new TavilyWebSearchTool(tavilySearchApiKey),
                new MailSendTool(),
                new DateLocationTool(),
                new FileOperationTool() // 添加文件操作工具，支持读取用户上传的文本
//                new RomanceImageSearchTool()
        );

        // MCP 工具（高德地图 15 个工具、图片搜索等）
        ToolCallback[] mcpTools = toolCallbackProvider.getToolCallbacks();

        // 合并两个数组
        return Stream.concat(
                Arrays.stream(localTools),
                Arrays.stream(mcpTools)
        ).toArray(ToolCallback[]::new);
    }
}
