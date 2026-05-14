<template>
  <q-card class="gym-card">
    <q-card-section>
      <div class="text-h6 text-primary">{{ gym.name }}</div>
      <div class="text-grey-7 text-body1">{{ gym.address }}</div>
    </q-card-section>

    <q-card-section v-if="gym.phone">
      <q-btn
        label="Call"
        color="primary"
        flat
        @click="$q.notify({ message: `Dial ${gym.phone}`, type: 'info' })"
      />
      <q-btn
        label="Website"
        color="primary"
        flat
        @click="openURL(gym.website)"
      />
    </q-card-section>

    <q-card-section>
      <div class="text-caption text-grey-8">
        <span v-for="(hours, day) in gym.openingHours" :key="day">
          {{ day }}: {{ hours }}
          <br v-if="day !== Object.keys(gym.openingHours).slice(0, -1).pop()" />
        </span>
      </div>
    </q-card-section>

    <q-card-section v-if="authStore.isAuthenticated">
      <q-btn
        :label="isFavorite ? 'Remove from favorites' : 'Add to favorites'"
        color="primary"
        flat
        @click="toggleFavorite"
      />
    </q-card-section>

    <q-card-section>
      <q-btn
        label="View Details"
        color="primary"
        :to="`/gyms/${gym.id}`"
      />
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import { openURL, useQuasar } from 'quasar'
import { useAuthStore } from 'stores/authStore'
import { useFavoriteStore } from 'stores/favoriteStore'
import { favoriteApi } from 'boot/axios'
import type { Gym } from 'src/types'
import { computed } from 'vue'
import { getErrorMessage } from 'src/utils/errors'

const props = defineProps<{ gym: Gym }>()
const $q = useQuasar()
const authStore = useAuthStore()
const favoriteStore = useFavoriteStore()

const isFavorite = computed(() => {
  return favoriteStore.gyms.some((g: Gym) => g.id === props.gym.id)
})

const toggleFavorite = async () => {
  try {
    if (isFavorite.value) {
      await favoriteApi.remove(props.gym.id)
    } else {
      await favoriteApi.add(props.gym.id)
    }
    await fetchGyms()
  } catch (error: unknown) {
    $q.notify({ message: getErrorMessage(error, 'Failed to update favorites'), type: 'negative' })
  }
}

const fetchGyms = async () => {
  await favoriteStore.fetchGyms()
}
</script>

<style scoped>
.gym-card {
  min-width: 280px;
  max-width: 320px;
}
</style>
