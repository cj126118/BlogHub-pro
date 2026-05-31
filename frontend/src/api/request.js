import axios from 'axios'
import { ElMessage } from 'element-plus'

// 访客 ID 生成
function getVisitorId() {
  let id = localStorage.getItem('blog_visitor_id')
  if (!id) {
    id = 'visitor_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 8)
    localStorage.setItem('blog_visitor_id', id)
  }
  return id
}

const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

// 是否正在刷新 Token
let isRefreshing = false
let refreshSubscribers = []

function onRefreshed(token) {
  refreshSubscribers.forEach((callback) => callback(token))
  refreshSubscribers = []
}

function addRefreshSubscriber(callback) {
  refreshSubscribers.push(callback)
}

// 请求拦截器 — 注入访客 ID + Token
request.interceptors.request.use(
  (config) => {
    config.headers['X-Visitor-Id'] = getVisitorId()
    const token = localStorage.getItem('blog_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器 — 统一解包 + 错误处理 + 自动刷新 Token
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== undefined && res.code !== 200 && res.code !== 201) {
      if (res.code === 401) {
        // 尝试刷新 Token
        const refreshToken = localStorage.getItem('blog_refresh_token')
        if (refreshToken && !isRefreshing) {
          isRefreshing = true
          return axios
            .post('/api/auth/refresh', { refreshToken })
            .then((res2) => {
              const data = res2.data.data || res2.data
              const newToken = data.token
              const newRefreshToken = data.refreshToken
              localStorage.setItem('blog_token', newToken)
              localStorage.setItem('blog_refresh_token', newRefreshToken)
              isRefreshing = false
              onRefreshed(newToken)
              // 重试原请求
              const originalRequest = response.config
              originalRequest.headers['Authorization'] = `Bearer ${newToken}`
              return request(originalRequest)
            })
            .catch(() => {
              isRefreshing = false
              refreshSubscribers = []
              // 刷新失败，清除登录态
              localStorage.removeItem('blog_token')
              localStorage.removeItem('blog_refresh_token')
              localStorage.removeItem('blog_username')
              localStorage.removeItem('blog_nickname')
              localStorage.removeItem('blog_role')
              localStorage.removeItem('blog_userId')
              window.location.href = '/login'
              return Promise.reject(new Error('登录已过期，请重新登录'))
            })
        } else if (refreshToken && isRefreshing) {
          // 正在刷新中，排队等待
          return new Promise((resolve) => {
            addRefreshSubscriber((newToken) => {
              const originalRequest = response.config
              originalRequest.headers['Authorization'] = `Bearer ${newToken}`
              resolve(request(originalRequest))
            })
          })
        } else {
          // 没有 refresh token，直接跳转登录
          localStorage.clear()
          window.location.href = '/login'
          return Promise.reject(new Error('未授权'))
        }
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data !== undefined ? res.data : res
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      const msg = data?.message || getStatusText(status)
      ElMessage.error(msg)
    } else if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请检查网络')
    } else {
      ElMessage.error('网络异常')
    }
    return Promise.reject(error)
  }
)

function getStatusText(status) {
  const map = {
    400: '请求参数错误',
    401: '未授权',
    403: '禁止访问',
    404: '资源不存在',
    422: '参数校验失败',
    429: '请求过于频繁',
    500: '服务器内部错误',
    503: '服务不可用',
  }
  return map[status] || `请求失败 (${status})`
}

export default request
