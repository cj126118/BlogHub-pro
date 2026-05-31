import request from './request'

export const statsApi = {
  dashboard() { return request.get('/admin/stats/dashboard') },
  users(params) { return request.get('/admin/users', { params }) },
  toggleUserStatus(id, status) { return request.put(`/admin/users/${id}/status`, { status }) },
  comments(params) { return request.get('/admin/comments', { params }) },
  toggleCommentStatus(id, status) { return request.put(`/admin/comments/${id}/status`, { status }) },
  logs(params) { return request.get('/admin/logs', { params }) },
}
