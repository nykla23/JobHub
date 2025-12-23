import http from '../http';

const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchResourceList = async (params = {}) => {
  const res = await http.get('/api/v1/expert/resource/list', { params });
  return unwrap(res);
};

export const fetchExpertList = async (params = {}) => {
  const res = await http.get('/api/v1/expert/list', { params });
  return unwrap(res);
};

export const applyAsExpert = async (payload) => {
  const res = await http.post('/api/v1/expert/apply', payload);
  return unwrap(res);
};

export const fetchExpertProfile = async (expertId, params = {}) => {
  if (!expertId) return Promise.reject(new Error('expertId is required'));
  const res = await http.get(`/api/v1/expert/${expertId}`, { params });
  return unwrap(res);
};

export const followExpert = async (expertId, payload) => {
  if (!expertId) return Promise.reject(new Error('expertId is required'));
  const res = await http.put(`/api/v1/expert/${expertId}/follow`, payload);
  return unwrap(res);
};

export const messageExpert = async (expertId, payload) => {
  if (!expertId) return Promise.reject(new Error('expertId is required'));
  const res = await http.post(`/api/v1/expert/${expertId}/message`, payload);
  return unwrap(res);
};

export const fetchExpertDashboard = async () => {
  const res = await http.get('/api/v1/expert/backend/dashboard');
  return unwrap(res);
};

export const fetchExpertContent = async (params = {}) => {
  const res = await http.get('/api/v1/expert/backend/content', { params });
  return unwrap(res);
};

export const createExpertContent = async (payload) => {
  const res = await http.post('/api/v1/expert/backend/content', payload);
  return unwrap(res);
};

export const updateExpertContent = async (contentId, payload) => {
  if (!contentId) return Promise.reject(new Error('contentId is required'));
  const res = await http.put(`/api/v1/expert/backend/content/${contentId}`, payload);
  return unwrap(res);
};
