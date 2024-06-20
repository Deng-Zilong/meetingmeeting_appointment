import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css';
// import TabBar from "./layout/tabbar/tabbar.vue";

const app = createApp(App)
const pinia = createPinia() // 创建pinia实例
pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)
app.use(Vant);
// app.component('TabBar', TabBar);
app.use(ElementPlus, {
  locale: zhCn,
})

app.mount('#app')
