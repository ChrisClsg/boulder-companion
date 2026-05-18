import { defineStore } from 'pinia'
import { climbLogApi } from 'src/api'
import type {
  ClimbLog,
  CreateClimbLogPayload,
  RoutePersonalSummary,
  UpdateClimbLogPayload,
} from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

type FetchClimbLogsParams = {
  gymId?: string
  routeId?: string
  sessionId?: string
  topped?: boolean
  from?: string
  to?: string
}

export const useClimbLogStore = defineStore('climbLogs', {
  state: () => ({
    logs: [] as ClimbLog[],
    currentLog: null as ClimbLog | null,
    isLoading: false,
    isSaving: false,
    error: null as string | null,
  }),

  getters: {
    getLogsByRoute: (state) => {
      return (routeId: string): ClimbLog[] =>
        state.logs.filter(log => log.routeId === routeId)
    },

    getLogsByGym: (state) => {
      return (gymId: string): ClimbLog[] =>
        state.logs.filter(log => log.gymId === gymId)
    },

    getLogsBySession: (state) => {
      return (sessionId: string): ClimbLog[] =>
        state.logs.filter(log => log.sessionId === sessionId)
    },

    getLastLogByRoute: (state) => {
      return (routeId: string): ClimbLog | null =>
        state.logs
          .filter(log => log.routeId === routeId)
          .sort(
            (a, b) =>
              new Date(b.climbedAt).getTime() -
              new Date(a.climbedAt).getTime(),
          )[0] ?? null
    },

    getSummaryByRoute: (state) => {
      return (routeId: string): RoutePersonalSummary => {
        const routeLogs = state.logs.filter(log => log.routeId === routeId)

        const sortedLogs = [...routeLogs].sort(
          (a, b) =>
            new Date(b.climbedAt).getTime() -
            new Date(a.climbedAt).getTime(),
        )

        return {
          totalLogs: routeLogs.length,
          totalAttempts: routeLogs.reduce((sum, log) => sum + log.attempts, 0),
          topped: routeLogs.some(log => log.topped),
          flashed: routeLogs.some(log => log.flashed),
          lastLog: sortedLogs[0] ?? null,
        }
      }
    },
  },

  actions: {
    async fetchMyLogs(params?: FetchClimbLogsParams): Promise<ClimbLog[]> {
      this.isLoading = true
      this.error = null

      try {
        const logs = await climbLogApi.getAll(params)

        if (!params || Object.keys(params).length === 0) {
          this.logs = logs
          return logs
        }

        this.mergeLogs(logs)

        return logs
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch climb logs')
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async fetchLogsByRoute(routeId: string): Promise<ClimbLog[]> {
      return this.fetchMyLogs({ routeId })
    },

    async fetchLogsByGym(gymId: string): Promise<ClimbLog[]> {
      return this.fetchMyLogs({ gymId })
    },

    async fetchLogsBySession(sessionId: string): Promise<ClimbLog[]> {
      return this.fetchMyLogs({ sessionId })
    },

    async fetchToppedLogs(): Promise<ClimbLog[]> {
      return this.fetchMyLogs({ topped: true })
    },

    async createLog(payload: CreateClimbLogPayload): Promise<ClimbLog> {
      this.isSaving = true
      this.error = null

      try {
        const createdLog = await climbLogApi.create(payload)

        this.logs = [
          createdLog,
          ...this.logs.filter(log => log.id !== createdLog.id),
        ]

        this.currentLog = createdLog

        return createdLog
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to create climb log')
        throw error
      } finally {
        this.isSaving = false
      }
    },

    async updateLog(
      id: string,
      payload: UpdateClimbLogPayload,
    ): Promise<ClimbLog> {
      this.isSaving = true
      this.error = null

      try {
        const updatedLog = await climbLogApi.update(id, payload)

        const index = this.logs.findIndex(log => log.id === id)

        if (index !== -1) {
          this.logs[index] = updatedLog
        } else {
          this.logs.unshift(updatedLog)
        }

        if (this.currentLog?.id === id) {
          this.currentLog = updatedLog
        }

        return updatedLog
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to update climb log')
        throw error
      } finally {
        this.isSaving = false
      }
    },

    async deleteLog(id: string): Promise<void> {
      this.error = null

      try {
        await climbLogApi.delete(id)

        this.logs = this.logs.filter(log => log.id !== id)

        if (this.currentLog?.id === id) {
          this.currentLog = null
        }
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to delete climb log')
        throw error
      }
    },

    mergeLogs(logs: ClimbLog[]): void {
      const byId = new Map<string, ClimbLog>()

      for (const log of this.logs) {
        byId.set(log.id, log)
      }

      for (const log of logs) {
        byId.set(log.id, log)
      }

      this.logs = [...byId.values()].sort(
        (a, b) =>
          new Date(b.climbedAt).getTime() -
          new Date(a.climbedAt).getTime(),
      )
    },

    clearCurrentLog(): void {
      this.currentLog = null
    },

    clearError(): void {
      this.error = null
    },
  },
})
