<template>
  <q-card class="route-card">
    <q-carousel
      v-if="route.images?.length"
      v-model="activeSlide"
      animated
      swipeable
      :arrows="route.images.length > 1"
      infinite
      class="route-carousel"
      control-color="white"
    >
      <q-carousel-slide
        v-for="(img, index) in route.images"
        :key="index"
        :name="index"
        :img-src="img.url"
        class="route-slide"
      >
        <div class="absolute-top route-image-overlay" />

        <q-chip
          color="primary"
          text-color="white"
          class="absolute-top-right q-ma-sm"
        >
          {{ route.difficulty.value }}
        </q-chip>

        <q-chip
          v-if="route.images.length > 1"
          dense
          color="black"
          text-color="white"
          class="absolute-bottom-right q-ma-sm route-image-count"
        >
          {{ activeSlide + 1 }} / {{ route.images.length }}
        </q-chip>

        <div class="absolute-bottom route-carousel-caption">
          <div class="text-h6 text-white">
            {{ route.name }}
          </div>

          <div class="text-caption text-grey-3">
            {{ route.wall }}
          </div>
        </div>
      </q-carousel-slide>
    </q-carousel>

    <q-card-section
      v-else
      class="route-placeholder"
    >
      <q-chip
        color="primary"
        text-color="white"
      >
        {{ route.difficulty.value }}
      </q-chip>

      <div class="text-h6 text-primary q-mt-sm">
        {{ route.name }}
      </div>

      <div class="text-body2 text-grey-7">
        {{ route.wall }}
      </div>
    </q-card-section>

    <q-card-section class="q-pt-sm">
      <div class="row items-center q-gutter-sm">
        <q-chip
          dense
          outline
          color="primary"
        >
          {{ route.holdColor }}
        </q-chip>

        <q-chip
          v-for="type in route.holdTypes"
          :key="type"
          dense
          color="grey-2"
          text-color="grey-8"
        >
          {{ type }}
        </q-chip>
      </div>
    </q-card-section>

    <q-card-actions
      align="right"
      class="q-pa-md q-pt-none"
    >
      <q-btn
        label="View Details"
        color="primary"
        unelevated
        rounded
        :to="`/routes/${route.id}`"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Route } from 'src/types'

defineProps<{ route: Route }>()

const activeSlide = ref(0)
</script>

<style scoped>
.route-card {
  width: 100%;
  min-width: 280px;
  max-width: 360px;
  border-radius: 18px;
  overflow: hidden;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;
}

.route-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.14);
}

.route-carousel {
  aspect-ratio: 4 / 5;
  height: auto;
}

.route-slide {
  background-size: cover;
  background-position: center;
}

.route-carousel-caption {
  padding: 32px 72px 14px 16px;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.72),
    rgba(0, 0, 0, 0)
  );
}

.route-image-overlay {
  height: 80px;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.32),
    rgba(0, 0, 0, 0)
  );
}

.route-image-count {
  background: rgba(0, 0, 0, 0.62) !important;
}

.route-placeholder {
  aspect-ratio: 4 / 5;
  min-height: 280px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  background: linear-gradient(135deg, #f5f7fb, #e8eef8);
}
</style>
