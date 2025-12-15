<template>
  <div class="page">
    <section class="card">
      <p class="eyebrow">专家入驻</p>
      <h1>提交职业资格证书、从业经历、个人简介</h1>
      <form class="form" @submit.prevent="submit">
        <label class="row">
          <span>职业资格证书</span>
          <div class="upload">上传附件占位</div>
        </label>
        <label class="row">
          <span>从业经历</span>
          <textarea rows="3" placeholder="填写年份、公司、岗位"></textarea>
        </label>
        <label class="row">
          <span>个人简介/擅长领域</span>
          <textarea rows="3" placeholder="示例：职业规划/面试辅导/职场心理"></textarea>
        </label>
        <div class="actions">
          <button class="ghost-btn" type="button">取消</button>
          <button class="primary-btn" type="submit">提交审核</button>
        </div>
        <p class="hint">审核中（2个工作日内），结果将通过系统通知推送。</p>
      </form>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ExpertApply',
  methods: {
    submit() {
      // 占位：提交入驻申请逻辑
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

h1 {
  margin: 6px 0 14px;
  font-size: 20px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.row span {
  color: var(--gray-700);
  font-weight: 600;
}

.upload {
  border: 1px dashed var(--gray-200);
  border-radius: 12px;
  padding: 14px;
  color: var(--gray-500);
  background: var(--gray-50);
}

textarea {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  outline: none;
  resize: vertical;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.ghost-btn,
.primary-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
}

.primary-btn {
  background: var(--blue);
  border-color: var(--blue);
  color: #fff;
}

.hint {
  margin: 4px 0 0;
  color: var(--gray-500);
  font-size: 12px;
}
</style>
