<template>
  <q-page padding>
    <div class="gym-list-page">
      <div class="gyms-header">
        <div>
          <div class="text-overline text-primary">
            Gyms
          </div>

          <h1>Climbing gyms</h1>

          <p>
            Find your favorite climbing spots, browse all gyms, and jump back into routes quickly.
          </p>
        </div>
      </div>

      <q-card flat bordered class="search-card">
        <q-card-section>
          <q-input
            v-model="search"
            outlined
            rounded
            clearable
            debounce="150"
            placeholder="Search gyms by name..."
            class="search-input"
            @clear="search = ''"
          >
            <template #prepend>
              <q-icon name="search" />
            </template>
          </q-input>
        </q-card-section>
      </q-card>

      <div v-if="isLoading" class="state state--loading">
        <q-spinner-dots color="primary" size="44px" />

        <div class="text-body2 text-grey-6 q-mt-sm">
          Loading gyms...
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
            Could not load gyms
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

      <template v-else>
        <section class="gym-section">
          <div class="section-header">
            <div>
              <h2>Favorite gyms</h2>

              <p>
                Your saved climbing spots for quick access.
              </p>
            </div>

            <q-chip
              outline
              color="primary"
              icon="favorite"
            >
              {{ filteredFavoriteGyms.length }}
            </q-chip>
          </div>

          <q-card
            v-if="filteredFavoriteGyms.length === 0"
            flat
            bordered
            class="empty-section-card"
          >
            <q-card-section>
              <q-icon name="favorite_border" size="42px" color="grey-5" />

              <div class="text-h6 q-mt-sm">
                No favorite gyms yet
              </div>

              <div class="text-body2 text-grey-6">
                Favorite gyms will appear here once you mark them.
              </div>
            </q-card-section>
          </q-card>

          <card-grid
            v-else
            variant="compact"
          >
            <gym-card
              v-for="gym in filteredFavoriteGyms"
              :key="gym.id"
              :gym="gym"
            />
          </card-grid>
        </section>

        <section class="gym-section">
          <div class="section-header">
            <div>
              <h2>Other gyms</h2>

              <p>
                Discover more climbing gyms you have not favorited yet.
              </p>
            </div>

            <q-chip
              outline
              color="primary"
              icon="fitness_center"
            >
              {{ filteredOtherGyms.length }}
            </q-chip>
          </div>

          <q-card
            v-if="gymStore.gyms.length === 0"
            flat
            bordered
            class="empty-section-card"
          >
            <q-card-section>
              <q-icon name="fitness_center" size="48px" color="grey-5" />

              <div class="text-h6 q-mt-sm">
                No gyms found
              </div>

              <div class="text-body2 text-grey-6">
                Gyms will appear here once they are added.
              </div>
            </q-card-section>
          </q-card>

          <q-card
            v-else-if="filteredOtherGyms.length === 0"
            flat
            bordered
            class="empty-section-card"
          >
            <q-card-section>
              <q-icon name="search_off" size="48px" color="grey-5" />

              <div class="text-h6 q-mt-sm">
                No matching gyms
              </div>

              <div class="text-body2 text-grey-6">
                Try a different search term.
              </div>
            </q-card-section>
          </q-card>

          <card-grid
            v-else
            variant="compact"
          >
            <gym-card
              v-for="gym in filteredOtherGyms"
              :key="gym.id"
              :gym="gym"
            />
          </card-grid>
        </section>
      </template>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useGymStore } from 'stores/gymStore'
import { useFavoriteStore } from 'stores/favoriteStore'
import CardGrid from 'components/CardGrid.vue'
import GymCard from 'components/GymCard.vue'

const gymStore = useGymStore()
const favoriteStore = useFavoriteStore()

const search = ref('')

const isLoading = computed(() =>
  gymStore.isLoading || favoriteStore.isLoadingFavoriteGyms,
)

const error = computed(() =>
  gymStore.error || favoriteStore.error,
)

const normalizedSearch = computed(() =>
  search.value.trim().toLowerCase(),
)

const matchesSearch = (gym: { name?: string; location?: string }) => {
  if (!normalizedSearch.value) {
    return true
  }

  return [
    gym.name,
    gym.location,
  ]
    .filter(Boolean)
    .some(value =>
      String(value).toLowerCase().includes(normalizedSearch.value),
    )
}

const favoriteGymIds = computed(() =>
  new Set(favoriteStore.favoriteGyms.map(gym => gym.id)),
)

const filteredFavoriteGyms = computed(() =>
  favoriteStore.favoriteGyms.filter(matchesSearch),
)

const filteredOtherGyms = computed(() =>
  gymStore.gyms
    .filter(gym => !favoriteGymIds.value.has(gym.id))
    .filter(matchesSearch),
)

const fetchPageData = async () => {
  await Promise.all([
    gymStore.fetchGyms(),
    favoriteStore.fetchFavoriteGyms(),
  ])
}

onMounted(() => {
  void fetchPageData()
})
</script>

<style scoped>
.gym-list-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.gyms-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.gyms-header h1 {
  margin: 0;
  font-size: clamp(2.2rem, 5vw, 4rem);
  line-height: 1;
  letter-spacing: -0.06em;
  font-weight: 850;
}

.gyms-header p {
  max-width: 680px;
  margin: 14px 0 0;
  color: #667085;
  font-size: 1.05rem;
  line-height: 1.6;
}

.search-card {
  margin-bottom: 32px;
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.08), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
}

.search-card .q-card__section {
  padding: 18px;
}

.search-input {
  max-width: 680px;
}

.gym-section {
  margin-top: 44px;
}

.gym-section:first-of-type {
  margin-top: 0;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 1.6rem;
  line-height: 1.2;
  letter-spacing: -0.04em;
  font-weight: 850;
}

.section-header p {
  margin: 6px 0 0;
  color: #667085;
  line-height: 1.5;
}

.state {
  max-width: 520px;
  margin: 64px auto;
  text-align: center;
}

.state--loading {
  padding: 48px 24px;
}

.state--error {
  border-radius: 24px;
  padding: 20px;
}

.empty-section-card {
  max-width: 560px;
  margin: 0 auto;
  border-radius: 24px;
  padding: 20px;
  text-align: center;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.06), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
}

@media (max-width: 700px) {
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-input {
    max-width: none;
  }
}
</style>
