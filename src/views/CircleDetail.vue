<template>
  <div class="page">
    <section class="card hero">
      <div>
        <p class="eyebrow">小组详情</p>
        <h1>{{ circle.title }}</h1>
        <p class="meta">标签：{{ circle.badge }} · 成员 {{ circle.meta || '人数占位' }}</p>
        <p class="desc">{{ circle.desc || '简介占位：每日打卡、简历互评、面试真题讨论' }}</p>
      </div>
      <div class="actions">
        <button class="ghost-btn" @click="toggleJoin">{{ joined ? '退出小组' : '加入小组' }}</button>
        <button class="primary-btn" @click="publish">发布动态</button>
      </div>
    </section>

    <div class="layout">
      <section class="card feed">
        <header class="section-header">
          <h3>小组动态</h3>
          <button class="ghost-btn" @click="publish">发布动态</button>
        </header>
        <div class="post" v-for="item in posts" :key="item.id">
          <div class="title">{{ item.title }}</div>
          <p class="desc">{{ item.body }}</p>
          <div class="meta">{{ item.time }} · 评论 {{ item.comments }} · 收藏 {{ item.fav }}</div>
        </div>
      </section>

      <aside class="side">
        <section class="card">
          <header class="section-header">
            <h4>小组资源库</h4>
            <button class="ghost-btn">上传资料</button>
          </header>
          <ul class="list">
            <li v-for="res in resources" :key="res.id">{{ res.title }} · 上传者 {{ res.owner }} · 下载 {{ res.downloads }}</li>
          </ul>
          <p class="hint">管理员审核后可见，支持免费下载。</p>
        </section>

        <section class="card">
          <header class="section-header">
            <h4>小组通知</h4>
            <button class="ghost-btn">发布通知</button>
          </header>
          <ul class="list">
            <li v-for="note in notices" :key="note.id">{{ note.title }} · {{ note.time }}</li>
          </ul>
        </section>

        <section class="card">
          <header class="section-header">
            <h4>成员管理（管理员视角）</h4>
          </header>
          <ul class="list">
            <li>成员列表：加入时间 / 发言次数 / 贡献值</li>
            <li>违规处理：禁言 1-7 天 / 移出小组</li>
            <li>入组审核：通过 / 拒绝（需理由）</li>
          </ul>
        </section>
      </aside>
    </div>
  </div>
</template>

<script>
import { fetchCirclesByRole } from '@/api/mockService';

export default {
  name: 'CircleDetail',
  data() {
    return {
      circle: {
        title: '小组名称',
        badge: '应届生',
        desc: '小组简介占位'
      },
      joined: false,
      posts: [
        { id: 'p1', title: '动态标题占位 1', body: '动态正文内容，支持评论/引用/点赞/收藏。', time: '5 分钟前', comments: 12, fav: 4 },
        { id: 'p2', title: '动态标题占位 2', body: '第二条动态示例，后端接入后替换。', time: '20 分钟前', comments: 8, fav: 3 }
      ],
      resources: [
        { id: 'r1', title: '简历模板 / 面试真题', owner: '上传者A', downloads: 120 },
        { id: 'r2', title: '产品案例拆解 PPT', owner: '上传者B', downloads: 86 }
      ],
      notices: [
        { id: 'n1', title: '活动/答疑预告推送到成员消息', time: '今天' },
        { id: 'n2', title: '请遵守发帖规范，违规将禁言', time: '昨天' }
      ]
    };
  },
  async created() {
    await this.loadCircle();
  },
  methods: {
    async loadCircle() {
      const identity = this.$store.state.identityTag || '默认';
      const list = await fetchCirclesByRole(identity);
      const id = this.$route.params.id;
      const found = (list || []).find((c) => c.id === id) || list[0];
      if (found) this.circle = found;
    },
    toggleJoin() {
      this.joined = !this.joined;
    },
    publish() {
      this.$emit('open-publish');
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
  align-items: center;
  gap: 12px;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.hero h1 {
  margin: 4px 0 6px;
}

.meta {
  margin: 0 0 6px;
  color: var(--gray-700);
}

.desc {
  margin: 0;
  color: var(--gray-900);
}

.actions {
  display: flex;
  gap: 8px;
}

.layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  align-items: start;
}

.feed .post {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  margin-top: 10px;
}

.post .title {
  font-weight: 700;
}

.post .meta {
  margin-top: 6px;
  color: var(--gray-500);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.list {
  margin: 10px 0 0;
  padding-left: 18px;
  color: var(--gray-700);
}

.hint {
  color: var(--gray-500);
  font-size: 12px;
}

.ghost-btn,
.primary-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.primary-btn {
  background: var(--blue);
  border-color: var(--blue);
  color: #fff;
}

@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .hero {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
