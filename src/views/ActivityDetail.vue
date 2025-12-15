<template>
  <div class="page activity-detail">
    <section class="card hero">
      <div>
        <p class="eyebrow">热点活动</p>
        <h1>{{ activity.title }}</h1>
        <p class="meta">{{ activity.time }} · 参与 {{ activity.participantCount || 0 }}</p>
        <p class="desc">{{ activity.desc }}</p>
      </div>
      <div class="actions">
        <button class="ghost-btn" @click="$router.push('/')">返回首页</button>
        <button class="primary-btn" @click="notify">立即报名</button>
      </div>
    </section>

    <section class="card body-card">
      <h3>活动介绍</h3>
      <p class="body-text">{{ activity.body }}</p>
      <ul class="highlights">
        <li v-for="point in activity.highlights" :key="point">{{ point }}</li>
      </ul>
    </section>
  </div>
</template>

<script>
import { hotActivities } from '@/mock/activity';

export default {
  name: 'ActivityDetail',
  computed: {
    activity() {
      const id = this.$route.params.id;
      return hotActivities[id] || hotActivities.default;
    }
  },
  methods: {
    notify() {
      this.$root.$refs.toast?.show('报名通道占位，后端接入后可用', 'info');
    }
  }
};
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

h1 {
  margin: 4px 0 6px;
}

.meta {
  margin: 0 0 6px;
  color: var(--gray-700);
}

.desc {
  margin: 0;
  color: var(--gray-800);
}

.actions {
  display: flex;
  gap: 10px;
}

.body-card h3 {
  margin: 0 0 10px;
}

.body-text {
  margin: 0 0 8px;
  line-height: 1.6;
  color: var(--gray-800);
}

.highlights {
  margin: 0;
  padding-left: 18px;
  color: var(--gray-800);
  line-height: 1.6;
}

.ghost-btn,
.primary-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 10px 14px;
  cursor: pointer;
  font-weight: 600;
}

.primary-btn {
  background: var(--blue);
  border-color: var(--blue);
  color: #fff;
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .actions button {
    flex: 1;
  }
}
</style>
