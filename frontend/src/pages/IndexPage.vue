<template>
  <q-page padding>
    <div class="home-page">
      <div v-if="loading" class="text-center q-pa-md">
        <q-spinner-dots color="primary" size="40px" />
      </div>

      <div v-else-if="error" class="text-center text-negative q-pa-md">
        {{ error }}
      </div>

      <card-grid v-else-if="authStore.isAuthenticated && gyms.length > 0">
        <gym-card
          v-for="gym in gyms"
          :key="gym.id"
          :gym="gym"
        />
      </card-grid>

      <q-card
        v-else-if="authStore.isAuthenticated && gyms.length === 0"
        flat
        bordered
        class="empty-state-card text-center"
      >
        <q-card-section>
          <q-icon
            name="fitness_center"
            size="48px"
            color="grey-5"
          />

          <div class="text-h6 q-mt-sm">
            No gyms found
          </div>

          <div class="text-body2 text-grey-6">
            Add your first climbing gym.
          </div>

          <q-btn
            label="Add Gym"
            color="primary"
            unelevated
            rounded
            icon="add"
            class="q-mt-md"
            @click="$router.push('/gyms/new')"
          />
        </q-card-section>
      </q-card>

      <q-card
        v-else
        flat
        bordered
        class="empty-state-card text-center"
      >
        <q-card-section>
          <q-icon
            name="lock"
            size="48px"
            color="grey-5"
          />

          <div class="text-h6 q-mt-sm">
            Please login to view gyms
          </div>

          <div class="text-body2 text-grey-6">
            Your gym list is available after signing in.
          </div>

          <q-btn
            label="Login"
            color="primary"
            unelevated
            rounded
            class="q-mt-md"
            @click="authStore.loginWithGithub"
          />
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'src/stores/authStore'
import { gymApi } from 'src/api'
import CardGrid from 'src/components/CardGrid.vue'
import GymCard from 'src/components/GymCard.vue'
import type { Gym } from 'src/types'
import { getErrorMessage } from 'src/utils/errors'

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
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to fetch gyms')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await authStore.fetchUser()

  if (authStore.isAuthenticated) {
    await fetchGyms()
  }
})
</script>

<style scoped>
.home-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.home-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 32px;
}

.home-title {
  margin: 0;
  font-size: clamp(2rem, 5vw, 3.25rem);
  line-height: 1.05;
  font-weight: 700;
}

.empty-state-card {
  max-width: 520px;
  margin: 0 auto;
  border-radius: 18px;
  padding: 24px;
}

@media (max-width: 600px) {
  .home-header {
    flex-direction: column;
    align-items: stretch;
  }

  .home-header .q-btn {
    align-self: flex-start;
  }
}
</style>
