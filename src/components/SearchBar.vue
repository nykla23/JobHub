<template>
  <div class="search-bar">
    <input
      v-model="keyword"
      type="text"
      class="search-input"
      :placeholder="placeholder"
      @keyup.enter="onSearch"
      @focus="openPanel = true"
    >
    <button class="search-btn" @click="onSearch">搜索</button>
    <div v-if="openPanel" class="search-panel" @mouseleave="openPanel = false">
      <div class="panel-section">
        <div class="section-title">
          最近搜索
          <button class="clear-btn" @click.stop="clearHistory">清除</button>
        </div>
        <div class="chips">
          <button v-for="item in displayHistory" :key="item" class="chip" @click="selectKeyword(item)">{{ item }}</button>
          <span v-if="!displayHistory.length" class="empty">暂无记录</span>
        </div>
      </div>
      <div class="panel-section">
        <div class="section-title">热门搜索</div>
        <div class="chips">
          <button v-for="hot in displayHotKeywords" :key="hot" class="chip hot" @click="selectKeyword(hot)">{{ hot }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchBar',
  data() {
    return {
      keyword: '',
      openPanel: false
    };
  },
  props: {
    placeholder: {
      type: String,
      default: '搜索话题/内容/圈子/用户/专家'
    }
  },
  computed: {
    displayHistory() {
      return (this.$store.state.searchHistory || []).slice(0, 10);
    },
    displayHotKeywords() {
      return (this.$store.state.hotKeywords || []).slice(0, 10);
    }
  },
  methods: {
    onSearch() {
      const text = (this.keyword || '').trim();
      if (!text) return;
      this.$store.commit('addSearchHistory', text);
      this.$router.push({ path: '/search', query: { q: text } });
      this.openPanel = false;
    },
    selectKeyword(text) {
      this.keyword = text;
      this.onSearch();
    },
    clearHistory() {
      this.$store.commit('clearSearchHistory');
    }
  }
};
</script>

<style scoped>
.search-bar {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--gray-50);
  border: 1px solid var(--gray-200);
  border-radius: 10px;
  padding: 2px 2px 2px 10px;
  min-height: 44px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  color: var(--gray-900);
}

.search-btn {
  border: none;
  background: var(--blue);
  color: #fff;
  padding: 9px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.search-panel {
  position: absolute;
  top: 104%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid var(--gray-200);
  border-radius: 10px;
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.08);
  padding: 12px;
  z-index: 5;
}

.panel-section + .panel-section {
  margin-top: 12px;
  border-top: 1px solid var(--gray-200);
  padding-top: 10px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: var(--gray-700);
  margin-bottom: 8px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  border: 1px solid var(--gray-200);
  background: #fff;
  padding: 6px 10px;
  border-radius: 999px;
  cursor: pointer;
  font-size: 13px;
  color: var(--gray-700);
}

.chip.hot {
  border-color: rgba(37, 99, 235, 0.25);
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
}

.clear-btn {
  border: none;
  background: transparent;
  color: var(--blue);
  cursor: pointer;
  font-size: 12px;
}

.empty {
  color: var(--gray-500);
  font-size: 13px;
}
</style>