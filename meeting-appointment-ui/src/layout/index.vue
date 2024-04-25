<template>
  <div class="layout-container">
    <div class="layout_header">
      <div class="logo" @click="toHome">
        <img src="@/assets/img/logo.png" alt="">
      </div>
      <div class="avatar">
        <el-popover placement="top-start" trigger="hover" class="el-popover">
          <template #reference>
            <el-avatar :icon="UserFilled"/>
          </template>
          <div class="exit-login" @click="exitLogin">退出登录</div>
        </el-popover>
      </div>
    </div>
    <div class="common-layout">
      <el-container>
        <el-header>
          <Navbar/>
        </el-header>
        <el-container>
          <el-aside width="15.625rem">
            <Sidebar/>
          </el-aside>
          <el-main>
            <RouterView></RouterView>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </div>
</template>
<script setup lang="ts">
import {UserFilled} from '@element-plus/icons-vue';
import {useRouter} from 'vue-router'
import Navbar from "@/layout/components/navbar.vue";
import Sidebar from "@/layout/components/sidebar.vue";

const router = useRouter();
/**
 * @description 退出登录
 */
const exitLogin = () => {
  router.push('/login');
};
const toHome = () => {
  router.push('/');
}
</script>
<style scoped lang="scss">
.layout-container {
  .layout_header {
    width: 100%;
    height: 3.75rem;
    padding: 1.875rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #FFFFFF;
    box-shadow: 0px 3px 8px 0px rgba(0, 0, 0, 0.1);

    .logo, .avatar {
      margin: 0;
    }
  }

}

/** 退出登录 */
.exit-login {
  text-align: center;
  line-height: normal;
  font-size: 1.3rem;
  color: #96999a;
}

.exit-login, .logo, .avatar {
  cursor: pointer;
}
</style>