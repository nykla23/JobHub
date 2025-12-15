<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">消息中心</p>
        <h1>互动通知 / 系统通知 / 圈子通知</h1>
      </div>
      <button class="ghost-btn">全部标为已读</button>
    </header>
    <div class="tabs">
      <button v-for="tab in tabs" :key="tab" :class="['tab', { active: activeTab === tab }]" @click="activeTab = tab">
        {{ tab }}
      </button>
    </div>
    <section class="card">
      <ul class="list">
        <li v-for="item in 5" :key="item">
          <div class="title">{{ activeTab }} 消息 {{ item }}</div>
          <div class="desc">未读红点 · hover 预览 · 点击进入对应场景。</div>
        </li>
      </ul>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Messages',
  data() {
    return {
      tabs: ['互动通知', '系统通知', '圈子通知'],
      activeTab: '互动通知'
    };
  }
};
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.page-header h1 {
  margin: 4px 0 0;
  font-size: 20px;
}

.tabs {
  display: flex;
  gap: 8px;
}

.tab {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.tab.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.list {
  margin: 0;
  padding-left: 18px;
  color: var(--gray-700);
}

.title {
  font-weight: 700;
}

.desc {
  margin: 2px 0 12px;
  color: var(--gray-500);
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}
</style>
