import { api } from 'src/boot/axios'
import type { User } from 'src/types'

export const authApi = {
  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/auth/me')
    return response.data
  },

  async getCsrfToken(): Promise<void> {
    await api.get('/auth/csrf')
  },

  async logout(): Promise<void> {
    await api.post('/auth/logout')
  },
}
