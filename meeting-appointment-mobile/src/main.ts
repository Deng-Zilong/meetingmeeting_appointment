import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css';
// import TabBar from "./layout/tabbar/tabbar.vue";

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Vant);
// app.component('TabBar', TabBar);

app.mount('#app')
