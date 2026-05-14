import { defineBoot } from '#q-app/wrappers'
import axios from 'axios'
import type { ClimbingHistory, Gym, Route } from 'src/types'

const backendUrl =
  window.location.host === 'localhost:5173'
    ? 'http://localhost:8080'
    : window.location.origin

const api = axios.create({
  baseURL: backendUrl + '/api',
  withCredentials: true,
  withXSRFToken: true,
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
  getCurrentUser: () => api.get('/auth/me'),
  getCsrfToken: () => api.get('/auth/csrf'),
  logout: () => api.post('/auth/logout'),
}

export const oauthApi = {
  loginWithGithub: () => {
    window.location.href = `${backendUrl}/oauth2/authorization/github`
  },
}

export type CreateGymPayload = Omit<Gym, 'id' | 'createdAt' | 'updatedAt'>
export type UpdateGymPayload = Omit<Partial<Gym>, 'id' | 'createdAt' | 'updatedAt'>

export const gymApi = {
  async getAll(): Promise<Gym[]> {
    const response = await api.get<Gym[]>('/gyms')
    return response.data
  },

  async getById(id: string): Promise<Gym> {
    const response = await api.get<Gym>(`/gyms/${id}`)
    return response.data
  },

  async getByAdmin(adminId: string): Promise<Gym[]> {
    const response = await api.get<Gym[]>(`/gyms/admin/${adminId}`)
    return response.data
  },

  async create(gym: CreateGymPayload): Promise<Gym> {
    const response = await api.post<Gym>('/gyms', gym)
    return response.data
  },

  async update(id: string, gym: UpdateGymPayload): Promise<Gym> {
    const response = await api.put<Gym>(`/gyms/${id}`, gym)
    return response.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/gyms/${id}`)
  },
}

export type UpdateRoutePayload = Partial<Omit<Route, 'id' | 'createdAt' | 'updatedAt'>>
export const routeApi = {
  getByGym: (gymId: string) => api.get(`/routes?gymId=${gymId}`),
  getById: (id: string) => api.get(`/routes/${id}`),
  getByArchived: (gymId: string) => api.get(`/routes/${gymId}/archived`),
  create: (route: Route) => api.post('/routes', route),
  update: (id: string, route: UpdateRoutePayload) => api.put(`/routes/${id}`, route),
  delete: (id: string) => api.delete(`/routes/${id}`),
  archive: (id: string) => api.post(`/routes/${id}/archive`),
  bulkCreate: (request: Route[]) => api.post('/routes/bulk', request),
}

export type UpdateHistoryPayload = Partial<Omit<ClimbingHistory, 'id' | 'createdAt'>>
export const historyApi = {
  getAll: (params?: Record<string, string>) => api.get('/history', { params }),
  getByUser: (userId: string) => api.get('/history', { params: { userId } }),
  getByGym: (gymId: string) => api.get('/history', { params: { gymId } }),
  getById: (id: string) => api.get(`/history/${id}`),
  getByRoute: (routeId: string) => api.get('/history', { params: { routeId } }),
  create: (history: ClimbingHistory) => api.post('/history', history),
  update: (id: string, history: UpdateHistoryPayload) => api.put(`/history/${id}`, history),
  delete: (id: string) => api.delete(`/history/${id}`),
  getToppedByUser: (userId: string) => api.get('/history/topped', { params: { userId } }),
  getToppedByGym: (gymId: string) => api.get('/history/topped', { params: { gymId } }),
}

export const favoriteApi = {
  getGyms: () => api.get('/favorites/gyms'),
  add: (gymId: string) => api.post(`/favorites/${gymId}`),
  remove: (gymId: string) => api.delete(`/favorites/${gymId}`),
}
