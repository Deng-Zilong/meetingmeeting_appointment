import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  base: "./",
  plugins: [
    vue(),
    VueDevTools(),
  ],
  server: {
    port: 5714, // 默认端口
    // hmr:true,
    // proxy: {
    //   '/api': {
    //     target: (import.meta as any).VITE_BASE_URL, //目标url
    //     changeOrigin: true, //支持跨域
    //     rewrite: (path) => path.replace(/^\/api/, ""),
    //     //重写路径,替换/api
    //   }
    // }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: { 
      scss: {
        additionalData: '@import "./src/styles/global.scss";' // 添加公共样式
      }
    }
  },
})
