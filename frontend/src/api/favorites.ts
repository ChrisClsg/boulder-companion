import { api } from 'src/boot/axios'
import type { Gym } from 'src/types'

export const favoriteApi = {
  async getFavoriteGyms(): Promise<Gym[]> {
    const response = await api.get<Gym[]>('/users/me/favorite-gyms')
    return response.data
  },

  async addFavoriteGym(gymId: string): Promise<Gym[]> {
    const response = await api.put<Gym[]>(`/users/me/favorite-gyms/${gymId}`)
    return response.data
  },

  async removeFavoriteGym(gymId: string): Promise<Gym[]> {
    const response = await api.delete<Gym[]>(`/users/me/favorite-gyms/${gymId}`)
    return response.data
  },
}
