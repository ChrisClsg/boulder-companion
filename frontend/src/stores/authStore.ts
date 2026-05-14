import { defineStore } from 'pinia'
import { authApi } from 'src/boot/axios'
import type { User } from 'src/types'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    isAuthenticated: false,
    isLoading: false,
  }),

  getters: {
    hasRole: (state) => (role: string) => {
      if (!state.user) return false
      return (
        state.user.role === role ||
        (role === 'ADMIN' && state.user.role !== 'CLIMBER') ||
        (role === 'ROUTE_SETTER' && state.user.role === 'ROUTE_SETTER')
      )
    },
    isClimber: (state) => state.user?.role === 'CLIMBER',
    isAdmin: (state) => state.user?.role === 'GYM_ADMIN',
    isRouteSetter: (state) => state.user?.role === 'ROUTE_SETTER',
  },

  actions: {
    async fetchUser() {
      this.isLoading = true
      try {
        const response = await authApi.getCurrentUser()
        this.user = response.data as User
        this.isAuthenticated = true
      } catch (error) {
        console.error('Failed to fetch user:', error)
        this.clearUser()
      } finally {
        this.isLoading = false
      }
    },

    login(callbackUrl: string) {
      authApi.login(callbackUrl)
    },

    logout() {
      this.user = null
      this.isAuthenticated = false
      authApi.logout()
    },

    clearUser() {
      this.user = null
      this.isAuthenticated = false
    },
  },
})
