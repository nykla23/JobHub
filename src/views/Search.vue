<template>
  <div class="page search-page">
    <section class="card search-panel">
      <div>
        <p class="eyebrow">智能搜索</p>
        <h1>找话题 · 找圈子 · 找专家</h1>
      </div>
      <form class="search-form" @submit.prevent="handleSearch">
        <input
          v-model.trim="localKeyword"
          type="search"
          placeholder="输入关键词，如 秋招面试 / 简历优化"
        />
        <button type="submit" class="primary-btn">搜索</button>
      </form>
    </section>

    <section class="card meta-panel">
      <div class="meta-block">
        <div class="meta-header">
          <h2>搜索历史</h2>
          <button class="link-btn" @click="clearHistory" :disabled="!searchHistory.length">清除</button>
        </div>
        <div class="chips">
          <button
            v-for="item in searchHistory"
            :key="item"
            class="chip"
            @click="applyKeyword(item)"
          >
            {{ item }}
          </button>
          <p v-if="!searchHistory.length" class="meta-hint">暂无历史，搜索后自动记录</p>
        </div>
      </div>
      <div class="meta-block">
        <div class="meta-header">
          <h2>热门搜索</h2>
          <small class="hint">实时热度榜</small>
        </div>
        <div class="chips hot">
          <button
            v-for="item in hotSearch"
            :key="item"
            class="chip ghost"
            @click="applyKeyword(item)"
          >
            {{ item }}
          </button>
          <p v-if="!hotSearch.length" class="meta-hint">暂无热门词</p>
        </div>
      </div>
    </section>

    <section class="card result-panel">
      <header class="result-header">
        <div class="tabs">
          <button
            v-for="option in typeOptions"
            :key="option.value"
            :class="['tab', { active: activeType === option.value }]"
            @click="changeType(option.value)"
          >
            {{ option.label }}
          </button>
        </div>
        <div class="sorts">
          <span>排序：</span>
          <button
            v-for="option in sortOptions"
            :key="option.value"
            :class="['sort-btn', { active: activeSort === option.value }]"
            @click="changeSort(option.value)"
          >
            {{ option.label }}
          </button>
        </div>
      </header>
      <p class="hint">
        关键词：{{ keyword || '全部' }} · 共 {{ total }} 条 · 每页 {{ pageSize }} 条
      </p>

      <template v-if="loadingResults">
        <skeleton-block v-for="n in pageSize" :key="'sr-sk-' + n" height="92px" />
      </template>

      <template v-else-if="!results.length">
        <empty-state title="暂无结果" desc="试试其他关键词或切换类型" />
      </template>

      <template v-else>
        <ul class="list">
          <li v-for="item in results" :key="item.id" class="list-row">
            <div>
              <div class="title">{{ item.title }}</div>
              <div class="desc">{{ item.desc }}</div>
              <div class="meta">{{ item.meta || defaultMeta(item) }}</div>
            </div>
            <button class="ghost-btn" @click="openLink(item.link)">查看</button>
          </li>
        </ul>
      </template>

      <footer class="pagination">
        <span class="page-info">第 {{ pageNum }} / {{ totalPages }} 页</span>
        <div class="page-actions">
          <button class="ghost-btn" :disabled="pageNum === 1" @click="prevPage">上一页</button>
          <button class="ghost-btn" :disabled="pageNum >= totalPages" @click="nextPage">下一页</button>
        </div>
      </footer>
    </section>
  </div>
</template>

<script>
import { mapMutations } from 'vuex';
import SkeletonBlock from '@/components/state/SkeletonBlock.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import {
  fetchSearchMeta,
  fetchSearchResults,
  clearSearchHistory as clearHistoryApi,
  appendSearchHistory
} from '@/api/services/search';

export default {
  name: 'Search',
  components: { SkeletonBlock, EmptyState },
  data() {
    return {
      typeOptions: [
        { label: '话题', value: 'topic' },
        { label: '内容', value: 'content' },
        { label: '圈子', value: 'group' },
        { label: '用户', value: 'user' },
        { label: '专家', value: 'expert' }
      ],
      sortOptions: [
        { label: '热度优先', value: 'hot' },
        { label: '最新发布', value: 'time' }
      ],
      activeType: 'topic',
      activeSort: 'hot',
      localKeyword: '',
      searchHistory: [],
      hotSearch: [],
      results: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loadingResults: false
    };
  },
  computed: {
    keyword() {
      const query = this.$route.query || {};
      return (query.keyword || query.q || '').trim();
    },
    totalPages() {
      if (!this.total) return 1;
      return Math.max(1, Math.ceil(this.total / this.pageSize));
    }
  },
  watch: {
    '$route.query': {
      immediate: true,
      handler() {
        this.syncFromRoute();
      }
    }
  },
  created() {
    this.fetchMeta();
  },
  methods: {
    ...mapMutations(['addSearchHistory', 'setHotKeywords', 'clearSearchHistory']),
    async fetchMeta() {
      try {
        const data = await fetchSearchMeta();
        this.searchHistory = data.searchHistory || [];
        this.hotSearch = data.hotSearch || [];
        this.setHotKeywords(this.hotSearch);
      } catch (error) {
        console.error('[search] 获取历史/热门失败', error);
      }
    },
    syncFromRoute() {
      const { type = 'topic', sort = 'hot', page = 1 } = this.$route.query || {};
      this.activeType = type || 'topic';
      this.activeSort = sort || 'hot';
      this.pageNum = Number(page) || 1;
      this.localKeyword = this.keyword;
      this.fetchResults();
    },
    async fetchResults() {
      this.loadingResults = true;
      try {
        const data = await fetchSearchResults({
          keyword: this.keyword,
          type: this.activeType,
          sort: this.activeSort,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        });
        this.results = data.list || [];
        this.total = data.total || 0;
        this.pageNum = data.pageNum || this.pageNum;
        this.pageSize = data.pageSize || this.pageSize;
      } catch (error) {
        console.error('[search] 获取搜索结果失败', error);
        this.results = [];
        this.total = 0;
      } finally {
        this.loadingResults = false;
      }
    },
    async handleSearch() {
      if (!this.localKeyword) {
        this.updateRoute({ page: 1, keyword: '' });
        return;
      }
      await appendSearchHistory(this.localKeyword);
      this.addSearchHistory(this.localKeyword);
      this.fetchMeta();
      this.updateRoute({ keyword: this.localKeyword, page: 1 });
    },
    applyKeyword(text) {
      this.localKeyword = text;
      this.handleSearch();
    },
    async clearHistory() {
      if (!this.searchHistory.length) return;
      await clearHistoryApi();
      this.searchHistory = [];
      this.clearSearchHistory();
    },
    changeType(value) {
      if (value === this.activeType) return;
      this.updateRoute({ type: value, page: 1 });
    },
    changeSort(value) {
      if (value === this.activeSort) return;
      this.updateRoute({ sort: value, page: 1 });
    },
    nextPage() {
      if (this.pageNum >= this.totalPages) return;
      this.updateRoute({ page: this.pageNum + 1 });
    },
    prevPage() {
      if (this.pageNum <= 1) return;
      this.updateRoute({ page: this.pageNum - 1 });
    },
    updateRoute(patch = {}) {
      const query = {
        keyword: patch.keyword !== undefined ? patch.keyword : this.keyword,
        type: patch.type || this.activeType,
        sort: patch.sort || this.activeSort,
        page: patch.page || this.pageNum
      };
      if (!query.keyword) delete query.keyword;
      this.$router.replace({ path: this.$route.path, query });
    },
    defaultMeta(item) {
      if (item.stats) return item.stats;
      return item.author ? `来自：${item.author}` : '平台推荐';
    },
    openLink(link) {
      if (!link) return;
      if (link.startsWith('http') || link.startsWith('/api')) {
        window.open(link, '_blank');
        return;
      }
      this.$router.push(link);
    }
  }
};
</script>

<style scoped>
.search-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.search-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.search-panel h1 {
  margin: 4px 0 0;
  font-size: 22px;
}

.search-form {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.search-form input {
  flex: 1;
  min-width: 240px;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 15px;
}

.primary-btn {
  border: none;
  background: var(--blue);
  color: #fff;
  border-radius: 12px;
  padding: 10px 18px;
  font-weight: 600;
  cursor: pointer;
}

.meta-panel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.meta-block {
  border: 1px solid var(--gray-200);
  border-radius: 14px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-header h2 {
  margin: 0;
  font-size: 16px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chip {
  border: 1px solid var(--gray-200);
  border-radius: 999px;
  padding: 6px 12px;
  background: #fff;
  cursor: pointer;
}

.chip.ghost {
  background: rgba(37, 99, 235, 0.08);
  border-color: transparent;
  color: var(--blue);
}

.meta-hint {
  margin: 0;
  color: var(--gray-500);
  font-size: 13px;
}

.link-btn {
  border: none;
  background: transparent;
  color: var(--blue);
  cursor: pointer;
}

.link-btn:disabled {
  color: var(--gray-500);
  opacity: 0.6;
  cursor: not-allowed;
}

.result-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 12px;
}

.tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tab {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.tab.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.sorts {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  color: var(--gray-700);
  font-size: 14px;
}

.sort-btn {
  border: 1px solid transparent;
  background: transparent;
  padding: 6px 10px;
  border-radius: 8px;
  cursor: pointer;
}

.sort-btn.active {
  background: rgba(37, 99, 235, 0.1);
  color: var(--blue);
}

.hint {
  margin: 0;
  color: var(--gray-500);
  font-size: 13px;
}

.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid var(--gray-200);
  border-radius: 14px;
  padding: 12px;
  gap: 12px;
}

.title {
  font-weight: 600;
  margin-bottom: 4px;
}

.desc {
  color: var(--gray-700);
  font-size: 14px;
}

.meta {
  color: var(--gray-500);
  font-size: 13px;
  margin-top: 4px;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 12px;
  padding: 8px 14px;
  cursor: pointer;
}

.ghost-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;
}

.page-info {
  color: var(--gray-700);
  font-size: 14px;
}

.page-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .list-row {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
