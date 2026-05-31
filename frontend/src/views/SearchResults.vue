<template>
  <div class="search-results">
    <div class="search-header">
      <h2>搜索结果</h2>
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索文章..."
          :prefix-icon="SearchIcon"
          size="large"
          clearable
          @keyup.enter="doSearch"
          @clear="doSearch"
        >
          <template #append>
            <el-button @click="doSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      <p v-if="searched && !loading" class="result-info">
        找到 <strong>{{ total }}</strong> 篇与 "<strong>{{ lastKeyword }}</strong>" 相关的文章
      </p>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="searched && posts.length === 0" class="empty">
      <el-empty description="没有找到相关文章" />
    </div>

    <template v-else>
      <BlogCard v-for="post in posts" :key="post.id" :post="post" />

      <div v-if="total > size" class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="doSearch"
        />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi } from '@/api/postApi'
import BlogCard from '@/components/BlogCard.vue'
import { Search as SearchIcon } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const keyword = ref(route.query.q || '')
const lastKeyword = ref('')
const posts = ref([])
const loading = ref(false)
const searched = ref(false)
const total = ref(0)
const page = ref(1)
const size = ref(20)

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

async function doSearch() {
  const q = keyword.value.trim()
  if (!q) {
    posts.value = []
    searched.value = false
    return
  }

  loading.value = true
  searched.value = true
  lastKeyword.value = q
  page.value = 1

  try {
    const res = await postApi.list({ page: page.value, size: size.value, keyword: q })
    posts.value = (res.list || []).map(formatPost)
    total.value = res.total || 0
  } catch {
    posts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 监听路由参数变化 — 顶栏搜索框跳转时重新搜索
watch(() => route.query.q, (newQ) => {
  keyword.value = newQ || ''
  if (newQ) {
    doSearch()
  } else {
    posts.value = []
    searched.value = false
  }
})

onMounted(() => {
  if (route.query.q) {
    keyword.value = route.query.q
    doSearch()
  }
})
</script>

<style scoped>
.search-results {
  max-width: 900px;
  margin: 0 auto;
}
.search-header {
  margin-bottom: 24px;
}
.search-header h2 {
  margin: 0 0 16px;
  font-size: 20px;
  color: #303133;
}
.search-bar {
  margin-bottom: 8px;
}
.result-info {
  font-size: 14px;
  color: #909399;
  margin: 8px 0 0;
}
.loading, .empty { padding: 40px 0; }
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
