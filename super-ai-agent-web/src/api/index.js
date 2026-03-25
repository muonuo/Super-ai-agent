import axios from "axios";

// 根据环境变量设置 API 基础 URL
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  (process.env.NODE_ENV === "production"
    ? "/api" // 生产环境使用相对路径，适用于前后端部署在同一域名下
    : "http://localhost:8123/api"); // 开发环境指向本地后端服务

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 300000,
});

export default apiClient;
