<template>
  <div class="image-upload">
    <!-- 拖拽区域 -->
    <div
      class="upload-area"
      :class="{ dragging, 'has-preview': previewUrl }"
      @dragenter.prevent="dragging = true"
      @dragover.prevent="dragging = true"
      @dragleave.prevent="dragging = false"
      @drop.prevent="handleDrop"
      @click="triggerInput"
    >
      <input ref="fileInput" type="file" accept="image/*" hidden @change="handleFile" />

      <div v-if="previewUrl" class="preview">
        <img :src="previewUrl" alt="预览" />
        <div class="preview-overlay">
          <el-button size="small" type="danger" circle @click.stop="removeFile">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div v-if="uploadedUrl" class="uploaded-url">
          <span class="url-text">{{ uploadedUrl }}</span>
          <el-button size="small" @click.stop="copyUrl">复制 URL</el-button>
          <el-button v-if="onInsert" type="primary" size="small" @click.stop="insertImage">
            插入到编辑器
          </el-button>
        </div>
      </div>

      <div v-else class="upload-placeholder">
        <el-icon :size="32" color="#c0c4cc"><Plus /></el-icon>
        <p>{{ uploading ? '上传中...' : '点击或拖拽图片到此处上传' }}</p>
        <p class="upload-hint">支持 jpg/png/gif/webp/svg，最大 5MB</p>
      </div>
    </div>

    <!-- 进度条 -->
    <el-progress v-if="uploading" :percentage="progress" :stroke-width="4" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Close } from '@element-plus/icons-vue'
import request from '@/api/request'

const props = defineProps({
  onInsert: { type: Function, default: null },
})

const emit = defineEmits(['uploaded'])

const fileInput = ref(null)
const dragging = ref(false)
const uploading = ref(false)
const progress = ref(0)
const previewUrl = ref('')
const uploadedUrl = ref('')

function triggerInput() {
  if (!uploading.value) fileInput.value?.click()
}

function handleDrop(e) {
  dragging.value = false
  const file = e.dataTransfer?.files?.[0]
  if (file) processFile(file)
}

function handleFile(e) {
  const file = e.target?.files?.[0]
  if (file) processFile(file)
}

function processFile(file) {
  // 校验类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/svg+xml']
  if (!validTypes.includes(file.type)) {
    ElMessage.warning('不支持的图片格式')
    return
  }
  // 校验大小
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    return
  }

  // 本地预览
  previewUrl.value = URL.createObjectURL(file)
  uploadFile(file)
}

async function uploadFile(file) {
  uploading.value = true
  progress.value = 0

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await request.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (e) => {
        if (e.total) progress.value = Math.round((e.loaded / e.total) * 100)
      },
    })
    uploadedUrl.value = res.url
    progress.value = 100
    emit('uploaded', res.url)
    ElMessage.success('上传成功')
  } catch {
    previewUrl.value = ''
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

function removeFile() {
  previewUrl.value = ''
  uploadedUrl.value = ''
}

function copyUrl() {
  navigator.clipboard?.writeText(uploadedUrl.value)
  ElMessage.success('已复制 URL')
}

function insertImage() {
  if (props.onInsert && uploadedUrl.value) {
    props.onInsert(uploadedUrl.value)
  }
}
</script>

<style scoped>
.upload-area {
  border: 2px dashed #e4e7ed;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.upload-area:hover { border-color: #409eff; background: #f5f7fa; }
.upload-area.dragging { border-color: #409eff; background: #ecf5ff; }
.upload-area.has-preview { padding: 12px; }
.upload-placeholder p { margin: 8px 0 0; color: #909399; font-size: 14px; }
.upload-hint { font-size: 12px !important; color: #c0c4cc !important; }

.preview { position: relative; width: 100%; }
.preview img { max-width: 100%; max-height: 200px; border-radius: 6px; object-fit: contain; }
.preview-overlay { position: absolute; top: 4px; right: 4px; }
.uploaded-url {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}
.url-text {
  font-size: 12px;
  color: #409eff;
  background: #ecf5ff;
  padding: 2px 8px;
  border-radius: 4px;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
