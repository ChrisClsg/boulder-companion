import { defineBoot } from '#q-app/wrappers'
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api',
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
})

export default defineBoot(({ app }) => {
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
})

export { axios, api }

export const authApi = {
  login: (callbackUrl: string) => {
    window.location.href = callbackUrl
  },
  logout: () => {
    window.location.href = '/logout'
  },
  getCurrentUser: () => api.get('/auth/me'),
  getOAuth2User: () => api.get('/auth/oauth2-user'),
}

export const gymApi = {
  getAll: () => api.get('/gyms'),
  getById: (id: string) => api.get(`/gyms/${id}`),
  getByAdmin: (adminId: string) => api.get(`/gyms/admin/${adminId}`),
  create: (gym: any) => api.post('/gyms', gym),
  update: (id: string, gym: any) => api.put(`/gyms/${id}`, gym),
  delete: (id: string) => api.delete(`/gyms/${id}`),
}

export const routeApi = {
  getByGym: (gymId: string) => api.get(`/routes?gymId=${gymId}`),
  getById: (id: string) => api.get(`/routes/${id}`),
  getByArchived: (gymId: string) => api.get(`/routes/${gymId}/archived`),
  create: (route: any) => api.post('/routes', route),
  update: (id: string, route: any) => api.put(`/routes/${id}`, route),
  delete: (id: string) => api.delete(`/routes/${id}`),
  archive: (id: string) => api.post(`/routes/${id}/archive`),
  bulkCreate: (request: any) => api.post('/routes/bulk', request),
}

export const historyApi = {
  getAll: (params?: any) => api.get('/history', { params }),
  getByUser: (userId: string) => api.get('/history', { params: { userId } }),
  getByGym: (gymId: string) => api.get('/history', { params: { gymId } }),
  getById: (id: string) => api.get(`/history/${id}`),
  getByRoute: (routeId: string) => api.get('/history', { params: { routeId } }),
  create: (history: any) => api.post('/history', history),
  update: (id: string, history: any) => api.put(`/history/${id}`, history),
  delete: (id: string) => api.delete(`/history/${id}`),
  getToppedByUser: (userId: string) => api.get('/history/topped', { params: { userId } }),
  getToppedByGym: (gymId: string) => api.get('/history/topped', { params: { gymId } }),
}

export const favoriteApi = {
  add: (gymId: string) => api.post(`/favorites/${gymId}`),
  remove: (gymId: string) => api.delete(`/favorites/${gymId}`),
}
