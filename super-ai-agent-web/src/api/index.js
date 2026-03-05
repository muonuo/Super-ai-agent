import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:8123/api',
  timeout: 300000,
})

export default apiClient
