<template>
    <div class="sidebar-box">
        <el-menu :router="true">
            <el-menu-item index="/home" :class="currentPath == '/home' ? 'active menu-item' : ' menu-item'">
                <img src="@/assets/img/homeicon.png" alt="">
                <span>首页概览</span>
            </el-menu-item>
            <el-menu-item index="/history" :class="currentPath == '/history' ? 'active menu-item' : ' menu-item'">
                <img src="@/assets/img/historyicon.png" alt="">
                <span>历史记录</span>
            </el-menu-item>
            <el-menu-item index="/group" class="menu-item" :class="currentPath == '/group' ? 'active menu-item' : ' menu-item'">
                <img src="@/assets/img/manageicon.png" alt="">
                <span>群组管理</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>
<script setup lang="ts">
    import { ref, watch} from 'vue';
    import { useRouter} from 'vue-router'

    const router = useRouter();
    // 当前路由
    let currentPath = ref('/home');
    // 监听路由变化 实现菜单选中后的状态
    watch(() => router.currentRoute.value.path, (newValue: any) => {
            currentPath.value = newValue;    
        },{ immediate: true }
    )
    
</script>
<style scoped lang="scss">
    .sidebar-box {
        height: 85vh;
        margin-top: .5rem;
        text-align: left;
        opacity: 1;
        background: #FFFFFF;
        .menu-item {
            margin-top: 1rem;
        }
        .menu-item img {
            margin-right: .625rem;
        }
        .menu-item span{
            font-size: 1rem;
            font-weight: 500;
            line-height: normal;
            letter-spacing: 0.05em;
            color: #0256FF;
        }
        .active {
            background: rgba(18, 115, 219, 0.05);
            border-right: #0256FF .125rem solid;
        }
    }
</style>