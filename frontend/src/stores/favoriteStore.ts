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

  getters: {},

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

  },
})
