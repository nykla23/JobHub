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
import { chatroomsMock } from '@/mock/data';
import { mapState } from 'vuex';

export default {
  name: 'Chatroom',
  data() {
    return {
      room: {
        title: '秋招求职聊天室',
        theme: '秋招求职交流',
        status: 'ongoing',
        statusLabel: '实时互动中',
        onlineCount: 1285,
        host: 'HR Jane',
        startTime: '19:00',
        endTime: '21:00'
      },
      messages: chatroomsMock.messages?.list || [
        {
          id: 1,
          nickname: 'HR Jane',
          content: '大家好，欢迎来到秋招求职聊天室，有问题可以直接提问~',
          sendTime: '19:00',
          isHost: true
        },
        {
          id: 2,
          nickname: '叶同学',
          content: '请问 HR，简历上的项目经历需要写多少个合适？',
          sendTime: '19:05',
          isHost: false
        }
      ],
      emojis: ['😀', '😍', '😎', '👍', '🔥'],
      input: ''
    };
  },
  computed: {
    ...mapState(['userInfo']),
    isAdmin() {
      return Boolean(this.userInfo && this.userInfo.isAdmin);
    }
  },
  created() {
    this.bindRoom();
  },
  methods: {
    bindRoom() {
      const id = this.$route.params.id;
      const live = chatroomsMock.live?.find((item) => item.id === id);
      const upcoming = chatroomsMock.upcoming?.find((item) => item.id === id);
      const ended = chatroomsMock.ended?.find((item) => item.id === id);
      const source = live || upcoming || ended;
      if (source) {
        this.room = {
          title: source.title,
          theme: source.desc || source.theme || '求职交流',
          status: live ? 'ongoing' : upcoming ? 'preview' : 'ended',
          statusLabel: live ? '实时互动中' : upcoming ? '聊天室预告' : '已结束',
          onlineCount: source.audience || 0,
          host: source.host || '主持人',
          startTime: source.time || '19:00',
          endTime: source.endTime || '21:00'
        };
      }
    },
    appendEmoji(emoji) {
      this.input += emoji;
    },
    send() {
      if (!this.input) return;
      this.messages.push({
        id: Date.now(),
        nickname: this.userInfo?.nickname || '我',
        content: this.input,
        sendTime: '刚刚',
        isHost: !!this.userInfo?.isAdmin
      });
      this.input = '';
      this.$nextTick(() => {
        const box = this.$refs.messages;
        if (box) box.scrollTop = box.scrollHeight;
      });
    },
    announce() {
      this.$root.$refs.toast?.show('预告创建成功（占位）', 'success');
    },
    pinMessage() {
      this.$root.$refs.toast?.show('已置顶最新消息（占位）', 'info');
    },
    generateNote() {
      this.$root.$refs.toast?.show('精华笔记生成中（占位）', 'info');
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

