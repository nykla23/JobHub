<template>
  <div class="page not-found">
    <div class="card">
      <div class="code">404</div>
      <h1>页面走丢了</h1>
      <p class="desc">你访问的页面不存在，或已被移动。</p>
      <div class="actions">
        <router-link to="/" class="primary-btn">返回首页</router-link>
        <button class="ghost-btn" @click="$router.back()">返回上一页</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound'
};
</script>

<style scoped>
.page {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.08);
  text-align: center;
  max-width: 420px;
}

.code {
  font-size: 42px;
  font-weight: 800;
  color: var(--blue);
}

h1 {
  margin: 8px 0 6px;
}

.desc {
  color: var(--gray-700);
  margin: 0 0 14px;
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.primary-btn,
.ghost-btn {
  padding: 10px 14px;
  border-radius: 10px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.primary-btn {
  background: var(--blue);
  color: #fff;
  border: 1px solid var(--blue);
}

.ghost-btn {
  background: #fff;
  color: var(--gray-700);
  border: 1px solid var(--gray-200);
  cursor: pointer;
}
</style>
