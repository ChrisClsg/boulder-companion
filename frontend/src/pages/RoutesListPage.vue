<template>
  <q-page padding>
    <div class="routes-page">
      <div class="routes-header">
        <div>
          <div class="text-overline text-primary">
            Routes
          </div>

          <h1>Climbing routes</h1>

          <p>
            Browse routes, log attempts, mark tops and flashes, and keep your feedback up to date.
          </p>
        </div>
      </div>

      <q-card flat bordered class="filters-card">
        <q-card-section>
          <div class="filters-row">
            <q-select
              v-model="filter.gymId"
              :options="gymOptions"
              label="Filter by gym"
              outlined
              clearable
              emit-value
              map-options
              class="filter-control"
            />

            <q-select
              v-model="filter.result"
              :options="resultOptions"
              label="Filter by result"
              outlined
              clearable
              emit-value
              map-options
              class="filter-control"
            />

            <q-select
              v-model="filter.userRating"
              :options="ratingOptions"
              label="Filter by rating"
              outlined
              clearable
              emit-value
              map-options
              class="filter-control"
            />

            <div class="filter-control filter-toggle-wrap">
              <q-toggle
                v-model="filter.favorites"
                label="Favorites only"
                color="primary"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <div v-if="loading" class="state state--loading">
        <q-spinner-dots color="primary" size="44px" />

        <div class="text-body2 text-grey-6 q-mt-sm">
          Loading routes...
        </div>
      </div>

      <q-card
        v-else-if="error"
        flat
        bordered
        class="state state--error"
      >
        <q-card-section>
          <q-icon name="error_outline" color="negative" size="44px" />

          <div class="text-h6 q-mt-sm">
            Could not load routes
          </div>

          <div class="text-body2 text-grey-7 q-mt-xs">
            {{ error }}
          </div>

          <q-btn
            label="Try again"
            color="primary"
            unelevated
            rounded
            icon="refresh"
            class="q-mt-md"
            @click="fetchPageData"
          />
        </q-card-section>
      </q-card>

      <q-card
        v-else-if="filteredRoutes.length === 0"
        flat
        bordered
        class="state state--empty"
      >
        <q-card-section>
          <q-icon name="route" size="48px" color="grey-5" />

          <div class="text-h6 q-mt-sm">
            No routes found
          </div>

          <div class="text-body2 text-grey-6">
            {{ hasActiveFilters ? 'No routes match your current filters.' : 'Routes will appear here once they are added to a gym.' }}
          </div>

          <q-btn
            v-if="hasActiveFilters"
            flat
            color="primary"
            label="Clear filters"
            class="q-mt-sm"
            @click="clearFilters"
          />
        </q-card-section>
      </q-card>

      <div v-else class="routes-grid">
        <route-card
          v-for="route in filteredRoutes"
          :key="route.id"
          :route="route"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useGymStore } from 'stores/gymStore'
import { useRouteStore } from 'stores/routeStore'
import { useClimbLogStore } from 'stores/climbLogStore'
import { useRouteFeedbackStore } from 'stores/routeFeedbackStore'
import { useFavoriteStore } from 'stores/favoriteStore'
import RouteCard from 'src/components/routes/RouteCard.vue'
import { getErrorMessage } from 'src/utils/errors'

type ResultFilter = 'unlogged' | 'attempted' | 'topped' | 'flashed'

const $q = useQuasar()
const vueRoute = useRoute()
const router = useRouter()

const gymStore = useGymStore()
const routeStore = useRouteStore()
const climbLogStore = useClimbLogStore()
const routeFeedbackStore = useRouteFeedbackStore()
const favoriteStore = useFavoriteStore()

const loading = ref(false)
const error = ref<string | null>(null)

const filter = ref<{
  gymId: string | null
  result: ResultFilter | null
  userRating: number | null
  favorites: boolean
}>({
  gymId: (vueRoute.query.gymId as string) || null,
  result: (vueRoute.query.result as ResultFilter) || null,
  userRating: vueRoute.query.userRating ? Number(vueRoute.query.userRating) : null,
  favorites: vueRoute.query.favorites === 'true',
})

watch(
  filter,
  (val) => {
    void router.replace({
      query: {
        ...(val.gymId ? { gymId: val.gymId } : {}),
        ...(val.result ? { result: val.result } : {}),
        ...(val.userRating !== null ? { userRating: String(val.userRating) } : {}),
        ...(val.favorites ? { favorites: 'true' } : {}),
      },
    })
  },
  { deep: true },
)

const gymOptions = computed(() => {
  const favIds = new Set(favoriteStore.favoriteGyms.map(g => g.id))
  const sorted = [...gymStore.gyms].sort((a, b) => {
    const aFav = favIds.has(a.id) ? 0 : 1
    const bFav = favIds.has(b.id) ? 0 : 1
    return aFav - bFav || a.name.localeCompare(b.name)
  })
  return sorted.map(gym => ({
    label: favIds.has(gym.id) ? `${gym.name} ★` : gym.name,
    value: gym.id,
  }))
})

const resultOptions = [
  { label: 'Not logged', value: 'unlogged' },
  { label: 'Attempted', value: 'attempted' },
  { label: 'Topped', value: 'topped' },
  { label: 'Flashed', value: 'flashed' },
]

const ratingOptions = [1, 2, 3, 4, 5].map(n => ({
  label: '★'.repeat(n),
  value: n,
}))

const favoriteRouteIds = computed(() =>
  new Set(favoriteStore.favoriteRoutes.map(r => r.id)),
)

const filteredRoutes = computed(() =>
  routeStore.routes.filter(route => {
    if (filter.value.gymId && route.gymId !== filter.value.gymId) return false

    if (filter.value.result) {
      const summary = climbLogStore.getSummaryByRoute(route.id)
      if (filter.value.result === 'unlogged' && summary.totalLogs > 0) return false
      if (filter.value.result === 'attempted' && (summary.totalLogs === 0 || summary.topped)) return false
      if (filter.value.result === 'topped' && !summary.topped) return false
      if (filter.value.result === 'flashed' && !summary.flashed) return false
    }

    if (filter.value.userRating !== null) {
      const feedback = routeFeedbackStore.getFeedbackByRoute(route.id)
      if (!feedback || feedback.userRating !== filter.value.userRating) return false
    }

    if (filter.value.favorites && !favoriteRouteIds.value.has(route.id)) return false

    return true
  }),
)

const hasActiveFilters = computed(
  () => !!(filter.value.gymId || filter.value.result || filter.value.userRating !== null || filter.value.favorites),
)

const clearFilters = () => {
  filter.value = { gymId: null, result: null, userRating: null, favorites: false }
}

const fetchPageData = async () => {
  loading.value = true
  error.value = null

  try {
    await Promise.all([
      gymStore.fetchGyms(),
      favoriteStore.fetchFavoriteGyms(),
      favoriteStore.fetchFavoriteRoutes(),
    ])

    await routeStore.fetchRoutesByGym('')
    await climbLogStore.fetchMyLogs()

    await Promise.allSettled(
      routeStore.routes.map(route =>
        routeFeedbackStore.fetchMyFeedback(route.id),
      ),
    )
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to fetch routes')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
  } finally {
    loading.value = false
  }
}

onMounted(fetchPageData)
</script>

<style scoped>
.routes-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.routes-header {
  margin-bottom: 24px;
}

.routes-header h1 {
  margin: 0;
  font-size: clamp(2.2rem, 5vw, 4rem);
  line-height: 1;
  letter-spacing: -0.06em;
  font-weight: 850;
}

.routes-header p {
  max-width: 680px;
  margin: 14px 0 0;
  color: #667085;
  font-size: 1.05rem;
  line-height: 1.6;
}

.filters-card {
  margin-bottom: 24px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.08), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
}

.filters-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.filter-control {
  min-width: 200px;
  flex: 1 1 200px;
}

.filter-toggle-wrap {
  display: flex;
  align-items: center;
  min-width: 160px;
  flex: 0 1 160px;
}

.routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 380px));
  gap: 22px;
  align-items: start;
  justify-content: center;
}

.state {
  max-width: 520px;
  margin: 64px auto;
  text-align: center;
}

.state--loading {
  padding: 48px 24px;
}

.state--error,
.state--empty {
  border-radius: 24px;
  padding: 20px;
}

@media (max-width: 700px) {
  .routes-grid {
    grid-template-columns: 1fr;
  }
}
</style>
