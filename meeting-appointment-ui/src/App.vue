<template>
    <!-- <el-config-provider :locale="locale"> -->
    <router-view :key=routerKey></router-view>
    <!-- </el-config-provider> -->
</template>

<script setup lang="ts">
// import zhCn from "element-plus/dist/locale/zh-cn.mjs";
// const locale = zhCn;
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const routerKey = computed(() => {
    return route.path + Math.random()
})
</script>

<style scoped></style>
