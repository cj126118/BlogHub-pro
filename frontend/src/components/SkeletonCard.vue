<template>
  <el-card shadow="hover" class="skeleton-card">
    <div class="skeleton-body">
      <div class="skeleton-content">
        <div class="skeleton-line w-30" />
        <div class="skeleton-line w-80 h3" />
        <div class="skeleton-line w-100" />
        <div class="skeleton-line w-60" />
        <div class="skeleton-footer">
          <div class="skeleton-line w-40" />
          <div class="skeleton-line w-15" />
        </div>
      </div>
      <div class="skeleton-cover" />
    </div>
  </el-card>
</template>

<style scoped>
.skeleton-card {
  margin-bottom: 16px;
}
.skeleton-body {
  display: flex;
  gap: 20px;
}
.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}
.skeleton-line {
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
.skeleton-line.h3 { height: 20px; }
.skeleton-line.w-15 { width: 15%; }
.skeleton-line.w-30 { width: 30%; }
.skeleton-line.w-40 { width: 40%; }
.skeleton-line.w-60 { width: 60%; }
.skeleton-line.w-80 { width: 80%; }
.skeleton-line.w-100 { width: 100%; }
.skeleton-cover {
  width: 160px;
  height: 120px;
  border-radius: 8px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  flex-shrink: 0;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
