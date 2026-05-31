<template>
  <div class="post-editor">
    <!-- 顶部：标题 + 元信息 + 操作按钮 — 保持卡片样式 -->
    <el-card shadow="never" class="editor-meta">
      <div class="editor-header">
        <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
        <div class="editor-actions">
          <el-button @click="saveDraft('DRAFT')">保存草稿</el-button>
          <el-button type="primary" @click="saveDraft('PUBLISHED')">发布</el-button>
        </div>
      </div>

      <div class="meta-fields">
        <div class="field-row">
          <div class="field-item flexible">
            <label>标题</label>
            <el-input v-model="form.title" placeholder="请输入文章标题" size="large" />
          </div>
        </div>
        <div class="field-row">
          <div class="field-item">
            <label>分类</label>
            <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width:100%">
              <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </div>
          <div class="field-item flexible">
            <label>标签</label>
            <TagSelector v-model="form.tagIds" />
          </div>
          <div class="field-item">
            <label>摘要</label>
            <el-input v-model="form.description" placeholder="文章摘要（可选）" />
          </div>
        </div>
        <div class="field-row" style="flex-wrap: wrap;">
          <div class="field-item" style="min-width: 300px; flex: 2;">
            <label>封面图</label>
            <ImageUpload @uploaded="(url) => form.coverImage = url" />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 编辑器主体：占满剩余宽度，无卡片包裹 -->
    <div class="editor-body">
      <div class="editor-pane">
        <div class="pane-header">
          <span>Markdown</span>
          <div class="pane-tools">
            <el-popover placement="bottom" trigger="click" :width="300">
              <template #reference>
                <el-button size="small" text><el-icon><Picture /></el-icon> 图片</el-button>
              </template>
              <ImageUpload :on-insert="insertImage" />
            </el-popover>
          </div>
        </div>
        <el-input
          v-model="form.content"
          type="textarea"
          placeholder="使用 Markdown 编写文章..."
          class="md-editor"
        />
      </div>
      <div class="editor-pane">
        <div class="pane-header">实时预览</div>
        <div class="preview-content" v-html="previewHtml"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi } from '@/api/postApi'
import { categoryApi } from '@/api/categoryApi'
import { markdownToHtml } from '@/composables/useMarkdown'
import TagSelector from '@/components/TagSelector.vue'
import ImageUpload from '@/components/ImageUpload.vue'
import { ElMessage } from 'element-plus'
import { Close, Picture } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.slug && route.name === 'PostEdit')
const categories = ref([])
const coverError = ref(false)

const form = reactive({
  title: '',
  slug: '',
  content: '',
  description: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  status: 'DRAFT',
})

const previewHtml = computed(() => {
  if (!form.content) return '<p style="color:#909399;text-align:center;padding:40px;">✏️ 在左侧输入 Markdown，此处实时预览</p>'
  try {
    return markdownToHtml(form.content).html
  } catch {
    return '<p style="color:#f56c6c">Markdown 解析错误</p>'
  }
})

watch(() => form.coverImage, () => { coverError.value = false })

async function saveDraft(status) {
  if (!form.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  const data = {
    title: form.title,
    slug: form.slug || undefined,
    content: form.content,
    description: form.description,
    coverImage: form.coverImage,
    categoryId: form.categoryId,
    tagIds: form.tagIds,
    status,
  }
  try {
    if (isEdit.value) {
      await postApi.update(route.params.slug, data)
      ElMessage.success('保存成功')
      router.push(`/blog/${route.params.slug}`)
    } else {
      const res = await postApi.create(data)
      ElMessage.success('发布成功')
      router.push(`/blog/${res.slug}`)
    }
  } catch { /* 拦截器已提示 */ }
}

async function loadPost() {
  if (!isEdit.value) return
  try {
    const raw = await postApi.detail(route.params.slug)
    form.title = raw.title || ''
    form.slug = raw.slug || ''
    form.content = raw.content || ''
    form.description = raw.description || ''
    form.coverImage = raw.coverImage || ''
    form.categoryId = raw.categoryId || null
    form.tagIds = raw.tags ? raw.tags.split(',').map(s => s.trim()).filter(Boolean) : []
  } catch {
    ElMessage.error('加载文章失败')
    router.push('/blog')
  }
}

async function loadCategories() {
  try {
    const res = await categoryApi.list()
    categories.value = res || []
  } catch { /* 静默 */ }
}

function insertImage(url) {
  form.content += '\n![图片](' + url + ')\n'
}

onMounted(() => {
  loadCategories()
  if (isEdit.value) loadPost()
})
</script>

<style scoped>
/* 编辑器容器 — 占满主内容区 */
.post-editor {
  margin: -24px;
  height: calc(100vh - 56px);
  display: flex;
  flex-direction: column;
}

/* 顶部元信息卡片 — 紧凑 */
.editor-meta {
  margin: 0;
  border-radius: 0;
  flex-shrink: 0;
}
.editor-meta :deep(.el-card__body) {
  padding: 16px 24px;
}
.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.editor-header h2 { margin: 0; font-size: 20px; }
.editor-actions { display: flex; gap: 8px; }

/* 元信息字段行 */
.meta-fields { display: flex; flex-direction: column; gap: 8px; }
.field-row { display: flex; gap: 12px; }
.field-item {
  min-width: 180px;
  flex-shrink: 0;
}
.field-item.flexible { flex: 1; min-width: 0; }
.field-item label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

/* 封面图 */
.cover-field { width: 100%; }
.cover-input { margin-bottom: 4px; }
.cover-preview {
  position: relative;
  display: inline-block;
  max-width: 200px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}
.cover-preview img {
  display: block;
  max-width: 100%;
  max-height: 100px;
  object-fit: cover;
}
.cover-preview .el-button {
  position: absolute;
  top: 2px;
  right: 2px;
}

/* 编辑器主体 — 填满剩余高度，无卡片束缚 */
.editor-body {
  flex: 1;
  display: flex;
  gap: 0;
  overflow: hidden;
  min-height: 0;
}
.editor-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 50%;
  overflow: hidden;
}
.editor-pane:first-child {
  border-right: 1px solid #e4e7ed;
}

.pane-header {
  padding: 8px 16px;
  background: #f5f7fa;
  font-size: 13px;
  color: #606266;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}
.pane-hint { font-size: 12px; color: #c0c4cc; }
.pane-tools { display: flex; align-items: center; gap: 4px; }

/* Markdown 输入 — 占满 pane 剩余空间 */
.md-editor {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.md-editor :deep(.el-textarea__wrapper) {
  flex: 1;
  height: 100% !important;
  border: none;
  border-radius: 0;
}
.md-editor :deep(textarea) {
  height: 100% !important;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.7;
  padding: 16px;
  border: none;
  resize: none;
}

/* 预览区 */
.preview-content {
  flex: 1;
  padding: 16px 24px;
  overflow-y: auto;
  font-size: 15px;
  line-height: 1.8;
  background: #fff;
}
</style>
