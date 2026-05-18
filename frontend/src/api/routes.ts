import { api } from 'src/boot/axios'
import type { Route } from 'src/types'

export type CreateRoutePayload = Omit<Route, 'id' | 'createdAt' | 'updatedAt'>
export type UpdateRoutePayload = Partial<Omit<Route, 'id' | 'createdAt' | 'updatedAt'>>

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

  async getByArchived(gymId: string): Promise<Route[]> {
    const response = await api.get<Route[]>(`/routes/${gymId}/archived`)
    return response.data
  },

  async create(route: CreateRoutePayload): Promise<Route> {
    const response = await api.post<Route>('/routes', route)
    return response.data
  },

  async update(id: string, route: UpdateRoutePayload): Promise<Route> {
    const response = await api.put<Route>(`/routes/${id}`, route)
    return response.data
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/routes/${id}`)
  },

  async archive(id: string): Promise<Route> {
    const response = await api.post<Route>(`/routes/${id}/archive`)
    return response.data
  },

  async bulkCreate(request: CreateRoutePayload[]): Promise<Route[]> {
    const response = await api.post<Route[]>('/routes/bulk', request)
    return response.data
  },
}
