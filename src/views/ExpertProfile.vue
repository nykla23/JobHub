<template>
  <div class="page">
    <section class="card hero">
      <div>
        <p class="eyebrow">专家主页</p>
        <h1>专家姓名 · 认证标签</h1>
        <p class="meta">资质：职业规划师 · 擅长：互联网运营/求职策略 · 综合评分 4.9</p>
        <div class="tags">
          <span class="tag">职业规划</span>
          <span class="tag">心理疏导</span>
        </div>
      </div>
      <div class="actions">
        <button class="ghost-btn">私信咨询</button>
        <button class="primary-btn">关注</button>
      </div>
    </section>

    <section class="card">
      <header class="section-header">
        <h3>专家内容</h3>
        <button class="ghost-btn">专家答疑话题</button>
      </header>
      <div class="grid">
        <article v-for="item in 4" :key="item" class="content-card">
          <div class="badge">视频</div>
          <h4>内容标题 {{ item }}</h4>
          <p class="desc">摘要占位，支持文字/音频/视频/PPT 模板。</p>
          <div class="meta">评分 4.8 · 评论 68 · 收藏 420</div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ExpertProfile'
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

.hero {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.hero h1 {
  margin: 4px 0 6px;
}

.meta {
  margin: 0 0 8px;
  color: var(--gray-700);
}

.tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 6px 10px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  border-radius: 10px;
  font-size: 12px;
}

.actions {
  display: flex;
  gap: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.content-card {
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
  background: rgba(245, 158, 11, 0.12);
  color: var(--orange);
  border-radius: 8px;
  font-size: 12px;
}

.desc {
  margin: 0;
  color: var(--gray-700);
}

.meta {
  color: var(--gray-500);
  font-size: 13px;
}

.ghost-btn,
.primary-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 12px;
  padding: 10px 18px;
  min-width: 120px;
  height: 44px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
}

.primary-btn {
  background: var(--blue);
  border-color: var(--blue);
  color: #fff;
  box-shadow: 0 8px 18px rgba(37, 99, 235, 0.2);
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
