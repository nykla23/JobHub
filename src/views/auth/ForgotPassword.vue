<template>
  <div class="auth-layout">
    <div class="auth-card">
      <h2>忘记密码</h2>
      <form @submit.prevent="handleSubmit">
        <label>
          用户名/邮箱
          <input
            v-model.trim="form.username"
            type="text"
            placeholder="请输入用户名或邮箱"
            required
          />
        </label>
        <label>
          新密码
          <input
            v-model.trim="form.newPassword"
            type="password"
            placeholder="请输入新密码"
            required
          />
        </label>
        <label>
          确认新密码
          <input
            v-model.trim="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            required
          />
        </label>
        <button class="primary-btn" type="submit" :disabled="loading">
          {{ loading ? '提交中...' : '重置密码' }}
        </button>
      </form>
      <div class="links">
        想起来了？
        <button class="link-btn" type="button" @click="goLogin">返回登录</button>
      </div>
      <p v-if="message" class="success-tip">{{ message }}</p>
      <p v-if="error" class="error-tip">{{ error }}</p>
    </div>
  </div>
</template>

<script>
import { resetPassword } from '@/api/services/auth';

export default {
  name: 'ForgotPassword',
  data() {
    return {
      form: {
        username: '',
        newPassword: '',
        confirmPassword: ''
      },
      loading: false,
      message: '',
      error: ''
    };
  },
  methods: {
    async handleSubmit() {
      this.error = '';
      this.message = '';
      if (this.form.newPassword !== this.form.confirmPassword) {
        this.error = '两次输入的密码不一致';
        return;
      }
      this.loading = true;
      try {
        await resetPassword({
          username: this.form.username,
          newPassword: this.form.newPassword,
          confirmPassword: this.form.confirmPassword
        });
        this.message = '密码重置成功，请登录';
      } catch (err) {
        this.error = err.message || '重置失败';
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

.success-tip {
  color: #16a34a;
  text-align: center;
  margin: 0;
}
</style>
