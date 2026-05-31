import request from './request'

export const categoryApi = {
  list() { return request.get('/categories') },
  detail(id) { return request.get(`/categories/${id}`) },
  create(data) { return request.post('/categories', data) },
  update(id, data) { return request.put(`/categories/${id}`, data) },
  delete(id) { return request.delete(`/categories/${id}`) },
}
