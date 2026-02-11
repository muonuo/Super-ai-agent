package com.monuo.superaiagent.config;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PgVectorVectorStoreConfigTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    @Test
    void pgVectorVectorStore() {
        List<Document> documents = List.of(
                new Document("鱼皮的编程导航有什么用?学编程啊，做项目啊", Map.of("meta1", "meta1")),
                new Document("程序员鱼皮的原创项目教程 codefather.cn", Map.of("meta2", "meta2")),
                new Document("鱼皮这小伙子比较帅气", Map.of("meta3", "meta3"))
        );

        // Add the documents to PGVector
        pgVectorVectorStore.add(documents);

        // Retrieve documents similar to a query
        List<Document> results = this.pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("鱼皮是谁").topK(3).build());
        Assertions.assertNotNull(results);
    }
}