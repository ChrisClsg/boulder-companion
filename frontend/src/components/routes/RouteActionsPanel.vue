<template>
  <q-list
    bordered
    separator
    class="action-list"
  >
    <q-expansion-item
      :icon="personalSummary.topped ? 'add_task' : 'add'"
      label="Add new log"
      :caption="quickLogCaption"
      :default-opened="!personalSummary.topped"
      header-class="text-primary"
      class="action-item"
    >
      <route-quick-log-panel
        :route-id="routeId"
        :gym-id="gymId"
        :last-log="lastLog"
        :saving="isSaving"
        @save="saveLog"
      />
    </q-expansion-item>

    <q-expansion-item
      icon="rate_review"
      label="Your route feedback"
      :caption="feedbackCaption"
      :default-opened="!feedback"
      header-class="text-primary"
      class="action-item"
    >
      <route-feedback-card
        :feedback="feedback"
        :saving="isSaving"
        @save="saveFeedback"
      />
    </q-expansion-item>
  </q-list>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouteActions } from 'src/composables/useRouteActions'
import RouteQuickLogPanel from 'src/components/routes/RouteQuickLogPanel.vue'
import RouteFeedbackCard from 'src/components/routes/RouteFeedbackCard.vue'

const props = defineProps<{
  routeId: string
  gymId: string
}>()

const { personalSummary, feedback, lastLog, isSaving, saveLog, saveFeedback } =
  useRouteActions(() => props.routeId)

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
