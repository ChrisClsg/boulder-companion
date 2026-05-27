<template>
  <section class="public-home">
    <div class="hero">
      <div class="hero-content">
        <q-badge
          color="primary"
          outline
          rounded
          class="hero-badge"
        >
          Built for climbers
        </q-badge>

        <h1>
          Track your climbs.
          Remember your routes.
          See your progress.
        </h1>

        <p class="hero-text">
          A simple climbing companion for saving favorite gyms, logging sessions,
          tracking routes, and building a history of your climbing journey.
        </p>

        <div class="hero-actions">
          <q-btn
            label="Sign up with GitHub"
            color="primary"
            unelevated
            rounded
            size="lg"
            icon="login"
            @click="$emit('login')"
          />

          <q-btn
            label="Explore features"
            flat
            rounded
            size="lg"
            icon-right="arrow_downward"
            href="#features"
          />
        </div>

        <div class="hero-stats">
          <div>
            <strong>Gyms</strong>
            <span>Save your favorites</span>
          </div>

          <div>
            <strong>Routes</strong>
            <span>Track what you climbed</span>
          </div>

          <div>
            <strong>Progress</strong>
            <span>Watch yourself improve</span>
          </div>
        </div>
      </div>

      <div class="hero-preview">
        <q-card flat bordered class="preview-card preview-card--main">
          <q-card-section>
            <div class="preview-header">
              <div>
                <div class="text-overline text-primary">
                  Today
                </div>

                <div class="text-h6">
                  Evening session
                </div>
              </div>

              <q-icon
                name="fitness_center"
                color="primary"
                size="32px"
              />
            </div>

            <div class="route-list">
              <div class="route-item">
                <div>
                  <strong>Blue Arete</strong>
                  <span>Boulder · V4</span>
                </div>

                <q-badge color="positive" rounded>
                  Sent
                </q-badge>
              </div>

              <div class="route-item">
                <div>
                  <strong>Moon Slab</strong>
                  <span>Boulder · V3</span>
                </div>

                <q-badge color="orange" rounded>
                  Project
                </q-badge>
              </div>

              <div class="route-item">
                <div>
                  <strong>Red Corner</strong>
                  <span>Lead · 6b+</span>
                </div>

                <q-badge color="positive" rounded>
                  Sent
                </q-badge>
              </div>
            </div>
          </q-card-section>
        </q-card>

        <q-card flat bordered class="preview-card preview-card--floating">
          <q-card-section>
            <q-icon name="trending_up" color="positive" size="28px" />

            <div>
              <strong>+18%</strong>
              <span>more routes this month</span>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <div id="features" class="features">
      <q-card flat bordered class="feature-card">
        <q-card-section>
          <div class="feature-icon">
            <q-icon name="place" size="30px" />
          </div>

          <h2>Favorite gyms</h2>

          <p>
            Keep your regular climbing spots organized and easy to access.
          </p>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="feature-card">
        <q-card-section>
          <div class="feature-icon">
            <q-icon name="route" size="30px" />
          </div>

          <h2>Route history</h2>

          <p>
            Log climbs, remember grades, and see what you have already sent.
          </p>
        </q-card-section>
      </q-card>

      <q-card flat bordered class="feature-card">
        <q-card-section>
          <div class="feature-icon">
            <q-icon name="insights" size="30px" />
          </div>

          <h2>Progress overview</h2>

          <p>
            Turn your sessions into a clear picture of your climbing growth.
          </p>
        </q-card-section>
      </q-card>
    </div>
  </section>
</template>

<script setup lang="ts">
defineEmits<{
  login: []
}>()
</script>

<style scoped>
.public-home {
  padding: 24px 0 56px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(340px, 0.95fr);
  gap: 56px;
  align-items: center;
  min-height: calc(100vh - 180px);
}

.hero-content {
  max-width: 680px;
}

.hero-badge {
  padding: 8px 14px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.hero h1 {
  margin: 24px 0 0;
  font-size: clamp(2.7rem, 7vw, 5.6rem);
  line-height: 0.95;
  letter-spacing: -0.07em;
  font-weight: 850;
}

.hero-text {
  max-width: 620px;
  margin: 28px 0 0;
  color: #667085;
  font-size: clamp(1.05rem, 2vw, 1.25rem);
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 36px;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 42px;
}

.hero-stats div {
  padding: 18px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.72));
  backdrop-filter: blur(10px);
}

.hero-stats strong {
  display: block;
  font-size: 1rem;
  font-weight: 800;
}

.hero-stats span {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 0.9rem;
}

.hero-preview {
  position: relative;
  min-height: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-preview::before {
  content: '';
  position: absolute;
  inset: 44px 10px 20px;
  border-radius: 42px;
  background:
    radial-gradient(circle at 30% 20%, rgba(25, 118, 210, 0.24), transparent 34%),
    radial-gradient(circle at 70% 70%, rgba(76, 175, 80, 0.2), transparent 36%),
    linear-gradient(135deg, rgba(25, 118, 210, 0.12), rgba(0, 0, 0, 0.03));
  filter: blur(2px);
}

.preview-card {
  position: relative;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 30px 80px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(16px);
}

.preview-card--main {
  width: min(100%, 430px);
  padding: 10px;
}

.preview-card--floating {
  position: absolute;
  right: 6px;
  bottom: 54px;
  width: 230px;
}

.preview-card--floating .q-card__section {
  display: flex;
  align-items: center;
  gap: 14px;
}

.preview-card--floating strong,
.preview-card--floating span {
  display: block;
}

.preview-card--floating strong {
  font-size: 1.4rem;
  line-height: 1;
}

.preview-card--floating span {
  margin-top: 4px;
  color: #667085;
  font-size: 0.88rem;
}

.preview-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 22px;
}

.route-list {
  display: grid;
  gap: 12px;
}

.route-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 16px;
  border-radius: 22px;
  background: #f7f8fa;
}

.route-item strong,
.route-item span {
  display: block;
}

.route-item span {
  margin-top: 3px;
  color: #667085;
  font-size: 0.88rem;
}

.features {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin-top: 24px;
}

.feature-card {
  border-radius: 28px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(250, 251, 252, 1));
  transition:
    transform 180ms ease,
    box-shadow 180ms ease,
    border-color 180ms ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  border-color: rgba(25, 118, 210, 0.32);
  box-shadow: 0 18px 48px rgba(15, 23, 42, 0.08);
}

.feature-card .q-card__section {
  padding: 28px;
}

.feature-icon {
  width: 56px;
  height: 56px;
  display: grid;
  place-items: center;
  border-radius: 18px;
  color: var(--q-primary);
  background: rgba(25, 118, 210, 0.1);
}

.feature-card h2 {
  margin: 22px 0 8px;
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.feature-card p {
  margin: 0;
  color: #667085;
  line-height: 1.6;
}

@media (max-width: 1024px) {
  .hero {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .hero-content {
    max-width: none;
  }

  .hero-preview {
    min-height: 440px;
  }
}

@media (max-width: 720px) {
  .public-home {
    padding-top: 8px;
  }

  .hero {
    gap: 36px;
  }

  .hero-stats,
  .features {
    grid-template-columns: 1fr;
  }

  .hero-preview {
    min-height: auto;
    display: block;
  }

  .hero-preview::before {
    inset: 24px 0;
  }

  .preview-card--main {
    width: 100%;
  }

  .preview-card--floating {
    position: relative;
    right: auto;
    bottom: auto;
    width: 100%;
    margin-top: 14px;
  }
}
</style>
