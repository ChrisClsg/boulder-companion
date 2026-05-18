<template>
  <div class="quick-log-panel">
    <div class="quick-log-title-row">
      <div>
        <div class="text-subtitle2">
          Add new log
        </div>

        <div v-if="lastLog" class="text-caption text-grey-6">
          Last: {{ lastLogLabel }}
        </div>

        <div v-else class="text-caption text-grey-6">
          Record attempts, tops and flashes.
        </div>
      </div>

      <q-icon name="add_circle" color="primary" size="26px" />
    </div>

    <div class="q-mt-md">
      <div class="text-caption text-grey-7 q-mb-sm">
        Result
      </div>

      <q-btn-toggle
        v-model="result"
        spread
        unelevated
        rounded
        toggle-color="primary"
        color="grey-2"
        text-color="grey-8"
        :options="resultOptions"
      />
    </div>

    <div class="attempt-control q-mt-md">
      <div>
        <div class="text-subtitle2">
          Attempts
        </div>

        <div class="text-caption text-grey-6">
          Number of tries for this log.
        </div>
      </div>

      <div class="attempt-stepper">
        <q-btn
          dense
          round
          flat
          icon="remove"
          :disable="attempts <= 1 || result === 'flashed'"
          @click.stop="attempts--"
        />

        <div class="attempt-number">
          {{ attempts }}
        </div>

        <q-btn
          dense
          round
          flat
          icon="add"
          :disable="result === 'flashed'"
          @click.stop="attempts++"
        />
      </div>
    </div>

    <q-banner
      v-if="result === 'flashed'"
      rounded
      class="q-mt-md bg-primary text-white"
    >
      Flash means topped on your first attempt.
    </q-banner>

    <q-separator class="q-my-md" />

    <div class="feedback-header">
      <div>
        <div class="text-subtitle2">
          Feedback
        </div>

        <div class="text-caption text-grey-6">
          Optional. Saved once per route.
        </div>
      </div>

      <q-btn
        v-if="existingFeedback && !editingFeedback"
        label="Edit"
        dense
        flat
        rounded
        color="primary"
        @click.stop="editingFeedback = true"
      />

      <q-toggle
        v-else-if="!existingFeedback"
        v-model="addingFeedback"
        dense
        color="primary"
      />
    </div>

    <div
      v-if="existingFeedback && !editingFeedback"
      class="feedback-summary q-mt-sm"
    >
      <q-rating
        :model-value="existingFeedback.userRating"
        readonly
        size="18px"
        color="amber"
        icon="star_border"
        icon-selected="star"
      />

      <span>{{ difficultyLabel(existingFeedback.difficultyFeedback) }}</span>
    </div>

    <q-slide-transition>
      <div
        v-if="shouldShowFeedbackControls"
        class="feedback-controls"
      >
        <div class="text-caption text-grey-7 q-mb-xs">
          Rating
        </div>

        <q-rating
          v-model="rating"
          size="28px"
          color="amber"
          icon="star_border"
          icon-selected="star"
        />

        <div class="text-caption text-grey-7 q-mt-md q-mb-xs">
          Difficulty feel
        </div>

        <q-btn-toggle
          v-model="difficultyFeedback"
          spread
          dense
          rounded
          unelevated
          toggle-color="primary"
          color="grey-2"
          text-color="grey-8"
          :options="difficultyOptions"
        />
      </div>
    </q-slide-transition>

    <q-btn
      label="Save new log"
      color="primary"
      unelevated
      rounded
      class="full-width q-mt-md"
      :loading="saving"
      :disable="!canSave"
      @click.stop="save"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type {
  ClimbLog,
  DifficultyFeedback,
  RouteFeedback,
} from 'src/types'

const props = defineProps<{
  routeId: string
  gymId: string
  lastLog?: ClimbLog | null
  existingFeedback?: RouteFeedback | null
  saving?: boolean
}>()

const emit = defineEmits<{
  save: [{
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
  }]
}>()

const result = ref<'attempted' | 'topped' | 'flashed'>('topped')
const attempts = ref(1)

const addingFeedback = ref(false)
const editingFeedback = ref(false)
const rating = ref(props.existingFeedback?.userRating ?? 0)
const difficultyFeedback = ref<DifficultyFeedback | null>(
  props.existingFeedback?.difficultyFeedback ?? null,
)

const resultOptions = [
  {
    label: 'Tried',
    value: 'attempted',
  },
  {
    label: 'Topped',
    value: 'topped',
  },
  {
    label: 'Flash',
    value: 'flashed',
  },
]

const difficultyOptions = [
  {
    label: 'Much easier',
    value: 'MUCH_EASIER',
  },
  {
    label: 'Easier',
    value: 'EASIER',
  },
  {
    label: 'Right',
    value: 'ABOUT_RIGHT',
  },
  {
    label: 'Harder',
    value: 'HARDER',
  },
  {
    label: 'Much harder',
    value: 'MUCH_HARDER',
  },
]

watch(result, (value) => {
  if (value === 'flashed') {
    attempts.value = 1
  }
})

watch(
  () => props.existingFeedback,
  (feedback) => {
    rating.value = feedback?.userRating ?? 0
    difficultyFeedback.value = feedback?.difficultyFeedback ?? null
    addingFeedback.value = false
    editingFeedback.value = false
  },
)

const shouldShowFeedbackControls = computed(() =>
  addingFeedback.value || editingFeedback.value,
)

const feedbackIsValid = computed(() => {
  if (!shouldShowFeedbackControls.value) {
    return true
  }

  return rating.value >= 1 && difficultyFeedback.value !== null
})

const canSave = computed(() =>
  attempts.value >= 1 && feedbackIsValid.value,
)

const lastLogLabel = computed(() => {
  if (!props.lastLog) {
    return ''
  }

  const status = props.lastLog.flashed
    ? 'Flash'
    : props.lastLog.topped
      ? 'Topped'
      : 'Tried'

  return `${status} · ${props.lastLog.attempts} ${
    props.lastLog.attempts === 1 ? 'attempt' : 'attempts'
  }`
})

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

const save = () => {
  if (!canSave.value) {
    return
  }

  const log = {
    routeId: props.routeId,
    gymId: props.gymId,
    sessionId: null,
    attempts: attempts.value,
    topped: result.value === 'topped' || result.value === 'flashed',
    flashed: result.value === 'flashed',
    climbedAt: new Date().toISOString(),
  }

  if (shouldShowFeedbackControls.value && difficultyFeedback.value) {
    emit('save', {
      log,
      feedback: {
        userRating: rating.value,
        difficultyFeedback: difficultyFeedback.value,
      },
    })

    return
  }

  emit('save', {
    log,
  })
}
</script>

<style scoped>
.quick-log-panel {
  padding: 16px;
  border-radius: 22px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.08), transparent 32%),
    linear-gradient(180deg, #f8fafc, #f3f5f8);
}

.quick-log-title-row,
.attempt-control,
.feedback-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.attempt-stepper {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px;
  border-radius: 999px;
  background: white;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.04);
}

.attempt-number {
  min-width: 34px;
  text-align: center;
  font-size: 1.15rem;
  font-weight: 850;
}

.feedback-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667085;
  font-size: 0.88rem;
}

.feedback-controls {
  padding-top: 14px;
}
</style>
