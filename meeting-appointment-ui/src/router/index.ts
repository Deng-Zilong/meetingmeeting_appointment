import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        // 匹配不到路由时跳转登录页面
        path: '/:pathMatch(.*)*',
        component: () => import("@/views/login/login.vue"),
    },
    {
        path: '/',
        name: "layout",
        component: () => import("@/layout/index.vue"),
        children: [
            {
                // 首页
                path: "/",
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
                // 会议室状态
                path: "/meeting-state",
                name: "meeting-state",
                component: () => import("@/views/meeting-state/meeting-state.vue")
            },
            {
                // 会议室预约
                path: "/meeting-appoint",
                name: "meeting-appoint",
                component: () => import("@/views/meeting-appoint/meeting-appoint.vue")
            },
        ]
    },
    {
        path: "/login",
        name: "login",
        component: () => import("@/views/login/login.vue"),
    }
];
const router = createRouter({
    history: createWebHistory(),
    linkActiveClass: 'active',
    routes,
});

export default router
