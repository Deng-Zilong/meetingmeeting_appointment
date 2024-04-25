import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: "layout",
    component: () => import("@/layout/index.vue"),
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
