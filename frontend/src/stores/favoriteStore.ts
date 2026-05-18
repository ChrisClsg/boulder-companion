import { defineStore } from 'pinia'
import { favoriteApi } from 'src/api'
import type { Gym } from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

export const useFavoriteStore = defineStore('favorites', {
  state: () => ({
    favoriteGyms: [] as Gym[],
    isLoadingFavoriteGyms: false,
    error: null as string | null,
  }),

  getters: {
    favoriteGymIds: (state): string[] =>
      state.favoriteGyms.map((gym) => gym.id),

    favoriteGymsCount: (state): number =>
      state.favoriteGyms.length,

    isFavoriteGym: (state) => {
      return (gymId: string): boolean =>
        state.favoriteGyms.some((gym) => gym.id === gymId)
    },
  },

  actions: {
    async fetchFavoriteGyms(): Promise<Gym[]> {
      this.isLoadingFavoriteGyms = true
      this.error = null

      try {
        const gyms = await favoriteApi.getFavoriteGyms()
        this.favoriteGyms = gyms
        return gyms
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch favorite gyms')
        throw error
      } finally {
        this.isLoadingFavoriteGyms = false
      }
    },

    async addFavoriteGym(gymId: string): Promise<void> {
      this.error = null

      try {
        const gyms = await favoriteApi.addFavoriteGym(gymId)
        this.favoriteGyms = gyms
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to add favorite gym')
        throw error
      }
    },

    async removeFavoriteGym(gymId: string): Promise<void> {
      this.error = null

      try {
        const gyms = await favoriteApi.removeFavoriteGym(gymId)
        this.favoriteGyms = gyms
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to remove favorite gym')
        throw error
      }
    },

    async toggleFavoriteGym(gymId: string): Promise<void> {
      if (this.isFavoriteGym(gymId)) {
        await this.removeFavoriteGym(gymId)
      } else {
        await this.addFavoriteGym(gymId)
      }
    },

    clearError(): void {
      this.error = null
    },
  },
})
