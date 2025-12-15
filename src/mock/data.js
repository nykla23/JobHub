// 集中 mock 数据（前端占位，可替换为接口响应）
export const recommendByRole = {
  默认: [
    { tag: '求助帖', title: '秋招面试如何准备产品案例', desc: '想冲刺秋招面试，求实战建议与案例拆解', author: '小林·应届', stats: '128 评论 · 512 互动' },
    { tag: '专家干货', title: '简历优化：去掉这些低质描述', desc: 'STAR 法则写亮点，附下载模板', author: '职场顾问 Ada', stats: '4.8★ · 2.1k 阅读' },
    { tag: '热门话题', title: '第一份实习该不该选大厂外包？', desc: '前辈避坑指南，真实经历分享', author: '老王·运营', stats: '86 回复 · A级' },
    { tag: '圈子动态', title: '2025 届秋招互助组 · 打卡冲刺', desc: '每日打卡面试题，互评简历', author: '小组公告', stats: '3.2k 成员' }
  ],
  学生: [
    { tag: '简历模板', title: '0-1 写出亮点简历（学生版）', desc: '聚焦校内项目与实习实践，模板下载', author: '运营学姐', stats: '4.7★ · 1.8k 下载' },
    { tag: '秋招攻略', title: '秋招时间线与内推渠道合集', desc: '校招节点、投递节奏、面试准备', author: '导师团', stats: '2.3k 阅读 · 360 收藏' },
    { tag: '圈子', title: '2025 届秋招互助组 · 每日打卡', desc: '每日面试题与互评简历，监督打卡', author: '小组公告', stats: '3.2k 成员' },
    { tag: '面试', title: '产品经理经典案例拆解', desc: '手把手带你过一遍 STAR 讲故事', author: 'PM Mentor', stats: '128 评论 · 512 互动' }
  ],
  职场菜鸟: [
    { tag: '避坑', title: '入职 90 天避坑清单', desc: '汇总新人常见误区与沟通技巧', author: '职场老手', stats: '98 回复 · A级' },
    { tag: '心理', title: '适应期焦虑如何调适', desc: '专家心理疏导音频课', author: '心理咨询师', stats: '4.9★ · 5.4k 收听' },
    { tag: '圈子', title: '职场新人打怪升级小组', desc: '日报周报模板、反馈技巧', author: '组长', stats: '1.1k 成员' },
    { tag: '工具', title: '提升效率的 10 个必备工具', desc: '从日程管理到会议纪要模板', author: '效率教练', stats: '1.3k 收藏' }
  ],
  专家: [
    { tag: '创作', title: '发布你的专家内容，优先曝光', desc: '文字/音频/视频/PPT 模板，S/A 评级机制', author: '平台提示', stats: '优先推荐位' },
    { tag: '榜单', title: '热门专家榜曝光规则', desc: '综合评分 + 互动加权，榜单前列优先推荐', author: '运营', stats: '榜单每周更新' },
    { tag: '私信咨询', title: '高效处理私信咨询的 SOP', desc: '如何筛选、回复、引导服务', author: '资深顾问', stats: '4.6★ · 820 阅读' },
    { tag: '答疑话题', title: '发起专家答疑话题，拉新互动', desc: '示例话题与运营模板', author: '运营助手', stats: '模板下载' }
  ],
  职场老手: [
    { tag: '分享', title: '你的经验可以帮助新人', desc: '申请创作者/小组管理员，获得曝光', author: '平台提示', stats: '高贡献用户权益' },
    { tag: 'offer 选择', title: '两份 offer 如何权衡', desc: '真实案例与决策框架', author: '职场前辈', stats: '76 回复 · A级' },
    { tag: '圈子', title: '行业交流组（运营/产品/技术）', desc: '与同行深度讨论与机会互换', author: '行业组', stats: '2.4k 成员' },
    { tag: '导师', title: '成为导师，提供 1v1 咨询', desc: '入驻流程与收益规则', author: '运营', stats: '入驻优先通道' }
  ]
};

export const topicsByRole = {
  默认: Array.from({ length: 6 }).map((_, i) => ({
    id: `topic-${i}`,
    title: `话题标题占位 ${i + 1}`,
    meta: '所属标签 · 参与人数 · 互动次数 · 最新回复时间',
    rating: 'A级',
    comments: 86,
    favorites: 34
  })),
  学生: [
    { id: 'topic-stu-1', title: '秋招面经互助', meta: '秋招面试 · 参与 1.2k · 最新 5 分钟前', rating: 'A级', comments: 180, favorites: 90 },
    { id: 'topic-stu-2', title: '第一份实习怎么选', meta: '实习选择 · 参与 800 · 最新 20 分钟前', rating: 'A级', comments: 92, favorites: 40 }
  ],
  职场菜鸟: [
    { id: 'topic-new-1', title: '试用期如何要反馈', meta: '沟通反馈 · 参与 560 · 最新 10 分钟前', rating: 'A级', comments: 70, favorites: 28 },
    { id: 'topic-new-2', title: '入职 90 天避坑清单', meta: '避坑 · 参与 1k · 最新 1 小时前', rating: 'S级', comments: 210, favorites: 140 }
  ],
  职场老手: [
    { id: 'topic-old-1', title: '管理进阶的关键节点', meta: '管理提升 · 参与 320 · 最新 3 小时前', rating: 'A级', comments: 48, favorites: 30 }
  ]
};

export const circlesByRole = {
  默认: Array.from({ length: 6 }).map((_, i) => ({
    id: `circle-${i}`,
    title: `为你推荐小组 ${i + 1}`,
    badge: '应届生专属',
    desc: '小组简介占位，展示成员数、最新活动、加入方式',
    meta: '成员 1.2k · 最新活动：简历互评'
  })),
  学生: [
    { id: 'circle-stu-1', title: '2025 秋招互助组', badge: '应届生', desc: '每日打卡面试题，互评简历', meta: '成员 3.2k · 活动：打卡' },
    { id: 'circle-stu-2', title: '实习内推交流', badge: '实习', desc: '内推机会与实习经验分享', meta: '成员 1.8k · 活动：分享会' }
  ],
  职场菜鸟: [
    { id: 'circle-new-1', title: '职场新人打怪升级', badge: '新人', desc: '日报周报模板、反馈技巧', meta: '成员 1.1k · 活动：模板分享' },
    { id: 'circle-new-2', title: '试用期互助', badge: '试用期', desc: '试用期经验交流与避坑', meta: '成员 960 · 活动：答疑' }
  ],
  职场老手: [
    { id: 'circle-old-1', title: '行业交流 · 运营', badge: '行业', desc: '行业趋势、岗位机会、经验分享', meta: '成员 2.4k · 活动：直播分享' }
  ]
};

export const proResources = Array.from({ length: 6 }).map((_, i) => ({
  id: `res-${i}`,
  title: `资源标题 ${i + 1}`,
  desc: '职业规划/心理疏导/简历优化/面试技巧/职场适应 内容摘要',
  badge: i % 2 === 0 ? 'S级' : 'A级',
  meta: '评分 4.8 · 观看 12k · 收藏 3.4k'
}));

export const experts = Array.from({ length: 6 }).map((_, i) => ({
  id: `expert-${i}`,
  name: `专家姓名 ${i + 1}`,
  desc: '资质/擅长领域/用户评价',
  rating: '4.8',
  tags: ['职业规划', '简历优化']
}));

export const searchDatasets = {
  话题: [
    { id: 'topic-1', title: '秋招面试准备清单', desc: '包含常见面试题、案例拆解、STAR 讲故事模板', meta: '热度 8.6k · 标签：秋招/面试' },
    { id: 'topic-2', title: '第一份实习怎么选', desc: '大厂外包 vs 中小正编，前辈经验对比', meta: '热度 5.3k · 标签：实习/选择' },
    { id: 'topic-3', title: '职场新人避坑合集', desc: '试用期沟通、日报周报模板、反馈技巧', meta: '热度 6.1k · 标签：新人/沟通' }
  ],
  内容: [
    { id: 'content-1', title: '简历优化：去掉这些低质描述', desc: '专家教你用 STAR 法写亮点，附下载模板', meta: '评分 4.8 · 收藏 3.2k' },
    { id: 'content-2', title: '面试技巧 30 讲', desc: '产品/运营/技术通用的结构化回答示例', meta: '评分 4.7 · 评论 1.1k' },
    { id: 'content-3', title: '职场心理疏导音频课', desc: '缓解焦虑、提升情绪复原力的实用练习', meta: '评分 4.9 · 收听 5.4k' }
  ],
  圈子: [
    { id: 'circle-1', title: '2025 秋招互助组', desc: '每日打卡面试题，互评简历，审核入组', meta: '成员 3.2k · 最新活动：打卡第 12 天' },
    { id: 'circle-2', title: '职场新人打怪升级', desc: '日报周报模板、反馈与沟通技巧', meta: '成员 1.1k · 最新活动：周报互评' },
    { id: 'circle-3', title: '行业交流 · 运营', desc: '行业趋势、岗位机会、经验分享', meta: '成员 2.4k · 最新活动：直播分享' }
  ],
  用户: [
    { id: 'user-1', title: '小林 · 2025 届', desc: '目标运营岗，分享秋招面经与实习日常', meta: '关注 1.2k · 获赞 3.4k' },
    { id: 'user-2', title: '运营学姐', desc: '简历点评/面试辅导志愿者', meta: '关注 3.1k · 回答 420' },
    { id: 'user-3', title: '职场老王', desc: '5 年运营经理，避坑经验分享', meta: '关注 2.3k · 获赞 5.6k' }
  ],
  专家: [
    { id: 'expert-1', title: 'Ada · 职业规划师', desc: '擅长校招/转岗规划，支持 1v1 咨询', meta: '综合评分 4.9 · 内容 32 篇' },
    { id: 'expert-2', title: '心理咨询师 Kevin', desc: '职场心理疏导，缓解焦虑与职场适应', meta: '综合评分 4.8 · 内容 18 篇' },
    { id: 'expert-3', title: 'PM Mentor', desc: '产品面试辅导/案例拆解', meta: '综合评分 4.7 · 内容 26 篇' }
  ]
};

export const messagesMock = {
  interaction: [
    { id: 'int-1', title: '你的帖子收到 3 条新评论', desc: '点击查看并回复，保持高互动', time: '2 分钟前' },
    { id: 'int-2', title: '有人点赞了你的回答', desc: '《秋招面试如何准备产品案例》', time: '10 分钟前' }
  ],
  system: [
    { id: 'sys-1', title: '内容审核通过', desc: '你的发布已上线，去查看曝光数据', time: '1 小时前' },
    { id: 'sys-2', title: '活动预告', desc: '今晚 19:00 职场心理疏导直播开始', time: '3 小时前' }
  ],
  circle: [
    { id: 'cir-1', title: '小组公告', desc: '秋招互助组发布了新的打卡题目', time: '5 分钟前' },
    { id: 'cir-2', title: '管理员公告', desc: '请遵守发帖规范，违规将禁言', time: '昨天' }
  ]
};

export const chatroomsMock = {
  upcoming: [{ id: 'chat-up-1', title: '秋招冲刺答疑', time: '今晚 19:00', host: '专家 A', scope: '全员可见' }],
  live: [{ id: 'chat-live-1', title: '产品面试复盘', host: 'PM Mentor', desc: '正在讲解案例拆解思路', audience: 320 }],
  ended: [{ id: 'chat-end-1', title: '职场新人避坑', note: '查看精华笔记', link: '/topics' }],
  messages: {
    list: [
      {
        id: 1,
        nickname: 'HR Jane',
        avatar: '',
        content: '大家好，欢迎来到秋招求职聊天室，有问题可以直接提问~',
        sendTime: '19:00',
        isHost: true
      },
      {
        id: 2,
        nickname: '叶同学',
        avatar: '',
        content: '请问 HR，简历上的项目经历需要写多少个合适？',
        sendTime: '19:05',
        isHost: false
      },
      {
        id: 3,
        nickname: '专家 A',
        avatar: '',
        content: '建议围绕岗位核心能力挑选 2-3 个代表性项目，并结合成果量化。',
        sendTime: '19:07',
        isHost: true
      }
    ]
  }
};

export const creditLogs = [
  { date: '2025-01-01', reason: '广告/违规内容', delta: -10, result: '内容删除，通知已推送' },
  { date: '2025-01-15', reason: '优质回答被采纳', delta: 5, result: '信用回升，曝光加权' }
];
