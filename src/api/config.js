const DEFAULT_BASE_URL = process.env.VUE_APP_API_BASE || 'http://localhost:8080';
const USE_MOCK_FLAG = process.env.VUE_APP_USE_MOCK;

export const apiConfig = {
  baseURL: DEFAULT_BASE_URL,
  // 默认开启 mock，只有显式设置 VUE_APP_USE_MOCK=false 时才关闭
  useMock: USE_MOCK_FLAG === undefined ? true : USE_MOCK_FLAG !== 'false'
};
