package com.monuo.superaiagent.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 恋爱大师 (LoveApp) 工具注册
 *
 * 包含工具：
 * - 网络搜索（获取恋爱知识）
 * - 邮件发送（发送恋爱报告）
 *
 * 注意：恋爱大师主要依靠RAG知识库回答问题，工具相对较少
 */
@Configuration
public class LoveAppToolRegistration {

    @Value("${search-api.tavily-api-key}")
    private String tavilySearchApiKey;

    @Bean
    public ToolCallback[] loveAppTools() {
        TavilyWebSearchTool tavilyWebSearchTool = new TavilyWebSearchTool(tavilySearchApiKey);
        MailSendTool mailSendTool = new MailSendTool();

        return ToolCallbacks.from(
                tavilyWebSearchTool,
                mailSendTool
        );
    }
}
