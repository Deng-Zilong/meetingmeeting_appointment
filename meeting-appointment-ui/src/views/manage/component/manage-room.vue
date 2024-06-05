<template>
  <!-- 新增会议室 -->
  <el-card>
    <template #header>
      <div class="card-header">新增会议室</div>
    </template>
    <el-form :model="addMeetingForm">
      <el-form-item label="会议室名称：">
        <el-input v-model="addMeetingForm.roomName" />
      </el-form-item>
      <el-form-item label="会议室位置：">
        <el-input v-model="addMeetingForm.location" />
      </el-form-item>
      <el-form-item label="会议室容量：">
        <el-input type="number" min="1" v-model.number="addMeetingForm.capacity" @input="handleInputInt" />
      </el-form-item>
      <el-form-item label="会议室设备：">
        <el-input v-model="addMeetingForm.equipment" />
      </el-form-item>
      <el-form-item label="会议室状态：">
        <el-radio-group v-model="addMeetingForm.status">
          <el-radio :value="0" size="large">禁用</el-radio>
          <el-radio :value="1" size="large">空闲</el-radio>
        </el-radio-group>
        <!-- <el-select v-model="addMeetingForm.status" placeholder="请选择会议室初始状态">
          <el-option v-for="item in meetingStates" :key="item.type" :label="item.label" :value="item.type"/>
        </el-select> -->
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="card-footer">
        <el-button @click="clearAddInfo()">重置</el-button>
        <el-button type="primary" @click="addMeetingInfo()">
          确认
        </el-button>
      </div>
    </template>
  </el-card>
  <div class="room-table">
    <div class="room-table-th">
      <div class="th-title t-name">会议室名</div>
      <div class="th-title t-location">位置</div>
      <div class="th-title t-people">容量</div>
      <div class="th-title t-equip">设备</div>
      <div class="th-title t-status">当前状态</div>
      <div class="th-title t-operate">操作</div>
    </div>
    <div class="room-table-main my-main-scrollbar">
      <div class="room-table-tr" v-for="item in useMeetingStatus.centerRoomName">
        <div class="room-tr-cell t-name">
          <el-popover
              placement="bottom"
              :disabled="item.roomName.length < 20"
              :width="130"
              trigger="hover"
              :content="item.roomName"
          >
              <template #reference>
                  {{ item.roomName }}
              </template>
          </el-popover>
        </div>
        <div class="room-tr-cell t-location">
          <el-popover
              placement="bottom"
              :disabled="item.location?.length < 27"
              :width="200"
              trigger="hover"
              :content="item.location"
          >
              <template #reference>
                  {{ item.location }}
              </template>
          </el-popover>
        </div>
        <div class="room-tr-cell t-people">{{ item.capacity }}人</div>
        <div class="room-tr-cell t-equip">{{ item.equipment }}</div>
        <div class="room-tr-cell t-status">{{ item.roomStatusLabel }}</div>
        <div>
          <div class="room-tr-cell t-operate">
            <el-button plain :type="item.status == 0 ? 'primary' : 'warning'" @click="handleBanRoom(item)">{{ editRoomStatus(item.status) }}</el-button>
            <el-button type="danger" @click="handleDeleteRoom(item)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
    import { ref, onMounted, computed } from 'vue'
    import { ElMessage, ElMessageBox } from 'element-plus';

    import { meetingStatus } from '@/stores/meeting-status'
    import { getMeetingBanData, addRoomData, deleteRoomDate } from '@/request/api/manage'
      
    let userInfo = ref<any>({});  // 获取用户信息 用于传后端参数
    const useMeetingStatus = meetingStatus();
  
    // 会议室状态 0-暂停使用 1-空闲 2-使用中
    // 定义 新增会议室信息
    let addMeetingForm = ref<any>({
      roomName: '',  // 会议室名称
      location: '',   // 会议室位置
      capacity: '',  // 会议室容量
      status: '', // 0-暂停使用(禁用) 1-可使用/空闲(空闲)
      equipment: '',  // 会议室设备
    })

    onMounted(() => {
      userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}');  // 用户信息
      useMeetingStatus.getCenterRoomName();  // 获取所有会议室及状态
    })

/******************************************* 会议室 ***********************************/

    // 操作 禁用显示
    const editRoomStatus = computed(() => (status: any) => {
      if(status === 0) {
        return '启用'
      } else {
        return "禁用"
      }
    })

    /**
     * @description 会议室禁用
     * @param {id} 会议室id
     * @param {status} 会议室状态
     * @param {currentLevel} 当前用户等级
     */

    // 禁用会议室点击事件
    const handleBanRoom = (item: any) => {      
      if (item.status === 0) {
        item.status = 1;
      } else {
        item.status = 0;
      }
      getMeetingBanData({ id: item.id, status: item.status, currentLevel: userInfo.value.level })  // 会议室禁用
        .then(() => {
          useMeetingStatus.getCenterRoomName();
        })
        .catch((err) => {})
    }

   // 点击取消 就将表单数据清空
    const clearAddInfo = () => {
      addMeetingForm.value = {
        roomName: '',  // 会议室名称
        location: '',   // 会议室位置
        capacity: '',  // 会议室容量
        status: '', // 0-暂停使用(禁用) 1-可使用/空闲(空闲)
        equipment: '',  // 会议室设备
      }
    }
    /**
     * @description 添加会议室
    */
    const addMeetingInfo = () => {
      const { roomName, location, capacity,status,equipment } = addMeetingForm.value
      addRoomData({ createdBy: userInfo.value.userId, roomName, location, capacity: Number(capacity), status: Number(status), equipment })
      .then(() => {
        ElMessage.success('新增会议室成功')
        useMeetingStatus.getCenterRoomName()
        clearAddInfo()  // 新增会议室后清空表单数据
      })
      .catch(() => {
        ElMessage.warning('取消新增会议室')
      })
    }

    // 容量输入为整数 不可以为小数
    const handleInputInt = (value: any) => {
      // 使用正则表达式来确保输入的是整数
      const regex = /^[0-9]*$/;
      if (!regex.test(value) || parseInt(value, 10) <= 0) {
        // 如果输入的不是大于0的整数，则设置为''
        addMeetingForm.value.capacity = value.replace('');
        ElMessage.warning('请输入大于0的整数');
      }
    }
    
    /**
     * @description 删除会议室
     * @param {currentLevel} 用户等级 0超级管理员 1管理员 2员工
     * @param {id} 会议室id
     * 会议室状态 0-暂停使用 1-空闲 2-使用中
    */ 
    const handleDeleteRoom = (item: any) => {
      if (item.status == 0) {
        ElMessageBox.confirm('确定删除该会议室吗？')
        .then(async() => {
          await deleteRoomDate({ currentLevel: userInfo.value.level, id: item.id })
          ElMessage.success('删除会议室成功')
          useMeetingStatus.getCenterRoomName()
        })
        .catch(() => {
          ElMessage.warning('取消删除会议室')
        })
      } else {
        ElMessage.warning('仅禁用状态可删除')
      }
    }
    
</script>
<style scoped lang="scss">
  // tab-操作会议室
  .el-card {
    width: 29rem;
    margin-right: 20px;
    padding: 0 20px;
    .card-header {
      font-size: 1.1rem;
      font-weight: 400;
      text-align: center;
    }
    .el-form {
      .el-form-item {
        height: 70px;
        flex-direction: column;
        // align-items: center;
        :deep().el-form-item__label {
          justify-content: flex-start;
          // margin-bottom: 5px;
        }
      }
    }
    .card-footer {
      text-align: center;
    }
  }
  .room-table {
    height: 637px;
    // flex: 1;
    border: 2px solid rgba(18, 115, 219, 0.8);
    border-radius: 15px;
    padding: 10px 18px;
    // 表内每个单元格共同样式
    .t-name {
      width: 15rem !important;
    }
    .t-location {
      width: 18rem !important;
    }
    .t-people {
      width: 7rem !important;
    }
    .t-equip {
      width: 7rem !important;
    }
    .t-status {
      width: 8rem !important;
    }
    .t-operate {
      width: 10rem !important;
    }

    .room-table-th {
      display: flex;
      text-align: center;
      padding-bottom: 0.375rem;
      font-size: 1.1rem;
      .th-title {
        font-weight: 400;
        color: #3A3A3A;
      }
    }
    .room-table-main {
      max-height: 36rem;
      overflow-y: auto;
      &::-webkit-scrollbar {
        display: none;
      }
      .room-table-tr {
        display: flex;
        align-items: center;
        text-align: center;
        color: #666;
        background: #FFF;
        border-radius: 15px;
        margin: 10px 0;
        padding: 11px 0;
        .room-tr-cell {
          text-wrap: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          padding: 0 10px;
          &:last-child {
            cursor: pointer;
          }
        }
      }
    }
  }
</style>