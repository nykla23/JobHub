<template>
  <div class="profile-edit-page">
    <header class="page-header">
      <div>
        <p class="eyebrow">编辑基础信息</p>
        <h1>更新昵称、简介、职业阶段等信息</h1>
      </div>
      <button class="ghost-btn" @click="$router.push('/profile/me')">返回主页</button>
    </header>

    <section class="card">
      <div class="avatar-preview">
        <div class="avatar">{{ avatarLetter }}</div>
        <p>头像默认根据昵称首字生成，如需上传可在后续接入中拓展。</p>
      </div>
      <form @submit.prevent="handleSubmit" class="form">
        <label>
          昵称
          <input v-model.trim="form.nickname" maxlength="20" required />
        </label>
        <label>
          职业阶段
          <input v-model.trim="form.careerStage" placeholder="如：2025 届应届生 · 运营方向" maxlength="30" />
        </label>
        <label>
          一句话简介
          <textarea v-model.trim="form.intro" rows="3" maxlength="80"></textarea>
        </label>
        <label>
          关注领域（用“·”分隔）
          <input v-model.trim="form.focusArea" placeholder="互联网运营 · 新媒体 · 用户增长" />
        </label>
        <label>
          常驻城市
          <input v-model.trim="form.city" />
        </label>
        <div class="actions">
          <button type="button" class="ghost-btn" @click="resetForm">重置</button>
          <button type="submit" class="primary-btn" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
        </div>
        <p v-if="message" class="success">{{ message }}</p>
        <p v-if="error" class="error">{{ error }}</p>
      </form>
    </section>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import { saveProfile } from '@/api/services/user';

export default {
  name: 'ProfileEdit',
  data() {
    return {
      form: {
        nickname: '',
        careerStage: '',
        intro: '',
        focusArea: '',
        city: ''
      },
      saving: false,
      message: '',
      error: ''
    };
  },
  computed: {
    ...mapState(['profile']),
    avatarLetter() {
      if (!this.form.nickname) return 'JH';
      return this.form.nickname.slice(0, 1).toUpperCase();
    }
  },
  created() {
    this.applyProfile();
  },
  methods: {
    applyProfile() {
      const source = this.profile || {};
      this.form = {
        nickname: source.nickname || '',
        careerStage: source.stage || source.careerStage || '',
        intro: source.bio || source.intro || '',
        focusArea: source.focusArea || (source.focus ? source.focus.join(' · ') : ''),
        city: source.city || ''
      };
    },
    async handleSubmit() {
      this.saving = true;
      this.error = '';
      this.message = '';
      try {
        const payload = {
          nickname: this.form.nickname,
          careerStage: this.form.careerStage,
          intro: this.form.intro,
          focusArea: this.form.focusArea,
          city: this.form.city
        };
        const data = await saveProfile(payload);
        this.$store.commit('setProfile', data.profile || payload);
        this.message = '保存成功';
        setTimeout(() => this.$router.push('/profile/me'), 600);
      } catch (err) {
        this.error = err.message || '保存失败';
      } finally {
        this.saving = false;
      }
    },
    resetForm() {
      this.applyProfile();
    }
  }
};
</script>

<style scoped>
.profile-edit-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.eyebrow {
  margin: 0;
  color: var(--gray-600);
  font-size: 13px;
}

h1 {
  margin: 4px 0 0;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.avatar-preview {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.avatar {
  width: 72px;
  height: 72px;
  background: rgba(99, 102, 241, 0.15);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #3730a3;
  font-weight: 700;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

label {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  color: var(--gray-700);
  gap: 6px;
}

input,
textarea {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.ghost-btn,
.primary-btn {
  border-radius: 12px;
  padding: 10px 18px;
  cursor: pointer;
  border: 1px solid var(--gray-200);
  background: #fff;
}

.primary-btn {
  background: var(--blue);
  color: #fff;
  border-color: var(--blue);
}

.error {
  color: #ef4444;
  margin: 0;
}

.success {
  color: #16a34a;
  margin: 0;
}
</style>

