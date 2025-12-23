<template>
  <div class="page topics-page">
    <section class="card page-header">
      <div>
        <p class="eyebrow">话题广场</p>
        <h1>精选求职与职场话题</h1>
      </div>
      <div class="sort-group">
        <button
          v-for="item in sortOptions"
          :key="item.value"
          :class="['pill', { active: activeSort === item.value }]"
          @click="changeSort(item.value)"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="card filter-panel">
      <div class="filter-row">
        <span class="filter-label">标签</span>
        <div class="chips">
          <button
            v-for="tag in tagFilters"
            :key="tag || 'all'"
            :class="['chip', { active: activeTag === tag }]"
            @click="changeTag(tag)"
          >
            {{ tag || '全部' }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <span class="filter-label">等级</span>
        <div class="chips">
          <button
            v-for="level in levelOptions"
            :key="level.value || 'all-level'"
            :class="['chip ghost', { active: activeLevel === level.value }]"
            @click="changeLevel(level.value)"
          >
            {{ level.label }}
          </button>
        </div>
      </div>
    </section>

    <section class="card grid-card">
      <template v-if="loading">
        <skeleton-block v-for="n in pageSize" :key="'tp-sk-' + n" height="150px" />
      </template>
      <template v-else-if="!topics.length">
        <empty-state title="暂无话题" desc="换个筛选条件或稍后再试" />
      </template>
      <template v-else>
        <topic-card v-for="item in topics" :key="item.id" :item="item" />
      </template>
    </section>

    <footer class="pagination">
      <span class="page-info">第 {{ pageNum }} / {{ totalPages }} 页 · 共 {{ total }} 条</span>
      <div class="page-actions">
        <button class="ghost-btn" :disabled="pageNum === 1" @click="prevPage">上一页</button>
        <button class="ghost-btn" :disabled="pageNum >= totalPages" @click="nextPage">下一页</button>
      </div>
    </footer>
  </div>
</template>

<script>
import TopicCard from '@/components/cards/TopicCard.vue';
import SkeletonBlock from '@/components/state/SkeletonBlock.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import { fetchTopicList } from '@/api/services/topic';


export default {
  name: 'Topics',
  components: { TopicCard, SkeletonBlock, EmptyState },
  data() {
    return {
      sortOptions: [
        { value: 'hot', label: '热度优先' },
        { value: 'time', label: '最新回复' }
      ],
      levelOptions: [
        { value: '', label: '全部' },
        { value: 'A', label: 'A级' },
        { value: 'S', label: 'S级' }
      ],
      activeTag: '',
      activeLevel: '',
      activeSort: 'hot',
      topics: [],
      loading: false,
      total: 0,
      pageNum: 1,
      pageSize: 8
    };
  },

  computed: {
    identityTag() {
      return this.$store.state.identityTag;
    },

    tagFilters() {
  return [
    '',
    '秋招面试',
    '第一份实习',
    '简历优化',
    '职场新人避坑',
    '转正汇报',
    '沟通技巧',
    '行业交流',
    '管理进阶',
    '经验分享',
    'offer选择'
  ];
},

    totalPages() {
      if (!this.total) return 1;
      return Math.max(1, Math.ceil(this.total / this.pageSize));
    }
  },

  watch: {
    // 页面初始化、路由变化 → 拉取数据
    '$route.query': {
      immediate: true,
      handler() {
        this.syncFromRoute();
      }
    },
  },

  methods: {
    // ========== 从路由同步状态 ==========
    syncFromRoute() {
      const { tag = '', level = '', sort = 'hot', page = 1 } = this.$route.query || {};

      this.activeTag = tag;
      this.activeLevel = level;
      this.activeSort = sort;
      this.pageNum = Number(page) || 1;

      this.loadTopics();
    },

    // ========== 加载话题 ==========
    async loadTopics() {
      this.loading = true;
      try {
        const data = await fetchTopicList({
          tag: this.activeTag || undefined,
          level: this.activeLevel || undefined,
          sort: this.activeSort,
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        });

        this.topics = data.list || [];
        this.total = data.total || 0;
        this.pageNum = data.pageNum || this.pageNum;
        this.pageSize = data.pageSize || this.pageSize;

      } catch (error) {
        console.error('[topics] 获取话题列表失败', error);
        this.topics = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },

    // ========== 切换标签 ==========
    changeTag(tag) {
      const next = this.activeTag === tag ? '' : tag;
      this.updateRoute({ tag: next, page: 1 });
    },

    changeLevel(level) {
      const next = this.activeLevel === level ? '' : level;
      this.updateRoute({ level: next, page: 1 });
    },

    changeSort(sort) {
      if (sort === this.activeSort) return;
      this.updateRoute({ sort, page: 1 });
    },

    // ========== 翻页 ==========
    nextPage() {
      if (this.pageNum >= this.totalPages) return;
      this.updateRoute({ page: this.pageNum + 1 });
    },

    prevPage() {
      if (this.pageNum <= 1) return;
      this.updateRoute({ page: this.pageNum - 1 });
    },

    // ========== 路由更新（已修复 watch 无法触发问题） ==========
    updateRoute(patch = {}) {
      this.$router.replace({
        path: this.$route.path,
        query: {
          tag: patch.tag !== undefined ? patch.tag : this.activeTag,
          level: patch.level !== undefined ? patch.level : this.activeLevel,
          sort: patch.sort || this.activeSort,
          page: patch.page || this.pageNum
        }
      });
    }
  }
};
</script>

<style scoped>
.topics-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.04);
}

.page-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.eyebrow {
  margin: 0;
  color: var(--gray-600);
  font-size: 13px;
}

.page-header h1 {
  margin: 4px 0 0;
  font-size: 22px;
}

.sort-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pill {
  border: 1px solid var(--gray-200);
  border-radius: 999px;
  padding: 8px 14px;
  background: #fff;
  cursor: pointer;
}

.pill.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

/* 其他样式保持你的原样不动 */
.filter-panel { display:flex;flex-direction:column;gap:14px;}
.filter-row{display:flex;gap:12px;flex-wrap:wrap;align-items:flex-start;}
.filter-label{font-weight:600;color:var(--gray-700);min-width:48px;}
.chips{display:flex;flex-wrap:wrap;gap:8px;flex:1;}
.chip{border:1px solid var(--gray-200);border-radius:999px;padding:6px 14px;background:#fff;cursor:pointer;font-size:14px;}
.chip.ghost{background:rgba(37,99,235,0.05);border-color:transparent;color:var(--blue);}
.chip.active{border-color:var(--blue);color:var(--blue);background:rgba(37,99,235,0.1);}
.grid-card{display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:14px;padding:0;background:transparent;box-shadow:none;}
.pagination{display:flex;justify-content:space-between;align-items:center;padding:0 4px;}
.page-info{color:var(--gray-600);font-size:14px;}
.page-actions{display:flex;gap:8px;}
.ghost-btn{border:1px solid var(--gray-200);background:#fff;border-radius:10px;padding:8px 12px;cursor:pointer;}
.ghost-btn:disabled{opacity:.4;cursor:not-allowed;}

@media (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
}
</style>
