<template>
  <header class="app-header">
    <div class="brand" @click="$emit('go-home')">
      <span class="brand-mark">JH</span>
      <span class="brand-name">JobHub</span>
    </div>
    <nav class="main-nav">
      <router-link to="/" :class="linkClass('home')">首页</router-link>
      <router-link to="/topics" :class="linkClass('topics')">话题广场</router-link>
      <router-link to="/circles" :class="linkClass('circles')">兴趣圈子</router-link>
      <router-link to="/pro" :class="linkClass('pro')">专业中心</router-link>
    </nav>
    <div class="header-right">
      <search-bar class="header-search" />
      <button class="publish-btn" @click="$emit('open-publish')">快捷发布</button>
      <router-link to="/messages" class="message-bell" aria-label="消息">
        <span class="bell-icon">??</span>
        <span v-if="unreadCount > 0" class="badge" aria-hidden="true">{{ displayUnread }}</span>
      </router-link>
      <router-link to="/profile/me" class="mini-avatar" aria-label="个人中心">
        {{ avatarLetter }}
      </router-link>
    </div>
  </header>
</template>

<script>
import SearchBar from './SearchBar.vue';

export default {
  name: 'AppHeader',
  components: { SearchBar },
  props: {
    activeKey: {
      type: String,
      default: 'home'
    },
    unreadCount: {
      type: Number,
      default: 0
    }
  },
  computed: {
    displayUnread() {
      if (this.unreadCount > 99) return '99+';
      return this.unreadCount;
    },
    avatarLetter() {
      const nickname =
        this.$store.state.profile.nickname ||
        (this.$store.state.userInfo && this.$store.state.userInfo.nickname) ||
        'JH';
      return nickname.slice(0, 1).toUpperCase();
    }
  },
  methods: {
    linkClass(key) {
      return ['nav-link', { active: this.activeKey === key }];
    }
  }
};
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  padding: 12px 24px;
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.brand {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
  color: var(--gray-900);
  font-weight: 700;
}

.brand-mark {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--blue), var(--blue-600));
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.brand-name {
  font-size: 16px;
  letter-spacing: 0.4px;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: 24px;
}

.nav-link {
  padding: 8px 10px;
  border-radius: 6px;
  color: var(--gray-700);
  font-size: 14px;
}

.nav-link.active {
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mini-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(99, 102, 241, 0.15);
  color: #3730a3;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.header-search {
  min-width: 280px;
}

.publish-btn {
  background: var(--orange);
  color: #fff;
  border: none;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.publish-btn:hover {
  box-shadow: 0 8px 16px rgba(245, 158, 11, 0.35);
  transform: translateY(-1px);
}

.publish-btn:active {
  transform: translateY(0);
}

.message-bell {
  position: relative;
  width: 40px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: var(--gray-50);
  color: var(--gray-900);
}

.badge {
  position: absolute;
  top: 8px;
  right: 10px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--red);
  border-radius: 50%;
  color: #fff;
  font-size: 11px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 1024px) {
  .app-header {
    grid-template-columns: auto 1fr;
    grid-template-rows: auto auto;
    row-gap: 12px;
    padding: 12px 16px;
  }

  .main-nav {
    grid-column: 1 / -1;
    order: 2;
    overflow-x: auto;
  }

  .header-right {
    justify-content: flex-end;
  }

  .header-search {
    min-width: 200px;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 10px 12px;
    grid-template-columns: 1fr auto;
  }

  .main-nav {
    display: none;
  }

  .header-search {
    display: none;
  }

  .publish-btn {
    display: none;
  }
}
</style>
