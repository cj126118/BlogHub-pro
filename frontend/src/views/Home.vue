<template>
  <div class="home">
    <el-row :gutter="24">
      <!-- 主内容区：最新文章 -->
      <el-col :span="16">
        <div class="section-title">
          <h2>最新文章</h2>
        </div>

        <!-- 加载态：骨架屏 -->
        <div v-if="loading" class="skeleton-list">
          <SkeletonCard v-for="n in 5" :key="n" />
        </div>

        <!-- 空状态 -->
        <el-empty v-else-if="posts.length === 0" description="暂无文章" />

        <!-- 文章列表 -->
        <template v-else>
          <BlogCard v-for="post in posts" :key="post.id" :post="post" />
        </template>
      </el-col>

      <!-- 侧边栏 -->
      <el-col :span="8">
        <!-- 分类 -->
        <el-card shadow="never" class="sidebar-card">
          <template #header>
            <span class="sidebar-title">分类</span>
          </template>
          <div v-if="categories.length" class="category-list">
            <div
              v-for="cat in categories"
              :key="cat.id"
              class="category-item"
              @click="filterByCategory(cat.id)"
            >
              <span class="cat-name">{{ cat.name }}</span>
              <el-tag size="small" type="info" effect="plain">{{ cat.postCount || 0 }}</el-tag>
            </div>
          </div>
          <el-empty v-else :image-size="60" description="暂无分类" />
        </el-card>

        <!-- 标签云 -->
        <el-card shadow="never" class="sidebar-card">
          <template #header>
            <span class="sidebar-title">标签云</span>
          </template>
          <div v-if="tags.length" class="tag-cloud">
            <el-tag
              v-for="tag in tags"
              :key="tag.id"
              :size="tagSize(tag.postCount)"
              :type="tagType(tag.postCount)"
              effect="plain"
              class="tag-item"
              @click="$router.push(`/blog?tagId=${tag.id}`)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
          <el-empty v-else :image-size="60" description="暂无标签" />
        </el-card>

        <!-- 热门文章 -->
        <el-card shadow="never" class="sidebar-card">
          <template #header>
            <span class="sidebar-title">热门文章</span>
          </template>
          <div v-if="hotPosts.length" class="hot-list">
            <div
              v-for="(post, idx) in hotPosts"
              :key="post.id"
              class="hot-item"
              @click="$router.push(`/blog/${post.slug}`)"
            >
              <span class="hot-rank" :class="{ top3: idx < 3 }">{{ idx + 1 }}</span>
              <span class="hot-title">{{ post.title }}</span>
              <span class="hot-views">{{ post.views }} 阅读</span>
            </div>
          </div>
          <el-empty v-else :image-size="60" description="暂无数据" />
        </el-card>

        <!-- 关于 -->
        <el-card shadow="never" class="sidebar-card">
          <template #header>
            <span class="sidebar-title">关于 BlogHub</span>
          </template>
          <p class="about-text">基于 Vue 3 + Spring Boot 的全栈博客平台。</p>
          <el-button text @click="$router.push('/about')">了解更多 →</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { postApi } from '@/api/postApi'
import { categoryApi } from '@/api/categoryApi'
import { tagApi } from '@/api/tagApi'
import BlogCard from '@/components/BlogCard.vue'
import SkeletonCard from '@/components/SkeletonCard.vue'

const router = useRouter()

const posts = ref([])
const categories = ref([])
const tags = ref([])
const hotPosts = ref([])
const loading = ref(true)

function tagSize(count) {
  if (!count) return 'small'
  if (count > 5) return 'large'
  if (count > 2) return 'default'
  return 'small'
}

function tagType(count) {
  if (!count) return 'info'
  if (count > 5) return 'danger'
  if (count > 2) return 'warning'
  return 'primary'
}

function filterByCategory(catId) {
  router.push(`/blog?categoryId=${catId}`)
}

async function fetchLatestPosts() {
  try {
    const res = await postApi.list({ page: 1, size: 10 })
    posts.value = (res.list || []).map(formatPost)
  } catch {
    posts.value = []
  }
}

function formatPost(raw) {
  return {
    id: raw.id,
    slug: raw.slug,
    title: raw.title || raw.slug,
    date: raw.createdAt ? raw.createdAt.slice(0, 16).replace('T', ' ') : '',
    createdAtRaw: raw.createdAt || null,
    updatedAt: raw.updatedAt || null,
    tags: raw.tags ? (Array.isArray(raw.tags) ? raw.tags : raw.tags.split(',')) : [],
    description: raw.description || '',
    views: raw.views || 0,
    likes: raw.likes || 0,
    author: raw.authorName || '',
    authorAvatar: raw.authorAvatar || '',
    coverImage: raw.coverImage || '',
    categoryName: raw.categoryName || '',
  }
}

async function fetchCategories() {
  try {
    const res = await categoryApi.list()
    categories.value = res || []
  } catch { categories.value = [] }
}

async function fetchTags() {
  try {
    const res = await tagApi.list()
    tags.value = res || []
  } catch { tags.value = [] }
}

async function fetchHotPosts() {
  try {
    const res = await postApi.list({ page: 1, size: 5, sort: 'views' })
    hotPosts.value = (res.list || []).map(formatPost)
  } catch {
    hotPosts.value = []
  }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    fetchLatestPosts(),
    fetchCategories(),
    fetchTags(),
    fetchHotPosts(),
  ])
  loading.value = false
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}
.section-title h2 {
  margin: 0 0 16px;
  font-size: 20px;
  color: #303133;
}

/* 侧边栏卡片 */
.sidebar-card {
  margin-bottom: 20px;
}
.sidebar-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

/* 分类列表 */
.category-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  cursor: pointer;
  transition: color 0.2s;
}
.category-item:hover { color: #409eff; }
.cat-name { font-size: 14px; }

/* 标签云 */
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tag-item { cursor: pointer; }

/* 热门文章 */
.hot-list { display: flex; flex-direction: column; gap: 8px; }
.hot-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 0;
  transition: color 0.2s;
}
.hot-item:hover { color: #409eff; }
.hot-rank {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  flex-shrink: 0;
}
.hot-rank.top3 { background: #409eff; color: #fff; }
.hot-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}
.hot-views {
  font-size: 12px;
  color: #c0c4cc;
  flex-shrink: 0;
}

/* 关于 */
.about-text {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 8px;
}

/* 骨架屏列表 */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
