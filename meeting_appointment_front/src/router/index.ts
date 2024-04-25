import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs';
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        name: "layout",
        component: ()=>import("@/layout/index.vue"),
    }
];
const router = createRouter({
    history: createWebHistory(),
    linkActiveClass: 'active',
    routes,
  });

export default router
