<template>
  <q-page padding>
    <div class="gym-list-page">
      <div class="row items-center justify-between q-mb-md">
        <div class="text-h4">Gyms</div>
      </div>

      <div v-if="gymStore.isLoading" class="text-center q-pa-md">
        <q-spinner-dots color="primary" size="40px" />
      </div>

      <div v-else-if="gymStore.error" class="text-center text-negative q-pa-md">
        {{ gymStore.error }}
      </div>

      <q-card
        v-else-if="gymStore.gyms.length === 0"
        flat
        bordered
        class="empty-gyms-card text-center"
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
        </q-card-section>
      </q-card>

      <card-grid v-else>
        <gym-card
          v-for="gym in gymStore.gyms"
          :key="gym.id"
          :gym="gym"
        />
      </card-grid>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useGymStore } from 'stores/gymStore'
import CardGrid from 'components/CardGrid.vue'
import GymCard from 'components/GymCard.vue'

const gymStore = useGymStore()

onMounted(() => {
  void gymStore.fetchGyms()
})
</script>

<style scoped>
.gym-list-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.empty-gyms-card {
  max-width: 520px;
  margin: 0 auto;
  border-radius: 18px;
  padding: 24px;
}
</style>
