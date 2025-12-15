<template>
  <div class="empty">
    <div class="icon">🗂️</div>
    <div class="title">{{ title }}</div>
    <div class="desc" v-if="desc">{{ desc }}</div>
    <slot />
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    title: {
      type: String,
      default: '暂无内容'
    },
    desc: {
      type: String,
      default: ''
    }
  }
};
</script>

<style scoped>
.empty {
  border: 1px dashed var(--gray-200);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  background: var(--gray-50);
}

.icon {
  font-size: 22px;
}

.title {
  margin-top: 6px;
  font-weight: 700;
}

.desc {
  color: var(--gray-600);
  font-size: 13px;
  margin-top: 4px;
}
</style>
