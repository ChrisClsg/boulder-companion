import { defineStore } from 'pinia'
import { authApi } from 'src/api/auth'
import { oauthApi } from 'src/api/oauth'
import type { User } from 'src/types'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    isAuthenticated: false,
    isLoading: false,
    hasFetchedUser: false,
  }),

  getters: {},

  actions: {
    async initAuth() {
      this.isLoading = true

      try {
        await authApi.getCsrfToken()
        await this.fetchUser()
      } finally {
        this.isLoading = false
      }
    },

    async fetchUser() {
      if (this.hasFetchedUser) {
        return this.user
      }

      try {
        const response = await authApi.getCurrentUser()

        this.user = response
        this.isAuthenticated = true
        this.hasFetchedUser = true

        return this.user
      } catch {
        this.clearUser()
        this.hasFetchedUser = true

        return null
      }
    },

    loginWithGithub() {
      oauthApi.loginWithGithub()
    },

    async logout() {
      this.isLoading = true

      try {
        await authApi.getCsrfToken()
        await authApi.logout()
      } finally {
        this.clearUser()
        this.hasFetchedUser = true
        this.isLoading = false
      }
    },

    clearUser() {
      this.user = null
      this.isAuthenticated = false
    },
  },
})
