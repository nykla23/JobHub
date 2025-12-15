import { withDelay, buildSuccess } from './utils';

const profile = {
  nickname: '叶同学',
  identity: '学生',
  careerStage: '2025 届应届生 · 运营方向',
  intro: '2025 届准毕业生，目标互联网运营岗，正在积极准备秋招/校招',
  focusArea: '互联网运营 · 新媒体 · 用户增长',
  city: '上海',
  avatar: '',
  stats: {
    views: 5230,
    likes: 1280,
    followers: 34,
    collections: 7
  }
};

let privacySettings = {
  hideCompany: false,
  hideSalary: true,
  hideHistory: false
};

const historyList = [
  {
    id: 5001,
    type: 'topic',
    title: '运营岗位需要掌握哪些基础能力？',
    source: '话题广场',
    viewTime: '2025-05-20 09:13'
  },
  {
    id: 8002,
    type: 'group',
    title: '运营校招生互助群 · 上海站',
    source: '小组推荐',
    viewTime: '2025-05-19 20:41'
  }
];

const circleList = [
  {
    id: 8001,
    name: '2025 秋招互助组',
    tags: ['应届生', '秋招'],
    role: '成员',
    memberCount: 3200,
    activity: '每日打卡面试题'
  },
  {
    id: 8003,
    name: '运营成长营',
    tags: ['运营', '成长'],
    role: '创建者',
    memberCount: 980,
    activity: '每周案例拆解'
  }
];

const collectionList = [
  {
    id: 6001,
    title: '如何回答「你为什么选择运营岗位」？',
    type: 'content',
    folderName: '面试技巧',
    collectTime: '2025-05-15 16:30',
    link: '/api/v1/content/6001'
  },
  {
    id: 5002,
    title: '秋招面试准备清单',
    type: 'topic',
    folderName: '秋招必看',
    collectTime: '2025-05-18 09:00',
    link: '/api/v1/topic/5002'
  }
];

const publishList = [
  {
    id: 7001,
    title: '如何为运营岗位搭建一个清晰的作品集结构？',
    type: 'article',
    status: 'passed',
    publishTime: '2025-03-21 14:26',
    viewCount: 1200,
    likeCount: 186,
    isTop: true
  },
  {
    id: 7002,
    title: '入职 90 天避坑清单',
    type: 'topic',
    status: 'pending',
    publishTime: '2025-05-01 12:00',
    viewCount: 430,
    likeCount: 56,
    isTop: false
  }
];

export const getProfileOverview = async () =>
  withDelay(
    buildSuccess({
      profile,
      stats: profile.stats,
      privacy: privacySettings
    })
  );

export const updateProfile = async (payload) => {
  Object.assign(profile, payload);
  return withDelay(
    buildSuccess({
      profile
    })
  );
};

export const updatePrivacy = async (payload) => {
  privacySettings = { ...privacySettings, ...payload };
  return withDelay(buildSuccess(privacySettings));
};

export const getHistory = async () =>
  withDelay(
    buildSuccess({
      list: historyList,
      total: historyList.length
    })
  );

export const getMyCircles = async () =>
  withDelay(
    buildSuccess({
      list: circleList,
      total: circleList.length
    })
  );

export const getMyCollections = async () =>
  withDelay(
    buildSuccess({
      list: collectionList,
      total: collectionList.length
    })
  );

export const getMyPublish = async () =>
  withDelay(
    buildSuccess({
      list: publishList,
      total: publishList.length
    })
  );

