<template>
  <!-- 会议室状态 -->
  <div class="container meeting-container">
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
          <div class="week-day">{{ week[dayjs(date).day()] }}</div>
        </div>
        <div class="left-table">
          <div class="table-title">会议预约时间选择</div>
          <div class="table-main">
            <div class="table-items" :class="timeColor(item.state)" v-for="item in timeArr" :ref="(el) => timeRefs(el, item.time)" @contextmenu.prevent="onRightClick(item.time, item.state)" 
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
            <img class="items-left" :src="item.src" alt="">
            <div class="items-right">
              <div class="items-right-title">{{ item.title }}</div>
              <div class="items-right-info">{{ item.info }}</div>
            </div>
          </div>
          <!-- 设备信息 单独 -->
          <div class="right-items">
            <img class="items-left" :src="deviceInfo.src" alt="">
            <div class="items-right">
              <div class="items-right-title item-click">
                <span>{{ deviceInfo.title }}</span>
                <el-tooltip content="点击设备报损" placement="top" effect="light" v-if="deviceInfo.info" >
                  <el-icon @click="handleBreak(title)"><WarnTriangleFilled /></el-icon>
                </el-tooltip>
              </div>
              <div class="items-right-info">{{ deviceInfo.info ? deviceInfo.info : "暂无设备" }}</div>
            </div>
          </div>
          <!-- 报损弹窗 -->
          <el-dialog v-model="dialogFormVisible" width="500" top="25vh">
            <el-table :data="gridData" >
              <el-table-column property="deviceName" label="设备" width="150" />
              <el-table-column property="info" label="报损信息" width="200">
                <template #default="scope">
                  <el-input v-model="scope.row.info" />
                </template>
                </el-table-column>
              <el-table-column label="操作">
                <template #default="scope">
                  <el-button type="primary" @click="submitBreak(scope.row, scope.$index)">提交</el-button>
                </template>
                </el-table-column>
            </el-table>
          </el-dialog>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, dayjs } from 'element-plus';

// 导入图片地址
import chamber from '@/assets/img/chamber.png'
import address from '@/assets/img/address.png'
import capacity from '@/assets/img/capacity.png'
import device from '@/assets/img/device.png'
import { getBusyData } from '@/request/api/home'
import { addBreakInfoData, getDeviceData } from '@/request/api/manage'

const routes = useRoute();  // 用于传数据
const router = useRouter() // 用于选择时间段

const userInfo = ref<any>()
const date = ref<any>(dayjs().startOf('date').format('YYYY-MM-DD'))  // 会议日期选择
const week = ref(['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六',]) // 会议日期显示星期
const currentMeetingId = ref<any>(routes.query.id);  // 会议室id
const title = ref(routes.query.title);  // 会议室名称
const locate = ref(routes.query.address);  // 会议室位置
const person = ref(routes.query.person);  // 会议室容量人数
// const equipment = ref(routes.query.equipment);  // 会议室设备
const equipment = ref()  // 会议室设备(最新的) 用于展示
const equipmentData = ref()  // 设备信息数组 用于弹窗
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
  // {
  //   src: device,
  //   title: '设备',
  //   info: equipment
  // }
])
const deviceInfo = ref({
  src: device,
  title: '设备',
  info: equipment
})

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


onMounted(async() => {
  getBusy({ id: currentMeetingId.value, date: dayjs(date.value).format('YYYY-MM-DD') });
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}');  // 用户信息
  // 从“查询设备”接口中获取设备信息
  await handleDeviceInfoReq('');
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
    router.push('/meeting-appoint');
    // 如果存在多选时间点
    if (sortTimeArr?.length) {
        const startTime = sortTimeArr[1] ?? sortTimeArr[0];
        // 结束时间加半个小时
        const index = timeArr.value.findIndex((el: any) => el.time == sortTimeArr[0]);
        let endTime = '';
        if (timeArr.value.length == index + 1) {
          endTime = sortTimeArr[0];
        } else {
          endTime = [...timeArr.value].splice(index+1, 1)[0].time;
        }
        const meetingInfo = {
          date: date.value, // 会议日期
          meetingRoomId: currentMeetingId.value,  // 将会议室id传到 预约页面
          startTime,  // 将点击的时间点传到 预约页面
          endTime  // 将点击的时间点传到 预约页面
        }
        return sessionStorage.setItem('meetingInfo', JSON.stringify(meetingInfo));
    }
    const meetingInfo = {
        date: date.value, // 会议日期
        meetingRoomId: currentMeetingId.value,  // 将会议室id传到 预约页面
        startTime: item.time  // 将点击的时间点传到 预约页面
    }    
    sessionStorage.setItem('meetingInfo', JSON.stringify(meetingInfo));
  }
}

let classListTimeRefs:any = {}; // 存储dom的classList
// 获取时间选择器dom
const timeRefs = (el:any, key: string) => {
    classListTimeRefs[key] = el?.classList;
}
let selectTimeArr = ref<any>([]); // 选中的时间点
let sortTimeArr:any = []; // 降序排序后的选中时间点
const onRightClick = (time: any, state: number) => {
  if ([0, 1].includes(state)) {
    return;
  } else {
    // 重复点击同一时间点 移除选中状态
    if (selectTimeArr.value.length == 1 && selectTimeArr.value.includes(time)) {
        classListTimeRefs[time].remove('active');
        return selectTimeArr.value = [];
    }

    if (selectTimeArr.value.length == 2) {
        // 选中的时间点中 是否存在 当前点击的时间点
        const index = selectTimeArr.value.findIndex((el: any) => el == time);
        // 选中的时间点中 已存在 当前点击的时间点 移除选中状态 并删除此时间点
        if (index != -1) {
            classListTimeRefs[time].remove('active');
            return selectTimeArr.value.splice(index, 1);
        }
        // 不存在 删除旧时间点 添加新时间点
        selectTimeArr.value.splice(1,1);
    }
    // 添加时间点
    selectTimeArr.value.push(time);

    // 将时间点按照降序排列（跳转预约页面需要区分开始和结束时间）
    sortTimeArr = [...selectTimeArr.value].sort((a: string, b: string) => {
        // 将字符串时间转换为分钟数以便比较
        const timeA = parseInt(a.split(':')[0]) * 60 + parseInt(a.split(':')[1]);
        const timeB = parseInt(b.split(':')[0]) * 60 + parseInt(b.split(':')[1]);

        // 返回值决定排序顺序，此处返回负数使得a排在b前面，即降序排列
        return timeB - timeA;
    })

    // 添加选中状态
    if (selectTimeArr.value.length == 1) {
        return classListTimeRefs[time].add('active');
    }

    // 将选中的时间区间内的时间点变成选中状态
    for (const key in classListTimeRefs) {
        if (sortTimeArr[0] >= key && key >= sortTimeArr[1]) {
            classListTimeRefs[key].add('active');
        } else {
            classListTimeRefs[key].remove('active');
        }
    }
  }
};

/***************************************************报损信息***********************************************/
const dialogFormVisible = ref(false)  // 报损信息弹出框
let formDialog = reactive({
  input: '',
  selected: ''
})

const gridData = ref<any>([])

const handleDeviceInfoReq = async(index: number | string, id?: number) => {
  const res: any = await getDeviceData({ current: -1, size: -1, roomId: currentMeetingId.value, status: 1 });
  equipmentData.value = res.data.records.map((item: any) => {
    return item
  })
  equipment.value = equipmentData.value.map((item: any) => item.deviceName).join(', ')  // 用于设备展示
  if (index !== '') {
    const deviceInfo = equipmentData.value.find((item: any) => item.id == id);
    if (deviceInfo !== undefined) {
      return gridData.value[index].extent = equipmentData.value.find((item: any) => item.id == id).extent;
    }
    return gridData.value.splice(index, 1);
  }
  gridData.value = equipmentData.value.map((item: any) => {  // 将信息给弹窗gridData
    return {
      id: item.id,
      deviceName: item.deviceName,
      extent: item.extent
    }
  })

}

/**
 * @description  新增报损信息
 * @param {deviceId} 设备id
 * @param {userId} 申报人id
 * @param {info} 报损内容
}
*/
const submitBreak = (row: any, index: number) => {
  
  if (row.info != '') {
    addBreakInfoData({ deviceId: row.id, userId: userInfo.value.userId, info: row.info })
    .then((res) => {
        ElMessage.success(`成功提交"${row.deviceName}"的报损信息`);
        gridData.value[index].info = '';
        handleDeviceInfoReq(index, row.id);
      })
      .catch((err) => { })
  } else {
    ElMessage.warning('提交信息不可为空！')
  }
}

// 打开报损弹窗
const handleBreak = (title: any) => {
  dialogFormVisible.value = true
  // 打开弹窗 内容清空
  formDialog.input = ''
  formDialog.selected = ''
}


</script>

<style scoped lang="scss">
// 弹窗样式修改
:deep().el-dialog {
  .el-table__inner-wrapper {
    &::before {
      height: 0;
    }
    .el-table__body-wrapper {
      .el-scrollbar {
        padding-bottom: 20px;
        max-height: 245px;
        overflow-y: auto;
        &::-webkit-scrollbar {  // 滚动条隐藏
          display: none !important;
          width: 0 !important;
        }
      }
    }
  }
}
.meeting-container {
  padding: 40px 56px;

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
        span {
          font-size: 24px;
        }
    }
    .el-divider {
      height: 50px;
      border: 4px solid #1273DB;
      border-radius: 4px;
      margin: -10px 24px 0 0;
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
        align-items: center;
        justify-content: flex-end;
        margin: 8px 0;

        ::v-deep(.left-date) {
          width: 245px;
          height: 48px;
          .el-input__inner {
            font-size: 24px;
          }
        }

        .two-icon {
          display: flex;
          flex-direction: column;
          justify-content: space-around;
          margin-right: 10px;
          color: #A8ABB2;
          cursor: pointer;
        }
        .week-day {
          font-size: 20.8px;
          position: absolute;
          padding-right: 10px;
        }
      }

      .left-table {
        // margin: 10px 0;
        .table-title {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 64px;
          background: #FFF;
          border-radius: 15px 15px 0 0;
          box-shadow: inset 0 1px 8px 0 #DBE9F7;
          color: #3E78F4;
          font-size: 24px;
        }

        .table-main {
          display: grid;
          grid-template-columns: repeat(6, 1fr);
          grid-template-rows: repeat(5, auto);
          gap: 7px;
          border-radius: 0 0 15px 15px;
          box-shadow: inset 0 1px 8px 0 #DBE9F7;
          padding: 7px;

          .table-items {
            // padding: 29px;
            height: 79px;
            line-height: 79px;
            text-align: center;
            background: #FFF;
            overflow: hidden;
            border-radius: 5px;
            box-shadow: inset 0 1px 8px 0 #DBE9F7;
            cursor: pointer;
            transition: transform 0.2s ease;  // 标题会议室 展示动画效果
            transition: transform 0.3s linear;  // 每个时间点的动画效果

            &:hover {
              font-size: 20.8px;
              color: #FFF;
              background-color: #1273DB;
              transform: translateY(-5px) scale(1.06);  // 鼠标移入时，放大并上移
            }
          }
          .active {
            cursor: pointer;
            font-size: 16px;
            color: #FFF;
            background-color: #1273DB;
            transform: translateY(-5px) scale(1.06);
          }
        }
      }

      .left-state {
        display: flex;
        justify-content: space-between;
        margin: 20px;

        .state-items {
          display: flex;
          align-items: center;

          // 状态公共样式
          .state-items-color {
            width: 50px;
            height: 50px;
            margin: 0 19.2px;
            border-radius: 5px;
            box-shadow: 0 3px 8px 0 rgba(0, 0, 0, 0.16);
          }

          // 状态单独样式
          .color-three {
            background: #417AF3 !important;
          }

          .state-items-text {
            font-size: 25.6px;
            color: #636363;
          }
        }
      }
    }

    .meeting-right {
      width: 359px;
      height: 620px;
      border-radius: 15px;
      background: #FFFFFF;
      box-shadow: inset 0 1px 8px 0 #DBE9F7;

      .right-title {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 80px;
        color: #3E78F4;
        font-size: 40px;
      }

      .right-table {
        display: flex;
        flex-direction: column;
        justify-content: space-evenly;
        height: 540px;
        padding: 0 40px;
        border-radius: 0 0 15px 15px;
        box-shadow: inset 0 1px 8px 0 #DBE9F7;
        font-size: 24px;

        .right-items {
          display: flex;
          align-items: center;

          .items-left {
            width: 40px;
            height: 40px;
            margin-right: 10px;
          }

          .items-right {
            .items-right-title {
              font-size: 25px;
              color: #3E78F4;
              span {
                font-size: 25px;
              }
            }

            .items-right-info {
              width: 239px;
              word-wrap: break-word; // 英文换行
              font-size: 19.2px;
              color: #585858;
            }
            // 报损信息单独样式
            .item-click {
              display: flex;
              align-items: center;
              .el-icon {
                cursor: pointer;
                // margin-left: 5px;
                color: #F16105;
              }
            }
          }
        }
      }
    }
  }
}
</style>