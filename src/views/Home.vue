<template>
  <div class="page home-page">

    <!-- =======================
          轮播图（使用 banners 图片）
    ======================= -->
    <section class="card carousel-card">
      <header class="section-header">
        <h2>热门内容与活动</h2>
      </header>

      <div v-if="overviewLoading && !slides.length">
        <skeleton-block height="180px" />
      </div>

      <div v-else-if="slides.length" class="carousel">
        <div class="slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">

          <div v-for="(slide, idx) in slides" :key="slide.title" class="slide">

            <!-- 背景图 -->
            <div
              class="slide-bg"
              :style="{ backgroundImage: `url(${slide.imageUrl})` }"
            ></div>

            <!-- 文本内容 -->
            <div class="slide-body">
              <p class="eyebrow">{{ slide.tag }}</p>
              <h3>{{ slide.title }}</h3>
              <p class="desc">{{ slide.desc }}</p>
              <button class="ghost-btn" @click="openLink(slide)">查看详情</button>
            </div>

            <div class="badge">{{ idx + 1 }}/{{ slides.length }}</div>
          </div>
        </div>

        <!-- 小点点 -->
        <div class="dots">
          <button
            v-for="(slide, idx) in slides"
            :key="slide.title"
            :class="['dot', { active: currentSlide === idx }]"
            @click="currentSlide = idx"
          ></button>
        </div>
      </div>

      <div v-else class="empty-placeholder">
        <empty-state title="暂无轮播" desc="稍后自动加载" />
      </div>
    </section>

    <!-- =======================
          热点活动
    ======================= -->
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

    <!-- =======================
          推荐内容
    ======================= -->
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
import { mapState } from "vuex";
import ContentCard from "@/components/cards/ContentCard.vue";
import LoadingState from "@/components/state/LoadingState.vue";
import SkeletonBlock from "@/components/state/SkeletonBlock.vue";
import EmptyState from "@/components/state/EmptyState.vue";
import { getHomeOverview, refreshRecommendFeed } from "@/api/services/home";

export default {
  name: "Home",
  components: { ContentCard, LoadingState, SkeletonBlock, EmptyState },

  data() {
    return {
      refreshKey: 0,
      recommendPage: 1,
      pageSize: 2,
      overviewLoading: false,
      recommendLoading: false,
      recommendedList: [],

      /** 轮播图使用本地 banners 图片 */
      slides: [
        {
          title: "校园活动宣传",
          tag: "热门",
          desc: "",
          link: "",
          imageUrl: "/banners/1.jpg"
        },
        {
          title: "简历模板推荐",
          tag: "推荐",
          desc: "",
          link: "",
          imageUrl: "/banners/2.jpg"
        },
        {
          title: "求职面试指导",
          tag: "职场",
          desc: "",
          link: "",
          imageUrl: "/banners/3.jpg"
        }
      ],

      currentSlide: 0,
      slideTimer: null,

      /** 活动 */
      hotActivities: []
    };
  },

  computed: {
    ...mapState(["identityTag"])
  },

  mounted() {
    this.fetchHomeData();
    this.startAutoSlide();
  },

  beforeUnmount() {
    this.stopAutoSlide();
  },

  methods: {
    /** 获取首页其他数据 */
    async fetchHomeData() {
  this.overviewLoading = true;
  this.recommendLoading = true;

  try {
    const data = await getHomeOverview({ pageNum: 1 });

    // 热点活动
    this.hotActivities = data.hotActivities || [];

    // 话题推荐（2 条）
    this.recommendedList = (data.topicRecommend || []).map(t => ({
      tag: t.level || "话题",
      title: t.title,
      desc: t.tag,
      author: "话题",
      stats: `${t.participantCount || 0} 人参与 · ${t.interactiveCount || 0} 互动`,
      link: `/topic/${t.id}`,
      type: "topic"
    }));

    this.recommendPage = 1;
    this.refreshKey++;
  } catch (err) {
    console.error("[home] 获取首页数据失败", err);
  } finally {
    this.overviewLoading = false;
    this.recommendLoading = false;
  }
},


    /** 刷新推荐 */
    async refresh() {
  this.recommendLoading = true;

  try {
    const data = await getHomeOverview({ identity: this.identityTag });

    this.recommendedList = (data.topicRecommend || []).map(t => ({
      tag: t.level || '话题',
      title: t.title,
      desc: t.tag,
      author: '话题',
      stats: `${t.participantCount || 0} 人参与 · ${t.interactiveCount || 0} 互动`,
      link: `/topic/${t.id}`,
      type: 'topic'
    }));

    this.refreshKey++;
  } catch (err) {
    console.error("[home] 换一批推荐失败", err);
  } finally {
    this.recommendLoading = false;
  }
}

,

    /** 推荐内容格式适配 */
    adaptRecommendList(list) {
      return list.map((item) => {
        let tag = item.type || "推荐";
        const typeLower = tag.toLowerCase();

        if (typeLower.includes("article")) tag = "内容";
        else if (typeLower.includes("video") || typeLower.includes("expert"))
          tag = "专家资源";
        else if (typeLower.includes("group")) tag = "圈子";
        else if (typeLower.includes("topic")) tag = "话题";

        return {
          tag,
          title: item.title,
          desc: item.desc || item.summary || "",
          author: item.author || "JobHub",
          stats:
            item.likeCount || item.collectCount
              ? `${item.likeCount || 0} 赞 · ${item.collectCount || 0} 收藏`
              : item.publishTime || "",
          link: item.link,
          type: item.type
        };
      });
    },

    /** 打开链接 */
    openLink(item) {
  if (!item) return;

  // ⭐ 1️⃣ 只要后端给了 link，直接用
  if (item.link) {
    // 外链
    if (item.link.startsWith("http")) {
      window.open(item.link, "_blank");
    } else {
      this.$router.push(item.link);
    }
    return;
  }

  // 2️⃣ 兜底逻辑（几乎不会再用到）
  const t = (item.type || "").toLowerCase();
  if (t.includes("group")) {
    this.$router.push("/circles");
  } else if (t.includes("video") || t.includes("expert") || t.includes("pro")) {
    this.$router.push("/pro");
  } else {
    this.$router.push("/search");
  }
},

    /** 活动描述字段 */
    formatActivityMeta(activity) {
      const head = activity.time || "时间待定";
      const participants = activity.participantCount
        ? `${activity.participantCount} 人报名`
        : "立即报名";
      return `${head} · ${participants}`;
    },

    /** 自动轮播 */
    startAutoSlide() {
      this.stopAutoSlide();
      this.slideTimer = setInterval(() => {
        this.currentSlide = (this.currentSlide + 1) % this.slides.length;
      }, 4000);
    },

    stopAutoSlide() {
      if (this.slideTimer) clearInterval(this.slideTimer);
      this.slideTimer = null;
    }
  }
};
</script>

<style scoped>
/* ---- 通用布局 ---- */
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

/* ---- 轮播图 ---- */
.carousel {
  position: relative;
  overflow: hidden;
}

.slides {
  display: flex;
  transition: transform 0.4s ease;
}

.slide {
  position: relative;
  min-width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
}

.slide-bg {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: brightness(0.8);
  z-index: 1;
}

.slide-body {
  position: relative;
  z-index: 2;
  color: white;
  padding: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.4);
}

.badge {
  position: absolute;
  top: 10px;
  right: 12px;
  z-index: 2;
  background: rgba(0, 0, 0, 0.25);
  color: white;
  padding: 4px 10px;
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
  background: #ddd;
  cursor: pointer;
}

.dot.active {
  background: var(--blue);
}
/* =====================
     推荐栏布局修复
   ===================== */
.recommend-card .section-header {
  display: flex;
  justify-content: space-between; /* 左右布局 */
  align-items: center;
  gap: 10px;
}

.recommend-card h2 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
}

.recommend-card .actions {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap; /* 防止换行 */
}

.recommend-card .actions .ghost-btn {
  padding: 6px 12px;
  font-size: 14px;
}
/* =====================
     推荐栏按钮圆角修复
   ===================== */
.recommend-card .actions .ghost-btn {
  border: 1px solid var(--gray-300);
  background: #fff;
  padding: 8px 14px;
  border-radius: 12px; /* ★ 圆角按钮 */
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.recommend-card .actions .ghost-btn:hover {
  background: var(--gray-100);
}

</style>
