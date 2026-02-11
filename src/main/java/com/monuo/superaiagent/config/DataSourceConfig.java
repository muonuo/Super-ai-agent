package com.monuo.superaiagent.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
@Configuration
public class DataSourceConfig {

    /**
     * ========== MySQL 主数据源 ==========
     * 用于 MyBatis-Plus 等业务操作
     */
    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    /**
     * ========== PostgreSQL 向量数据源 ==========
     * 用于 PgVector 向量存储
     */
    @Bean(name = "pgVectorDataSource")
    @ConfigurationProperties(prefix = "pgvector.datasource")
    public DataSource pgVectorDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    /**
     * 主数据源对应的 JdbcTemplate（可选）
     * 如果业务代码需要直接使用 JdbcTemplate 操作 MySQL
     */
    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(
            @Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * PGVector 数据源对应的 JdbcTemplate
     * 用于 PgVector 向量数据库操作
     */
    @Bean(name = "vectorJdbcTemplate")
    public JdbcTemplate vectorJdbcTemplate(
            @Qualifier("pgVectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
