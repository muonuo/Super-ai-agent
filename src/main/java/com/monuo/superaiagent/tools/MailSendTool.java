package com.monuo.superaiagent.tools;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件发送工具类（基于 Hutool MailUtil）
 * 支持从用户对话中自动提取邮箱地址
 */
@Slf4j
@Component
public class MailSendTool {

    @Value("${qq-email.from}")
    private String fromEmail;

    @Value("${qq-email.auth-code}")
    private String authCode;

    @Value("${qq-email.smtp-host}")
    private String smtpHost;

    @Value("${qq-email.smtp-port}")
    private String smtpPort;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    /**
     * 从文本中提取邮箱地址
     *
     * @param text 用户输入的文本
     * @return 提取到的邮箱地址，如果没有则返回null
     */
    public String extractEmailFromText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            String email = matcher.group();
            log.info("从文本中提取到邮箱: {}", email);
            return email;
        }
        return null;
    }

    /**
     * 验证邮箱格式是否合法
     *
     * @param email 邮箱地址
     * @return 是否合法
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 从对话历史中提取邮箱地址
     * 支持多种表达方式：
     * - "我的邮箱是 xxx@xxx.com"
     * - "发给 xxx@xxx.com"
     * - "xxx@xxx.com"
     *
     * @param conversationHistory 对话历史（多条消息用\n分隔）
     * @return 提取到的邮箱地址
     */
    public String extractEmailFromConversation(String conversationHistory) {
        if (conversationHistory == null || conversationHistory.isEmpty()) {
            return null;
        }
        return extractEmailFromText(conversationHistory);
    }

    /**
     * 发送邮件（自动提取邮箱版本）
     * 当用户没有明确提供邮箱时，会尝试从对话上下文中提取
     *
     * @param to                   收件人邮箱（如果为null或空，将从对话历史中提取）
     * @param subject              邮件标题
     * @param content              邮件正文
     * @param html                 是否为html格式
     * @param conversationHistory 对话历史（用于提取邮箱）
     */
    @Tool(description = "Send email to a user. " +
            "If the recipient email is not explicitly provided, the tool will automatically extract " +
            "the email address from the conversation context. " +
            "Supported patterns: 'my email is xxx@xxx.com', 'send to xxx@xxx.com', or just the email address.")
    public String sendMail(
            @ToolParam(description = "Recipient's email address. If not provided, will be extracted from conversation context.") String to,
            @ToolParam(description = "Email subject") String subject,
            @ToolParam(description = "Email content") String content,
            @ToolParam(description = "If the content is .html style", required = false) Boolean html,
            @ToolParam(description = "Conversation history for extracting email address if 'to' is not provided", required = false) String conversationHistory) {

        String targetEmail = to;

        if (!isValidEmail(targetEmail) && conversationHistory != null) {
            targetEmail = extractEmailFromConversation(conversationHistory);
        }

        if (!isValidEmail(targetEmail)) {
            String errorMsg = "无法获取有效的邮箱地址。请在请求中明确提供收件人邮箱，例如：'发邮件到 xxx@xxx.com'";
            log.warn("❌ 邮箱提取失败: {}", errorMsg);
            return errorMsg;
        }

        return doSendEmail(targetEmail, subject, content, html != null ? html : false);
    }

    /**
     * 发送邮件（简洁版本）
     *
     * @param to      收件人邮箱
     * @param subject 邮件标题
     * @param content 邮件正文
     */
    @Tool(description = "Send email to a user with subject and content. " +
            "The recipient email must be explicitly provided in the 'to' parameter.")
    public String sendMailSimple(
            @ToolParam(description = "Recipient's email address") String to,
            @ToolParam(description = "Email subject") String subject,
            @ToolParam(description = "Email content") String content,
            @ToolParam(description = "If the content is .html style", required = false) Boolean html) {

        if (!isValidEmail(to)) {
            return "无效的邮箱地址：" + to + "。请提供正确的邮箱格式，如：user@example.com";
        }

        return doSendEmail(to, subject, content, html != null ? html : false);
    }

    /**
     * 执行实际的邮件发送操作
     */
    private String doSendEmail(String to, String subject, String content, boolean html) {
        try {
            MailAccount account = new MailAccount();
            account.setHost(smtpHost);
            account.setPort(Integer.parseInt(smtpPort));
            account.setAuth(true);
            account.setFrom(fromEmail);
            account.setPass(authCode);
            account.setSslEnable(true);

            MailUtil.send(account, to, subject, content, html);
            log.info("✅ 邮件发送成功 -> {}", to);
            return "邮件发送成功 -> " + to + "，主题：" + subject;
        } catch (Exception e) {
            log.error("❌ 邮件发送失败：{}", e.getMessage());
            return "邮件发送失败：" + e.getMessage();
        }
    }
}
