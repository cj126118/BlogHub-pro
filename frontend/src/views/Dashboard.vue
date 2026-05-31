<template>
  <div class="dashboard">
    <h2>仪表盘</h2>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-value">{{ stats.articleCount || 0 }}</div>
          <div class="stat-label">文章数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
          <div class="stat-label">用户数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-value">{{ stats.commentCount || 0 }}</div>
          <div class="stat-label">评论数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-value">{{ stats.newUsers || 0 }}</div>
          <div class="stat-label">近7天新用户</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statsApi } from '@/api/statsApi'

const stats = ref({})

onMounted(async () => {
  try { stats.value = await statsApi.dashboard() } catch {}
})
</script>

<style scoped>
.dashboard { max-width: 1000px; margin: 0 auto; }
.dashboard h2 { font-size: 20px; margin: 0 0 20px; }
.stat-cards { margin-bottom: 24px; }
.stat-value { font-size: 32px; font-weight: bold; color: #409eff; text-align: center; }
.stat-label { font-size: 14px; color: #909399; text-align: center; margin-top: 4px; }
</style>
