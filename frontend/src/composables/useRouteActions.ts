import { computed } from 'vue'
import type { MaybeRefOrGetter } from 'vue'
import { toValue } from 'vue'
import { useQuasar } from 'quasar'
import { useClimbLogStore } from 'src/stores/climbLogStore'
import { useRouteFeedbackStore } from 'src/stores/routeFeedbackStore'
import { getErrorMessage } from 'src/utils/errors'
import type {
  ClimbLog,
  CreateClimbLogPayload,
  RouteFeedback,
  SaveRouteFeedbackPayload,
} from 'src/types'

export function useRouteActions(routeId: MaybeRefOrGetter<string>) {
  const $q = useQuasar()
  const climbLogStore = useClimbLogStore()
  const routeFeedbackStore = useRouteFeedbackStore()

  const personalSummary = computed(() =>
    climbLogStore.getSummaryByRoute(toValue(routeId)),
  )

  const feedback = computed(() =>
    routeFeedbackStore.getFeedbackByRoute(toValue(routeId)),
  )

  const lastLog = computed(() => personalSummary.value.lastLog)

  const isSaving = computed(() =>
    climbLogStore.isSaving || routeFeedbackStore.isSaving,
  )

  async function saveLog(payload: { log: CreateClimbLogPayload }): Promise<ClimbLog | null> {
    try {
      const createdLog = await climbLogStore.createLog(payload.log)
      $q.notify({ closeBtn: true, message: 'Climb logged', timeout: 2000,type: 'positive' })
      return createdLog
    } catch (err: unknown) {
      $q.notify({
        message: getErrorMessage(err, 'Failed to save climb log'),
        type: 'negative',
      })
      return null
    }
  }

  async function saveFeedback(payload: SaveRouteFeedbackPayload): Promise<RouteFeedback | null> {
    try {
      const saved = await routeFeedbackStore.saveMyFeedback(toValue(routeId), payload)
      $q.notify({ message: 'Feedback saved', type: 'positive' })
      return saved
    } catch (err: unknown) {
      $q.notify({
        message: getErrorMessage(err, 'Failed to save feedback'),
        type: 'negative',
      })
      return null
    }
  }

  return { personalSummary, feedback, lastLog, isSaving, saveLog, saveFeedback }
}
