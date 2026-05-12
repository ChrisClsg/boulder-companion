import { defineStore } from 'pinia'
import { gymApi, type CreateGymPayload, type UpdateGymPayload } from 'boot/axios'
import type { Gym } from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

export const useGymStore = defineStore('gyms', {
  state: () => ({
    gyms: [] as Gym[],
    currentGym: null as Gym | null,
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    getGym: (state) => {
      return (id: string): Gym | undefined =>
        state.gyms.find((gym) => gym.id === id) ??
        (state.currentGym?.id === id ? state.currentGym : undefined)
    },

    gymsCount: (state): number => state.gyms.length,
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

    async fetchGymById(id: string): Promise<Gym> {
      this.isLoading = true
      this.error = null

      try {
        const gym = await gymApi.getById(id)
        this.currentGym = gym

        const index = this.gyms.findIndex((item) => item.id === gym.id)

        if (index === -1) {
          this.gyms.push(gym)
        } else {
          this.gyms[index] = gym
        }

        return gym
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch gym')
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async fetchGymsByAdmin(adminId: string): Promise<Gym[]> {
      this.isLoading = true
      this.error = null

      try {
        const gyms = await gymApi.getByAdmin(adminId)
        this.gyms = gyms
        return gyms
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch admin gyms')
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async createGym(gym: CreateGymPayload): Promise<Gym> {
      this.error = null

      try {
        const createdGym = await gymApi.create(gym)
        this.gyms.unshift(createdGym)
        this.currentGym = createdGym
        return createdGym
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to create gym')
        throw error
      }
    },

    async updateGym(id: string, updates: UpdateGymPayload): Promise<Gym> {
      this.error = null

      try {
        const updatedGym = await gymApi.update(id, updates)

        const index = this.gyms.findIndex((gym) => gym.id === id)

        if (index !== -1) {
          this.gyms[index] = updatedGym
        }

        if (this.currentGym?.id === id) {
          this.currentGym = updatedGym
        }

        return updatedGym
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to update gym')
        throw error
      }
    },

    async deleteGym(id: string): Promise<void> {
      this.error = null

      try {
        await gymApi.delete(id)

        this.gyms = this.gyms.filter((gym) => gym.id !== id)

        if (this.currentGym?.id === id) {
          this.currentGym = null
        }
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to delete gym')
        throw error
      }
    },

    clearCurrentGym(): void {
      this.currentGym = null
    },

    clearError(): void {
      this.error = null
    },
  },
})
