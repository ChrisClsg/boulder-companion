<template>
  <q-card flat bordered class="feedback-card">
    <q-card-section class="feedback-card-section">
      <p class="text-body2 text-grey-7 q-mt-sm">
        Rate this route once. You can update your feedback anytime.
      </p>

      <div class="q-mt-lg">
        <div class="text-caption text-grey-7 q-mb-xs">
          Your rating
        </div>

        <q-rating
          v-model="rating"
          size="34px"
          color="amber"
          icon="star_border"
          icon-selected="star"
        />
      </div>

      <div class="q-mt-lg">
        <div class="text-caption text-grey-7 q-mb-sm">
          How did the grade feel?
        </div>

        <q-option-group
          v-model="difficultyFeedback"
          :options="difficultyOptions"
          color="primary"
          type="radio"
        />
      </div>
    </q-card-section>

    <q-card-actions align="right" class="q-pa-md q-pt-none">
      <q-btn
        label="Save feedback"
        color="primary"
        unelevated
        rounded
        :loading="saving"
        :disable="!canSave"
        @click="save"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type {
  DifficultyFeedback,
  RouteFeedback,
} from 'src/types'

const props = defineProps<{
  feedback: RouteFeedback | null
  saving?: boolean
}>()

const emit = defineEmits<{
  save: [{
    userRating: number
    difficultyFeedback: DifficultyFeedback
  }]
}>()

const rating = ref(0)
const difficultyFeedback = ref<DifficultyFeedback>('ABOUT_RIGHT')

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
    label: 'About right',
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

watch(
  () => props.feedback,
  (feedback) => {
    rating.value = feedback?.userRating ?? 0
    difficultyFeedback.value = feedback?.difficultyFeedback ?? 'ABOUT_RIGHT'
  },
  { immediate: true },
)

const canSave = computed(() =>
  rating.value >= 1 &&
  rating.value <= 5 &&
  difficultyFeedback.value !== null,
)

const save = () => {
  if (!canSave.value || difficultyFeedback.value === null) {
    return
  }

  emit('save', {
    userRating: rating.value,
    difficultyFeedback: difficultyFeedback.value,
  })
}
</script>

<style scoped>
.feedback-card {
  background: inherit;
  border: none;
}

.feedback-card-section {
  padding: 1rem !important;
}

.feedback-card .q-card__section {
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
</style>
