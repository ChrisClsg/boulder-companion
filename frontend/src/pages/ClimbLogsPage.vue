<template>
  <q-page padding>
    <div class="climb-logs-page">
      <div class="page-header">
        <div>
          <div class="text-overline text-primary">
            Climb logs
          </div>

          <h1>Your climbing history</h1>

          <p>
            Review your logged attempts, tops and flashes across your gyms.
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
          </div>
        </q-card-section>
      </q-card>

      <div v-if="loading" class="state state--loading">
        <q-spinner-dots color="primary" size="44px" />

        <div class="text-body2 text-grey-6 q-mt-sm">
          Loading climb logs...
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
            Could not load climb logs
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
        v-else-if="filteredLogs.length === 0"
        flat
        bordered
        class="state state--empty"
      >
        <q-card-section>
          <q-icon
            name="timeline"
            size="48px"
            color="grey-5"
          />

          <div class="text-h6 q-mt-sm">
            No climb logs found
          </div>

          <div class="text-body2 text-grey-6">
            Log attempts from a route card or route detail page.
          </div>
        </q-card-section>
      </q-card>

      <div v-else class="logs-list">
        <q-card
          v-for="log in filteredLogs"
          :key="log.id"
          flat
          bordered
          class="log-card"
          @click="$router.push(`/routes/${log.routeId}`)"
        >
          <q-card-section>
            <div class="log-card-content">
              <div class="log-main">
                <q-chip
                  dense
                  :color="logColor(log)"
                  text-color="white"
                  :icon="logIcon(log)"
                >
                  {{ logLabel(log) }}
                </q-chip>

                <div>
                  <div class="text-subtitle1 text-weight-bold">
                    {{ routeName(log.routeId) }}
                  </div>

                  <div class="text-caption text-grey-6">
                    {{ gymName(log.gymId) }} ·
                    {{ log.attempts }}
                    {{ log.attempts === 1 ? 'attempt' : 'attempts' }} ·
                    {{ formatDate(log.climbedAt) }}
                  </div>
                </div>
              </div>

              <q-btn
                flat
                round
                dense
                color="negative"
                icon="delete_outline"
                :loading="deletingLogId === log.id"
                @click="ev => { ev.stopPropagation(); deleteLog(log) }"
              />
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useGymStore } from 'stores/gymStore'
import { useRouteStore } from 'stores/routeStore'
import { useClimbLogStore } from 'stores/climbLogStore'
import { getErrorMessage } from 'src/utils/errors'
import type { ClimbLog } from 'src/types'

type ResultFilter = 'attempted' | 'topped' | 'flashed'

const $q = useQuasar()

const gymStore = useGymStore()
const routeStore = useRouteStore()
const climbLogStore = useClimbLogStore()

const loading = ref(false)
const error = ref<string | null>(null)
const deletingLogId = ref<string | null>(null)

const filter = ref<{
  gymId: string | null
  result: ResultFilter | null
}>({
  gymId: null,
  result: null,
})

const logs = computed(() => climbLogStore.logs)

const gymOptions = computed(() =>
  gymStore.gyms.map(gym => ({
    label: gym.name,
    value: gym.id,
  })),
)

const resultOptions = [
  {
    label: 'Unfinished',
    value: 'attempted',
  },
  {
    label: 'Topped',
    value: 'topped',
  },
  {
    label: 'Flash',
    value: 'flashed',
  },
]

const filteredLogs = computed(() => {
  return [...logs.value]
    .filter(log => {
      if (filter.value.gymId && log.gymId !== filter.value.gymId) {
        return false
      }

      if (filter.value.result === 'flashed') {
        return log.flashed
      }

      if (filter.value.result === 'topped') {
        return log.topped
      }

      if (filter.value.result === 'attempted') {
        return !log.topped && !log.flashed
      }

      return true
    })
    .sort((a, b) =>
      new Date(b.climbedAt).getTime() - new Date(a.climbedAt).getTime(),
    )
})

const fetchPageData = async () => {
  loading.value = true
  error.value = null

  try {
    await Promise.all([
      gymStore.fetchGyms(),
      climbLogStore.fetchMyLogs(),
    ])

    const gymIds = new Set(climbLogStore.logs.map(log => log.gymId))

    await Promise.allSettled(
      [...gymIds].map(gymId => routeStore.fetchRoutesByGym(gymId)),
    )
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to fetch climb logs')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
  } finally {
    loading.value = false
  }
}

const deleteLog = async (log: ClimbLog) => {
  deletingLogId.value = log.id

  try {
    await climbLogStore.deleteLog(log.id)

    $q.notify({
      message: 'Log deleted',
      type: 'positive',
    })
  } catch (err: unknown) {
    $q.notify({
      message: getErrorMessage(err, 'Failed to delete log'),
      type: 'negative',
    })
  } finally {
    deletingLogId.value = null
  }
}

const routeName = (routeId: string): string => {
  return routeStore.getRoute(routeId)?.name ?? 'Unknown route'
}

const gymName = (gymId: string): string => {
  return gymStore.getGym(gymId)?.name ?? 'Unknown gym'
}

const logLabel = (log: ClimbLog): string => {
  if (log.flashed) {
    return 'Flash'
  }

  if (log.topped) {
    return 'Topped'
  }

  return 'Tried'
}

const logColor = (log: ClimbLog): string => {
  if (log.flashed) {
    return 'purple'
  }

  if (log.topped) {
    return 'positive'
  }

  return 'orange'
}

const logIcon = (log: ClimbLog): string => {
  if (log.flashed) {
    return 'bolt'
  }

  if (log.topped) {
    return 'check_circle'
  }

  return 'hourglass_bottom'
}

const formatDate = (value: string): string => {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

onMounted(fetchPageData)
</script>

<style scoped>
.climb-logs-page {
  width: 100%;
  max-width: 1040px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: clamp(2.2rem, 5vw, 4rem);
  line-height: 1;
  letter-spacing: -0.06em;
  font-weight: 850;
}

.page-header p {
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
}

.filter-control {
  min-width: 220px;
  flex: 1 1 220px;
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

.logs-list {
  display: grid;
  gap: 12px;
}

.log-card {
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #fafbfc);
}

.log-card:hover {
  cursor: pointer;
}

.log-card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.log-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

@media (max-width: 600px) {
  .log-card-content,
  .log-main {
    align-items: flex-start;
  }

  .log-main {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
