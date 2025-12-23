<template>
  <article class="card">
    <div class="card-head">
      <h3>{{ item.title }}</h3>
      <span class="badge">{{ item.badge }}</span>
    </div>
    <p class="desc">{{ item.desc }}</p>
    <div class="meta">{{ item.meta }}</div>
    
    <!-- 将按钮容器修改为固定在左下角 -->
    <div class="actions">
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
  /* 设置为相对定位，作为按钮的定位参考 */
  position: relative;
  /* 为按钮预留底部空间 */
  padding-bottom: 50px;
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
  /* 添加一些灵活性，让描述可以扩展 */
  flex: 1;
}

.meta {
  color: var(--gray-500);
  font-size: 13px;
}

.actions {
  /* 修改为绝对定位，固定在左下角 */
  position: absolute;
  bottom: 14px;  /* 与卡片内边距一致 */
  left: 14px;    /* 与卡片内边距一致 */
  right: 14px;   /* 可选的，如果需要也可以设置右边距 */
  margin-top: 0; /* 移除原来的上边距 */
  display: flex;
  justify-content: flex-start; /* 左对齐 */
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
  /* 可选：添加一些样式让链接更像按钮 */
  padding: 8px 0;
  display: inline-block;
}
</style>