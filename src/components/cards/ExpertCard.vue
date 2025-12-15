<template>
  <article class="expert-card">
    <div class="name">{{ item.name }}</div>
    <p class="desc">{{ item.desc }}</p>
    <div class="tags">
      <span v-for="tag in item.tags" :key="tag" class="tag">{{ tag }}</span>
    </div>
    <div class="actions">
      <router-link :to="`/pro/expert/${item.id || 'demo'}`" class="link">进入主页</router-link>
      <button class="ghost-btn">私信咨询</button>
    </div>
  </article>
</template>

<script>
export default {
  name: 'ExpertCard',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>
.expert-card {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.name {
  font-weight: 700;
}

.desc {
  margin: 0;
  color: var(--gray-700);
}

.tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 8px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  border-radius: 8px;
  font-size: 12px;
}

.actions {
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
