import { api } from 'src/boot/axios'
import type { Gym } from 'src/types'

export type CreateGymPayload = Omit<Gym, 'id' | 'createdAt' | 'updatedAt'>
export type UpdateGymPayload = Omit<Partial<Gym>, 'id' | 'createdAt' | 'updatedAt'>

export const gymApi = {
  async getAll(): Promise<Gym[]> {
    const response = await api.get<Gym[]>('/gyms')
    return response.data
  },

  async getById(id: string): Promise<Gym> {
    const response = await api.get<Gym>(`/gyms/${id}`)
    return response.data
  },

  async getByAdmin(adminId: string): Promise<Gym[]> {
    const response = await api.get<Gym[]>(`/gyms/admin/${adminId}`)
    return response.data
  },

  async create(gym: CreateGymPayload): Promise<Gym> {
    const response = await api.post<Gym>('/gyms', gym)
    return response.data
  },

  async update(id: string, gym: UpdateGymPayload): Promise<Gym> {
    const response = await api.put<Gym>(`/gyms/${id}`, gym)
    return response.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/gyms/${id}`)
  },
}
