<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4">Edit Gym</div>
      <q-form @submit="updateGym" class="q-gap-md">
        <q-input
          v-model="form.name"
          label="Name"
          outlined
          :rules="[(v) => !!v || 'Name is required']"
        />
        <q-input
          v-model="form.address"
          label="Address"
          outlined
          :rules="[(v) => !!v || 'Address is required']"
        />
        <q-input
          v-model="form.description"
          label="Description"
          outlined
        />
        <q-input
          v-model="form.phone"
          label="Phone"
          outlined
        />
        <q-input
          v-model="form.website"
          label="Website"
          outlined
        />
        <q-input
          v-model="form.openingHours.monday"
          label="Monday"
          outlined
        />
        <q-input
          v-model="form.openingHours.tuesday"
          label="Tuesday"
          outlined
        />
        <q-input
          v-model="form.openingHours.wednesday"
          label="Wednesday"
          outlined
        />
        <q-input
          v-model="form.openingHours.thursday"
          label="Thursday"
          outlined
        />
        <q-input
          v-model="form.openingHours.friday"
          label="Friday"
          outlined
        />
        <q-input
          v-model="form.openingHours.saturday"
          label="Saturday"
          outlined
        />
        <q-input
          v-model="form.openingHours.sunday"
          label="Sunday"
          outlined
        />
        <div class="row q-gap-sm">
          <q-btn label="Save" type="submit" color="primary" />
          <q-btn label="Cancel" color="grey" @click="$router.push(`/gyms/${gym?.id}`)" />
        </div>
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { gymApi } from 'src/api'
import type { Gym } from 'src/types'

const route = useRoute()
const router = useRouter()
const $q = useQuasar()

const gym = ref<Gym | null>(null)
const form = ref({
  name: '',
  address: '',
  description: '',
  phone: '',
  website: '',
  openingHours: {
    monday: '',
    tuesday: '',
    wednesday: '',
    thursday: '',
    friday: '',
    saturday: '',
    sunday: '',
  },
})

onMounted(async () => {
  if (gym.value) return
  try {
    const data = await gymApi.getById(route.params.id as string)
    gym.value = data
    form.value = {
      name: data.name,
      address: data.address,
      description: data.description,
      phone: data.phone,
      website: data.website,
      openingHours: {
        monday: data.openingHours.monday,
        tuesday: data.openingHours.tuesday,
        wednesday: data.openingHours.wednesday,
        thursday: data.openingHours.thursday,
        friday: data.openingHours.friday,
        saturday: data.openingHours.saturday,
        sunday: data.openingHours.sunday,
      },
    }
  } catch (err: unknown) {
    $q.notify({ message: (err as Error).message || 'Failed to fetch gym', type: 'negative' })
  }
})

const updateGym = async () => {
  try {
    await gymApi.update(route.params.id as string, {
      ...form.value,
      adminId: gym.value?.adminId || '',
    })
    $q.notify({ message: 'Gym updated successfully', type: 'positive' })
    await router.push(`/gyms/${String(route.params.id)}`)
  } catch (err: unknown) {
    $q.notify({ message: (err as { message?: string }).message || 'Failed to update gym', type: 'negative' })
  }
}
</script>
