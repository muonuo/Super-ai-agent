package com.monuo.superaiagent.config;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.monuo.superaiagent.rag.LoveAppDocumentLoader;

import com.monuo.superaiagent.rag.MyKeywordEnricher;
import com.monuo.superaiagent.rag.MyTokenTextSplitter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 恋爱大师向量数据库配置（初始化基于内存的向量数据库的 Bean ）
 */
//@Configuration  // 注释掉，暂停向量库初始化
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private DashScopeEmbeddingModel dashscopeEmbeddingModel;

    private VectorStore vectorStore;

    @Bean
    VectorStore loveAppVectorStore() {
        return vectorStore;
    }

    @PostConstruct
    public void init() {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        // 自主切分文档
//        documents = myTokenTextSplitter.splitCustomized(documents);
        // 自动补充关键词元信息
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);
        simpleVectorStore.add(enrichedDocuments);
        this.vectorStore = simpleVectorStore;
    }

}

