-- 创建数据库（如果不存在）
-- 注意：云环境部署时请谨慎使用，避免删除已有数据
CREATE DATABASE IF NOT EXISTS `super_ai_agent`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `super_ai_agent`;

-- 导出 表 super_ai_agent.chat_message 结构
-- 使用 DROP TABLE IF EXISTS 避免重复创建报错
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `conversation_id` varchar(64)     NOT NULL DEFAULT '' COMMENT '会话 ID',
    `message_type`    varchar(20)     NOT NULL DEFAULT '' COMMENT '消息类型：user-用户，assistant-助手，system-系统',
    `content`         text            NOT NULL COMMENT '消息内容',
    `metadata`        text            NOT NULL COMMENT '元数据（JSON 格式）',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint(1)      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`)
    ) ENGINE=InnoDB
    AUTO_INCREMENT=1
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_general_ci
    COMMENT='聊天消息表';
