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
            @click="fetchRoutes"
          />
        </q-card-section>
      </q-card>

      <q-card
        v-else-if="routes.length === 0"
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
            Routes will appear here once they are added to this gym.
          </div>
        </q-card-section>
      </q-card>

      <div v-else class="routes-grid">
        <route-card
          v-for="route in routes"
          :key="route.id"
          :route="route"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouteStore } from 'stores/routeStore'
import { useClimbLogStore } from 'stores/climbLogStore'
import { useRouteFeedbackStore } from 'stores/routeFeedbackStore'
import RouteCard from 'src/components/routes/RouteCard.vue'
import { getErrorMessage } from 'src/utils/errors'

const $q = useQuasar()

const routeStore = useRouteStore()
const climbLogStore = useClimbLogStore()
const routeFeedbackStore = useRouteFeedbackStore()

const loading = ref(false)
const error = ref<string | null>(null)

const routes = computed(() => routeStore.routes)

const fetchRoutes = async () => {
  loading.value = true
  error.value = null

  try {
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

onMounted(fetchRoutes)
</script>

<style scoped>
.routes-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.routes-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
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

.routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 380px));
  gap: 22px;
  align-items: start;
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
