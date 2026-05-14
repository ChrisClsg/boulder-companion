<template>
  <q-page padding>
    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="gym" class="q-pa-md">
      <div class="text-h4">{{ gym.name }}</div>
      <div class="text-body1 text-grey-7">{{ gym.address }}</div>
      <div v-if="gym.description" class="text-body2 text-grey-6">{{ gym.description }}</div>

      <div class="q-mt-md">
        <h5 class="text-h6">Opening Hours</h5>
        <div class="text-body1">
          <div v-for="(hours, day) in gym.openingHours" :key="day">
            {{ day }}: {{ hours }}
          </div>
        </div>
      </div>

      <div class="q-mt-md">
        <h5 class="text-h6">Routes</h5>
        <div v-if="routes.length === 0" class="text-center text-grey-6">
          No routes yet
        </div>
        <div v-else class="row flex flex-wrap">
          <route-card
            v-for="route in routes"
            :key="route.id"
            :route="route"
          />
        </div>
      </div>

      <div v-if="authStore.isAuthenticated" class="q-mt-md">
        <q-btn
          label="Add Route"
          color="primary"
          :to="`/gyms/${gym.id}/routes/new`"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { gymApi, routeApi } from 'boot/axios'
import { useAuthStore } from 'stores/authStore'
import type { Gym, Route } from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const gym = ref<Gym | null>(null)
const routes = ref<Route[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const fetchGym = async () => {
  loading.value = true
  error.value = null
  try {
    const gymData = await gymApi.getById(route.params.id as string)
    gym.value = gymData
  } catch (err: unknown) {
    error.value = (err as { message?: string }).message || 'Failed to fetch gym'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

const fetchRoutes = async () => {
  loading.value = true
  error.value = null
  try {
    const routesData = await routeApi.getByGym(gym.value?.id || '')
    routes.value = routesData.data as Route[]
  } catch (err: unknown) {
    error.value = (err as { message?: string }).message || 'Failed to fetch routes'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (authStore.isAuthenticated) {
    await Promise.all([fetchGym(), fetchRoutes()])
  } else {
    await router.push('/')
  }
})
</script>
