import request from './request'

export const interactionApi = {
  getLikeStatus(slug) { return request.get(`/posts/${slug}/like`) },
  toggleLike(slug) { return request.post(`/posts/${slug}/like`) },
  getBookmarks() { return request.get('/bookmarks') },
  toggleBookmark(slug) { return request.post(`/bookmarks/toggle`, { slug }) },
}
