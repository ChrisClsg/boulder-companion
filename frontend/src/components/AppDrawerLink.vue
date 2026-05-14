<template>
  <q-item
    clickable
    :to="to"
    exact
  >
    <q-item-section
      v-if="icon"
      avatar
    >
      <q-icon :name="icon" />
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ title }}</q-item-label>
      <q-item-label caption v-if="caption">{{ caption }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
export interface AppDrawerLinkProps {
  title: string
  to: string
  icon?: string
  caption?: string
}

withDefaults(defineProps<AppDrawerLinkProps>(), {
  caption: '',
  icon: '',
})
</script>
