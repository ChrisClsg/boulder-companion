import { defineStore } from 'pinia'
import { routeFeedbackApi } from 'src/api'
import type {
  RouteFeedback,
  SaveRouteFeedbackPayload,
} from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

type RouteFeedbackState = {
  feedbackByRouteId: Record<string, RouteFeedback | null>
  isLoading: boolean
  isSaving: boolean
  error: string | null
}

export const useRouteFeedbackStore = defineStore('routeFeedbacks', {
  state: (): RouteFeedbackState => ({
    feedbackByRouteId: {},
    isLoading: false,
    isSaving: false,
    error: null,
  }),

  getters: {
    getFeedbackByRoute: (state) => {
      return (routeId: string): RouteFeedback | null =>
        state.feedbackByRouteId[routeId] ?? null
    },
  },

  actions: {
    async fetchMyFeedback(routeId: string): Promise<RouteFeedback | null> {
      this.isLoading = true
      this.error = null

      try {
        const feedback = await routeFeedbackApi.getMyFeedback(routeId)
        this.feedbackByRouteId[routeId] = feedback
        return feedback
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to fetch route feedback')
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async saveMyFeedback(
      routeId: string,
      payload: SaveRouteFeedbackPayload,
    ): Promise<RouteFeedback> {
      this.isSaving = true
      this.error = null

      try {
        const feedback = await routeFeedbackApi.saveMyFeedback(
          routeId,
          payload,
        )

        this.feedbackByRouteId[routeId] = feedback

        return feedback
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to save route feedback')
        throw error
      } finally {
        this.isSaving = false
      }
    },

    async deleteMyFeedback(routeId: string): Promise<void> {
      this.error = null

      try {
        await routeFeedbackApi.deleteMyFeedback(routeId)
        this.feedbackByRouteId[routeId] = null
      } catch (error: unknown) {
        this.error = getErrorMessage(error, 'Failed to delete route feedback')
        throw error
      }
    },

    clearError(): void {
      this.error = null
    },
  },
})
