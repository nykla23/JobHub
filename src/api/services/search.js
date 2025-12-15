import http from '../http';
import { apiConfig } from '../config';
import * as mockSearch from '../mock/search';

const useMock = apiConfig.useMock;
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchSearchMeta = async () => {
  const payload = useMock
    ? await mockSearch.getSearchMeta()
    : await http.get('/api/v1/search/history-hot');
  return unwrap(payload);
};

export const fetchSearchResults = async (params = {}) => {
  const payload = useMock
    ? await mockSearch.searchAll(params)
    : await http.get('/api/v1/search/result', { params });
  return unwrap(payload);
};

export const clearSearchHistory = async () => {
  const payload = useMock
    ? await mockSearch.clearSearchHistory()
    : await http.delete('/api/v1/search/history');
  return unwrap(payload);
};

export const appendSearchHistory = async (keyword) => {
  const payload = useMock
    ? await mockSearch.appendSearchHistory(keyword)
    : await http.post('/api/v1/search/history', { keyword });
  return unwrap(payload);
};
