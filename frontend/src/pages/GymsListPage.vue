<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4">Gyms</div>

      <q-btn
        v-if="authStore.isAuthenticated"
        label="Add Gym"
        color="primary"
        @click="router.push('/gyms/new')"
      />
    </div>

    <div v-if="gymStore.isLoading" class="text-center q-pa-md">
      <q-spinner-dots color="primary" />
    </div>

    <div v-else-if="gymStore.error" class="text-center text-negative q-pa-md">
      {{ gymStore.error }}
    </div>

    <div v-else-if="gymStore.gyms.length === 0" class="text-center text-grey-6 q-pa-md">
      No gyms found.

      <q-btn
        v-if="authStore.isAuthenticated"
        label="Add Gym"
        color="primary"
        flat
        @click="router.push('/gyms/new')"
      />
    </div>

    <div v-else class="row q-col-gutter-md">
      <div
        v-for="gym in gymStore.gyms"
        :key="gym.id"
        class="col-12 col-sm-6 col-md-4 col-lg-3"
      >
        <gym-card :gym="gym" />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'stores/authStore'
import { useGymStore } from 'stores/gymStore'
import GymCard from 'components/GymCard.vue'

const router = useRouter()
const authStore = useAuthStore()
const gymStore = useGymStore()

onMounted(() => {
  void gymStore.fetchGyms()
})
</script>
