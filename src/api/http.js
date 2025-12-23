import axios from "axios";
import { apiConfig } from "./config";

const http = axios.create({
  baseURL: apiConfig.baseURL,
  timeout: 12000,
  withCredentials: true
});

// ================================
// 请求拦截器
// ================================
http.interceptors.request.use(
  (config) => {
    const headers = config.headers || {};
    const url = config.url || "";

    // 统一 JSON
    headers["Content-Type"] = "application/json";

    // ⭐ 这些接口【绝对不能】携带 Authorization
    const isAuthApi =
      url.includes("/api/user/login") ||
      url.includes("/api/user/register") ||
      url.includes("/api/user/forgotpassword") ||
      url.includes("/api/user/identity");

    if (!isAuthApi && typeof window !== "undefined") {
      const token = localStorage.getItem("jobhub_token");

      if (token) {
        headers.Authorization = token.startsWith("Bearer ")
          ? token
          : `Bearer ${token}`;
      }
    } else {
      // 登录 / 注册接口确保无 Authorization
      delete headers.Authorization;
    }

    config.headers = headers;
    return config;
  },
  (error) => Promise.reject(error)
);

// ================================
// 响应拦截器
// ================================
http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      // token 失效 / 未登录
      if (status === 401 || status === 403) {
        // 可选：自动清理 token
        localStorage.removeItem("jobhub_token");
        // 可选：跳转登录页
        // window.location.href = "/login";
      }

      const message =
        data?.message || data?.msg || error.message || "网络请求异常";

      return Promise.reject(new Error(`[${status}] ${message}`));
    }

    return Promise.reject(error);
  }
);

export default http;
