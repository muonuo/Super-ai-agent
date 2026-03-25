# Super AI Agent

基于 Spring AI 的智能对话 Web 应用，提供 AI 恋爱大师与多模态 Agent 服务。

## 功能特性

### AI 恋爱大师
- 智能情感顾问，为用户提供恋爱建议和关系咨询
- 流式输出，实时显示 AI 回复
- 优雅的对话界面

### AI 超级智能体 (Manus)
- 支持 Tool Calling 工具调用
- RAG 知识增强
- 多模态交互
- 展示 AI 思考过程

## 技术栈

- **前端框架**: Vue 3
- **路由**: Vue Router
- **HTTP 客户端**: Axios
- **构建工具**: Vite
- **后端**: Spring AI

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
super-ai-agent-web/
├── src/
│   ├── components/     # 公共组件
│   ├── views/          # 页面视图
│   │   ├── HomeView.vue        # 首页
│   │   ├── LoveAppView.vue     # AI恋爱大师
│   │   └── ManusView.vue       # AI超级智能体
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── public/             # 静态资源
├── index.html          # HTML 入口
├── vite.config.js     # Vite 配置
└── package.json       # 项目配置
```

## 配置说明

在 `.env.production` 文件中配置后端 API 地址。

## License

MIT
