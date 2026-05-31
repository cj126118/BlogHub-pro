<template>
  <div class="comment-manage">
    <h2>评论管理</h2>
    <el-table :data="comments" stripe style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="authorName" label="作者" width="100">
        <template #default="{ row }">{{ row.authorName || row.nickname || '匿名' }}</template>
      </el-table-column>
      <el-table-column prop="articleTitle" label="文章" min-width="200">
        <template #default="{ row }">
          <router-link v-if="row.articleSlug" :to="'/blog/' + row.articleSlug" class="article-link">
            {{ row.articleTitle || row.articleSlug || '#' + row.articleId }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="150">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="70">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)">
            {{ row.status === 1 ? '隐藏' : '显示' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statsApi } from '@/api/statsApi'
import { ElMessage } from 'element-plus'

const comments = ref([])

function formatTime(s) { return s ? s.slice(0, 16).replace('T', ' ') : '' }

async function fetchData() {
  try {
    const res = await statsApi.comments({ page: 1, size: 100 })
    comments.value = res.list || []
  } catch {}
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await statsApi.toggleCommentStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(newStatus === 1 ? '已显示' : '已隐藏')
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.comment-manage { max-width: 1200px; margin: 0 auto; }
.comment-manage h2 { font-size: 20px; margin: 0 0 20px; }
.article-link { color: #409eff; text-decoration: none; }
.article-link:hover { text-decoration: underline; }
</style>
