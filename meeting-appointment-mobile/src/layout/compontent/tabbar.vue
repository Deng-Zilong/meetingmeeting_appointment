<template>
    
    <van-tabbar v-model="active" route placeholder>
        <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
        <van-tabbar-item to="/createMeeting" icon="add-o">新建</van-tabbar-item>
        <van-tabbar-item to="/info" icon="friends-o">我的</van-tabbar-item>
    </van-tabbar>
</template>

<script setup lang="ts">
    import { ref } from 'vue';
    const active = ref(0);
</script>

<style lang="scss"></style>