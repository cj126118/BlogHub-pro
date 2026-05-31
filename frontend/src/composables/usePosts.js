import { ref } from 'vue'
import { postApi } from '@/api/postApi'
import { markdownToHtml } from './useMarkdown'

/**
 * 格式化文章原始数据
 */
function formatPost(raw) {
  const markdown = raw.content || ''
  let html = ''
  try {
    const rendered = markdownToHtml(markdown)
    html = rendered.html || ''
  } catch (e) {
    html = '<pre style="white-space:pre-wrap;background:#f5f7fa;padding:16px;border-radius:8px">'
         + markdown.replace(/</g, '&lt;').replace(/>/g, '&gt;') + '</pre>'
  }
  return {
    id: raw.id,
    slug: raw.slug,
    title: raw.title || raw.slug,
    date: raw.createdAt ? raw.createdAt.slice(0, 10) : '',
    tags: raw.tags || [],
    description: raw.description || '',
    html,
    content: markdown,
    views: raw.views || 0,
    likes: raw.likes || 0,
    author: raw.authorName || '',
    authorId: raw.authorId || null,
    authorAvatar: raw.authorAvatar || '',
    coverImage: raw.coverImage || '',
    isPinned: raw.isPinned || 0,
    categoryId: raw.categoryId,
    categoryName: raw.categoryName || '',
    status: raw.status,
  }
}

export function usePosts() {
  const posts = ref([])
  const loading = ref(false)
  const error = ref(null)
  const total = ref(0)

  async function fetchPosts(params = {}) {
    loading.value = true
    error.value = null
    try {
      const res = await postApi.list({ page: 1, size: 50, ...params })
      const list = res.list || []
      posts.value = list.map(formatPost)
      total.value = res.total || 0
    } catch (e) {
      error.value = e.message || '加载文章失败'
      posts.value = []
    } finally {
      loading.value = false
    }
  }

  async function getPost(slug) {
    loading.value = true
    error.value = null
    try {
      const raw = await postApi.detail(slug)
      return formatPost(raw)
    } catch (e) {
      error.value = e.message || '加载文章失败'
      return null
    } finally {
      loading.value = false
    }
  }

  function getTags() {
    const tagSet = new Set()
    posts.value.forEach(p => (p.tags || []).forEach(t => tagSet.add(t)))
    return [...tagSet]
  }

  return { posts, loading, error, total, fetchPosts, getPost, getTags }
}
