<template>
  <div class="skeleton" :style="{ height }">
    <div class="shine"></div>
  </div>
</template>

<script>
export default {
  name: 'SkeletonBlock',
  props: {
    height: {
      type: String,
      default: '120px'
    }
  }
};
</script>

<style scoped>
.skeleton {
  position: relative;
  overflow: hidden;
  background: linear-gradient(90deg, #f2f4f7 25%, #e5e7eb 37%, #f2f4f7 63%);
  background-size: 400% 100%;
  border-radius: 12px;
  animation: shimmer 1.4s ease infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>
