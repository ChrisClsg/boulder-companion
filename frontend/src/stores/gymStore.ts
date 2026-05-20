import { defineStore } from 'pinia'
import { gymApi } from 'src/api'
import type { Gym } from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

export const useGymStore = defineStore('gyms', {
  state: () => ({
    gyms: [] as Gym[],
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    getGym: (state) => {
      return (id: string): Gym | undefined =>
        state.gyms.find((gym) => gym.id === id)
    },
  },

  actions: {
    async fetchGyms(): Promise<Gym[]> {
      this.isLoading = true
      this.error = null

      try {
        const gyms = await gymApi.getAll()
        this.gyms = gyms
        return gyms
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch gyms')
        throw error
      } finally {
        this.isLoading = false
      }
    },

  },
})
