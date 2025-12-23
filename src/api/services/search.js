import http from '../http';
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchSearchMeta = async () => {
  const payload = await http.get('/api/v1/search/history-hot');
  return unwrap(payload);
};

export const fetchSearchResults = async (params = {}) => {
  const payload = await http.get('/api/v1/search/result', { params });
  return unwrap(payload);
};

export const clearSearchHistory = async () => {
  const payload = await http.delete('/api/v1/search/history');
  return unwrap(payload);
};

export const appendSearchHistory = async (keyword) => {
  const payload = await http.get('/api/v1/search/history', { keyword });
  return unwrap(payload);
};
