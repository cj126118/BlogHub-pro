<template>
  <ReadingProgress />
  <div class="blog-post" v-if="!loading">
    <el-card shadow="never" class="post-card">
      <div class="post-header">
        <div class="post-meta">
          <span v-if="post.categoryName" class="category-tag">{{ post.categoryName }}</span>
          <span class="date">{{ post.date }}</span>
          <span v-if="post.updatedAt && post.updatedAt.slice(0, 19) !== post.createdAtRaw" class="updated-tag">
            编辑于 {{ post.updatedAt.slice(0, 16).replace('T', ' ') }}
          </span>
          <span class="author">{{ post.author }}</span>
        </div>
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-tags" v-if="post.tags && post.tags.length">
          <el-tag v-for="tag in post.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
        </div>
      </div>

      <div class="post-content" v-html="post.html"></div>

      <div v-if="authStore.userId && String(authStore.userId) === String(post.authorId)" class="post-admin-bar">
        <el-button type="primary" size="small" @click="$router.push('/blog/' + post.slug + '/edit')">
          <el-icon><Edit /></el-icon> 编辑文章
        </el-button>
      </div>

      <div class="post-actions">
        <el-button :type="liked ? 'primary' : 'default'" @click="toggleLike">
          <el-icon><Star /></el-icon> {{ liked ? '已赞' : '点赞' }} ({{ likeCount }})
        </el-button>
        <el-button :type="bookmarked ? 'warning' : 'default'" @click="toggleBookmark">
          <el-icon><Star /></el-icon> {{ bookmarked ? '已收藏' : '收藏' }}
        </el-button>
      </div>

      <div class="post-footer">
        <span class="views"><el-icon><View /></el-icon> {{ post.views }} 次阅读</span>
      </div>

      <BlogComments :slug="post.slug" />
    </el-card>
  </div>

  <div v-else class="loading">
    <el-skeleton :rows="6" animated />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi } from '@/api/postApi'
import { markdownToHtml } from '@/composables/useMarkdown'
import ReadingProgress from '@/components/ReadingProgress.vue'
import BlogComments from '@/components/BlogComments.vue'
import { useLikes, useBookmarks } from '@/composables/useInteraction'
import { useAuthStore } from '@/stores/auth'
import { Star, View, Edit } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const post = ref({})
const loading = ref(true)

const authStore = useAuthStore()
const { liked, count: likeCount, toggleLike } = useLikes(route.params.slug)
const { bookmarked, toggleBookmark } = useBookmarks(route.params.slug)

async function fetchPost() {
  loading.value = true
  try {
    const raw = await postApi.detail(route.params.slug)
    const md = raw.content || ''
    let html = ''
    try {
      const rendered = markdownToHtml(md)
      html = rendered.html
    } catch {
      html = '<pre>' + md.replace(/</g, '&lt;') + '</pre>'
    }
    post.value = {
      id: raw.id,
      slug: raw.slug,
      title: raw.title,
      date: raw.createdAt ? raw.createdAt.slice(0, 19).replace('T', ' ') : '',
      createdAtRaw: raw.createdAt || null,
      updatedAt: raw.updatedAt || null,
      tags: raw.tags ? (typeof raw.tags === 'string' ? raw.tags.split(',').map(s => s.trim()).filter(Boolean) : raw.tags) : [],
      description: raw.description || '',
      html,
      content: md,
      views: raw.views || 0,
      authorId: raw.authorId || null,
      author: raw.authorName || '',
      authorAvatar: raw.authorAvatar || '',
      coverImage: raw.coverImage || '',
      categoryName: raw.categoryName || '',
    }
  } catch (e) {
    post.value = null
  } finally {
    loading.value = false
  }
}

onMounted(fetchPost)
</script>

<style scoped>
.blog-post {
  max-width: 800px;
  margin: 0 auto;
}
.post-card {
  padding: 24px;
}
.post-header {
  margin-bottom: 32px;
}
.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
}
.updated-tag {
  font-size: 12px;
  color: #e6a23c;
  background: #fdf6ec;
  padding: 0 8px;
  border-radius: 4px;
}
.category-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.post-title {
  font-size: 28px;
  color: #303133;
  margin: 0 0 12px;
  line-height: 1.4;
}
.post-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.post-content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}
.post-content :deep(h2) {
  margin: 28px 0 12px;
  font-size: 22px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}
.post-content :deep(h3) {
  margin: 24px 0 8px;
  font-size: 18px;
}
.post-content :deep(p) {
  margin: 12px 0;
}
.post-content :deep(pre) {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
}
.post-content :deep(code) {
  font-family: 'Courier New', monospace;
  font-size: 14px;
}
.post-content :deep(blockquote) {
  border-left: 4px solid #409eff;
  margin: 16px 0;
  padding: 8px 16px;
  background: #f5f7fa;
  color: #606266;
}
.post-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 16px 0;
}
.post-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}
.post-content :deep(th), .post-content :deep(td) {
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  text-align: left;
}
.post-content :deep(th) {
  background: #f5f7fa;
}
.post-admin-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}
.post-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin: 40px 0 20px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}
.post-footer {
  text-align: center;
  font-size: 13px;
  color: #909399;
}
.post-footer span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.loading {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px;
}
</style>
