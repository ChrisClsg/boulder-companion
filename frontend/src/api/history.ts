import { api } from 'src/boot/axios'
import type { ClimbingHistory } from 'src/types'

export type CreateHistoryPayload = Omit<ClimbingHistory, 'id' | 'createdAt'>
export type UpdateHistoryPayload = Partial<Omit<ClimbingHistory, 'id' | 'createdAt'>>

export const historyApi = {
  async getAll(params?: Record<string, string>): Promise<ClimbingHistory[]> {
    const response = await api.get<ClimbingHistory[]>('/history', { params })
    return response.data
  },

  async getByUser(userId: string): Promise<ClimbingHistory[]> {
    const response = await api.get<ClimbingHistory[]>('/history', {
      params: { userId },
    })

    return response.data
  },

  async getByGym(gymId: string): Promise<ClimbingHistory[]> {
    const response = await api.get<ClimbingHistory[]>('/history', {
      params: { gymId },
    })

    return response.data
  },

  async getById(id: string): Promise<ClimbingHistory> {
    const response = await api.get<ClimbingHistory>(`/history/${id}`)
    return response.data
  },

  async getByRoute(routeId: string): Promise<ClimbingHistory[]> {
    const response = await api.get<ClimbingHistory[]>('/history', {
      params: { routeId },
    })

    return response.data
  },

  async create(history: CreateHistoryPayload): Promise<ClimbingHistory> {
    const response = await api.post<ClimbingHistory>('/history', history)
    return response.data
  },

  async update(
    id: string,
    history: UpdateHistoryPayload,
  ): Promise<ClimbingHistory> {
    const response = await api.put<ClimbingHistory>(`/history/${id}`, history)
    return response.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/history/${id}`)
  },

  async getToppedByUser(userId: string): Promise<ClimbingHistory[]> {
    const response = await api.get<ClimbingHistory[]>('/history/topped', {
      params: { userId },
    })

    return response.data
  },

  async getToppedByGym(gymId: string): Promise<ClimbingHistory[]> {
    const response = await api.get<ClimbingHistory[]>('/history/topped', {
      params: { gymId },
    })

    return response.data
  },
}
