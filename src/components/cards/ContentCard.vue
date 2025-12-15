<template>
  <article class="content-card" @click="$emit('select', item)">
    <div class="tag">{{ item.tag }}</div>
    <h3>{{ item.title }}</h3>
    <p>{{ item.desc }}</p>
    <div class="meta">
      <span>{{ item.author }}</span>
      <span>{{ item.stats }}</span>
    </div>
  </article>
</template>

<script>
export default {
  name: 'ContentCard',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>
.content-card {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #fff;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.content-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}

.tag {
  align-self: flex-start;
  padding: 4px 8px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  border-radius: 8px;
  font-size: 12px;
}

h3 {
  margin: 0;
  font-size: 16px;
}

p {
  margin: 0;
  color: var(--gray-700);
  font-size: 14px;
}

.meta {
  display: flex;
  justify-content: space-between;
  color: var(--gray-500);
  font-size: 12px;
}
</style>
