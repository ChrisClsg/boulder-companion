import { authApi } from 'src/boot/axios'
import { useAuthStore } from 'stores/authStore'
import type { User } from 'src/types'

export const authService = {
  async fetchUser(): Promise<User> {
    const store = useAuthStore()
    if (store.isAuthenticated && store.user) {
      return store.user
    }
    await store.fetchUser()
    return store.user!
  },

  login(): void {
    const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
    authApi.login(`${host}/oauth2/authorization/github`)
  },

  logout(): void {
    const store = useAuthStore()
    store.logout()
    window.location.href = '/logout'
  },
}
