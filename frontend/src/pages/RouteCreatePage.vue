<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4">Add Route</div>
      <q-form @submit="createRoute" class="q-gap-md">
        <q-input
          v-model="form.gymId"
          label="Gym ID"
          outlined
          :rules="[(v) => !!v || 'Gym ID is required']"
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
          :rules="[(v) => !!v || 'Difficulty is required']"
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
          <q-btn label="Cancel" color="grey" @click="$router.push('/routes')" />
        </div>
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { routeApi } from 'boot/axios'
import type { Route } from 'src/types'

const router = useRouter()
const $q = useQuasar()

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

const createRoute = async () => {
  try {
    const newId = Date.now().toString(36) + Math.random().toString(36).substr(2, 9)
    const routeData: Omit<Route, 'id'> = {
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
      archived: false,
      archivedAt: '',
      images: [],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }
    await routeApi.create({ id: newId, ...routeData })
    $q.notify({ message: 'Route created successfully', type: 'positive' })
    await router.push('/routes')
  } catch (err: unknown) {
    $q.notify({ message: (err as { message?: string }).message || 'Failed to create route', type: 'negative' })
  }
}
</script>
