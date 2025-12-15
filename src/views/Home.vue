<template>
  <div class="page home-page">
    <section class="card carousel-card">
      <header class="section-header">
        <h2>热门直播与活动</h2>
        <small>自动轮播 + 手动切换</small>
      </header>
      <div v-if="overviewLoading && !slides.length">
        <skeleton-block height="180px" />
      </div>
      <div v-else-if="slides.length" class="carousel">
        <div class="slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
          <div v-for="(slide, idx) in slides" :key="slide.title" class="slide">
            <div class="slide-body">
              <p class="eyebrow">{{ slide.tag }}</p>
              <h3>{{ slide.title }}</h3>
              <p class="desc">{{ slide.desc }}</p>
              <button class="ghost-btn" @click="openLink(slide.link)">查看详情</button>
            </div>
            <div class="badge">{{ idx + 1 }}/{{ slides.length }}</div>
          </div>
        </div>
        <div class="dots">
          <button
            v-for="(slide, idx) in slides"
            :key="slide.title"
            :class="['dot', { active: currentSlide === idx }]"
            @click="currentSlide = idx"
            aria-label="切换轮播"
          ></button>
        </div>
      </div>
      <div v-else class="empty-placeholder">
        <empty-state title="暂无轮播" desc="稍后自动加载" />
      </div>
    </section>

    <section class="card hot-card" v-if="hotActivities.length || overviewLoading">
      <header class="section-header">
        <h2>热点活动</h2>
        <small class="hint">实时同步文档中的热门活动</small>
      </header>
      <div v-if="overviewLoading && !hotActivities.length" class="grid">
        <skeleton-block v-for="n in 3" :key="'hot-sk-' + n" height="120px" />
      </div>
      <div v-else class="hot-grid">
        <article v-for="activity in hotActivities" :key="activity.id" class="hot-item">
          <div>
            <p class="eyebrow">{{ activity.time }}</p>
            <h3>{{ activity.title }}</h3>
            <p class="hot-meta">{{ formatActivityMeta(activity) }}</p>
          </div>
          <button class="ghost-btn small" @click="openLink(activity.link)">了解详情</button>
        </article>
      </div>
    </section>

    <section class="card recommend-card">
      <header class="section-header">
        <h2>为你推荐 <small class="hint">基于身份：{{ identityTag || '未选择' }}</small></h2>
        <div class="actions">
          <loading-state v-if="recommendLoading" text="为你挑选中.." />
          <button v-else class="ghost-btn" @click="refresh">换一批</button>
        </div>
      </header>
      <div v-if="recommendLoading" class="grid">
        <skeleton-block v-for="n in pageSize" :key="'sk-' + n" height="160px" />
      </div>
      <div v-else-if="!recommendedList.length">
        <empty-state title="暂无推荐" desc="换个身份或稍后再试">
          <button class="ghost-btn" @click="refresh">重新加载</button>
        </empty-state>
      </div>
      <div v-else class="grid">
        <content-card
          v-for="item in recommendedList"
          :key="item.title + '-' + refreshKey"
          :item="item"
          @select="openLink(item)"
        />
      </div>
    </section>

  </div>
</template>

<script>
import { mapState } from 'vuex';
import ContentCard from '@/components/cards/ContentCard.vue';
import LoadingState from '@/components/state/LoadingState.vue';
import SkeletonBlock from '@/components/state/SkeletonBlock.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import { getHomeOverview, refreshRecommendFeed } from '@/api/services/home';

export default {
  name: 'Home',
  components: { ContentCard, LoadingState, SkeletonBlock, EmptyState },
  data() {
    return {
      refreshKey: 0,
      recommendPage: 1,
      pageSize: 5,
      overviewLoading: false,
      recommendLoading: false,
      recommendedList: [],
      slides: [],
      hotActivities: [],
      currentSlide: 0,
      slideTimer: null
    };
  },
  computed: {
    ...mapState(['identityTag'])
  },
  mounted() {
    this.fetchHomeData();
  },
  beforeDestroy() {
    this.stopAutoSlide();
  },
  watch: {
    identityTag(next) {
      this.fetchHomeData();
    }
  },
  methods: {
    async fetchHomeData() {
      this.overviewLoading = true;
      this.recommendLoading = true;
      try {
        const data = await getHomeOverview({ identity: this.identityTag });
        this.slides = data.carousel || [];
        this.hotActivities = data.hotActivities || [];
        const recommended = data.recommendedContent || {};
        this.recommendedList = this.adaptRecommendList(recommended.list || []);
        this.recommendPage = recommended.pageNum || 1;
        this.pageSize = recommended.pageSize || this.pageSize;
        this.refreshKey += 1;
      } catch (error) {
        console.error('[home] 获取首页数据失败', error);
        this.slides = [];
        this.hotActivities = [];
        this.recommendedList = [];
      } finally {
        this.overviewLoading = false;
        this.recommendLoading = false;
        if (this.slides.length) {
          this.startAutoSlide();
        } else {
          this.stopAutoSlide();
        }
      }
    },
    async refresh() {
      this.recommendLoading = true;
      const nextPage = this.recommendPage + 1;
      try {
        const data = await refreshRecommendFeed({
          identity: this.identityTag,
          pageNum: nextPage,
          pageSize: this.pageSize
        });
        this.recommendedList = this.adaptRecommendList(data.list || []);
        this.recommendPage = data.pageNum || nextPage;
        this.refreshKey += 1;
      } catch (error) {
        console.error('[home] 换一批推荐失败', error);
      } finally {
        this.recommendLoading = false;
      }
    },
    adaptRecommendList(list) {
      return list.map((item) => {
        let tag = item.type || '推荐';
        if (tag === 'article') tag = '内容';
        if (tag === 'video') tag = '专家资源';
        if (tag === 'group') tag = '圈子';
        if (tag === 'topic') tag = '话题';
        return {
          tag,
          title: item.title,
          desc: item.desc || '',
          author: item.author || 'JobHub',
          stats:
            item.likeCount || item.collectCount
              ? `${item.likeCount || 0} 赞 · ${item.collectCount || 0} 收藏`
              : item.publishTime || '',
          link: item.link,
          type: item.type
        };
      });
    },
    openLink(item) {
      if (!item) {
        this.$root.$refs.toast?.show('暂无链接', 'info');
        return;
      }
      let target = item.link || '';
      if (target && target.startsWith('/api')) {
        target = '';
      }
      if (!target) {
        const t = (item.type || item.tag || '').toLowerCase();
        if (t.includes('topic') || t.includes('话题')) target = '/topics';
        else if (t.includes('group') || t.includes('圈')) target = '/circles';
        else if (t.includes('video') || t.includes('expert') || t.includes('pro') || t.includes('资源')) target = '/pro';
        else target = '/search';
      }
      if (target.startsWith('http')) {
        window.open(target, '_blank');
        return;
      }
      this.$router.push(target);
    },
    formatActivityMeta(activity) {
      const head = activity.time || '时间待定';
      const participants = activity.participantCount ? `${activity.participantCount} 人报名` : '立即报名';
      return `${head} · ${participants}`;
    },
    startAutoSlide() {
      this.stopAutoSlide();
      if (!this.slides.length) return;
      this.slideTimer = setInterval(() => {
        this.currentSlide = (this.currentSlide + 1) % this.slides.length;
      }, 4000);
    },
    stopAutoSlide() {
      if (this.slideTimer) {
        clearInterval(this.slideTimer);
        this.slideTimer = null;
      }
    }
  }
};
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.06);
}

.identity-banner {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(245, 158, 11, 0.08));
  border: 1px dashed var(--gray-200);
  border-radius: 16px;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: center;
}

.eyebrow {
  margin: 0 0 4px;
  color: var(--gray-700);
  font-size: 13px;
}

.identity-banner h1 {
  margin: 0;
  font-size: 22px;
}

.sub {
  margin: 6px 0 10px;
  color: var(--gray-700);
  font-size: 14px;
}

.chips {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.chip {
  border: 1px solid var(--gray-200);
  background: #fff;
  padding: 8px 12px;
  border-radius: 10px;
  cursor: pointer;
}

.chip.ghost {
  background: transparent;
}

.chip.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.primary-btn {
  background: var(--blue);
  color: #fff;
  border: none;
  padding: 12px 16px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 10px;
}

.section-header h2 {
  margin: 0;
  font-size: 18px;
  display: inline-flex;
  gap: 8px;
  align-items: center;
}

.hint {
  color: var(--gray-500);
  font-size: 12px;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.carousel-card {
  overflow: hidden;
}

.carousel {
  position: relative;
  overflow: hidden;
}

.slides {
  display: flex;
  transition: transform 0.4s ease;
}

.slide {
  min-width: 100%;
  padding: 14px;
  border: 1px dashed var(--gray-200);
  border-radius: 12px;
  background: var(--gray-50);
  position: relative;
}

.slide-body h3 {
  margin: 4px 0 6px;
}

.slide-body .desc {
  margin: 0 0 10px;
  color: var(--gray-700);
}

.badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(37, 99, 235, 0.1);
  color: var(--blue);
  padding: 4px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.dots {
  display: flex;
  gap: 6px;
  justify-content: center;
  margin-top: 8px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 1px solid var(--gray-300);
  background: #fff;
  cursor: pointer;
}

.dot.active {
  background: var(--blue);
  border-color: var(--blue);
}

.empty-placeholder {
  border: 1px dashed var(--gray-200);
  border-radius: 12px;
  padding: 16px;
}

.hot-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}

.hot-item {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background: #fff;
}

.hot-item h3 {
  margin: 4px 0 6px;
  font-size: 16px;
}

.hot-meta {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.ghost-btn.small {
  align-self: flex-start;
  padding: 6px 10px;
  font-size: 13px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.identity-mask {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 30;
  padding: 16px;
}

.identity-card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  width: min(720px, 100%);
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.16);
}

.id-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.id-header h2 {
  margin: 4px 0 6px;
}

.close-btn {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
}

.role-chips {
  margin: 14px 0;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
  align-items: center;
}

.actions .ghost-btn,
.actions .primary-btn {
  min-width: 120px;
}

.hint {
  margin: 6px 0 0;
  color: var(--gray-500);
  font-size: 12px;
}

@media (max-width: 768px) {
  .identity-banner {
    grid-template-columns: 1fr;
  }

  .id-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
