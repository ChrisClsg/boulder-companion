import { api, historyApi } from '#boot'
import { useHistoryStore } from '#stores'
import type { ClimbingHistory } from '#types'

export const historyService = {
  async fetchHistory(userId?: string, gymId?: string, routeId?: string): Promise<ClimbingHistory[]> {
    const store = useHistoryStore()
    if (store.history.length > 0) {
      return store.history
    }
    await store.fetchHistory(userId, gymId, routeId)
    return store.history
  },

  async fetchHistoryById(id: string): Promise<ClimbingHistory> {
    const store = useHistoryStore()
    if (store.currentHistory && store.currentHistory.id === id) {
      return store.currentHistory
    }
    await store.fetchHistoryById(id)
    return store.currentHistory!
  },

  async createHistory(history: ClimbingHistory): Promise<ClimbingHistory> {
    const store = useHistoryStore()
    await store.createHistory(history)
    return history
  },

  async updateHistory(id: string, updates: Partial<ClimbingHistory>): Promise<ClimbingHistory> {
    const store = useHistoryStore()
    await store.updateHistory(id, updates)
    return store.getHistory(id)!
  },

  async deleteHistory(id: string): Promise<void> {
    const store = useHistoryStore()
    await store.deleteHistory(id)
  },

  async fetchToppedRoutes(userId?: string, gymId?: string): Promise<ClimbingHistory[]> {
    const store = useHistoryStore()
    if (store.history.length > 0) {
      return store.history.filter(h => h.topped)
    }
    await store.fetchToppedRoutes(userId, gymId)
    return store.history.filter(h => h.topped)
  },
}
