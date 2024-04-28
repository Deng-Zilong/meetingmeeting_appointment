<template>
  <!-- 会议室状态 -->
  <div class="container meeting-container">
    <header>
      <el-divider direction="vertical" />会议室名称
    </header>
    <main>
      <div class="meeting-left">
        <div class="meeting-left-date">
          <el-date-picker v-model="date" type="date" class="left-date" :disabled-date="disabledDate"
            placeholder="选择日期" />
        </div>
        <div class="left-table">
          <div class="table-title">会议预约时间选择</div>
          <div class="table-main">
            <div class="table-items" :class="color(item.state)" v-for="item in timeArr" @click.stop="selectTime(item)">
              {{ item.time }}
            </div>
          </div>
        </div>
        <div class="left-state">
          <div class="state-items">
            <div class="state-items-color color-one"></div>
            <div class="state-items-text">已过期</div>
          </div>
          <div class="state-items">
            <div class="state-items-color color-two"></div>
            <div class="state-items-text">已预定</div>
          </div>
          <div class="state-items">
            <div class="state-items-color color-three"></div>
            <div class="state-items-text">已选中</div>
          </div>
          <div class="state-items">
            <div class="state-items-color color-four"></div>
            <div class="state-items-text">可预约</div>
          </div>
        </div>
      </div>
      <div class="meeting-right">
        <div class="right-title">{{ time }}</div>
        <div class="right-table">
          <div class="right-items" v-for="item in infoArr">
            <img class="items-left" style="width: 40px;height: 40px;" :src="item.src" alt="">
            <div class="items-right">
              <div class="items-right-title">{{ item.title }}</div>
              <div class="items-right-info">{{ item.info }}</div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router';

/* 会议日期选择 */
const date = ref(new Date())
// 禁止选择今日之前的日期
const disabledDate = (date: any) => {
  return date.getTime() < Date.now() - 8.64e7
}

// 显示当前时间点
const time = ref(new Date().toLocaleTimeString().substring(0, 5))
setInterval(() => {
  time.value = new Date().toLocaleTimeString().substring(0, 5)
}, 1000)


// 会议时间点
interface Time {
  time: String,
  state: Number
}
const timeArr = reactive<Time[]>([
  {
    time: '1714114711',
    state: 4
  }, {
    time: '8:30',
    state: 4
  }, {
    time: '9:00',
    state: 4
  }, {
    time: '9:30',
    state: 1
  }, {
    time: '8:00',
    state: 1
  },
  {
    time: '8:30',
    state: 2
  }, {
    time: '8:00',
    state: 3
  },
  {
    time: '8:30',
    state: 4
  }, {
    time: '8:00',
    state: 4
  },
  {
    time: '8:30',
    state: 1
  }, {
    time: '8:00',
    state: 4
  }, {
    time: '8:30',
    state: 4
  }, {
    time: '9:00',
    state: 4
  }, {
    time: '9:30',
    state: 1
  }, {
    time: '8:00',
    state: 1
  },
  {
    time: '8:30',
    state: 2
  }, {
    time: '8:00',
    state: 3
  },
  {
    time: '8:30',
    state: 4
  }, {
    time: '8:00',
    state: 4
  },
  {
    time: '8:30',
    state: 1
  }, {
    time: '8:00',
    state: 0
  }, {
    time: '8:30',
    state: 0
  }, {
    time: '9:00',
    state: 0
  }, {
    time: '9:30',
    state: 1
  }, {
    time: '8:00',
    state: 1
  },
  {
    time: '8:30',
    state: 2
  }, {
    time: '8:00',
    state: 3
  },
  {
    time: '8:30',
    state: 4
  }, {
    time: '8:00',
    state: 0
  },
  {
    time: '8:30',
    state: 1
  },
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
const routes = useRouter()
const selectTime = (item: any) => {
  if ([1, 2].includes(item.state)) {
    return;
  } else {
    routes.push('/meeting-appoint')
  }
}

// 会议室信息
const infoArr = reactive([
  {
    src: '../src/assets/img/chamber.png',
    title: '会议室名称',
    info: '至和通宝'
  },
  {
    src: '../src/assets/img/address.png',
    title: '第3层',
    info: '西北裙-3F-至和通宝'
  },
  {
    src: '../src/assets/img/capacity.png',
    title: '会议室容量',
    info: '8人'
  },
  {
    src: '../src/assets/img/device.png',
    title: '设备',
    info: '投屏TV'
  }
])

</script>

<style scoped lang="scss">
.meeting-container {
  // 公共样式 上传时删除
  width: 101rem;
  height: 45.9rem;
  border-radius: 15px;
  background-color: #ffffff;
  box-shadow: 0px 3px 6px 0px rgba(0, 0, 0, 0.08);
  // 公共样式结束

  padding: 2.5rem 3.5rem;

  // 预约时间模块颜色 && 状态样式颜色
  .color-one {
    background: #E2E2E2 !important;
    cursor: not-allowed !important;
  }

  .color-two {
    background: #FFD4B0 !important;
    cursor: not-allowed !important;
  }

  header {
    font-size: 1.5rem;

    .el-divider {
      height: 3.125rem;
      border: .25rem solid #1273DB;
      border-radius: .25rem;
      margin-right: 1.5rem;
    }
  }

  main {
    display: flex;
    justify-content: space-between;

    .meeting-left {
      width: 1080px;

      .meeting-left-date {
        display: flex;
        justify-content: flex-end;
        margin: .5rem 0;
      }

      .left-table {
        .table-title {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 4.2rem;
          background: #FFF;
          border-radius: 15px 15px 0px 0px;
          box-shadow: inset 0px 1px 8px 0px #DBE9F7;
          color: #3E78F4;
          font-size: 1.5rem;
        }

        .table-main {
          display: flex;
          flex-wrap: wrap;

          .table-items {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 11rem;
            height: 5rem;
            margin: 0.125rem;
            background: #FFF;
            border-radius: 5px;
            box-shadow: inset 0px 1px 8px 0px #DBE9F7;
            cursor: pointer;

            &:hover {
              background-color: #1273DB;
            }
          }
        }
      }

      .left-state {
        display: flex;
        justify-content: space-between;
        margin: 1.25rem;

        .state-items {
          display: flex;
          align-items: center;

          // 状态公共样式
          .state-items-color {
            width: 3.125rem;
            height: 3.125rem;
            margin: 0 1.2rem;
            border-radius: 5px;
            box-shadow: 0px 3px 8px 0px rgba(0, 0, 0, 0.16);
          }

          // 状态单独样式
          .color-three {
            background: #417AF3 !important;
          }

          .state-items-text {
            font-size: 1.6rem;
            color: #636363;
          }
        }
      }

    }

    .meeting-right {
      width: 359px;
      height: 38.75rem;
      border-radius: 15px;
      background: #FFFFFF;
      box-shadow: inset 0px 1px 8px 0px #DBE9F7;

      .right-title {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 80px;
        color: #3E78F4;
        font-size: 2.5rem;
      }

      .right-table {
        display: flex;
        flex-direction: column;
        justify-content: space-evenly;
        height: 33.75rem;
        padding: 0 2.5rem;
        border-radius: 0 0 15px 15px;
        box-shadow: inset 0px 1px 8px 0px #DBE9F7;
        font-size: 1.5rem;

        .right-items {
          display: flex;
          align-items: center;

          .items-left {
            margin-right: 10px;
          }

          .items-right {
            .items-right-title {
              color: #3E78F4;
            }

            .items-right-info {
              color: #585858;
            }
          }
        }
      }
    }
  }
}
</style>