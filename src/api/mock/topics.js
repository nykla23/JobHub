import { withDelay, buildSuccess } from './utils';

const topicPool = [
  {
    id: 5001,
    title: '运营岗位需要掌握哪些基础能力？',
    level: 'A',
    tags: ['秋招', '面试'],
    participantCount: 3200,
    interactionCount: 8600,
    latestReplyTime: '2025-05-20 14:55:00',
    intro: '专家和前辈在线答疑，互动 3.2k',
    identities: ['学生', '职场菜鸟'],
    hotValue: 8600,
    chatroomId: 'chat-live-1'
  },
  {
    id: 5002,
    title: '第一份实习怎么选？',
    level: 'A',
    tags: ['实习', '择业'],
    participantCount: 2300,
    interactionCount: 7600,
    latestReplyTime: '2025-05-20 14:55:00',
    intro: '大厂外包 vs 创业核心，真实经历对比',
    identities: ['学生'],
    hotValue: 7600,
    chatroomId: 'chat-up-1'
  },
  {
    id: 5003,
    title: '秋招面试准备清单',
    level: 'S',
    tags: ['秋招', '面试'],
    participantCount: 4100,
    interactionCount: 9600,
    latestReplyTime: '2025-05-20 10:00:00',
    intro: '包含常见面试题、案例拆解、STAR 模板',
    identities: ['学生', '职场菜鸟'],
    hotValue: 9600
  },
  {
    id: 5004,
    title: '入职 90 天避坑清单',
    level: 'S',
    tags: ['职场新人', '避坑'],
    participantCount: 1800,
    interactionCount: 5400,
    latestReplyTime: '2025-05-19 21:00:00',
    intro: '试用期沟通、反馈、周报模板合集',
    identities: ['职场菜鸟'],
    hotValue: 6800
  },
  {
    id: 5005,
    title: '管理进阶的关键节点',
    level: 'A',
    tags: ['管理', '进阶'],
    participantCount: 320,
    interactionCount: 1800,
    latestReplyTime: '2025-05-19 18:30:00',
    intro: '前辈拆解从骨干到管理的关键能力',
    identities: ['职场老手'],
    hotValue: 3200
  },
  {
    id: 5006,
    title: '内容创作者评级与曝光机制',
    level: 'A',
    tags: ['专家', '创作指南'],
    participantCount: 520,
    interactionCount: 2600,
    latestReplyTime: '2025-05-18 20:00:00',
    intro: 'S/A 评级规则、榜单曝光与扶持政策',
    identities: ['专家'],
    hotValue: 2600
  },
  {
    id: 5007,
    title: '行业交流 · 运营专场',
    level: 'S',
    tags: ['行业交流', '运营'],
    participantCount: 2400,
    interactionCount: 6400,
    latestReplyTime: '2025-05-18 22:00:00',
    intro: '趋势讨论、岗位机会互换、直播纪要',
    identities: ['职场老手', '职场菜鸟'],
    hotValue: 7200
  },
  {
    id: 5008,
    title: '校招 Offer 决策指南',
    level: 'A',
    tags: ['校招', 'offer选择'],
    participantCount: 1500,
    interactionCount: 4300,
    latestReplyTime: '2025-05-18 11:20:00',
    intro: '多维度评估薪资、成长、地域与团队',
    identities: ['学生'],
    hotValue: 4300
  }
];

const matchIdentity = (item, identity) => {
  if (!identity) return true;
  if (!item.identities || !item.identities.length) return true;
  return item.identities.includes(identity);
};

const normalizeTag = (tag = '') => tag.trim();

const sortTopics = (list, sort = 'hot') => {
  if (sort === 'time') {
    return list.slice().sort(
      (a, b) => new Date(b.latestReplyTime).getTime() - new Date(a.latestReplyTime).getTime()
    );
  }
  if (sort === 'hot') {
    return list.slice().sort((a, b) => (b.hotValue || 0) - (a.hotValue || 0));
  }
  return list;
};

export const fetchTopicList = async ({
  identity,
  tag,
  level,
  sort = 'hot',
  pageNum = 1,
  pageSize = 10
} = {}) => {
  const normalizedTag = normalizeTag(tag);
  let list = topicPool.filter((item) => matchIdentity(item, identity));

  if (normalizedTag) {
    list = list.filter((item) => item.tags?.includes(normalizedTag));
  }
  if (level) {
    list = list.filter((item) => (item.level || '').toUpperCase() === level.toUpperCase());
  }

  list = sortTopics(list, sort);
  const total = list.length;
  const start = (Number(pageNum) - 1) * Number(pageSize || 10);
  const slice = list.slice(start, start + Number(pageSize || 10)).map((item) => ({
    ...item,
    link: `/api/v1/topic/${item.id}`
  }));

  return withDelay(
    buildSuccess({
      total,
      pageNum: Number(pageNum),
      pageSize: Number(pageSize || 10),
      list: slice
    })
  );
};

