import axios from 'axios';
import { apiConfig } from './config';

const http = axios.create({
  baseURL: apiConfig.baseURL,
  timeout: 12000
});

// ----------- 请求拦截器 -----------
http.interceptors.request.use(
  (config) => {
    const headers = config.headers || {};

    // 统一使用 JSON
    headers['Content-Type'] = 'application/json';

    // 只有本地存储中有真实 token 才加 Authorization
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('jobhub_token');

      if (token) {
        headers.Authorization = token.startsWith('Bearer ')
          ? token
          : `Bearer ${token}`;
      } else {
        // 登录接口等情况禁止携带 Authorization
        delete headers.Authorization;
      }
    }

    config.headers = headers;
    return config;
  },
  (error) => Promise.reject(error)
);

// ----------- 响应拦截器 -----------
http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      const message = data?.message || error.message || '网络请求异常';
      return Promise.reject(new Error(`[${status}] ${message}`));
    }
    return Promise.reject(error);
  }
);

export default http;
