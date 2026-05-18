import { api } from 'src/boot/axios'
import type { User } from 'src/types'

export const authApi = {
  getCurrentUser: () => api.get<User>('/auth/me'),

  getCsrfToken: () => api.get('/auth/csrf'),

  logout: () => api.post('/auth/logout'),
}
