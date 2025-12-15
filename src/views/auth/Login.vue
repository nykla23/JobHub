<template>
  <div class="auth-layout">
    <div class="auth-card">
      <h2>用户登录</h2>
      <form @submit.prevent="handleSubmit">
        <label>
          账号
          <input v-model.trim="form.username" type="text" placeholder="请输入账号/邮箱 (test@example.com)" required />
        </label>
        <label>
          密码
          <input v-model.trim="form.password" type="password" placeholder="请输入密码 (123456)" required />
        </label>
        <button class="primary-btn" type="submit" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </form>
      <div class="links">
        <button class="link-btn" type="button" @click="goForgot">忘记密码？</button>
        <span>还没有账号？
          <button class="link-btn" type="button" @click="goRegister">注册</button>
        </span>
      </div>
      <p v-if="error" class="error-tip">{{ error }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: 'test@example.com',
        password: '123456'
      },
      loading: false,
      error: ''
    };
  },
  methods: {
    async handleSubmit() {
      this.error = '';
      this.loading = true;
      try {
        await this.$store.dispatch('login', this.form);
        this.$router.push('/');
      } catch (err) {
        this.error = err.message || '登录失败，请稍后再试';
      } finally {
        this.loading = false;
      }
    },
    goRegister() {
      this.$router.push('/auth/register');
    },
    goForgot() {
      this.$router.push('/auth/forgot');
    }
  }
};
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4f46e5, #60a5fa);
  padding: 24px;
}

.auth-card {
  width: min(360px, 100%);
  background: #fff;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.25);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

h2 {
  margin: 0 0 4px;
  text-align: center;
}

form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

label {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  color: var(--gray-700);
  gap: 6px;
}

input {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
}

.primary-btn {
  border: none;
  border-radius: 12px;
  background: #6366f1;
  color: #fff;
  padding: 12px;
  font-weight: 600;
  cursor: pointer;
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.links {
  display: flex;
  flex-direction: column;
  gap: 6px;
  text-align: center;
  font-size: 13px;
  color: var(--gray-600);
}

.link-btn {
  border: none;
  background: transparent;
  color: #6366f1;
  cursor: pointer;
  padding: 0;
}

.error-tip {
  color: #ef4444;
  text-align: center;
  margin: 0;
}
</style>

