import { api } from 'src/boot/axios'
import type { ClimbLog, CreateClimbLogPayload } from 'src/types'

export const climbLogApi = {
  async getAll(params?: {
    gymId?: string
    routeId?: string
    sessionId?: string
    topped?: boolean
    from?: string
    to?: string
  }): Promise<ClimbLog[]> {
    const response = await api.get<ClimbLog[]>('/climb-logs', {
      params,
    })

    return response.data
  },

  async create(payload: CreateClimbLogPayload): Promise<ClimbLog> {
    const response = await api.post<ClimbLog>('/climb-logs', payload)
    return response.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/climb-logs/${id}`)
  },
}
