<template>
  <q-layout view="lHh Lpr lFf">
    <q-header class="app-header">
      <q-toolbar class="app-toolbar">
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
          :disable="!isAuthenticated"
          class="app-menu-btn"
        />

        <div
          class="app-brand"
          @click="$router.push('/')"
        >
          <q-img
            src="~assets/boulder_companion_logo.png"
            alt="Boulder Companion Logo"
            class="app-brand__mark"
          />
          <span
            class="app-brand__name"
            v-if="$q.screen.gt.xs"
          >
            Boulder<span class="app-brand__accent">Companion</span>
          </span>
        </div>

        <q-space />

        <template v-if="isAuthenticated">
          <q-btn
            flat
            dense
            round
            icon="account_circle"
            aria-label="Account"
            @click="goToProfile"
            class="app-action-btn"
          />
          <q-btn
            flat
            dense
            round
            icon="logout"
            aria-label="Logout"
            @click="handleLogout"
            class="app-action-btn"
          />
        </template>

        <q-btn
          v-else
          unelevated
          rounded
          color="primary"
          label="Sign in"
          icon="login"
          size="sm"
          @click="authStore.loginWithGithub"
          class="app-signin-btn"
        />
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      v-if="isAuthenticated"
      class="app-drawer"
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

<style scoped>
.app-header {
  background: #ffffff;
  color: #0f172a;
  box-shadow:
    0 1px 0 rgba(0, 0, 0, 0.07),
    0 4px 16px rgba(15, 23, 42, 0.04);
}

.app-toolbar {
  height: 64px;
  padding: 0 16px;
  gap: 4px;
}

.app-menu-btn {
  color: #64748b;
  margin-right: 4px;
}

.app-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
  text-decoration: none;
}

.app-brand__mark {
  width: 5rem;
  height: auto;
  top: 1rem;
  z-index: 10000;
}

.app-brand__name {
  font-size: 1.1rem;
  font-weight: 850;
  letter-spacing: -0.04em;
  line-height: 1;
  color: #0f172a;
  /* margin-left: 5rem; */
}

.app-brand__accent {
  color: #1976d2;
  margin-left: 4px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.app-action-btn {
  color: #64748b;
  transition: color 150ms ease, background 150ms ease;
}

.app-action-btn:hover {
  color: #1976d2;
}

.app-signin-btn {
  font-weight: 600;
  letter-spacing: -0.01em;
  padding: 0 16px;
}

.app-drawer {
  background: #ffffff;
}
</style>
