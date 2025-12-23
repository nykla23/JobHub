<template>
  <div class="page activity-detail">
    <loading-state v-if="loading" text="活动加载中..." />
    <template v-else-if="activity">
      <section class="card hero">
        <div>
          <p class="eyebrow">热点活动</p>
          <h1>{{ activity.title }}</h1>
          <p class="meta">{{ activity.time }} · 参与 {{ activity.participantCount || 0 }}</p>
          <p class="desc">{{ activity.desc }}</p>
        </div>
        <div class="actions">
          <button class="ghost-btn" @click="$router.push('/')">返回首页</button>
          <button class="primary-btn" @click="notify">立即报名</button>
        </div>
      </section>

      <section class="card body-card">
        <h3>活动介绍</h3>
        <p class="body-text">{{ activity.body }}</p>
        <ul class="highlights">
          <li v-for="point in activity.highlights" :key="point">{{ point }}</li>
        </ul>
      </section>
    </template>
    <empty-state v-else title="未找到活动" desc="请返回首页查看最新活动" />
  </div>
</template>

<script>
import LoadingState from '@/components/state/LoadingState.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import { fetchActivityDetail, getHomeOverview } from '@/api';

export default {
  name: 'ActivityDetail',
  components: { LoadingState, EmptyState },
  data() {
    return {
      loading: false,
      activity: null
    };
  },
  created() {
    this.loadActivity();
  },
  methods: {
    async loadActivity() {
      const id = this.$route.params.id;
      if (!id) return;
      this.loading = true;
      try {
        const detail = await fetchActivityDetail(id);
        this.activity = this.normalizeActivity(detail);
      } catch (primaryError) {
        console.warn('[activity] 详情接口不可用，回退到首页数据', primaryError);
        try {
          const homeData = await getHomeOverview();
          const all = [...(homeData.hotActivities || []), ...(homeData.carousel || [])];
          const matched = all.find(
            (item) => String(item.id || item.activityId) === String(id)
          );
          this.activity = this.normalizeActivity(matched);
        } catch (fallbackError) {
          console.error('[activity] 无法获取活动详情', fallbackError);
          this.activity = null;
        }
      } finally {
        this.loading = false;
      }
    },
    normalizeActivity(raw) {
      if (!raw) return null;
      const start = raw.startTime || raw.time || raw.beginTime;
      const end = raw.endTime || raw.finishTime;
      const time = start && end ? `${start} - ${end}` : start || '时间待定';
      const highlights = raw.highlights || raw.tags || [];
      return {
        title: raw.title || raw.name || '热点活动',
        desc: raw.desc || raw.description || raw.intro || '',
        body: raw.body || raw.content || raw.detail || '',
        time,
        participantCount: raw.participantCount || raw.participants || raw.memberCount || 0,
        highlights: Array.isArray(highlights) ? highlights : [],
        link: raw.link
      };
    },
    notify() {
      this.$root.$refs.toast?.show('报名通道已切换为后端接口，请关注后端响应', 'info');
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

.card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

h1 {
  margin: 4px 0 6px;
}

.meta {
  margin: 0 0 6px;
  color: var(--gray-700);
}

.desc {
  margin: 0;
  color: var(--gray-800);
}

.actions {
  display: flex;
  gap: 10px;
}

.body-card h3 {
  margin: 0 0 10px;
}

.body-text {
  margin: 0 0 8px;
  line-height: 1.6;
  color: var(--gray-800);
}

.highlights {
  margin: 0;
  padding-left: 18px;
  color: var(--gray-800);
  line-height: 1.6;
}

.ghost-btn,
.primary-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 10px 14px;
  cursor: pointer;
  font-weight: 600;
}

.primary-btn {
  background: var(--blue);
  border-color: var(--blue);
  color: #fff;
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .actions button {
    flex: 1;
  }
}
</style>
