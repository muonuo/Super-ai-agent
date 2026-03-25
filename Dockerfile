# 多阶段构建 - 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制 Maven 配置文件，利用 Docker 缓存层
COPY pom.xml .
COPY monuo-image-search-mcp-server/pom.xml monuo-image-search-mcp-server/

# 下载依赖（利用缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src
COPY monuo-image-search-mcp-server/src ./monuo-image-search-mcp-server/src

# 打包构建（跳过测试）
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用轻量级 JRE 镜像
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 安装必要的系统库（用于 PostgreSQL JDBC 等）
RUN apk add --no-cache libstdc++ curl

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/Super-ai-agent-0.0.1-SNAPSHOT.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8123/api/actuator/health || exit 1

# 暴露端口
EXPOSE 8123

# JVM 参数优化 - 关键！限制内存使用以适配微信云托管免费版 512MB
# -Xmx256m: 最大堆内存 256MB
# -Xms128m: 初始堆内存 128MB
# -XX:+UseG1GC: 使用 G1 垃圾回收器
# -XX:MaxGCPauseMillis=200: 最大 GC 停顿时间
# -XX:+HeapDumpOnOutOfMemoryError: OOM 时生成堆转储
# -XX:HeapDumpPath=/tmp: 堆转储文件路径
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"

# 设置 Spring Profile 和时区
ENV SPRING_PROFILES_ACTIVE=cloud
ENV TZ=Asia/Shanghai

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
