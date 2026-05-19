<template>
  <div class="drawer-inner">
    <div class="drawer-brand">
      <div class="drawer-brand__mark">
        <q-icon name="terrain" size="22px" />
      </div>
      <div class="drawer-brand__text">
        <span class="drawer-brand__name">Boulder</span>
        <span class="drawer-brand__sub">Companion</span>
      </div>
    </div>

    <div class="drawer-section-label">Navigation</div>

    <q-list class="drawer-nav">
      <AppDrawerLink
        v-for="link in linksList"
        :key="link.title"
        v-bind="link"
      />
    </q-list>
  </div>
</template>

<script setup lang="ts">
import AppDrawerLink, { type AppDrawerLinkProps } from 'components/AppDrawerLink.vue'

const linksList: AppDrawerLinkProps[] = [
  {
    title: 'Home',
    to: '/',
    icon: 'home',
  },
  {
    title: 'Gyms',
    to: '/gyms',
    icon: 'fitness_center',
  },
  {
    title: 'Routes',
    to: '/routes',
    icon: 'route',
  },
  {
    title: 'Climb Logs',
    to: '/climb-logs',
    icon: 'history',
  },
  {
    title: 'Profile',
    to: '/profile',
    icon: 'person',
  },
]
</script>

<style scoped>
.drawer-inner {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.drawer-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 22px 20px 20px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.12), transparent 55%),
    linear-gradient(180deg, #f0f6ff, #f8fafc);
  border-bottom: 1px solid rgba(25, 118, 210, 0.1);
}

.drawer-brand__mark {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  border-radius: 14px;
  background: rgba(25, 118, 210, 0.14);
  color: #1976d2;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.15);
}

.drawer-brand__text {
  display: flex;
  flex-direction: column;
  line-height: 1;
}

.drawer-brand__name {
  font-size: 1.05rem;
  font-weight: 850;
  letter-spacing: -0.04em;
  color: #0f172a;
}

.drawer-brand__sub {
  margin-top: 3px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.drawer-section-label {
  padding: 20px 24px 6px;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #94a3b8;
}

.drawer-nav {
  padding: 0 10px 16px;
  flex: 1;
}
</style>
