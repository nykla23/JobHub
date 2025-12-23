import http from '../http';
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchGroupList = async (params = {}) => {
  const res = await http.get('/api/v1/group/list', { params });
  return unwrap(res);
};

export const createGroup = async (body = {}) => {
  const res = await http.post('/api/v1/group', body);
  return unwrap(res);
};

export const fetchGroupDetail = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const res = await http.get(`/api/v1/group/${id}`);
  return unwrap(res);
};

export const joinOrQuitGroup = async (id, action) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const res = await http.put(`/api/v1/group/${id}/join-or-quit`, { action });
  return unwrap(res);
};

export const postGroupDynamic = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const res = await http.post(`/api/v1/group/${id}/dynamic`, body);
  return unwrap(res);
};

export const uploadGroupResource = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const res = await http.post(`/api/v1/group/${id}/resource`, body);
  return unwrap(res);
};
