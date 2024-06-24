<template>
  <div class="history-manage">
    <!-- <van-sticky>
      <van-nav-bar title="全部会议" left-text="返回" left-arrow @click-left="onClickLeft"/>
    </van-sticky> -->
    <!-- <van-field v-model="blurValue" clearable placeholder="请输入群组名称搜索" @update:model-value="updataBlurValue" /> -->
    <van-collapse v-model="activeNames">
      <van-collapse-item class="myMeeting" title="我的会议" name="1" ref="createdRef">
        <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" offset="200"
          :style="{ height: activeNames == '1' ? '84.8vh' : '41.23vh' }" @load="onLoad">
          <van-swipe-cell v-for="item in listData">
            <div class="card">
              <div class="card-title">
                <span>{{ item.title }}</span>
                <van-tag plain size="large" :type="stateColor(item.status)">{{ item.stateInfo }}</van-tag>
              </div>
              <div class="card-content">
                <div class="content-tr"><van-icon name="location" />{{ item.meetingRoomName }}</div>
                <div class="content-tr"><van-icon name="underway" />{{ item.date }} &nbsp; {{ item.time }}</div>
                <div class="content-tr"><van-icon name="user" />发起人：{{ item.adminUserName }}</div>
                <div class="content-tr"><van-icon name="friends" />{{ item.attendees }}</div>
              </div>
            </div>
            <template #right v-if="item.status == 0">
              <van-button text="修改" type="primary" @click="editMeetingInfo(item)" />
              <van-button text="取消" type="danger" @click="cancelMeeting(item.id)"/>
            </template>
          </van-swipe-cell>
        </van-list>
      </van-collapse-item>

      <van-collapse-item class="ended" title="已结束" name="2">
        <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" offset="200"
          :style="{ height: activeNames == '2' ? '84.8vh' : '41.23vh' }" @load="onLoad">
          <van-swipe-cell v-for="item in endedData">
            <div class="card">
              <div class="card-title">
                <span>{{ item.title }}</span>
                <van-tag plain size="large" :type="stateColor(item.status)">{{ item.stateInfo }}</van-tag>
              </div>
              <div class="card-content">
                <div class="content-tr"><van-icon name="location" />{{ item.meetingRoomName }}</div>
                <div class="content-tr"><van-icon name="underway" />{{ item.date }} &nbsp; {{ item.time }}</div>
                <div class="content-tr"><van-icon name="user" />发起人：{{ item.adminUserName }}</div>
                <div class="content-tr"><van-icon name="friends" />{{ item.attendees }}</div>
              </div>
            </div>
          </van-swipe-cell>
        </van-list>
      </van-collapse-item>

    </van-collapse>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router'
import { addMeetingMinutes, cancelMeetingRecord, getHistoryList, getMeetingMinutes, recordExport } from '@/request/api/history'
import dayjs from 'dayjs';
import { meetingState } from '@/utils/type'
import { ElMessage } from 'element-plus';
import { showFailToast } from 'vant';

const router = useRouter();
let title = ref<string>(''); // card-群组名称
let groupPeopleNames = ref<string>(''); // card-群组成员
const userInfo = ref<any>({});  // 用户信息
let activeNames = ref<any>(['1', '2']);  // 折叠面板 展开的面板的值

const listData = ref<any>([]); // 历史会议列表
const endedData = ref<any>([]); // 历史会议列表 我参与的
const loading = ref(false); // 加载状态
const finished = ref(false); // 加载结束状态
const page = ref(1); // 页码
const size = ref(10); // 每页条数
const total = ref(0); // 总条数
// #region
onMounted(() => {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}') // 获取用户信息
  onLoad() // 加载历史会议列表
})

/**
 * @description 状态标签展示颜色
 * @param status 0-已预约 1-进行中 2-已结束 3-已取消
*/
const stateColor = computed(() => (status: number) => {
  switch (status) {
    case 0: return 'primary'
    case 1: return 'warning'
    case 2: return 'default'
    case 3: return 'danger'
  }
})

const transferData = (data: any) => {
  return data.map((item: any) => {
    const date = dayjs(item.startTime).format("YYYY-MM-DD")
    const time = dayjs(item.startTime).format("HH:mm") + "-" + dayjs(item.endTime).format("HH:mm")
    const stateInfo = meetingState.find((itm: any) => itm.value === item.status)?.label;
    return { date, time, stateInfo, ...item }
  })
}
/**
 * @description 历史会议列表
*/
const getHistoryListData = async () => {
  await getHistoryList({ userId: userInfo.value.userId, page: page.value, limit: size.value })
    .then((res: any) => {
        total.value = res.data.length;
        listData.value.push(...transferData(res.data));
        endedData.value.push(...transferData(res.data.filter((item: any) => item.status === 2)));
      })
    .catch(() => {})
}

const onLoad = async () => {
  if (finished.value && loading.value) return;
  loading.value = true;
  await getHistoryListData();
  if (total.value < size.value) {
    return finished.value = true;
  }
  page.value++;
  loading.value = false;
};

// 编辑
const editMeetingInfo = (item: any) => {
  if (item.createdBy == userInfo.value.userId) {
    sessionStorage.setItem('meetingInfo', JSON.stringify(item))
    router.push('/createMeeting')
    refresh()
  } else {
    showFailToast('当前用户没有权限修改此会议');
  }
}

// 增删改的时候 刷新一下列表页面 将页码变为1，鉴于列表是push的 所以将列表置空
const refresh = async() => {
  page.value = 1;
  listData.value = [];
  endedData.value = [];
  finished.value = false;
  await onLoad();
}

/**
 * @description 取消会议
*/
const cancelMeeting = (id: number) => {
  cancelMeetingRecord({userId: userInfo.value.userId, meetingId: id})
    .then((res) => {
      ElMessage.success('取消成功!');
      refresh()
    })
    .catch(err => {
      showFailToast('当前用户没有权限取消此会议');
    })
}
</script>

<style lang="scss" scoped>
html {
  background: #F7F8FA;
}
.history-manage {
  height: 94.6vh;
  background: #F7F8FA;
//   padding: .1rem 0;
  // .van-cell-group {
  //   display: flex;
  //   .van-field {
  //     border: 1px solid rgba(0, 0, 0, 0.1);
  //   }

  // }
  // 折叠面板
  :deep().van-collapse {
    .van-cell {
      font-size: 1rem;
      color: #000;
      background: #FFF;
      span {
        font-weight: bolder;
      }
      i {
        color: #000;
        font-weight: bolder;
      }
    }

    .van-collapse-item__content {
      background: #F7F8FA;
      padding-top: 0;
      // max-height: 42vh;
      overflow: auto;
      .card {
        background: #FFF;
        border-radius: .5rem;
        padding: .7rem 1rem;
        margin: .7rem 0;
        .card-title {
          display: flex;
          justify-content: space-between;
          color: #000;
          font-size: .9rem;
          font-weight: bolder;
        }
        .card-content {
          padding: .3rem 0;
          .content-tr {
            padding: .3rem 0;
            i {
              margin-right: .7rem;
            }
          }
        }
      }
      .van-swipe-cell__right {
        button {
          height: 100%;
        }
      }
    }
  }
}
</style>