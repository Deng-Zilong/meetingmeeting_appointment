<template>
  <div class="home-container" v-loading="loading">
    <div class="home-row">
      <!-- 会议室大屏 -->
      <div class="every-block screen">
        <div class="screen-title">中心会议室大屏</div>
        <div class="screen-main">
          <div class="main-table my-main-scrollbar">
            <div class="main-items" :class="item.status !== 0 ? 'pointer' : 'ban'" v-for="item in roomMeetingData" @click.stop="handleRoomClick(item)">
              <div class="name">{{ item.roomName }}</div>
              <div class="state">{{ roomStatus(item.status) }}</div>
            </div>
          </div>
          <div class="guage-chart">
            <GuageChart :gaugeData="gaugeData"  />
          </div>
        </div>
      </div>
      <!-- 预约信息 -->
      <div class="every-block info">
        <div class="info-board">
          <el-icon><ChatRound /></el-icon>
          <div class="info-text">
            <div class="text-scroll">
              {{ notice[0] || '暂无公告' }}
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
              <div class="rule-title">近期热门时间段</div>
              <div class="rule-main">
                <div class="rule-scroll">
                  <div class="scroll-item" v-for="item in timesData.slice(0, 10)">{{ item }}</div>
                  <div class="scroll-item">请注意及时预约</div>
                </div>
              </div>
            </div>
            <div class="rule-block">
              <div class="rule-title">规则</div>
              <div class="rule-main">
                <div class="rule-scroll">
                  <div class="scroll-item" v-for="item in rulesData">{{ item }}</div>
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
          <div class="choose-cell" :ref="(el) => timeRefs(el, item.time)" :class="timeColor(item.state)" v-for="item in timeArr" @click.stop="selectTime(item)" @contextmenu.prevent="onRightClick(item.time, item.state)">{{ item.time }}</div>
        </div>
      </div>
      <!-- 会议室预约情况 -->
      <div class="every-block situation">
        <div class="situation-title">今日会议室预约情况</div>
        <div class="situation-table">
          <div class="table-th">
            <div class="title t-title">会议主题</div>
            <div class="title t-room">会议室名称</div>
            <div class="title t-time">预约时间</div>
            <div class="title t-status">会议状态</div>
            <div class="title t-people">人数</div>
            <div class="title t-operate">操作</div>
          </div>
          <div class="table-main my-main-scrollbar">
            <div class="table-tr" v-for="(item, index) in tableData">
              <div class="tr-cell t-title">
                <el-popover
                    placement="bottom"
                    :disabled="item.title?.length < 11"
                    :width="100"
                    trigger="hover"
                    :content="item.title"
                >
                    <template #reference>
                        {{ item.title }}
                    </template>
                </el-popover></div>
              <div class="tr-cell t-room">
                <el-popover
                    placement="bottom"
                    :disabled="item.meetingRoomName?.length < 16"
                    :width="100"
                    trigger="hover"
                    :content="item.meetingRoomName"
                >
                    <template #reference>
                        {{ item.meetingRoomName }}
                    </template>
                </el-popover>
              </div>
              <div class="tr-cell t-time">{{ time(item) }}</div>
              <div class="tr-cell t-status">{{ statusName(item.status) }}</div>
              <div class="tr-cell t-people">{{ item.meetingNumber }}人</div>
              <div>
                  <div class="tr-cell t-operate" :style="{ 'color': (operate(item) === '修改' ? '#3268DC' : '#F56C6C') }"
                  @click="operate(item) === '修改' ? modifyMeeting(item) : delMeeting(item,index) ">
                    {{ operate(item) }}
                  </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {  getTodayMeetingRecordData, getDeleteMeetingRecordData, getCenterAllNumberData, getRoomStatusData, getTimeBusyData, getNoticeData } from '@/request/api/home'
//import { getTimePeriodDate } from '@/request/api/manage'

import Clock from '@/views/home/component/clock.vue'
import GuageChart from '@/views/home/component/guage-chart.vue'
import { ElMessage, ElMessageBox, dayjs } from 'element-plus';
import { meetingState } from '@/utils/types';
import { qwLogin } from '@/request/api/login';

const loading = ref(true);  // 获取数据loading 
const route = useRoute();
const router = useRouter();
const userInfo = ref();  // 获取用户信息 传后端数据

let timesData = ref<any>([])  // 排名前十的时间段
const rulesData = ref<any>([  // 规则
  // 预约
  "点击首页“会议室预约”按钮",
  "点击自已想预约的会议室名称",
  "紧急预约可直接点击“空闲中”会议室",
  "点击自己想预约会议的时间",
  "长期预约会议室可使用“企微”预约",
  // 取消
  "点击首页“取消预约”按钮",
  "点击“历史记录”-“取消会议”",
  "取消后若该会议室空闲可重新预约",
  "“修改会议”≠“取消会议”",
  "取消会议后“历史记录”该记录消失",
])
let notice = ref<any>('暂无公告信息'); // 公告信息

let gaugeData = ref<any>([ // echarts数据展示 今日总预约数
  {
    value: 0,
    name: '中心会议总次数'
  }
])
let roomMeetingData = ref<any>([]) // 会议室状态信息
const timeArr = ref([  // 预约时间点及该时间点可预约状态
  { time: '08:00', state: 3 }, { time: '08:30', state: 3 },
  { time: '09:00', state: 3 }, { time: '09:30', state: 3 },
  { time: '10:00', state: 3 }, { time: '10:30', state: 3 },
  { time: '11:00', state: 3 }, { time: '11:30', state: 3 },
  { time: '12:00', state: 3 }, { time: '12:30', state: 3 },
  { time: '13:00', state: 3 }, { time: '13:30', state: 3 },
  { time: '14:00', state: 3 }, { time: '14:30', state: 3 },
  { time: '15:00', state: 3 }, { time: '15:30', state: 3 },
  { time: '16:00', state: 3 }, { time: '16:30', state: 3 },
  { time: '17:00', state: 3 }, { time: '17:30', state: 3 },
  { time: '18:00', state: 3 }, { time: '18:30', state: 3 },
  { time: '19:00', state: 3 }, { time: '19:30', state: 3 },
  { time: '20:00', state: 3 }, { time: '20:30', state: 3 },
  { time: '21:00', state: 3 }, { time: '21:30', state: 3 },
  { time: '22:00', state: 3 }, { time: '22:30', state: 3 }
])

let tableData = ref<any>([]) // 预约情况数据

/******************************************* 会议室大屏 ***********************************/
/**
 * @description 今日中心会议总次数
*/
const getCenterAllNumber = () => {
  getCenterAllNumberData()
    .then((res) => {
      gaugeData.value = [{
        value: res.data,
        name: '中心会议总次数'
      }]
    })
    .catch((err) => {})
}

/**
 * @description 查询会议室状态
 */
// 处理状态名称展示
const roomStatus = computed(() => (status: number) => {
  switch (status) {
    case 0:
      return '暂停使用'
    case 1:
      return '空闲'
    case 2: 
      return '使用中'
  }
})
const getRoomStatus = () => {
  getRoomStatusData()
    .then((res) => {
      roomMeetingData.value = res.data
    })
    .catch((err) => {})
}

const handleRoomClick = (item: any) => {
    if(item.status !== 0) {
      sessionStorage.setItem('meetingInfo', JSON.stringify({meetingRoomId: String(item.id)}));
      router.push('/meeting-appoint')
  }
}

/******************************************* 预约时间选择 ***********************************/
/**
 * @description 查询当日时间段占用情况
 * 时间状态  0：已过期 1：已预定 2：可预约
*/
const getTimeBusy = async () => {
  let res:any = await getTimeBusyData()
  timeArr.value.forEach((item: any, index: number) => {
    item.state = res.data[index];
  })
}
// 会议时间点
const timeColor = computed(() => (state: any) => {
  switch (state) {
    case 0:
      return 'color-one';
    case 1:
      return 'color-two';
  }
})
// 选择时间段
const selectTime = (item: any) => {
  if ([0, 1].includes(item.state)) {
    return;
  } else {
    router.push('/meeting-appoint')
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
        return sessionStorage.setItem('meetingInfo', JSON.stringify({startTime, endTime}));
    }
    sessionStorage.setItem('meetingInfo', JSON.stringify({ startTime: item.time }));
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


/******************************************* 会议室预约情况 ***********************************/

/**
 * @description 处理列表数据
 * @param data 获取的数据
 */
// 处理时间格式
const time = (item: any) => {
  const time = dayjs(item.startTime).format('HH:mm') + ' - ' + dayjs(item.endTime).format('HH:mm')
  return time
} 
// 会议室状态
const statusName = computed(() => (status: any) => {
  let state = meetingState.find((item: any) => item.value === status)?.label
  return state
})

// 操作 展示状态
const operate = computed(() => (item: any) => {
  // 会议-未开始 且 登陆人员=创建者时(item.createdBy === userInfo.value.userId) 才可以修改
  if (item.status === 0 && item.createdBy === userInfo.value.userId) {
    return '修改';
    // 暂定 状态为"已取消"时 且 登陆人员=创建者时(item.createdBy === userInfo.value.userId)  可删除
  } else if (item.createdBy === userInfo.value.userId) {
    return '删除';
  } else {
    return '';
  }

})

/**
 * @description 查询今日会议情况
 * @param {userId} 用户id
 */
const getTodayRecord = (data: { userId: string }) => {
  getTodayMeetingRecordData(data)
    .then((res) => {
      tableData.value = res.data
    })
    .catch((err) => {})
}


// 点击修改会议
const modifyMeeting = (item: any) => {
  // 会议-未开始 且 登陆人员=创建者时(item.createdBy === userInfo.value.userId) 才可以修改
  item.date = dayjs(item.startTime).format('YYYY-MM-DD');  // 获取日期
  if (item.status === 0 && item.createdBy === userInfo.value.userId) {
    sessionStorage.setItem('meetingInfo', JSON.stringify(item));
    router.push('/meeting-appoint')
  }
}

/**
 * @description 删除会议记录  点击删除
 * @param {userId} 用户id
 * @param {meetingId} 会议id
 */

const delMeeting = (item: any, index: number) => {  
  ElMessageBox.confirm('是否确定删除会议？')
      .then(() => {
        getDeleteMeetingRecordData({userId: userInfo.value.userId, meetingId: item.id})  // 删除会议室接口
            .then((res) => {
              ElMessage.success('删除成功')
              getTodayRecord({userId: userInfo.value.userId})
            })
      .catch((err) => {})
      })
      .catch(() => {
        ElMessage.info('取消删除')
      })
}

/******************************************* 公告 ***********************************/
/**
 * @description 查询所有公告
 */
const getNotice = async () => {
  const res = await getNoticeData()
  notice.value = res.data  
}

/**
 * @description 获取前十时间段数据
 */
// getTimePeriodDate()  
//   .then((res) => {
//     res.data.map((item: any) => {
//       timesData.value.push(`${dayjs(item.startTime).format('HH:mm')}-${dayjs(item.endTime).format('HH:mm')} : 使用${item.count}次`)
//     })    
//   })
//   .catch((err) => {})

onMounted( async () => {
    /* 判断扫码登录状态 */
    const code = decodeURIComponent(route.query.code as string);
    const loginMethod: number = 0; // web扫码登录
    userInfo.value = JSON.parse(localStorage.getItem('userInfo') as string);
    const token = userInfo.value?.accessToken;
    // 扫码登录
    if(!token) {
        if (code === 'undefined') {
            return router.replace('/login');
        }
        try {
            const res:any = await qwLogin({code, loginMethod});
            if (res.code !== '00000') {
                throw new Error(res.msg);
            }
            userInfo.value = res.data;
            localStorage.setItem('userInfo', JSON.stringify(res.data));
            router.replace('/home');
        } catch(err) {
            router.replace('/login');
            return;
        }
    }
    // 登录成功后或已登录状态下执行
    await Promise.all([
        getTodayRecord({ userId: userInfo.value.userId }),  // 查询今日会议情况
        getCenterAllNumber(),  // 查询中心会议总次数
        getRoomStatus(),  // 查询会议室状态
        getTimeBusy(),  // 查询当日时间段占用情况
        getNotice(), // 查询公告
        // getTimePeriodDate()  // 获取时间段数据
    ])
    loading.value = false
});

</script>

<style lang="scss" scoped>
.home-container {

  .home-row {
    display: flex;
    justify-content: space-between;

    .every-block {
      width: 803.2px;
      height: 366px;
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
        font-size: 18.08px;
        color: #3E78F4;
        margin-left: 17px;
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
          // overflow-y: auto;
          .main-items {
            display: flex;
            flex-direction: column;  // 控制内部元素垂直排列
            align-items: center;
            justify-content: space-evenly;
            overflow: hidden; 
            border-radius: 10px;
            background: rgba(255, 255, 255, 0.1);
            box-sizing: border-box;
            // border: 1px solid rgba(111, 167, 249, 0.8);
            // box-shadow: inset 0px 0px 60px -10px rgba(16, 127, 255, 0.4);
            -webkit-backdrop-filter: blur(5px);
            backdrop-filter: blur(5px);
            border: 1px solid rgba(0, 102, 255, .5);
            box-shadow: inset 0 0 30px #1b7ef24d;
            height: 90px;
            &:hover {
              backdrop-filter: blur(1.6px);
              filter: opacity(0.8); /* 鼠标悬停时模糊效果 */
            }
            .name {
              font-size: 17.6px;
              color: #6A6A6A;
            }
            .state {
              font-size: 20.8px;
              font-weight: bold;
              color: #1273DB;
            }
          }
          // main-items 会议室状态为空闲1 为小手，其他为 禁止
          .pointer {
            cursor: pointer;
          }
          .ban {
            cursor: not-allowed;
          }
        }
      }
    }

    /* 预约信息样式 */
    .info {
      display: flex;
      flex-direction: column;
      margin-bottom: 10px;
      // 告示滚动信息
      .info-board {
        display: flex;
        align-items: center;
        height: 32px;
        border-radius: 6px;
        color: #0D3F81;
        background: #FFFFFF;
        border: 2px solid rgba(18, 115, 219, 0.6);
        margin-bottom: 12.8px;

        .el-icon {
          margin: 0 11.2px;
        }

        .info-text {
          width: 94%;
          overflow: hidden;
          @keyframes scrollX {
            0% {
              transform: translateX(760.8px);
            }
            100% {
              transform: translateX(-100%);
            }
          }
          .text-scroll {
            width: max-content;
            white-space: nowrap;
            animation: scrollX 20s linear infinite;
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
              line-height: 67.2px;
              // font-size: 25.6px;
              border-radius: 5px;
              border: 2px solid #6BD1D8;
              .left-year {
                width: 89.6px;
                font-size: 25.6px;
                text-align: center;
                background: #FFF;
                border-radius: 5px 0 0 5px;
              }
              .left-date {
                width: fit-content;
                font-size: 25.6px;
                color: #FFF;
                background: #5ad0d8;
                padding: 0 5px;
              }
            }
            .day-right {
              width: 68.8px;
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
              font-size: 12.8px;
              text-align: center;
              padding: 5px 0;
            }
            .rule-main {
              height: 120px;
              overflow: hidden; // 滚动溢出隐藏
              @keyframes scrollY {
                0% {
                  transform: translateY(120px);
                }
                100% {
                  transform: translateY(-100%);
                }
              }
              .rule-scroll {
                // height: fit-content;
                display: flex;
                flex-direction: column;
                align-items: center;
                font-size: 11.2px;
                color: #676767;
                animation: scrollY 20s linear infinite; // 滚动动画
                .scroll-item {
                  width: 200px;
                  height: 20px;
                  font-size: 12px;
                  border-radius: 4px;
                  box-sizing: border-box;
                  border: 0.2px solid #1273DB;
                  box-shadow: 0px 2px 5px 0px #F1F6FF;
                  
                  white-space: nowrap; /* 确保文本在一行内显示 */
                  overflow: hidden; /* 隐藏溢出的内容 */
                  text-overflow: ellipsis; /* 使用省略的文本表示溢出的内容 */
                  padding: 0 5px;
                  margin-bottom: 5px;
                  &::before {
                    content: '·';
                    margin-right: 2px;
                    color: #676767;
                  }
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
        height: 44.8px;
        background: #FFF;
        border-radius: 15px 15px 0px 0px;
        box-shadow: inset 0px 1px 8px 0px #DBE9F7;
        color: #3E78F4;
        font-size: 18px;
      }

      .choose-main {
        width: 803.2px;
        display: grid;
        grid-template-columns: repeat(6, 1fr); /* 创建6列，每列等宽 */
        grid-template-rows: repeat(5, auto); /* 创建5行，自动高度适应内容 */
        gap: 7px; /* 设置单元格之间的间距 */
        background: #F5F7FA;
        border-radius: 0 0 15px 15px;
        padding: 7px;
        
        .choose-cell {
          height: 57px;
          line-height: 57px;
          text-align: center;
          overflow: hidden; /* 防止内容溢出 */
          font-size: 12.8px;
          background: #FFF;
          border-radius: 5px;
          box-shadow: 1px 1px 2px 1px #DBE9F7;
          transition: transform 0.3s linear;
          &:hover {
            cursor: pointer;
            font-size: 16px;
            color: #FFF;
            background-color: #1273DB;
            transform: translateY(-5px) scale(1.06);
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
        font-size: 18.4px;
        color: #3E78F4;
        margin-bottom: 5px;
      }

      .situation-table {
        flex: 1;
        border: 2px solid #69A5E4;
        border-radius: 15px;
        padding: 10px 18px;

        // 每个单元格共同样式
        .table-th, .table-tr {
          .title, .tr-cell {
            width: 197.12px;
            padding: 0 14.4px;
          }
        }
        .t-time {
          width: 128px !important;
        }
        .t-status {
          width: 96px !important;
        }
        .t-people {
          width: 64px !important;
        }
        .t-operate {
          width: 80px !important;
        }

        .table-th {
          display: flex;
          text-align: center;
          padding-bottom: 6px;
        }
        .table-main {
          max-height: 297.6px;
          // overflow-y: auto;
          // &::-webkit-scrollbar {
          //   display: none;
          // }
          .table-tr {
            display: flex;
            text-align: center;
            color: #666;
            background: #FFF;
            border-radius: 15px;
            margin: 10px 0;
            padding: 11px 0;
            .tr-cell {
              text-wrap: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              &:last-child {
                cursor: pointer;
              }
            }
          }
        }
      }
    }
  }
}
</style>