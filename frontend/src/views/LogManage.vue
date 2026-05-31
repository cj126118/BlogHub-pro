<template>
  <div class="log-manage">
    <h2>操作日志</h2>
    <el-table :data="logs" stripe style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="action" label="操作" width="150" />
      <el-table-column prop="resource" label="资源" width="120" />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statsApi } from '@/api/statsApi'

const logs = ref([])

function formatTime(s) { return s ? s.slice(0, 19).replace('T', ' ') : '' }

onMounted(async () => {
  try {
    const res = await statsApi.logs({ page: 1, size: 100 })
    logs.value = res.list || []
  } catch {}
})
</script>

<style scoped>
.log-manage { max-width: 1000px; margin: 0 auto; }
.log-manage h2 { font-size: 20px; margin: 0 0 20px; }
</style>
