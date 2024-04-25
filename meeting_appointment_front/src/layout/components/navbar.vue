<template>
    <div class="nav-box">
       <div class="left">
               <!-- <el-menu mode="horizontal" :ellipsis="false">
                <div class="center">
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
                </div>
                    <el-menu-item index="1" class="first-menu-item">
                        <el-button type="primary" plain>Primary</el-button>
                    </el-menu-item>
                    <el-menu-item index="2">
                        <el-button type="primary" plain>Primary</el-button>
                    </el-menu-item>
                    <el-menu-item index="3">
                        <el-button type="primary" plain>Primary</el-button>
                    </el-menu-item nu-item>
                </el-menu> -->
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
                <el-button type="primary" plain :class="active == index ? 'active btn-margin' : 'btn-margin'" @click="handleMenu(index)" >{{item}}</el-button>
            </div>
            <!-- <router-link to="/" active-class="active" exact>广政通宝会议室</router-link>
            <router-link to="/room" active-class="active" exact>广政通宝会议室</router-link>
            <router-link to="/room" active-class="active" exact>广政通宝会议室</router-link>
            <router-link to="/room" active-class="active" exact>广政通宝会议室</router-link> -->

        </div>
       <div class="right">
        <el-button type="primary" plain>取消预约</el-button>
        <el-button type="primary">会议室预约</el-button>
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
        router.push('/room');
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
            // .center {
            //     display: flex;
            //     align-items: center;
            // }
            // .el-menu-item {
            //     padding-left: .125rem !important;
            // }
            // a {
            //     height: 2rem;
            //     line-height: 1.9rem;
            //     text-align: center;
            //     font-size: .875rem;
            //     padding: 0 1.1rem;
            //     margin: 0 .6rem 0 0;
            //     font-size: 14px;
            //     font-weight: normal;
            //     letter-spacing: 0.03em;
            //     color: #3E78F4;
            //     border: 1px solid #3E78F4;
            //     border-radius: .3125rem;
            //     background-color: #FFFFFF;
            // }
            .btn-margin {
                margin-right: .625rem;
            }
            .active {
                background-color:#409EFF;
            }
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