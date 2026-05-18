<template>
  <q-page padding>
    <div class="row items-center q-mb-md">
      <div class="text-h4">Climbing History</div>
      <q-select
        v-model="filter.userId"
        :options="userOptions"
        label="Filter by User"
        outlined
        clearable
      />
      <q-select
        v-model="filter.gymId"
        :options="gymOptions"
        label="Filter by Gym"
        outlined
        clearable
      />
    </div>

    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="history.length === 0" class="text-center text-grey-6 q-pa-md">
      No climbing history found
    </div>

    <div v-else class="row flex flex-wrap">
      <history-card
        v-for="item in filteredHistory"
        :key="item.id"
        :history="item"
        @update="onHistoryUpdate"
        @remove="onHistoryRemove"
      />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { historyApi, gymApi } from 'src/api'
import HistoryCard from 'src/components/HistoryCard.vue'
import type { ClimbingHistory, Gym } from 'src/types'

const $q = useQuasar()

const history = ref<ClimbingHistory[]>([])
const gyms = ref<Gym[]>([])
const userOptions = ref<string[]>([])
const gymOptions = ref<string[]>([])
const filter = ref({
  userId: '',
  gymId: '',
})
const loading = ref(false)
const error = ref<string | null>(null)

const filteredHistory = computed(() => {
  return history.value.filter(h => {
    if (filter.value.userId && h.userId !== filter.value.userId) return false
    if (filter.value.gymId && h.gymId !== filter.value.gymId) return false
    return true
  })
})

const fetchGyms = async () => {
  try {
    const data = await gymApi.getAll()
    gyms.value = data
    gymOptions.value = data.map(g => g.name)
  } catch (err: unknown) {
    $q.notify({ message: (err as Error).message || 'Failed to fetch gyms', type: 'negative' })
  }
}

const fetchHistory = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await historyApi.getAll()
    history.value = response.data
  } catch (err: unknown) {
    error.value = (err as Error).message || 'Failed to fetch history'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

const onHistoryUpdate = (updated: ClimbingHistory) => {
  const index = history.value.findIndex(h => h.id === updated.id)
  if (index !== -1) {
    history.value[index] = updated
  }
}

const onHistoryRemove = (removed: ClimbingHistory) => {
  history.value = history.value.filter(h => h.id !== removed.id)
}

onMounted(async () => {
  await Promise.all([fetchGyms(), fetchHistory()])
})
</script>
