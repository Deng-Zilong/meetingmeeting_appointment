<template>
  <div class="manage">
    <div class="manage-theme">
      <div class="theme-left">
          <el-input v-model="input" placeholder="请输入公告" />
          <el-button type="primary" @click="uploadBulletin(input)">上传新公告</el-button>
      </div>
      <div class="theme-right" v-if="userInfo.level === 0">
        <el-tooltip content="新增管理员" placement="top" effect="light">
          <el-button type="primary" class="add-button" @click="handleAddManage">增加管理员</el-button>
        </el-tooltip>
         <!-- 添加群组成员弹窗 -->
         <personTreeDialog 
          v-model="addGroupVisible" 
          title="添加管理员"
          :type="type"
          @close-dialog="closeAddPersonDialog" 
          @submit-dialog="handleCheckedPerson" 
        />
        <el-popover trigger="hover" :width="180" :popper-style="{ maxHeight: '250px', overflow: 'auto' }">
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
      
      <el-tab-pane label="会议列表" name="first" class="first-tab">
        <div class="search-form">
          <el-input v-model="searchForm.title" clearable placeholder="请输入会议主题"  @clear="handleChangeSearch" />
          <el-select v-model="searchForm.meetingRoomId" filterable clearable placeholder="请选择会议室" @clear="handleChangeSearch">
            <el-option v-for="item in roomOptions" :key="item.id" :label="item.value" :value="item.id" />
          </el-select>
          <el-input v-model="searchForm.adminUserName" clearable placeholder="请输入发起人名称" @clear="handleChangeSearch" />
          <el-input v-model="searchForm.department" clearable placeholder="请输入发起人部门" @clear="handleChangeSearch" />
          <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="-" value-format="YYYY-MM-DD"
            start-placeholder="开始日期" end-placeholder="结束日期" />

          <el-button type="primary" @click="handleSearch">查 询</el-button>
          <el-button type="primary" @click="resetSearch">重 置</el-button>
        </div>
        
        <div class="tab-table">
          <div class="table-th">
            <div class="title t-theme">会议主题</div>
            <div class="title t-name">会议室名称</div>
            <div class="title">发起人</div>
            <div class="title t-department">发起人部门</div>
            <div class="title">会议时间</div>
            <div class="title t-attend">参会人员</div>
            <div class="title">会议状态</div>
            <div class="title">操作</div>
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
                    <div class="tr-cell t-theme">
                      <el-popover
                            placement="bottom"
                            :disabled="item.title?.length < 13"
                            :width="240"
                            trigger="hover"
                            :content="item.title"
                        >
                            <template #reference>
                              <p class="three-dot">{{ item.title }}</p>
                            </template>
                        </el-popover>
                    </div>
                    <div class="tr-cell t-name">
                      <el-popover
                            placement="bottom"
                            :disabled="item.meetingRoomName?.length < 15"
                            :width="240"
                            trigger="hover"
                            :content="item.meetingRoomName"
                        >
                            <template #reference>
                              <p class="three-dot">{{ item.meetingRoomName }}</p>
                            </template>
                        </el-popover>
                    </div>
                    <div class="tr-cell">{{ item.adminUserName }}</div>
                    <div class="tr-cell t-department">{{ item.department }}</div>
                    <div class="tr-cell">{{ item.time }}</div>
                    <div class="tr-cell t-attend">
                        <el-popover
                            placement="bottom"
                            :disabled="item.attendees?.length < 14"
                            :width="300"
                            trigger="hover"
                            :content="item.attendees"
                        >
                            <template #reference>
                              <p class="three-dot">{{ item.attendees }}</p>
                            </template>
                        </el-popover>
                    </div>
                    <div class="tr-cell">{{ item.stateValue }}</div>
                    <div class="tr-cell">会议纪要</div>
                  </div>
                </div>
              </el-timeline-item>
              <div class="loading" v-show="isLoading">数据加载中......</div>
            </el-timeline>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="操作会议室" name="second" class="second-tab">
        <ManageRoom v-if="activeName == 'second'" />        
      </el-tab-pane>

      <el-tab-pane label="管理设备" name="fourth" class="fourth-tab">
        <ManageEquip v-if="activeName == 'fourth'" />
      </el-tab-pane>

      <el-tab-pane label="柱状图统计" name="third" class="third-tab">
        <div class="third-tab-date">
          <span class="demonstration">选择日期：</span>
          <el-date-picker v-model="pickDates" type="daterange" range-separator="To" value-format="YYYY-MM-DD"
          start-placeholder="开始日期" end-placeholder="结束日期" :shortcuts="shortcuts" @change="handleEchartDate" />
        </div>
        <div class="third-tab-echart">
          <TimeChart class="tab-echart" :pieData="pieData" v-if="activeName == 'third'" />
          <RoomChart class="tab-echart" :barData="barData" v-if="activeName == 'third'" />
        </div>
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
    import TimeChart from '@/views/manage/component/room-pie.vue';
    import RoomChart from '@/views/manage/component/room-chart.vue';
    
    import { addNoticeData, getSelectAdminData, deleteAdminData, addAdminData, getAllRecordData, getRoomSelectionRate, getRoomOccupancyDate } from '@/request/api/manage'
      

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
    

/******************************************* 会议记录 ***********************************/

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

    // 实时搜索查询 暂无
    const themeValue = ref()
    const createdByValue = ref()
    const departmentValue = ref()
    const handleChangeSearch = () => {
      refresh()
      getDataOnScroll()
    }
    
    // 查询信息
    let searchForm = ref<any>({
      title: '',
      meetingRoomId: '',
      adminUserName: '',
      department: '',
      dateRange: [],
    })
    const roomOptions = useMeetingStatus.centerRoomName.map((item: any) => {
      const id = item.id
      const value = item.roomName
      return { id, value }
    })
    const handleSearch = () => {
      refresh();
      getDataOnScroll();
    }
    const resetSearch = () => {
      searchForm.value = {
          title: '',
          meetingRoomId: '',
          adminUserName: '',
          department: '',
          dateRange: [],
      }
      refresh();
      getDataOnScroll();
    }
    const refresh = () => {
      page.value = 1;
      manageData.value = []
      canLoadMore.value = true;
    }

    /**
     * @description 查询所有会议记录
     * @param {number} pageNum 页码
     * @param {number} pageSize 每页条数 默认10
     * @param {number} currentLevel 用户等级
     */
    const getAllRecord = async () => {
      let list: any = [];
      let total: any = 0;
      const params = {
        pageNum: page.value,
        pageSize: limit.value,
        title: searchForm.value.title,
        meetingRoomId: searchForm.value.meetingRoomId,
        adminUserName: searchForm.value.adminUserName,
        department: searchForm.value.department,
        startDate: searchForm.value.dateRange[0],
        endDate: searchForm.value.dateRange[1],
      }
      await getAllRecordData(params)
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
        const {data: newData, total} = await getAllRecord()  // 查询所有会议记录        
        
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
    
/******************************************* 统计图 ***********************************/
    const pickDates = ref<any>(); // 统计图 日期选择
    const shortcuts = [
      {
        text: '过去七天',
        value: () => {
          const end = new Date()
          const start = new Date()
          start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
          return [start, end]
        },
      },
      {
        text: '过去一个月',
        value: () => {
          const end = new Date()
          const start = new Date()
          start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
          return [start, end]
        },
      },
      {
        text: '过去三个月',
        value: () => {
          const end = new Date()
          const start = new Date()
          start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
          return [start, end]
        },
      },
    ]
    const pieData = ref<any>(); // 饼图数据
    const barData = ref<any>(); // 柱状图数据
    const handleEchartDate = (val: any) => {
      if (!val) {
        getRoomSelectionRate({})  // 获取会议室选择率数据 接口
          .then((res) => {
            pieData.value = res.data
          })
          .catch((err) => { })

        getRoomOccupancyDate({})  // 获取会议室选择率数据 接口
        .then((res) => {
          barData.value = res.data
        })
        .catch((err) => {})
      } else {
        getRoomSelectionRate({ startDate: val[0], endDate: val[1] })  // 获取会议室选择率数据 接口
          .then((res) => {
            pieData.value = res.data
          })
          .catch((err) => { })

        getRoomOccupancyDate({ startDate: val[0], endDate: val[1] })  // 获取会议室选择率数据 接口
          .then((res) => {
            barData.value = res.data
          })
          .catch((err) => {})
      }
    }
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
        width: 20px;
        height: 20px;
        &:hover {
          // background: #c3d6fc;
          color: red;
          cursor: pointer;
        }
      }
    }
  }
  .manage {
    height: 751px;
    padding: 0 45px;
    // 全部按钮统一样式
    .el-button {
      border-radius: 9.6px;
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
            border-radius: 8px;
            box-shadow: none;
          }
        }
      }
      .theme-right {
        display: flex;
        align-items: center;
        margin-left: 20px;
        width: 243px;  // 宽度
        .add-button {
          margin-right: 20px;
        }
      }
    }
    .manage-tabs {
      :deep().el-tabs__item {
        font-size: 16px;
      }
      // tab-会议列表样式
      .first-tab {
        :deep() .search-form {
          margin-bottom: 10px;
          .el-input {
            width: 220px;
            margin-right: 10px;
          }
          .el-select {
            width: 220px;
            margin-right: 10px;
          }
          .el-date-editor--daterange {
            width: 260px;
            margin-right: 10px;
            input {
              font-size: 16px;
            }
          }
        }
        .tab-table {
          height: calc(635.2px - 40px);
          border: 3px solid rgba(18, 115, 219, 0.8);
          border-radius: 15px;
          padding: 10px 18px;
  
          .table-th, .table-tr { // 每行共同样式
            display: flex;
            text-align: center;
            div {
              // width: 224px;
              width: 150px;
              padding: 0 17.6px; // 只控制每行左右内边距
              &:last-child {
                width: 120px;
              }
            }
            // 发起人 主题 参会人员 单独设置宽
            .t-name {
              width: 240px;
            }
            .t-department {
              width: 180px;
            }
            .t-theme {
              width: 224px;
            }
            .t-attend {
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
            margin-left: 40px;
            margin-right: 30px;
            .title {
              padding: 10px 0; // 单独控制头部上下内边距
              font-size: 17.6px;
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
              top: 8.8px;
              text-align: center;
              p:first-child {
                // font-size: 17.6px;
                font-weight: 600;
                color: #676767;
                margin-bottom: 10px;
              }
              p:last-child {
                font-size: 19.2px;
                font-weight: 600;
                color: #3A3A3A;
              }
            }
            .table-main {
              font-size: 16px;
              margin-left: 40px;
              .table-tr {
                color: #666;
                background: #FFF;
                border-radius: 15px;
                margin: 10px 0;
                padding: 20px 0; // 单独控制单元行上下内边距
                .tr-cell {
                  text-wrap: nowrap;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  &:last-child {
                    color:#75C560;
                    cursor: pointer;
                  }
                }
              }
            }
            // 滚动条样式
            .loading {
              color: #666666;
              font-size: 20px;
              text-align: center;
              font-weight: 300;
            }
            &::-webkit-scrollbar {
              display: block !important;
              width: 17.6px !important;
              border-radius: 15px !important;
            }
            /* 自定义滚动条轨道 */
            &::-webkit-scrollbar-track {
              // background: #FFFFFF;
              border-radius: 15px;
            }
            
            /* 自定义滚动条的滑块（thumb） */
            &::-webkit-scrollbar-thumb {
              background: #EDEBEB;
              border-radius: 15px;
            }
          }
        }
      }

      // tab-操作会议室
      .second-tab {
        display: flex;
        justify-content: space-between;
      }

      .third-tab {
        .third-tab-date {
          margin: 0 0 10px calc(50% - 256px) ;
        }
        .third-tab-echart {
          display: flex;
          justify-content: space-between;
          .tab-echart {
            background: #FFF;
            border-radius: 15px;
          }
        }
      }

    }
  }
</style>