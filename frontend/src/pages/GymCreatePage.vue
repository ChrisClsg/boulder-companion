<template>
  <q-page padding>
    <div class="q-pa-md">
      <div class="text-h4">Add Gym</div>
      <q-form @submit="createGym" class="q-gap-md">
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
          <q-btn label="Cancel" color="grey" @click="$router.push('/gyms')" />
        </div>
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { gymApi } from 'src/api'
import { useAuthStore } from 'src/stores/authStore'

const router = useRouter()
const $q = useQuasar()
const authStore = useAuthStore()

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
  await authStore.fetchUser()
})

const createGym = async () => {
  try {
    await gymApi.create({
      ...form.value,
      adminId: authStore.user?.id || '',
    })
    $q.notify({ message: 'Gym created successfully', type: 'positive' })
    await router.push('/gyms')
  } catch (err: unknown) {
    $q.notify({ message: (err as { message?: string }).message || 'Failed to create gym', type: 'negative' })
  }
}
</script>
