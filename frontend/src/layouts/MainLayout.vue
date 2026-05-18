<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
          :disable="!isAuthenticated"
        />

        <q-toolbar-title>Boulder Companion</q-toolbar-title>

        <q-btn
          flat
          dense
          round
          icon="account_circle"
          aria-label="Account"
          @click="goToProfile"
          v-if="isAuthenticated"
        />

        <q-btn
          flat
          dense
          round
          icon="logout"
          aria-label="Logout"
          @click="handleLogout"
          v-if="isAuthenticated"
        />

        <q-btn
          flat
          dense
          round
          icon="login"
          aria-label="Login"
          @click="authStore.loginWithGithub"
          v-if="!isAuthenticated"
        />
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      v-if="isAuthenticated"
    >
      <AppDrawerLinks />
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'src/stores/authStore'
import AppDrawerLinks from 'components/AppDrawerLinks.vue'

const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = ref(authStore.isAuthenticated)

// Watch auth store changes
authStore.$subscribe((mutation, state) => {
  isAuthenticated.value = state.isAuthenticated
})

const leftDrawerOpen = ref(false)

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function goToProfile() {
  void router.push('/profile')
  leftDrawerOpen.value = false
}

async function handleLogout() {
  await authStore.logout()
  await router.push('/')
}
</script>
