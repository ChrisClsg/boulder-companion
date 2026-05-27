import { api } from 'src/boot/axios'
import type {
  RouteFeedback,
  SaveRouteFeedbackPayload,
} from 'src/types'

export const routeFeedbackApi = {
  async getMyFeedback(routeId: string): Promise<RouteFeedback | null> {
    try {
      const response = await api.get<RouteFeedback | undefined>(
        `/routes/${routeId}/my-feedback`,
      )

      return response.status === 204 ? null : response.data ?? null
    } catch (error: unknown) {
      console.error("Failed to load route feedback", error)
      throw error
    }
  },

  async saveMyFeedback(
    routeId: string,
    payload: SaveRouteFeedbackPayload,
  ): Promise<RouteFeedback> {
    const response = await api.put<RouteFeedback>(
      `/routes/${routeId}/my-feedback`,
      payload,
    )

    return response.data
  },

}
