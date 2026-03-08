package com.monuo.superaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailSendToolTest {

    @Test
    void testEmailExtraction() {
        MailSendTool mailSendTool = new MailSendTool();

        String test1 = "我的邮箱是 test@example.com，请发给我";
        String email1 = mailSendTool.extractEmailFromText(test1);
        System.out.println("测试1 - 提取: " + email1);
        assertEquals("test@example.com", email1);

        String test2 = "请发送到 user@163.com";
        String email2 = mailSendTool.extractEmailFromText(test2);
        System.out.println("测试2 - 提取: " + email2);
        assertEquals("user@163.com", email2);

        String test3 = "没有任何邮箱信息";
        String email3 = mailSendTool.extractEmailFromText(test3);
        System.out.println("测试3 - 无邮箱: " + email3);
        assertNull(email3);
    }

    @Test
    void testEmailValidation() {
        MailSendTool mailSendTool = new MailSendTool();

        assertTrue(mailSendTool.isValidEmail("test@example.com"));
        assertTrue(mailSendTool.isValidEmail("user@163.com"));
        assertTrue(mailSendTool.isValidEmail("abc@qq.com"));
        assertFalse(mailSendTool.isValidEmail("invalid-email"));
        assertFalse(mailSendTool.isValidEmail("@example.com"));
        assertFalse(mailSendTool.isValidEmail("test@"));
        assertFalse(mailSendTool.isValidEmail(null));
        assertFalse(mailSendTool.isValidEmail(""));

        System.out.println("✅ 邮箱验证测试通过");
    }

    @Test
    void testSendMailSimple() {
        MailSendTool mailSendTool = new MailSendTool();

        // 测试无效邮箱
        String result1 = mailSendTool.sendMailSimple(
                "invalid-email",
                "测试主题",
                "测试内容",
                false
        );
        System.out.println("无效邮箱测试: " + result1);
        assertTrue(result1.contains("无效"));

        // 测试有效邮箱（需要配置qq-email相关配置才能真正发送）
        String result2 = mailSendTool.sendMailSimple(
                "recipient@example.com",  // 替换为实际收件人邮箱
                "测试邮件",
                "这是一封测试邮件",
                false
        );
        System.out.println("发送结果: " + result2);
        assertNotNull(result2);
    }

    @Test
    void testSendMailWithConversationHistory() {
        MailSendTool mailSendTool = new MailSendTool();

        String conversation = "用户: 请发邮件给我朋友\n助手: 好的，请问发到哪个邮箱？\n用户: 发到 helper@company.com";

        String result = mailSendTool.sendMail(
                null,  // 不提供to参数
                "测试主题",
                "测试内容",
                false,
                conversation
        );
        System.out.println("从对话历史提取邮箱测试: " + result);
        assertNotNull(result);
    }

    @Test
    void testSendMailWithExplicitEmail() {
        MailSendTool mailSendTool = new MailSendTool();

        // 明确提供邮箱，不从对话历史提取
        String result = mailSendTool.sendMail(
                "target@example.com",
                "明确邮箱测试",
                "测试内容",
                false,
                null
        );
        System.out.println("明确邮箱测试: " + result);
        assertNotNull(result);
    }
}
