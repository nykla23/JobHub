<template>
  <div class="page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div>
        <p class="eyebrow">专业中心</p>

        <!-- 分类按钮 -->
        <div class="tags">
          <button
            v-for="tag in tags"
            :key="tag"
            :class="['tag', { active: activeTag === tag }]"
            @click="handleTagChange(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </div>
    </header>

    <!-- 专业资源 -->
    <section class="card">
      <header class="section-header">
        <h3>热门专业资源</h3>
        <router-link to="/creator" class="link">创作中心</router-link>
      </header>

      <div class="grid">
        <template v-if="loading">
          <SkeletonBlock v-for="n in 4" :key="'pr-sk-' + n" height="140px" />
        </template>

        <template v-else-if="!filteredResources.length">
          <EmptyState title="暂无资源" desc="稍后再试或换个分类" />
        </template>

        <template v-else>
          <ProResourceCard
            v-for="item in filteredResources"
            :key="item.id"
            :item="item"
          />
        </template>
      </div>
    </section>

    <!-- 专家列表 -->
    <section class="card">
      <header class="section-header">
        <h3>专家列表</h3>
        <router-link to="/pro/apply" class="link">专家入驻</router-link>
      </header>

      <div class="grid experts">
        <template v-if="loading">
          <SkeletonBlock v-for="n in 4" :key="'ex-sk-' + n" height="120px" />
        </template>

        <template v-else-if="!filteredExperts.length">
          <EmptyState title="暂无专家" desc="稍后再试或调整分类" />
        </template>

        <template v-else>
          <ExpertCard
            v-for="expert in filteredExperts"
            :key="expert.id"
            :item="expert"
          />
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
  components: {
    ProResourceCard,
    ExpertCard,
    SkeletonBlock,
    EmptyState
  },
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
    filteredResources() {
      const kw = this.keyword.trim();
      return this.proResources.filter(
        (item) =>
          (!kw ||
            item.title.includes(kw) ||
            (item.desc || '').includes(kw))
      );
    },
    filteredExperts() {
      const kw = this.keyword.trim();
      return this.experts.filter(
        (item) =>
          (!kw ||
            item.name.includes(kw) ||
            (item.desc || '').includes(kw))
      );
    }
  },
  async created() {
    await this.loadData();
  },
  methods: {
    async handleTagChange(tag) {
      if (this.activeTag === tag) return;
      this.activeTag = tag;
      await this.loadData();
    },

    async loadData() {
      this.loading = true;
      try {
        const [resourceRes, expertRes] = await Promise.all([
          fetchResourceList({
            keyword: this.keyword || undefined,
            tag: this.activeTag || undefined
          }),
          fetchExpertList({
            keyword: this.keyword || undefined,
            tag: this.activeTag || undefined
          })
        ]);

        this.proResources = this.adaptResources(
          resourceRes.records || resourceRes.list || []
        );
        this.experts = this.adaptExperts(
          expertRes.records || expertRes.list || []
        );
      } catch (e) {
        console.error('[ProCenter] 数据加载失败', e);
        this.proResources = [];
        this.experts = [];
      } finally {
        this.loading = false;
      }
    },

    adaptResources(list = []) {
      return list.map((item, idx) => {
        const meta = [
          item.score ? `评分 ${item.score}` : null,
          item.viewCount ? `浏览 ${item.viewCount}` : null,
          item.collectCount ? `收藏 ${item.collectCount}` : null
        ]
          .filter(Boolean)
          .join(' · ');

        return {
          id: item.id || `res-${idx}`,
          title: item.title || '资源',
          desc: item.desc || item.description || '',
          badge: item.tag || '资源',
          meta
        };
      });
    },

    adaptExperts(list = []) {
      return list.map((item, idx) => {
        const tags =
          item.tags ||
          (item.expertise
            ? String(item.expertise)
                .split(/[,，/]/)
                .map((t) => t.trim())
                .filter(Boolean)
            : []);

        return {
          id: item.id || `expert-${idx}`,
          name: item.name || '专家',
          desc: item.intro || item.certification || '',
          rating: item.score || '',
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
  flex-direction: column;
  gap: 8px;
}

.eyebrow {
  margin: 0;
  font-size: 13px;
  color: var(--gray-700);
}

/* 标签按钮 */
.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 14px;
  padding: 8px 14px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag:hover {
  border-color: var(--blue);
  color: var(--blue);
}

.tag.active {
  border-color: var(--blue);
  background: rgba(37, 99, 235, 0.1);
  color: var(--blue);
  font-weight: 600;
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
    align-items: flex-start;
  }
}
</style>
