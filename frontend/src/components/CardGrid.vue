<template>
  <div
    class="card-grid"
    :class="[`card-grid--${variant}`]"
  >
    <slot />
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    variant?: 'default' | 'compact'
  }>(),
  {
    variant: 'default',
  },
)
</script>

<style scoped>
.card-grid {
  display: grid;
  gap: 24px;
  align-items: start;
}

.card-grid--default {
  grid-template-columns: repeat(auto-fill, minmax(280px, 360px));
  justify-content: center;
}

.card-grid--compact {
  grid-template-columns: repeat(auto-fill, minmax(280px, 360px));
}

@media (max-width: 600px) {
  .card-grid {
    gap: 16px;
  }

  .card-grid--default {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
