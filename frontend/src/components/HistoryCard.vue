<template>
  <q-card
    class="history-card"
    :class="{
      'history-card--topped': history.topped,
    }"
  >
    <div class="history-card-status">
      <q-icon
        :name="history.topped ? 'emoji_events' : 'timeline'"
        size="22px"
      />

      <span>
        {{ history.topped ? 'Topped' : 'Attempt' }}
      </span>
    </div>

    <q-card-section class="q-pb-sm">
      <div class="row items-start justify-between no-wrap q-gutter-sm">
        <div>
          <div class="text-caption text-grey-6">
            Logged on
          </div>

          <div class="history-date">
            {{ formatDate(history.createdAt) }}
          </div>
        </div>

        <q-chip
          dense
          :color="history.topped ? 'green' : 'grey-3'"
          :text-color="history.topped ? 'white' : 'grey-8'"
          :icon="history.topped ? 'check_circle' : 'sports_score'"
        >
          {{ history.tries }}
          {{ history.tries === 1 ? 'try' : 'tries' }}
        </q-chip>
      </div>
    </q-card-section>

    <q-card-section class="q-pt-sm q-gutter-md">
      <div v-if="history.userRating > 0">
        <div class="text-caption text-grey-6 q-mb-xs">
          Rating
        </div>

        <q-rating
          :model-value="history.userRating"
          size="sm"
          color="amber"
          icon="star_border"
          icon-selected="star"
          :max="5"
          @update:model-value="updateRating"
        />
      </div>

      <div v-else>
        <div class="text-caption text-grey-6 q-mb-xs">
          Rating
        </div>

        <q-rating
          :model-value="0"
          size="sm"
          color="amber"
          icon="star_border"
          icon-selected="star"
          :max="5"
          @update:model-value="updateRating"
        />
      </div>

      <div v-if="history.difficultyFeedback">
        <div class="text-caption text-grey-6 q-mb-xs">
          Difficulty feedback
        </div>

        <q-chip
          dense
          outline
          color="primary"
        >
          {{ formatFeedback(history.difficultyFeedback) }}
        </q-chip>
      </div>
    </q-card-section>

    <q-separator />

    <q-card-actions
      v-if="authStore.isAuthenticated"
      class="q-pa-md"
      align="right"
    >
      <q-btn
        v-if="!history.topped"
        label="Mark as topped"
        color="green"
        unelevated
        rounded
        icon="check"
        :loading="actionLoading"
        @click="markAsTopped"
      />

      <q-btn
        v-else
        label="Remove"
        color="negative"
        flat
        rounded
        icon="delete"
        :loading="actionLoading"
        @click="removeHistory"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getErrorMessage } from 'src/utils/errors'
import { historyApi } from 'src/api'
import { useQuasar } from 'quasar'
import type { ClimbingHistory } from 'src/types'
import { useAuthStore } from 'src/stores/authStore'

const authStore = useAuthStore()

const props = defineProps<{
  history: ClimbingHistory
}>()

const emit = defineEmits<{
  (e: 'update', history: ClimbingHistory): void
  (e: 'remove', history: ClimbingHistory): void
}>()

const $q = useQuasar()
const actionLoading = ref(false)

const formatDate = (dateStr: string): string => {
  return new Intl.DateTimeFormat(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(dateStr))
}

const formatFeedback = (feedback: string): string => {
  const mapping: Record<string, string> = {
    MUCH_HARDER: 'Much harder',
    HARDER: 'Harder',
    ABOUT_RIGHT: 'About right',
    EASIER: 'Easier',
    MUCH_EASIER: 'Much easier',
  }

  return mapping[feedback] || feedback
}

const updateRating = async (rating: number) => {
  try {
    const updatedHistory = {
      ...props.history,
      userRating: rating,
    }

    await historyApi.update(props.history.id, updatedHistory)
    emit('update', updatedHistory)
  } catch (error: unknown) {
    $q.notify({
      message: getErrorMessage(error, 'Failed to update rating'),
      type: 'negative',
    })
  }
}

const markAsTopped = async () => {
  if (actionLoading.value) {
    return
  }

  actionLoading.value = true

  try {
    const updatedHistory = {
      ...props.history,
      topped: true,
    }

    await historyApi.update(props.history.id, updatedHistory)
    emit('update', updatedHistory)
  } catch (error: unknown) {
    $q.notify({
      message: getErrorMessage(error, 'Failed to mark as topped'),
      type: 'negative',
    })
  } finally {
    actionLoading.value = false
  }
}

const removeHistory = async () => {
  if (actionLoading.value) {
    return
  }

  actionLoading.value = true

  try {
    await historyApi.delete(props.history.id)
    emit('remove', props.history)
  } catch (error: unknown) {
    $q.notify({
      message: getErrorMessage(error, 'Failed to remove history'),
      type: 'negative',
    })
  } finally {
    actionLoading.value = false
  }
}
</script>

<style scoped>
.history-card {
  width: 100%;
  min-width: 260px;
  max-width: 360px;
  border-radius: 18px;
  overflow: hidden;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;
}

.history-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.14);
}

.history-card-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  color: var(--q-primary);
  font-weight: 700;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.14), transparent 36%),
    linear-gradient(135deg, #f7f9fc, #eef3f8);
}

.history-card--topped .history-card-status {
  color: #1b5e20;
  background:
    radial-gradient(circle at top right, rgba(76, 175, 80, 0.2), transparent 36%),
    linear-gradient(135deg, #f3fbf4, #e8f5e9);
}

.history-date {
  font-size: 1.05rem;
  font-weight: 700;
  line-height: 1.2;
}

@media (max-width: 600px) {
  .history-card {
    max-width: none;
  }
}
</style>
