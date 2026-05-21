import { defineStore } from 'pinia'
import { climbStatsApi } from 'src/api'
import type { ClimbStatsSummary } from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

export const useClimbStatsStore = defineStore('climbStats', {
  state: () => ({
    summary: null as ClimbStatsSummary | null,
    isLoading: false,
    error: null as string | null,
  }),

  getters: {},

  actions: {
    async fetchSummary(): Promise<ClimbStatsSummary> {
      this.isLoading = true
      this.error = null

      try {
        const summary = await climbStatsApi.getSummary()
        this.summary = summary
        return summary
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch climb statistics')
        throw error
      } finally {
        this.isLoading = false
      }
    },
  },
})
