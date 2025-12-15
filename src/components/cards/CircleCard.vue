<template>
  <article class="card">
    <div class="card-head">
      <h3>{{ item.title }}</h3>
      <span class="badge">{{ item.badge }}</span>
    </div>
    <p class="desc">{{ item.desc }}</p>
    <div class="meta">{{ item.meta }}</div>
    <div class="actions">
      <button class="ghost-btn">加入小组</button>
      <router-link :to="`/circle/${item.id || 'demo'}`" class="link">进入详情</router-link>
    </div>
  </article>
</template>

<script>
export default {
  name: 'CircleCard',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--gray-200);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h3 {
  margin: 0;
  font-size: 16px;
}

.badge {
  padding: 4px 8px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  font-size: 12px;
}

.desc {
  margin: 0;
  color: var(--gray-700);
}

.meta {
  color: var(--gray-500);
  font-size: 13px;
}

.actions {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.link {
  color: var(--blue);
  font-weight: 600;
}
</style>
