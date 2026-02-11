package com.monuo.superaiagent.app;

import com.monuo.superaiagent.entity.ChatMessage;
import com.monuo.superaiagent.repository.ChatMessageRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;


@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ChatMessageRepository chatMessageRepository;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        // 第一轮
        String message = "你好，我是程序员monuo";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 验证数据库中保存了消息
        List<ChatMessage> messagesAfterFirstRound = chatMessageRepository.listByConversationId(chatId);
        Assertions.assertTrue(messagesAfterFirstRound.size() >= 2, "第一轮对话后应该至少有2条消息（用户消息+助手消息）");

        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 验证数据库中保存了更多消息
        List<ChatMessage> messagesAfterSecondRound = chatMessageRepository.listByConversationId(chatId);
        Assertions.assertTrue(messagesAfterSecondRound.size() >= 4, "第二轮对话后应该至少有4条消息");

        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 验证数据库中保存了所有消息
        List<ChatMessage> messagesAfterThirdRound = chatMessageRepository.listByConversationId(chatId);
        Assertions.assertTrue(messagesAfterThirdRound.size() >= 6, "第三轮对话后应该至少有6条消息");

        // 验证消息内容包含用户输入
        boolean hasUserMessage = messagesAfterThirdRound.stream()
                .anyMatch(msg -> msg.getContent().contains("程序员monuo"));
        Assertions.assertTrue(hasUserMessage, "数据库中应该包含用户的消息");

        // 清理测试数据
        chatMessageRepository.deleteByConversationId(chatId);
        List<ChatMessage> messagesAfterCleanup = chatMessageRepository.listByConversationId(chatId);
        Assertions.assertTrue(messagesAfterCleanup.isEmpty(), "清理后数据库中不应该有消息");
    }

    @Test
    void testDoChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员monuo，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void testChat1() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员monuo";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void TestDoChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
