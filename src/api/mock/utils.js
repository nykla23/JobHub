export const withDelay = (payload, timeout = 380) =>
  new Promise((resolve) => {
    setTimeout(() => {
      const cloned = JSON.parse(JSON.stringify(payload));
      resolve(cloned);
    }, timeout);
  });

export const buildSuccess = (data, message = 'mock success') => ({
  code: 200,
  message,
  data
});

