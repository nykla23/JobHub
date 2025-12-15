<template>
  <div class="loading">
    <div class="spinner"></div>
    <div class="text">{{ text }}</div>
  </div>
</template>

<script>
export default {
  name: 'LoadingState',
  props: {
    text: {
      type: String,
      default: '加载中...'
    }
  }
};
</script>

<style scoped>
.loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--gray-700);
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid var(--gray-300);
  border-top-color: var(--blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
