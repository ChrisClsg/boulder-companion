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

      <div v-if="gym.description" class="text-body2 text-grey-6">
        {{ gym.description }}
      </div>

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
            v-for="routeItem in routes"
            :key="routeItem.id"
            :route="routeItem"
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
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { gymApi, routeApi } from 'boot/axios'
import { useAuthStore } from 'stores/authStore'
import RouteCard from 'src/components/RouteCard.vue'
import type { Gym, Route as ClimbingRoute } from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const gym = ref<Gym | null>(null)
const routes = ref<ClimbingRoute[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const gymId = computed(() => {
  const id = route.params.id
  return Array.isArray(id) ? id[0] : id
})

const getErrorMessage = (err: unknown, fallback: string) => {
  if (err instanceof Error) {
    return err.message
  }

  return fallback
}

const fetchGym = async (id: string) => {
  const gymData = await gymApi.getById(id)
  gym.value = gymData
}

const fetchRoutes = async (id: string) => {
  const response = await routeApi.getByGym(id)
  routes.value = response.data as ClimbingRoute[]
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    await router.push('/')
    return
  }

  const id = gymId.value

  if (!id) {
    error.value = 'Missing gym id'
    loading.value = false
    return
  }

  loading.value = true
  error.value = null

  try {
    await Promise.all([
      fetchGym(id),
      fetchRoutes(id),
    ])
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to load gym')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
  } finally {
    loading.value = false
  }
})
</script>+
