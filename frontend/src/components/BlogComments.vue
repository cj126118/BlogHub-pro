<template>
  <div class="blog-comments">
    <h3 class="comments-title">评论 ({{ total }})</h3>

    <!-- 顶部评论表单 -->
    <div class="top-form">
      <div class="form-row" v-if="!isLoggedIn">
        <el-input v-model="formNickname" placeholder="昵称" size="small" class="form-input" />
        <el-input v-model="formEmail" placeholder="邮箱（可选）" size="small" class="form-input" />
      </div>
      <el-input v-model="formContent" type="textarea" :rows="3" placeholder="发表评论..." />
      <div class="form-actions">
        <el-button type="primary" size="small" :loading="submitting" @click="submitComment()">发表评论</el-button>
      </div>
    </div>

    <div v-if="loading" class="loading"><el-skeleton :rows="3" animated /></div>
    <div v-else-if="comments.length === 0" class="empty"><p>暂无评论，快来抢沙发吧~</p></div>

    <div v-else class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="c-avatar">{{ (comment.authorName || comment.nickname || '?').charAt(0) }}</div>
        <div class="c-body">
          <div class="c-meta">
            <span class="c-author">{{ comment.authorName || comment.nickname || '匿名' }}</span>
            <span class="c-date">{{ formatTime(comment.createdAt) }}</span>
          </div>
          <div class="c-text">{{ comment.content }}</div>
          <div class="c-actions">
            <el-button text size="small" @click="toggleReply(comment.id, comment.authorName || comment.nickname)">回复</el-button>
          </div>

          <!-- 内联回复表单 -->
          <div v-if="activeReplyId === comment.id" class="inline-form">
            <div class="form-row" v-if="!isLoggedIn">
              <el-input v-model="replyFormNickname" placeholder="昵称" size="small" class="form-input" />
            </div>
            <el-input v-model="replyFormContent" type="textarea" :rows="2" placeholder="输入回复..." />
            <div class="form-actions">
              <el-button size="small" @click="activeReplyId = null">取消</el-button>
              <el-button type="primary" size="small" :loading="submitting" @click="submitReply(comment.id)">回复</el-button>
            </div>
          </div>

          <!-- 子回复 -->
          <div v-if="comment.children && comment.children.length" class="replies">
            <div v-for="reply in comment.children" :key="reply.id" class="reply-item">
              <div class="r-avatar">{{ (reply.authorName || reply.nickname || '?').charAt(0) }}</div>
              <div class="r-body">
                <div class="c-meta">
                  <span class="c-author">{{ reply.authorName || reply.nickname || '匿名' }}</span>
                  <span v-if="reply.replyToName" class="reply-to">
                    <el-icon><CaretRight /></el-icon>
                    {{ reply.replyToName === (reply.authorName || reply.nickname) ? '自己' : reply.replyToName }}
                  </span>
                  <span class="c-date">{{ formatTime(reply.createdAt) }}</span>
                </div>
                <div class="c-text">{{ reply.content }}</div>
                <div class="c-actions">
                  <el-button text size="small" @click="toggleReply(comment.id, reply.authorName || reply.nickname)">回复</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { commentApi } from '@/api/commentApi'
import { useAuthStore } from '@/stores/auth'
import { CaretRight } from '@element-plus/icons-vue'

const props = defineProps({ slug: { type: String, required: true } })

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)

const comments = ref([])
const loading = ref(true)
const total = ref(0)

// 顶部表单
const formNickname = ref('')
const formEmail = ref('')
const formContent = ref('')

// 内联回复
const activeReplyId = ref(null)
const activeReplyTarget = ref('')
const replyFormNickname = ref('')
const replyFormContent = ref('')
const submitting = ref(false)

function toggleReply(commentId, targetName) {
  if (activeReplyId.value === commentId) {
    activeReplyId.value = null
  } else {
    activeReplyId.value = commentId
    activeReplyTarget.value = targetName
    replyFormContent.value = ''
  }
}

function formatTime(s) { return s ? s.slice(0, 16).replace('T', ' ') : '' }

async function fetchComments() {
  loading.value = true
  try {
    const res = await commentApi.list(props.slug)
    comments.value = res || []
    total.value = (res || []).reduce((s, c) => s + 1 + (c.children?.length || 0), 0)
  } catch { comments.value = [] }
  finally { loading.value = false }
}

async function submitComment() {
  const content = formContent.value.trim()
  if (!content) return
  submitting.value = true
  const data = { content }
  if (!isLoggedIn.value) {
    data.nickname = formNickname.value.trim() || '匿名'
    data.email = formEmail.value.trim()
  }
  try {
    await commentApi.create(props.slug, data)
    formContent.value = ''; formNickname.value = ''; formEmail.value = ''
    await fetchComments()
  } catch {}
  finally { submitting.value = false }
}

async function submitReply(commentId) {
  const content = replyFormContent.value.trim()
  if (!content) return
  submitting.value = true
  const data = { content, parentId: commentId, replyToName: activeReplyTarget.value }
  if (!isLoggedIn.value) {
    data.nickname = replyFormNickname.value.trim() || '匿名'
  }
  try {
    await commentApi.create(props.slug, data)
    replyFormContent.value = ''; replyFormNickname.value = ''
    activeReplyId.value = null; activeReplyTarget.value = ''
    await fetchComments()
  } catch {}
  finally { submitting.value = false }
}

onMounted(fetchComments)
</script>

<style scoped>
.blog-comments { margin-top: 40px; padding-top: 24px; border-top: 1px solid #e4e7ed; }
.comments-title { font-size: 18px; color: #303133; margin: 0 0 20px; }
.top-form { margin-bottom: 24px; padding: 16px; background: #f5f7fa; border-radius: 8px; }
.form-row { display: flex; gap: 8px; margin-bottom: 8px; }
.form-input { flex: 1; }
.form-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
.loading, .empty { padding: 20px 0; text-align: center; color: #909399; }
.empty p { margin: 0; }
.comment-list { display: flex; flex-direction: column; gap: 16px; }
.comment-item, .reply-item { display: flex; gap: 12px; }
.c-avatar, .r-avatar {
  width: 36px; height: 36px; border-radius: 50%; background: #409eff; color: #fff;
  display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 600; flex-shrink: 0;
}
.r-avatar { width: 28px; height: 28px; font-size: 12px; }
.c-body, .r-body { flex: 1; min-width: 0; }
.c-meta { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; flex-wrap: wrap; }
.c-author { font-size: 14px; font-weight: 600; color: #303133; }
.reply-to { font-size: 12px; color: #909399; display: inline-flex; align-items: center; gap: 2px; }
.c-date { font-size: 12px; color: #c0c4cc; }
.c-text { font-size: 14px; color: #606266; line-height: 1.6; word-break: break-word; }
.c-actions { margin-top: 4px; }
.inline-form { margin-top: 8px; padding: 12px; background: #f5f7fa; border-radius: 8px; }
.replies { margin-top: 12px; padding-left: 12px; border-left: 2px solid #e4e7ed; display: flex; flex-direction: column; gap: 12px; }
</style>
