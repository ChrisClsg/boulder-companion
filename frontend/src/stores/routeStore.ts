import { defineStore } from 'pinia'
import { routeApi } from 'boot/axios'
import type { Route } from 'src/types'

export const useRouteStore = defineStore('routes', {
  state: () => ({
    routes: [] as Route[],
    currentRoute: null as Route | null,
    isLoading: false,
    error: null as string | null,
  }),

  getters: {
    getRoute: (state) => (id: string) => state.routes.find(r => r.id === id) || state.currentRoute,
    routesCount: (state) => state.routes.length,
  },

  actions: {
    async fetchRoutesByGym(gymId: string) {
      this.isLoading = true
      this.error = null
      try {
        const response = await routeApi.getByGym(gymId)
        this.routes = response.data
      } catch (error: unknown) {
        this.error = (error as { message?: string }).message || 'Failed to fetch routes'
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async fetchRouteById(id: string) {
      this.isLoading = true
      this.error = null
      try {
        const response = await routeApi.getById(id)
        this.currentRoute = response.data as Route
      } catch (error: unknown) {
        this.error = (error as { message?: string }).message || 'Failed to fetch route'
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async createRoute(route: Route) {
      this.error = null
      try {
        const response = await routeApi.create(route)
        this.routes.unshift(response.data)
        return response.data
      } catch (error: unknown) {
        this.error = (error as { message?: string }).message || 'Failed to create route'
        throw error
      }
    },

    async updateRoute(id: string, updates: Partial<Route>) {
      this.error = null
      try {
        const response = await routeApi.update(id, { ...this.getRoute(id), ...updates })
        const index = this.routes.findIndex(r => r.id === id)
        if (index !== -1) {
          this.routes[index] = response.data
        }
        return response.data
      } catch (error: unknown) {
        this.error = (error as { message?: string }).message || 'Failed to update route'
        throw error
      }
    },

    async deleteRoute(id: string) {
      this.error = null
      try {
        await routeApi.delete(id)
        this.routes = this.routes.filter(r => r.id !== id)
      } catch (error: unknown) {
        this.error = (error as { message?: string }).message || 'Failed to delete route'
        throw error
      }
    },

    async archiveRoute(id: string) {
      this.error = null
      try {
        const response = await routeApi.archive(id)
        const index = this.routes.findIndex(r => r.id === id)
        if (index !== -1) {
          this.routes[index] = response.data
        }
        return response.data
      } catch (error: unknown) {
        this.error = (error as { message?: string }).message || 'Failed to archive route'
        throw error
      }
    },
  },
})
