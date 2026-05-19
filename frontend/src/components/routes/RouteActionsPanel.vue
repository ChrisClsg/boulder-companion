<template>
  <q-list
    bordered
    separator
    class="action-list"
  >
    <q-expansion-item
      v-model="logOpen"
      :icon="personalSummary.topped ? 'add_task' : 'add'"
      label="Add new log"
      :caption="quickLogCaption"
      header-class="text-primary"
      class="action-item"
    >
      <route-quick-log-panel
        :route-id="routeId"
        :gym-id="gymId"
        :last-log="lastLog"
        :saving="isSaving"
        @save="onSaveLog"
      />
    </q-expansion-item>

    <q-expansion-item
      v-model="feedbackOpen"
      icon="rate_review"
      label="Your route feedback"
      :caption="feedbackCaption"
      header-class="text-primary"
      class="action-item"
    >
      <route-feedback-card
        :feedback="feedback"
        :saving="isSaving"
        @save="onSaveFeedback"
      />
    </q-expansion-item>
  </q-list>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouteActions } from 'src/composables/useRouteActions'
import RouteQuickLogPanel from 'src/components/routes/RouteQuickLogPanel.vue'
import RouteFeedbackCard from 'src/components/routes/RouteFeedbackCard.vue'
import type { CreateClimbLogPayload, SaveRouteFeedbackPayload } from 'src/types'

const props = defineProps<{
  routeId: string
  gymId: string
  openFeedback?: boolean
}>()

const { personalSummary, feedback, lastLog, isSaving, saveLog, saveFeedback } =
  useRouteActions(() => props.routeId)

const logOpen = ref(!personalSummary.value.topped)
const feedbackOpen = ref((props.openFeedback ?? true) && !feedback.value)

const quickLogCaption = computed(() => {
  const last = lastLog.value
  if (!last) return 'Add your first attempt'
  const verb = last.flashed ? 'Flashed' : last.topped ? 'Topped' : 'Tried'
  return `Last: ${verb} · ${last.attempts} ${last.attempts === 1 ? 'attempt' : 'attempts'} · ${formatFullDate(last.climbedAt)}`
})

const feedbackCaption = computed(() => {
  if (!feedback.value) return 'Not rated yet'
  const diff = feedback.value.difficultyFeedback.toLowerCase().replace(/_/g, ' ')
  return `★ ${feedback.value.userRating} · Felt ${diff}`
})

const onSaveLog = async (payload: { log: CreateClimbLogPayload }) => {
  await saveLog(payload)
  logOpen.value = false
}

const onSaveFeedback = async (payload: SaveRouteFeedbackPayload) => {
  await saveFeedback(payload)
  feedbackOpen.value = false
}

const formatFullDate = (value: string): string =>
  new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
</script>

<style scoped>
.action-list {
  border-radius: 28px;
  overflow: hidden;
}

.action-item {
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.08), transparent 32%),
    linear-gradient(180deg, #f8fafc, #f3f5f8);
}
</style>
