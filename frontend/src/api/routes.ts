import { api } from 'src/boot/axios'
import type { Route } from 'src/types'

export const routeApi = {
  async getByGym(gymId: string): Promise<Route[]> {
    const response = await api.get<Route[]>('/routes', {
      params: { gymId },
    })

    return response.data
  },

  async getById(id: string): Promise<Route> {
    const response = await api.get<Route>(`/routes/${id}`)
    return response.data
  },
}
