<template>
  <q-card class="route-card">
    <q-carousel
      v-if="route.images?.length"
      v-model="activeSlide"
      animated
      swipeable
      :arrows="route.images.length > 1"
      infinite
      class="route-carousel"
      control-color="white"
    >
      <q-carousel-slide
        v-for="(img, index) in route.images"
        :key="index"
        :name="index"
        :img-src="img.url"
        class="route-slide"
      >
        <div class="absolute-top route-image-overlay" />

        <q-chip
          color="primary"
          text-color="white"
          class="absolute-top-right q-ma-sm"
        >
          {{ route.difficulty.value }}
        </q-chip>

        <q-chip
          v-if="route.images.length > 1"
          dense
          color="black"
          text-color="white"
          class="absolute-bottom-right q-ma-sm route-image-count"
        >
          {{ activeSlide + 1 }} / {{ route.images.length }}
        </q-chip>

        <div class="absolute-bottom route-carousel-caption">
          <div class="text-h6 text-white">
            {{ route.name }}
          </div>

          <div class="text-caption text-grey-3">
            {{ route.wall }}
          </div>
        </div>
      </q-carousel-slide>
    </q-carousel>

    <q-card-section
      v-else
      class="route-placeholder"
    >
      <q-chip
        color="primary"
        text-color="white"
      >
        {{ route.difficulty.value }}
      </q-chip>

      <div class="text-h6 text-primary q-mt-sm">
        {{ route.name }}
      </div>

      <div class="text-body2 text-grey-7">
        {{ route.wall }}
      </div>
    </q-card-section>

    <q-card-section class="q-pt-sm q-pb-sm">
      <div class="row items-center q-gutter-sm">
        <q-chip
          dense
          outline
          color="primary"
        >
          {{ route.holdColor }}
        </q-chip>

        <q-chip
          v-for="type in route.holdTypes"
          :key="type"
          dense
          color="grey-2"
          text-color="grey-8"
        >
          {{ type }}
        </q-chip>
      </div>
    </q-card-section>

    <q-card-section class="personal-section">
      <div class="personal-summary">
        <div class="summary-main">
          <q-chip
            dense
            :color="summaryColor"
            text-color="white"
            :icon="summaryIcon"
          >
            {{ summaryLabel }}
          </q-chip>

          <span
            v-if="personalSummary.totalAttempts > 0"
            class="text-caption text-grey-6"
          >
            {{ personalSummary.totalAttempts }}
            {{ personalSummary.totalAttempts === 1 ? 'attempt' : 'attempts' }}
          </span>
        </div>

        <div
          v-if="personalSummary.lastLog"
          class="text-caption text-grey-6 q-mt-xs"
        >
          Last logged {{ formatDate(personalSummary.lastLog.climbedAt) }}
        </div>

        <div
          v-if="feedback"
          class="feedback-mini q-mt-xs"
        >
          <q-rating
            :model-value="feedback.userRating"
            readonly
            size="16px"
            color="amber"
            icon="star_border"
            icon-selected="star"
          />

          <span>{{ difficultyLabel(feedback.difficultyFeedback) }}</span>
        </div>
      </div>

      <q-slide-transition>
        <route-quick-log-panel
          v-if="quickLogOpen"
          class="q-mt-sm"
          :route-id="route.id"
          :gym-id="route.gymId"
          :last-log="personalSummary.lastLog"
          :existing-feedback="feedback"
          :saving="climbLogStore.isSaving || routeFeedbackStore.isSaving"
          @save="saveQuickLog"
        />
      </q-slide-transition>
    </q-card-section>

    <q-card-actions
      align="between"
      class="q-pa-md q-pt-none"
    >
      <q-btn
        :label="quickLogOpen ? 'Close' : 'Log'"
        :icon="quickLogOpen ? 'expand_less' : 'add'"
        color="primary"
        outline
        rounded
        @click.stop="quickLogOpen = !quickLogOpen"
      />

      <q-btn
        label="Details"
        color="primary"
        unelevated
        rounded
        :to="`/routes/${route.id}`"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useQuasar } from 'quasar'
import RouteQuickLogPanel from 'src/components/routes/RouteQuickLogPanel.vue'
import { useClimbLogStore } from 'src/stores/climbLogStore'
import { useRouteFeedbackStore } from 'src/stores/routeFeedbackStore'
import { getErrorMessage } from 'src/utils/errors'
import type {
  ClimbLog,
  DifficultyFeedback,
  Route,
  RouteFeedback,
} from 'src/types'

const props = defineProps<{
  route: Route
}>()

const emit = defineEmits<{
  saved: [{
    routeId: string
    log: ClimbLog
    feedback?: RouteFeedback
  }]
}>()

const $q = useQuasar()
const climbLogStore = useClimbLogStore()
const routeFeedbackStore = useRouteFeedbackStore()

const activeSlide = ref(0)
const quickLogOpen = ref(false)

const personalSummary = computed(() =>
  climbLogStore.getSummaryByRoute(props.route.id),
)

const feedback = computed(() =>
  routeFeedbackStore.getFeedbackByRoute(props.route.id),
)

const summaryLabel = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'Not tried'
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
    flashed: boolean
    climbedAt: string
  }
  feedback?: {
    userRating: number
    difficultyFeedback: DifficultyFeedback
  }
}) => {
  try {
    const createdLog = await climbLogStore.createLog(payload.log)

    let savedFeedback: RouteFeedback | undefined

    if (payload.feedback) {
      savedFeedback = await routeFeedbackStore.saveMyFeedback(
        props.route.id,
        payload.feedback,
      )
    }

    quickLogOpen.value = false

    $q.notify({
      message: 'Climb logged',
      type: 'positive',
    })

    if (savedFeedback) {
      emit('saved', {
        routeId: props.route.id,
        log: createdLog,
        feedback: savedFeedback,
      })
    } else {
      emit('saved', {
        routeId: props.route.id,
        log: createdLog,
      })
    }
  } catch (err: unknown) {
    $q.notify({
      message: getErrorMessage(err, 'Failed to save climb log'),
      type: 'negative',
    })
  }
}

const difficultyLabel = (value: DifficultyFeedback): string => {
  const labels: Record<DifficultyFeedback, string> = {
    MUCH_HARDER: 'Much harder',
    HARDER: 'Harder',
    ABOUT_RIGHT: 'About right',
    EASIER: 'Easier',
    MUCH_EASIER: 'Much easier',
  }

  return labels[value]
}

const formatDate = (value: string): string => {
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))
}
</script>

<style scoped>
.route-card {
  width: 100%;
  min-width: 280px;
  max-width: 380px;
  border-radius: 22px;
  overflow: hidden;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;
}

.route-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.14);
}

.route-carousel {
  aspect-ratio: 4 / 5;
  height: auto;
}

.route-slide {
  background-size: cover;
  background-position: center;
}

.route-carousel-caption {
  padding: 32px 72px 14px 16px;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.72),
    rgba(0, 0, 0, 0)
  );
}

.route-image-overlay {
  height: 80px;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.32),
    rgba(0, 0, 0, 0)
  );
}

.route-image-count {
  background: rgba(0, 0, 0, 0.62) !important;
}

.route-placeholder {
  aspect-ratio: 4 / 5;
  min-height: 280px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.16), transparent 34%),
    linear-gradient(135deg, #f5f7fb, #e8eef8);
}

.personal-section {
  padding-top: 0;
}

.personal-summary {
  padding: 14px;
  border-radius: 20px;
  background: #f8fafc;
}

.summary-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.feedback-mini {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667085;
  font-size: 0.84rem;
}
</style>
