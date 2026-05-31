<template>
  <div class="about">
    <el-card shadow="never">
      <template #header>
        <h2>关于 BlogHub</h2>
      </template>
      <div class="about-content">
        <p>BlogHub 是一个基于现代化技术栈构建的全栈博客平台。</p>
        <h3>技术栈</h3>
        <el-table :data="techStack" stripe style="width: 100%">
          <el-table-column prop="layer" label="层级" width="120" />
          <el-table-column prop="tech" label="技术" />
          <el-table-column prop="desc" label="说明" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
const techStack = [
  { layer: '前端框架', tech: 'Vue 3 + Vite 5', desc: 'Composition API, `<script setup>`' },
  { layer: 'UI 库', tech: 'Element Plus 2', desc: '全套组件 + 图标库' },
  { layer: '状态管理', tech: 'Pinia', desc: 'Composition API 风格 Store' },
  { layer: '后端框架', tech: 'Spring Boot 2.7', desc: 'Java 8, MyBatis-Plus' },
  { layer: '数据库', tech: 'MySQL 8', desc: 'InnoDB, utf8mb4' },
  { layer: '认证', tech: 'JWT + BCrypt', desc: '双 Token 模式' },
]
</script>

<style scoped>
.about {
  max-width: 800px;
  margin: 0 auto;
}
.about-content p {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
}
.about-content h3 {
  margin: 24px 0 12px;
  font-size: 18px;
  color: #303133;
}
</style>
