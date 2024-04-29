<template>
    <div class="nav-box">
       <div class="left">
            <el-dropdown>
                <el-button type="primary">
                    中心会议室<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                    <el-dropdown-item>Action 1</el-dropdown-item>
                    <el-dropdown-item>Action 2</el-dropdown-item>
                    <el-dropdown-item>Action 3</el-dropdown-item>
                    <el-dropdown-item>Action 4</el-dropdown-item>
                    <el-dropdown-item>Action 5</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-divider direction="vertical" />
            <div v-for="(item, index) in centerRoomName">
                <el-button :class="active == index ? 'active btn-margin' : 'btn-margin'" @click="handleMenu(index)" >{{item}}</el-button>
            </div>
        </div>
       <div class="right">
        <router-link to="/history"><el-button>取消预约</el-button> </router-link>
        <router-link to="/meeting-appoint"><el-button type="primary">会议室预约</el-button></router-link>
       </div>
    </div>
</template>
<script setup lang="ts">
    import { ref } from "vue";
    import { useRouter } from "vue-router";
    import { ArrowDown } from '@element-plus/icons-vue';
    const centerRoomName = ['广政通宝会议室', 'EN-2F-02 恰谈室会议室', 'EN-2F-03 恰谈室会议室', 'EN-3F-02 恰谈室会议室'];
    let active = ref(-1);
    const router = useRouter();
    const handleMenu = (index: number) => {
        active.value = index;
        router.push('/meeting-state');
    }
</script>
<style scoped lang="scss">
    .nav-box {
        height: 3.75rem;
        opacity: 1;
        padding: 0 1.875rem;
        margin: 0.5rem 0 0 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: #FFFFFF;
        border-bottom: 1px solid var(--el-border-color);
        .left {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            .btn-margin {
                margin-right: .625rem;
            }
            .active {
                background-color:#409EFF;
            }
        }
        .right a {
            margin-left: .625rem;
        }
        .left, .right {
            margin: 0;
        }
    }
    .active {
      color: #fff;
      background-color: skyblue;
    }
</style>