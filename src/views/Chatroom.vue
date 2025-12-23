<template>
  <div class="chatroom-page">
    <router-link class="back-link" to="/topics">← 返回话题广场</router-link>
    <div class="layout">
      <section class="card chat-panel">
        <header class="chat-header">
          <div>
            <h2>{{ room.title }}</h2>
            <p class="subtitle">{{ room.theme }} · 主持人：{{ room.host }} · 状态：{{ room.status }}</p>
          </div>
          <span class="status-pill" :class="room.status">{{ room.statusLabel }}</span>
        </header>
        <div class="messages" ref="messages">
          <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ host: msg.isHost }">
            <div class="meta">
              <span class="name">{{ msg.nickname }}</span>
              <span class="time">{{ msg.sendTime }}</span>
            </div>
            <p class="body">{{ msg.content }}</p>
          </div>
        </div>
        <footer class="composer">
          <div class="emoji-row">
            <button v-for="emoji in emojis" :key="emoji" class="emoji" @click="appendEmoji(emoji)">
              {{ emoji }}
            </button>
          </div>
          <div class="input-row">
            <input v-model.trim="input" placeholder="说点什么..." />
            <button class="primary-btn" @click="send" :disabled="!input">发送</button>
          </div>
        </footer>
      </section>
      <aside class="card info-card">
        <div class="info-block">
          <h3>聊天室信息</h3>
          <p>状态：{{ room.statusLabel }}</p>
          <p>在线人数：{{ room.onlineCount }}</p>
          <p>主持人：{{ room.host }}</p>
          <p>主题：{{ room.theme }}</p>
          <p>时间：{{ room.startTime }} - {{ room.endTime }}</p>
        </div>
        <div class="info-block" v-if="isAdmin">
          <h3>管理员工具</h3>
          <button class="ghost-btn" @click="announce">创建聊天室预告</button>
          <button class="ghost-btn" @click="pinMessage">主持人置顶一条消息</button>
          <button class="ghost-btn" @click="generateNote">生成精华笔记</button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import {
  fetchChatroomDetail,
  generateChatroomNote,
  sendChatroomMessage
} from '@/api/services/topic';

export default {
  name: 'Chatroom',
  data() {
    return {
      room: {
        title: '聊天室',
        theme: '',
        status: 'preview',
        statusLabel: '加载中',
        onlineCount: 0,
        host: '',
        startTime: '',
        endTime: '',
        notice: ''
      },
      pinnedMessage: null,
      messages: [],
      emojis: ['😀', '😍', '😎', '👍', '🔥'],
      input: '',
      loading: false
    };
  },
  computed: {
    ...mapState(['userInfo']),
    isAdmin() {
      return Boolean(this.userInfo && this.userInfo.isAdmin);
    }
  },
  created() {
    this.loadRoom();
  },
  methods: {
    async loadRoom() {
      const id = this.$route.params.id;
      if (!id) return;
      this.loading = true;
      try {
        const data = await fetchChatroomDetail(id);
        const info = data.chatroomInfo || {};
        this.room = {
          title: info.title || '聊天室',
          theme: info.theme || info.desc || '',
          status: info.status || 'preview',
          statusLabel: this.mapStatus(info.status || 'preview'),
          onlineCount: info.onlineCount || 0,
          host: info.host || '主持人',
          startTime: this.formatTime(info.startTime),
          endTime: this.formatTime(info.endTime),
          notice: info.notice || ''
        };
        this.pinnedMessage = data.pinnedMessage || null;
        this.messages = (data.messages?.list || []).map(this.normalizeMessage);
      } catch (error) {
        console.error('[chatroom] 获取详情失败', error);
        this.$root.$refs.toast?.show('聊天室详情加载失败，请稍后重试', 'error');
      } finally {
        this.loading = false;
      }
    },
    mapStatus(status) {
      if (status === 'ongoing') return '实时互动中';
      if (status === 'ended') return '已结束';
      return '聊天室预告';
    },
    normalizeMessage(msg) {
      return {
        id: msg.id || msg.messageId || Date.now(),
        nickname: msg.nickname || msg.sender || '访客',
        content: msg.content || msg.body || '',
        sendTime: this.formatTime(msg.sendTime || msg.createdAt),
        isHost: Boolean(msg.isHost)
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
    appendEmoji(emoji) {
      this.input += emoji;
    },
    async send() {
      if (!this.input) return;
      const id = this.$route.params.id;
      try {
        const resp = await sendChatroomMessage(id, { content: this.input });
        const payload = resp?.message || resp;
        this.messages.push(this.normalizeMessage(payload));
        this.input = '';
        this.$nextTick(() => {
          const box = this.$refs.messages;
          if (box) box.scrollTop = box.scrollHeight;
        });
      } catch (error) {
        console.error('[chatroom] 发送消息失败', error);
        this.$root.$refs.toast?.show('发送失败，请稍后再试', 'error');
      }
    },
    async announce() {
      this.$root.$refs.toast?.show('聊天室公告将通过后端接口发布', 'info');
    },
    async pinMessage() {
      if (!this.messages.length) return;
      const latest = this.messages[this.messages.length - 1];
      this.pinnedMessage = latest;
      this.$root.$refs.toast?.show('已置顶最新消息', 'success');
    },
    async generateNote() {
      const id = this.$route.params.id;
      try {
        await generateChatroomNote(id);
        this.$root.$refs.toast?.show('精华笔记生成中，请稍后查看', 'info');
      } catch (error) {
        console.error('[chatroom] 生成笔记失败', error);
        this.$root.$refs.toast?.show('生成笔记失败', 'error');
      }
    }
  }
};
</script>

<style scoped>
.chatroom-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.back-link {
  color: var(--blue);
  font-weight: 600;
}

.layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.chat-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 520px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subtitle {
  margin: 4px 0 0;
  color: var(--gray-600);
}

.status-pill {
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 13px;
  background: rgba(16, 185, 129, 0.15);
  color: #047857;
}

.messages {
  flex: 1;
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  overflow-y: auto;
}

.message-item {
  margin-bottom: 16px;
}

.message-item.host .name {
  color: #854d0e;
}

.meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--gray-600);
}

.body {
  margin: 4px 0 0;
  font-size: 15px;
}

.composer {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.emoji-row {
  display: flex;
  gap: 8px;
}

.emoji {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
}

.input-row {
  display: flex;
  gap: 8px;
}

.input-row input {
  flex: 1;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 10px 14px;
}

.primary-btn {
  border: none;
  background: var(--blue);
  color: #fff;
  border-radius: 12px;
  padding: 10px 18px;
  cursor: pointer;
}

.info-card .info-block {
  border: 1px solid var(--gray-100);
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.ghost-btn {
  width: 100%;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 10px 12px;
  margin-top: 8px;
  background: #fff;
  cursor: pointer;
}

@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
  }
}
</style>

