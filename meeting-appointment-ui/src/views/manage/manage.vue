<template>
  <div class="container manage">
    <div class="theme">
      <div class="theme-left">
        <div class="left-common meeting-set">
          <el-checkbox-group v-model="checkList" @change="changeCheckAll">
            <el-checkbox v-for="item in checkItem" :label="item.label" :value="item.id" />
          </el-checkbox-group>
          <div class="text">会议室禁用设置</div>
        </div>
        <div class="left-common meeting-bulletin">
          <el-input v-model="input" placeholder="请输入公告" />
          <div class="text upload-text" @click="uploadBulletin(input)">上传公告</div>
        </div>
      </div>
      <div class="theme-right">
        <el-icon class="plus-icon"><Plus /></el-icon>
        <el-popover trigger="click" :width="180" :popper-style="{ maxHeight: '250px', overflow: 'auto'}">
          <template #reference>
            <div class="operate-people">
              操作管理员<el-icon><arrow-down /></el-icon>
            </div>
          </template>
          <div class="people-items" v-if="manageList.length" v-for="(item, index) in manageList">
            <span>{{ item }}</span>
            <el-icon @click="handleDelPeople(index)"><close /></el-icon>
          </div>
          <div v-else>暂无管理员</div>
        </el-popover>
      </div>
    </div>
    <div class="manage-table">
      <div class="table-th">
        <div class="title">会议室名称</div>
        <div class="title">会议时间</div>
        <div class="title">会议主题</div>
        <div class="title attend-cell">参会人员</div>
        <div class="title">会议状态</div>
        <div class="title">其他</div>
      </div>
      <div class="table-container" ref="timelineRef" v-for="(value, index) in manageData">
        <div class="table-left">
          <p>{{ value.month }}月</p>
          <p>{{ value.day }}</p>
        </div>
        <div class="table-main">
          <div class="table-tr" v-for="(item, index) in value.list">
            <div class="tr-cell">{{ item.meetingRoomName }}</div>
            <div class="tr-cell">{{ item.time }}</div>
            <div class="tr-cell">{{ item.title }}</div>
            <div class="tr-cell attend-cell">{{ item.attendees }}</div>
            <div class="tr-cell">{{ item.stateValue }}</div>
            <div class="tr-cell"></div>
          </div>
        </div>
        <div class="loading" v-show="isLoading">数据加载中......</div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
    import { ref, onMounted, computed } from 'vue'
    import { ElMessage, ElMessageBox, dayjs } from 'element-plus';
    import { useInfiniteScroll } from '@vueuse/core'
    import { Close } from '@element-plus/icons-vue'
    import { meetingState } from '@/utils/types';
    import { getMeetingBan, getSelectAdminData, getAllRecordData } from '@/request/api/manage'

    // 会议室状态 0-暂停使用 1-空闲 2-使用中
    const checkList = ref([3,5])  // 选中会议室 为禁用会议室
    const checkItem = ref([  // 会议室
      { 
        id: 1,
        label: '广政通宝会议室',
        status: 1
      },
      { 
        id: 2,
        label: 'EN-2F-02 恰谈室会议室',
        status: 1
      },
      { 
        id: 3,
        label: 'EN-2F-03 恰谈室会议室',
        status: 1
      },
      { 
        id: 4,
        label: 'EN-3F-02 恰谈室会议室',
        status: 1
      },
      { 
        id: 5,
        label: 'EN-3F-03 恰谈室会议室',
        status: 1
      },
    ])
    const input = ref(''); 
    const manageList = ref<any>([])  // 所有管理员列表
    
    let manageData = ref<any>([]); // 所有会议记录数据
    const timelineRef = ref(null); // 获取dom节点
    let limit = ref(10); // 默认限制条数 10
    let page = ref(1); // 默认页数 1
    let isLoading = ref(false); // 控制数据加载中是否显示
    let canLoadMore = ref(true); // 是否 继续请求数据
    
    // 自己设置的数据列表！！！
    let manage = ref([
         {
            id: 1,
            title: "test会议1",
            description: "会议内容1",
            createdBy: "dzl",
            adminUserName: "邓子龙",
            meetingRoomId: 4,
            meetingRoomName: "会议室4",
            location: 4,
            meetingNumber: 3,
            attendees: "邓子龙,邓子龙,邓子龙,邓子龙,邓",
            startTime: "2024-04-30 11:22:10",
            endTime: "2024-04-30 11:23:11",
            status: 0,
            isDeleted: 0,
            date: '2024-04-30'
        },
        {
            id: 1,
            title: "test会议1111111111111111111111",
            description: "会议内容1",
            createdBy: "dzl",
            adminUserName: "邓子龙",
            meetingRoomId: 4,
            meetingRoomName: "会议室4",
            location: 4,
            meetingNumber: 3,
            attendees: "卢振兴,翟臣宇,杨家乐,寇梦梦,杨家乐,寇梦梦,杨家乐,寇梦梦,杨家乐,杨家乐,寇梦梦,杨家乐杨家乐,寇梦梦,杨家乐",
            startTime: "2024-04-30 11:22:10",
            endTime: "2024-04-30 11:23:11",
            status: 2,
            isDeleted: 0,
            date: '2024-04-30'
        },
        {
            id: 1,
            title: "test会议1",
            description: "会议内容1",
            createdBy: "dzl",
            adminUserName: "邓子龙",
            meetingRoomId: 4,
            meetingRoomName: "EN-2F-02 恰谈室会议室是事实会",
            location: 4,
            meetingNumber: 3,
            attendees: "邓子龙,zhangsan,lisi",
            startTime: "2024-12-29 11:22:10",
            endTime: "2024-04-30 11:23:11",
            status: 2,
            isDeleted: 0,
            date: '2024-04-29'
        },
        
    ])

    onMounted(() => {
      // manageData.value = processData(manage.value)
      getMeetingBan({id: 1, status: 1})
      manageData.value = getAllRecord({ page: page.value, limit: limit.value })  // 查询所有会议记录
      getSelectAdmin()  // 查询所有管理员
    })

    // 会议室禁用设置
const changeCheckAll = (value: any) => {
  console.log(value,);
   const meeting = value.map((item: any) => {
    if (!checkList.value.includes(item.id)) {
      return {
        id: item.id,
        status: item.status,
      }
    }
})
  
  let arr1 = ref({});
      checkItem.value.forEach((item: any) => {
        if (checkList.value.includes(item.id)) {
          item.status = 0;

        }
        console.log(item, 'item');
      })
      console.log(checkList.value, 'changeCheckAll', );
}
    // const arr1 =  checkList.value.map((item: any) => {
    //   return {
    //     id: item.id,
    //     status: item.status
    //   }
    // })
    // getSelectAdmin(arr1)
    
    // 公告输入信息
    const uploadBulletin = (input: string) => {
      ElMessageBox.confirm('确定上传公告吗？')
        .then(() => {
          // 判断公告信息是否为空
          if (input) {
            ElMessage.success('上传公告成功')
          } else {
            ElMessage.warning('请输入公告')
          }
        })
        .catch(() => {
          ElMessage.warning('取消上传公告')
        })
    }

    /**
     * @description 查询所有管理员 
     */
    const getSelectAdmin = async () => {
      const res = await getSelectAdminData()
      manageList.value = res.data
    }

    /**
     * @description 增删管理员    还没写！！！
     * @param {string} userId 用户id
     * @param {number} level 用户等级-管理员
     */
    
    // 操作管理员--删除管理员
    const handleDelPeople = (index: number) => {
      ElMessageBox.confirm('确定删除该管理员吗？')
        .then(() => {
          manageList.value.splice(index,1)
          ElMessage.success('删除成功')          
        })
        .catch(() => {
          ElMessage.warning('取消删除')
        })
    }

    /**
     * @description 处理列表数据
     * @param data 获取的数据
     */
     const processData = (data: any[]) => {
      return data.reduce((acc: any, current: any) => {
        const obj = acc.find((group: any) => group.date === current.date);
        const month = dayjs(current.startTime).format('M');
        const day = dayjs(current.startTime).format('D');
        current.time = `${dayjs(current.startTime).format('HH:mm')} - ${dayjs(current.endTime).format('HH:mm')}`;
        current.stateValue = meetingState.find((item: any) => item.value === current.status)?.label;
        
        if (obj) {
            obj.list.push(current);
        } else {
            acc.push({ date: current.date, month: month, day: day, list: [current] });
        }
        return acc;
      }, []);
    }

    /**
     * @description 查询所有会议记录
     * @param {number} page 页码
     * @param {number} limit 每页条数 默认10
     */
    const getAllRecord = async (data: { page: number, limit: number}) => {
      let list:any = []
      await getAllRecordData(data)
        .then((res) => {
          list = processData(res.data);
        })
        .catch((err) => {
          console.log(err, "err");
        });
      return list;
}
    
    // 滚动加载
    const getDataOnScroll = async () => {
      // 中断处理
      if (!canLoadMore.value || isLoading.value) return;
      // 打开loading
      isLoading.value = true;
      // 延迟请求
      await new Promise((resolve) => setTimeout(resolve, 2000));
      // 发送请求
      const newData = await getAllRecord({ page: page.value, limit: limit.value });
      // 返回数组长度
      const length = ref<number>(newData.length)
      // 若返回数据长度小于限制 停止加载
      if(length < limit) {
          canLoadMore.value = false;
      }
      // 合并数据
      manageData.value = [...manageData.value, ...newData];
      // page + 1
      page.value++;
      // 关闭loading
      isLoading.value = false;
    };

    // 滚动加载插件
    useInfiniteScroll(
      timelineRef, // 容器
      async () => {
          await getDataOnScroll();
      },
      {
          distance: 15, // 距离底部的长度
      }
    );
    
</script>
<style scoped lang="scss">
  // 操作管理员 气泡弹窗样式
  .el-popover {
    height: 250px;
    .people-items {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 5px 2px;
      &:hover {
        background: #D7E6F8;
      }
      .el-icon {
        width: 1.25rem;
        height: 1.25rem;
        &:hover {
          // background: #c3d6fc;
          color: red;
          cursor: pointer;
        }
      }
    }
  }
  .manage {
    background-color: #f5f5f5;
    padding: 0 26px;
    .theme {
      display: flex;
      height: 90px;
      margin-bottom: 10px;
      .theme-left {
        width: 80%;
        height: 2.8rem;
        .left-common {
          display: flex;
          align-items: center;
          height: 100%;
          .text {
            color: #FFF;
            background: #409EFF;
            border-radius: .5rem;
            padding: .35rem 1.5rem;
          }
        }
        .meeting-set {
          .el-checkbox-group {
            display: flex;
            align-items: center;
            justify-content: space-evenly;
            width: 85%;
            background: #FFF;
            border-radius: .5rem;
          }
        }
        .meeting-bulletin {
          :deep().el-input {
            width: 89%;
            .el-input__wrapper {
              border-radius: .5rem;
              box-shadow: none;
            }
          }

          .upload-text {
            cursor: pointer;
          }
        }
      }
      .theme-right {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 20%;
        height: 70%;
        background: #FFF;
        border-radius: .5rem;
        margin-top: .8rem;
        padding: 0 0.8rem;
        .plus-icon {
          width: 1.9rem;
          height: 1.9rem;
          color: #3268DC;
          background: #ECF2FF;
          cursor: pointer;
        }
        .operate-people {
          &:hover {
            color: #409EFF;
            cursor: pointer;
          }
          .el-icon {
            margin-left: 12px;
          }
        }
      }
    }
    .manage-table {
      height: 38.75rem;
      border: .1875rem solid rgba(18, 115, 219, 0.8);
      border-radius: 15px;
      padding: 0.625rem 1.125rem;

      .table-th, .table-tr { // 每行共同样式
        display: flex;
        text-align: center;
        div {
          width: 14rem;
          padding: 0 1.1rem; // 只控制每行左右内边距
          &:last-child {
            width: 5rem;
          }
        }
        // 参会人员 单独设置宽
        .attend-cell {
          flex: 1;
        }
      }
      .table-th {
        margin-left: 2.5rem;
        .title {
          padding: 10px 0; // 单独控制头部上下内边距
        }
      }
      .table-container {
        position: relative;
        max-height: 92%;
        overflow-y: auto;
        .table-left {
          position: absolute;
          top: 1.25rem;
          text-align: center;
          p:first-child {
            // font-size: 1.1rem;
            font-weight: 600;
            color: #676767;
            margin-bottom: .625rem;
          }
          p:last-child {
            font-size: 1.2rem;
            font-weight: 600;
            color: #3A3A3A;
          }
        }
        .table-main {
          margin-left: 2.5rem;
          .table-tr {
            color: #666;
            background: #FFF;
            border-radius: 0.9375rem;
            margin: 10px 0;
            padding: 25px 0; // 单独控制单元行上下内边距
            .tr-cell {
              text-wrap: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
        // 滚动条样式
        .loading {
          color: #666666;
          font-size: 1.25rem;
          text-align: center;
          font-weight: 600;
        }
        &::-webkit-scrollbar {
          width: 1.1rem;
          border-radius: .9375rem;
        }
        /* 自定义滚动条轨道 */
        &::-webkit-scrollbar-track {
          // background: #FFFFFF;
          border-radius: .9375rem;
        }
        
        /* 自定义滚动条的滑块（thumb） */
        &::-webkit-scrollbar-thumb {
          background: #EDEBEB;
          border-radius: .9375rem;
        }
      }
    }
  }
</style>