import { AxiosError } from 'axios'
import { api } from 'src/boot/axios'
import type {
  RouteFeedback,
  SaveRouteFeedbackPayload,
} from 'src/types'

export const routeFeedbackApi = {
  async getMyFeedback(routeId: string): Promise<RouteFeedback | null> {
    try {
      const response = await api.get<RouteFeedback>(
        `/routes/${routeId}/my-feedback`,
      )

      return response.data
    } catch (error: unknown) {
      if (
        error instanceof AxiosError &&
        error.response?.status === 404
      ) {
        return null
      }

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
