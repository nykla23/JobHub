import http from '../http';
import { apiConfig } from '../config';
import * as mockTopics from '../mock/topics';

const useMock = apiConfig.useMock;
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

export const fetchTopicList = async (params = {}) => {
  const payload = useMock
    ? await mockTopics.fetchTopicList(params)
    : await http.get('/api/v1/topic/list', { params });
  return unwrap(payload);
};

export const fetchTopicDetail = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? await mockTopics.fetchTopicList({ id }).then((res) => res?.list?.find((item) => `${item.id}` === `${id}`) || {})
    : await http.get(`/api/v1/topic/${id}`);
  return unwrap(payload);
};

export const postTopicComment = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: { id: Date.now(), content: body.content } }
    : await http.post(`/api/v1/topic/${id}/comment`, body);
  return unwrap(payload);
};

export const toggleCommentLike = async (id, isLike) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: { id, isLike: !!isLike } }
    : await http.put(`/api/v1/comment/${id}/like`, { isLike });
  return unwrap(payload);
};

export const fetchChatroomList = async (params = {}) => {
  const payload = useMock
    ? { list: [], total: 0, pageNum: 1, pageSize: 10 }
    : await http.get('/api/v1/chatroom/list', { params });
  return unwrap(payload);
};

export const fetchChatroomDetail = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { id, title: '聊天室', status: 'ongoing' }
    : await http.get(`/api/v1/chatroom/${id}`);
  return unwrap(payload);
};

export const sendChatroomMessage = async (id, body = {}) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: { messageId: Date.now(), ...body } }
    : await http.post(`/api/v1/chatroom/${id}/message`, body);
  return unwrap(payload);
};

export const generateChatroomNote = async (id) => {
  if (!id) return Promise.reject(new Error('id is required'));
  const payload = useMock
    ? { code: 200, data: { noteId: Date.now() } }
    : await http.post(`/api/v1/chatroom/${id}/essence-note`);
  return unwrap(payload);
};

