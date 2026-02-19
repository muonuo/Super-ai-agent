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

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
//        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");
//
//        // 测试网页抓取：恋爱案例分析
//        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");
//
//        // 测试资源下载：图片下载
//        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");
//
//        // 测试终端操作：执行代码
//        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存一份文件,里面写我的恋爱");

        // 测试 PDF 生成
//        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }


}
