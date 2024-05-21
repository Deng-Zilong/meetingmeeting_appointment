<template>
  <div class="nav-box">
     <div class="left">
          <!-- <el-dropdown> -->
              <el-button type="primary" @click="handleToHome">
                  中心会议室<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <!-- <template #dropdown>
                  <el-dropdown-menu>
                  <el-dropdown-item>Action 1</el-dropdown-item>
                  <el-dropdown-item>Action 2</el-dropdown-item>
                  <el-dropdown-item>Action 3</el-dropdown-item>
                  <el-dropdown-item>Action 4</el-dropdown-item>
                  <el-dropdown-item>Action 5</el-dropdown-item>
                  </el-dropdown-menu>
              </template> -->
          <!-- </el-dropdown> -->
          <el-divider direction="vertical" />
          <div v-for="(item, index) in centerRoomName">
              <el-button class="btn-margin" :class="active == item.id ? 'active' : ''" @click="handleMenu(item)" >{{item.title}}</el-button>
          </div>
      </div>
     <div class="right">
      <router-link to="/history"><el-button :disabled="cancelDisable">取消预约</el-button> </router-link>
      <router-link to="/meeting-appoint"><el-button type="primary">会议室预约</el-button></router-link>
     </div>
  </div>
</template>
<script setup lang="ts">
  import { computed, ref, watch } from "vue";
  import { useRouter } from "vue-router";
  import { ArrowDown } from '@element-plus/icons-vue';
  const router = useRouter();
  const centerRoomName = [
    { 
      id: 1,
      title: '广政通宝会议室',
      address: '西南裙一 3 F 一 广政通宝会议室'
    },
    { 
      id: 2,
      title: 'EN-2F-02 恰谈室会议室',
      address: '西南裙一 3 F 一 EN-2F-02 恰谈室会议室'
    },
    { 
      id: 3,
      title: 'EN-2F-03 恰谈室会议室',
      address: '西南裙一 3 F 一 EN-2F-03 恰谈室会议室'
    },
    { 
      id: 4,
      title: 'EN-3F-02 恰谈室会议室',
      address: '西南裙一 3 F 一 EN-3F-02 恰谈室会议室'
    },
    { 
      id: 5,
      title: 'EN-3F-03 恰谈室会议室',
      address: '西南裙一 3 F 一 EN-3F-03 恰谈室会议室'
    }
 ];
  let active = ref(-1); // 活动页面id

  const handleToHome = () => {
    router.push('/home')
  }

  
  /**
   * @description 点击导航栏切换页面
   * @param index 活动页id
   */
  const handleMenu = (item: any) => {
    router.push({
      path: '/meeting-state',
      query: {
        id: item.id,
        title: item.title,
        address: item.address,
      }
    });
  }
  // 监听导航栏的传参 实现样式切换
  watch(() => router.currentRoute.value.query.id, (newValue: any) => {
      if (router.currentRoute.value.name == 'meeting-state') {
          active.value = newValue; 
      } else {
          active.value = -1;
      }
  }, {immediate: true})
  // 取消预约按钮禁用状态
  const cancelDisable = computed(() => {
      return router.currentRoute.value.name == 'history';
  })
  

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