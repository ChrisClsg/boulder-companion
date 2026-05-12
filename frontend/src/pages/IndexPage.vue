<template>
  <q-page class="row flex flex-no-wrap">
    <div class="col-12 q-pa-md">
      <div class="row items-center q-mb-md">
        <div class="text-h4">Boulder Companion</div>
        <q-btn
          v-if="!authStore.isAuthenticated"
          label="Login"
          color="primary"
          @click="login"
        />
        <q-btn
          v-else
          label="Logout"
          color="negative"
          @click="logout"
        />
      </div>

      <div v-if="loading" class="text-center">
        <q-spinner-dots color="primary" />
      </div>

      <div v-else-if="error" class="text-center text-negative">
        {{ error }}
      </div>

      <div v-else-if="authStore.isAuthenticated && gyms.length > 0" class="row flex flex-wrap">
        <gym-card
          v-for="gym in gyms"
          :key="gym.id"
          :gym="gym"
        />
      </div>

      <div v-else-if="authStore.isAuthenticated && gyms.length === 0" class="text-center">
        <div class="text-h6">No gyms found</div>
        <q-btn
          label="Add Gym"
          color="primary"
          @click="$router.push('/gyms/new')"
        />
      </div>

      <div v-else class="text-center">
        <div class="text-h6">Please login to view gyms</div>
        <q-btn
          label="Login"
          color="primary"
          @click="login"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'stores/authStore'
import { gymApi, authApi } from 'boot/axios'
import type { Gym } from 'src/types'

const $q = useQuasar()
const authStore = useAuthStore()
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

const login = () => {
  const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
  authApi.login(`${host}/oauth2/authorization/github`)
}

const logout = () => {
  authStore.logout()
  window.location.href = '/logout'
}

onMounted(async () => {
  await fetchGyms()
})
</script>
