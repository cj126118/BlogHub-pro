<template>
  <div class="archive">
    <h2 class="archive-title">文章归档</h2>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="error" class="error">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button @click="fetchData">重试</el-button></template>
      </el-result>
    </div>

    <template v-else>
      <div v-for="(group, year) in grouped" :key="year" class="year-group">
        <h3 class="year-title">{{ year }} <span class="year-count">({{ totalByYear[year] }} 篇)</span></h3>

        <div v-for="(monthGroup, month) in group" :key="month" class="month-group">
          <h4 class="month-title">{{ month }} 月</h4>
          <div
            v-for="post in monthGroup"
            :key="post.id"
            class="archive-item"
            @click="$router.push(`/blog/${post.slug}`)"
          >
            <span class="archive-date">{{ (post.dateTime || post.date)?.slice(8, 10) }}</span>
            <span class="archive-title-text">{{ post.title }}</span>
          </div>
        </div>
      </div>

      <div v-if="allPosts.length === 0" class="empty">
        <el-empty description="还没有文章" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { postApi } from '@/api/postApi'

const allPosts = ref([])
const loading = ref(true)
const error = ref(null)

const grouped = computed(() => {
  const result = {}
  const counts = {}
  for (const post of allPosts.value) {
    const date = post.date || post.createdAt || ''
    const year = date.slice(0, 4)
    const month = String(parseInt(date.slice(5, 7)))
    if (!result[year]) result[year] = {}
    if (!result[year][month]) result[year][month] = []
    result[year][month].push(post)
    counts[year] = (counts[year] || 0) + 1
  }
  totalByYear.value = counts
  return result
})

const totalByYear = ref({})

async function fetchData() {
  loading.value = true
  error.value = null
  try {
    const res = await postApi.list({ page: 1, size: 100, sort: 'date' })
    allPosts.value = (res.list || []).map(p => ({
      id: p.id,
      slug: p.slug,
      title: p.title || p.slug,
      date: p.createdAt ? p.createdAt.slice(0, 10) : '',
      dateTime: p.createdAt ? p.createdAt.slice(0, 19).replace('T', ' ') : '',
    }))
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.archive {
  max-width: 800px;
  margin: 0 auto;
}
.archive-title {
  font-size: 22px;
  color: #303133;
  margin: 0 0 24px;
}
.loading, .error, .empty { padding: 40px 0; }

.year-group { margin-bottom: 32px; }
.year-title {
  font-size: 20px;
  color: #303133;
  margin: 0 0 12px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}
.year-count {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.month-group { margin: 0 0 16px 20px; }
.month-title {
  font-size: 16px;
  color: #606266;
  margin: 0 0 8px;
}

.archive-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.2s;
}
.archive-item:hover { background: #f5f7fa; }

.archive-date {
  font-size: 13px;
  color: #909399;
  font-family: monospace;
  flex-shrink: 0;
  width: 32px;
}
.archive-title-text {
  font-size: 15px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
