<template>
  <q-page padding>
    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" size="40px" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="routeData" class="route-detail-page">
      <section class="route-hero">
        <div class="route-hero-media">
          <q-carousel
            v-if="routeData.images?.length"
            v-model="activeImage"
            animated
            swipeable
            :arrows="routeData.images.length > 1"
            :infinite="routeData.images.length > 1"
            class="route-carousel"
            control-color="white"
          >
            <q-carousel-slide
              v-for="(img, index) in routeData.images"
              :key="index"
              :name="index"
              :img-src="img.url"
              class="route-slide"
            >
              <div class="absolute-top route-image-overlay" />

              <q-chip
                color="primary"
                text-color="white"
                class="absolute-top-right q-ma-md"
              >
                {{ routeData.difficulty.value }}
              </q-chip>

              <q-chip
                v-if="routeData.images.length > 1"
                dense
                color="black"
                text-color="white"
                class="absolute-bottom-right q-ma-md route-image-count"
              >
                {{ activeImage + 1 }} / {{ routeData.images.length }}
              </q-chip>

              <div class="absolute-bottom route-image-caption">
                <div class="route-image-title">
                  {{ routeData.name }}
                </div>

                <div class="text-caption text-grey-3">
                  {{ routeData.wall }}
                </div>

                <div
                  v-if="img.caption"
                  class="text-caption text-grey-4 q-mt-xs"
                >
                  {{ img.caption }}
                </div>
              </div>
            </q-carousel-slide>
          </q-carousel>

          <div
            v-else
            class="route-image-placeholder"
          >
            <q-icon
              name="image_not_supported"
              size="56px"
              color="grey-5"
            />

            <div class="text-body2 text-grey-6 q-mt-sm">
              No images
            </div>
          </div>
        </div>

        <div class="route-hero-content">
          <div class="text-overline text-primary">
            Boulder Route
          </div>

          <div class="route-title-block">
            <h1 class="route-title">
              {{ routeData.name }}
            </h1>

            <div class="text-body1 text-grey-7">
              {{ routeData.wall }}
            </div>
          </div>

          <div class="route-meta q-mt-lg">
            <q-chip
              color="primary"
              text-color="white"
              icon="fitness_center"
            >
              {{ routeData.difficulty.value }}
              <span class="q-ml-xs text-weight-regular">
                {{ routeData.difficulty.scale }}
              </span>
            </q-chip>

            <q-chip
              outline
              color="primary"
              icon="palette"
            >
              {{ routeData.holdColor }}
            </q-chip>
          </div>

          <div class="q-mt-md">
            <div class="text-caption text-grey-6 q-mb-xs">
              Hold types
            </div>

            <div class="row q-gutter-sm">
              <q-chip
                v-for="type in routeData.holdTypes"
                :key="type"
                dense
                color="grey-2"
                text-color="grey-8"
              >
                {{ type }}
              </q-chip>
            </div>
          </div>
        </div>
      </section>

      <section class="history-section">
        <div class="history-header">
          <div>
            <h2 class="section-title">
              Climbing History
            </h2>

            <div class="text-body2 text-grey-6">
              {{ history.length }} {{ history.length === 1 ? 'attempt' : 'attempts' }}
            </div>
          </div>
        </div>

        <q-card
          v-if="history.length === 0"
          flat
          bordered
          class="empty-history-card text-center"
        >
          <q-card-section>
            <q-icon
              name="timeline"
              size="48px"
              color="grey-5"
            />

            <div class="text-h6 q-mt-sm">
              No attempts yet
            </div>

            <div class="text-body2 text-grey-6">
              Your climbing history for this route will appear here.
            </div>
          </q-card-section>
        </q-card>

        <card-grid
          v-else
          variant="compact"
        >
          <history-card
            v-for="item in history"
            :key="item.id"
            :history="item"
            @update="onHistoryUpdate"
            @remove="onHistoryRemove"
          />
        </card-grid>
      </section>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { routeApi, historyApi } from 'src/api'
import { useAuthStore } from 'stores/authStore'
import CardGrid from 'src/components/CardGrid.vue'
import HistoryCard from 'src/components/HistoryCard.vue'
import { getErrorMessage } from 'src/utils/errors'
import type { Route, ClimbingHistory } from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

const routeData = ref<Route | null>(null)
const history = ref<ClimbingHistory[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const activeImage = ref(0)

const fetchRoute = async () => {
  const response = await routeApi.getById(route.params.id as string)
  routeData.value = response
}

const fetchHistory = async () => {
  const response = await historyApi.getByRoute(route.params.id as string)
  history.value = response
}

const fetchPageData = async () => {
  loading.value = true
  error.value = null

  try {
    await Promise.all([
      fetchRoute(),
      fetchHistory(),
    ])
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to fetch route details')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
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
  if (!authStore.isAuthenticated) {
    await router.push('/')
    return
  }

  await fetchPageData()
})
</script>

<style scoped>
.route-detail-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.route-hero {
  display: grid;
  grid-template-columns: minmax(280px, 440px) minmax(0, 1fr);
  gap: 40px;
  align-items: start;
}

.route-hero-media {
  min-width: 0;
}

.route-carousel {
  aspect-ratio: 4 / 5;
  height: auto;
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.16);
}

.route-slide {
  background-size: cover;
  background-position: center;
}

.route-image-overlay {
  height: 120px;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.36),
    rgba(0, 0, 0, 0)
  );
}

.route-image-caption {
  padding: 42px 88px 18px 18px;
  color: white;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.74),
    rgba(0, 0, 0, 0)
  );
}

.route-title-block {
  display: block;
}

.route-image-title {
  font-size: 1.6rem;
  line-height: 1.1;
  font-weight: 800;
}

.route-image-count {
  background: rgba(0, 0, 0, 0.62) !important;
}

.route-image-placeholder {
  aspect-ratio: 4 / 5;
  border-radius: 22px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.12), transparent 32%),
    linear-gradient(135deg, #f5f7fb, #e8eef8);
}

.route-hero-content {
  min-width: 0;
  padding-top: 12px;
}

.route-title {
  margin: 0;
  font-size: clamp(2.25rem, 6vw, 4rem);
  line-height: 1.02;
  font-weight: 800;
}

.route-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-section {
  margin-top: 56px;
}

.history-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.section-title {
  margin: 0;
  font-size: 1.5rem;
  line-height: 1.2;
  font-weight: 700;
}

.empty-history-card {
  max-width: 520px;
  margin: 0 auto;
  border-radius: 18px;
  padding: 24px;
}

@media (max-width: 900px) {
  .route-hero {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .route-hero-media {
    max-width: none;
    width: calc(100% + 32px);
    margin-left: -16px;
    margin-right: -16px;
  }

  .route-carousel {
    border-radius: 0;
    box-shadow: none;
  }

  .route-image-placeholder {
    border-radius: 0;
  }

  .route-title-block {
    display: none;
  }

  .route-hero-content {
    padding-top: 0;
  }
}

@media (max-width: 600px) {
  .history-section {
    margin-top: 40px;
  }
}
</style>
