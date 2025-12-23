import http from '../http';
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchMessages = async (params = {}) => {
  const res = await http.get('/api/v1/message/list', { params });
  return unwrap(res);
};

export const markMessagesRead = async (ids = []) => {
  const res = await http.put('/api/v1/message/read', { ids });
  return unwrap(res);
};

export const markAllMessagesRead = async () => {
  const res = await http.put('/api/v1/message/read/all');
  return unwrap(res);
};

export const deleteMessages = async (ids = []) => {
  const res = await http.delete('/api/v1/message/delete', { data: { ids } });
  return unwrap(res);
};
