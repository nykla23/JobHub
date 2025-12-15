import http from '../http';
import { apiConfig } from '../config';
import * as mockService from '../mockService';

const useMock = apiConfig.useMock;

const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchResourceList = async (params = {}) => {
  if (useMock) {
    const list = await mockService.fetchProResources();
    return { records: list, total: list.length, size: list.length, current: 1, pages: 1 };
  }
  const res = await http.get('/api/v1/expert/resource/list', { params });
  return unwrap(res);
};

export const fetchExpertList = async (params = {}) => {
  if (useMock) {
    const list = await mockService.fetchExperts();
    return { records: list, total: list.length, size: list.length, current: 1, pages: 1 };
  }
  const res = await http.get('/api/v1/expert/list', { params });
  return unwrap(res);
};

export const applyAsExpert = async (payload) => {
  if (useMock) {
    return {
      applicationId: Date.now(),
      status: 'pending',
      submitted: payload
    };
  }
  const res = await http.post('/api/v1/expert/apply', payload);
  return unwrap(res);
};

export const fetchExpertProfile = async (expertId, params = {}) => {
  if (!expertId) return Promise.reject(new Error('expertId is required'));
  if (useMock) {
    const experts = await mockService.fetchExperts();
    const expert = experts.find((item) => `${item.id}` === `${expertId}`) || experts[0] || {};
    return {
      expertInfo: {
        id: expertId,
        name: expert.name || '专家',
        avatar: expert.avatar || '',
        certification: expert.certification || '认证信息',
        expertise: expert.tags?.join(' / ') || '擅长领域',
        score: Number(expert.rating) || 4.8,
        consultCount: expert.consultCount || 0,
        intro: expert.desc || '',
        followed: false
      },
      expertContent: {
        total: 0,
        pageNum: Number(params.pageNum) || 1,
        pageSize: Number(params.pageSize) || 10,
        list: []
      },
      expertQa: {
        total: 0,
        pageNum: Number(params.pageNum) || 1,
        pageSize: Number(params.pageSize) || 10,
        list: []
      }
    };
  }
  const res = await http.get(`/api/v1/expert/${expertId}`, { params });
  return unwrap(res);
};

export const followExpert = async (expertId, payload) => {
  if (!expertId) return Promise.reject(new Error('expertId is required'));
  if (useMock) {
    return { followed: payload?.isFollow ?? true };
  }
  const res = await http.put(`/api/v1/expert/${expertId}/follow`, payload);
  return unwrap(res);
};

export const messageExpert = async (expertId, payload) => {
  if (!expertId) return Promise.reject(new Error('expertId is required'));
  if (useMock) {
    return {
      messageId: Date.now(),
      sendTime: new Date().toISOString(),
      tips: '已发送（mock）'
    };
  }
  const res = await http.post(`/api/v1/expert/${expertId}/message`, payload);
  return unwrap(res);
};

export const fetchExpertDashboard = async () => {
  if (useMock) {
    return {
      viewCount: 0,
      avgScore: 0,
      commentCount: 0,
      collectCount: 0,
      followCount: 0,
      recent7Days: { viewCount: 0, followCount: 0, commentCount: 0 }
    };
  }
  const res = await http.get('/api/v1/expert/backend/contentapi/v1/expert/backend/dashboard');
  return unwrap(res);
};

export const fetchExpertContent = async (params = {}) => {
  if (useMock) {
    return { records: [], total: 0, size: Number(params.pageSize) || 10, current: Number(params.pageNum) || 1, pages: 0 };
  }
  const res = await http.get('/api/v1/expert/backend/content', { params });
  return unwrap(res);
};

export const createExpertContent = async (payload) => {
  if (useMock) {
    return {
      contentId: Date.now(),
      status: 'pending',
      submitTime: new Date().toISOString(),
      updateTime: null
    };
  }
  const res = await http.post('/api/v1/expert/backend/content', payload);
  return unwrap(res);
};

export const updateExpertContent = async (contentId, payload) => {
  if (!contentId) return Promise.reject(new Error('contentId is required'));
  if (useMock) {
    return {
      contentId,
      status: 'updated',
      submitTime: null,
      updateTime: new Date().toISOString()
    };
  }
  const res = await http.put(`/api/v1/expert/backend/content/${contentId}`, payload);
  return unwrap(res);
};
