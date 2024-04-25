<template>
    <div class="sidebar-box">
        <el-menu>
            <el-menu-item index="2">
                <el-icon><icon-menu /></el-icon>
                <span>Navigator Two</span>
            </el-menu-item>
            <el-menu-item index="3" disabled>
                <el-icon><document /></el-icon>
                <span>Navigator Three</span>
            </el-menu-item>
            <el-menu-item index="4">
                <el-icon><setting /></el-icon>
                <span>Navigator Four</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>
<script setup lang="ts">
    import {
    Document,
    Menu as IconMenu,
    Setting,
} from '@element-plus/icons-vue'
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