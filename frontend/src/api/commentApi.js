import request from './request'

export const commentApi = {
  list(slug) { return request.get(`/posts/${slug}/comments`) },
  create(slug, data) { return request.post(`/posts/${slug}/comments`, data) },
  delete(id) { return request.delete(`/comments/${id}`) },
}
