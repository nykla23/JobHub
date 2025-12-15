<template>
  <div id="app">
    <div class="app-shell">
      <app-header
        v-if="!isAuthPage"
        :active-key="activeKey"
        :unread-count="unreadTotal"
        @open-publish="togglePublish(true)"
        @go-home="$router.push('/')"
      />
      <main :class="['app-content', { 'auth-full': isAuthPage }]">
        <router-view />
      </main>
      <bottom-tab
        v-if="!isAuthPage"
        :active-key="activeKey"
        @open-publish="togglePublish(true)"
      />
      <publish-modal
        v-if="showPublish && !isAuthPage"
        @close="togglePublish(false)"
      />
      <toast ref="toast" />
    </div>
  </div>
</template>

<script>
import AppHeader from './components/AppHeader.vue';
import BottomTab from './components/BottomTab.vue';
import PublishModal from './components/PublishModal.vue';
import Toast from './components/state/Toast.vue';

export default {
  name: 'App',
  components: {
    AppHeader,
    BottomTab,
    PublishModal,
    Toast
  },
  computed: {
    isAuthPage() {
      return this.$route.path.startsWith('/auth');
    },
    showPublish() {
      return this.$store.state.publishOpen;
    },
    activeKey() {
      const path = this.$route.path;
      // 个人主页相关路径不激活任何导航项
      if (path.startsWith('/profile')) return '';
      if (path.startsWith('/topics') || path.startsWith('/topic/')) return 'topics';
      if (path.startsWith('/circles') || path.startsWith('/circle/')) return 'circles';
      // 注意：需要排除 /profile 路径，因为它也以 /pro 开头
      if (path.startsWith('/pro') || path.startsWith('/creator')) return 'pro';
      if (path.startsWith('/messages')) return 'messages';
      return 'home';
    },
    unreadTotal() {
      return this.$store.getters.unreadTotal;
    }
  },
  methods: {
    togglePublish(next) {
      this.$store.commit('setPublishOpen', next);
    },
    notify(message, type = 'info') {
      if (this.$refs.toast) {
        this.$refs.toast.show(message, type);
      }
    }
  }
};
</script>

<style>
:root {
  --blue: #2563eb;
  --blue-600: #1d4ed8;
  --orange: #f59e0b;
  --red: #ef4444;
  --gray-900: #111827;
  --gray-700: #374151;
  --gray-500: #9ca3af;
  --gray-200: #e5e7eb;
  --gray-50: #f5f7fa;
  --card: #ffffff;
}

*,
*::before,
*::after {
  box-sizing: border-box;
}

body,
#app {
  margin: 0;
  font-family: "Microsoft YaHei", "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  background: var(--gray-50);
  color: var(--gray-900);
  -webkit-font-smoothing: antialiased;
}

a {
  color: inherit;
  text-decoration: none;
}

button {
  font-family: inherit;
}

.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-content {
  flex: 1;
  width: 100%;
  max-width: 1280px;
  margin: 88px auto 72px;
  padding: 0 16px 32px;
}

.auth-full {
  margin: 0;
  padding: 0;
  max-width: 100%;
}

@media (max-width: 768px) {
  .app-content {
    margin: 72px 0 88px;
    max-width: 100%;
    padding: 0 12px 80px;
  }
}
</style>
