<template>
  <div class="page">
    <section class="card">
      <p class="eyebrow">隐私设置</p>
      <h1>管理个人信息展示与推送</h1>
      <div class="list">
        <label class="row" @click="toggle('hideCompany')">
          <span>隐藏具体公司</span>
          <button class="ghost-btn">{{ settings.hideCompany ? '已隐藏' : '显示' }}</button>
        </label>
        <label class="row" @click="toggle('hideSalary')">
          <span>隐藏薪资情况</span>
          <button class="ghost-btn">{{ settings.hideSalary ? '已隐藏' : '显示' }}</button>
        </label>
        <label class="row" @click="toggle('hideHistory')">
          <span>隐藏历史发布内容</span>
          <button class="ghost-btn">{{ settings.hideHistory ? '已隐藏' : '显示' }}</button>
        </label>
      </div>
    </section>

    <section class="card">
      <p class="eyebrow">动态推送</p>
      <h2>关注更新的推送频率与类型</h2>
      <div class="list">
        <label class="row">
          <span>推送频率</span>
          <div class="chips">
            <button class="chip" :class="{ active: settings.pushFrequency === '实时' }" @click="setPush('pushFrequency', '实时')">实时</button>
            <button class="chip" :class="{ active: settings.pushFrequency === '每日汇总' }" @click="setPush('pushFrequency', '每日汇总')">每日汇总</button>
          </div>
        </label>
        <label class="row">
          <span>推送类型</span>
          <div class="chips">
            <button class="chip" :class="{ active: settings.pushType === '仅发布' }" @click="setPush('pushType', '仅发布')">仅发布内容</button>
            <button class="chip" :class="{ active: settings.pushType === '仅评论' }" @click="setPush('pushType', '仅评论')">仅评论</button>
            <button class="chip" :class="{ active: settings.pushType === '全部' }" @click="setPush('pushType', '全部')">全部</button>
          </div>
        </label>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ProfileSettings',
  computed: {
    settings() {
      return this.$store.state.settings;
    }
  },
  methods: {
    toggle(key) {
      this.$store.dispatch('savePrivacySettings', { [key]: !this.settings[key] });
    },
    setPush(key, value) {
      this.$store.dispatch('savePrivacySettings', { [key]: value });
    }
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

h1, h2 {
  margin: 6px 0 10px;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
}

.row span {
  font-weight: 600;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.chips {
  display: flex;
  gap: 8px;
}

.chip {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.chip.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}
</style>
