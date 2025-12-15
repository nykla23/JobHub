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

export const fetchGroupList = async (params = {}) => {
  if (useMock) {
    const list = await mockService.fetchCirclesByRole?.(params.tag) || [];
    return { records: list, total: list.length, pageNum: 1, pageSize: list.length };
  }
  const res = await http.get('/api/v1/group/list', { params });
  return unwrap(res);
};

export const createGroup = async (body = {}) => {
  if (useMock) {
    return { id: Date.now(), ...body };
  }
  const res = await http.post('/api/v1/group', body);
  return unwrap(res);
};

export const fetchGroupDetail = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  if (useMock) {
    const list = await mockService.fetchCirclesByRole?.() || [];
    return list.find((item) => `${item.id}` === `${id}`) || { id, name: '小组', desc: '' };
  }
  const res = await http.get(`/api/v1/group/${id}`);
  return unwrap(res);
};

export const joinOrQuitGroup = async (id, action) => {
  if (!id) return Promise.reject(new Error('id is required'));
  if (useMock) {
    return { id, action: action || 'join' };
  }
  const res = await http.put(`/api/v1/group/${id}/join-or-quit`, { action });
  return unwrap(res);
};

export const postGroupDynamic = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  if (useMock) {
    return { id: Date.now(), groupId: id, ...body };
  }
  const res = await http.post(`/api/v1/group/${id}/dynamic`, body);
  return unwrap(res);
};

export const uploadGroupResource = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  if (useMock) {
    return { resourceId: Date.now(), groupId: id, ...body };
  }
  const res = await http.post(`/api/v1/group/${id}/resource`, body);
  return unwrap(res);
};
