<template>
  <q-page padding>
    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" size="40px" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="gym" class="gym-page">
      <section class="gym-hero">
        <div>
          <div class="text-overline text-primary">
            Climbing Gym
          </div>

          <h1 class="gym-title">
            {{ gym.name }}
          </h1>
        </div>
      </section>

      <q-list class="gym-info-list">
        <q-expansion-item
          v-if="gym.description || gym.address"
          icon="info"
          label="Gym information"
          header-class="text-primary"
        >
          <q-card>
            <q-card-section>
              <div v-if="gym.description">
                <div class="text-caption text-grey-6">
                  Description
                </div>

                <div class="text-body2">
                  {{ gym.description }}
                </div>
              </div>

              <div v-if="gym.address">
                <div class="q-mt-md text-caption text-grey-6">
                  Address
                </div>

                <div class="text-body2">
                  {{ gym.address }}
                </div>
              </div>
            </q-card-section>
          </q-card>
        </q-expansion-item>

        <q-separator />

        <q-expansion-item
          icon="schedule"
          label="Opening hours"
          header-class="text-primary"
        >
          <q-card>
            <q-card-section>
              <div
                v-if="hasOpeningHours"
                class="opening-hours"
              >
                <div
                  v-for="(hours, day) in gym.openingHours"
                  :key="day"
                  class="opening-hours-row"
                >
                  <span class="text-weight-medium">
                    {{ day }}
                  </span>

                  <span class="text-grey-7">
                    {{ hours }}
                  </span>
                </div>
              </div>

              <div
                v-else
                class="text-grey-6"
              >
                No opening hours available
              </div>
            </q-card-section>
          </q-card>
        </q-expansion-item>
      </q-list>

      <section class="routes-section">
        <div class="routes-header">
          <div>
            <h2 class="routes-title">
              Routes
            </h2>

            <div class="text-body2 text-grey-6">
              {{ routes.length }} {{ routes.length === 1 ? 'route' : 'routes' }}
              across
              {{ routesByWall.length }}
              {{ routesByWall.length === 1 ? 'wall' : 'walls' }}
            </div>
          </div>
        </div>

        <q-card
          v-if="routes.length === 0"
          flat
          bordered
          class="empty-routes-card text-center"
        >
          <q-card-section>
            <q-icon
              name="terrain"
              size="48px"
              color="grey-5"
            />

            <div class="text-h6 q-mt-sm">
              No routes yet
            </div>

            <div class="text-body2 text-grey-6">
              Add the first route for this gym.
            </div>
          </q-card-section>
        </q-card>

        <div
          v-else
          class="wall-sections"
        >
          <q-expansion-item
            v-for="wallGroup in routesByWall"
            :key="wallGroup.wall"
            default-opened
            class="wall-section"
            header-class="wall-section-expansion-header"
            expand-icon-class="text-grey-6"
          >
            <template #header>
              <div class="wall-section-header">
                <div>
                  <h3 class="wall-title">
                    {{ wallGroup.wall }}
                  </h3>

                  <div class="text-body2 text-grey-6">
                    {{ wallGroup.routes.length }}
                    {{ wallGroup.routes.length === 1 ? 'route' : 'routes' }}
                  </div>
                </div>
              </div>
            </template>

            <card-grid class="q-my-md">
              <route-card
                v-for="routeItem in wallGroup.routes"
                :key="routeItem.id"
                :route="routeItem"
              />
            </card-grid>
          </q-expansion-item>
        </div>
      </section>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { gymApi, routeApi } from 'src/api'
import { useAuthStore } from 'stores/authStore'
import CardGrid from 'src/components/CardGrid.vue'
import RouteCard from 'src/components/routes/RouteCard.vue'
import type { Gym, Route as ClimbingRoute } from 'src/types'

type WallRouteGroup = {
  wall: string
  routes: ClimbingRoute[]
}

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

const hasOpeningHours = computed(() => {
  return Boolean(
    gym.value?.openingHours &&
      Object.keys(gym.value.openingHours).length > 0,
  )
})

const routesByWall = computed<WallRouteGroup[]>(() => {
  const groupedRoutes = routes.value.reduce<Record<string, ClimbingRoute[]>>(
    (groups, routeItem) => {
      const wallName = routeItem.wall || 'Unknown wall'

      if (!groups[wallName]) {
        groups[wallName] = []
      }

      groups[wallName].push(routeItem)

      return groups
    },
    {},
  )

  return Object.entries(groupedRoutes)
    .map(([wall, wallRoutes]) => ({
      wall,
      routes: wallRoutes,
    }))
    .sort((a, b) => a.wall.localeCompare(b.wall))
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
  routes.value = response
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
</script>

<style scoped>
.gym-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.gym-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.gym-title {
  margin: 0;
  font-size: clamp(2rem, 5vw, 3.25rem);
  line-height: 1.05;
  font-weight: 700;
}

.gym-info-list {
  max-width: 600px;
  margin-bottom: 40px;
}

.opening-hours {
  display: grid;
  gap: 8px;
}

.opening-hours-row {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 6px 0;
}

.routes-section {
  margin-top: 24px;
}

.routes-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 32px;
}

.routes-title {
  margin: 0;
  font-size: 1.5rem;
  line-height: 1.2;
  font-weight: 700;
}

.wall-sections {
  display: grid;
  gap: 48px;
}

.wall-section {
  min-width: 0;
}

.wall-section-header {
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.wall-section-expansion-header {
  padding: 0;
  margin-bottom: 20px;
  min-height: unset;
}

.wall-title {
  margin: 0;
  font-size: 1.25rem;
  line-height: 1.2;
  font-weight: 700;
}

.empty-routes-card {
  max-width: 520px;
  margin: 0 auto;
  border-radius: 18px;
  padding: 24px;
}

@media (max-width: 600px) {
  .gym-hero {
    flex-direction: column;
    align-items: stretch;
  }

  .gym-hero .q-btn {
    align-self: flex-start;
  }

  .opening-hours-row {
    flex-direction: column;
    gap: 2px;
  }

  .wall-sections {
    gap: 40px;
  }
}
</style>
