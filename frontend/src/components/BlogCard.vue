<template>
  <el-card shadow="hover" class="blog-card" @click="$router.push(`/blog/${post.slug}`)">
    <div class="card-body">
      <div class="card-content">
        <div class="card-meta">
          <span v-if="post.categoryName" class="category-tag">{{ post.categoryName }}</span>
          <span class="date">{{ post.date }}</span>
          <span v-if="post.updatedAt && post.updatedAt.slice(0, 19) !== post.createdAtRaw" class="updated">已编辑</span>
          <span class="author">{{ post.author }}</span>
        </div>
        <h3 class="card-title">{{ post.title }}</h3>
        <p class="card-desc">{{ post.description || '暂无摘要' }}</p>
        <div class="card-footer">
          <div class="tags">
            <el-tag v-for="tag in (post.tags || [])" :key="tag" size="small" type="info" effect="plain">
              {{ tag }}
            </el-tag>
          </div>
          <div class="stats">
            <span><el-icon><View /></el-icon> {{ post.views }}</span>
            <span><el-icon><Star /></el-icon> {{ post.likes }}</span>
          </div>
        </div>
      </div>
      <div v-if="post.coverImage" class="card-cover">
        <img :src="post.coverImage" :alt="post.title" />
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { View, Star } from '@element-plus/icons-vue'

const props = defineProps({
  post: { type: Object, required: true },
})
</script>

<style scoped>
.blog-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}
.blog-card:hover {
  transform: translateY(-2px);
}
.card-body {
  display: flex;
  gap: 20px;
}
.card-content {
  flex: 1;
  min-width: 0;
}
.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #909399;
}
.updated {
  font-size: 11px;
  color: #e6a23c;
  background: #fdf6ec;
  padding: 0 6px;
  border-radius: 3px;
}
.category-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.card-title {
  margin: 0 0 8px;
  font-size: 18px;
  color: #303133;
  line-height: 1.4;
}
.card-desc {
  margin: 0 0 12px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
.stats {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
}
.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.card-cover {
  width: 160px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
