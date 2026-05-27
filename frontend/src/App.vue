<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from 'stores/authStore'

const authStore = useAuthStore()

onMounted(() => {
  void authStore.initAuth()
})
</script>
