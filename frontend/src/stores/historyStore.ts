import { defineStore } from 'pinia'
import { historyApi } from '#boot'
import type { ClimbingHistory } from '#types'

export const useHistoryStore = defineStore('history', {
  state: () => ({
    history: [] as ClimbingHistory[],
    currentHistory: null as ClimbingHistory | null,
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    getHistory: (state) => (id: string) => state.history.find(h => h.id === id) || state.currentHistory,
    historyCount: (state) => state.history.length,
    toppedCount: (state) => state.history.filter(h => h.topped).length,
    averageRating: (state) => {
      if (state.history.length === 0) return 0
      const sum = state.history.reduce((acc, h) => acc + h.userRating, 0)
      return Math.round((sum / state.history.length) * 10) / 10
    },
  },

  actions: {
    async fetchHistory(userId?: string, gymId?: string, routeId?: string) {
      this.isLoading = true
      this.error = null
      try {
        const params: Record<string, string> = {}
        if (userId) params.userId = userId
        if (gymId) params.gymId = gymId
        if (routeId) params.routeId = routeId

        const response = await historyApi.getAll(params)
        this.history = response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to fetch history'
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async fetchHistoryById(id: string) {
      this.isLoading = true
      this.error = null
      try {
        const response = await historyApi.getById(id)
        this.currentHistory = response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to fetch history'
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async createHistory(history: ClimbingHistory) {
      this.error = null
      try {
        const response = await historyApi.create(history)
        this.history.push(response.data)
        return response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to create history'
        throw error
      }
    },

    async updateHistory(id: string, updates: Partial<ClimbingHistory>) {
      this.error = null
      try {
        const response = await historyApi.update(id, { ...this.getHistory(id), ...updates })
        const index = this.history.findIndex(h => h.id === id)
        if (index !== -1) {
          this.history[index] = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to update history'
        throw error
      }
    },

    async deleteHistory(id: string) {
      this.error = null
      try {
        await historyApi.delete(id)
        this.history = this.history.filter(h => h.id !== id)
      } catch (error: any) {
        this.error = error.message || 'Failed to delete history'
        throw error
      }
    },

    async fetchToppedRoutes(userId?: string, gymId?: string) {
      this.isLoading = true
      this.error = null
      try {
        const params: Record<string, string> = {}
        if (userId) params.userId = userId
        if (gymId) params.gymId = gymId

        const response = await historyApi.getToppedByUser(userId || gymId || '')
        this.history = response.data
      } catch (error: any) {
        this.error = error.message || 'Failed to fetch topped routes'
        throw error
      } finally {
        this.isLoading = false
      }
    },
  },
})
