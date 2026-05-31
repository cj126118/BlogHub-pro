# 项目规格文档 — BlogHub

> 本文档描述一个完整的 Vue 3 + Element Plus 博客前端项目的全部规范、约定和代码模式。
> **用途**: agent 在创建新项目时按此文档自动生成代码，无需再人工翻阅源代码。
> **生成依据**: https://github.com/your-org/vue3-elementplus-starter

---

## 1. 技术栈（精确到次版本）

| 层 | 技术 | 版本约束 | 用途 |
|---|---|---|---|
| 框架 | Vue 3 | ^3.4.x | Composition API, `<script setup>` |
| 构建 | Vite | ^5.2.x | 快速 HMR |
| UI 库 | Element Plus | ^2.6.x | 所有基础组件 |
| 图标 (EP) | `@element-plus/icons-vue` | ^2.3.x | `<el-icon>` 标签使用 |
| 图标 (BI) | `bootstrap-icons` | ^1.13.x | `<i class="bi bi-xxx">` 标签使用 |
| 状态管理 | Pinia | ^2.1.x | `defineStore` + Composition API |
| 路由 | Vue Router | ^4.3.x | `createWebHistory` 模式 |
| HTTP | Axios | ^1.16.x | 统一拦截器 |
| Markdown | markdown-it | ^14.1.x | 前端渲染 |
| 图表 | ECharts | ^6.1.x | 仪表盘/趋势图 |
| 前端解析 | — | — | 手动解析 frontmatter（不用 gray-matter） |

**禁止事项**:
- ❌ 禁止 TypeScript（全 JS）
- ❌ 禁止 CSS 预处理器（纯 CSS，scoped）
- ❌ 禁止 Options API（全 Composition API + `<script setup>`）
- ❌ 禁止在组件中直接写后端地址（通过 API 模块调用）

---

## 2. 项目脚手架

### 2.1 package.json

```json
{
  "name": "bloghub",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "element-plus": "^2.6.1",
    "@element-plus/icons-vue": "^2.3.1",
    "axios": "^1.16.1",
    "markdown-it": "^14.1.1",
    "echarts": "^6.1.0",
    "bootstrap-icons": "^1.13.1"
  },
  "devDependencies": {
    "vite": "^5.2.0",
    "@vitejs/plugin-vue": "^5.0.4",
    "eslint": "^8.57.0",
    "eslint-plugin-vue": "^9.24.0"
  }
}
```

### 2.2 vite.config.js

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

### 2.3 index.html

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes" />
  <title>BlogHub</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

### 2.4 环境变量文件

**`.env`**:
```
VITE_APP_TITLE=BlogHub
VITE_APP_BASE_API=/api
```

**`.env.development`**:
```
VITE_APP_ENV=development
VITE_APP_BASE_URL=http://localhost:3000
```

**`.env.production`**:
```
VITE_APP_ENV=production
VITE_APP_BASE_URL=https://your-domain.com
```

---

## 3. 目录结构（必须严格遵循）

```
project-root/
├── index.html
├── vite.config.js
├── package.json
├── .env / .env.development / .env.production
├── public/
│   └── favicon.svg
└── src/
    ├── main.js                  # 应用入口
    ├── App.vue                  # 根组件
    ├── router/
    │   └── index.js             # 路由配置 + 守卫
    ├── stores/                  # Pinia stores
    │   ├── auth.js
    │   └── counter.js
    ├── api/                     # 所有 API 调用
    │   ├── request.js           # Axios 实例 + 拦截器
    │   ├── authApi.js
    │   ├── postApi.js
    │   ├── commentApi.js
    │   ├── interactionApi.js
    │   ├── albumApi.js
    │   ├── tagApi.js
    │   └── hotTopicApi.js
    ├── composables/             # 可复用逻辑
    │   ├── useApi.js
    │   ├── usePosts.js
    │   ├── useInteraction.js
    │   └── useMarkdown.js
    ├── components/              # 公共组件
    │   ├── BlogCard.vue
    │   ├── BlogActionBar.vue
    │   ├── BlogToc.vue
    │   ├── BlogComments.vue
    │   ├── BlogReadingProgress.vue
    │   ├── ReadingProgress.vue
    │   ├── ImageLightbox.vue
    │   ├── SkeletonCard.vue
    │   └── TagSelector.vue
    ├── layouts/
    │   └── MainLayout.vue       # 主布局
    ├── views/                   # 页面（懒加载）
    │   ├── Home.vue
    │   ├── Login.vue
    │   ├── BlogList.vue
    │   ├── BlogPost.vue
    │   ├── PostEditor.vue
    │   ├── PostEdit.vue
    │   ├── Archive.vue
    │   ├── SearchResults.vue
    │   ├── TagCloud.vue
    │   ├── Dashboard.vue
    │   ├── Profile.vue
    │   ├── MyPosts.vue
    │   ├── UserManage.vue
    │   ├── CommentManage.vue
    │   ├── ImageManage.vue
    │   ├── LogManage.vue
    │   ├── AlbumList.vue
    │   ├── AlbumDetail.vue
    │   ├── Bookmarks.vue
    │   ├── HotTopics.vue
    │   ├── About.vue
    │   └── UserProfile.vue
    ├── styles/
    │   └── global.css           # 全局样式
    └── utils/
        └── tracker.js           # 前端埋点
```

---

## 4. 入口文件（src/main.js）

```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import 'bootstrap-icons/font/bootstrap-icons.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './styles/global.css'

// 必须：Edge/Chromium 焦点 bug 修复
;(function patchHistoryFocusBug() {
  const origReplaceState = window.history.replaceState.bind(window.history)
  const origPushState = window.history.pushState.bind(window.history)
  window.history.replaceState = function () {
    if (document.hidden) return
    return origReplaceState.apply(this, arguments)
  }
  window.history.pushState = function () {
    if (document.hidden) return
    return origPushState.apply(this, arguments)
  }
})()

const app = createApp(App)

// 必须：全量注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
```

---

## 5. 根组件（src/App.vue）

```vue
<template>
  <router-view />
</template>

<script setup>
// 必须：修复 Element Plus 给 body 加 aria-hidden 触发 Chrome 警告
import { onMounted } from 'vue'

onMounted(() => {
  const observer = new MutationObserver(() => {
    if (document.body.getAttribute('aria-hidden')) {
      document.body.removeAttribute('aria-hidden')
    }
  })
  observer.observe(document.body, { attributes: true, attributeFilter: ['aria-hidden'] })
})
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}
</style>
```

---

## 6. 全局样式（src/styles/global.css）

```css
*,
*::before,
*::after {
  box-sizing: border-box;
}

html {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 滚动条美化 */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background-color: #c0c4cc; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background-color: #909399; }

/* Element Plus 自定义覆盖 */
.el-card { border-radius: 8px; }
.el-menu { border-right: none; }
```

---

## 7. 路由规范（src/router/index.js）

### 7.1 路由模式
```js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes,
})
```

### 7.2 路由元信息字段

| 字段 | 类型 | 必填 | 含义 |
|---|---|---|---|
| `title` | String | ✅ | 页面标题，自动设为 `document.title` |
| `public` | Boolean | — | 公开页面（如登录），无需认证 |
| `requiresAuth` | Boolean | — | 需要登录 |
| `requiresAdmin` | Boolean | — | 需要 admin 角色 |
| `roles` | String[] | — | 角色白名单 |

### 7.3 路由守卫逻辑

```js
router.beforeEach((to, from, next) => {
  // 1. 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} | BlogHub` : 'BlogHub'

  // 2. Meta 描述
  setMetaDescription(to.meta.title)

  const token = localStorage.getItem('blog_token')
  const role = localStorage.getItem('blog_role')

  // 3. 需要登录但无 token → 跳转登录
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 4. 需要管理员但角色不是 admin → 跳主页
  if (to.meta.requiresAdmin && role !== 'admin') {
    next('/')
    return
  }

  // 5. 已登录用户访问登录页 → 跳主页
  if (to.path === '/login' && token) {
    next('/')
    return
  }

  next()
})
```

### 7.4 路由路径命名规则

| 路径模式 | 示例 |
|---|---|
| 普通页面 | `/home`, `/about`, `/tags` |
| 列表页 | `/blog`, `/albums` |
| 详情页 | `/blog/:slug`, `/albums/:id` |
| 编辑页 | `/blog/:slug/edit` |
| 用户页 | `/user/:id` |
| 搜索页 | `/search`（带 query `?q=xxx`） |

### 7.5 路由定义模板

```js
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/home',
      },
      {
        path: 'blog',
        name: 'BlogList',
        component: () => import('@/views/BlogList.vue'),
        meta: { title: '博客' },
      },
      {
        path: 'blog/:slug',
        name: 'BlogPost',
        component: () => import('@/views/BlogPost.vue'),
        meta: { title: '文章详情' },
      },
      // ...其他子路由
    ],
  },
]
```

⚠️ **所有视图组件必须使用 `() => import('@/views/Xxx.vue')` 懒加载。**

---

## 8. API 层规范（src/api/）

### 8.1 request.js — Axios 实例（一次性写好，不可变动）

```js
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
  baseURL: 'http://localhost:8080/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

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

// 响应拦截器 — 统一解包 Result<T> + 错误处理
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== undefined && res.code !== 200 && res.code !== 201) {
      if (res.code === 401) {
        // 清除登录态
        localStorage.removeItem('blog_token')
        localStorage.removeItem('blog_username')
        localStorage.removeItem('blog_nickname')
        localStorage.removeItem('blog_role')
        window.location.href = '/login'
        return Promise.reject(new Error(res.message || '未授权'))
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
    400: '请求参数错误', 401: '未授权', 403: '禁止访问',
    404: '资源不存在', 422: '参数校验失败', 500: '服务器内部错误',
    503: '服务不可用',
  }
  return map[status] || `请求失败 (${status})`
}

export default request
```

### 8.2 API 模块写法模板

```js
import request from './request'

export const postApi = {
  list(params)         { return request.get('/posts', { params }) },
  detail(slug)         { return request.get(`/posts/${slug}`) },
  create(data)         { return request.post('/posts', data) },
  update(id, data)     { return request.put(`/posts/${id}`, data) },
  delete(id)           { return request.delete(`/posts/${id}`) },
}
```

**规则**:
- 文件名: `xxxApi.js`（camelCase）
- 导出: `export const xxxApi = { ... }`
- 每个方法 return `request.verb(url, ...)`
- 后端响应格式统一为 `Result<T>` = `{ code, message, data }`

### 8.3 后端接口约定

| 项 | 约定 |
|---|---|
| 基准地址 | `http://localhost:8080/api` |
| 响应格式 | `{ code: number, message: string, data: T }` |
| 分页响应 | `{ records: [], list: [], total: number, page: number, size: number }` |
| 认证 | Bearer Token |
| 访客标识 | Header `X-Visitor-Id` |
| 静态资源 | `http://localhost:8080`（无 `/api` 前缀） |

---

## 9. 认证 & 状态管理（src/stores/）

### 9.1 Auth Store（必须完整复制）

```js
import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('blog_token') || '')
  const username = ref(localStorage.getItem('blog_username') || '')
  const nickname = ref(localStorage.getItem('blog_nickname') || '')
  const avatar = ref(localStorage.getItem('blog_avatar') || '')
  const role = ref(localStorage.getItem('blog_role') || '')
  const userId = ref(localStorage.getItem('blog_userId') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')

  function saveUser(t, u, n, a, r, id) {
    token.value = t; username.value = u; nickname.value = n
    avatar.value = a || ''; role.value = r; userId.value = id || ''
    localStorage.setItem('blog_token', t)
    localStorage.setItem('blog_username', u)
    localStorage.setItem('blog_nickname', n)
    localStorage.setItem('blog_avatar', a || '')
    localStorage.setItem('blog_role', r)
    if (id) localStorage.setItem('blog_userId', id)
  }

  async function login(user, password) {
    const res = await authApi.login({ username: user, password })
    saveUser(res.token, res.username, res.nickname, res.avatar, res.role, res.userId)
    return res
  }

  async function register(user, password) {
    const res = await authApi.register({ username: user, password })
    saveUser(res.token, res.username, res.nickname, res.avatar, res.role, res.userId)
    return res
  }

  function logout() {
    token.value = username.value = nickname.value = avatar.value = role.value = userId.value = ''
    ;['blog_token','blog_username','blog_nickname','blog_avatar','blog_role','blog_userId']
      .forEach(k => localStorage.removeItem(k))
  }

  return { token, username, nickname, avatar, role, userId, isLoggedIn, isAdmin,
           login, register, logout, saveUser }
})
```

### 9.2 localStorage Key 一览

| Key | 存储内容 |
|---|---|
| `blog_token` | JWT Token |
| `blog_username` | 用户名 |
| `blog_nickname` | 昵称 |
| `blog_avatar` | 头像路径 |
| `blog_role` | 角色（`admin` / 空） |
| `blog_userId` | 用户 ID |
| `blog_visitor_id` | 访客 UUID |
| `code-theme` | 代码主题（`dark` / `light`） |
| `draft_autosave` | 编辑器自动保存的草稿 |

---

## 10. Composables 规范（src/composables/）

### 10.1 useApi — 通用异步三态管理

```js
import { ref } from 'vue'

/**
 * @param {Function} fn  返回 Promise 的异步函数
 * @param {Object}   options
 * @param {boolean}  options.immediate  是否立即执行
 * @param {any}      options.initial    初始 data 值
 * @returns {{ data, loading, error, execute }}
 */
export function useApi(fn, options = {}) {
  const data = ref(options.initial ?? null)
  const loading = ref(false)
  const error = ref(null)

  async function execute(...args) {
    loading.value = true
    error.value = null
    try {
      const result = await fn(...args)
      data.value = result
      return result
    } catch (e) {
      error.value = e
      throw e
    } finally {
      loading.value = false
    }
  }

  if (options.immediate) execute()
  return { data, loading, error, execute }
}
```

**用法**: `const { data, loading, error, execute } = useApi(() => request.get('/users'))`

### 10.2 usePosts — 文章数据

```js
import { ref } from 'vue'
import { postApi } from '@/api/postApi'
import { markdownToHtml } from './useMarkdown'

function formatPost(raw) {
  // 必须：将后端原始数据转换为前端格式
  const markdown = raw.content || ''
  let html = ''
  try {
    const rendered = markdownToHtml(markdown)
    html = rendered.html || ''
  } catch (e) {
    html = '<pre style="white-space:pre-wrap;background:#f5f7fa;padding:16px;border-radius:8px">'
         + markdown.replace(/</g, '&lt;').replace(/>/g, '&gt;') + '</pre>'
  }
  return {
    id: raw.id,
    slug: raw.slug,
    title: raw.title || raw.slug,
    date: raw.createdAt ? raw.createdAt.slice(0, 10) : '',
    tags: raw.tags ? raw.tags.split(',').map(s => s.trim()).filter(Boolean) : [],
    description: raw.description || '',
    html,
    content: markdown,
    views: raw.views || 0,
    likes: raw.likes || 0,
    author: raw.authorName || '',
    authorId: raw.authorId || null,
    authorAvatar: raw.authorAvatar || '',
    coverImage: raw.coverImage || '',
    isPinned: raw.isPinned || 0,
  }
}

export function usePosts() {
  const posts = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchPosts(tag, keyword) {
    loading.value = true; error.value = null
    try {
      const params = { page: 1, size: 50 }
      if (tag) params.tag = tag
      if (keyword) params.keyword = keyword
      const res = await postApi.list(params)
      const list = res.list || []
      posts.value = list.map(formatPost)
    } catch (e) {
      error.value = e.message || '加载文章失败'
      posts.value = []
    } finally { loading.value = false }
  }

  async function getPost(slug) {
    loading.value = true; error.value = null
    try {
      const raw = await postApi.detail(slug)
      return formatPost(raw)
    } catch (e) {
      error.value = e.message || '加载文章失败'
      return null
    } finally { loading.value = false }
  }

  function getTags() {
    const tagSet = new Set()
    posts.value.forEach(p => p.tags.forEach(t => tagSet.add(t)))
    return [...tagSet]
  }

  return { posts, loading, error, fetchPosts, getPost, getTags }
}
```

### 10.3 useInteraction — 互动功能

```js
import { ref, computed } from 'vue'
import { interactionApi } from '@/api/interactionApi'
import { commentApi } from '@/api/commentApi'

// 点赞
export function useLikes(slug) {
  const liked = ref(false)
  const count = ref(0)

  async function fetchStatus() {
    try {
      const res = await interactionApi.getLikeStatus(slug)
      liked.value = res.liked
      count.value = res.count
    } catch { /* 静默 */ }
  }

  async function toggleLike() {
    try {
      const res = await interactionApi.toggleLike(slug)
      liked.value = res.liked
      count.value = res.count
    } catch { /* 静默 */ }
  }

  fetchStatus()
  return { liked, count, toggleLike }
}

// 收藏
export function useBookmarks(slug) {
  const allBookmarks = ref([])
  const bookmarked = computed(() => slug ? allBookmarks.value.includes(slug) : false)
  const bookmarksList = computed(() => allBookmarks.value)

  async function fetchBookmarks() {
    try {
      const res = await interactionApi.getBookmarks()
      allBookmarks.value = (res || []).map(p => p.slug)
    } catch { /* 静默 */ }
  }

  async function toggleBookmark() {
    try {
      const res = await interactionApi.toggleBookmark(slug)
      if (res.bookmarked) {
        if (!allBookmarks.value.includes(slug)) allBookmarks.value.push(slug)
      } else {
        allBookmarks.value = allBookmarks.value.filter(s => s !== slug)
      }
    } catch { /* 静默 */ }
  }

  function isBookmarked(s) { return allBookmarks.value.includes(s) }

  fetchBookmarks()
  return { bookmarked, bookmarksList, toggleBookmark, isBookmarked }
}

// 评论
export function useComments(slug) {
  const comments = ref([])

  async function fetchComments() {
    try {
      const res = await commentApi.list(slug)
      comments.value = res || []
    } catch { comments.value = [] }
  }

  async function addComment(nickname, content) {
    if (!nickname.trim() || !content.trim()) return
    try {
      await commentApi.create(slug, { nickname, content })
      await fetchComments()
    } catch { /* 拦截器已提示 */ }
  }

  fetchComments()
  return { comments, addComment, fetchComments }
}
```

### 10.4 useMarkdown — Markdown 渲染

```js
import MarkdownIt from 'markdown-it'

let md = null
function getMd() {
  if (!md) {
    md = new MarkdownIt({
      html: true,
      linkify: true,
      typographer: true,
      highlight(str, lang) {
        const language = lang || ''
        const escaped = str
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
        return `<pre class="language-${language}"><code class="language-${language}">${escaped}</code></pre>`
      },
    })
  }
  return md
}

// 手动解析 frontmatter（不依赖 gray-matter）
export function parseFrontmatter(raw) {
  const result = { frontmatter: {}, content: raw }
  const match = raw.match(/^---\r?\n([\s\S]*?)\r?\n---\r?\n/)
  if (!match) return result

  const fmRaw = match[1]
  const content = raw.slice(match[0].length)
  const frontmatter = {}

  for (const line of fmRaw.split('\n')) {
    const sep = line.indexOf(':')
    if (sep === -1) continue
    const key = line.slice(0, sep).trim()
    let val = line.slice(sep + 1).trim()
    if ((val.startsWith("'") && val.endsWith("'")) ||
        (val.startsWith('"') && val.endsWith('"'))) {
      val = val.slice(1, -1)
    } else if (val.startsWith('[') && val.endsWith(']')) {
      val = val.slice(1, -1).split(',').map(s => s.trim().replace(/^['"]|['"]$/g, ''))
    }
    frontmatter[key] = val
  }

  result.frontmatter = frontmatter
  result.content = content
  return result
}

export function markdownToHtml(raw) {
  const { frontmatter, content } = parseFrontmatter(raw)
  const html = getMd().render(content)
  return { html, frontmatter }
}
```

---

## 11. 布局规范（src/layouts/MainLayout.vue）

**结构**:
```
ReadingProgress（全局顶部阅读进度条）
el-container (height:100%)
├── el-aside (默认 220px, 折叠 64px)
│   ├── Logo (B + BlogHub)
│   └── el-menu (router, collapse-toggle)
└── el-container
    ├── el-header (56px)
    │   ├── 折叠按钮 + 面包屑导航
    │   └── 右侧: 写文章按钮 / 通知铃铛 / 登录或用户下拉菜单
    └── el-main (bg:#f5f7fa, padding:24px)
        └── <router-view />
```

**侧边栏菜单结构**:
1. 首页 `/home`
2. 博客 `/blog`
3. 我的文章 `/my-posts`
4. 写文章 `/blog/new`
5. 标签云 `/tags`
6. 文章归档 `/archive`
7. 分隔线
8. 仪表盘 `/dashboard` (admin only)
9. 用户管理 `/users` (admin only)
10. 评论管理 `/comments` (admin only)
11. 操作日志 `/logs` (admin only)
12. 图片管理 `/images` (admin only)
13. 收藏 `/bookmarks`
14. 宝宝相册 `/albums`
15. 每日热点 `/hot`
16. 关于 `/about`

**面包屑生成**: 基于 `route.path` 的预配置字典，非匹配路径自动 fallback。

---

## 12. 组件规范

### 12.1 通用组件模式

每个组件文件 = `<template>` + `<script setup>` + `<style scoped>`。

**三态渲染模式**:
```vue
<div v-if="loading">
  <el-skeleton :rows="3" animated />
</div>
<template v-else-if="data.length">
  <!-- 内容 -->
</template>
<el-empty v-else description="暂无数据" :image-size="60" />
```

**Props 规范**:
```js
defineProps({
  slug: String,
  title: String,
  tags: {
    type: Array,
    default: () => [],
  },
  isBookmarked: { type: Boolean, default: false },
  isPinned: { type: Boolean, default: false },
})
```

### 12.2 核心组件清单

| 组件 | Props | 功能 |
|---|---|---|
| `BlogCard` | `slug, title, date, tags[], coverImage, description, author, likes, views, isBookmarked, isPinned` | 文章卡片，hover 上移效果 |
| `BlogActionBar` | `slug` | 侧边浮动栏：点赞/收藏/分享/评论/回顶部 |
| `BlogToc` | `selector` (默认 `.markdown-body`) | 文章目录，IntersectionObserver 高亮 |
| `BlogComments` | `slug` | 评论区（输入框 + 嵌套回复） |
| `ReadingProgress` | — | 全局顶部 3px 阅读进度条 |
| `BlogReadingProgress` | — | 文章页 3px 进度条 |
| `ImageLightbox` | `expose: open(src, alt)`, `emit: close` | 图片点击放大灯箱 |
| `SkeletonCard` | — | 骨架屏卡片 |
| `TagSelector` | `v-model` (逗号分隔字符串) | 标签输入+搜索+删除 |

---

## 13. 页面模板

### 13.1 列表页模板

```vue
<template>
  <div class="xxx-list">
    <div class="page-header">
      <h1>页面标题</h1>
    </div>

    <!-- 加载中 -->
    <el-row :gutter="20" v-if="loading">
      <el-col v-for="i in 6" :key="i" :xs="24" :sm="12" :lg="8">
        <SkeletonCard />
      </el-col>
    </el-row>

    <!-- 数据列表 -->
    <el-row :gutter="20" v-else-if="items.length">
      <el-col v-for="item in items" :key="item.id" :xs="24" :sm="12" :lg="8">
        <!-- 卡片内容 -->
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-else description="暂无数据" :image-size="60" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const items = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    // fetch data
  } catch { /* handle */ }
  finally { loading.value = false }
})
</script>

<style scoped>
.xxx-list { max-width: 1200px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
</style>
```

### 13.2 表单页模板（编辑器/设置）

```vue
<template>
  <div class="xxx-editor">
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="正文" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="12" />
      </el-form-item>
      <div class="form-actions">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交</el-button>
      </div>
    </el-form>
  </div>
</template>
```

### 13.3 顶部操作栏样式模式

```css
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
.form-actions-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
```

---

## 14. CSS 样式体系

### 14.1 颜色系统

| 用途 | 色值 |
|---|---|
| Primary | `#409eff` |
| Primary hover | `#337ecc` |
| Primary light bg | `#ecf5ff` |
| 标题/正文 | `#303133` |
| 次要文字 | `#909399` |
| 弱文字 | `#c0c4cc` |
| 页面背景 | `#f5f7fa` |
| 白色背景 | `#fff` |
| 边框 | `#e8eaed` / `#dcdfe6` / `#f0f0f0` |
| Danger | `#f56c6c` |
| Warning | `#e6a23c` |
| Success | `#67c23a` |

### 14.2 圆角系统

| 层级 | 值 |
|---|---|
| 大卡片 | `12px` / `16px` |
| 小卡片 | `8px` / `10px` |
| 按钮/标签 | `4px` / `6px` |
| 头像/小圆 | `50%` |

### 14.3 阴影系统

```css
/* 默认卡片 */
box-shadow: none;

/* Hover 卡片 */
box-shadow: 0 4px 12px rgba(0,0,0,0.06);
box-shadow: 0 6px 20px rgba(0,0,0,0.06);

/* 弹出面板 */
box-shadow: 0 4px 20px rgba(0,0,0,0.1);
```

### 14.4 过渡动画

```css
/* 通用 */
transition: all 0.2s;

/* 卡片 hover */
transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
transform: translateY(-2px) / translateY(-3px);

/* 侧边栏折叠 */
transition: width 0.25s ease;
```

---

## 15. 样式间距指南

| 上下文 | 值 |
|---|---|
| Card body padding（PC） | `40px` |
| Card body padding（Mobile） | `24px 20px` |
| 页面内上下间距 | `20px` |
| 表单行间距 | `4-8px` |
| 侧边栏展开宽度 | `220px` |
| 侧边栏折叠宽度 | `64px` |
| 顶栏高度 | `56px` |

---

## 16. 关键功能实现模式

### 16.1 图片上传

```js
// 1. 创建 FormData
const formData = new FormData()
formData.append('file', file)

// 2. 通过 request 上传（需覆盖 Content-Type）
const res = await request.post('/upload', formData, {
  headers: { 'Content-Type': undefined }
})

// 3. 获取 URL 并插入编辑器
const url = 'http://localhost:8080' + res.url  // res.url = /uploads/xxx.jpg
form.value.content += `\n![image](${url})\n`
```

支持拖拽、粘贴、按钮三种上传方式。

### 16.2 自动保存草稿

```js
const DRAFT_KEY = 'draft_autosave'

// 内容变化后 30 秒自动保存
watch(() => [form.value.content, form.value.title], () => {
  clearTimeout(autoSaveTimer)
  autoSaveTimer = setTimeout(saveDraft, 30000)
}, { deep: false })

// Ctrl+S 快捷键
function handleKeydown(e) {
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault()
    saveDraft()
    ElMessage.success('草稿已保存')
  }
}

// 页面加载时恢复草稿
onMounted(() => {
  const raw = localStorage.getItem(DRAFT_KEY)
  if (raw) {
    const draft = JSON.parse(raw)
    if (draft.content || draft.title) {
      ElMessage.info('检测到未保存的草稿，内容已自动恢复')
      // 恢复所有表单字段
    }
  }
})
```

### 16.3 阅读进度条

```vue
<template>
  <div class="reading-progress" :style="{ transform: `scaleX(${progress})` }" />
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const progress = ref(0)
let ticking = false

function update() {
  const scrollTop = window.scrollY
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  progress.value = docHeight > 0 ? scrollTop / docHeight : 0
}

function onScroll() {
  if (!ticking) {
    requestAnimationFrame(() => { update(); ticking = false })
    ticking = true
  }
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #409eff, #337ecc);
  transform-origin: left;
  z-index: 9999;
}
</style>
```

### 16.4 图片灯箱（Teleport）

```vue
<template>
  <Teleport to="body">
    <Transition name="lightbox">
      <div v-if="visible" class="lightbox-overlay" @click.self="close" @keydown.esc="close" tabindex="0">
        <button class="lightbox-close" @click="close">&times;</button>
        <img :src="src" class="lightbox-img" />
      </div>
    </Transition>
  </Teleport>
</template>
```

### 16.5 前端埋点

```js
const TRACK_URL = '/api/stats/track'

let visitorId = localStorage.getItem('visitor_id')
if (!visitorId) {
  visitorId = 'v_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8)
  localStorage.setItem('visitor_id', visitorId)
}

export function track(event, data = {}) {
  try {
    navigator.sendBeacon(TRACK_URL, JSON.stringify({
      event, data, url: location.href, visitorId, ts: Date.now(),
    }))
  } catch { /* 静默 */ }
}

export function trackPageView() {
  track('page_view', { title: document.title })
}
```

### 16.6 分享功能

```js
function shareTo(platform) {
  const url = encodeURIComponent(window.location.href)
  const title = encodeURIComponent(document.title)
  switch (platform) {
    case 'weibo':
      window.open(`https://service.weibo.com/share/share.php?title=${title}&url=${url}`, '_blank', 'width=600,height=500')
      break
    case 'zhihu':
      window.open(`https://zhuanlan.zhihu.com/write?title=${title}`, '_blank')
      break
    case 'copy':
      navigator.clipboard.writeText(window.location.href).then(() => ElMessage.success('链接已复制'))
      break
    case 'wechat':
      ElMessage.info('请在微信内打开此页面分享给好友')
      break
  }
}
```

---

## 17. 响应式断点

| 断点 | 说明 |
|---|---|
| `1200px+` | 桌面：显示侧边浮动栏 + 目录 |
| `768px` | 平板：Card 内边距 24px |
| 默认 | `el-col` 响应式：`xs:24 sm:12 lg:8` 三列布局 |

---

## 18. 生成代码检查清单

agent 生成代码完成后，逐项检查：

- [ ] 根组件/入口按规范生成
- [ ] Vite 配置包含 `@/` 别名和 `/uploads` 代理
- [ ] 所有 Vue 组件使用 `<script setup>` + Composition API
- [ ] 无 TypeScript
- [ ] 无 CSS 预处理器，全部 `<style scoped>`
- [ ] 路由懒加载 `() => import(...)`
- [ ] 路由 meta 包含 `title` 字段
- [ ] API 模块导出格式 `export const xxxApi = { ... }`
- [ ] request.js 正确实现 axios 拦截器（访客 ID + Token + 统一错误处理）
- [ ] 每个 list 页面有三态：loading（骨架屏）→ data（列表）→ empty（空状态）
- [ ] `defineProps` 显式声明 `type` 和 `default`
- [ ] 后端 URL 使用 `http://localhost:8080/api` 前缀
- [ ] 静态资源使用 `http://localhost:8080` 前缀
- [ ] localStorage key 统一以 `blog_` 为前缀
- [ ] 颜色值使用标准色板（Primary `#409eff`，背景 `#f5f7fa` 等）
- [ ] 卡片圆角 `12px`，边框 `1px solid #e8eaed`
- [ ] 图标: Element Plus 图标用 `<el-icon>`，Bootstrap Icons 用 `<i class="bi bi-xxx">`
- [ ] 文件/目录命名规范：JS/Pug 文件 camelCase，Vue 组件 PascalCase

---

## 19. 后端 API 路径参考（RESTful）

| 方法 | 路径 | 用途 |
|---|---|---|
| GET | `/api/posts` | 文章列表（分页） |
| GET | `/api/posts/:slug` | 文章详情 |
| POST | `/api/posts` | 创建文章 |
| PUT | `/api/posts/:id` | 更新文章 |
| DELETE | `/api/posts/:id` | 删除文章 |
| GET | `/api/posts/archive` | 文章归档 |
| GET | `/api/posts/ranking/likes` | 点赞排行榜 |
| GET | `/api/posts/search?q=` | 搜索文章 |
| POST | `/api/auth/login` | 登录 |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/upload` | 图片上传 |
| GET | `/api/tags` | 标签列表 |
| GET | `/api/posts/:slug/comments` | 评论列表 |
| POST | `/api/posts/:slug/comments` | 发表评论 |
| GET | `/api/posts/:slug/like` | 点赞状态 |
| POST | `/api/posts/:slug/like` | 切换点赞 |
| GET | `/api/bookmarks` | 收藏列表 |
| POST | `/api/notifications/read-all` | 标记全部已读 |
| GET | `/api/stats/dashboard` | 仪表盘数据 |
| GET | `/api/stats/post/:id/trend` | 文章阅读趋势 |
| GET | `/api/export/:type` | 数据导出 CSV |
