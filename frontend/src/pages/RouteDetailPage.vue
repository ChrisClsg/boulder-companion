<template>
  <q-page
    :class="`${$q.screen.lt.md ? 'q-pt-none' : ''}`"
    padding
  >
    <div v-if="loading" class="state state--loading">
      <q-spinner-dots color="primary" size="44px" />

      <div class="text-body2 text-grey-6 q-mt-sm">
        Loading route...
      </div>
    </div>

    <q-card
      v-else-if="error"
      flat
      bordered
      class="state state--error"
    >
      <q-card-section>
        <q-icon name="error_outline" color="negative" size="44px" />

        <div class="text-h6 q-mt-sm">
          Could not load route
        </div>

        <div class="text-body2 text-grey-7 q-mt-xs">
          {{ error }}
        </div>

        <q-btn
          label="Try again"
          color="primary"
          unelevated
          rounded
          icon="refresh"
          class="q-mt-md"
          @click="fetchPageData"
        />
      </q-card-section>
    </q-card>

    <div v-else-if="routeData" class="route-detail-page">
      <section class="route-hero">
        <div class="route-hero-media">
          <q-carousel
            v-if="routeData.images?.length"
            v-model="activeImage"
            animated
            swipeable
            :arrows="routeData.images.length > 1"
            :infinite="routeData.images.length > 1"
            class="route-carousel"
            control-color="white"
          >
            <q-carousel-slide
              v-for="(img, index) in routeData.images"
              :key="index"
              :name="index"
              :img-src="img.url"
              class="route-slide"
            >
              <div class="absolute-top route-image-overlay" />

              <q-chip
                color="primary"
                text-color="white"
                class="absolute-top-right q-ma-md"
              >
                {{ routeData.difficulty.value }}
              </q-chip>

              <q-chip
                v-if="routeData.images.length > 1"
                dense
                color="black"
                text-color="white"
                class="absolute-bottom-right q-ma-md route-image-count"
              >
                {{ activeImage + 1 }} / {{ routeData.images.length }}
              </q-chip>

              <div class="absolute-bottom route-image-caption">
                <div class="route-image-title">
                  {{ routeData.name }}
                </div>

                <div class="text-caption text-grey-3">
                  {{ routeData.wall }}
                </div>
              </div>
            </q-carousel-slide>
          </q-carousel>

          <div
            v-else
            class="route-image-placeholder"
          >
            <q-icon
              name="image_not_supported"
              size="56px"
              color="grey-5"
            />

            <div class="text-body2 text-grey-6 q-mt-sm">
              No images
            </div>
          </div>
        </div>

        <div class="route-hero-content">
          <div class="text-overline text-primary">
            Boulder Route
          </div>

          <div class="route-title-block">
            <h1 class="route-title">
              {{ routeData.name }}
            </h1>

            <div class="text-body1 text-grey-7">
              {{ routeData.wall }}
            </div>
          </div>

          <div class="route-meta q-mt-lg">
            <q-chip
              color="primary"
              text-color="white"
              icon="fitness_center"
            >
              {{ routeData.difficulty.value }}

              <span class="q-ml-xs text-weight-regular">
                {{ routeData.difficulty.scale }}
              </span>
            </q-chip>

            <q-chip
              outline
              color="primary"
              icon="palette"
            >
              {{ routeData.holdColor }}
            </q-chip>
          </div>

          <div class="q-mt-md">
            <div class="text-caption text-grey-6 q-mb-xs">
              Hold types
            </div>

            <div class="row q-gutter-sm">
              <q-chip
                v-for="type in routeData.holdTypes"
                :key="type"
                dense
                color="grey-2"
                text-color="grey-8"
              >
                {{ type }}
              </q-chip>
            </div>
          </div>

          <div class="side-grid q-mt-xl">
            <q-card flat bordered class="summary-card">
              <q-card-section>
                <div class="card-header">
                  <div>
                    <div class="text-overline text-primary">
                      Your progress
                    </div>

                    <h2>{{ summaryLabel }}</h2>
                  </div>

                  <q-icon
                    :name="summaryIcon"
                    :color="summaryColor"
                    size="34px"
                  />
                </div>

                <div class="summary-stats q-mt-md">
                  <div>
                    <strong>{{ personalSummary.totalLogs }}</strong>
                    <span>logs</span>
                  </div>

                  <div>
                    <strong>{{ personalSummary.totalAttempts }}</strong>
                    <span>attempts</span>
                  </div>
                </div>

                <div
                  v-if="personalSummary.lastLog"
                  class="text-body2 text-grey-7 q-mt-md"
                >
                  Last logged {{ formatFullDate(personalSummary.lastLog.climbedAt) }}
                </div>
              </q-card-section>
            </q-card>

            <route-quick-log-panel
              :route-id="routeData.id"
              :gym-id="routeData.gymId"
              :last-log="personalSummary.lastLog"
              :existing-feedback="feedback"
              :saving="climbLogStore.isSaving || routeFeedbackStore.isSaving"
              @save="saveQuickLog"
            />

            <route-feedback-card
              :feedback="feedback"
              :saving="routeFeedbackStore.isSaving"
              @save="saveFeedback"
            />
          </div>
        </div>
      </section>

      <section class="logs-section">
        <div class="logs-header">
          <div>
            <h2 class="section-title">
              Climb logs
            </h2>

            <div class="text-body2 text-grey-6">
              {{ logs.length }}
              {{ logs.length === 1 ? 'log' : 'logs' }} for this route
            </div>
          </div>
        </div>

        <q-card
          v-if="logs.length === 0"
          flat
          bordered
          class="empty-logs-card text-center"
        >
          <q-card-section>
            <q-icon
              name="timeline"
              size="48px"
              color="grey-5"
            />

            <div class="text-h6 q-mt-sm">
              No logs yet
            </div>

            <div class="text-body2 text-grey-6">
              Add your first climb log above.
            </div>
          </q-card-section>
        </q-card>

        <div v-else class="log-list">
          <q-card
            v-for="log in sortedLogs"
            :key="log.id"
            flat
            bordered
            class="log-card"
          >
            <q-card-section>
              <div class="log-card-content">
                <div class="log-main">
                  <q-chip
                    dense
                    :color="logColor(log)"
                    text-color="white"
                    :icon="logIcon(log)"
                  >
                    {{ logLabel(log) }}
                  </q-chip>

                  <div>
                    <div class="text-subtitle1 text-weight-bold">
                      {{ log.attempts }}
                      {{ log.attempts === 1 ? 'attempt' : 'attempts' }}
                    </div>

                    <div class="text-caption text-grey-6">
                      {{ formatFullDate(log.climbedAt) }}
                    </div>
                  </div>
                </div>

                <q-btn
                  flat
                  round
                  dense
                  color="negative"
                  icon="delete_outline"
                  :loading="deletingLogId === log.id"
                  @click="deleteLog(log)"
                />
              </div>
            </q-card-section>
          </q-card>
        </div>
      </section>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'stores/authStore'
import { useRouteStore } from 'src/stores/routeStore'
import { useClimbLogStore } from 'src/stores/climbLogStore'
import { useRouteFeedbackStore } from 'src/stores/routeFeedbackStore'
import RouteQuickLogPanel from 'src/components/routes/RouteQuickLogPanel.vue'
import RouteFeedbackCard from 'src/components/routes/RouteFeedbackCard.vue'
import { getErrorMessage } from 'src/utils/errors'
import type {
  ClimbLog,
  DifficultyFeedback,
  RoutePersonalSummary,
} from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const authStore = useAuthStore()
const routeStore = useRouteStore()
const climbLogStore = useClimbLogStore()
const routeFeedbackStore = useRouteFeedbackStore()

const loading = ref(false)
const deletingLogId = ref<string | null>(null)
const error = ref<string | null>(null)
const activeImage = ref(0)

const routeId = computed(() => route.params.id as string)

const routeData = computed(() => routeStore.currentRoute)

const logs = computed(() =>
  climbLogStore.getLogsByRoute(routeId.value),
)

const feedback = computed(() =>
  routeFeedbackStore.getFeedbackByRoute(routeId.value),
)

const fetchPageData = async () => {
  loading.value = true
  error.value = null

  try {
    await Promise.all([
      routeStore.fetchRouteById(routeId.value),
      climbLogStore.fetchLogsByRoute(routeId.value),
      routeFeedbackStore.fetchMyFeedback(routeId.value),
    ])
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to fetch route details')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
  } finally {
    loading.value = false
  }
}

const sortedLogs = computed(() => {
  return [...logs.value].sort((a, b) =>
    new Date(b.climbedAt).getTime() - new Date(a.climbedAt).getTime(),
  )
})

const personalSummary = computed<RoutePersonalSummary>(() =>
  climbLogStore.getSummaryByRoute(routeId.value),
)

const summaryLabel = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'Not tried yet'
  }

  if (personalSummary.value.flashed) {
    return 'Flashed'
  }

  if (personalSummary.value.topped) {
    return 'Topped'
  }

  return 'Project'
})

const summaryColor = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'grey'
  }

  if (personalSummary.value.flashed) {
    return 'purple'
  }

  if (personalSummary.value.topped) {
    return 'positive'
  }

  return 'orange'
})

const summaryIcon = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'radio_button_unchecked'
  }

  if (personalSummary.value.flashed) {
    return 'bolt'
  }

  if (personalSummary.value.topped) {
    return 'check_circle'
  }

  return 'hourglass_bottom'
})

const saveQuickLog = async (payload: {
  log: {
    routeId: string
    gymId: string
    sessionId: string | null
    attempts: number
    topped: boolean
    climbedAt: string
  }
  feedback?: {
    userRating: number
    difficultyFeedback: DifficultyFeedback
  }
}) => {
  try {
    await climbLogStore.createLog(payload.log)

    if (payload.feedback) {
      await routeFeedbackStore.saveMyFeedback(
        routeId.value,
        payload.feedback,
      )
    }

    $q.notify({
      message: 'Climb logged',
      type: 'positive',
    })
  } catch (err: unknown) {
    $q.notify({
      message: getErrorMessage(err, 'Failed to save climb log'),
      type: 'negative',
    })
  }
}

const saveFeedback = async (payload: {
  userRating: number
  difficultyFeedback: DifficultyFeedback
}) => {
  try {
    await routeFeedbackStore.saveMyFeedback(routeId.value, payload)

    $q.notify({
      message: 'Feedback saved',
      type: 'positive',
    })
  } catch (err: unknown) {
    $q.notify({
      message: getErrorMessage(err, 'Failed to save feedback'),
      type: 'negative',
    })
  }
}

const deleteLog = async (log: ClimbLog) => {
  deletingLogId.value = log.id

  try {
    await climbLogStore.deleteLog(log.id)

    $q.notify({
      message: 'Log deleted',
      type: 'positive',
    })
  } catch (err: unknown) {
    $q.notify({
      message: getErrorMessage(err, 'Failed to delete log'),
      type: 'negative',
    })
  } finally {
    deletingLogId.value = null
  }
}

const logLabel = (log: ClimbLog): string => {
  if (log.flashed) {
    return 'Flash'
  }

  if (log.topped) {
    return 'Topped'
  }

  return 'Tried'
}

const logColor = (log: ClimbLog): string => {
  if (log.flashed) {
    return 'purple'
  }

  if (log.topped) {
    return 'positive'
  }

  return 'orange'
}

const logIcon = (log: ClimbLog): string => {
  if (log.flashed) {
    return 'bolt'
  }

  if (log.topped) {
    return 'check_circle'
  }

  return 'hourglass_bottom'
}

const formatFullDate = (value: string): string => {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    await router.push('/')
    return
  }

  await fetchPageData()
})
</script>

<style scoped>
.route-detail-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.state {
  max-width: 520px;
  margin: 64px auto;
  text-align: center;
}

.state--loading {
  padding: 48px 24px;
}

.state--error {
  border-radius: 24px;
  padding: 20px;
}

.route-hero {
  display: grid;
  grid-template-columns: minmax(280px, 440px) minmax(0, 1fr);
  gap: 40px;
  align-items: start;
}

.route-hero-media {
  min-width: 0;
}

.route-carousel {
  aspect-ratio: 4 / 5;
  height: auto;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.18);
}

.route-slide {
  background-size: cover;
  background-position: center;
}

.route-image-overlay {
  height: 120px;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.36),
    rgba(0, 0, 0, 0)
  );
}

.route-image-caption {
  padding: 42px 88px 18px 18px;
  color: white;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.74),
    rgba(0, 0, 0, 0)
  );
}

.route-image-title {
  font-size: 1.6rem;
  line-height: 1.1;
  font-weight: 850;
}

.route-image-count {
  background: rgba(0, 0, 0, 0.62) !important;
}

.route-image-placeholder {
  aspect-ratio: 4 / 5;
  border-radius: 28px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.12), transparent 32%),
    linear-gradient(135deg, #f5f7fb, #e8eef8);
}

.route-hero-content {
  min-width: 0;
  padding-top: 12px;
}

.route-title {
  margin: 0;
  font-size: clamp(2.25rem, 6vw, 4rem);
  line-height: 1.02;
  letter-spacing: -0.05em;
  font-weight: 850;
}

.route-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.side-grid {
  display: grid;
  gap: 18px;
}

.summary-card {
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.08), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
}

.summary-card .q-card__section {
  padding: 26px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.card-header h2 {
  margin: 0;
  font-size: 1.35rem;
  line-height: 1.2;
  font-weight: 850;
  letter-spacing: -0.04em;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.summary-stats div {
  padding: 16px;
  border-radius: 20px;
  background: #f8fafc;
}

.summary-stats strong,
.summary-stats span {
  display: block;
}

.summary-stats strong {
  font-size: 1.5rem;
  line-height: 1;
  font-weight: 850;
}

.summary-stats span {
  margin-top: 4px;
  color: #667085;
  font-size: 0.86rem;
}

.logs-section {
  margin-top: 56px;
}

.logs-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.section-title {
  margin: 0;
  font-size: 1.6rem;
  line-height: 1.2;
  font-weight: 850;
  letter-spacing: -0.04em;
}

.empty-logs-card {
  max-width: 520px;
  margin: 0 auto;
  border-radius: 24px;
  padding: 24px;
}

.log-list {
  display: grid;
  gap: 12px;
}

.log-card {
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #fafbfc);
}

.log-card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.log-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

@media (max-width: 900px) {
  .route-hero {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .route-hero-media {
    max-width: none;
    width: calc(100% + 32px);
    margin-left: -16px;
    margin-right: -16px;
  }

  .route-carousel {
    border-radius: 0;
    box-shadow: none;
  }

  .route-image-placeholder {
    border-radius: 0;
  }

  .route-title-block {
    display: none;
  }

  .route-hero-content {
    padding-top: 0;
  }
}

@media (max-width: 600px) {
  .logs-section {
    margin-top: 40px;
  }

  .log-card-content,
  .log-main {
    align-items: flex-start;
  }

  .log-main {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
