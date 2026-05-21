<template>
  <q-page padding>
    <div class="profile-page">
      <div class="page-header">
        <div class="text-overline text-primary">Profile</div>
        <h1>Your profile</h1>
        <p>Your climbing identity, personal stats, and a snapshot of your latest activity.</p>
      </div>

      <div v-if="loading" class="state state--loading">
        <q-spinner-dots color="primary" size="44px" />
        <div class="text-body2 text-grey-6 q-mt-sm">Loading profile...</div>
      </div>

      <q-card v-else-if="error" flat bordered class="state state--error">
        <q-card-section>
          <q-icon name="error_outline" color="negative" size="44px" />
          <div class="text-h6 q-mt-sm">Could not load profile</div>
          <div class="text-body2 text-grey-7 q-mt-xs">{{ error }}</div>
          <q-btn
            label="Try again"
            color="primary"
            unelevated
            rounded
            icon="refresh"
            class="q-mt-md"
            @click="fetchPageData"
          />
        </q-card-section>
      </q-card>

      <template v-else-if="user">
        <q-card flat bordered class="identity-card">
          <q-card-section>
            <div class="identity-layout">
              <q-avatar
                size="80px"
                :color="avatarFailed ? roleColor : undefined"
                :text-color="avatarFailed ? 'white' : undefined"
                class="user-avatar"
              >
                <img
                  v-if="!avatarFailed"
                  :src="avatarUrl"
                  :alt="user.name"
                  @error="avatarFailed = true"
                />
                <template v-else>{{ initials }}</template>
              </q-avatar>

              <div class="identity-info">
                <div class="identity-name">{{ user.name }}</div>
                <div class="identity-email text-grey-6">{{ user.email }}</div>
                <q-chip
                  :color="roleColor"
                  text-color="white"
                  :icon="roleIcon"
                  class="q-mt-xs role-chip"
                >
                  {{ formatRole(user.role) }}
                </q-chip>
              </div>
            </div>
          </q-card-section>
        </q-card>

        <section class="profile-section">
          <div class="section-header">
            <h2>Climbing stats</h2>
          </div>

          <div class="stats-grid">
            <q-card
              v-for="stat in stats"
              :key="stat.label"
              flat
              bordered
              class="stat-card"
              @click="stat.link && $router.push(stat.link)"
              :style="{ cursor: stat.link ? 'pointer' : 'default' }"
            >
              <q-card-section class="stat-card-inner">
                <q-icon :name="stat.icon" :color="stat.iconColor" size="28px" />
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label text-grey-6">{{ stat.label }}</div>
              </q-card-section>
            </q-card>
          </div>
        </section>

        <section class="profile-section">
          <div class="section-header">
            <div>
              <h2>Favorite gyms</h2>
              <p>Your saved climbing spots.</p>
            </div>
            <q-chip outline color="primary" icon="favorite">
              {{ favoriteStore.favoriteGyms.length }}
            </q-chip>
          </div>

          <q-card
            v-if="favoriteStore.favoriteGyms.length === 0"
            flat
            bordered
            class="empty-section-card"
          >
            <q-card-section>
              <q-icon name="favorite_border" size="42px" color="grey-5" />
              <div class="text-h6 q-mt-sm">No favorite gyms yet</div>
              <div class="text-body2 text-grey-6">
                Mark gyms as favorites to see them here.
              </div>
            </q-card-section>
          </q-card>

          <div v-else class="gym-chips">
            <q-btn
              v-for="gym in favoriteStore.favoriteGyms"
              :key="gym.id"
              :to="`/gyms/${gym.id}`"
              unelevated
              rounded
              no-caps
              color="primary"
              class="gym-chip-btn"
            >
              <q-icon name="fitness_center" size="16px" left />
              {{ gym.name }}
            </q-btn>
          </div>
        </section>

        <section class="profile-section">
          <div class="section-header">
            <div>
              <h2>Recent activity</h2>
              <p>Your latest logged climbs.</p>
            </div>
            <q-btn
              to="/climb-logs"
              flat
              rounded
              no-caps
              color="primary"
              icon-right="arrow_forward"
              label="View all"
            />
          </div>

          <q-card
            v-if="recentLogs.length === 0"
            flat
            bordered
            class="empty-section-card"
          >
            <q-card-section>
              <q-icon name="timeline" size="42px" color="grey-5" />
              <div class="text-h6 q-mt-sm">No climbs logged yet</div>
              <div class="text-body2 text-grey-6">
                Log your first climb from a route page.
              </div>
            </q-card-section>
          </q-card>

          <div v-else class="logs-list">
            <q-card
              v-for="log in recentLogs"
              :key="log.id"
              flat
              bordered
              class="log-card"
            >
              <q-card-section class="log-card-inner">
                <q-icon
                  :name="logIcon(log)"
                  :color="logColor(log)"
                  size="24px"
                  class="log-result-icon"
                />
                <div class="log-details">
                  <div class="text-body2 text-weight-medium">
                    {{ logLabel(log) }}
                  </div>
                  <div class="text-caption text-grey-6">
                    {{ log.attempts }}
                    {{ log.attempts === 1 ? 'attempt' : 'attempts' }} ·
                    {{ formatClimbedAt(log.climbedAt) }}
                  </div>
                </div>
                <q-chip
                  dense
                  :color="logColor(log)"
                  text-color="white"
                  class="log-chip"
                >
                  {{ logLabel(log) }}
                </q-chip>
              </q-card-section>
            </q-card>
          </div>
        </section>
      </template>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'stores/authStore'
import { useClimbLogStore } from 'stores/climbLogStore'
import { useFavoriteStore } from 'stores/favoriteStore'
import { getErrorMessage } from 'src/utils/errors'
import { logLabel, logColor, logIcon, formatClimbedAt } from 'src/utils/climbLog'

const $q = useQuasar()
const authStore = useAuthStore()
const climbLogStore = useClimbLogStore()
const favoriteStore = useFavoriteStore()

const loading = ref(false)
const error = ref<string | null>(null)
const avatarFailed = ref(false)

const user = computed(() => authStore.user)
const logs = computed(() => climbLogStore.logs)

const avatarUrl = computed(() =>
  user.value ? `https://avatars.githubusercontent.com/u/${user.value.githubId}` : '',
)

const initials = computed(() => {
  const name = user.value?.name ?? ''
  return name
    .split(' ')
    .map(n => n[0])
    .slice(0, 2)
    .join('')
    .toUpperCase() || '?'
})

const formatRole = (role: string): string => {
  const mapping: Record<string, string> = {
    CLIMBER: 'Climber',
    GYM_ADMIN: 'Gym Admin',
    ROUTE_SETTER: 'Route Setter',
  }
  return mapping[role] ?? role
}

const roleColor = computed(() => {
  const colors: Record<string, string> = {
    CLIMBER: 'primary',
    GYM_ADMIN: 'positive',
    ROUTE_SETTER: 'deep-orange',
  }
  return colors[user.value?.role ?? ''] ?? 'grey'
})

const roleIcon = computed(() => {
  const icons: Record<string, string> = {
    CLIMBER: 'person',
    GYM_ADMIN: 'admin_panel_settings',
    ROUTE_SETTER: 'brush',
  }
  return icons[user.value?.role ?? ''] ?? 'person'
})

const stats = computed(() => [
  {
    label: 'Total logs',
    value: logs.value.length,
    icon: 'format_list_numbered',
    iconColor: 'primary',
    link: logs.value.length > 0 ? '/climb-logs' : undefined,
  },
  {
    label: 'Total attempts',
    value: logs.value.reduce((sum, l) => sum + l.attempts, 0),
    icon: 'repeat',
    iconColor: 'orange',
  },
  {
    label: 'Topped attempts',
    value: logs.value.filter(l => l.topped).length,
    icon: 'check_circle',
    iconColor: 'positive',
    link: logs.value.some(l => l.topped) ? '/climb-logs?result=topped' : undefined,
  },
  {
    label: 'Flashed',
    value: logs.value.filter(l => l.flashed).length,
    icon: 'bolt',
    iconColor: 'purple',
    link: logs.value.some(l => l.flashed) ? '/climb-logs?result=flashed' : undefined,
  },
])

const recentLogs = computed(() =>
  [...logs.value]
    .sort((a, b) => new Date(b.climbedAt).getTime() - new Date(a.climbedAt).getTime())
    .slice(0, 5),
)

const fetchPageData = async () => {
  loading.value = true
  error.value = null
  avatarFailed.value = false

  try {
    await Promise.all([
      authStore.fetchUser(),
      climbLogStore.fetchMyLogs(),
      favoriteStore.fetchFavoriteGyms(),
    ])
  } catch (err: unknown) {
    error.value = getErrorMessage(err, 'Failed to load profile')
    $q.notify({ message: error.value, type: 'negative' })
  } finally {
    loading.value = false
  }
}

onMounted(fetchPageData)
</script>

<style scoped>
.profile-page {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: clamp(2.2rem, 5vw, 4rem);
  line-height: 1;
  letter-spacing: -0.06em;
  font-weight: 850;
}

.page-header p {
  max-width: 680px;
  margin: 14px 0 0;
  color: #667085;
  font-size: 1.05rem;
  line-height: 1.6;
}

/* Identity card */
.identity-card {
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.08), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
  margin-bottom: 44px;
}

.identity-layout {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  flex-shrink: 0;
  font-size: 1.6rem;
  font-weight: 700;
  border: 3px solid rgba(25, 118, 210, 0.18);
}

.identity-name {
  font-size: 1.4rem;
  font-weight: 750;
  letter-spacing: -0.03em;
  color: #0f172a;
  line-height: 1.2;
}

.identity-email {
  font-size: 0.9rem;
  margin-top: 2px;
}

.role-chip {
  font-size: 0.75rem;
}

/* Sections */
.profile-section {
  margin-top: 44px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 1.6rem;
  line-height: 1.2;
  letter-spacing: -0.04em;
  font-weight: 850;
}

.section-header p {
  margin: 6px 0 0;
  color: #667085;
  line-height: 1.5;
}

/* Stats grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-card {
  border-radius: 20px;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.06), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
}

.stat-card-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 6px;
  padding: 20px 16px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 850;
  letter-spacing: -0.05em;
  line-height: 1;
  color: #0f172a;
}

.stat-label {
  font-size: 0.8rem;
  line-height: 1.3;
}

/* Gym chips */
.gym-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.gym-chip-btn {
  font-size: 0.875rem;
}

/* Logs list */
.logs-list {
  display: grid;
  gap: 8px;
}

.log-card {
  border-radius: 16px;
}

.log-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
}

.log-result-icon {
  flex-shrink: 0;
}

.log-details {
  flex: 1;
  min-width: 0;
}

.log-chip {
  flex-shrink: 0;
  font-size: 0.75rem;
}

/* Empty / error / loading states */
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

.empty-section-card {
  max-width: 560px;
  margin: 0 auto;
  border-radius: 24px;
  padding: 20px;
  text-align: center;
  background:
    radial-gradient(circle at top right, rgba(25, 118, 210, 0.06), transparent 34%),
    linear-gradient(180deg, #ffffff, #fafbfc);
}

/* Responsive */
@media (max-width: 700px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .identity-layout {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
