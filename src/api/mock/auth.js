import { withDelay, buildSuccess } from './utils';

const mockAccount = {
  account: 'test@example.com',
  password: '123456',
  nickname: '测试用户',
  identity: '学生',
  token: 'mock-token-123',
  userId: 1001,
  isAdmin: true
};

let pendingIdentity = mockAccount.identity;

export const login = async ({ account, username, password }) => {
  const loginAccount = account || username;
  if (loginAccount === mockAccount.account && password === mockAccount.password) {
    return withDelay(
      buildSuccess({
        token: mockAccount.token,
        userInfo: {
          id: mockAccount.userId,
          nickname: mockAccount.nickname,
          identity: pendingIdentity,
          avatar: '',
          isAdmin: mockAccount.isAdmin
        }
      })
    );
  }
  return withDelay(
    {
      code: 401,
      message: '账号或密码错误'
    },
    300
  );
};

export const register = async ({ username, password, identityTag, emotionTag, signature }) => {
  mockAccount.account = username;
  mockAccount.password = password;
  mockAccount.identityTag = identityTag;
  mockAccount.emotionTag = emotionTag;
  mockAccount.signature = signature;
  mockAccount.isAdmin = false;
  return withDelay(
    buildSuccess({
      id: mockAccount.userId,
      userId: mockAccount.userId,
      username: username,
      message: '注册成功'
    })
  );
};

export const selectIdentity = async ({ userId, careerStage, identity }) => {
  if (userId !== mockAccount.userId) {
    return withDelay({ code: 404, message: '用户不存在' }, 200);
  }
  const nextIdentity = careerStage || identity;
  pendingIdentity = nextIdentity;
  return withDelay(
    buildSuccess({
      userId,
      identity: nextIdentity,
      updateTime: new Date().toISOString()
    })
  );
};

export const requestResetCode = async ({ email }) =>
  withDelay(
    buildSuccess({
      email,
      expireTime: 600,
      code: '123456'
    })
  );

export const resetPassword = async ({ username, newPassword, confirmPassword }) => {
  if (username !== mockAccount.account) {
    return withDelay({ code: 404, message: '账号不存在' }, 200);
  }
  if (newPassword !== confirmPassword) {
    return withDelay({ code: 400, message: '两次密码不一致' }, 200);
  }
  mockAccount.password = newPassword;
  return withDelay(buildSuccess({}, '密码重置成功'));
};

