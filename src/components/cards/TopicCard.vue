<template>
  <article class="topic-card">
    <div class="card-header">
      <span class="badge">{{ levelLabel }}</span>
      <div class="tag-list">
        <span v-for="tag in item.tags || []" :key="tag" class="tag-pill">{{ tag }}</span>
      </div>
    </div>
    <h3>{{ item.title }}</h3>
    <p class="intro">{{ item.intro || defaultIntro }}</p>
    <div class="stats">
      <span>参与 {{ item.participantCount || 0 }}</span>
      <span>互动 {{ item.interactionCount || 0 }}</span>
      <span v-if="latestReplyText">最新 {{ latestReplyText }}</span>
    </div>
    <div class="actions">
      <div class="left-actions">
        <router-link :to="`/topic/${item.id || 'demo'}`" class="link">查看详情</router-link>
      </div>
      <div class="counts">热度 {{ item.hotValue || item.interactionCount || '--' }}</div>
    </div>
  </article>
</template>

<script>
export default {
  name: 'TopicCard',
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  computed: {
    levelLabel() {
      return this.item.level || this.item.rating || 'A';
    },
    defaultIntro() {
      const tags = (this.item.tags || []).join(' · ');
      return tags || '精选话题';
    },
    latestReplyText() {
      if (!this.item.latestReplyTime) return '';
      return this.item.latestReplyTime.replace('T', ' ').slice(0, 16);
    }
  }
};
</script>

<style scoped>
.topic-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid var(--gray-200);
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: center;
}

.badge {
  min-width: 44px;
  text-align: center;
  padding: 4px 0;
  background: rgba(245, 158, 11, 0.12);
  color: var(--orange);
  border-radius: 8px;
  font-weight: 700;
  font-size: 12px;
}

.tag-list {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.tag-pill {
  background: var(--gray-50);
  color: var(--gray-700);
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
}

h3 {
  margin: 0;
  font-size: 18px;
}

.intro {
  margin: 0;
  color: var(--gray-700);
  font-size: 14px;
}

.stats {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: var(--gray-600);
  font-size: 13px;
}

.actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.left-actions {
  display: flex;
  gap: 8px;
}

.link {
  color: var(--blue);
  font-weight: 600;
}

.counts {
  color: var(--gray-500);
  font-size: 12px;
}
</style>
