import { api } from 'src/boot/axios'
import type { Gym } from 'src/types'

export const gymApi = {
  async getAll(): Promise<Gym[]> {
    const response = await api.get<Gym[]>('/gyms')
    return response.data
  },

  async getById(id: string): Promise<Gym> {
    const response = await api.get<Gym>(`/gyms/${id}`)
    return response.data
  },
}
