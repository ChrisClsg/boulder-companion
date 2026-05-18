<template>
  <q-list>
    <q-item-label header>Navigation</q-item-label>

    <AppDrawerLink
      v-for="link in linksList"
      :key="link.title"
      v-bind="link"
    />
  </q-list>
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
