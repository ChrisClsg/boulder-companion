<template>
  <q-card class="gym-card">
    <div class="gym-card-hero">
      <div class="gym-card-gradient" />

      <q-btn
        v-if="authStore.isAuthenticated"
        round
        flat
        dense
        class="favorite-btn"
        :icon="isFavorite ? 'favorite' : 'favorite_border'"
        :color="isFavorite ? 'red' : 'white'"
        :loading="favoriteLoading"
        :aria-label="isFavorite ? 'Remove from favorites' : 'Add to favorites'"
        @click.stop.prevent="toggleFavorite"
      />

      <div class="gym-card-hero-content">
        <div class="text-overline text-grey-3">
          Climbing Gym
        </div>

        <div class="gym-name">
          {{ gym.name }}
        </div>
      </div>
    </div>

    <q-card-section class="q-gutter-md">
      <div
        v-if="gym.address"
        class="info-row"
      >
        <q-icon
          name="place"
          color="primary"
          size="20px"
        />

        <div class="text-body2 text-grey-8">
          {{ gym.address }}
        </div>
      </div>

      <div
        v-if="todayOpeningHours"
        class="info-row"
      >
        <q-icon
          name="schedule"
          color="primary"
          size="20px"
        />

        <div>
          <div class="text-caption text-grey-6">
            Today
          </div>

          <div class="text-body2 text-grey-8">
            {{ todayOpeningHours }}
          </div>
        </div>
      </div>

      <div
        v-else
        class="info-row"
      >
        <q-icon
          name="schedule"
          color="primary"
          size="20px"
        />

        <div class="text-body2 text-grey-8">
          No opening hours available
        </div>
      </div>

      <div
        v-if="gym.phone || gym.website"
        class="quick-actions"
      >
        <q-btn
          v-if="gym.phone"
          dense
          flat
          rounded
          icon="call"
          label="Call"
          color="primary"
          @click.stop="callGym"
        />

        <q-btn
          v-if="gym.website"
          dense
          flat
          rounded
          icon="language"
          label="Website"
          color="primary"
          @click.stop="openWebsite"
        />
      </div>
    </q-card-section>

    <q-separator />

    <q-card-actions class="q-pa-md">
      <q-btn
        label="View Details"
        color="primary"
        unelevated
        rounded
        class="full-width"
        :to="{ name: 'GymDetail', params: { id: gym.id } }"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { openURL, useQuasar } from 'quasar'
import { useAuthStore } from 'src/stores/authStore'
import { useFavoriteStore } from 'src/stores/favoriteStore'
import { favoriteApi } from 'src/api'
import { getErrorMessage } from 'src/utils/errors'
import type { Gym, OpeningHours } from 'src/types'

type Weekday = keyof OpeningHours

const props = defineProps<{
  gym: Gym
}>()

const $q = useQuasar()
const authStore = useAuthStore()
const favoriteStore = useFavoriteStore()

const favoriteLoading = ref(false)

const isFavorite = computed(() => {
  return favoriteStore.favoriteGyms.some((gym: Gym) => gym.id === props.gym.id)
})

const todayOpeningHours = computed(() => {
  if (!props.gym.openingHours) {
    return null
  }

  const today = new Intl.DateTimeFormat('en-US', {
    weekday: 'long',
  }).format(new Date()).toLowerCase() as Weekday

  return props.gym.openingHours[today] || null
})

const toggleFavorite = async () => {
  if (favoriteLoading.value) {
    return
  }

  favoriteLoading.value = true

  try {
    if (isFavorite.value) {
      await favoriteApi.removeFavoriteGym(props.gym.id)
    } else {
      await favoriteApi.addFavoriteGym(props.gym.id)
    }

    await favoriteStore.fetchFavoriteGyms()
  } catch (error: unknown) {
    $q.notify({
      message: getErrorMessage(error, 'Failed to update favorites'),
      type: 'negative',
    })
  } finally {
    favoriteLoading.value = false
  }
}

const callGym = () => {
  if (!props.gym.phone) {
    return
  }

  openURL(`tel:${props.gym.phone}`)
}

const openWebsite = () => {
  if (!props.gym.website) {
    return
  }

  openURL(props.gym.website)
}
</script>

<style scoped>
.gym-card {
  width: 100%;
  min-width: 280px;
  max-width: 360px;
  border-radius: 18px;
  overflow: hidden;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;
}

.gym-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.14);
}

.gym-card-hero {
  position: relative;
  min-height: 160px;
  display: flex;
  align-items: flex-end;
  padding: 18px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.35), transparent 34%),
    linear-gradient(135deg, #263238, #111827);
}

.gym-card-gradient {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(
      to top,
      rgba(0, 0, 0, 0.56),
      rgba(0, 0, 0, 0.08)
    );
}

.gym-card-hero-content {
  position: relative;
  z-index: 1;
  min-width: 0;
}

.gym-name {
  color: white;
  font-size: 1.35rem;
  line-height: 1.15;
  font-weight: 700;
  word-break: break-word;
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  background: rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(8px);
}

.favorite-btn:hover {
  background: rgba(0, 0, 0, 0.42);
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  min-width: 0;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 4px;
}

@media (max-width: 600px) {
  .gym-card {
    max-width: none;
  }

  .gym-card-hero {
    min-height: 140px;
  }
}
</style>
