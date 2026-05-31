import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'

export const useAuthStore = defineStore('auth', () => {
  // 从 localStorage 恢复状态
  const token = ref(localStorage.getItem('blog_token') || '')
  const refreshToken = ref(localStorage.getItem('blog_refresh_token') || '')
  const username = ref(localStorage.getItem('blog_username') || '')
  const nickname = ref(localStorage.getItem('blog_nickname') || '')
  const avatar = ref(localStorage.getItem('blog_avatar') || '')
  const role = ref(localStorage.getItem('blog_role') || '')
  const userId = ref(localStorage.getItem('blog_userId') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')

  /**
   * 持久化用户信息到 localStorage
   */
  function saveUser(data) {
    token.value = data.token || ''
    refreshToken.value = data.refreshToken || ''
    username.value = data.username || ''
    nickname.value = data.nickname || ''
    avatar.value = data.avatar || ''
    role.value = data.role || ''
    userId.value = data.userId ? String(data.userId) : ''

    localStorage.setItem('blog_token', token.value)
    localStorage.setItem('blog_refresh_token', refreshToken.value)
    localStorage.setItem('blog_username', username.value)
    localStorage.setItem('blog_nickname', nickname.value)
    localStorage.setItem('blog_avatar', avatar.value)
    localStorage.setItem('blog_role', role.value)
    localStorage.setItem('blog_userId', userId.value)
  }

  /**
   * 登录
   */
  async function login(user, password) {
    const res = await authApi.login({ username: user, password })
    saveUser(res)
    return res
  }

  /**
   * 注册
   */
  async function register(user, password) {
    const res = await authApi.register({ username: user, password })
    saveUser(res)
    return res
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    refreshToken.value = ''
    username.value = ''
    nickname.value = ''
    avatar.value = ''
    role.value = ''
    userId.value = ''

    const keys = [
      'blog_token', 'blog_refresh_token', 'blog_username',
      'blog_nickname', 'blog_avatar', 'blog_role', 'blog_userId',
    ]
    keys.forEach((k) => localStorage.removeItem(k))
  }

  return {
    token, refreshToken, username, nickname, avatar, role, userId,
    isLoggedIn, isAdmin,
    login, register, logout, saveUser,
  }
})
