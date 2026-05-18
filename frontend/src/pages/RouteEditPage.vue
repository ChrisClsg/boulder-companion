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
import { routeApi } from 'src/api'
import type { Route, Image } from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

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
  archivedAt: '',
  images: [] as Image[],
  createdAt: '',
  updatedAt: '',
})

onMounted(async () => {
  if (routeData.value) return
  try {
    const response = await routeApi.getById(route.params.id as string)
    const fetchedRoute = response
    routeData.value = fetchedRoute
    form.value = {
      gymId: fetchedRoute.gymId,
      name: fetchedRoute.name,
      difficulty: {
        value: fetchedRoute.difficulty.value,
        scale: fetchedRoute.difficulty.scale,
      },
      holdColor: fetchedRoute.holdColor,
      holdTypes: fetchedRoute.holdTypes || [],
      setterId: fetchedRoute.setterId,
      wall: fetchedRoute.wall,
      archived: fetchedRoute.archived,
      archivedAt: fetchedRoute.archivedAt,
      images: fetchedRoute.images || [],
      createdAt: fetchedRoute.createdAt,
      updatedAt: fetchedRoute.updatedAt,
    }
  } catch (err: unknown) {
    $q.notify({ message: (err as { message?: string }).message || 'Failed to fetch route', type: 'negative' })
  }
})

const updateRoute = async () => {
  try {
    if (!routeData.value) return
    const updatedRoute: Route = {
      ...routeData.value,
      gymId: form.value.gymId,
      name: form.value.name,
      difficulty: {
        value: form.value.difficulty.value,
        scale: form.value.difficulty.scale as 'v' | 'font' | 'custom',
      },
      holdColor: form.value.holdColor,
      holdTypes: form.value.holdTypes,
      setterId: form.value.setterId,
      wall: form.value.wall,
      archived: form.value.archived,
      archivedAt: form.value.archivedAt,
      images: form.value.images,
      createdAt: form.value.createdAt,
      updatedAt: form.value.updatedAt,
    }
    await routeApi.update(route.params.id as string, updatedRoute)
    $q.notify({ message: 'Route updated successfully', type: 'positive' })
    await router.push(`/routes/${String(route.params.id)}`)
  } catch (err: unknown) {
    $q.notify({ message: (err as { message?: string }).message || 'Failed to update route', type: 'negative' })
  }
}
</script>
