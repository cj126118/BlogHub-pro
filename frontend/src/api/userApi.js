import request from './request'

export const userApi = {
  getProfile() { return request.get('/users/profile') },
  updateProfile(data) { return request.put('/users/profile', data) },
  getPublicInfo(id) { return request.get(`/users/${id}`) },
}
