<template>
  <div class="page">
    <section class="card">
      <p class="eyebrow">信用档案</p>
      <h1>当前信用分：{{ credit.score }}（低于 60 限制发布）</h1>
      <p class="desc">违规一次扣 10-50 分，支持查看违规原因与处理结果。</p>
      <table class="table">
        <thead>
          <tr>
            <th>时间</th>
            <th>原因</th>
            <th>扣分</th>
            <th>处理结果</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in credit.logs" :key="row.date + row.reason">
            <td>{{ row.date }}</td>
            <td>{{ row.reason }}</td>
            <td :class="{ minus: row.delta < 0, plus: row.delta > 0 }">{{ row.delta }}</td>
            <td>{{ row.result }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script>
import { fetchCreditLogs } from '@/api/mockService';

export default {
  name: 'Credit',
  computed: {
    credit() {
      return this.$store.state.credit;
    }
  },
  async created() {
    const logs = await fetchCreditLogs();
    this.$store.commit('setCredit', { ...this.credit, logs });
  }
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
  margin: 6px 0 6px;
  font-size: 20px;
}

.desc {
  margin: 0 0 10px;
  color: var(--gray-700);
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

.minus {
  color: var(--red);
}

.plus {
  color: var(--blue);
}
</style>
