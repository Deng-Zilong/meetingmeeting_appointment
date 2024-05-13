import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login/login.vue"),
  },
  {
    // 匹配不到路由时跳转登录页面
    path: '/:pathMatch(.*)*',
    component: () => import("@/views/login/login.vue"),
  },
  {
    path: '/',
    name: "layout",
    component: () => import("@/layout/index.vue"),
    redirect: '/home',
    children: [
      {
        // 首页
        path: "/home",
        name: "home",
        component: () => import("@/views/home/home.vue")
      },
      {
        // 历史记录
        path: "/history",
        name: "history",
        component: () => import("@/views/history/history.vue")
      },
      {
        // 群组管理
        path: "/group",
        name: "group",
        component: () => import("@/views/group/group.vue")
      },
      {
        // 后台管理
        path: "/manage",
        name: "manage",
        component: () => import("@/views/manage/manage.vue")
      },
      {
        // 会议室状态
        path: "/meeting-state",
        name: "meeting-state",
        component: () => import("@/views/meeting-state/meeting-state.vue")
      },
      {
        // 会议室预约
        path: "/meeting-appoint",
        name: "meeting-appoint",
        props: true,
        component: () => import("@/views/meeting-appoint/meeting-appoint.vue")
      },
    ]
  }
];
const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
    const token = JSON.parse(localStorage.getItem('userInfo') || '{}')?.accessToken;

    // 若目标路由为主页时（可能为扫码登录） 暂不判断token
    if(to.path == '/home') {
        return next();
    }
    // 若token存在且目标路由为登录时 跳转到主页
    if (token && to.path == '/login') {
        return next('/');
    }
    // 若token不存在且目标路由不为登录时 跳转到登录页面
    if (!token && to.path != '/login') {
        return next('/login');
    }
    next();
})
export default router
