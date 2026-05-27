import { defineStore } from 'pinia'
import { routeApi } from 'src/api'
import type { Route } from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

export const useRouteStore = defineStore('routes', {
  state: () => ({
    routes: [] as Route[],
    currentRoute: null as Route | null,
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    getRoute: (state) => {
      return (id: string): Route | undefined =>
        state.routes.find(route => route.id === id) ??
        (state.currentRoute?.id === id ? state.currentRoute : undefined)
    },
  },

  actions: {
    async fetchRoutesByGym(gymId: string): Promise<Route[]> {
      this.isLoading = true
      this.error = null

      try {
        const fetched = await routeApi.getByGym(gymId)
        this.routes = [
          ...this.routes.filter(r => r.gymId !== gymId),
          ...fetched,
        ]
        return fetched
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch routes')
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async fetchRouteById(id: string): Promise<Route> {
      this.isLoading = true
      this.error = null

      try {
        const route = await routeApi.getById(id)
        this.currentRoute = route

        const index = this.routes.findIndex(item => item.id === route.id)

        if (index === -1) {
          this.routes.push(route)
        } else {
          this.routes[index] = route
        }

        return route
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch route')
        throw error
      } finally {
        this.isLoading = false
      }
    },

  },
})
