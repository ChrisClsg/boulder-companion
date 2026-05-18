<template>
  <q-page padding>
    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="user" class="q-pa-md">
      <div class="text-h4">Profile</div>

      <div class="q-mt-md">
        <h5 class="text-h6">User Information</h5>
        <div class="text-body1">{{ user.name }}</div>
        <div class="text-body2 text-grey-6">{{ user.email }}</div>
        <div class="text-body2 text-grey-6">Role: {{ formatRole(user.role) }}</div>

        <div class="q-mt-md">
          <h6 class="text-h6">Favorite Gyms ({{ user.favoriteGyms.length }})</h6>
          <div v-if="user.favoriteGyms.length === 0" class="text-grey-6">None</div>
          <div v-else class="row flex-wrap">
            <q-badge v-for="id in user.favoriteGyms" :key="id" color="primary">
              Gym {{ id }}
            </q-badge>
          </div>
        </div>
      </div>

      <div class="q-mt-md">
        <h5 class="text-h6">Climbing History</h5>
        <div class="text-body1">Total attempts: {{ history.length }}</div>
        <div class="text-body1">Topped routes: {{ toppedCount }}</div>
        <div class="text-body1">Average rating: {{ averageRating }} / 5</div>
      </div>

      <div class="q-mt-md">
        <q-btn
          label="View History"
          color="primary"
          :to="'/history'"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'stores/authStore'
import { useHistoryStore } from 'stores/historyStore'
import { useFavoriteStore } from 'stores/favoriteStore'
import type { User } from 'src/types'

const $q = useQuasar()
const authStore = useAuthStore()
const historyStore = useHistoryStore()
const favoriteStore = useFavoriteStore()
const user = ref<User | null>(null)
const history = computed(() => historyStore.history)
const loading = ref(false)
const error = ref<string | null>(null)

const toppedCount = computed(() => {
  return history.value.filter(h => h.topped).length
})

const averageRating = computed(() => {
  if (history.value.length === 0) return 0
  const sum = history.value.reduce((acc, h) => acc + h.userRating, 0)
  return Math.round((sum / history.value.length) * 10) / 10
})

const formatRole = (role: string): string => {
  const mapping: Record<string, string> = {
    CLIMBER: 'Climber',
    GYM_ADMIN: 'Gym Admin',
    ROUTE_SETTER: 'Route Setter',
  }
  return mapping[role] || role
}

const fetchUser = async () => {
  loading.value = true
  error.value = null
  try {
    await authStore.fetchUser()
    user.value = authStore.user
    await favoriteStore.fetchFavoriteGyms()
  } catch (err: unknown) {
    error.value = (err as { message?: string }).message || 'Failed to fetch user'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

const fetchHistory = async () => {
  loading.value = true
  error.value = null
  try {
    await historyStore.fetchHistory(user.value?.id)
  } catch (err: unknown) {
    error.value = (err as { message?: string }).message || 'Failed to fetch history'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchUser(), fetchHistory()])
})
</script>
