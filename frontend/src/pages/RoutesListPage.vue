<template>
  <q-page padding>
    <div class="row items-center q-mb-md">
      <div class="text-h4">Routes</div>
    </div>

    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="routes.length === 0" class="text-center text-grey-6 q-pa-md">
      No routes found
    </div>

    <div v-else class="row flex flex-wrap">
      <route-card
        v-for="route in routes"
        :key="route.id"
        :route="route"
      />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { routeApi } from 'boot/axios'
import { useRouteStore } from 'stores/routeStore'
import type { Route } from 'src/types'

const $q = useQuasar()
const routeStore = useRouteStore()
const routes = ref<Route[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const fetchRoutes = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await routeApi.getByGym('')
    routes.value = data
  } catch (err: any) {
    error.value = err.message || 'Failed to fetch routes'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

onMounted(fetchRoutes)
</script>
