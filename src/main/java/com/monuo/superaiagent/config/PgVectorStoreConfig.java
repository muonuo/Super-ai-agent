package com.monuo.superaiagent.config;

import com.monuo.superaiagent.rag.LoveAppDocumentLoader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * PGVector 向量数据库配置
 * 自动将文档加载到 PostgreSQL 向量数据库中
 */
@Configuration
@Slf4j
public class PgVectorStoreConfig {

    @Resource
    private PgVectorStore pgVectorVectorStore;

    @Resource

    private LoveAppDocumentLoader loveAppDocumentLoader;

    /**
     * 应用启动时自动执行，将文档加载到向量数据库
     * 注意：每次重启都会重新加载，如需避免重复加载，请检查数据库中是否已存在数据
     */
    @PostConstruct
    public void initVectorStore() {
        try {
            // 检查是否已有数据，避免重复加载
            List<Document> existingDocs = pgVectorVectorStore.similaritySearch(
                    org.springframework.ai.vectorstore.SearchRequest.builder()
                            .query("test")
                            .topK(1)
                            .build()
            );

            if (existingDocs.isEmpty()) {
                log.info("向量数据库为空，开始加载文档...");
                List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
                pgVectorVectorStore.add(documents);
                log.info("文档加载完成，共加载 {} 个文档片段", documents.size());
            } else {
                log.info("向量数据库已有数据，跳过初始化加载");
            }
        } catch (Exception e) {
            log.error("向量数据库初始化失败", e);
        }
    }
}
