<template>
  <div class="page topic-detail">
    <section class="card topic-hero">
      <div class="hero-header">
        <div>
          <p class="eyebrow">话题详情</p>
          <h1>
            {{ topic.title }}
            <span class="rating-tag">{{ topic.rating }}</span>
          </h1>
        </div>
        <button class="collect-btn" @click="toggleCollect">
          {{ collected ? '已收藏' : '收藏话题' }}
        </button>
      </div>
      <p class="meta">{{ topic.meta }}</p>
      <p class="guide">分享你的经历，专家和前辈帮你分析</p>
      <div class="chips">
        <span v-for="tag in topic.tags" :key="tag" class="chip">{{ tag }}</span>
      </div>
    </section>

    <section class="card topic-content">
      <h3>话题内容</h3>
      <p class="body-text">{{ topic.body }}</p>
    </section>

    <section class="card feed full-width">
      <div class="section-header">
        <h3>讨论区</h3>
        <div class="filters">
          <button class="pill" :class="{ active: filter === 'mix' }" @click="filter = 'mix'">综合</button>
          <button class="pill" :class="{ active: filter === 'recent' }" @click="filter = 'recent'">最新</button>
        </div>
      </div>
      <div class="post" v-for="post in displayPosts" :key="post.id">
        <div class="post-meta">
          <div class="name">{{ post.author }}</div>
          <div class="time">{{ post.time }} · 收藏 {{ post.fav }}</div>
        </div>
        <p class="post-body">{{ post.body }}</p>
        <div class="post-actions">
          <button class="ghost-btn" @click="act('评论')">评论</button>
          <button class="ghost-btn" @click="act('引用回复')">引用回复</button>
          <button class="ghost-btn" @click="act('点赞')">点赞</button>
          <button class="ghost-btn" @click="act('收藏')">收藏</button>
        </div>
      </div>
      <div class="related">
        <h4>相关优质回答</h4>
        <ul>
          <li v-for="item in related" :key="item.id">{{ item.title }} · {{ item.meta }}</li>
        </ul>
      </div>
    </section>

    <section class="card composer">
      <div class="compose-header">发布你的问题/经验</div>
      <textarea
        v-model.trim="form.body"
        rows="4"
        placeholder="输入文字，支持图片/表情（前端占位），提交进入审核"
      ></textarea>
      <div class="compose-actions">
        <div class="tips">标签上限 3 · 违规会进入人工审核</div>
        <div class="action-btns">
          <button class="ghost-btn" @click="insertImage">插入图片</button>
          <button class="primary-btn" @click="submitPost" :disabled="sending">
            {{ sending ? '提交中...' : '提交' }}
          </button>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>
  </div>
</template>

<script>
export default {
  name: 'TopicDetail',
  data() {
    return {
      topic: {
        title: '话题标题示例：第一份实习怎么选？',
        meta: '所属标签 · 参与 2.3k · 互动 8.6k · 最新回复 5 分钟前',
        rating: 'A级',
        body:
          '示例内容占位：第一份实习如何在大厂外包和中小正编之间选择？可以从成长空间、导师质量、具体工作内容和未来转正机会几个维度考量，也欢迎分享你的亲身经验。',
        tags: ['秋招面试', '第一份实习', '求职策略']
      },
      collected: false,
      form: {
        body: ''
      },
      sending: false,
      error: '',
      filter: 'mix',
      posts: [
        { id: 'p1', author: '用户1', time: '5 分钟前', fav: 12, body: '帖子正文占位，支持评论、引用回复、点赞、收藏' },
        { id: 'p2', author: '用户2', time: '10 分钟前', fav: 4, body: '第二条帖子示例，前端占位交互' },
        { id: 'p3', author: '用户3', time: '30 分钟前', fav: 9, body: '支持按最新排序，后端接入分页' }
      ],
      related: [
        { id: 'r1', title: '秋招面试经典问题', meta: '高赞回答 · A级' },
        { id: 'r2', title: '第一份实习避坑指南', meta: '采纳数 120' }
      ]
    };
  },
  computed: {
    displayPosts() {
      const list = [...this.posts];
      if (this.filter === 'recent') {
        return list.reverse();
      }
      return list;
    }
  },
  methods: {
    submitPost() {
      if (this.sending) return;
      if (!this.form.body || this.form.body.length < 10) {
        this.error = '正文至少 10 个字';
        return;
      }
      this.error = '';
      this.sending = true;
      setTimeout(() => {
        this.posts.unshift({
          id: `new-${Date.now()}`,
          author: '我',
          time: '刚刚',
          fav: 0,
          body: this.form.body
        });
        this.form.body = '';
        this.sending = false;
        this.toast('提交成功，等待审核', 'success');
      }, 400);
    },
    insertImage() {
      this.toast('前端占位：调用上传组件', 'info');
    },
    act(action) {
      this.toast(`${action}占位，后端接入后生效`, 'info');
    },
    toggleCollect() {
      this.collected = !this.collected;
      this.toast(this.collected ? '已收藏该话题' : '已取消收藏', 'success');
    },
    toast(message, type = 'info') {
      const toast = this.$root && this.$root.$refs && this.$root.$refs.toast;
      if (toast && typeof toast.show === 'function') {
        toast.show(message, type);
      }
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

.topic-hero {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hero-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.eyebrow {
  margin: 0;
  color: var(--gray-700);
  font-size: 13px;
}

.topic-hero h1 {
  margin: 0;
  font-size: 22px;
}

.rating-tag {
  display: inline-block;
  margin-left: 10px;
  padding: 6px 10px;
  background: rgba(245, 158, 11, 0.12);
  color: var(--orange);
  border-radius: 10px;
  font-size: 14px;
  vertical-align: middle;
}

.meta {
  margin: 0;
  color: var(--gray-700);
}

.guide {
  margin: 0;
  color: var(--gray-900);
  font-weight: 600;
}

.chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.chip {
  border: 1px solid var(--gray-200);
  background: rgba(37, 99, 235, 0.06);
  color: var(--blue);
  border-radius: 10px;
  padding: 6px 10px;
  font-size: 12px;
}

.collect-btn {
  align-self: flex-start;
  border: 1px solid var(--blue);
  background: var(--blue);
  color: #fff;
  border-radius: 12px;
  padding: 10px 14px;
  font-weight: 700;
  cursor: pointer;
  min-width: 120px;
  text-align: center;
}

.topic-content h3 {
  margin: 0 0 10px;
}

.body-text {
  margin: 0;
  line-height: 1.7;
  color: var(--gray-800);
}

.composer textarea {
  width: 100%;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  font-size: 14px;
  outline: none;
  resize: vertical;
}

.compose-header {
  font-weight: 700;
  margin-bottom: 8px;
}

.compose-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.tips {
  color: var(--gray-700);
  font-size: 13px;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.error {
  color: var(--red);
  margin: 6px 0 0;
  font-size: 13px;
}

.feed .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.filters {
  display: flex;
  gap: 8px;
}

.pill {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 6px 10px;
  cursor: pointer;
}

.pill.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.post {
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px;
  margin-top: 12px;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--gray-700);
}

.post-body {
  margin: 8px 0;
  color: var(--gray-900);
}

.post-actions {
  display: flex;
  gap: 8px;
}

.related {
  margin-top: 14px;
  padding-top: 10px;
  border-top: 1px dashed var(--gray-200);
}

.related h4 {
  margin: 0 0 6px;
}

.related ul {
  margin: 0;
  padding-left: 18px;
  color: var(--gray-700);
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.primary-btn {
  border: 1px solid var(--blue);
  background: var(--blue);
  color: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}
</style>
