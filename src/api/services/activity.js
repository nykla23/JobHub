import http from '../http';

const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchActivityDetail = async (id, params = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const res = await http.get(`/api/v1/activity/${id}`, { params });
  return unwrap(res);
};
