\c super_ai_agent;
-- 1. 如果上面没数据，执行安装
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. 创建新表
CREATE TABLE IF NOT EXISTS public.vector_store (
    id TEXT PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding VECTOR(1024) -- 对应代码中的 .dimensions(1024)
);

-- 3. 创建 HNSW 索引 (对应 .indexType(HNSW))
-- 注意：必须先安装 pgvector 插件才能执行此语句
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx ON public.vector_store USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);