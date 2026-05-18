<template>
  <section class="dashboard-home">
    <div class="dashboard-hero">
      <div>
        <q-badge
          color="primary"
          outline
          rounded
          class="dashboard-badge"
        >
          Dashboard
        </q-badge>

        <h1>Your climbing overview</h1>

        <p>
          Jump back into your favorite gyms, review your latest sessions,
          and keep your climbing momentum visible.
        </p>
      </div>

      <div class="quick-actions">
        <q-btn
          label="Log climb"
          color="primary"
          unelevated
          rounded
          icon="add"
        />

        <q-btn
          label="View gyms"
          flat
          rounded
          icon="fitness_center"
        />
      </div>
    </div>

    <div class="summary-grid">
      <q-card flat bordered class="summary-card">
        <q-card-section>
          <div class="summary-icon">
            <q-icon name="favorite" size="26px" />
          </div>

          <div>
            <span>Favorite gyms</span>
            <strong>{{ favoriteStore.favoriteGyms.length }}</strong>
          </div>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="summary-card">
        <q-card-section>
          <div class="summary-icon">
            <q-icon name="route" size="26px" />
          </div>

          <div>
            <span>Recent routes</span>
            <strong>0</strong>
          </div>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="summary-card">
        <q-card-section>
          <div class="summary-icon">
            <q-icon name="calendar_month" size="26px" />
          </div>

          <div>
            <span>Sessions this month</span>
            <strong>0</strong>
          </div>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="summary-card">
        <q-card-section>
          <div class="summary-icon">
            <q-icon name="trending_up" size="26px" />
          </div>

          <div>
            <span>Current streak</span>
            <strong>0d</strong>
          </div>
        </q-card-section>
      </q-card>
    </div>

    <div v-if="favoriteStore.isLoadingFavoriteGyms" class="state state--loading">
      <q-spinner-dots color="primary" size="40px" />
      <div class="text-body2 text-grey-6 q-mt-sm">
        Loading favorite gyms...
      </div>
    </div>

    <q-card
      v-else-if="favoriteStore.error"
      flat
      bordered
      class="dashboard-card error-card"
    >
      <q-card-section>
        <q-icon name="error_outline" color="negative" size="36px" />

        <div class="text-h6 q-mt-sm">
          Could not load dashboard
        </div>

        <p>
          {{ favoriteStore.error }}
        </p>

        <q-btn
          label="Try again"
          color="primary"
          unelevated
          rounded
          icon="refresh"
          @click="loadDashboard"
        />
      </q-card-section>
    </q-card>

    <div v-else class="dashboard-grid">
      <q-card flat bordered class="dashboard-card dashboard-card--large">
        <q-card-section>
          <div class="card-header">
            <div>
              <div class="text-overline text-primary">
                Favorites
              </div>

              <h2>Favorite gyms</h2>
            </div>

            <q-btn
              flat
              round
              dense
              icon="arrow_forward"
            />
          </div>

          <card-grid v-if="favoriteStore.favoriteGyms.length > 0" class="q-mt-md">
            <gym-card
              v-for="gym in favoriteStore.favoriteGyms"
              :key="gym.id"
              :gym="gym"
            />
          </card-grid>

          <div v-else class="empty-panel">
            <q-icon name="favorite_border" size="44px" color="grey-5" />

            <div class="text-h6 q-mt-sm">
              No favorite gyms yet
            </div>

            <p>
              Mark gyms as favorites and they will appear here for quick access.
            </p>
          </div>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="dashboard-card">
        <q-card-section>
          <div class="card-header">
            <div>
              <div class="text-overline text-primary">
                Activity
              </div>

              <h2>Recently climbed</h2>
            </div>
          </div>

          <div class="empty-panel empty-panel--compact">
            <q-icon name="route" size="40px" color="grey-5" />

            <div class="text-subtitle1 q-mt-sm">
              No routes logged yet
            </div>

            <p>
              Once you start logging climbs, your recent routes will show up here.
            </p>
          </div>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="dashboard-card">
        <q-card-section>
          <div class="card-header">
            <div>
              <div class="text-overline text-primary">
                Progress
              </div>

              <h2>This month</h2>
            </div>
          </div>

          <div class="progress-placeholder">
            <div class="progress-ring">
              <span>0</span>
            </div>

            <div>
              <strong>Routes completed</strong>

              <p>
                Your monthly climbing progress will be summarized here.
              </p>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import CardGrid from 'src/components/CardGrid.vue'
import GymCard from 'src/components/GymCard.vue'
import { useFavoriteStore } from 'src/stores/favoriteStore'

const favoriteStore = useFavoriteStore()

const loadDashboard = async () => {
  await favoriteStore.fetchFavoriteGyms()
}

onMounted(loadDashboard)
</script>

<style scoped>
.dashboard-home {
  padding: 24px 0 56px;
}

.dashboard-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 32px;
  padding: 34px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 34px;
  background:
    radial-gradient(circle at top left, rgba(25, 118, 210, 0.18), transparent 34%),
    radial-gradient(circle at bottom right, rgba(76, 175, 80, 0.14), transparent 30%),
    linear-gradient(135deg, #ffffff, #f7f9fc);
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.08);
}

.dashboard-badge {
  padding: 8px 14px;
  font-weight: 700;
}

.dashboard-hero h1 {
  max-width: 760px;
  margin: 18px 0 0;
  font-size: clamp(2.3rem, 5vw, 4.5rem);
  line-height: 0.98;
  letter-spacing: -0.06em;
  font-weight: 850;
}

.dashboard-hero p {
  max-width: 660px;
  margin: 20px 0 0;
  color: #667085;
  font-size: 1.08rem;
  line-height: 1.65;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 12px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 22px;
}

.summary-card {
  border-radius: 26px;
  background: #ffffff;
  transition:
    transform 180ms ease,
    box-shadow 180ms ease,
    border-color 180ms ease;
}

.summary-card:hover {
  transform: translateY(-3px);
  border-color: rgba(25, 118, 210, 0.28);
  box-shadow: 0 16px 42px rgba(15, 23, 42, 0.08);
}

.summary-card .q-card__section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px;
}

.summary-icon {
  width: 54px;
  height: 54px;
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  border-radius: 18px;
  color: var(--q-primary);
  background: rgba(25, 118, 210, 0.1);
}

.summary-card span {
  display: block;
  color: #667085;
  font-size: 0.9rem;
}

.summary-card strong {
  display: block;
  margin-top: 2px;
  font-size: 1.65rem;
  line-height: 1;
  font-weight: 850;
  letter-spacing: -0.04em;
}

.state {
  max-width: 520px;
  margin: 48px auto 0;
  text-align: center;
}

.state--loading {
  padding: 48px 24px;
}

.error-card {
  max-width: 560px;
  margin: 32px auto 0;
  text-align: center;
}

.error-card p {
  color: #667085;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.65fr);
  gap: 18px;
  margin-top: 22px;
}

.dashboard-card {
  border-radius: 30px;
  background:
    linear-gradient(180deg, #ffffff, #fafbfc);
}

.dashboard-card--large {
  grid-row: span 2;
}

.dashboard-card .q-card__section {
  padding: 28px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
}

.card-header h2 {
  margin: 0;
  font-size: 1.45rem;
  font-weight: 850;
  letter-spacing: -0.04em;
}

.empty-panel {
  display: grid;
  place-items: center;
  min-height: 320px;
  padding: 32px;
  text-align: center;
  border-radius: 24px;
  background:
    linear-gradient(135deg, rgba(25, 118, 210, 0.06), rgba(0, 0, 0, 0.025));
}

.empty-panel--compact {
  min-height: 220px;
}

.empty-panel p {
  max-width: 340px;
  margin: 8px auto 0;
  color: #667085;
  line-height: 1.6;
}

.progress-placeholder {
  display: flex;
  align-items: center;
  gap: 22px;
  min-height: 220px;
}

.progress-ring {
  width: 118px;
  height: 118px;
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  border-radius: 50%;
  background:
    radial-gradient(circle at center, #ffffff 54%, transparent 55%),
    conic-gradient(var(--q-primary) 0deg, rgba(25, 118, 210, 0.12) 0deg);
  box-shadow: inset 0 0 0 1px rgba(25, 118, 210, 0.08);
}

.progress-ring span {
  font-size: 2rem;
  font-weight: 850;
  letter-spacing: -0.05em;
}

.progress-placeholder strong {
  display: block;
  font-size: 1.05rem;
}

.progress-placeholder p {
  margin: 6px 0 0;
  color: #667085;
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .dashboard-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .quick-actions {
    justify-content: flex-start;
  }

  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-card--large {
    grid-row: auto;
  }
}

@media (max-width: 640px) {
  .dashboard-home {
    padding-top: 8px;
  }

  .dashboard-hero {
    padding: 26px;
    border-radius: 28px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-card .q-card__section {
    padding: 22px;
  }

  .progress-placeholder {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
