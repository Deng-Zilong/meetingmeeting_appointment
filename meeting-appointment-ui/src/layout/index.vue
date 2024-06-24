<template>
  <div class="layout-container">
    <div class="layout_header">
      <div class="logo" @click="toHome">
        <img src="@/assets/img/logo.png" alt="">
      </div>
      <div class="avatar">
        <el-popover placement="top-start" trigger="hover" class="el-popover">
          <template #reference>
            <el-avatar>
              <img src="@/assets/img/avatar.png" alt="">
            </el-avatar>
          </template>
          <div class="exit-login" @click="exitLogin">退出登录</div>
        </el-popover>
      </div>
    </div>
    <div class="common-layout">
      <el-container>
        <el-header>
          <Navbar />
        </el-header>
        <el-container>
          <el-aside width="15.625rem">
            <Sidebar />
          </el-aside>
          <el-main class="main">
            <RouterView></RouterView>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router'
import Navbar from "@/layout/components/navbar.vue";
import Sidebar from "@/layout/components/sidebar.vue";
import { useUserStore } from '@/stores/user'
import { deleteInfo } from '@/request/api/login';
// 用户信息
const userStore = useUserStore();
const router = useRouter();
const userInfo = JSON.parse(localStorage.getItem('userInfo') as string); // 用户信息

/**
 * @description 退出登录
 */
const exitLogin = () => {
  const userId = JSON.parse(localStorage.getItem('userInfo') as string).userId; // 用户id
  deleteInfo(userId)
    .then(res=> {
        // 重置用户信息
        userStore.resetUserInfo();
        router.replace('/login');
    })
    .catch(err => {})
    .finally(() => {})
};

/**
 * @description 点击logo 回到主页
 */
const toHome = () => {
  router.push('/home');
}
</script>
<style scoped lang="scss">
.layout-container {
  .layout_header {
    width: 100%;
    height: 60px;
    padding: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #FFFFFF;
    box-shadow: 0px 3px 8px 0px rgba(0, 0, 0, 0.1);

    .logo,
    .avatar {
      margin: 0;

      .el-avatar {
        width: 50px;
        height: 50px;
      }
    }
  }

}

.common-layout {
  .main {
    margin-top: 8px;
    background-color: #ECF0F3;
  }
  .el-aside {
    width: 224px !important;
  }
}

/** 退出登录 */
.exit-login {
  text-align: center;
  line-height: normal;
  font-size: 20.8px;
  color: #96999a;
}

.exit-login,
.logo,
.avatar {
  cursor: pointer;
}
</style>