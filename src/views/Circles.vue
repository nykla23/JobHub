<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">兴趣圈子 · {{ identityTag || '通用' }}</p>
        <h1>{{ headline }}</h1>
      </div>
      <div class="tools">
        <div class="search-group">
          <input v-model.trim="keyword" type="text" placeholder="搜索小组名称/标签" class="search-input">
          <button class="search-btn" @click="doSearch">搜索</button>
        </div>
        <button class="primary-btn create-btn">+ 创建小组</button>
      </div>
    </header>
    <div class="tags">
      <button
        v-for="tag in displayTags"
        :key="tag"
        :class="['tag', { active: selectedTag === tag }]"
        @click="selectTag(tag)"
      >
        {{ tag }}
      </button>
    </div>
    <section class="grid">
      <template v-if="loading">
        <skeleton-block v-for="n in 6" :key="'cl-sk-' + n" height="140px" />
      </template>
      <template v-else-if="!displayCircles.length">
        <empty-state title="暂无圈子" desc="换个身份或稍后再试" />
      </template>
      <template v-else>
        <circle-card v-for="circle in displayCircles" :key="circle.id" :item="circle" />
      </template>
    </section>
  </div>
</template>

<script>
import CircleCard from '@/components/cards/CircleCard.vue';
import SkeletonBlock from '@/components/state/SkeletonBlock.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import { fetchCirclesByRole } from '@/api/mockService';

export default {
  name: 'Circles',
  components: { CircleCard, SkeletonBlock, EmptyState },
  data() {
    return {
      tags: ['应届生专属', '职场新人', '行业交流', '考公/考编', '求职专项'],
      loading: true,
      circles: [],
      keyword: '',
      selectedTag: ''
    };
  },
  computed: {
    identityTag() {
      return this.$store.state.identityTag;
    },
    headline() {
      const map = {
        学生: '秋招/实习互助小组',
        职场菜鸟: '入职适应与成长互助圈',
        专家: '专家答疑/创作者圈',
        职场老手: '行业交流与管理进阶圈'
      };
      return map[this.identityTag] || '按群体与行业划分的小组';
    },
    displayTags() {
      const map = {
        学生: ['应届生专属', '实习互助', '校招打卡', 'offer选择', '考研/考公'],
        职场菜鸟: ['职场新人', '试用期', '沟通反馈', '效率工具', '转正'],
        专家: ['专家创作', '答疑主持', '榜单交流', '私信咨询', '创作工具'],
        职场老手: ['行业交流', '管理进阶', '跳槽', '职场心理', '招聘/内推']
      };
      return map[this.identityTag] || this.tags;
    },
    displayCircles() {
      const kw = this.keyword.trim();
      const tag = this.selectedTag;
      let list = this.circles;
      if (tag) {
        list = list.filter(
          (c) =>
            (c.badge && c.badge.includes(tag)) ||
            (c.title && c.title.includes(tag)) ||
            (c.desc && c.desc.includes(tag))
        );
      }
      if (!kw) return list;
      return list.filter((c) => c.title.includes(kw) || (c.desc || '').includes(kw));
    }
  },
  watch: {
    identityTag: {
      immediate: true,
      handler() {
        this.loadCircles();
      }
    }
  },
  methods: {
    doSearch() {
      this.keyword = this.keyword.trim();
    },
    selectTag(tag) {
      this.selectedTag = this.selectedTag === tag ? '' : tag;
    },
    async loadCircles() {
      this.loading = true;
      const identity = this.identityTag || '默认';
      this.circles = await fetchCirclesByRole(identity);
      this.loading = false;
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
  font-size: 20px;
}

.tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-group {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 6px;
}

.search-input {
  border: none;
  border-radius: 8px;
  padding: 10px 12px;
  min-width: 240px;
  background: #f6f7fb;
}

.search-input:focus {
  outline: none;
}

.search-btn {
  border: none;
  background: var(--blue);
  color: #fff;
  border-radius: 10px;
  padding: 10px 14px;
  cursor: pointer;
  font-weight: 600;
}

.primary-btn {
  background: var(--blue);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
}

.create-btn {
  background: var(--orange);
  box-shadow: 0 8px 18px rgba(245, 158, 11, 0.28);
}

.tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  border: 1px solid var(--gray-200);
  padding: 8px 12px;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
}

.tag.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.circle-card .card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.circle-card h3 {
  margin: 0;
  font-size: 16px;
}

.badge {
  padding: 4px 8px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--blue);
  font-size: 12px;
}

.desc {
  margin: 8px 0;
  color: var(--gray-700);
}

.meta {
  color: var(--gray-500);
  font-size: 13px;
}

.actions {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
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

  .tools {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .search-group {
    width: 100%;
  }

  .search-input {
    flex: 1;
    min-width: 0;
  }

  .create-btn {
    width: 100%;
    text-align: center;
  }
}
</style>
