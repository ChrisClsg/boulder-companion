<template>
  <q-page padding>
    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="routeData" class="q-pa-md">
      <div class="text-h4">{{ routeData.name }}</div>
      <div class="text-body2 text-grey-7">{{ routeData.wall }}</div>
      <div class="text-body2 text-grey-7">Difficulty: {{ routeData.difficulty.value }} ({{ routeData.difficulty.scale }})</div>
      <div class="text-body2 text-grey-7">Hold color: {{ routeData.holdColor }}</div>
      <div class="text-body2 text-grey-7">Hold types: {{ routeData.holdTypes.join(', ') }}</div>

      <div class="q-mt-md">
        <h5 class="text-h6">Images</h5>
        <div v-if="routeData.images && routeData.images.length > 0" class="row flex flex-wrap">
          <q-img
            v-for="(img, index) in routeData.images"
            :key="index"
            :src="img.url"
            :caption="img.caption"
            style="max-width: 200px; height: 150px"
          />
        </div>
        <div v-else class="text-grey-6">No images</div>
      </div>

      <div class="q-mt-md">
        <h5 class="text-h6">Climbing History</h5>
        <div v-if="history.length === 0" class="text-grey-6">No attempts yet</div>
        <div v-else class="row flex-wrap">
          <history-card
            v-for="item in history"
            :key="item.id"
            :history="item"
            @update="onHistoryUpdate"
            @remove="onHistoryRemove"
          />
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { routeApi, historyApi } from 'boot/axios'
import { useAuthStore } from 'stores/authStore'
import HistoryCard from 'src/components/HistoryCard.vue'
import type { Route, ClimbingHistory } from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const routeData = ref<Route | null>(null)
const history = ref<ClimbingHistory[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const fetchRoute = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await routeApi.getById(route.params.id as string)
    routeData.value = response.data as Route
  } catch (err: unknown) {
    error.value = (err as { message?: string }).message || 'Failed to fetch route'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

const fetchHistory = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await historyApi.getByRoute(route.params.id as string)
    history.value = response.data as ClimbingHistory[]
  } catch (err: unknown) {
    error.value = (err as { message?: string }).message || 'Failed to fetch history'
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
  if (authStore.isAuthenticated) {
    await Promise.all([fetchRoute(), fetchHistory()])
  } else {
    await router.push('/')
  }
})
</script>
