import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', public: true },
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
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'blog',
        name: 'BlogList',
        component: () => import('@/views/BlogList.vue'),
        meta: { title: '博客' },
      },
      {
        path: 'blog/new',
        name: 'PostEditor',
        component: () => import('@/views/PostEditor.vue'),
        meta: { title: '写文章', requiresAuth: true },
      },
      {
        path: 'blog/:slug',
        name: 'BlogPost',
        component: () => import('@/views/BlogPost.vue'),
        meta: { title: '文章详情' },
      },
      {
        path: 'blog/:slug/edit',
        name: 'PostEdit',
        component: () => import('@/views/PostEdit.vue'),
        meta: { title: '编辑文章', requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
      {
        path: 'my-posts',
        name: 'MyPosts',
        component: () => import('@/views/MyPosts.vue'),
        meta: { title: '我的文章', requiresAuth: true },
      },
      {
        path: 'bookmarks',
        name: 'Bookmarks',
        component: () => import('@/views/Bookmarks.vue'),
        meta: { title: '我的收藏', requiresAuth: true },
      },
      {
        path: 'archive',
        name: 'Archive',
        component: () => import('@/views/Archive.vue'),
        meta: { title: '文章归档' },
      },
      {
        path: 'search',
        name: 'SearchResults',
        component: () => import('@/views/SearchResults.vue'),
        meta: { title: '搜索' },
      },
      // 管理后台
      {
        path: 'admin/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'admin/users',
        name: 'UserManage',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'admin/comments',
        name: 'CommentManage',
        component: () => import('@/views/CommentManage.vue'),
        meta: { title: '评论管理', requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'admin/logs',
        name: 'LogManage',
        component: () => import('@/views/LogManage.vue'),
        meta: { title: '操作日志', requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/About.vue'),
        meta: { title: '关于' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} | BlogHub` : 'BlogHub'

  const token = localStorage.getItem('blog_token')

  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 需要管理员权限
  if (to.meta.requiresAdmin) {
    const role = localStorage.getItem('blog_role')
    if (role !== 'admin') {
      next('/')
      return
    }
  }

  // 公开页面
  if (to.meta.public) {
    if (to.path === '/login' && token) {
      next('/')
      return
    }
    next()
    return
  }

  // 其他页面无需认证
  next()
})

export default router
