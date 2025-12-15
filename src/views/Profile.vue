<template>
  <div class="profile-page">
    <section class="card hero-card">
      <div class="avatar">{{ avatarLetter }}</div>
      <div class="profile-info">
        <p class="eyebrow">个人主页</p>
        <h1>{{ profile.nickname || '未命名用户' }}</h1>
        <p class="stage-text">{{ profile.stage || profile.careerStage }}</p>
        <p class="intro">{{ profile.bio || profile.intro }}</p>
        <div class="focus-tags">
          <span v-for="tag in focusList" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>
      <div class="hero-actions">
        <router-link class="ghost-btn" to="/profile/edit">编辑信息</router-link>
        <router-link class="ghost-btn" to="/profile/settings">隐私设置</router-link>
      </div>
      <ul class="stats">
        <li>
          <p class="value">{{ stats.views || 0 }}</p>
          <p class="label">主页浏览</p>
        </li>
        <li>
          <p class="value">{{ stats.likes || profile.likes || 0 }}</p>
          <p class="label">获赞</p>
        </li>
        <li>
          <p class="value">{{ stats.followers || 0 }}</p>
          <p class="label">关注者</p>
        </li>
        <li>
          <p class="value">{{ stats.collections || 0 }}</p>
          <p class="label">收藏</p>
        </li>
      </ul>
    </section>

    <section class="card identity-card">
      <div>
        <p class="eyebrow">身份标签</p>
        <h2>{{ identityTag || '未选择' }}</h2>
        <p class="sub">用于精准推荐内容、话题和活动，后续可随时切换。</p>
      </div>
      <div class="identity-actions">
        <button class="ghost-btn" @click="openIdentity">切换身份</button>
      </div>
    </section>

    <div v-if="showIdentityModal" class="identity-mask" @click.self="closeIdentity">
      <div class="identity-dialog">
        <header class="dialog-header">
          <div>
            <p class="eyebrow">选择你的身份</p>
            <h3>学生 / 职场菜鸟 / 专家 / 职场老手</h3>
            <p class="sub">保存后写入本地，推荐内容将更精准</p>
          </div>
          <button class="close-btn" aria-label="关闭" @click="closeIdentity">×</button>
        </header>
        <div class="chips role-chips">
          <button
            v-for="role in roles"
            :key="role"
            :class="['chip', { active: pendingRole === role }]"
            @click="pendingRole = role"
          >
            {{ role }}
          </button>
        </div>
        <div class="dialog-actions">
          <button class="ghost-btn" @click="clearIdentity">暂不设置</button>
          <button class="ghost-btn" @click="confirmIdentity">保存</button>
        </div>
      </div>
    </div>

    <section class="card privacy-card">
      <div class="privacy-header">
        <h2>隐私设置</h2>
        <span>支持隐藏公司、薪资、历史内容等信息</span>
      </div>
      <div class="privacy-list">
        <label class="privacy-row">
          <div>
            <p class="title">隐藏具体公司</p>
            <p class="desc">展示行业/岗位，不展示具体所在企业</p>
          </div>
          <button class="switch" @click="togglePrivacy('hideCompany')">
            {{ settings.hideCompany ? '已隐藏' : '显示' }}
          </button>
        </label>
        <label class="privacy-row">
          <div>
            <p class="title">隐藏薪资范围</p>
            <p class="desc">薪资信息仅自己可见</p>
          </div>
          <button class="switch" @click="togglePrivacy('hideSalary')">
            {{ settings.hideSalary ? '已隐藏' : '显示' }}
          </button>
        </label>
        <label class="privacy-row">
          <div>
            <p class="title">隐藏历史发布</p>
            <p class="desc">隐藏后主页仅展示精选内容</p>
          </div>
          <button class="switch" @click="togglePrivacy('hideHistory')">
            {{ settings.hideHistory ? '已隐藏' : '显示' }}
          </button>
        </label>
      </div>
    </section>

    <section class="card data-card">
      <header class="data-header">
        <div>
          <h2>数据管理</h2>
        </div>
        <div class="tabs">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            :class="['tab', { active: activeTab === tab.value }]"
            @click="switchTab(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>
      </header>
      <div v-if="tabLoading" class="loading">加载中...</div>
      <template v-else>
        <!-- 浏览历史 -->
        <div v-if="activeTab === 'history'" class="data-list">
          <div class="list-header">
            <span>记录查看过的话题/内容/小组，支持单条删除或批量清除</span>
            <button v-if="currentTabList.length" class="ghost-btn small" @click="clearAllHistory">批量清除</button>
          </div>
          <div v-for="item in currentTabList" :key="item.id" class="history-item">
            <div class="item-content">
              <span class="item-type">{{ item.type || '内容' }}</span>
              <span class="item-title">{{ item.title }}</span>
              <span class="item-meta">{{ item.time }} · 来自:{{ item.source }}</span>
            </div>
            <div class="item-actions">
              <button class="ghost-btn small" @click="viewItem(item)">查看</button>
              <button class="ghost-btn small danger" @click="deleteHistory(item.id)">删除</button>
            </div>
          </div>
        </div>
        <!-- 我的圈子 -->
        <div v-else-if="activeTab === 'circles'" class="data-list">
          <div class="list-header">
            <span>展示已加入/创建的小组，支持快速进入、退出小组</span>
            <button class="ghost-btn small" @click="discoverCircles">发现更多小组</button>
          </div>
          <div v-for="item in currentTabList" :key="item.id" class="circle-item">
            <div class="item-content">
              <h4 class="item-title">{{ item.name }}</h4>
              <span class="item-role">{{ item.role }}</span>
              <span class="item-meta">成员数:{{ item.memberCount }} · {{ item.desc }}</span>
            </div>
            <div class="item-actions">
              <button class="ghost-btn small" @click="enterCircle(item.id)">进入小组</button>
              <button class="ghost-btn small danger" @click="exitCircle(item.id)">退出小组</button>
            </div>
          </div>
        </div>
        <!-- 我发布的 -->
        <div v-else-if="activeTab === 'publish'" class="data-list">
          <div class="list-header">
            <span>管理帖子：支持编辑、删除、置顶</span>
            <button class="primary-btn small" @click="publishNew">+ 发布新帖</button>
          </div>
          <div v-for="item in currentTabList" :key="item.id" class="publish-item">
            <div class="item-content">
              <div class="item-header">
                <h4 class="item-title">{{ item.title }}</h4>
                <div class="item-badges">
                  <span v-if="item.pinned" class="badge">已置顶</span>
                  <span v-if="item.private" class="badge">仅自己</span>
                </div>
              </div>
              <span class="item-meta">发布于: {{ item.publishTime }} · 获赞 {{ item.likeCount || 0 }}</span>
            </div>
            <div class="item-actions">
              <button class="ghost-btn small" @click="editPost(item.id)">编辑</button>
              <button v-if="item.pinned" class="ghost-btn small" @click="unpinPost(item.id)">取消置顶</button>
              <button v-else class="ghost-btn small" @click="pinPost(item.id)">置顶</button>
              <button class="ghost-btn small danger" @click="deletePost(item.id)">删除</button>
            </div>
          </div>
        </div>
        <!-- 我的收藏 -->
        <div v-else class="data-list collections-layout">
          <div class="collections-sidebar">
            <div class="list-header">
              <span>收藏夹</span>
              <button class="primary-btn small" @click="createFolder">+ 新建收藏夹</button>
            </div>
            <div class="folder-list">
              <div
                v-for="folder in folders"
                :key="folder.id"
                :class="['folder-item', { active: activeFolder === folder.id }]"
                @click="activeFolder = folder.id"
              >
                <span class="folder-icon">📁</span>
                <span class="folder-name">{{ folder.name }}</span>
                <span class="folder-count">({{ folder.count }})</span>
              </div>
            </div>
          </div>
          <div class="collections-content">
            <div class="list-header">
              <span>分类管理收藏的内容/话题/帖子，支持自定义收藏夹</span>
            </div>
            <div v-for="item in filteredCollections" :key="item.id" class="collection-item">
              <div class="item-content">
                <span class="item-type">{{ item.type || '内容' }}</span>
                <span class="item-title">{{ item.title }}</span>
              </div>
              <div class="item-actions">
                <button class="ghost-btn small" @click="viewItem(item)">查看</button>
                <button class="ghost-btn small danger" @click="uncollect(item.id)">取消收藏</button>
              </div>
            </div>
          </div>
        </div>
        <p v-if="!currentTabList.length && activeTab !== 'collections'" class="empty">暂无数据</p>
        <p v-if="activeTab === 'collections' && !filteredCollections.length" class="empty">该收藏夹暂无内容</p>
      </template>
    </section>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import { fetchHistory, fetchMyCircles, fetchMyCollections, fetchMyPublish } from '@/api/services/user';

const tabLoaders = {
  history: fetchHistory,
  circles: fetchMyCircles,
  publish: fetchMyPublish,
  collections: fetchMyCollections
};

export default {
  name: 'Profile',
  data() {
    return {
      roles: ['学生', '职场菜鸟', '专家', '职场老手'],
      pendingRole: null,
      showIdentityModal: false,
      stats: {},
      tabs: [
        { label: '浏览历史', value: 'history' },
        { label: '我的圈子', value: 'circles' },
        { label: '我发布的', value: 'publish' },
        { label: '我的收藏', value: 'collections' }
      ],
      activeTab: 'history',
      tabLoading: false,
      activeFolder: null,
      folders: [
        { id: 1, name: '简历相关', count: 2 },
        { id: 2, name: '面试技巧', count: 1 },
        { id: 3, name: '案例拆解', count: 1 },
        { id: 4, name: '未命名收藏夹', count: 0 }
      ],
      tabData: {
        history: [],
        circles: [],
        publish: [],
        collections: []
      }
    };
  },
  computed: {
    ...mapState(['profile', 'settings', 'identityTag']),
    avatarLetter() {
      return (this.profile.nickname || 'JH').slice(0, 1).toUpperCase();
    },
    focusList() {
      if (Array.isArray(this.profile.focus)) return this.profile.focus;
      if (typeof this.profile.focusArea === 'string') {
        return this.profile.focusArea.split('·').map((item) => item.trim());
      }
      return [];
    },
    currentTabList() {
      return this.tabData[this.activeTab] || [];
    },
    filteredCollections() {
      if (!this.activeFolder) return this.tabData.collections || [];
      return (this.tabData.collections || []).filter(item => item.folderId === this.activeFolder);
    }
  },
  created() {
    this.loadOverview();
    this.switchTab('history');
    this.activeFolder = this.folders[0]?.id || null;
    this.pendingRole = this.identityTag || this.roles[0];
  },
  methods: {
    ...mapMutations(['setIdentityTag']),
    async loadOverview() {
      const data = await this.$store.dispatch('fetchProfileOverview');
      this.stats = data.stats || {};
    },
    async switchTab(tab) {
      if (this.activeTab === tab && this.tabData[tab].length) return;
      this.activeTab = tab;
      this.tabLoading = true;
      try {
        const loader = tabLoaders[tab];
        const data = loader ? await loader() : { list: [] };
        this.tabData = {
          ...this.tabData,
          [tab]: data.list || []
        };
      } catch (error) {
        console.error('[profile] 获取数据失败', error);
      } finally {
        this.tabLoading = false;
      }
    },
    togglePrivacy(key) {
      this.$store.dispatch('savePrivacySettings', { [key]: !this.settings[key] });
    },
    formatStatus(status) {
      const map = {
        pending: '审核中',
        passed: '已通过',
        rejected: '已拒绝'
      };
      return map[status] || status;
    },
    viewItem(item) {
      if (item.link) {
        this.$router.push(item.link);
      } else {
        this.$root.$refs.toast?.show('查看详情', 'info');
      }
    },
    deleteHistory(id) {
      this.tabData.history = this.tabData.history.filter(item => item.id !== id);
      this.$root.$refs.toast?.show('已删除', 'success');
    },
    clearAllHistory() {
      this.tabData.history = [];
      this.$root.$refs.toast?.show('已清除全部历史', 'success');
    },
    discoverCircles() {
      this.$router.push('/circles');
    },
    enterCircle(id) {
      this.$router.push(`/circle/${id}`);
    },
    exitCircle(id) {
      this.tabData.circles = this.tabData.circles.filter(item => item.id !== id);
      this.$root.$refs.toast?.show('已退出小组', 'success');
    },
    publishNew() {
      this.$store.commit('setPublishOpen', true);
    },
    editPost(id) {
      this.$router.push(`/publish/edit/${id}`);
    },
    pinPost(id) {
      const item = this.tabData.publish.find(p => p.id === id);
      if (item) item.pinned = true;
      this.$root.$refs.toast?.show('已置顶', 'success');
    },
    unpinPost(id) {
      const item = this.tabData.publish.find(p => p.id === id);
      if (item) item.pinned = false;
      this.$root.$refs.toast?.show('已取消置顶', 'success');
    },
    deletePost(id) {
      this.tabData.publish = this.tabData.publish.filter(item => item.id !== id);
      this.$root.$refs.toast?.show('已删除', 'success');
    },
    createFolder() {
      const name = prompt('请输入收藏夹名称');
      if (name) {
        this.folders.push({ id: Date.now(), name, count: 0 });
        this.$root.$refs.toast?.show('收藏夹创建成功', 'success');
      }
    },
    uncollect(id) {
      this.tabData.collections = this.tabData.collections.filter(item => item.id !== id);
      this.$root.$refs.toast?.show('已取消收藏', 'success');
    },
    openIdentity() {
      this.showIdentityModal = true;
      this.pendingRole = this.identityTag || this.roles[0];
    },
    closeIdentity() {
      this.showIdentityModal = false;
    },
    clearIdentity() {
      this.pendingRole = null;
      this.setIdentityTag(null);
      this.closeIdentity();
    },
    confirmIdentity() {
      const role = this.pendingRole || this.roles[0];
      this.setIdentityTag(role);
      this.pendingRole = role;
      this.$root.$refs.toast?.show('身份已更新', 'success');
      this.closeIdentity();
    }
  }
};
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.hero-card {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 20px;
  position: relative;
}

.avatar {
  width: 96px;
  height: 96px;
  border-radius: 24px;
  background: linear-gradient(135deg, #6366f1, #22d3ee);
  color: #fff;
  font-size: 36px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-info h1 {
  margin: 6px 0 4px;
}

.stage-text {
  margin: 0;
  color: var(--gray-600);
}

.intro {
  margin: 8px 0 12px;
  color: var(--gray-700);
}

.focus-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 10px;
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
  border-radius: 999px;
  font-size: 12px;
}

.hero-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.identity-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.identity-card .sub {
  margin: 4px 0 0;
  color: var(--gray-700);
  font-size: 13px;
}

.identity-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 6px;
}

.chip {
  border: 1px solid var(--gray-200);
  background: #fff;
  padding: 8px 12px;
  border-radius: 10px;
  cursor: pointer;
}

.chip.active {
  border-color: var(--blue);
  color: var(--blue);
  background: rgba(37, 99, 235, 0.08);
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

.identity-dialog {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  width: min(680px, 100%);
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.16);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.dialog-header h3 {
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

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
  align-items: center;
}

.dialog-actions .ghost-btn,
.dialog-actions .primary-btn {
  min-width: 120px;
  padding: 10px 14px;
  border-radius: 10px;
}

.ghost-btn {
  border: 1px solid var(--gray-200);
  background: #fff;
  border-radius: 12px;
  padding: 10px 16px;
  cursor: pointer;
}

.stats {
  grid-column: 1 / -1;
  display: flex;
  gap: 20px;
  list-style: none;
  margin: 12px 0 0;
  padding: 0;
  border-top: 1px solid #eef2ff;
  padding-top: 12px;
}

.value {
  margin: 0;
  font-weight: 700;
  font-size: 20px;
}

.label {
  margin: 0;
  color: var(--gray-600);
  font-size: 13px;
}

.privacy-card .privacy-header {
  margin-bottom: 12px;
}

.privacy-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.privacy-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px 16px;
}

.privacy-row .title {
  margin: 0;
  font-weight: 600;
}

.privacy-row .desc {
  margin: 2px 0 0;
  color: var(--gray-600);
  font-size: 13px;
}

.switch {
  border: none;
  border-radius: 12px;
  padding: 8px 12px;
  background: #eef2ff;
  color: #4338ca;
  cursor: pointer;
}

.data-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.data-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.tabs {
  display: flex;
  gap: 8px;
}

.tab {
  border: 1px solid var(--gray-200);
  border-radius: 999px;
  padding: 6px 12px;
  background: #fff;
  cursor: pointer;
}

.tab.active {
  border-color: #6366f1;
  color: #6366f1;
  background: rgba(99, 102, 241, 0.1);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th,
.data-table td {
  padding: 10px 12px;
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
}

.loading,
.empty {
  text-align: center;
  color: var(--gray-500);
  padding: 40px 0;
}

.data-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 13px;
  color: var(--gray-600);
}

.history-item,
.circle-item,
.publish-item,
.collection-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  gap: 12px;
}

.item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-type {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(99, 102, 241, 0.1);
  color: #4338ca;
  border-radius: 4px;
  font-size: 12px;
  width: fit-content;
}

.item-title {
  font-weight: 600;
  color: var(--gray-900);
}

.item-meta {
  font-size: 12px;
  color: var(--gray-600);
}

.item-role {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(245, 158, 11, 0.1);
  color: var(--orange);
  border-radius: 4px;
  font-size: 12px;
  width: fit-content;
  margin: 4px 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.item-badges {
  display: flex;
  gap: 4px;
}

.badge {
  padding: 2px 6px;
  background: rgba(245, 158, 11, 0.1);
  color: var(--orange);
  border-radius: 4px;
  font-size: 11px;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.ghost-btn.small {
  padding: 6px 12px;
  font-size: 12px;
}

.ghost-btn.danger {
  color: var(--red);
  border-color: var(--red);
}

.primary-btn.small {
  padding: 6px 12px;
  font-size: 12px;
}

.collections-layout {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 16px;
}

.collections-sidebar {
  border-right: 1px solid var(--gray-200);
  padding-right: 16px;
}

.folder-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
}

.folder-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.folder-item.active {
  background: rgba(99, 102, 241, 0.1);
  color: var(--blue);
}

.folder-icon {
  font-size: 16px;
}

.folder-name {
  flex: 1;
  font-size: 14px;
}

.folder-count {
  font-size: 12px;
  color: var(--gray-600);
}

.collections-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

@media (max-width: 900px) {
  .collections-layout {
    grid-template-columns: 1fr;
  }

  .collections-sidebar {
    border-right: none;
    border-bottom: 1px solid var(--gray-200);
    padding-right: 0;
    padding-bottom: 16px;
  }

  .folder-list {
    flex-direction: row;
    overflow-x: auto;
  }
}

@media (max-width: 900px) {
  .hero-card {
    grid-template-columns: auto 1fr;
  }

  .hero-actions {
    flex-direction: row;
    grid-column: 1 / -1;
  }

  .stats {
    flex-wrap: wrap;
  }
}
</style>
