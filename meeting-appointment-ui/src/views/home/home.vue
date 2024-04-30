<template>
  <div class="home-container">
    <div class="home-row">
      <div class="every-block screen">中心会议室大屏</div>
      <div class="every-block info">
        <div class="info-notice">告示</div>
        <div class="info-main">
          <div class="time">时间</div>
          <div class="rules">
            <div class="rule-block">预约规则</div>
            <div class="rule-block">取消规则</div>
          </div>
          <div class="right"></div>
        </div>
      </div>
    </div>
    <div class="home-row">
      <div class="every-block choose">
        <div class="choose-title">今日会议预约时间选择</div>
        <div class="choose-main"></div>
      </div>
      <div class="every-block situation">
        <div class="situation-title">今日会议室预约情况</div>
        <div class="situation-table">
          <el-table :data="tableData">
            <el-table-column prop="theme" label="会议主题" width="130" />
            <el-table-column prop="name" label="会议室名称" width="130" />
            <el-table-column prop="time" label="预约时间" width="130" />
            <el-table-column prop="status" label="会议状态" width="130" />
            <el-table-column prop="persons" label="人数" width="130" />
            <el-table-column prop="operate" label="操作" />
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user'
import { reactive, ref } from 'vue'

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  const code = decodeURIComponent(route.query.code as string);
  const token = userStore.userInfo.access_token;
  if (!token) {
    router.replace('/login');
  } else {
    if (code != 'undefined') {
      await userStore.getQWUserInfo(code);
    }
  }
});

// 会议室预约情况
const tableData = reactive([
  {
    theme: '11',
    name: '11',
    time: '11',
    status: '11',
    persons: '11',
    operate: '11'
  }
]);

</script>

<style lang="scss" scoped>
.home-container {
  // display: flex;
  // flex-direction: column;
  // justify-content: space-between;
  // justify-content: flex-start;
  // align-items: center;
  // flex-wrap: wrap;
  // flex: 1;
  // box-sizing: border-box;

  .home-row {
    display: flex;
    justify-content: space-between;

    .every-block {
      width: 50.2rem;
      height: 22.875rem;
      border-radius: 15px;
    }

    .screen {
      background-image: url('@/assets/img/screen-bg.png');
      background-size: cover;
    }

    .info {
      // width: 808px;
      // background-color: #eeeeee;
      display: flex;
      flex-direction: column;
      margin-bottom: .625rem;

      .info-notice {
        // width: 808px;
        height: 40px;
        border-radius: 6px;
        background: #FFFFFF;
        border: 2px solid rgba(18, 115, 219, 0.6);
        margin-bottom: 0.8rem;
      }

      .info-main {
        // height: 360px;
        display: flex;
        flex: 1;
        justify-content: space-between;

        .time {
          width: 400px;
          height: 100%;
          border-radius: 15px;
          background: url('@/assets/img/time_bg.png') no-repeat;
          background-size: 100% 100%;
          filter: opacity(0.8);
        }

        .rules {
          // display: inline-block;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .rule-block {
            width: 235px;
            // height: 150px;
            height: 48%;
            border-radius: 15px;
            background: #FFFFFF;
            // box-sizing: border-box;
            border: 1px solid #3E78F4;
          }
        }

        .right {
          width: 133px;
          // height: 315px;
          height: 100%;
          border-radius: 15px;
          background-image: url("@/assets/img/center_right_bgc.png");
          background-size: 100% 100%;
          filter: opacity(0.9);
        }
      }
    }

    .choose {

      // background-color: #3A3A3A;
      .choose-title {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 2.8rem;
        background: #FFF;
        border-radius: 15px 15px 0px 0px;
        box-shadow: inset 0px 1px 8px 0px #DBE9F7;
        color: #3E78F4;
        font-size: 1.13rem;
      }
    }

    .situation {
      display: flex;
      flex-direction: column;

      // width: 808px;
      // background-color: #409EFF;
      .situation-title {
        text-align: center;
        font-size: 1.15rem;
        color: #3E78F4;
        margin-bottom: 5px;
      }

      .situation-table {
        flex: 1;
        border: 2px solid #69A5E4;
        border-radius: 15px;

        .el-table {
          background: none;
          // text-align: center;
        }
      }
    }
  }
}
</style>