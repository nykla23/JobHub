<template>
  <div class="auth-layout">
    <div class="auth-card">
      <h2>创建账号</h2>
      <form @submit.prevent="handleSubmit">
        <label>
          邮箱
          <input v-model.trim="form.username" type="email" placeholder="请输入邮箱" required />
        </label>
        <label>
          密码
          <input v-model.trim="form.password" type="password" placeholder="请输入密码" required />
        </label>
        <label>
          确认密码
          <input v-model.trim="form.confirmPassword" type="password" placeholder="再次输入密码" required />
        </label>
        <label>
          身份标签
          <select v-model="form.identityTag" required>
            <option value="">请选择身份</option>
            <option value="学生">学生</option>
            <option value="职场菜鸟">职场菜鸟</option>
            <option value="专家">专家</option>
            <option value="职场老手">职场老手</option>
          </select>
        </label>
        <label>
          情感标签
          <input v-model.trim="form.emotionTag" type="text" placeholder="请输入情感标签（如：开心）" />
        </label>
        <label>
          个性签名
          <input v-model.trim="form.signature" type="text" placeholder="请输入个性签名" />
        </label>
        <button class="primary-btn" type="submit" :disabled="loading">{{ loading ? '提交中...' : '注册' }}</button>
      </form>
      <div class="links">
        已有账号？
        <button class="link-btn" type="button" @click="goLogin">返回登录</button>
      </div>
      <p v-if="error" class="error-tip">{{ error }}</p>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/services/auth';

export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        identityTag: '',
        emotionTag: '',
        signature: ''
      },
      loading: false,
      error: ''
    };
  },
  methods: {
    async handleSubmit() {
      this.error = '';
      if (this.form.password !== this.form.confirmPassword) {
        this.error = '两次输入的密码不一致';
        return;
      }
      if (!this.form.identityTag) {
        this.error = '请选择身份标签';
        return;
      }
      this.loading = true;
      try {
        const data = await register({
          username: this.form.username,
          password: this.form.password,
          identityTag: this.form.identityTag,
          emotionTag: this.form.emotionTag || '开心',
          signature: this.form.signature || '我的个性签名'
        });
        // 注册成功后跳转到身份选择页面或直接登录
        this.$router.push({ path: '/auth/select-identity', query: { userId: data.userId || data.id } });
      } catch (err) {
        this.error = err.message || '注册失败';
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
</style>

