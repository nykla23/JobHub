<template>
  <div class="page">
    <section class="card">
      <p class="eyebrow">审核中</p>
      <h1>内容正在审核，预计 1-2 个工作日内完成</h1>
      <p class="desc">通过后将自动发布；如未通过会在系统通知中说明原因。</p>
      <div class="actions">
        <router-link to="/" class="primary-btn">返回首页</router-link>
        <router-link to="/messages" class="ghost-btn">查看系统通知</router-link>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'PublishPending'
};
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

h1 {
  margin: 6px 0 8px;
  font-size: 20px;
}

.desc {
  margin: 0 0 12px;
  color: var(--gray-700);
}

.actions {
  display: flex;
  gap: 10px;
}

.primary-btn,
.ghost-btn {
  padding: 10px 12px;
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
}
</style>
