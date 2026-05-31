<template>
  <div class="bookmarks">
    <h2>我的收藏</h2>

    <div v-if="loading" class="loading"><el-skeleton :rows="3" animated /></div>
    <el-empty v-else-if="list.length === 0" description="还没有收藏文章" />

    <template v-else>
      <div v-for="item in list" :key="item.id" class="bm-item" @click="$router.push(`/blog/${item.slug}`)">
        <div class="bm-info">
          <span class="bm-title">{{ item.title || item.slug }}</span>
          <span class="bm-date">{{ formatTime(item.createdAt) }}</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { interactionApi } from '@/api/interactionApi'

const list = ref([])
const loading = ref(true)

function formatTime(s) { return s ? s.slice(0, 16).replace('T', ' ') : '' }

onMounted(async () => {
  try {
    const res = await interactionApi.getBookmarks()
    list.value = res || []
  } catch { list.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.bookmarks { max-width: 800px; margin: 0 auto; }
.bookmarks h2 { font-size: 20px; margin: 0 0 20px; }
.loading { padding: 40px 0; }
.bm-item {
  display: flex; align-items: center; padding: 12px 0;
  border-bottom: 1px solid #f0f0f0; cursor: pointer;
}
.bm-item:hover { color: #409eff; }
.bm-info { flex: 1; display: flex; justify-content: space-between; align-items: center; }
.bm-title { font-size: 15px; color: #303133; }
.bm-date { font-size: 12px; color: #c0c4cc; }
</style>
