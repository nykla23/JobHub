<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">专业中心 · {{ identityTag || '通用' }}</p>
        <h1>{{ headline }}</h1>
      </div>
      <input v-model.trim="keyword" type="text" class="search-input" placeholder="搜索专家姓名/标签/关键词">
    </header>

    <div class="tags">
      <button
        v-for="tag in tags"
        :key="tag"
        :class="['tag', { active: activeTag === tag }]"
        @click="activeTag = tag"
      >
        {{ tag }}
      </button>
    </div>

    <section class="card">
      <header class="section-header">
        <h3>热门专业资源</h3>
        <router-link to="/creator" class="link">专家发布入口</router-link>
      </header>
      <div class="grid">
        <template v-if="loading">
          <skeleton-block v-for="n in 4" :key="'pr-sk-' + n" height="140px" />
        </template>
        <template v-else-if="!filteredResources.length">
          <empty-state title="暂无资源" desc="稍后再试或换个筛选" />
        </template>
        <template v-else>
          <pro-resource-card v-for="item in filteredResources" :key="item.id" :item="item" />
        </template>
      </div>
    </section>

    <section class="card">
      <header class="section-header">
        <h3>专家列表</h3>
        <router-link to="/pro/apply" class="link">专家入驻</router-link>
      </header>
      <div class="grid experts">
        <template v-if="loading">
          <skeleton-block v-for="n in 4" :key="'ex-sk-' + n" height="120px" />
        </template>
        <template v-else-if="!filteredExperts.length">
          <empty-state title="暂无专家" desc="稍后再试或调整筛选" />
        </template>
        <template v-else>
          <expert-card v-for="expert in filteredExperts" :key="expert.id" :item="expert" />
        </template>
      </div>
    </section>
  </div>
</template>

<script>
import ProResourceCard from '@/components/cards/ProResourceCard.vue';
import ExpertCard from '@/components/cards/ExpertCard.vue';
import SkeletonBlock from '@/components/state/SkeletonBlock.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import { fetchResourceList, fetchExpertList } from '@/api/services/expert';

export default {
  name: 'ProCenter',
  components: { ProResourceCard, ExpertCard, SkeletonBlock, EmptyState },
  data() {
    return {
      tags: ['职业规划', '心理咨询', '简历优化', '面试技巧', '职场适应'],
      activeTag: '职业规划',
      keyword: '',
      proResources: [],
      experts: [],
      loading: true
    };
  },
  computed: {
    identityTag() {
      return this.$store.state.identityTag;
    },
    headline() {
      const map = {
        学生: '校招/实习 · 简历/面试强化',
        职场菜鸟: '职场适应与沟通 · 心理支持',
        专家: '专家创作/榜单/答疑入口',
        职场老手: '职业规划与进阶 · 行业洞察'
      };
      return map[this.identityTag] || '职业规划 / 心理咨询 / 简历优化 / 面试技巧 / 职场适应';
    },
    filteredResources() {
      const kw = this.keyword.trim();
      return this.proResources.filter(
        (item) =>
          (!this.activeTag || (item.desc && item.desc.includes(this.activeTag))) &&
          (!kw || item.title.includes(kw) || (item.desc || '').includes(kw))
      );
    },
    filteredExperts() {
      const kw = this.keyword.trim();
      return this.experts.filter(
        (item) =>
          (!this.activeTag || (item.tags || []).includes(this.activeTag)) &&
          (!kw || item.name.includes(kw) || (item.desc || '').includes(kw))
      );
    }
  },
  async created() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const [resourceRes, expertRes] = await Promise.all([
          fetchResourceList({ keyword: this.keyword || undefined, tag: this.activeTag || undefined }),
          fetchExpertList({ keyword: this.keyword || undefined, tag: this.activeTag || undefined })
        ]);
        this.proResources = this.adaptResources(resourceRes.records || resourceRes.list || []);
        this.experts = this.adaptExperts(expertRes.records || expertRes.list || []);
      } catch (error) {
        console.error('[pro] 获取专家/资源失败', error);
        this.proResources = [];
        this.experts = [];
      } finally {
        this.loading = false;
      }
    },
    adaptResources(list = []) {
      return list.map((item, idx) => {
        if (typeof item === 'string') {
          return {
            id: `res-${idx}`,
            title: item,
            desc: '',
            badge: '资源',
            meta: ''
          };
        }
        const scoreMeta =
          item.score || item.viewCount || item.collectCount
            ? [
                item.score ? `评分 ${item.score}` : null,
                item.viewCount ? `浏览 ${item.viewCount}` : null,
                item.collectCount ? `收藏 ${item.collectCount}` : null
              ].filter(Boolean).join(' · ')
            : item.meta || '';
        return {
          id: item.id || item.contentId || `res-${idx}`,
          title: item.title || item.name || '资源',
          desc: item.desc || item.description || item.intro || '',
          badge: item.tag || item.type || '资源',
          meta: scoreMeta
        };
      });
    },
    adaptExperts(list = []) {
      return list.map((item, idx) => {
        const tags =
          item.tags ||
          (item.expertise ? String(item.expertise).split(/[,，/]/).map((t) => t.trim()).filter(Boolean) : []);
        return {
          id: item.id || `expert-${idx}`,
          name: item.name || item.title || '专家',
          desc: item.intro || item.description || item.certification || item.desc || '',
          rating: item.score || item.rating || '',
          tags
        };
      });
    }
  }
};
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.page-header h1 {
  margin: 4px 0 0;
  font-size: 18px;
}

.search-input {
  border: 1px solid var(--gray-200);
  border-radius: 10px;
  padding: 10px 12px;
  min-width: 260px;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 12px;
  padding: 8px 12px;
  cursor: pointer;
}

.tag.active {
  color: var(--blue);
  border-color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.link {
  color: var(--blue);
  font-weight: 600;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-input {
    width: 100%;
  }
}
</style>
