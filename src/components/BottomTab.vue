<template>
  <nav class="bottom-tab">
    <router-link to="/" :class="linkClass('home')">首页</router-link>
    <router-link to="/topics" :class="linkClass('topics')">话题</router-link>
    <button class="publish" @click="$emit('open-publish')">发布</button>
    <router-link to="/circles" :class="linkClass('circles')">圈子</router-link>
    <router-link to="/pro" :class="linkClass('pro')">专业</router-link>
  </nav>
</template>

<script>
export default {
  name: 'BottomTab',
  props: {
    activeKey: {
      type: String,
      default: 'home'
    }
  },
  methods: {
    linkClass(key) {
      return [{ active: this.activeKey === key }];
    }
  }
};
</script>

<style scoped>
.bottom-tab {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 64px;
  background: #ffffff;
  border-top: 1px solid var(--gray-200);
  display: none;
  align-items: center;
  justify-content: space-around;
  z-index: 9;
}

.bottom-tab a,
.bottom-tab button {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: var(--gray-700);
  cursor: pointer;
}

.bottom-tab a.active {
  color: var(--blue);
  font-weight: 600;
}

.publish {
  background: var(--orange);
  color: #fff;
  margin: 6px;
  border-radius: 12px;
}

@media (max-width: 768px) {
  .bottom-tab {
    display: flex;
  }
}
</style>
