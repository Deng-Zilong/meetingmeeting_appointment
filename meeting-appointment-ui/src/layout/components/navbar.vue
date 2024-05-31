<template>
  <div class="nav-box">
     <div class="left">
          <div class="left-menu">
            <el-button type="primary" @click="handleToHome">中心会议室目录</el-button>
            <el-divider direction="vertical" />
          </div>
          <div class="my-main-scrollbar">
            <div class="left-items" v-for="(item, index) in useMeetingStatus.centerRoomName">
                <el-button class="btn-margin"  :class="active == item.id ? 'active' : ''" :disabled="item.status == 0" @click="handleMenu(item)" >{{item.roomName}}</el-button>
            </div>
          </div>
      </div>
     <div class="right">
      <router-link to="/history"><el-button :disabled="cancelDisable">取消预约</el-button> </router-link>
      <router-link to="/meeting-appoint"><el-button type="primary">会议室预约</el-button></router-link>
     </div>
  </div>
</template>
<script setup lang="ts">
  import { computed, onMounted, ref, watch } from "vue";
  import { useRouter } from "vue-router";
  import { meetingStatus } from '@/stores/meeting-status'
  const router = useRouter();
  const useMeetingStatus = meetingStatus();
  
  

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
        title: item.roomName,
        address: item.location,
        person: item.capacity  // 暂无
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
  onMounted(() => {
    useMeetingStatus.getCenterRoomName();
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
          align-items: center;
          width: calc(100% - 720px);
          overflow-x: auto;
          position: relative;
          .left-menu {
            display: flex;
            align-items: center;
          }
          .my-main-scrollbar{
            display: flex;
            align-items: center;
            &::before {
              z-index: 1;
              width: 8.125rem;
              height: 100%;
              content: '';
              background: linear-gradient(to left,rgba(255, 255, 255, 1) 0%, rgba(255, 255, 255, 0.95) 10.5%, rgba(255, 255, 255, 0.8) 30.5%, rgba(254, 254, 254, 0.3) 86.8%, rgba(254, 254, 254, 0.3) 100%);
              position: absolute;
              top: 1;
              right: 0;
            }
            .left-items {
              &:last-child {
                margin-right: 8.125rem;
              }
              .btn-margin {
                  margin-right: .625rem;
              }
              .active {
                  background-color:#409EFF;
              }
            }
          }
      }
      .right {
        display: flex;
        width: 200px;
        a {
          margin-left: .625rem;
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