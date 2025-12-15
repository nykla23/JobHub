import http from '../http';
import { apiConfig } from '../config';
import * as mockHome from '../mock/home';

const useMock = apiConfig.useMock;
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const getHomeOverview = async (params = {}) => {
  const payload = useMock
    ? await mockHome.getHomeOverview(params)
    : await http.get('/api/v1/home', { params });
  return unwrap(payload);
};

export const refreshRecommendFeed = async (params = {}) => {
  const payload = useMock
    ? await mockHome.refreshRecommend(params)
    : await http.get('/api/v1/home/recommend/refresh', { params });
  return unwrap(payload);
};
