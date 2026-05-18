import { defineStore } from 'pinia'
import { favoriteApi } from 'src/api'
import type { Gym } from 'src/types'

export const useFavoriteStore = defineStore('favorites', {
  state: () => ({
    gyms: [] as Gym[],
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    gymIds: (state): string[] => state.gyms.map((g: Gym) => g.id),
  },

  actions: {
    async fetchGyms() {
      this.isLoading = true
      this.error = null
      try {
        const response = await favoriteApi.getGyms()
        this.gyms = response.data
      } catch (error: unknown) {
        this.error = typeof error === 'string' ? error : (error as { message?: string }).message || 'Failed to fetch favorites'
      } finally {
        this.isLoading = false
      }
    },

    async addFavorite(gymId: string) {
      this.error = null
      try {
        await favoriteApi.add(gymId)
        await this.fetchGyms()
      } catch (error: unknown) {
        this.error = typeof error === 'string' ? error : (error as { message?: string }).message || 'Failed to add favorite'
      }
    },

    async removeFavorite(gymId: string) {
      this.error = null
      try {
        await favoriteApi.remove(gymId)
        await this.fetchGyms()
      } catch (error: unknown) {
        this.error = typeof error === 'string' ? error : (error as { message?: string }).message || 'Failed to remove favorite'
      }
    },

    clearError(): void {
      this.error = null
    },
  },
})
