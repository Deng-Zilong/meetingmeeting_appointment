<template>
    <div class="sidebar-box">
        <el-menu :router="true">
            <el-menu-item index="/home" class="menu-item" :class="currentPath == '/home' ? 'active' : ''">
                <img src="@/assets/img/homeicon.png" alt="">
                <span>首页概览</span>
            </el-menu-item>
            <el-menu-item index="/history" class="menu-item" :class="currentPath == '/history' ? 'active' : ''">
                <img src="@/assets/img/historyicon.png" alt="">
                <span>历史记录</span>
            </el-menu-item>
            <el-menu-item index="/group" class="menu-item" :class="currentPath == '/group' ? 'active' : ''">
                <img src="@/assets/img/groupicon.png" alt="">
                <span>群组管理</span>
            </el-menu-item>
            <el-menu-item index="/manage" class="menu-item" :class="currentPath == '/manage' ? 'active' : ''" v-show="level != 2">
                <img src="@/assets/img/manageicon.png" alt="">
                <span>后台管理</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>
<script setup lang="ts">
    import { ref, watch} from 'vue';
    import { useRouter} from 'vue-router'

    const userInfo = JSON.parse(localStorage.getItem('userInfo') as string); // 当前登录的用户信息
    const level = ref(userInfo?.level); // 用户等级 0超级管理员 1管理员 2普通用户

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
        margin-top: 8px;
        text-align: left;
        opacity: 1;
        background: #FFFFFF;
        .menu-item {
            margin-top: 9.6px;
        }
        .menu-item img {
            width: 39px;
            height: 19px;
            margin-right: 10px;
            padding: 0 10px;
        }
        .menu-item span{
            font-size: 17.6px;
            font-weight: 400;
            line-height: normal;
            letter-spacing: 0.05em;
            color: #0256FF;
        }
        .active {
            background: rgba(18, 115, 219, 0.05);
            border-right: #0256FF 2px solid;
        }
    }
</style>