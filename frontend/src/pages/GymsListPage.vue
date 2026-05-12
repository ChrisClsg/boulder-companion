<template>
  <q-page padding>
    <div class="row items-center q-mb-md">
      <div class="text-h4">Gyms</div>
      <q-btn
        v-if="authStore.isAuthenticated"
        label="Add Gym"
        color="primary"
        @click="$router.push('/gyms/new')"
      />
    </div>

    <div v-if="loading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="error" class="text-center text-negative q-pa-md">
      {{ error }}
    </div>

    <div v-else-if="gyms.length === 0" class="text-center text-grey-6 q-pa-md">
      No gyms found. <q-btn label="Add Gym" color="primary" @click="$router.push('/gyms/new')" />
    </div>

    <div v-else class="row flex flex-wrap">
      <gym-card
        v-for="gym in gyms"
        :key="gym.id"
        :gym="gym"
      />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore, useGymStore } from '#stores'
import { gymApi } from '#boot'
import type { Gym } from '#types'

const authStore = useAuthStore()
const gymStore = useGymStore()
const gyms = ref<Gym[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const fetchGyms = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await gymApi.getAll()
    gyms.value = data
  } catch (err: any) {
    error.value = err.message || 'Failed to fetch gyms'
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

onMounted(fetchGyms)
</script>
