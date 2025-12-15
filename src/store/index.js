import Vue from "vue";
import Vuex from "vuex";
import { fetchSearchMeta, clearSearchHistory as clearSearchHistoryApi } from "@/api/services/search";
import { login as loginApi } from "@/api/services/auth";
import { fetchProfileOverview, savePrivacy as savePrivacyApi } from "@/api/services/user";

Vue.use(Vuex);

const getStoredIdentity = () => {
  if (typeof window === 'undefined') return null;
  try {
    return localStorage.getItem('identityTag');
  } catch (e) {
    return null;
  }
};

const getStoredToken = () => {
  if (typeof window === 'undefined') return null;
  try {
    return localStorage.getItem('jobhub_token');
  } catch (e) {
    return null;
  }
};

const defaultSettings = {
  hideCompany: false,
  hideSalary: false,
  hideHistory: false,
  pushFrequency: '\u5b9e\u65f6',
  pushType: '\u5168\u90e8'
};

const initialProfile = {
  nickname: '\u672a\u547d\u540d\u7528\u6237',
  stage: '\u672a\u8bbe\u7f6e',
  bio: '\u5b8c\u5584\u4e2a\u4eba\u7b80\u4ecb\uff0c\u83b7\u5f97\u66f4\u7cbe\u51c6\u63a8\u8350',
  focus: ['\u4ea7\u54c1', '\u8fd0\u8425'],
  likes: 0,
  avatar: ''
};

export default new Vuex.Store({
  state: {
    identityTag: getStoredIdentity(),
    authToken: getStoredToken(),
    userInfo: null,
    publishOpen: false,
    unread: {
      interaction: 2,
      system: 1,
      circle: 3
    },
    searchHistory: [],
    hotKeywords: [],
    searchMetaLoaded: false,
    profile: initialProfile,
    settings: defaultSettings,
    credit: {
      score: 100,
      logs: []
    }
  },
  getters: {
    unreadTotal: (state) => state.unread.interaction + state.unread.system + state.unread.circle
  },
  mutations: {
    setIdentityTag(state, payload) {
      state.identityTag = payload;
      try {
        if (payload) {
          localStorage.setItem('identityTag', payload);
        } else {
          localStorage.removeItem('identityTag');
        }
      } catch (e) {
        // ignore storage errors
      }
    },
    setAuth(state, payload) {
      state.authToken = payload?.token || null;
      state.userInfo = payload?.userInfo || null;
      try {
        if (payload?.token) {
          localStorage.setItem('jobhub_token', payload.token);
        } else {
          localStorage.removeItem('jobhub_token');
        }
      } catch (e) {
        // ignore
      }
    },
    logout(state) {
      state.authToken = null;
      state.userInfo = null;
      state.identityTag = null;
      try {
        localStorage.removeItem('jobhub_token');
        localStorage.removeItem('identityTag');
      } catch (e) {
        // ignore
      }
    },
    setPublishOpen(state, open) {
      state.publishOpen = open;
    },
    setUnread(state, payload) {
      state.unread = { ...state.unread, ...payload };
    },
    markAllRead(state) {
      state.unread = { interaction: 0, system: 0, circle: 0 };
    },
    addSearchHistory(state, keyword) {
      if (!keyword) return;
      const list = state.searchHistory.filter((item) => item !== keyword);
      list.unshift(keyword);
      state.searchHistory = list.slice(0, 10);
    },
    clearSearchHistory(state) {
      state.searchHistory = [];
    },
    setSearchHistory(state, payload) {
      const list = Array.isArray(payload) ? payload : [];
      state.searchHistory = list.slice(0, 10);
    },
    setHotKeywords(state, payload) {
      const list = Array.isArray(payload) ? payload : [];
      state.hotKeywords = list.slice(0, 10);
    },
    setSearchMetaLoaded(state, payload) {
      state.searchMetaLoaded = Boolean(payload);
    },
    setProfile(state, payload) {
      state.profile = { ...state.profile, ...payload };
    },
    updateSettings(state, payload) {
      state.settings = { ...state.settings, ...payload };
    },
    setCredit(state, payload) {
      state.credit = { ...state.credit, ...payload };
    }
  },
  actions: {
    async login({ commit, dispatch }, payload) {
      const data = await loginApi(payload);
      commit('setAuth', data);
      const profileData = await dispatch('fetchProfileOverview');
      const identity =
        data?.userInfo?.identity ||
        profileData?.profile?.identity ||
        profileData?.profile?.careerStage ||
        null;
      commit('setIdentityTag', identity);
      return data;
    },
    async fetchProfileOverview({ commit }) {
      const data = await fetchProfileOverview();
      if (data.profile) {
        commit('setProfile', data.profile);
      }
      if (data.privacy) {
        commit('updateSettings', data.privacy);
      }
      return data;
    },
    async fetchSearchMeta({ state, commit }, { force = false } = {}) {
      if (state.searchMetaLoaded && !force) return;
      try {
        const data = await fetchSearchMeta();
        commit('setSearchHistory', data.searchHistory || []);
        commit('setHotKeywords', data.hotSearch || []);
        commit('setSearchMetaLoaded', true);
      } catch (error) {
        console.error('[store] 获取搜索 Meta 失败', error);
      }
    },
    async clearSearchHistoryRemote({ commit }) {
      try {
        await clearSearchHistoryApi();
        commit('clearSearchHistory');
      } catch (error) {
        console.error('[store] 清空搜索历史失败', error);
      }
    },
    async savePrivacySettings({ commit, state }, payload) {
      try {
        const next = { ...state.settings, ...payload };
        const data = await savePrivacyApi(next);
        commit('updateSettings', data);
      } catch (error) {
        console.error('[store] 保存隐私设置失败', error);
      }
    }
  }
});
