<template>
  <q-card class="history-card">
    <q-card-section>
      <div class="text-body1 text-primary">
        {{ history.route.name }}
      </div>
      <div class="text-caption text-grey-6">
        {{ formatDate(history.date) }}
      </div>
    </q-card-section>

    <q-card-section v-if="history.topped">
      <div class="text-h6 text-green">Topped!</div>
      <div class="text-caption text-grey-6">Tries: {{ history.tries }}</div>
    </q-card-section>

    <q-card-section v-else>
      <div class="text-body2 text-grey-7">Attempt</div>
      <div class="text-caption text-grey-6">Tries: {{ history.tries }}</div>
    </q-card-section>

    <q-card-section v-if="history.userRating > 0">
      <q-rating
        v-model="history.userRating"
        size="sm"
        model="number"
        step="1"
        @update:model-value="updateRating"
      />
    </q-card-section>

    <q-card-section v-if="history.difficultyFeedback">
      <div class="text-caption text-grey-6">
        Difficulty: {{ formatFeedback(history.difficultyFeedback) }}
      </div>
    </q-card-section>

    <q-card-section v-if="$store.auth.isAuthenticated">
      <q-btn
        v-if="!history.topped"
        label="Mark as topped"
        color="green"
        flat
        @click="markAsTopped"
      />
      <q-btn
        v-else
        label="Remove"
        color="negative"
        flat
        @click="removeHistory"
      />
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import { useQuasar } from 'quasar'
import { useAuthStore } from '#stores'
import { historyApi } from '#boot'
import type { ClimbingHistory } from '#types'

const props = defineProps<{ history: ClimbingHistory }>()
const emit = defineEmits<{
  (e: 'update', history: ClimbingHistory): void
  (e: 'remove', history: ClimbingHistory): void
}>()

const $q = useQuasar()
const authStore = useAuthStore()

const formatDate = (dateStr: string): string => {
  return new Date(dateStr).toLocaleDateString()
}

const formatFeedback = (feedback: string): string => {
  const mapping: Record<string, string> = {
    MUCH_HARDER: 'Much Harder',
    HARDER: 'Harder',
    ABOUT_RIGHT: 'About Right',
    EASIER: 'Easier',
    MUCH_EASIER: 'Much Easier',
  }
  return mapping[feedback] || feedback
}

const updateRating = async (rating: number) => {
  try {
    await historyApi.update(props.history.id, { userRating: rating })
    emit('update', { ...props.history, userRating: rating })
  } catch (error: any) {
    $q.notify({ message: error?.message || 'Failed to update rating', type: 'negative' })
  }
}

const markAsTopped = async () => {
  try {
    await historyApi.update(props.history.id, { topped: true })
    emit('update', { ...props.history, topped: true })
  } catch (error: any) {
    $q.notify({ message: error?.message || 'Failed to mark as topped', type: 'negative' })
  }
}

const removeHistory = async () => {
  try {
    await historyApi.delete(props.history.id)
    emit('remove', props.history)
  } catch (error: any) {
    $q.notify({ message: error?.message || 'Failed to remove history', type: 'negative' })
  }
}
</script>

<style scoped>
.history-card {
  min-width: 280px;
  max-width: 320px;
}
</style>
