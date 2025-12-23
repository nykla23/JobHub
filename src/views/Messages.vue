<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">消息中心</p>
        <h1>互动通知 / 系统通知 / 圈子通知</h1>
      </div>
      <button class="ghost-btn" @click="markAllRead">
        全部标为已读
      </button>
    </header>

    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab"
        :class="['tab', { active: activeTab === tab }]"
        @click="onTabChange(tab)"
      >
        {{ tab }}
      </button>
    </div>

    <section class="card">
      <ul class="list">
        <li
          v-for="item in displayMessages"
          :key="item.id"
          @click="markRead(item)"
        >
          <div class="title">{{ activeTab }} 消息 {{ item.id }}</div>
          <div class="desc">
            未读红点 · hover 预览 · 点击进入对应场景。
          </div>
        </li>
      </ul>
    </section>
  </div>
</template>

<script>
import {
  fetchMessages,
  markMessagesRead,
  markAllMessagesRead
} from '@/api/services/message';

export default {
  name: 'Messages',
  data() {
    return {
      tabs: ['互动通知', '系统通知', '圈子通知'],
      activeTab: '互动通知',
      messages: []
    };
  },

  computed: {
    /* 根据当前 tab 显示消息（不改 UI，只换数据） */
    displayMessages() {
      return this.messages;
    }
  },

  created() {
    this.loadMessages();
  },

  methods: {
    /* 切换 tab */
    onTabChange(tab) {
      this.activeTab = tab;
      this.loadMessages(); // ⭐ 切 tab 重新拉接口
    },

    /* 拉消息列表 */
    async loadMessages() {
      const res = await fetchMessages({
        type: this.activeTab
      });
      this.messages = res.list || [];
    },

    /* 点击消息 → 标记已读 */
    async markRead(item) {
      await markMessagesRead([item.id]); // ⭐ 必然产生 Network
      this.loadMessages();
    },

    /* 全部已读 */
    async markAllRead() {
      await markAllMessagesRead(); // ⭐ 必然产生 Network
      this.loadMessages();
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
  justify-content: space-between;
  align-items: center;
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

.tabs {
  display: flex;
  gap: 8px;
}

.tab {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.tab.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.list {
  margin: 0;
  padding-left: 18px;
  color: var(--gray-700);
}

.title {
  font-weight: 700;
}

.desc {
  margin: 2px 0 12px;
  color: var(--gray-500);
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}
</style>
