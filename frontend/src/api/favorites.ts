import { api } from 'src/boot/axios'
import type { Gym } from 'src/types'

export const favoriteApi = {
  getGyms: () => api.get<Gym[]>('/users/me/favorite-gyms'),

  add: (gymId: string) => api.put<Gym[]>(`/users/me/favorite-gyms/${gymId}`),

  remove: (gymId: string) => api.delete<Gym[]>(`/users/me/favorite-gyms/${gymId}`),
}
