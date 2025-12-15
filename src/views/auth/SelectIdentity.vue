<template>
  <div class="auth-layout">
    <div class="auth-card">
      <h2>请选择你的身份</h2>
      <p class="sub">用于生成精准推荐，可随时在首页修改</p>
      <div class="identity-list">
        <button
          v-for="role in roles"
          :key="role.value"
          :class="['identity-btn', { active: activeRole === role.value }]"
          @click="activeRole = role.value"
        >
          {{ role.label }}
        </button>
      </div>
      <button class="primary-btn" :disabled="!activeRole || loading" @click="confirm">
        {{ loading ? '保存中...' : '下一步' }}
      </button>
      <div class="links">
        已有账号？
        <button class="link-btn" type="button" @click="goLogin">返回登录</button>
      </div>
      <p v-if="error" class="error-tip">{{ error }}</p>
    </div>
  </div>
</template>

<script>
import { selectIdentity } from '@/api/services/auth';

export default {
  name: 'SelectIdentity',
  data() {
    return {
      roles: [
        { label: '学生', value: '学生' },
        { label: '职场菜鸟', value: '职场菜鸟' },
        { label: '专家', value: '专家' },
        { label: '职场老手', value: '职场老手' }
      ],
      activeRole: '',
      loading: false,
      error: ''
    };
  },
  computed: {
    userId() {
      return Number(this.$route.query.userId);
    }
  },
  created() {
    if (!this.userId) {
      this.$router.replace('/auth/register');
    }
  },
  methods: {
    async confirm() {
      if (!this.activeRole || !this.userId) return;
      this.loading = true;
      this.error = '';
      try {
        await selectIdentity({ userId: this.userId, careerStage: this.activeRole });
        this.$router.push('/auth/login');
      } catch (err) {
        this.error = err.message || '保存失败';
      } finally {
        this.loading = false;
      }
    },
    goLogin() {
      this.$router.push('/auth/login');
    }
  }
};
</script>

<style scoped>
@import './styles.css';

.sub {
  text-align: center;
  margin: 0;
  color: var(--gray-600);
  font-size: 14px;
}

.identity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 16px 0;
}

.identity-btn {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  font-size: 16px;
  background: #f9fafb;
  cursor: pointer;
}

.identity-btn.active {
  border-color: #6366f1;
  background: rgba(99, 102, 241, 0.1);
  color: #3730a3;
}
</style>
