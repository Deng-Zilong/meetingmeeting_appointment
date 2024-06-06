<template>
  <!-- 会议室状态 -->
  <div class="container meeting-container" v-loading="loading">
    <header>
      <el-divider direction="vertical" /><div class="title-animation"><span>{{ title }}</span></div>
    </header>
    <main>
      <div class="meeting-left">
        <div class="meeting-left-date">
          <div class="two-icon">
            <el-icon @click="deleteDate"><ArrowUpBold /></el-icon>
            <el-icon @click="addDate"><ArrowDownBold /></el-icon>
          </div>
          <el-date-picker v-model="date" type="date" class="left-date" :clearable="false" :disabled-date="disabledDate"
            placeholder="选择日期" @change="changeDate" />
        </div>
        <div class="left-table">
          <div class="table-title">会议预约时间选择</div>
          <div class="table-main">
            <div class="table-items" :class="timeColor(item.state)" v-for="item in timeArr"
            @mouseover="handleMouseOver(item)" @mouseout="handleMouseOut" @click.stop="selectTime(item)">
              {{ hoveredItem === item && item.state === 1 ? `发起人：${item.initiator}` : item.time  }}
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
            <img class="items-left" style="width: 40px; height: 40px;" :src="item.src" alt="">
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { dayjs } from 'element-plus';

// 导入图片地址
import chamber from '@/assets/img/chamber.png'
import address from '@/assets/img/address.png'
import capacity from '@/assets/img/capacity.png'
import device from '@/assets/img/device.png'
import { getBusyData } from '@/request/api/home'

const loading = ref(false)  // 获取数据loading
const routes = useRoute();  // 用于传数据
const router = useRouter() // 用于选择时间段

const date = ref<any>(dayjs().startOf('date').format('YYYY-MM-DD'))  // 会议日期选择
const currentMeetingId = ref<any>(routes.query.id);  // 会议室id
const title = ref(routes.query.title);  // 会议室名称
const locate = ref(routes.query.address);  // 会议室位置
const person = ref(routes.query.person);  // 会议室容量人数
const equipment = ref(routes.query.equipment);  // 会议室容量人数
const time = ref(new Date().toLocaleTimeString().substring(0, 5))  // 显示当前时间点
const hoveredItem = ref<any>(null)  // 鼠标悬浮内容

const personInfo = computed(() => `${person.value}人`);  // 会议室容量人数  拼接个“人”
// 会议室信息
const infoArr = reactive([
  {
    src: chamber,
    title: '会议室名称',
    info: title
  },
  {
    src: address,
    title: '位置',
    info: locate
  },
  {
    src: capacity,
    title: '会议室容量',
    info: personInfo
  },
  {
    src: device,
    title: '设备',
    info: equipment
  }
])

// 会议时间点
const timeArr = ref([
  { time: '08:00', state: 3, initiator: '' }, { time: '08:30', state: 3, initiator: '' },
  { time: '09:00', state: 3, initiator: '' }, { time: '09:30', state: 3, initiator: '' },
  { time: '10:00', state: 3, initiator: '' }, { time: '10:30', state: 3, initiator: '' },
  { time: '11:00', state: 3, initiator: '' }, { time: '11:30', state: 3, initiator: '' },
  { time: '12:00', state: 3, initiator: '' }, { time: '12:30', state: 3, initiator: '' },
  { time: '13:00', state: 3, initiator: '' }, { time: '13:30', state: 3, initiator: '' },
  { time: '14:00', state: 3, initiator: '' }, { time: '14:30', state: 3, initiator: '' },
  { time: '15:00', state: 3, initiator: '' }, { time: '15:30', state: 3, initiator: '' },
  { time: '16:00', state: 3, initiator: '' }, { time: '16:30', state: 3, initiator: '' },
  { time: '17:00', state: 3, initiator: '' }, { time: '17:30', state: 3, initiator: '' },
  { time: '18:00', state: 3, initiator: '' }, { time: '18:30', state: 3, initiator: '' },
  { time: '19:00', state: 3, initiator: '' }, { time: '19:30', state: 3, initiator: '' },
  { time: '20:00', state: 3, initiator: '' }, { time: '20:30', state: 3, initiator: '' },
  { time: '21:00', state: 3, initiator: '' }, { time: '21:30', state: 3, initiator: '' },
  { time: '22:00', state: 3, initiator: '' }, { time: '22:30', state: 3, initiator: '' }
])

const disabledDate = (date: any) => {  // 禁止选择今日之前的日期
  return date.getTime() < Date.now() - 8.64e7
}

// 前一天
const deleteDate = () => {
  // 当前日期为今天，不可以减少日期 做判断
  if (dayjs(date.value).subtract(1, 'day').format('YYYY-MM-DD') < dayjs(new Date()).format('YYYY-MM-DD'))
    return;
  date.value = dayjs(date.value).subtract(1, 'day').format('YYYY-MM-DD')
  getBusy({ id: currentMeetingId.value, date: date.value });  
}

// 后一天
const addDate = () => {
  date.value = dayjs(date.value).add(1, 'day').format('YYYY-MM-DD')
  getBusy({ id: currentMeetingId.value, date: date.value });
}

setInterval(() => {  // 更新 时间 会议室名称、位置
  time.value = new Date().toLocaleTimeString().substring(0, 5) 
}, 100)


onMounted(() => {
  loading.value = false;
  getBusy({ id: currentMeetingId.value, date: dayjs(date.value).format('YYYY-MM-DD') });
})

/**
 * @description  每个会议室的时间状态
 * @param {id} 会议室id
 * @param {date} 日期
*/
const getBusy = async (data: {id: number, date: string}) => {
  let res: any = await getBusyData(data)
  timeArr.value.forEach((item: any, index: number) => {
    item.state = res.data[index].status;  // 后端返回值
    item.initiator = res.data[index].meetingAdminUserName;    
  })
}

// 日期变化
const changeDate = (val: any) => {
  getBusy({ id: currentMeetingId.value, date: dayjs(val).format('YYYY-MM-DD') })
}

// 时间段状态  0：已过期1：已占用2：可预约
const timeColor = computed(() => (state: any) => {
  switch (state) {
    case 0:
      return 'color-one';
    case 1:
      return 'color-two';
  }
})
// 鼠标移入
const handleMouseOver = (item: any) => {
  hoveredItem.value = item
}
// 鼠标移出
const handleMouseOut = () => {
  hoveredItem.value = null
}

// 选择时间段
const selectTime = (item: any) => {
  if ([0, 1].includes(item.state)) {
    return;
  } else {
    const meetingInfo = {
        date: date.value, // 会议日期
        meetingRoomId: currentMeetingId.value,  // 将会议室id传到 预约页面
        startTime: item.time  // 将点击的时间点传到 预约页面
    }
    sessionStorage.setItem('meetingInfo', JSON.stringify(meetingInfo));
    router.push('/meeting-appoint');
  }
}

// 监听会议室页面切换时 传的值是否变化
// watch(() => router.currentRoute.value.query, (newValue: any) => {
//   currentMeetingId.value = newValue.id
//   title.value = newValue.title
//   locate.value = newValue.address
//   person.value = routes.query.person  // 会议室容量人数
//   getBusy({ id: currentMeetingId.value, date: dayjs(date.value).format('YYYY-MM-DD') })  // 切换会议室时 调用会议室接口
// })


</script>

<style scoped lang="scss">
.meeting-container {
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
    .title-animation {
        display: inline-block;
        animation: scroll .5s linear;
        z-index: -1;
        @keyframes scroll {
            0% {
                opacity: 0;
                transform: translateX(-30%);
            }
            50% {
                opacity: 0;
                transform: translateX(-1%);
            }
            100% {
                opacity: 1;
                transform: translateX(0%);
            }
        }
    }
    .el-divider {
      height: 3.125rem;
      border: .25rem solid #1273DB;
      border-radius: .25rem;
      margin-right: 1.5rem;
      z-index:1;
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

        ::v-deep(.left-date) {
          height: 3rem !important;
          font-size: 1.5rem !important;
        }

        .two-icon {
          display: flex;
          flex-direction: column;
          justify-content: space-around;
          margin-right: 10px;
          color: #A8ABB2;
          cursor: pointer;
        }
      }

      .left-table {
        // margin: 10px 0;
        .table-title {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 4rem;
          background: #FFF;
          border-radius: 15px 15px 0px 0px;
          box-shadow: inset 0px 1px 8px 0px #DBE9F7;
          color: #3E78F4;
          font-size: 1.5rem;
        }

        .table-main {
          display: grid;
          grid-template-columns: repeat(6, 1fr);
          grid-template-rows: repeat(5, auto);
          gap: 7px;
          border-radius: 0 0 15px 15px;
          box-shadow: inset 0px 1px 8px 0px #DBE9F7;
          padding: 7px;

          .table-items {
            // padding: 29px;
            height: 79px;
            line-height: 79px;
            text-align: center;
            background: #FFF;
            overflow: hidden;
            border-radius: 5px;
            box-shadow: inset 0px 1px 8px 0px #DBE9F7;
            cursor: pointer;
            transition: transform 0.2s ease;  // 标题会议室 展示动画效果
            transition: transform 0.3s linear;  // 每个时间点的动画效果

            &:hover {
              font-size: 1.3rem;
              color: #FFF;
              background-color: #1273DB;
              transform: translateY(-0.3125rem) scale(1.06);  // 鼠标移入时，放大并上移
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
              font-size: 1.2rem;
              color: #585858;
            }
          }
        }
      }
    }
  }
}
</style>