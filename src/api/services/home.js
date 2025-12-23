import http from '../http';
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const getHomeOverview = async (params = {}) => {
  const payload = await http.get('/api/v1/home', { params });
  return unwrap(payload);
};

export const refreshRecommendFeed = async (params = {}) => {
  const payload = await http.get('/api/v1/home/recommend/refresh', { params });
  return unwrap(payload);
};
