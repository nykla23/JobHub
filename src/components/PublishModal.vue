<template>
  <div class="publish-mask" @click.self="$emit('close')">
    <div class="publish-card">
      <div class="card-header">
        <div>
          <p class="eyebrow">内容发布</p>
          <h3>文字 / 图片 / 资料，提交后进入审核</h3>
        </div>
        <button class="close-btn" aria-label="关闭" @click="$emit('close')">×</button>
      </div>
      <form class="publish-form" @submit.prevent="submit">
        <label class="form-row">
          <span>标题</span>
          <input
            v-model.trim="form.title"
            type="text"
            placeholder="一句话概括问题/经验"
            :disabled="loading"
          >
        </label>
        <label class="form-row">
          <span>正文</span>
          <textarea
            v-model.trim="form.body"
            rows="4"
            placeholder="支持文字，前端占位：可插入图片/资料"
            :disabled="loading"
          ></textarea>
        </label>
        <label class="form-row">
          <span>标签</span>
          <input
            v-model.trim="form.tags"
            type="text"
            placeholder="最多 3 个标签，逗号/空格分隔"
            :disabled="loading"
          >
          <small class="assist">示例：简历优化、秋招面试、职场心理（最多 3 个标签）</small>
        </label>
        <label class="form-row upload-row">
          <span>图片上传（占位预览）</span>
          <div class="upload-placeholder" @click="openFilePicker">
            <p>支持 JPG/PNG，最多 4 张（本地预览，不上传）</p>
            <input
              ref="fileInput"
              class="file-input"
              type="file"
              accept="image/*"
              multiple
              @change="handleFiles"
              :disabled="loading"
            >
          </div>
          <div v-if="previews.length" class="preview-list">
            <div v-for="(img, idx) in previews" :key="img.id" class="preview-item">
              <img :src="img.url" :alt="`预览 ${idx + 1}`">
              <button type="button" class="remove-btn" @click="removeImage(idx)">×</button>
            </div>
          </div>
        </label>
        <div class="form-actions">
          <button type="button" class="ghost" @click="saveDraft" :disabled="loading">保存草稿</button>
          <button type="button" class="ghost" @click="handleClose" :disabled="loading">取消</button>
          <button type="submit" class="primary" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            {{ loading ? '提交中...' : '提交审核' }}
          </button>
        </div>
        <p class="hint" :class="{ error: !!error }">
          {{ error || '提交后进入智能过滤 + 人工审核，未通过会在系统通知说明原因' }}
        </p>
        <p class="assist">前端占位：上传/违规校验、存储、审核流由后端接入。</p>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PublishModal',
  data() {
    return {
      form: {
        title: '',
        body: '',
        tags: '',
        images: []
      },
      previews: [],
      loading: false,
      error: ''
    };
  },
  methods: {
    submit() {
      if (this.loading) return;
      const tags = this.parseTags(this.form.tags);
      if (!this.form.title || this.form.title.length < 4) {
        this.error = '标题至少 4 个字';
        return;
      }
      if (!this.form.body || this.form.body.length < 10) {
        this.error = '正文至少 10 个字';
        return;
      }
      if (tags.length > 3) {
        this.error = '标签最多 3 个';
        return;
      }
      this.error = '';
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        this.showToast('提交成功，进入审核中', 'success');
        this.$store.commit('setPublishOpen', false);
        this.$router.push('/publish/pending');
      }, 800);
    },
    parseTags(text) {
      if (!text) return [];
      return text
        .split(/[,，\\s]+/)
        .map((t) => t.trim())
        .filter(Boolean);
    },
    handleClose() {
      if (this.loading) return;
      this.$emit('close');
    },
    openFilePicker() {
      if (this.loading) return;
      if (this.$refs.fileInput) {
        this.$refs.fileInput.click();
      }
    },
    handleFiles(event) {
      const files = Array.from(event.target.files || []);
      const next = files.slice(0, 4 - this.previews.length);
      next.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previews.push({ id: `${file.name}-${Date.now()}`, url: e.target.result });
        };
        reader.readAsDataURL(file);
      });
      event.target.value = '';
    },
    removeImage(index) {
      this.previews.splice(index, 1);
    },
    saveDraft() {
      if (this.loading) return;
      this.showToast('草稿已暂存（前端占位）', 'info');
    },
    showToast(message, type = 'info') {
      const toast = this.$root && this.$root.$refs && this.$root.$refs.toast;
      if (toast && typeof toast.show === 'function') {
        toast.show(message, type);
      }
    }
  }
};
</script>

<style scoped>
.publish-mask {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20;
  padding: 16px;
}

.publish-card {
  width: min(720px, 100%);
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.16);
  padding: 20px 20px 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.eyebrow {
  margin: 0 0 6px;
  font-size: 13px;
  color: var(--gray-700);
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
}

.publish-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.form-row span {
  color: var(--gray-700);
}

input,
textarea {
  width: 100%;
  border: 1px solid var(--gray-200);
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
}

textarea {
  resize: vertical;
  min-height: 120px;
}

.upload-row .upload-placeholder {
  border: 1px dashed var(--gray-200);
  background: var(--gray-50);
  padding: 14px;
  border-radius: 10px;
  color: var(--gray-700);
  font-size: 13px;
  cursor: pointer;
}

.file-input {
  display: none;
}

.preview-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.preview-item {
  position: relative;
  width: 110px;
  height: 110px;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  overflow: hidden;
  background: var(--gray-50);
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 6px;
  right: 6px;
  border: none;
  background: rgba(17, 24, 39, 0.65);
  color: #fff;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 6px;
  flex-wrap: wrap;
}

.ghost,
.primary {
  min-width: 96px;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  cursor: pointer;
  font-weight: 600;
}

.ghost {
  background: #fff;
  color: var(--gray-700);
}

.primary {
  background: var(--blue);
  color: #fff;
  border-color: var(--blue);
}

.hint {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--gray-500);
}

.assist {
  color: var(--gray-500);
  font-size: 12px;
}

.error {
  color: var(--red);
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-top-color: #fff;
  border-radius: 50%;
  display: inline-block;
  margin-right: 6px;
  animation: spin 0.8s linear infinite;
  vertical-align: middle;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
