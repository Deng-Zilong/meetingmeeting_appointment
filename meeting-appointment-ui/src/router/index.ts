import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: "/login",
    name: "login",
    meta: {
        title: '登录',
    },
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
        meta: {
            title: '首页',
        },
        component: () => import("@/views/home/home.vue")
      },
      {
        // 历史记录
        path: "/history",
        name: "history",
        meta: {
            title: '历史记录',
        },
        component: () => import("@/views/history/history.vue")
      },
      {
        // 群组管理
        path: "/group",
        name: "group",
        meta: {
            title: '群组管理',
        },
        component: () => import("@/views/group/group.vue")
      },
      {
        // 后台管理
        path: "/manage",
        name: "manage",
        meta: {
            title: '后台管理',
            requiresAuth: true,
            roles: [0, 1], // 只有超级管理员有权限  用户等级 0超级管理员 1管理员 2普通用户
        },
        component: () => import("@/views/manage/manage.vue")
      },
      {
        // 会议室状态
        path: "/meeting-state",
        name: "meeting-state",
        meta: {
            title: '会议预约',
        },
        component: () => import("@/views/meeting-state/meeting-state.vue")
      },
      {
        // 会议室预约
        path: "/meeting-appoint",
        name: "meeting-appoint",
        meta: {
            title: '会议创建',
        },
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
    const userInfo = JSON.parse(localStorage.getItem('userInfo') as string); // 用户信息
    const token = userInfo?.accessToken; // token
    const level = userInfo?.level; // 用户等级
    const roles = to.meta?.roles as any[]; // 模块权限数组
    
    if (to.meta.title) {
        document.title = to.meta.title + '-中心会议预约';
      } else {
        document.title = '中心会议预约';
      }

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
    // 若目标路由为后台管理页面 且 无权限时跳转到首页
    if (to.path == '/manage' && !roles.includes(level)) {
        return next('/');
    }
    next();
})
export default router
