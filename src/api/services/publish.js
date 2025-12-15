import http from '../http';
import { apiConfig } from '../config';

const useMock = apiConfig.useMock;

const unwrap = (response) => {
  const code = response?.code ?? response?.status;
  if (code && code !== 200) {
    const message = response?.message || response?.msg || '请求失败';
    return Promise.reject(new Error(message));
  }
  return response?.data ?? response;
};

const buildFormData = (key, files) => {
  const formData = new FormData();
  if (Array.isArray(files)) {
    files.forEach((file) => formData.append(key, file));
  } else if (files) {
    formData.append(key, files);
  }
  return formData;
};

export const quickPublish = async (body = {}) => {
  if (useMock) {
    return {
      contentId: Date.now(),
      type: body.type || 'article',
      status: 'pending',
      submitTime: new Date().toISOString()
    };
  }
  const res = await http.post('/api/v1/publish', body);
  return unwrap(res);
};

export const uploadImage = async (files) => {
  if (useMock) {
    const urls = (Array.isArray(files) ? files : [files]).map(
      (_, idx) => `https://mock.jobhub/upload/image-${Date.now()}-${idx}.jpg`
    );
    return { imageUrls: urls };
  }
  const formData = buildFormData('image', files);
  const res = await http.post('/api/v1/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return unwrap(res);
};

export const uploadFile = async (file) => {
  if (useMock) {
    return { fileUrl: `https://mock.jobhub/upload/file-${Date.now()}` };
  }
  const formData = buildFormData('file', file);
  const res = await http.post('/api/v1/upload/file', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return unwrap(res);
};
