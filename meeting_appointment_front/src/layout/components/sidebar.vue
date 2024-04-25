<template>
    <div class="sidebar-box">
        <el-menu :router="true">
            <el-menu-item index="/">
                <el-icon><icon-menu /></el-icon>
                <span>首页概览</span>
            </el-menu-item>
            <el-menu-item index="/history">
                <el-icon><document /></el-icon>
                <span>历史记录</span>
            </el-menu-item>
            <el-menu-item index="/group">
                <el-icon><setting /></el-icon>
                <span>群组管理</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>
<script setup lang="ts">
    import {Document, Menu as IconMenu, Setting} from '@element-plus/icons-vue'
</script>
<style scoped lang="scss">
    .sidebar-box {
        height: 86vh;
        margin-top: .5rem;
        text-align: left;
        opacity: 1;
        background: #FFFFFF;
        border-right: 1px solid var(--el-border-color);
    }
</style>