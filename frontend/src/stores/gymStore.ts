import { defineStore } from 'pinia'
import { gymApi } from '#boot'
import type { Gym } from '#types'

export const useGymStore = defineStore('gyms', {
  state: () => ({
    gyms: [] as Gym[],
    currentGym: null as Gym | null,
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    getGym: (state) => (id: string) => state.gyms.find(g => g.id === id) || state.currentGym,
    gymsCount: (state) => state.gyms.length,
  },

  actions: {
    async fetchGyms() {
      this.isLoading = true
      this.error = null
      try {
        const response = await gymApi.getAll()
        this.gyms = response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to fetch gyms'
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async fetchGymById(id: string) {
      this.isLoading = true
      this.error = null
      try {
        const response = await gymApi.getById(id)
        this.currentGym = response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to fetch gym'
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async createGym(gym: Gym) {
      this.error = null
      try {
        const response = await gymApi.create(gym)
        this.gyms.unshift(response.data)
        return response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to create gym'
        throw error
      }
    },

    async updateGym(id: string, updates: Partial<Gym>) {
      this.error = null
      try {
        const response = await gymApi.update(id, { ...this.getGym(id), ...updates })
        const index = this.gyms.findIndex(g => g.id === id)
        if (index !== -1) {
          this.gyms[index] = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to update gym'
        throw error
      }
    },

    async deleteGym(id: string) {
      this.error = null
      try {
        await gymApi.delete(id)
        this.gyms = this.gyms.filter(g => g.id !== id)
      } catch (error: any) {
        this.error = error.message || 'Failed to delete gym'
        throw error
      }
    },
  },
})
