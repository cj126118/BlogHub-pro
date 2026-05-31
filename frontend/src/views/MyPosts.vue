<template>
  <div class="my-posts">
    <h2>我的文章</h2>

    <div v-if="loading" class="loading"><el-skeleton :rows="3" animated /></div>
    <el-empty v-else-if="posts.length === 0" description="还没有发表过文章" />

    <template v-else>
      <div v-for="post in posts" :key="post.id" class="post-item">
        <div class="post-info" @click="$router.push(`/blog/${post.slug}`)">
          <span class="post-title">{{ post.title }}</span>
          <span class="post-status" :class="post.status?.toLowerCase()">{{ post.status }}</span>
          <span class="post-date">{{ formatTime(post.createdAt) }}</span>
        </div>
        <div class="post-actions">
          <el-button text size="small" @click="$router.push(`/blog/${post.slug}/edit`)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="deletePost(post.id)">
            <template #reference>
              <el-button text size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>

      <div class="pagination" v-if="total > size">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total"
          layout="prev, pager, next" @current-change="fetchData" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { postApi } from '@/api/postApi'
import { ElMessage } from 'element-plus'

const posts = ref([])
const loading = ref(true)
const total = ref(0)
const page = ref(1)
const size = ref(20)

function formatTime(s) { return s ? s.slice(0, 16).replace('T', ' ') : '' }

async function fetchData() {
  loading.value = true
  try {
    const res = await postApi.myPosts({ page: page.value, size: size.value })
    posts.value = res.list || []
    total.value = res.total || 0
  } catch { posts.value = [] }
  finally { loading.value = false }
}

async function deletePost(id) {
  try {
    await postApi.delete(id)
    ElMessage.success('已删除')
    fetchData()
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.my-posts { max-width: 800px; margin: 0 auto; }
.my-posts h2 { font-size: 20px; margin: 0 0 20px; }
.loading { padding: 40px 0; }
.post-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 0; border-bottom: 1px solid #f0f0f0;
}
.post-info { flex: 1; cursor: pointer; display: flex; align-items: center; gap: 12px; }
.post-title { font-size: 15px; color: #303133; }
.post-status {
  font-size: 12px; padding: 1px 6px; border-radius: 4px;
}
.post-status.published { background: #ecf5ff; color: #409eff; }
.post-status.draft { background: #fdf6ec; color: #e6a23c; }
.post-date { font-size: 12px; color: #c0c4cc; white-space: nowrap; }
.post-actions { display: flex; gap: 4px; flex-shrink: 0; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
