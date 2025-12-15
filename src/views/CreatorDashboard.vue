<template>
  <div class="page">
    <section class="card">
      <p class="eyebrow">创作者后台（专家）</p>
      <h1>数据面板</h1>
      <div class="stats">
        <div class="stat">
          <div class="label">阅读/观看量</div>
          <div class="value">12,340</div>
        </div>
        <div class="stat">
          <div class="label">平均评分</div>
          <div class="value">4.8</div>
        </div>
        <div class="stat">
          <div class="label">评论</div>
          <div class="value">860</div>
        </div>
      </div>
    </section>

    <section class="card">
      <header class="section-header">
        <h3>内容管理</h3>
        <button class="primary-btn">发布新内容</button>
      </header>
      <table class="table">
        <thead>
          <tr>
            <th>标题</th>
            <th>类型</th>
            <th>状态</th>
            <th>评分</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in 4" :key="row">
            <td>内容标题 {{ row }}</td>
            <td>视频/文字/音频/PPT</td>
            <td>审核中/通过/拒绝</td>
            <td>4.7</td>
            <td><button class="ghost-btn">编辑/更新</button></td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script>
export default {
  name: 'CreatorDashboard'
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
  margin: 6px 0 10px;
  font-size: 20px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px;
}

.stat {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
}

.label {
  color: var(--gray-700);
  font-size: 13px;
}

.value {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  border: 1px solid var(--gray-200);
  padding: 10px;
  text-align: left;
  font-size: 13px;
}

.primary-btn {
  background: var(--blue);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}
</style>
