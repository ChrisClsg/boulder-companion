<template>
  <q-card class="route-card">
    <q-card-section class="row items-center no-wrap">
      <div class="text-h6 text-primary flex-shrink">{{ route.name }}</div>
      <q-chip
        color="primary"
        text-color="white"
        class="ml-auto"
      >
        {{ route.difficulty.value }}
      </q-chip>
    </q-card-section>

    <q-card-section>
      <div class="text-body2 text-grey-7">{{ route.wall }}</div>
      <div class="text-caption text-grey-6">
        Hold color: {{ route.holdColor }} | Types: {{ route.holdTypes.join(', ') }}
      </div>
    </q-card-section>

    <q-card-section v-if="route.images && route.images.length > 0">
      <div class="row gutter-md">
        <div v-for="(img, index) in route.images" :key="index" class="col-6">
          <q-img
            :src="img.url"
            :caption="img.caption"
            style="max-width: 100px; height: 100px"
          />
        </div>
      </div>
    </q-card-section>

    <q-card-section>
      <q-btn
        label="View Details"
        color="primary"
        :to="`/routes/${route.id}`"
      />
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import type { Route } from 'src/types'

defineProps<{ route: Route }>()
</script>

<style scoped>
.route-card {
  min-width: 280px;
  max-width: 400px;
}
</style>
