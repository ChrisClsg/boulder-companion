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

    <q-card-section class="q-pt-sm q-pb-sm">
      <div class="row items-center q-gutter-sm">
        <q-chip
          outline
          color="primary"
        >
          {{ route.holdColor }}
        </q-chip>

        <q-chip
          v-for="type in route.holdTypes"
          :key="type"
          color="grey-3"
          text-color="grey-8"
        >
          {{ type }}
        </q-chip>
      </div>
    </q-card-section>

    <q-card-section class="personal-section">
      <div class="personal-summary">
        <div class="summary-main">
          <q-chip
            :color="summaryColor"
            text-color="white"
            :icon="summaryIcon"
          >
            {{ summaryLabel }}
          </q-chip>

          <span
            v-if="personalSummary.totalAttempts > 0"
            class="text-caption text-grey-6"
          >
            Logs: {{ personalSummary.totalAttempts }}
          <div
            v-if="personalSummary.lastLog"
            class="text-caption text-grey-6"
          >
            Latest: {{ formatDate(personalSummary.lastLog.climbedAt) }}
          </div>
          </span>
        </div>
      </div>

      <q-slide-transition>
        <route-actions-panel
          v-if="quickLogOpen"
          :route-id="route.id"
          :gym-id="route.gymId"
          :open-feedback="false"
          :open-log="true"
          class="q-mt-lg"
        />
      </q-slide-transition>
    </q-card-section>

    <q-card-actions
      align="between"
      class="q-pa-md q-pt-none"
    >
      <q-btn
        color="primary"
        outline
        padding="xs sm"
        rounded
        @click.stop="quickLogOpen = !quickLogOpen"
        >
        <q-icon
          :name="quickLogOpen ? 'expand_less' : 'add'"
          size="xs"
        />
        <span>{{ quickLogOpen ? 'Close' : 'Add Log' }}</span>
      </q-btn>

      <q-btn
        label="Details"
        color="primary"
        padding="xs md"
        unelevated
        rounded
        :to="`/routes/${route.id}`"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import RouteActionsPanel from 'src/components/routes/RouteActionsPanel.vue'
import { useRouteActions } from 'src/composables/useRouteActions'
import type { Route } from 'src/types'

const props = defineProps<{
  route: Route
}>()

const activeSlide = ref(0)
const quickLogOpen = ref(false)

const { personalSummary } =
  useRouteActions(() => props.route.id)

const summaryLabel = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'Untouched'
  }

  if (personalSummary.value.flashed) {
    return 'Flashed'
  }

  if (personalSummary.value.topped) {
    return 'Topped'
  }

  return 'Project'
})

const summaryColor = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'blue-grey-4'
  }

  if (personalSummary.value.flashed) {
    return 'purple'
  }

  if (personalSummary.value.topped) {
    return 'positive'
  }

  return 'orange'
})

const summaryIcon = computed(() => {
  if (personalSummary.value.totalLogs === 0) {
    return 'radio_button_unchecked'
  }

  if (personalSummary.value.flashed) {
    return 'bolt'
  }

  if (personalSummary.value.topped) {
    return 'check_circle'
  }

  return 'hourglass_bottom'
})

const formatDate = (value: string): string => {
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))
}
</script>

<style scoped>
.route-card {
  width: 100%;
  min-width: 280px;
  max-width: 380px;
  border-radius: 22px;
  overflow: hidden;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;
}

.route-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.14);
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
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.16), transparent 34%),
    linear-gradient(135deg, #f5f7fb, #e8eef8);
}

.personal-section {
  padding-top: 0;
}

.personal-summary {
  padding: 14px;
  border-radius: 20px;
  background: #f8fafc;
}

.summary-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  height: 2rem;
}

.feedback-mini {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667085;
  font-size: 0.84rem;
}
</style>
