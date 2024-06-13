import { createRouter, createWebHashHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'


const routes:any = [
    {
        // 匹配不到路由时跳转登录页面
        path: '/:pathMatch(.*)*',
        component: () => import("@/views/login/login.vue"),
    },
    {
        path: '/login',
        name: 'login',
        meta: {
            title: '登录',
        },
        component: () => import('@/views/login/login.vue')
    },
    {
        path: '/',
        name: 'layout',
        component: () => import('@/layout/index.vue'),
        redirect: '/home',
        children: [
            {
                path: '/home',
                name: 'home',
                meta: {
                    title: '首页',
                },
                component: () => import('@/views/home/home.vue')
            },
            {
                path: '/history',
                name: 'history',
                meta: {
                    title: '历史记录',
                },
                component: () => import('@/views/history/history.vue')
            },
            {
                path: '/group',
                name: 'group',
                meta: {
                    title: '群组管理',
                },
                component: () => import('@/views/group/group.vue')
            },
            {
                path: '/createMeeting',
                name: 'createMeeting',
                meta: {
                    title: '创建会议',
                },
                component: () => import('@/views/create-meeting/create-meeting.vue')
            },
            {
                path: '/info',
                name: 'info',
                meta: {
                    title: '个人信息',
                },
                component: () => import('@/views/info/info.vue')
            },
        ]
    },
  ];
  const router = createRouter({
    history: createWebHashHistory(),
    routes,
  });
  router.beforeEach((to, from, next) => {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') as string); // 用户信息
    const token = userInfo?.accessToken; // token
    
    if (to.meta.title) {
        document.title = to.meta.title + '-中心会议预约';
      } else {
        document.title = '中心会议预约';
      }

    // 若token存在且目标路由为登录时 跳转到主页
    // if (token && to.path == '/login') {
    //     return next('/');
    // }
    // // 若token不存在且目标路由不为登录时 跳转到登录页面
    // if (!token && to.path != '/login') {
    //     return next('/login');
    // }
    next();
  })
  export default router
