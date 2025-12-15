import { withDelay, buildSuccess } from './utils';
import { searchDatasets } from '@/mock/data';

let historySeed = [
  '秋招面试',
  '简历优化',
  '第一份实习',
  '面试技巧',
  '职业规划'
];

const hotSeed = [
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
];

const normalizeType = (type) => {
  if (!type || type === 'all') return '';
  const mapping = {
    topic: '话题',
    content: '内容',
    group: '圈子',
    user: '用户',
    expert: '专家'
  };
  return mapping[type] || '';
};

const paginate = (list = [], pageNum = 1, pageSize = 10) => {
  const total = list.length;
  const start = (pageNum - 1) * pageSize;
  return {
    total,
    pageNum,
    pageSize,
    list: list.slice(start, start + pageSize)
  };
};

const filterList = (items, keyword = '', sort = 'hot') => {
  if (!items?.length) return [];
  const trimmed = keyword.trim();
  const filtered = trimmed
    ? items.filter((item) => item.title.includes(trimmed) || item.desc.includes(trimmed))
    : items;
  if (sort === 'time') {
    return filtered.slice().reverse();
  }
  return filtered;
};

export const getSearchMeta = async () =>
  withDelay(
    buildSuccess({
      searchHistory: historySeed.slice(0, 10),
      hotSearch: hotSeed.slice(0, 10)
    })
  );

export const searchAll = async ({
  keyword = '',
  type = '',
  sort = 'hot',
  pageNum = 1,
  pageSize = 10
} = {}) => {
  const tabKey = normalizeType(type) || '话题';
  const pool = searchDatasets[tabKey] || [];
  const filtered = filterList(pool, keyword, sort);
  return withDelay(
    buildSuccess({
      keyword,
      type: tabKey,
      sort,
      ...paginate(filtered, pageNum, pageSize)
    })
  );
};

export const clearSearchHistory = async () => {
  historySeed = [];
  return withDelay(buildSuccess({}, 'history cleared'));
};

export const appendSearchHistory = async (keyword) => {
  const trimmed = (keyword || '').trim();
  if (trimmed) {
    const list = historySeed.filter((item) => item !== trimmed);
    list.unshift(trimmed);
    historySeed = list.slice(0, 10);
  }
  return withDelay(buildSuccess(historySeed.slice(0, 10), 'history updated'));
};
