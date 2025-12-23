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
import { fetchGroupDetail, joinOrQuitGroup } from '@/api/services/group';

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
      posts: [],
      resources: [],
      notices: [],
      loading: false
    };
  },
  async created() {
    await this.loadCircle();
  },
  methods: {
    async loadCircle() {
      const id = this.$route.params.id;
      if (!id) return;
      this.loading = true;
      try {
        const data = await fetchGroupDetail(id);
        const info = data.groupInfo || {};
        this.circle = {
          id: info.id,
          title: info.name || '小组名称',
          badge: info.tags?.[0] || info.activityType || '兴趣圈',
          desc: info.intro || '',
          meta: info.memberCount ?? 0 
        };
        this.joined = Boolean(info.isJoined);
        this.posts = (data.groupDynamic?.list || []).map((item) => ({
          id: item.id,
          title: item.title || item.nickname || '动态',
          body: item.content || '',
          time: this.formatTime(item.publishTime),
          comments: item.commentCount || 0,
          fav: item.likeCount || 0
        }));
        this.resources = (data.groupResource?.list || []).map((item) => ({
          id: item.id,
          title: item.title,
          owner: item.uploader,
          downloads: item.downloadCount || 0
        }));
        this.notices = (data.groupNotice?.list || []).map((item) => ({
          id: item.id,
          title: item.title,
          time: this.formatTime(item.publishTime)
        }));
      } catch (error) {
        console.error('[circle] 获取小组详情失败', error);
        this.$root.$refs.toast?.show('加载小组详情失败，请稍后重试', 'error');
      } finally {
        this.loading = false;
      }
    },
    formatTime(value) {
      if (!value) return '';
      try {
        const date = typeof value === 'string' ? new Date(value) : value;
        return new Intl.DateTimeFormat('zh-CN', {
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }).format(date);
      } catch (e) {
        return value;
      }
    },
    async toggleJoin() {
      const id = this.$route.params.id;
      try {
        await joinOrQuitGroup(id, this.joined ? 'quit' : 'join');
        this.joined = !this.joined;
        this.$root.$refs.toast?.show(this.joined ? '加入小组成功' : '已退出小组', 'success');
      } catch (error) {
        console.error('[circle] 更新小组状态失败', error);
        this.$root.$refs.toast?.show('操作失败，请稍后再试', 'error');
      }
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
