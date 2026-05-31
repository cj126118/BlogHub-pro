import { ref, computed } from 'vue'
import { interactionApi } from '@/api/interactionApi'
import { commentApi } from '@/api/commentApi'

/**
 * 点赞功能
 */
export function useLikes(slug) {
  const liked = ref(false)
  const count = ref(0)

  async function fetchStatus() {
    try {
      const res = await interactionApi.getLikeStatus(slug)
      liked.value = res.liked
      count.value = res.count
    } catch { /* 静默 */ }
  }

  async function toggleLike() {
    try {
      const res = await interactionApi.toggleLike(slug)
      liked.value = res.liked
      count.value = res.count
    } catch { /* 静默 */ }
  }

  fetchStatus()
  return { liked, count, toggleLike }
}

/**
 * 收藏功能
 */
export function useBookmarks(slug) {
  const allBookmarks = ref([])
  const bookmarked = computed(() => slug ? allBookmarks.value.includes(slug) : false)
  const bookmarksList = computed(() => allBookmarks.value)

  async function fetchBookmarks() {
    try {
      const res = await interactionApi.getBookmarks()
      allBookmarks.value = (res || []).map(p => p.slug)
    } catch { /* 静默 */ }
  }

  async function toggleBookmark() {
    try {
      const res = await interactionApi.toggleBookmark(slug)
      if (res.bookmarked) {
        if (!allBookmarks.value.includes(slug)) allBookmarks.value.push(slug)
      } else {
        allBookmarks.value = allBookmarks.value.filter(s => s !== slug)
      }
    } catch { /* 静默 */ }
  }

  function isBookmarked(s) { return allBookmarks.value.includes(s) }

  fetchBookmarks()
  return { bookmarked, bookmarksList, toggleBookmark, isBookmarked }
}

/**
 * 评论功能
 */
export function useComments(slug) {
  const comments = ref([])

  async function fetchComments() {
    try {
      const res = await commentApi.list(slug)
      comments.value = res || []
    } catch { comments.value = [] }
  }

  async function addComment(nickname, content) {
    if (!nickname.trim() || !content.trim()) return
    try {
      await commentApi.create(slug, { nickname, content })
      await fetchComments()
    } catch { /* 拦截器已提示 */ }
  }

  fetchComments()
  return { comments, addComment, fetchComments }
}
