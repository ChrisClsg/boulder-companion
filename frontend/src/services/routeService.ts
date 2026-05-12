import { useRouteStore } from 'stores/routeStore'
import type { Route } from 'src/types'

export const routeService = {
  async fetchRoutesByGym(gymId: string): Promise<Route[]> {
    const store = useRouteStore()
    if (store.routes.length > 0) {
      return store.routes
    }
    await store.fetchRoutesByGym(gymId)
    return store.routes
  },

  async fetchRouteById(id: string): Promise<Route> {
    const store = useRouteStore()
    if (store.currentRoute && store.currentRoute.id === id) {
      return store.currentRoute
    }
    await store.fetchRouteById(id)
    return store.currentRoute!
  },

  async createRoute(route: Route): Promise<Route> {
    const store = useRouteStore()
    await store.createRoute(route)
    return route
  },

  async updateRoute(id: string, updates: Partial<Route>): Promise<Route> {
    const store = useRouteStore()
    await store.updateRoute(id, updates)
    return store.getRoute(id)!
  },

  async deleteRoute(id: string): Promise<void> {
    const store = useRouteStore()
    await store.deleteRoute(id)
  },

  async archiveRoute(id: string): Promise<Route> {
    const store = useRouteStore()
    await store.archiveRoute(id)
    return store.getRoute(id)!
  },
}
