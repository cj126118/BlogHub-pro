import request from './request'

export const tagApi = {
  list() { return request.get('/tags') },
  detail(id) { return request.get(`/tags/${id}`) },
  create(data) { return request.post('/tags', data) },
  update(id, data) { return request.put(`/tags/${id}`, data) },
  delete(id) { return request.delete(`/tags/${id}`) },
}
