import request from './request'

export const postApi = {
  list(params) { return request.get('/posts', { params }) },
  detail(slug) { return request.get(`/posts/${slug}`) },
  create(data) { return request.post('/posts', data) },
  update(id, data) { return request.put(`/posts/${id}`, data) },
  delete(id) { return request.delete(`/posts/${id}`) },
  myPosts(params) { return request.get('/posts/my', { params }) },
}
