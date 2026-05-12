import { api, gymApi } from '#boot'
import { useGymStore } from '#stores'
import type { Gym } from '#types'

export const gymService = {
  async fetchGyms(): Promise<Gym[]> {
    const store = useGymStore()
    if (store.gyms.length > 0) {
      return store.gyms
    }
    await store.fetchGyms()
    return store.gyms
  },

  async fetchGymById(id: string): Promise<Gym> {
    const store = useGymStore()
    if (store.currentGym && store.currentGym.id === id) {
      return store.currentGym
    }
    await store.fetchGymById(id)
    return store.currentGym!
  },

  async createGym(gym: Gym): Promise<Gym> {
    const store = useGymStore()
    await store.createGym(gym)
    return gym
  },

  async updateGym(id: string, updates: Partial<Gym>): Promise<Gym> {
    const store = useGymStore()
    await store.updateGym(id, updates)
    return store.getGym(id)!
  },

  async deleteGym(id: string): Promise<void> {
    const store = useGymStore()
    await store.deleteGym(id)
  },
}
