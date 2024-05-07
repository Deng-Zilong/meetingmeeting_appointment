<template>
  <div class="home-container">
    <div class="home-row">
      <!-- 会议室大屏 -->
      <div class="every-block screen">
        <div class="screen-title">中心会议室大屏</div>
        <div class="screen-main">
          <div class="main-table">
            <div class="main-items" v-for="item in 3">
              <div class="name">会议室名</div>
              <div class="state">状态</div>
            </div>
          </div>
          <div class="guage-chart">
            <GuageChart :gaugeData="gaugeData" :width="300" :height="270"  />
          </div>
        </div>
        <!-- <div class="screen-footer">
          <p>今日中心会议总次数</p>
          <div class="num">次</div>
        </div> -->
      </div>
      <!-- 预约信息 -->
      <div class="every-block info">
        <div class="info-board">
          <el-icon><ChatRound /></el-icon>
          <div class="info-text">
            <div class="text-scroll">
              <span>告示信息滚动告示信息滚动告示信息滚动告示信息滚动告示信息滚动</span>
            </div>
          </div>
        </div>
        <div class="info-main">
          <div class="time">
            <div class="time-day">
              <div class="day-left">
                <div class="left-year">{{ new Date().getFullYear() }}</div>
                <div class="left-date">
                  {{ new Date().getMonth() + 1 }} 月 {{ new Date().getDate() }}日
                </div>
              </div>
              <div class="day-right">
                <div class="right-year">YEAR</div>
                <div calss="right-date">DATE</div>
              </div>
            </div>
            <div class="time-clock">
              <Clock />
            </div>
          </div>
          <div class="rules">
            <div class="rule-block">
              <div class="rule-title">预约规则</div>
              <div class="rule-main">
                <div class="rule-scroll">
                  <div class="scroll-item" v-for="item in 10"> · 预约前请先登录预约前请先登录预约前请先登录</div>
                </div>
              </div>
            </div>
            <div class="rule-block">
              <div class="rule-title">取消规则</div>
              <div class="rule-main">
                <div class="rule-scroll">
                  <div class="scroll-item" v-for="item in 10"> · 预约前请先登录预约前请先登录预约前请先登录</div>
                </div>
              </div>
            </div>
          </div>
          <div class="right"></div>
        </div>
      </div>
    </div>
    <div class="home-row">
      <!-- 预约时间选择 -->
      <div class="every-block choose">
        <div class="choose-title">今日会议预约时间选择</div>
        <div class="choose-main" >
          <div class="choose-cell" :class="color(item.state)" v-for="item in timeArr" @click.stop="selectTime(item)">{{ item.time }}</div>
        </div>
      </div>
      <!-- 会议室预约情况 -->
      <div class="every-block situation">
        <div class="situation-title">今日会议室预约情况</div>
        <div class="situation-table">
          <div class="table-th">
            <div class="title">会议主题</div>
            <div class="title">会议室名称</div>
            <div class="title">预约时间</div>
            <div class="title">会议状态</div>
            <div class="title">人数</div>
            <div class="title">操作</div>
          </div>
          <div class="table-main">
            <div class="table-tr" v-for="item in tableData">
              <div class="tr-cell">{{ item.theme }}</div>
              <div class="tr-cell">{{ item.name }}</div>
              <div class="tr-cell">{{ item.time }}</div>
              <div class="tr-cell">{{ item.status }}</div>
              <div class="tr-cell">{{ item.persons }}</div>
              <div class="tr-cell" :style="{ 'color': (item.operate === '修改' ? '#3268DC' : '') }">{{ item.operate }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user'
import { reactive, ref } from 'vue'
import Clock from '@/views/home/component/clock.vue'
import GuageChart from '@/views/home/component/guageChart.vue'

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  /* 判断扫码登录状态 */
  const code = decodeURIComponent(route.query.code as string);
  const token = userStore.userInfo.access_token;

  // 若 code 不为 undefined 时为扫码登录
  if (code != 'undefined') {
    return await userStore.getQWUserInfo(code);
  }
  
  // 若不是扫码登录则判断token  
  if (!token) {
    router.replace('/login');
  }
});

/* 会议室大屏 */
// echarts数据展示
const gaugeData = ref([
  {
    value: 12,
    name: '中心会议总次数'
  }
])
/* 预约时间选择 */
// 会议时间点
interface Time {
  time: String,
  state: Number
}
const timeArr = reactive<Time[]>([
  { time: '8:00', state: 4 }, { time: '8:30', state: 4 },
  { time: '9:00', state: 4 }, { time: '9:30', state: 1 },
  { time: '10:00', state: 1 }, { time: '10:30', state: 2 },
  { time: '11:00', state: 3 }, { time: '11:30', state: 4 },
  { time: '12:00', state: 4 }, { time: '12:30', state: 1 },
  { time: '13:00', state: 4 }, { time: '13:30', state: 4 },
  { time: '14:00', state: 4 }, { time: '14:30', state: 1 },
  { time: '15:00', state: 1 }, { time: '15:30', state: 2 },
  { time: '16:00', state: 3 }, { time: '16:30', state: 4 },
  { time: '17:00', state: 4 }, { time: '17:30', state: 1 },
  { time: '18:00', state: 1 }, { time: '18:30', state: 1 },
  { time: '19:00', state: 1 }, { time: '19:30', state: 1 },
  { time: '20:00', state: 1 }, { time: '20:30', state: 2 },
  { time: '21:00', state: 3 }, { time: '21:30', state: 4 },
  { time: '22:00', state: 1 }, { time: '22:30', state: 1 }
])
const color = computed(() => (state: any) => {
  switch (state) {
    case 1:
      return 'color-one';
    case 2:
      return 'color-two';
  }
})
// 选择时间段
const selectTime = (item: any) => {
  if ([1, 2].includes(item.state)) {
    return;
  } else {
    router.push('/meeting-appoint')
  }
}

/* 会议室预约情况 */
const tableData = reactive([
  {
    theme: '11',
    name: '11',
    time: '11',
    status: '11',
    persons: '11',
    operate: '删除'
  },
  {
    theme: '11',
    name: '11',
    time: '11',
    status: '11',
    persons: '11',
    operate: '修改'
  }
]);

</script>

<style lang="scss" scoped>
.home-container {

  .home-row {
    display: flex;
    justify-content: space-between;

    .every-block {
      width: 50.2rem;
      height: 22.875rem;
      border-radius: 15px;
    }

    /* 会议室大屏样式 */
    .screen {
      background-image: url('@/assets/img/screen-bg.png');
      background-size: cover;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 13px 20px;
      .screen-title {
        font-size: 1.13rem;
        color: #3E78F4;
      }
      .screen-main {
        position: relative;
        .guage-chart {
          position: absolute;
          left: 31.8%;
          top: 1%;
        }
        
        .main-table {          
          display: grid;
          // grid-auto-flow: column;
          grid-template-columns: 200px 200px;
          grid-template-rows: repeat(auto-fit, 90px);
          justify-content: space-between;
          gap: 10px;
          width: 770px;
          height: 320px;
          .main-items {
            display: flex;
            flex-direction: column;  // 控制内部元素垂直排列
            align-items: center;
            justify-content: space-evenly;
            overflow: hidden; 
            border-radius: 10px;
            background: rgba(255, 255, 255, 0.1);
            box-sizing: border-box;
            border: 1px solid rgba(111, 167, 249, 0.8);
            box-shadow: inset 0px 0px 30px 0px rgba(16, 127, 255, 0.3);
            .name {
              font-size: 1.1rem;
              color: #6A6A6A;
            }
            .state {
              font-size: 1.3rem;
              font-weight: bold;
              color: #1273DB;
            }
          }
        }
      }
    }

    /* 预约信息样式 */
    .info {
      display: flex;
      flex-direction: column;
      margin-bottom: 0.625rem;
      // 告示滚动信息
      .info-board {
        display: flex;
        align-items: center;
        height: 2rem;
        border-radius: 6px;
        color: #0D3F81;
        background: #FFFFFF;
        border: 2px solid rgba(18, 115, 219, 0.6);
        margin-bottom: 0.8rem;

        .el-icon {
          margin: 0 0.7rem;
        }

        .info-text {
          width: 94%;
          // width: fit-content;
          overflow: hidden;
          white-space: nowrap;
          @keyframes scrollX {
            0% {
              transform: translateX(100%);
            }
            100% {
              transform: translateX(-100%);
            }
          }
          .text-scroll {
            display: inline-block;
            animation: scrollX 15s linear infinite;
          }
          // 鼠标 悬停时停止滚动
          :hover {
            animation-play-state: paused;
            cursor: pointer;
          }
        }
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
          .time-day {
            display: flex;
            justify-content: space-between;
            padding: 15px;
            .day-left {
              display: flex;
              line-height: 4.2rem;
              font-size: 1.6rem;
              border-radius: 5px;
              border: 2px solid #6BD1D8;
              .left-year {
                width: 5.6rem;
                text-align: center;
                background: #FFF;
                border-radius: 5px 0 0 5px;
              }
              .left-date {
                width: fit-content;
                color: #FFF;
                background: #5ad0d8;
                padding: 0 5px;
              }
            }
            .day-right {
              // display: flex;
              // flex-direction: column;
              // align-items: center;
              // justify-content: space-evenly;
              width: 4.3rem;
              color: #FFF;
              text-align: center;
              border: 2px solid #6BD1D8;
              border-radius: 5px;
              .right-year {
                height: 50%;
                background: #6BD1D8;
              }
            }
          }
          .time-clock {
            display: flex;
            justify-content: center;
          }
        }

        .rules {
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          // height: 322px;
          .rule-block {
            width: 235px;
            height: 48%;
            border-radius: 15px;
            background: #FFFFFF;
            // box-sizing: border-box;
            border: 1px solid #3E78F4;
            // margin-bottom: 10px;
            .rule-title {
              font-size: 0.8rem;
              text-align: center;
              padding: 5px 0;
            }
            .rule-main {
              height: 120px;
              overflow: hidden; // 滚动溢出隐藏
              @keyframes scrollY {
                0% {
                  transform: translateY(100%);
                }
                100% {
                  transform: translateY(-100%);
                }
              }
              .rule-scroll {
                // height: 120px;
                display: flex;
                flex-direction: column;
                align-items: center;
                font-size: 0.7rem;
                color: #676767;
                animation: scrollY 15s linear infinite; // 滚动动画
                .scroll-item {
                  width: 12.5rem;
                  height: 1.25rem;
                  border-radius: 4px;
                  box-sizing: border-box;
                  border: 0.2px solid #1273DB;
                  box-shadow: 0px 2px 5px 0px #F1F6FF;
                  
                  white-space: nowrap; /* 确保文本在一行内显示 */
                  overflow: hidden; /* 隐藏溢出的内容 */
                  text-overflow: ellipsis; /* 使用省略的文本表示溢出的内容 */
                  padding: 0 5px;
                  margin-bottom: 5px;
                }
              }
              // 鼠标 悬停时停止滚动
              :hover {
                animation-play-state: paused;
                cursor: pointer;
              }
            }
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

    /* 预约时间选择样式 */
    .choose {

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

      .choose-main {
        width: 50.2rem;
        display: grid;
        grid-template-columns: repeat(6, 1fr); /* 创建6列，每列等宽 */
        grid-template-rows: repeat(5, auto); /* 创建5行，自动高度适应内容 */
        gap: 4px; /* 设置单元格之间的间距 */
        background: #F5F7FA;
        border-radius: 0 0 15px 15px;
        padding: 5px;
        
        .choose-cell {
          padding: 20px;
          text-align: center;
          overflow: hidden; /* 防止内容溢出 */
          background: #FFF;
          border-radius: 5px;
          box-shadow: 1px 1px 2px 1px #DBE9F7;

          &:hover {
            background-color: #1273DB;
            transform: scale(1.06);
          }
        }
      }

      // 预约时间段不同情况 时间块颜色不同
      // 预约时间模块颜色 && 状态样式颜色
      .color-one {
        background: #E2E2E2 !important;
        cursor: not-allowed !important;
      }

      .color-two {
        background: #FFD4B0 !important;
        cursor: not-allowed !important;
      }
    }

    /* 会议室预约情况样式 */
    .situation {
      display: flex;
      flex-direction: column;

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
        padding: 10px 18px;
        .table-th {
          display: flex;
          text-align: center;
          .title {
            width: 180px;
          }
        }
        .table-main {
          max-height: 19.5rem;
          overflow-y: auto;
          .table-tr {
            display: flex;
            text-align: center;
            color: #666;
            background: #FFF;
            border-radius: 15px;
            margin: 10px 0;
            padding: 11px 0;
            .tr-cell {
              width: 180px;
            }
          }
        }
      }
    }
  }
}
</style>