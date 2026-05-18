import { api } from 'src/boot/axios'
import type { ClimbingHistory } from 'src/types'

export type UpdateHistoryPayload = Partial<Omit<ClimbingHistory, 'id' | 'createdAt'>>

export const historyApi = {
  getAll: (params?: Record<string, string>) =>
    api.get<ClimbingHistory[]>('/history', { params }),

  getByUser: (userId: string) =>
    api.get<ClimbingHistory[]>('/history', { params: { userId } }),

  getByGym: (gymId: string) =>
    api.get<ClimbingHistory[]>('/history', { params: { gymId } }),

  getById: (id: string) =>
    api.get<ClimbingHistory>(`/history/${id}`),

  getByRoute: (routeId: string) =>
    api.get<ClimbingHistory[]>('/history', { params: { routeId } }),

  create: (history: ClimbingHistory) =>
    api.post<ClimbingHistory>('/history', history),

  update: (id: string, history: UpdateHistoryPayload) =>
    api.put<ClimbingHistory>(`/history/${id}`, history),

  delete: (id: string) =>
    api.delete(`/history/${id}`),

  getToppedByUser: (userId: string) =>
    api.get<ClimbingHistory[]>('/history/topped', { params: { userId } }),

  getToppedByGym: (gymId: string) =>
    api.get<ClimbingHistory[]>('/history/topped', { params: { gymId } }),
}
