import {
  recommendByRole,
  topicsByRole,
  circlesByRole,
  proResources,
  experts,
  searchDatasets,
  messagesMock,
  creditLogs
} from '@/mock/data';

const delay = (data, timeout = 260) =>
  new Promise((resolve) => setTimeout(() => resolve(JSON.parse(JSON.stringify(data))), timeout));

export const fetchHotKeywords = () =>
  delay({
    keywords: [
      '秋招面试',
      '简历优化',
      '面试技巧',
      '职业规划',
      '职场心理',
      'offer选择',
      '春招冲刺',
      '行业交流',
      '导师咨询',
      '求职专项'
    ]
  });

export const fetchSearchResults = (tab, keyword = '', page = 1, pageSize = 5) => {
  const pool = searchDatasets[tab] || [];
  const filtered = keyword
    ? pool.filter((item) => item.title.includes(keyword) || item.desc.includes(keyword))
    : pool;
  const start = (page - 1) * pageSize;
  return delay({
    items: filtered.slice(start, start + pageSize),
    total: filtered.length,
    page,
    pageSize
  });
};

export const fetchMessages = () => delay(messagesMock);

export const fetchRecommendations = (identity = '默认', page = 1, pageSize = 4) => {
  const source = recommendByRole[identity] || recommendByRole.默认;
  const start = (page - 1) * pageSize;
  return delay({
    items: source.slice(start, start + pageSize),
    total: source.length
  });
};

export const fetchTopicsByRole = (identity = '默认') =>
  delay(topicsByRole[identity] || topicsByRole.默认);

export const fetchCirclesByRole = (identity = '默认') =>
  delay(circlesByRole[identity] || circlesByRole.默认);

export const fetchProResources = () => delay(proResources);

export const fetchExperts = () => delay(experts);

export const fetchCreditLogs = () => delay(creditLogs);
