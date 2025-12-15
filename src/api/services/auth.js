// src/api/services/auth.js

import http from '../http';
import { apiConfig } from '../config';
import * as mockAuth from '../mock/auth';

const useMock = apiConfig.useMock;

// 统一解包工具
const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

// ==========================
// 登录（正确 JSON 版）
// ==========================
export const login = async (payload) => {
  const body =
    payload?.username
      ? { username: payload.username, password: payload.password }
      : payload;

  if (!body?.username || !body?.password) {
    return Promise.reject(new Error('用户名和密码不能为空'));
  }

  let data;

  if (useMock) {
    // mock 模式
    data = await mockAuth.login(body);
  } else {
    // ★★★ 正确 JSON 登录（关键修复）
    data = await http.post('/api/user/login', body);
  }

  const unwrapped = unwrap(data);

  // 后端返回 token 字符串时适配
  if (typeof unwrapped === 'string') {
    return { token: unwrapped };
  }

  return unwrapped;
};

// 注册
export const register = async (payload) => {
  const data = useMock
    ? await mockAuth.register(payload)
    : await http.post('/api/user/register', payload);
  return unwrap(data) || {};
};

// 选择身份
export const selectIdentity = async (payload) => {
  const data = useMock
    ? await mockAuth.selectIdentity(payload)
    : await http.post('/api/user/identity', null, { params: payload });
  return unwrap(data);
};

// 忘记密码
export const resetPassword = async (payload) => {
  const data = useMock
    ? await mockAuth.resetPassword(payload)
    : await http.post('/api/user/forgotpassword', payload);
  return unwrap(data);
};

// 修改密码
export const changePassword = async (payload) => {
  const data = useMock
    ? await mockAuth.resetPassword(payload)
    : await http.put('/api/user/password', payload);
  return unwrap(data);
};
