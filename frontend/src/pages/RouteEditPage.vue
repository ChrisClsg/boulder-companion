<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4">Edit Route</div>
      <q-form @submit="updateRoute" class="q-gap-md">
        <q-input
          v-model="form.gymId"
          label="Gym ID"
          outlined
          :disabled="true"
        />
        <q-input
          v-model="form.name"
          label="Name"
          outlined
          :rules="[(v) => !!v || 'Name is required']"
        />
        <q-select
          v-model="form.difficulty.value"
          :options="['v1', 'v2', 'v3', 'v4', 'v5', 'v6', 'v7', 'v8', 'v9', 'v10']"
          label="Difficulty"
          outlined
        />
        <q-select
          v-model="form.difficulty.scale"
          :options="['v', 'font', 'custom']"
          label="Difficulty Scale"
          outlined
        />
        <q-input
          v-model="form.holdColor"
          label="Hold Color"
          outlined
        />
        <q-select
          v-model="form.holdTypes"
          multiple
          :options="['crimp', 'jug', 'crank', 'sloper', 'arete', 'volcanic', 'juggernaut']"
          label="Hold Types"
          outlined
        />
        <q-input
          v-model="form.setterId"
          label="Setter ID"
          outlined
        />
        <q-input
          v-model="form.wall"
          label="Wall"
          outlined
          :rules="[(v) => !!v || 'Wall is required']"
        />
        <div class="row q-gap-sm">
          <q-btn label="Save" type="submit" color="primary" />
          <q-btn label="Cancel" color="grey" @click="$router.push(`/routes/${route.params.id}`)" />
        </div>
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { routeApi } from '#boot'
import { useRouteStore } from '#stores'
import type { Route } from '#types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const routeStore = useRouteStore()

const routeData = ref<Route | null>(null)
const form = ref({
  gymId: '',
  name: '',
  difficulty: {
    value: '',
    scale: 'v',
  },
  holdColor: '',
  holdTypes: [] as string[],
  setterId: '',
  wall: '',
  archived: false,
  archivedAt: null,
  images: [],
  createdAt: null,
  updatedAt: null,
})

onMounted(async () => {
  if (routeData.value) return
  try {
    const data = await routeApi.getById(route.params.id as string)
    routeData.value = data
    form.value = {
      gymId: data.gymId,
      name: data.name,
      difficulty: {
        value: data.difficulty.value,
        scale: data.difficulty.scale,
      },
      holdColor: data.holdColor,
      holdTypes: data.holdTypes || [],
      setterId: data.setterId,
      wall: data.wall,
      archived: data.archived,
      archivedAt: data.archivedAt,
      images: data.images || [],
      createdAt: data.createdAt,
      updatedAt: data.updatedAt,
    }
  } catch (err: any) {
    $q.notify({ message: err.message || 'Failed to fetch route', type: 'negative' })
  }
})

const updateRoute = async () => {
  try {
    await routeApi.update(route.params.id as string, {
      ...form.value,
      holderTypes: form.value.holdTypes,
    })
    $q.notify({ message: 'Route updated successfully', type: 'positive' })
    router.push(`/routes/${route.params.id}`)
  } catch (err: any) {
    $q.notify({ message: err.message || 'Failed to update route', type: 'negative' })
  }
}
</script>
