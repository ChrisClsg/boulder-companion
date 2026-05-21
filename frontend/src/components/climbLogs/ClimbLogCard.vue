<template>
  <q-card
    flat
    bordered
    class="log-card"
    :class="{ 'log-card--clickable': clickable }"
    @click="clickable ? $router.push(`/routes/${log.routeId}`) : undefined"
  >
    <q-card-section>
      <div class="log-card-content">
        <div class="log-main">
          <div :style="fixedChipWidth ? ('width: ' + (typeof fixedChipWidth === 'string' ? fixedChipWidth : '7rem')) : '7rem'">
            <q-chip
              :dense="denseChip"
              :color="logColor(log)"
              text-color="white"
              :icon="logIcon(log)"
            >
              {{ logLabel(log) }}
            </q-chip>
          </div>

          <div>
            <slot />
          </div>
        </div>

        <q-btn
          v-if="!hideDelete"
          flat
          round
          dense
          color="negative"
          icon="delete_outline"
          :loading="deleting"
          @click.stop="confirmDelete"
        />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import { useQuasar } from 'quasar'
import { logLabel, logColor, logIcon } from 'src/utils/climbLog'
import type { ClimbLog } from 'src/types'

const props = withDefaults(defineProps<{
  log: ClimbLog
  clickable?: boolean
  denseChip?: boolean
  fixedChipWidth?: boolean | string
  deleting?: boolean
  hideDelete?: boolean
}>(), {
  clickable: false,
  denseChip: false,
  fixedChipWidth: true,
  deleting: false,
  hideDelete: false,
})

const emit = defineEmits<{
  (e: 'delete', log: ClimbLog): void
}>()

const $q = useQuasar()

const confirmDelete = () => {
  $q.dialog({
    title: 'Delete log?',
    message: 'This climb log will be permanently removed.',
    ok: { color: 'negative', label: 'Delete', unelevated: true },
    cancel: { flat: true, label: 'Cancel' },
    persistent: true,
  }).onOk(() => {
    emit('delete', props.log)
  })
}
</script>

<style scoped>
.log-card {
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #fafbfc);
}

.log-card--clickable:hover {
  cursor: pointer;
}

.log-card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.log-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

@media (max-width: 600px) {
  .log-card-content,
  .log-main {
    align-items: flex-start;
  }

  .log-main {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
