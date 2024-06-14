<template>
  <div class="manage">
    <div class="manage-theme">
      <div class="theme-left">
          <el-input v-model="input" placeholder="请输入公告" />
          <el-button type="primary" @click="uploadBulletin(input)">上传新公告</el-button>
      </div>
      <div class="theme-right" v-if="userInfo.level === 0">
        <el-tooltip content="新增管理员" placement="top" effect="light">
          <el-button type="primary" @click="handleAddManage">增加管理员</el-button>
        </el-tooltip>
         <!-- 添加群组成员弹窗 -->
         <personTreeDialog 
          v-model="addGroupVisible" 
          title="添加管理员"
          :type="type"
          @close-dialog="closeAddPersonDialog" 
          @submit-dialog="handleCheckedPerson" 
        />
        <el-popover trigger="hover" :width="180" :popper-style="{ maxHeight: '250px', overflow: 'auto'}">
          <template #reference>
              <el-button type="primary">删除管理员</el-button>
          </template>
          <div class="people-items" v-if="manageList?.length" v-for="(item, index) in manageList">
            <span>{{ item.userName }}</span>
            <el-icon @click="handleDelPeople(index)"><close /></el-icon>
          </div>
          <div v-else>暂无管理员</div>
        </el-popover>
      </div>
    </div>
    <el-tabs v-model="activeName" class="manage-tabs" @tab-click="handleClick">
      
      <el-tab-pane label="会议列表" name="first">
        <div class="tab-table">
          <div class="table-th">
            <div class="title">会议室名称</div>
            <div class="title">会议时间</div>
            <div class="title">会议主题</div>
            <div class="title attend-cell">参会人员</div>
            <div class="title">会议状态</div>
            <div class="title">其他</div>
          </div>
          <div class="table-container" ref="timelineRef">
            <el-timeline>
              <el-timeline-item  placement="top" v-for="(value, index) in manageData">
                <div class="table-left">
                  <p>{{ value.month }}月</p>
                  <p>{{ value.day }}</p>
                </div>
                <div class="table-main">
                  <div class="table-tr" v-for="(item, index) in value.list">
                    <div class="tr-cell">{{ item.meetingRoomName }}</div>
                    <div class="tr-cell">{{ item.time }}</div>
                    <div class="tr-cell">
                      <el-popover
                            placement="bottom"
                            :disabled="item.title?.length < 12"
                            :width="240"
                            trigger="hover"
                            :content="item.title"
                        >
                            <template #reference>
                              <p class="three-dot">{{ item.title }}</p>
                            </template>
                        </el-popover>
                    </div>
                    <div class="tr-cell attend-cell">
                        <el-popover
                            placement="bottom"
                            :disabled="item.attendees?.length < 33"
                            :width="400"
                            trigger="hover"
                            :content="item.attendees"
                        >
                            <template #reference>
                              <p class="three-dot">{{ item.attendees }}</p>
                            </template>
                        </el-popover>
                    </div>
                    <div class="tr-cell">{{ item.stateValue }}</div>
                    <div class="tr-cell"></div>
                  </div>
                </div>
              </el-timeline-item>
              <div class="loading" v-show="isLoading">数据加载中......</div>
            </el-timeline>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="操作会议室" name="second" class="second-tab">
        <ManageRoom />        
      </el-tab-pane>

      <el-tab-pane label="管理设备" name="fourth" class="fourth-tab">
        <ManageEquip />
      </el-tab-pane>

      <el-tab-pane label="柱状图统计" name="third" class="third-tab">
        <TimeChart class="tab-echart" v-if="activeName == 'third'" />
        <RoomChart class="tab-echart" v-if="activeName == 'third'" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script lang="ts" setup>
    import { ref, onMounted, computed } from 'vue'
    import { ElMessage, ElMessageBox, dayjs, type TabsPaneContext } from 'element-plus';
    import { useInfiniteScroll } from '@vueuse/core'
    import { Close } from '@element-plus/icons-vue'

    import { meetingStatus } from '@/stores/meeting-status'
    import { meetingState } from '@/utils/types';
    import personTreeDialog from "@/components/person-tree-dialog.vue";
    import ManageRoom from '@/views/manage/component/manage-room.vue';
    import ManageEquip from '@/views/manage/component/manage-equip.vue';
    import TimeChart from '@/views/manage/component/time-chart.vue';
    import RoomChart from '@/views/manage/component/room-chart.vue';
    
    import { addNoticeData, getSelectAdminData, deleteAdminData, addAdminData, getAllRecordData } from '@/request/api/manage'
      

    // let loading = ref(true) // 加载状态
    let userInfo = ref<any>({});  // 获取用户信息 用于传后端参数
    const useMeetingStatus = meetingStatus();
    
    // 会议室状态 0-暂停使用 1-空闲 2-使用中

    let input = ref<any>('');   // 公告信息框
    let manageList = ref<any>([])  // 所有管理员列表
    let manageData = ref<any>([]); // 所有会议记录数据
    
    const timelineRef = ref(null); // 获取dom节点
    let limit = ref(10); // 默认限制条数 10
    let page = ref(1); // 默认页数 1
    let isLoading = ref(false); // 控制数据加载中是否显示
    let canLoadMore = ref(true); // 是否 继续请求数据

    // 添加群组人员弹窗数据
    let addGroupVisible = ref(false)
    let type = ref<number>(4); // 1 创建群组 2 修改群组


    const activeName = ref('first')
    const handleClick = (tab: TabsPaneContext, event: Event) => {
    }
    onMounted(() => {
      userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}');  // 用户信息
      getSelectAdmin()  // 查询所有管理员
      getDataOnScroll()  // 滚动加载
      useMeetingStatus.getCenterRoomName();  // 获取所有会议室及状态
    })

/******************************************* 公告 ***********************************/
    /**
     * @description 上传公告
     * @param {currentLevel} 用户等级
     * @param {substance} 公告内容
    */
    // 公告输入信息
    const uploadBulletin = (item: string) => {
      if (item) {
        ElMessageBox.confirm('确定上传公告吗？')
        .then(() => {
          addNoticeData({ currentLevel: userInfo.value.level, currentUserId: userInfo.value.userId, substance: item }) // 上传公告
          ElMessage.success('上传公告成功')
          input.value = ''  // 上传公告后清空输入框
        })
        .catch(() => {
          ElMessage.warning('取消上传公告')
        })
      } else {
        ElMessage.warning('请输入公告')
      }
    }


/******************************************* 管理员 ***********************************/
    /**
     * @description 查询所有管理员 
     * @param {number} currentLevel 用户等级
     */
    const getSelectAdmin = async () => {
        // 超级管理员时才可调用
        if (userInfo.value.level != 0) return;
        const res = await getSelectAdminData({ currentLevel: userInfo.value.level })
        manageList.value = res.data
    }

    /**
     * @description 删除管理员
     * @param {string} userId 用户id
     */
    const deleteAdmin = (data: { userId: string }) => {
      deleteAdminData(data)
        .then(() => {
          getSelectAdmin()
        })
        .catch((err) => {})
    }
    // 操作管理员--删除管理员
    const handleDelPeople = (index: number) => {
      ElMessageBox.confirm('确定删除该管理员吗？')
        .then(() => {
          deleteAdmin({ userId: manageList.value[index].userId })
          ElMessage.success('删除成功') 
        })
        .catch(() => {
          ElMessage.warning('取消删除')
        })
    }

    // 添加管理员
    const handleAddManage = () => {
      addGroupVisible.value = true;
    }
    
    /**
     * @description 提交需要添加的群组人员
     * 
     * @description 新增管理员
     * @param {userIds} 用户id数组
     */
      const handleCheckedPerson = (type: number, form: any) => {
        if (type == 4) {
          addAdminData({ userIds: form })  // 新增管理员
            .then(() => {
              ElMessage.success('成功添加管理员')
              getSelectAdmin()  // 调用全部管理员列表
            })
            .catch(() => { })
        }
        // 关闭弹窗
        closeAddPersonDialog();
    }
    /**
     * @description 关闭添加群组人员弹窗
     */
     const closeAddPersonDialog = () => {
        addGroupVisible.value = false;
    }
    

/******************************************* 会议室记录 ***********************************/

    /**
     * @description 处理列表数据
     * @param data 获取的数据
     */
     const processData = (data: any[]) => {
        return data.reduce((acc: any, current: any) => {
            current.date = dayjs(current.startTime).format('YYYY-MM-DD')
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
     * @param {number} pageNum 页码
     * @param {number} pageSize 每页条数 默认10
     * @param {number} currentLevel 用户等级
     */
    const getAllRecord = async (data: { pageNum: number, pageSize: number, currentLevel: number}) => {
      let list: any = [];
      let total: any = 0;
      await getAllRecordData(data)
        .then((res) => {
          total = res.data.length;
          list = processData(res.data);
        })
        .catch((err) => { })
      return {
        data: list,
        total,
      };
    }


/******************************************* 滚动加载 ***********************************/

    // 滚动加载
    const getDataOnScroll = async () => {
        // 中断处理
        if (!canLoadMore.value || isLoading.value) return;
        // 打开loading
        isLoading.value = true;
        // 发送请求
        const {data: newData, total} = await getAllRecord({ pageNum: page.value, pageSize: limit.value, currentLevel: userInfo.value.level })  // 查询所有会议记录        
        
        // 若返回数据长度小于限制 停止加载
        if(total < limit.value) {
            canLoadMore.value = false;
        }
        
        // 合并数据
        manageData.value = await [ ...manageData.value, ...newData].reduce((acc: any, cur: any) => {
            // 找出数组中日期一样的item
            const obj = acc.find((item:any) => cur.date == item.date);
            // 如果存在，将它的list push到已有日期的list中
            if (obj) {
                obj.list.push(...cur.list);
            } else {
                acc.push(cur)
            }
            return acc;
        }, []);
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
            distance: 30, // 距离底部的长度
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
    height: 46.9375rem;
    padding: 0 45px;
    // 全部按钮统一样式
    .el-button {
      border-radius: 0.6rem;
    }
    .manage-theme {
      display: flex;
      margin-bottom: 10px;
      .theme-left {
        display: flex;
        align-items: center;
        flex: 1;  // 占满剩余宽度
        :deep().el-input {
          .el-input__wrapper {
            border-radius: .5rem;
            box-shadow: none;
          }
        }
      }
      .theme-right {
        display: flex;
        align-items: center;
        margin-left: 40px;
        width: 13.5rem;  // 宽度
      }
    }
    .manage-tabs {
      :deep().el-tabs__item {
        font-size: 1rem;
      }
      // tab-会议列表样式
      .tab-table {
        height: 39.7rem;
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
          // 有显示popover的 单独设置溢出为省略号
          .three-dot {
            text-wrap: nowrap;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
        .table-th {
          margin-left: 2.5rem;
          margin-right: 30px;
          .title {
            padding: 10px 0; // 单独控制头部上下内边距
            font-size: 1.1rem;
            font-weight: 400;
            color: #3A3A3A;
          }
        }
        .table-container {
          // position: relative;
          max-height: 92%;
          overflow-y: auto;
          padding-right: 10px;
          .table-left {
            position: absolute;
            top: .55rem;
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
            font-size: 1rem;
            margin-left: 2.5rem;
            .table-tr {
              color: #666;
              background: #FFF;
              border-radius: 0.9375rem;
              margin: 10px 0;
              padding: 20px 0; // 单独控制单元行上下内边距
              .tr-cell {
                text-wrap: nowrap;
                white-space: nowrap;
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
            font-weight: 300;
          }
          &::-webkit-scrollbar {
            display: block !important;
            width: 1.1rem !important;
            border-radius: .9375rem !important;
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

      // tab-操作会议室
      .second-tab {
        display: flex;
        justify-content: space-between;
      }

      .third-tab {
        display: flex;
        justify-content: space-between;
        .tab-echart {
          background: #FFF;
          border-radius: .9375rem;
        }
      }

    }
  }
</style>