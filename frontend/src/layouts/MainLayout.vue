<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'">
      <div class="logo">
        <el-icon :size="28" color="#409eff"><Promotion /></el-icon>
        <span v-show="!isCollapse" class="logo-text">BlogHub</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="/blog">
          <el-icon><Reading /></el-icon>
          <template #title>博客</template>
        </el-menu-item>
        <el-menu-item index="/archive">
          <el-icon><Timer /></el-icon>
          <template #title>归档</template>
        </el-menu-item>
        <template v-if="authStore.isAdmin">
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/comments">
            <el-icon><ChatLineSquare /></el-icon>
            <template #title>评论管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </template>
        <template v-if="authStore.isLoggedIn">
          <el-menu-item index="/my-posts">
            <el-icon><Document /></el-icon>
            <template #title>我的文章</template>
          </el-menu-item>
          <el-menu-item index="/bookmarks">
            <el-icon><Star /></el-icon>
            <template #title>收藏</template>
          </el-menu-item>
        </template>
        <el-menu-item index="/about">
          <el-icon><InfoFilled /></el-icon>
          <template #title>关于</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索文章..."
              :prefix-icon="Search"
              size="small"
              clearable
              class="header-search"
              @keyup.enter="goSearch"
            />
          </div>
        </div>
        <div class="header-right">
          <template v-if="authStore.isLoggedIn">
            <el-button type="primary" size="small" @click="$router.push('/blog/new')">
              <el-icon><Edit /></el-icon> 写文章
            </el-button>
            <el-dropdown trigger="click" @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="authStore.avatar || undefined">
                  {{ authStore.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ authStore.nickname || authStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="my-posts">我的文章</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isAdmin" command="dashboard">管理后台</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button size="small" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Search, DataAnalysis, ChatLineSquare } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)
const searchKeyword = ref('')

function goSearch() {
  const q = searchKeyword.value.trim()
  if (q) {
    router.push({ path: '/search', query: { q } })
  }
}

function handleUserCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-posts':
      router.push('/my-posts')
      break
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'logout':
      authStore.logout()
      router.push('/home')
      break
  }
}
</script>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #2b3a4a;
}
.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  white-space: nowrap;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  height: 56px;
}
.header-left {
  display: flex;
  align-items: center;
}
.search-box {
  margin-left: 16px;
  flex: 1;
  max-width: 320px;
}
.header-search :deep(.el-input__wrapper) {
  background: #f5f7fa;
  border-radius: 20px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-size: 14px;
  color: #303133;
}
.main-content {
  background: #f5f7fa;
  padding: 24px;
  overflow-y: auto;
}
</style>
