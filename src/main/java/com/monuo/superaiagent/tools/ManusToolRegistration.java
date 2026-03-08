package com.monuo.superaiagent.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 超级智能体 (MonuoManus) 工具注册
 *
 * 包含工具：
 * - 文件操作
 * - 网络搜索
 * - 网页抓取
 * - 资源下载
 * - 终端命令
 * - PDF生成
 * - 邮件发送
 * - 用户交互
 */
@Configuration
public class ManusToolRegistration {

    @Value("${search-api.tavily-api-key}")
    private String tavilySearchApiKey;

    @Bean
    public ToolCallback[] manusTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        TavilyWebSearchTool tavilyWebSearchTool = new TavilyWebSearchTool(tavilySearchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        MailSendTool mailSendTool = new MailSendTool();
        UserAssistanceTool userAssistanceTool = new UserAssistanceTool();

        return ToolCallbacks.from(
                terminateTool,
                fileOperationTool,
                tavilyWebSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                mailSendTool,
                userAssistanceTool
        );
    }
}
