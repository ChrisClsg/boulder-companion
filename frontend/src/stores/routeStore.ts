import { defineStore } from 'pinia'
import {
  routeApi,
  type CreateRoutePayload,
  type UpdateRoutePayload,
} from 'src/api'
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

    routesCount: (state): number => state.routes.length,
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

    async createRoute(route: CreateRoutePayload): Promise<Route> {
      this.error = null

      try {
        const createdRoute = await routeApi.create(route)
        this.routes.unshift(createdRoute)
        this.currentRoute = createdRoute
        return createdRoute
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to create route')
        throw error
      }
    },

    async updateRoute(
      id: string,
      updates: UpdateRoutePayload,
    ): Promise<Route> {
      this.error = null

      try {
        const updatedRoute = await routeApi.update(id, updates)

        const index = this.routes.findIndex(route => route.id === id)

        if (index !== -1) {
          this.routes[index] = updatedRoute
        }

        if (this.currentRoute?.id === id) {
          this.currentRoute = updatedRoute
        }

        return updatedRoute
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to update route')
        throw error
      }
    },

    async deleteRoute(id: string): Promise<void> {
      this.error = null

      try {
        await routeApi.delete(id)

        this.routes = this.routes.filter(route => route.id !== id)

        if (this.currentRoute?.id === id) {
          this.currentRoute = null
        }
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to delete route')
        throw error
      }
    },

    async archiveRoute(id: string): Promise<Route> {
      this.error = null

      try {
        const archivedRoute = await routeApi.archive(id)

        const index = this.routes.findIndex(route => route.id === id)

        if (index !== -1) {
          this.routes[index] = archivedRoute
        }

        if (this.currentRoute?.id === id) {
          this.currentRoute = archivedRoute
        }

        return archivedRoute
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to archive route')
        throw error
      }
    },

    clearCurrentRoute(): void {
      this.currentRoute = null
    },

    clearError(): void {
      this.error = null
    },
  },
})
