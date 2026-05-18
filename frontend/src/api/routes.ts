import { api } from 'src/boot/axios'
import type { Route } from 'src/types'

export type UpdateRoutePayload = Partial<Omit<Route, 'id' | 'createdAt' | 'updatedAt'>>

export const routeApi = {
  getByGym: (gymId: string) => api.get<Route[]>('/routes', { params: { gymId } }),

  getById: (id: string) => api.get<Route>(`/routes/${id}`),

  getByArchived: (gymId: string) => api.get<Route[]>(`/routes/${gymId}/archived`),

  create: (route: Route) => api.post<Route>('/routes', route),

  update: (id: string, route: UpdateRoutePayload) => api.put<Route>(`/routes/${id}`, route),

  delete: (id: string) => api.delete(`/routes/${id}`),

  archive: (id: string) => api.post<Route>(`/routes/${id}/archive`),

  bulkCreate: (request: Route[]) => api.post<Route[]>('/routes/bulk', request),
}
