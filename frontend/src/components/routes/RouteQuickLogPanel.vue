<template>
  <div class="quick-log-panel">
    <div>
      <div class="text-subtitle2 q-mb-sm">
        Result
      </div>

      <q-btn-toggle
        v-model="result"
        spread
        unelevated
        rounded
        toggle-color="primary"
        color="grey-4"
        text-color="grey-9"
        :options="resultOptions"
      />
    </div>

    <div class="attempt-control q-mt-md">
      <div>
        <div class="text-subtitle2">
          Attempts
        </div>

        <div class="text-caption text-grey-6">
          Number of tries for this log.
        </div>
      </div>

      <div class="attempt-stepper">
        <q-btn
          dense
          round
          flat
          icon="remove"
          :disable="attempts <= 1"
          @click.stop="attempts--"
        />

        <div class="attempt-number">
          {{ attempts }}
        </div>

        <q-btn
          dense
          round
          flat
          icon="add"
          @click.stop="attempts++"
        />
      </div>
    </div>

    <q-btn
      label="Save new log"
      color="primary"
      unelevated
      rounded
      class="full-width q-mt-md"
      :loading="saving"
      :disable="!canSave"
      @click.stop="save"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type {
  ClimbLog,
} from 'src/types'

const props = defineProps<{
  routeId: string
  gymId: string
  lastLog?: ClimbLog | null
  saving?: boolean
}>()

const emit = defineEmits<{
  save: [{
    log: {
      routeId: string
      gymId: string
      sessionId: string | null
      attempts: number
      topped: boolean
      climbedAt: string
    }
  }]
}>()

const result = ref<'attempted' | 'topped'>('topped')
const attempts = ref(1)

const resultOptions = [
  {
    label: 'Tried',
    value: 'attempted',
  },
  {
    label: 'Topped',
    value: 'topped',
  },
]

const canSave = computed(() =>
  attempts.value >= 1
)

const save = () => {
  if (!canSave.value) {
    return
  }

  const log = {
    routeId: props.routeId,
    gymId: props.gymId,
    sessionId: null,
    attempts: attempts.value,
    topped: result.value === 'topped',
    climbedAt: new Date().toISOString(),
  }

  emit('save', {
    log,
  })
}
</script>

<style scoped>
.quick-log-panel {
  padding: 1rem;
}

.quick-log-title-row,
.attempt-control {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.attempt-stepper {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px;
  border-radius: 999px;
  background: white;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.04);
}

.attempt-number {
  min-width: 34px;
  text-align: center;
  font-size: 1.15rem;
  font-weight: 850;
}
</style>
