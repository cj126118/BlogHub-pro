import request from './request'

export const authApi = {
  /**
   * 用户登录
   */
  login(data) {
    return request.post('/auth/login', data)
  },

  /**
   * 用户注册
   */
  register(data) {
    return request.post('/auth/register', data)
  },

  /**
   * 刷新 Token
   */
  refresh(refreshToken) {
    return request.post('/auth/refresh', { refreshToken })
  },

  /**
   * 获取当前用户信息
   */
  me() {
    return request.get('/auth/me')
  },
}
