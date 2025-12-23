import http from '../http';

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
  const res = await http.post('/api/v1/publish', body);
  return unwrap(res);
};

export const uploadImage = async (files) => {
  const formData = buildFormData('image', files);
  const res = await http.post('/api/v1/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return unwrap(res);
};

export const uploadFile = async (file) => {
  const formData = buildFormData('file', file);
  const res = await http.post('/api/v1/upload/file', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return unwrap(res);
};
