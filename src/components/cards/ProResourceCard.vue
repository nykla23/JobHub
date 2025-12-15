<template>
  <article class="resource-card">
    <div class="badge">{{ item.badge }}</div>
    <h4>{{ item.title }}</h4>
    <p class="desc">{{ item.desc }}</p>
    <div class="meta">{{ item.meta }}</div>
  </article>
</template>

<script>
export default {
  name: 'ProResourceCard',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>
.resource-card {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.badge {
  align-self: flex-start;
  padding: 4px 8px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  border-radius: 8px;
  font-size: 12px;
}

h4 {
  margin: 0;
}

.desc {
  margin: 0;
  color: var(--gray-700);
}

.meta {
  color: var(--gray-500);
  font-size: 13px;
}
</style>
