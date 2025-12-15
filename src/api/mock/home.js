import { withDelay, buildSuccess } from './utils';

const carouselSeed = [
  {
    id: 101,
    title: '春招面试强化营',
    imageUrl: 'https://img.jobhub.mock/carousel/101.jpg',
    desc: '结构化回答 / 案例拆解 / 模拟面试',
    tag: '热门活动',
    link: '/topics'
  },
  {
    id: 102,
    title: '秋招冲刺答疑',
    imageUrl: 'https://img.jobhub.mock/carousel/102.jpg',
    desc: '今晚 19:00 · 专家 A 在线解答',
    tag: '直播预告',
    link: '/pro'
  },
  {
    id: 103,
    title: '0-1 写出亮点简历',
    imageUrl: 'https://img.jobhub.mock/carousel/103.jpg',
    desc: '附模板下载 · STAR 法实战示例',
    tag: '精选内容',
    link: '/search?keyword=简历'
  }
];

const hotActivitiesSeed = [
  {
    id: 201,
    title: '秋招面试强化营',
    time: '2025-05-25 19:00-21:00',
    participantCount: 580,
    link: '/activity/201'
  },
  {
    id: 202,
    title: '新手私信咨询 SOP',
    time: '2025-05-27 20:00-21:30',
    participantCount: 320,
    link: '/activity/202'
  },
  {
    id: 203,
    title: '行业交流 · 运营专场',
    time: '2025-05-29 19:30-20:30',
    participantCount: 430,
    link: '/activity/203'
  }
];

const recommendSeed = {
  默认: [
    {
      id: 6002,
      title: '0-1 写出亮点简历（学生版）',
      type: 'article',
      author: '求职导师 A',
      desc: '聚焦校内项目与实习实践，附模板下载',
      likeCount: 320,
      collectCount: 156,
      publishTime: '2025-05-18 10:00:00',
      link: '/api/v1/content/6002'
    },
    {
      id: 7001,
      title: '如何为运营岗位搭建作品集',
      type: 'article',
      author: '职场老手 B',
      desc: '拆解结构与呈现逻辑，附案例模板',
      likeCount: 186,
      collectCount: 92,
      publishTime: '2025-03-21 14:26:00',
      link: '/api/v1/content/7001'
    },
    {
      id: 5001,
      title: '运营岗位需要掌握哪些基础能力？',
      type: 'topic',
      author: '话题广场',
      desc: '专家和前辈在线答疑，互动 3.2k',
      likeCount: 0,
      collectCount: 0,
      publishTime: '2025-05-19 09:13:00',
      link: '/api/v1/topic/5001'
    },
    {
      id: 8002,
      title: '运营校招生互助群 · 上海站',
      type: 'group',
      author: '小组推荐',
      desc: '每日打卡面试题，互评简历',
      likeCount: 0,
      collectCount: 0,
      publishTime: '2025-05-19 20:41:00',
      link: '/api/v1/group/8002'
    },
    {
      id: 15001,
      title: 'STAR 法写简历亮点（实战教程）',
      type: 'video',
      author: '专家 A',
      desc: '教你用 STAR 法则提炼项目亮点',
      likeCount: 520,
      collectCount: 340,
      publishTime: '2025-04-20 09:00:00',
      link: '/api/v1/expert/resource/15001'
    }
  ],
  学生: [
    {
      id: 9001,
      title: '秋招时间线与内推渠道',
      type: 'article',
      author: '导师团',
      desc: '覆盖校招节点、投递节奏、面试准备',
      likeCount: 430,
      collectCount: 210,
      publishTime: '2025-05-10 08:00:00',
      link: '/api/v1/content/9001'
    },
    {
      id: 9002,
      title: '2025 秋招互助组 · 每日打卡',
      type: 'group',
      author: '小组公告',
      desc: '每日面试题与互评简历，监督打卡',
      likeCount: 0,
      collectCount: 0,
      publishTime: '2025-05-12 12:00:00',
      link: '/api/v1/group/9002'
    },
    {
      id: 9003,
      title: '产品经理经典案例拆解',
      type: 'topic',
      author: 'PM Mentor',
      desc: '手把手带你过一遍 STAR 讲故事',
      likeCount: 0,
      collectCount: 0,
      publishTime: '2025-05-15 10:00:00',
      link: '/api/v1/topic/9003'
    },
    {
      id: 9004,
      title: '秋招面试强化营（直播）',
      type: 'activity',
      author: '直播课程',
      desc: '结构化回答 / 案例拆解 / 模拟面试',
      likeCount: 0,
      collectCount: 0,
      publishTime: '2025-05-18 20:00:00',
      link: '/api/v1/activity/101'
    },
    {
      id: 9005,
      title: '校招 Offer 决策指南',
      type: 'article',
      author: '职场前辈',
      desc: '多维度评估薪资、成长、地域与团队',
      likeCount: 260,
      collectCount: 130,
      publishTime: '2025-05-20 09:30:00',
      link: '/api/v1/content/9005'
    }
  ]
};

const identityKey = (identity) => {
  if (!identity) return '默认';
  if (recommendSeed[identity]) return identity;
  return '默认';
};

const paginate = (list, pageNum = 1, pageSize = 5) => {
  const total = list.length;
  if (!total) {
    return { total: 0, pageNum, pageSize, list: [] };
  }
  const start = ((pageNum - 1) * pageSize) % total;
  const slice = [];
  for (let i = 0; i < pageSize; i += 1) {
    const targetIndex = (start + i) % total;
    slice.push(list[targetIndex]);
  }
  return { total, pageNum, pageSize, list: slice };
};

export const getHomeOverview = async ({ identity } = {}) => {
  const key = identityKey(identity);
  const recommendedContent = paginate(recommendSeed[key], 1, 5);
  return withDelay(
    buildSuccess({
      userIdentity: identity || 'student',
      carousel: carouselSeed,
      hotActivities: hotActivitiesSeed,
      recommendedContent
    })
  );
};

export const refreshRecommend = async ({ identity, pageNum = 2, pageSize = 5 } = {}) => {
  const key = identityKey(identity);
  const recommendedContent = paginate(recommendSeed[key], pageNum, pageSize);
  return withDelay(buildSuccess(recommendedContent, 'refresh success'));
};
