<template>
  <div class="blog-list">
    <div class="list-header">
      <h2>文章列表</h2>
      <div class="filters">
        <el-select v-model="categoryId" placeholder="全部分类" clearable size="default" @change="fetchData">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-input
          v-model="keyword"
          placeholder="搜索文章..."
          prefix-icon="Search"
          clearable
          size="default"
          class="search-input"
          @keyup.enter="fetchData"
          @clear="fetchData"
        />
      </div>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="error" class="error">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra>
          <el-button @click="fetchData">重试</el-button>
        </template>
      </el-result>
    </div>

    <div v-else-if="posts.length === 0" class="empty">
      <el-empty description="暂无文章" />
    </div>

    <template v-else>
      <BlogCard v-for="post in posts" :key="post.id" :post="post" />

      <div class="pagination" v-if="total > size">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { postApi } from '@/api/postApi'
import { categoryApi } from '@/api/categoryApi'
import BlogCard from '@/components/BlogCard.vue'
import { markdownToHtml } from '@/composables/useMarkdown'

const posts = ref([])
const categories = ref([])
const loading = ref(false)
const error = ref(null)
const total = ref(0)

const route = useRoute()

const page = ref(1)
const size = ref(20)
const categoryId = ref(null)
const tagId = ref(null)
const keyword = ref('')

function formatPost(raw) {
  return {
    id: raw.id,
    slug: raw.slug,
    title: raw.title || raw.slug,
    date: raw.createdAt ? raw.createdAt.slice(0, 16).replace('T', ' ') : '',
    createdAtRaw: raw.createdAt || null,
    updatedAt: raw.updatedAt || null,
    tags: raw.tags ? (typeof raw.tags === 'string' ? raw.tags.split(',').map(s => s.trim()).filter(Boolean) : raw.tags) : [],
    description: raw.description || '',
    views: raw.views || 0,
    likes: raw.likes || 0,
    author: raw.authorName || '',
    authorAvatar: raw.authorAvatar || '',
    coverImage: raw.coverImage || '',
    isPinned: raw.isPinned || 0,
    categoryName: raw.categoryName || '',
  }
}

async function fetchData() {
  loading.value = true
  error.value = null
  try {
    const params = { page: page.value, size: size.value }
    if (categoryId.value) params.categoryId = categoryId.value
    if (tagId.value) params.tagId = tagId.value
    if (keyword.value) params.keyword = keyword.value
    const res = await postApi.list(params)
    posts.value = (res.list || []).map(formatPost)
    total.value = res.total || 0
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const res = await categoryApi.list()
    categories.value = res || []
  } catch { /* 静默 */ }
}

// 从 URL query 参数读取分类筛选
watch(() => route.query.categoryId, (newVal) => {
  categoryId.value = newVal ? Number(newVal) : null
  page.value = 1
  fetchData()
})

watch(() => route.query.tagId, (newVal) => {
  tagId.value = newVal ? Number(newVal) : null
  page.value = 1
  fetchData()
})

onMounted(() => {
  if (route.query.categoryId) {
    categoryId.value = Number(route.query.categoryId)
  }
  if (route.query.tagId) {
    tagId.value = Number(route.query.tagId)
  }
  fetchData()
  fetchCategories()
})
</script>

<style scoped>
.blog-list {
  max-width: 900px;
  margin: 0 auto;
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.list-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}
.filters {
  display: flex;
  gap: 12px;
}
.search-input {
  width: 200px;
}
.loading {
  padding: 40px;
}
.error, .empty {
  padding: 60px 0;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
