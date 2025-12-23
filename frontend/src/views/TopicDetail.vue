<template>
  <div class="page topic-detail">
    <loading-state v-if="loading" text="话题加载中..." />
    <template v-else>
      <template v-if="topic">
        <section class="card topic-hero">
          <div class="hero-header">
            <div>
              <p class="eyebrow">话题详情</p>
              <h1>
                {{ topic.title }}
                <span v-if="topic.rating" class="rating-tag">{{ topic.rating }}</span>
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
          <p class="body-text">{{ topic.body || '暂无详情' }}</p>
        </section>

        <section class="card feed full-width">
          <div class="section-header">
            <h3>讨论区</h3>
            <div class="filters">
              <button class="pill" :class="{ active: filter === 'mix' }" @click="filter = 'mix'">综合</button>
              <button class="pill" :class="{ active: filter === 'recent' }" @click="filter = 'recent'">最新</button>
            </div>
          </div>
          <template v-if="displayPosts.length">
            <div class="post" v-for="post in displayPosts" :key="post.id">
              <div class="post-meta">
                <div class="name">{{ post.author }}</div>
                <div class="time">{{ post.time }} · 赞 {{ post.fav }}</div>
              </div>
              <p class="post-body">{{ post.body }}</p>
              <div class="post-actions">
                <button class="ghost-btn" @click="prefillReply(post)">评论</button>
                <button class="ghost-btn" @click="quote(post)">引用回复</button>
                <button class="ghost-btn" @click="like(post)" :disabled="post.liking">
                  {{ post.liking ? '处理中...' : '点赞' }}
                </button>
                <button class="ghost-btn" @click="collect(post)">收藏</button>
              </div>
            </div>
          </template>
          <empty-state v-else title="暂无讨论" desc="发表第一条评论吧" />
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
      </template>
      <empty-state v-else title="话题不存在" desc="请返回话题广场重试" />
    </template>
  </div>
</template>

<script>
import LoadingState from '@/components/state/LoadingState.vue';
import EmptyState from '@/components/state/EmptyState.vue';
import { fetchTopicDetail, postTopicComment, toggleCommentLike } from '@/api/services/topic';

export default {
  name: 'TopicDetail',
  components: { LoadingState, EmptyState },
  data() {
    return {
      topic: null,
      collected: false,
      form: {
        body: ''
      },
      sending: false,
      error: '',
      filter: 'mix',
      posts: [],
      loading: false,
      pageNum: 1,
      pageSize: 10
    };
  },
  computed: {
    displayPosts() {
      const list = [...this.posts];
      if (this.filter === 'recent') {
        return list.sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0));
      }
      return list;
    }
  },
  watch: {
    '$route.params.id': {
      immediate: true,
      handler() {
        this.loadTopic();
      }
    }
  },
  methods: {
    async loadTopic() {
      const id = this.$route.params.id;
      if (!id) return;
      this.loading = true;
      try {
        const data = await fetchTopicDetail(id, { pageNum: this.pageNum, pageSize: this.pageSize });
        this.topic = this.normalizeTopic(data.topicInfo);
        this.posts = (data.comments?.list || []).map(this.normalizeComment);
        this.pageNum = data.comments?.pageNum || this.pageNum;
        this.pageSize = data.comments?.pageSize || this.pageSize;
      } catch (error) {
        console.error('[topic] 获取话题详情失败', error);
        this.topic = null;
        this.posts = [];
        this.$root.$refs.toast?.show('话题详情加载失败，请稍后重试', 'error');
      } finally {
        this.loading = false;
      }
    },
    normalizeTopic(info = {}) {
      if (!info) return null;
      const metaParts = [];
      const participants = info.participantCount ?? info.participants;
      const interactions = info.interactionCount ?? info.replyCount;
      if (participants !== undefined) metaParts.push(`参与 ${participants}`);
      if (interactions !== undefined) metaParts.push(`互动 ${interactions}`);
      if (info.latestReplyTime) metaParts.push(`最新回复 ${this.formatTime(info.latestReplyTime)}`);
      return {
        id: info.id,
        title: info.title || '话题详情',
        rating: info.level || '',
        body: info.intro || '',
        tags: info.tags || [],
        meta: metaParts.join(' · ')
      };
    },
    normalizeComment(item = {}) {
      const timestamp = item.publishTime ? new Date(item.publishTime).getTime() : Date.now();
      return {
        id: item.id || item.commentId || `c-${timestamp}`,
        author: item.nickname || '匿名用户',
        time: this.formatTime(item.publishTime),
        fav: item.likeCount || 0,
        body: item.content || '',
        timestamp,
        liking: false,
        isLike: false
      };
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
    async submitPost() {
      if (this.sending) return;
      if (!this.form.body || this.form.body.length < 10) {
        this.error = '正文至少 10 个字';
        return;
      }
      this.error = '';
      this.sending = true;
      const id = this.$route.params.id;
      try {
        const resp = await postTopicComment(id, { content: this.form.body });
        const statusText = resp?.status === 'pending' ? '评论提交成功，待审核' : '评论提交成功';
        const user = this.$store.state.userInfo;
        const newPost = {
          id: resp?.commentId || `new-${Date.now()}`,
          author: user?.nickname || '我',
          time: this.formatTime(new Date()),
          fav: 0,
          body: this.form.body,
          timestamp: Date.now(),
          liking: false
        };
        this.posts.unshift(newPost);
        this.form.body = '';
        this.toast(statusText, 'success');
      } catch (error) {
        console.error('[topic] 提交评论失败', error);
        this.toast('提交失败，请登录后重试', 'error');
      } finally {
        this.sending = false;
      }
    },
    async like(post) {
  if (!post || post.liking) return;

  post.liking = true;
  const nextIsLike = !post.isLike; // ⭐ 关键：取反

  try {
    const result = await toggleCommentLike(post.id, nextIsLike);

    // 更新状态
    post.isLike = nextIsLike;
    post.fav = result?.likeCount ?? (
      nextIsLike ? post.fav + 1 : Math.max(0, post.fav - 1)
    );

    this.toast(nextIsLike ? '已点赞' : '已取消点赞', 'success');
  } catch (error) {
    console.error('[topic] 点赞/取消失败', error);
    this.toast('操作失败，请稍后再试', 'error');
  } finally {
    post.liking = false;
  }
}
,
    prefillReply(post) {
      if (!post) return;
      this.form.body = `@${post.author} `;
    },
    quote(post) {
      if (!post) return;
      this.form.body = `${this.form.body || ''}\n> ${post.body}`;
    },
    collect() {
      this.toast('收藏功能将在后端接口接入后启用', 'info');
    },
    insertImage() {
      this.toast('前端占位：调用上传组件', 'info');
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
