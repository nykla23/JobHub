// src/api/services/user.js

import http from '../http';
import { apiConfig } from '../config';
import * as mockUser from '../mock/user';

const useMock = apiConfig.useMock;

const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

// 获取个人资料（展示）
export const fetchProfileOverview = async () => {
  const payload = useMock
    ? await mockUser.getProfileOverview()
    : await http.get('/api/user/profile');
  return unwrap(payload);
};

// ===============================
// ★★★ 关键修复：编辑个人资料
// ===============================
export const saveProfile = async (body) => {
  // 字段映射，保证前端字段符合后端 UserProfileUpdateDTO
  const mapped = {
    displayName: body.displayName || body.nickname || '',
    bio: body.bio || body.intro || '',
    careerStage: body.careerStage || '',
    location: body.location || '',
    education: body.education || '',
    fields: []
  };

  // 修复 fields 必须是 List<String>
  if (Array.isArray(body.fields)) {
    mapped.fields = body.fields;
  } else if (typeof body.fields === 'string' && body.fields.trim()) {
    // 把 “产品 · 运营” 等字符串切成数组
    mapped.fields = body.fields
      .split(/,|·|\s+/)
      .map((v) => v.trim())
      .filter(Boolean);
  } else {
    mapped.fields = [];
  }

  // 昵称必填（后端注解 @NotBlank）
  if (!mapped.displayName.trim()) {
    return Promise.reject(new Error('昵称不能为空'));
  }

  const payload = useMock
    ? await mockUser.updateProfile(mapped)
    : await http.put('/api/user/profile', mapped);

  return unwrap(payload);
};

// ===============================
// 以下所有接口保持原样
// ===============================

export const savePrivacy = async (body) => {
  const payload = useMock
    ? await mockUser.updatePrivacy(body)
    : await http.put('/api/user/profile/privacy', body);
  return unwrap(payload);
};

export const fetchPrivacySettings = async () => {
  const payload = useMock
    ? await mockUser.updatePrivacy({})
    : await http.get('/api/user/profile/privacy');
  return unwrap(payload);
};

export const fetchHomepage = async () => {
  const payload = useMock
    ? await mockUser.getProfileOverview()
    : await http.get('/api/user/profile/homepage');
  return unwrap(payload);
};

export const fetchHistory = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getHistory(params)
    : await http.get('/api/user/browse-history/list', { params });
  return unwrap(payload);
};

export const addBrowseHistory = async (params = {}) => {
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post('/api/user/browse-history/add', null, { params });
  return unwrap(payload);
};

export const fetchHistoryDetail = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? await mockUser.getHistory({})
    : await http.get(`/api/user/browse-history/detail/${id}`);
  return unwrap(payload);
};

export const deleteHistoryRecord = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.delete(`/api/user/browse-history/delete/${id}`);
  return unwrap(payload);
};

export const fetchMyCircles = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyCircles(params)
    : await http.get('/api/user/group/my-groups', { params });
  return unwrap(payload);
};

export const joinCircle = async (groupId) => {
  if (!groupId) return Promise.reject(new Error('groupId is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post(`/api/user/group/join/${groupId}`);
  return unwrap(payload);
};

export const leaveCircle = async (groupId) => {
  if (!groupId) return Promise.reject(new Error('groupId is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post(`/api/user/group/leave/${groupId}`);
  return unwrap(payload);
};

export const discoverCircles = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyCircles(params)
    : await http.get('/api/user/group/discover', { params });
  return unwrap(payload);
};

export const fetchMyCollections = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyCollections(params)
    : await http.get('/api/user/collection/list', { params });
  return unwrap(payload);
};

export const fetchCollectionFolders = async () => {
  const payload = useMock
    ? await mockUser.getMyCollections({})
    : await http.get('/api/user/collection/folders');
  return unwrap(payload);
};

export const createCollectionFolder = async (name) => {
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post('/api/user/collection/folders', null, { params: { name } });
  return unwrap(payload);
};

export const cancelCollection = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.delete(`/api/user/collection/cancel/${id}`);
  return unwrap(payload);
};

export const fetchMyPublish = async (params = {}) => {
  const payload = useMock
    ? await mockUser.getMyPublish(params)
    : await http.get('/api/user/published/list', { params });
  return unwrap(payload);
};

export const createPublish = async (body = {}) => {
  const payload = useMock
    ? await mockUser.getMyPublish(body)
    : await http.post('/api/user/published/publish', body);
  return unwrap(payload);
};

export const updatePublish = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? await mockUser.getMyPublish(body)
    : await http.put(`/api/user/published/edit/${id}`, body);
  return unwrap(payload);
};

export const togglePublishTop = async (id, isTop = 1) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.post(`/api/user/published/top/${id}`, null, { params: { isTop } });
  return unwrap(payload);
};

export const deletePublish = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: null }
    : await http.delete(`/api/user/published/delete/${id}`);
  return unwrap(payload);
};
