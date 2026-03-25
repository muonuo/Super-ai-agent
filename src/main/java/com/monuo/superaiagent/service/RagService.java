package com.monuo.superaiagent.service;

import com.monuo.superaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG 服务
 * 负责向量检索、查询重写、相似度判断等 RAG 相关功能
 */
@Service
@Slf4j
public class RagService {

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * RAG 相似度阈值
     */
    private static final double RAG_SIMILARITY_THRESHOLD = 0.6;

    /**
     * 查询重写
     * 将用户原始查询改写为更适合向量检索的形式
     *
     * @param message 原始消息
     * @return 重写后的消息
     */
    public String rewriteQuery(String message) {
        try {
            return queryRewriter.doQueryRewrite(message);
        } catch (Exception e) {
            log.warn("查询重写失败，使用原始消息: {}", e.getMessage());
            return message;
        }
    }

    /**
     * 检查是否有相关文档
     * 判断向量数据库中是否有相似度 >= 阈值的文档
     *
     * @param message 用户消息
     * @return true 表示有相关文档，应该使用 RAG；false 表示无相关文档，应该使用 fallback
     */
    public boolean hasRelevantDocs(String message) {
        try {
            String rewrittenMessage = rewriteQuery(message);
            List<Document> docs = searchSimilarDocuments(rewrittenMessage, 3);
            
            if (docs == null || docs.isEmpty()) {
                log.info("RAG检索无结果（未找到文档）");
                return false;
            }
            
            boolean hasRelevant = docs.stream()
                    .anyMatch(doc -> doc.getScore() >= RAG_SIMILARITY_THRESHOLD);
            
            if (hasRelevant) {
                log.info("RAG检索到{}条相关文档（相似度>={}）", docs.size(), RAG_SIMILARITY_THRESHOLD);
            } else {
                log.info("RAG检索无结果（相似度<{}）", RAG_SIMILARITY_THRESHOLD);
            }
            
            return hasRelevant;
        } catch (Exception e) {
            log.error("RAG检索异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 搜索相似文档
     *
     * @param query 查询文本
     * @param topK 返回前 K 个结果
     * @return 文档列表
     */
    public List<Document> searchSimilarDocuments(String query, int topK) {
        return pgVectorVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build()
        );
    }

    /**
     * 获取 RAG 相似度阈值
     */
    public double getSimilarityThreshold() {
        return RAG_SIMILARITY_THRESHOLD;
    }
}
