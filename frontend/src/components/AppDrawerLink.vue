<template>
  <q-item
    clickable
    :to="to"
    exact
    class="nav-link"
  >
    <q-item-section
      v-if="icon"
      avatar
      class="nav-link__icon-wrap"
    >
      <q-icon :name="icon" size="20px" />
    </q-item-section>

    <q-item-section>
      <q-item-label class="nav-link__label">{{ title }}</q-item-label>
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

<style scoped>
.nav-link {
  border-radius: 14px;
  min-height: 46px;
  margin-bottom: 2px;
  padding: 0 14px;
  color: #475569;
  transition:
    background 150ms ease,
    color 150ms ease;
}

.nav-link:hover {
  background: rgba(25, 118, 210, 0.06);
  color: #1976d2;
}

.nav-link.q-router-link--active,
.nav-link.q-router-link--exact-active {
  background: rgba(25, 118, 210, 0.1);
  color: #1976d2;
  font-weight: 600;
}

.nav-link__icon-wrap {
  min-width: 36px;
}

.nav-link__label {
  font-size: 0.93rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}
</style>
