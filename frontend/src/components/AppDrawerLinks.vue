<template>
  <div class="drawer-inner">
    <div class="drawer-brand">
      <q-img
        alt="Boulder Companion Logo"
        class="drawer-brand__mark"
        src="~assets/boulder_companion_logo.png"
      />
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
  align-items: start;
  gap: 14px;
  padding: 1.5rem 20px 1.5rem;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.12), transparent 55%),
    linear-gradient(180deg, #f0f6ff, #f8fafc);
  border-bottom: 1px solid rgba(25, 118, 210, 0.1);
  height: 13rem;
}

.drawer-brand__mark {
  position: absolute;
  top: inherit;
  width: 9rem;
  height: 10rem;
}

.drawer-brand__text {
  display: flex;
  flex-direction: column;
  line-height: 1;
}

.drawer-brand__name {
  font-size: 1.05em;
  font-weight: 850;
  letter-spacing: -0.04em;
  color: #0f172a;
  position: absolute;
  top: 8rem;
  left: 9rem;
}

.drawer-brand__sub {
  position: absolute;
  top: 9rem;
  left: 7rem;
  margin-top: 3px;
  font-size: 1.5em;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #1976d2;
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
