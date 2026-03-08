package com.monuo.superaiagent.config;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.monuo.superaiagent.rag.LoveAppDocumentLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
@Slf4j
public class PgVectorVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean("pgVectorVectorStore")
    public VectorStore pgVectorVectorStore(
            @Qualifier("vectorJdbcTemplate") JdbcTemplate jdbcTemplate,
            DashScopeEmbeddingModel dashscopeEmbeddingModel) {

        log.info("PgVector 向量存储开始配置");

        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10000)
                .build();

        // 检查是否已有数据，避免重复加载
        if (hasDocuments(vectorStore)) {
            log.info("向量数据库已有数据，跳过加载");
            return vectorStore;
        }

        log.info("向量数据库为空，开始加载文档...");
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();

        // 分批加载（DashScope API 限制单次 batch size 不超过 10）
        int batchSize = 10;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            vectorStore.add(documents.subList(i, end));
        }

        log.info("文档加载完成，共 {} 条", documents.size());
        return vectorStore;
    }

    /**
     * 检查向量库是否已有文档
     */
    private boolean hasDocuments(VectorStore vectorStore) {
        try {
            List<Document> results = vectorStore.similaritySearch(
                    SearchRequest.builder().query("test").topK(1).build()
            );
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
