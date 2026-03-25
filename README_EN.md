# Super AI Agent - Intelligent Conversational Assistant Platform

<div align="center">

[中文](README.md) | English

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue)
![Vue](https://img.shields.io/badge/Vue-3.4.0-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

A production-ready AI conversational platform built with Spring Boot 3.5 + Java 21 + Spring AI + Vue 3. Features emotional consulting, deep-thinking agents, RAG knowledge retrieval, and multi-tool integration. Supports love report generation, map services, PDF processing, and more.

[Features](#-features) • [Architecture](#-architecture) • [Quick Start](#-quick-start) • [Demo](#-demo)

</div>

---

## 📸 Demo

### Home Page

<p align="center">
  <img src="docs/images/首页.jpg" alt="Home Page" width="800"/>
  <br/>
  <em>Home Page - Choose Your AI Assistant</em>
</p>

**Features:**

- ✅ Clean and modern interface design
- ✅ Two AI applications to choose from
- ✅ Quick access to Love Master and Super Agent
- ✅ Responsive layout for all devices

### AI Love Master

<p align="center">
  <img src="docs/images/恋爱大师.jpg" alt="AI Love Master Demo" width="800"/>
  <br/>
  <em>AI Love Master - Emotional Consulting with Pure Text Conversation</em>
</p>

**Features:**

- ✅ Natural pure text conversation, no Markdown formatting interference
- ✅ Three conversation modes: Basic, Smart (Recommended), RAG Q&A
- ✅ Enhanced features: Love report generation, tool calling
- ✅ Session management: New chat, rename, delete
- ✅ Real-time streaming output with typewriter effect

### AI Super Agent (Manus)

<p align="center">
  <img src="docs/images/智能体.jpg" alt="Manus Super Agent Demo" width="800"/>
  <br/>
  <em>Manus Super Agent - Deep Thinking with Tool Integration</em>
</p>

**Features:**

- ✅ Gemini-style thinking process display (collapsible)
- ✅ Real-time display of thinking steps and time
- ✅ 14+ automatic tool calling (search, file, email, PDF, etc.)
- ✅ MCP protocol integration (Amap 15 tools)
- ✅ Smart question classification (simple questions direct answer, complex questions deep thinking)

---

## 📖 Introduction

Super AI Agent is a **production-grade AI conversational platform** that demonstrates how to build complete agent applications using Spring AI.

### 🎭 Two Core Applications

<table>
<tr>
<td width="50%">

#### 💕 AI Love Master

Professional emotional consulting assistant

- ✅ Smart conversation (Basic/Smart/RAG modes)
- ✅ Auto-generate structured love reports
- ✅ RAG knowledge-enhanced responses
- ✅ Intelligent fallback strategy
- ✅ Report download and sharing

</td>
<td width="50%">

#### 🤖 AI Super Agent (Manus)

All-purpose assistant with deep thinking

- ✅ DeepSeek-style thinking process display
- ✅ Complete ReAct loop (Think-Act-Observe)
- ✅ 14+ tool integrations (search/file/email/PDF)
- ✅ MCP protocol integration (Amap 15 tools)
- ✅ Anti-loop detection and timeout control

</td>
</tr>
</table>

### 🌟 Why Choose This Project?

| Feature                      | Description                                                             |
| ---------------------------- | ----------------------------------------------------------------------- |
| 📚 **Learning Friendly**     | Detailed comments, clear architecture, perfect for Spring AI beginners  |
| 🏗️ **Complete Architecture** | Layered architecture + Agent pattern + RAG + Tool calling               |
| 🎯 **Production Ready**      | Includes exception handling, logging, monitoring, protection mechanisms |
| 📝 **Well Documented**       | README, code comments, architecture diagrams                            |
| 💼 **Resume Project**        | Modern tech stack, complete features, interview advantage               |
| 🚀 **Easy Deployment**       | One-click Docker Compose startup                                        |

---

## ✨ Features

### AI Love Master

- 💬 **Smart Conversation**: Three modes (Basic/Smart/RAG)
- 📊 **Love Reports**: Auto-generate structured emotional analysis
- 📥 **Report Download**: Support download and copy
- 🎯 **RAG Knowledge**: Professional answers based on love knowledge base
- 🔄 **Smart Fallback**: Auto-switch to general chat when RAG fails

### AI Super Agent (Manus)

- 🧠 **Deep Thinking**: Display complete thinking process (collapsible)
- 🔧 **Tool Calling**: 14+ tools (search, file, email, PDF generation, etc.)
- 🌐 **MCP Integration**: Amap 15 tools (POI search, route planning, etc.)
- 💭 **Thinking Visualization**: Gemini-style thinking process display
- ⚡ **Streaming Output**: Real-time AI responses and thinking steps
- 🎨 **Smart Classification**: Auto-judge simple/complex questions

### Core Capabilities

| Feature                     | Description                                                         |
| --------------------------- | ------------------------------------------------------------------- |
| **Question Classification** | Quickly judge question type based on keywords                       |
| **Selective Thinking**      | Direct answer for simple, deep thinking for complex                 |
| **Tool Calling**            | Auto-select and call appropriate tools                              |
| **Anti-Loop**               | Semantic repetition, tool repetition, consecutive failure detection |
| **Execution Monitoring**    | Timeout control, execution status tracking                          |
| **Conversation Memory**     | Multiple storage methods (memory/file/database)                     |
| **RAG Retrieval**           | Vector storage, query transformation, multi-query expansion         |

---

## 🏗️ Architecture

### Backend Stack

| Technology        | Version     | Description                  |
| ----------------- | ----------- | ---------------------------- |
| Java              | 21          | Programming Language         |
| Spring Boot       | 3.5.9       | Application Framework        |
| Spring AI         | 1.0.0       | AI Integration Framework     |
| Spring AI Alibaba | 1.0.0.2     | Alibaba Cloud AI Integration |
| MyBatis-Plus      | 3.5.12      | ORM Framework                |
| MySQL             | 8.0+        | Conversation History Storage |
| PostgreSQL        | 14+         | Vector Database (PGVector)   |
| LangChain4j       | 1.0.0-beta2 | AI Orchestration Framework   |

### Frontend Stack

| Technology | Version | Description        |
| ---------- | ------- | ------------------ |
| Vue        | 3.4.0   | Frontend Framework |
| Vue Router | 4.2.0   | Route Management   |
| Axios      | 1.6.0   | HTTP Client        |
| Vite       | 5.0.0   | Build Tool         |

### AI Capabilities

| Capability      | Provider          | Description                 |
| --------------- | ----------------- | --------------------------- |
| Chat Model      | Alibaba Qwen      | qwen-max, qwen-plus         |
| Embedding Model | Alibaba DashScope | text-embedding-v2           |
| Local Model     | Ollama            | Optional local deployment   |
| Vector Store    | PGVector          | PostgreSQL vector extension |
| MCP Tools       | Amap              | 15 map-related tools        |

### Architecture Design

```
┌─────────────────────────────────────────────────────────┐
│                   Frontend (Vue 3)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Love Master │  │  Super Agent │  │   Home       │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            │
                            │ HTTP/SSE
                            ▼
┌─────────────────────────────────────────────────────────┐
│                Controller Layer (Spring MVC)             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ LoveApp      │  │ Manus        │  │ ChatHistory  │  │
│  │ Controller   │  │ Controller   │  │ Controller   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Agent Layer                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │              MonuoManus (Super Agent)             │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐ │  │
│  │  │ Thinking   │  │ ToolCall   │  │ Database   │ │  │
│  │  │ Agent      │  │ Agent      │  │ Memory     │ │  │
│  │  └────────────┘  └────────────┘  └────────────┘ │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │                LoveApp (Love Master)              │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐ │  │
│  │  │ RAG        │  │ Fallback   │  │ Report     │ │  │
│  │  │ Advisor    │  │ Strategy   │  │ Generator  │ │  │
│  │  └────────────┘  └────────────┘  └────────────┘ │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Tool Layer                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │ Web      │ │ File     │ │ Mail     │ │ PDF      │  │
│  │ Search   │ │ Operation│ │ Send     │ │ Generate │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │ Terminal │ │ Download │ │ Scraping │ │ Document │  │
│  │ Operation│ │ Resource │ │ Web      │ │ Reader   │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │         MCP Tools (Amap 15 tools)                 │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ MySQL        │  │ PostgreSQL   │  │ File System  │  │
│  │ (Chat Hist.) │  │ (Vector DB)  │  │ (Docs/Cache) │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

If you have Docker installed, this is the easiest way:

```bash
# 1. Set environment variables
export DASHSCOPE_API_KEY=your_api_key
export MYSQL_PASSWORD=your_password
export POSTGRESQL_PASSWORD=your_password

# 2. Start all services (App + MySQL + PostgreSQL)
docker-compose -f docker-compose.local.yml up --build

# 3. Access after startup
# Backend Swagger UI: http://localhost:8123/api/swagger-ui.html
# Frontend: http://localhost:5173
```

### Option 2: Manual Deployment

#### 1. Prerequisites

- ✅ Java 21+
- ✅ Node.js 18+
- ✅ Maven 3.8+
- ✅ MySQL 8.0+
- ✅ PostgreSQL 14+ (with PGVector extension)
- ✅ Alibaba Cloud DashScope API Key

#### 2. Clone Repository

```bash
git clone https://github.com/muonuo/Super-ai-agent.git
cd Super-ai-agent
```

#### 3. Configure Databases

**MySQL Configuration:**

```sql
-- Create database
CREATE DATABASE super_ai_agent CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tables will be created automatically
```

**PostgreSQL + PGVector Configuration:**

```sql
-- Create database
CREATE DATABASE super_ai_agent;

-- Install PGVector extension (Spring AI will auto-initialize vector tables)
CREATE EXTENSION IF NOT EXISTS vector;
```

#### 4. Configure Environment

Edit `src/main/resources/application-local.yaml`:

```yaml
# MySQL Configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/super_ai_agent
    username: root
    password: your_mysql_password

  # AI Configuration (Required)
  ai:
    dashscope:
      api-key: your_dashscope_api_key # Get from https://dashscope.console.aliyun.com/

# PostgreSQL Configuration
pgvector:
  datasource:
    url: jdbc:postgresql://localhost:5432/super_ai_agent
    username: postgres
    password: your_postgresql_password

# Optional Configuration
search-api:
  tavily-api-key: your_tavily_api_key # For web search
qq-email:
  from: your_qq_email
  auth-code: your_qq_auth_code # For sending love reports
```

> 💡 **Get DashScope API Key**:
>
> 1. Visit https://dashscope.console.aliyun.com/
> 2. Register/Login to Alibaba Cloud
> 3. Enable DashScope service
> 4. Create API Key
> 5. New users get free credits

#### 5. Start Backend

```bash
# Option 1: Using Maven (Recommended)
cd Super-ai-agent
mvn clean package -DskipTests
java -jar target/Super-ai-agent-0.0.1-SNAPSHOT.jar

# Option 2: Using IDE
# Run src/main/java/com/monuo/superaiagent/SuperAiAgentApplication.java
```

Backend will start at `http://localhost:8123/api`

#### 6. Start Frontend

```bash
cd super-ai-agent-web
npm install
npm run dev
```

Frontend will start at `http://localhost:5173`

#### 7. Access Application

Open browser and visit:

- **Frontend**: http://localhost:5173
- **Backend Swagger UI**: http://localhost:8123/api/swagger-ui.html
- **API Docs**: http://localhost:8123/api/v3/api-docs

---

## 🎯 Usage Examples

### AI Love Master

```
User: I've been with my girlfriend for 3 years, but recently she seems cold to me. What should I do?

AI: [Smart Mode + Love Report]
1. Detailed conversation analysis
2. Auto-generate relationship analysis report
3. Provide 3-5 actionable suggestions
4. Support download and copy report
```

### AI Super Agent

```
User: Help me search for today's AI news

AI: [Display Thinking Process]
💭 Thinking...
├─ Question Type: Complex
├─ User wants today's AI news
├─ Need to use webSearch tool
└─ Thinking Time: 1.2s

[Call Tool: webSearch]
[Return search results...]
```

---

## ❓ FAQ

### Q1: Port Already in Use

Modify port in `src/main/resources/application.yaml`:

```yaml
server:
  port: 8123 # Change to another port, e.g., 8124
```

### Q2: Database Connection Failed

Ensure MySQL and PostgreSQL services are running:

```bash
# Windows
net start MySQL80
net start postgresql-x64-14

# Linux/Mac
sudo systemctl start mysql
sudo systemctl start postgresql
```

### Q3: PGVector Extension Not Installed

PostgreSQL requires PGVector extension:

```bash
# Ubuntu/Debian
sudo apt install postgresql-14-pgvector

# macOS (Homebrew)
brew install pgvector

# Windows
# Download from https://github.com/pgvector/pgvector-windows/releases
```

### Q4: Maven Build Failed

Ensure Java 21 and Maven 3.8+:

```bash
java -version
mvn -version
```

Upgrade Maven:

```bash
# macOS
brew install maven

# Windows
# Download from https://maven.apache.org/download.cgi
```

### Q5: Cannot Find DashScope API Key

1. Visit https://dashscope.console.aliyun.com/
2. Register/Login to Alibaba Cloud
3. Enable DashScope service
4. Create API Key
5. New users get free credits

---

## 🛠️ Development Guide

### Project Structure

```
Super-ai-agent/
├── src/main/java/com/monuo/superaiagent/
│   ├── agent/              # Agent core
│   │   ├── BaseAgent.java
│   │   ├── ThinkingAgent.java
│   │   ├── ToolCallAgent.java
│   │   └── MonuoManus.java
│   ├── app/                # Application layer
│   │   └── LoveApp.java
│   ├── tools/              # Tool collection
│   ├── rag/                # RAG related
│   ├── controller/         # Controllers
│   ├── service/            # Service layer
│   └── config/             # Configuration
├── super-ai-agent-web/     # Frontend project
│   ├── src/
│   │   ├── views/          # Pages
│   │   ├── components/     # Components
│   │   ├── api/            # API interfaces
│   │   └── router/         # Routes
│   └── package.json
├── docs/                   # Documentation
└── docker-compose.yaml     # Docker configuration
```

### Add New Tool

**Step 1: Create Tool Class**

```java
@Component
public class MyTool {

    @Tool(description = "Tool description")
    public String myFunction(
        @ToolParam(description = "Parameter description") String param) {
        // Tool logic
        return "result";
    }
}
```

**Step 2: Register Tool**

```java
@Configuration
public class ToolRegistration {

    @Bean
    public List<ToolCallback> myTools(MyTool myTool) {
        return ToolCallback.from(myTool);
    }
}
```

### Add RAG Documents

Place Markdown documents in `src/main/resources/document/` directory, system will auto-load.

---

## 🤝 Contributing

Issues and Pull Requests are welcome!

1. Fork this repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Submit Pull Request

---

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file

---

## 🙏 Acknowledgments

- [Spring AI](https://spring.io/projects/spring-ai) - AI Integration Framework
- [Alibaba Cloud Bailian](https://www.aliyun.com/product/bailian) - AI Model Service
- [LangChain4j](https://github.com/langchain4j/langchain4j) - AI Orchestration Framework
- [PGVector](https://github.com/pgvector/pgvector) - PostgreSQL Vector Extension

---

## 📞 Contact

- GitHub: [@muonuo](https://github.com/muonuo)
- Project: [Super-ai-agent](https://github.com/muonuo/Super-ai-agent)
- Issues: [Report Issues](https://github.com/muonuo/Super-ai-agent/issues)

---

<div align="center">

**If this project helps you, please give it a ⭐ Star!**

Made with ❤️ by [Monuo](https://github.com/muonuo)

</div>
