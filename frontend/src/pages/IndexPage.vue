<template>
  <q-page padding>
    <div class="home-page">
      <div v-if="loading" class="state state--loading">
        <q-spinner-dots color="primary" size="44px" />
        <div class="text-body2 text-grey-6 q-mt-sm">
          Loading your climbing world...
        </div>
      </div>

      <q-card
        v-else-if="error"
        flat
        bordered
        class="state state--error"
      >
        <q-card-section>
          <q-icon
            name="error_outline"
            size="44px"
            color="negative"
          />

          <div class="text-h6 q-mt-sm">
            Something went wrong
          </div>

          <div class="text-body2 text-grey-7 q-mt-xs">
            {{ error }}
          </div>

          <q-btn
            label="Try again"
            color="primary"
            unelevated
            rounded
            icon="refresh"
            class="q-mt-md"
            @click="initialize"
          />
        </q-card-section>
      </q-card>

      <dashboard-home v-else-if="authStore.isAuthenticated" />

      <public-home
        v-else
        @login="authStore.loginWithGithub"
      />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'src/stores/authStore'
import { getErrorMessage } from 'src/utils/errors'
import PublicHome from 'src/components/home/PublicHome.vue'
import DashboardHome from 'src/components/home/DashboardHome.vue'

const $q = useQuasar()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref<string | null>(null)

const initialize = async () => {
  loading.value = true
  error.value = null

  try {
    await authStore.fetchUser()
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to load home page')

    $q.notify({
      message: error.value,
      type: 'negative',
    })
  } finally {
    loading.value = false
  }
}

onMounted(initialize)
</script>

<style scoped>
.home-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.state {
  max-width: 520px;
  margin: 64px auto;
  text-align: center;
}

.state--loading {
  padding: 48px 24px;
}

.state--error {
  border-radius: 24px;
  padding: 20px;
}
</style>
