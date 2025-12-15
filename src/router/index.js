import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

const router = new Router({
  mode: 'history',
  scrollBehavior() {
    return { x: 0, y: 0 };
  },
  routes: [
    { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
    { path: '/search', name: 'Search', component: () => import('@/views/Search.vue') },
    { path: '/topics', name: 'Topics', component: () => import('@/views/Topics.vue') },
    { path: '/topic/:id', name: 'TopicDetail', component: () => import('@/views/TopicDetail.vue') },
    { path: '/activity/:id', name: 'ActivityDetail', component: () => import('@/views/ActivityDetail.vue') },
    { path: '/circles', name: 'Circles', component: () => import('@/views/Circles.vue') },
    { path: '/circle/:id', name: 'CircleDetail', component: () => import('@/views/CircleDetail.vue') },
    { path: '/chatroom/:id', name: 'Chatroom', component: () => import('@/views/Chatroom.vue') },
    { path: '/pro', name: 'ProCenter', component: () => import('@/views/ProCenter.vue') },
    { path: '/pro/expert/:id', name: 'ExpertProfile', component: () => import('@/views/ExpertProfile.vue') },
    { path: '/pro/apply', name: 'ExpertApply', component: () => import('@/views/ExpertApply.vue') },
    { path: '/creator', name: 'CreatorDashboard', component: () => import('@/views/CreatorDashboard.vue') },
    { path: '/profile/settings', name: 'ProfileSettings', component: () => import('@/views/ProfileSettings.vue') },
    { path: '/profile/edit', name: 'ProfileEdit', component: () => import('@/views/ProfileEdit.vue') },
    { path: '/profile/credit', name: 'Credit', component: () => import('@/views/Credit.vue') },
    { path: '/profile/me', name: 'ProfileMe', component: () => import('@/views/Profile.vue') },
    { path: '/profile/:id', name: 'Profile', component: () => import('@/views/Profile.vue') },
    { path: '/publish/pending', name: 'PublishPending', component: () => import('@/views/PublishPending.vue') },
    { path: '/messages', name: 'Messages', component: () => import('@/views/Messages.vue') },
    { path: '/auth/login', name: 'Login', component: () => import('@/views/auth/Login.vue') },
    { path: '/auth/register', name: 'Register', component: () => import('@/views/auth/Register.vue') },
    { path: '/auth/forgot', name: 'ForgotPassword', component: () => import('@/views/auth/ForgotPassword.vue') },
    {
      path: '/auth/select-identity',
      name: 'SelectIdentity',
      component: () => import('@/views/auth/SelectIdentity.vue')
    },
    { path: '/404', name: 'NotFound', component: () => import('@/views/NotFound.vue') },
    { path: '*', redirect: '/404' }
  ]
});

const publicPaths = ['/auth/login', '/auth/register', '/auth/forgot', '/auth/select-identity'];

router.beforeEach((to, from, next) => {
  if (publicPaths.includes(to.path)) {
    next();
    return;
  }
  let token = null;
  try {
    token = localStorage.getItem('jobhub_token');
  } catch (e) {
    token = null;
  }
  if (!token) {
    next('/auth/login');
    return;
  }
  next();
});

export default router;
