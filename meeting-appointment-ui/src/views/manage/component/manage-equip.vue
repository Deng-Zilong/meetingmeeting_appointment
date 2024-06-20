<template>  
  <!-- 新增设备 弹窗 -->
  <el-dialog v-model="dialogAddVisible" width="450" top="25vh" :title="isNew ? '新增设备' : '修改设备'">
    <el-form ref="ruleFormRef" :model="addEquipForm" :rules="rules">
      <el-form-item label="设备名称：" prop="deviceName">
        <el-input v-model.trim="addEquipForm.deviceName" clearable :maxlength="15" />
      </el-form-item>
      <el-form-item label="所在会议室：" v-if="isNew" prop="roomId">
        <el-select v-model="addEquipForm.roomId" filterable multiple clearable placement="right-start" placeholder="请选择所在会议室">
          <el-option v-for="item in roomOptions" :key="item.id" :label="item.value" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="所在会议室：" v-if="!isNew" prop="oneRoomId">
        <el-select v-model="addEquipForm.oneRoomId" filterable clearable placement="right-start" placeholder="请选择所在会议室">
          <el-option v-for="item in roomOptions" :key="item.id" :label="item.value" :value="item.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="card-footer">
        <el-button @click="cancelForm()">取消</el-button>
        <el-button type="primary" @click="submitTab(isNew, ruleFormRef)">
          {{ isNew ? "确认" : "修改" }}
        </el-button>
      </div>
    </template>
  </el-dialog>

  <div class="equip-container">
    <div class="equip-top">
      <el-button type="primary" @click="handleAddDevice">新增设备</el-button>
      <el-input v-model="equipValue" clearable placeholder="请搜索设备" style="width: 240px" @input="handleChangeEquip" />
      <el-select v-model="roomValue" filterable clearable placeholder="会议室" style="width: 240px" @change="handleChangeRoom">
        <el-option v-for="item in roomOptions" :key="item.id" :label="item.value" :value="item.id" />
      </el-select>
      <el-select v-model="stateValue" filterable clearable placeholder="状态" style="width: 240px" @change="handleChangeStatus">
        <el-option v-for="item in stateOptions" :key="item.id" :label="item.value" :value="item.id" />
      </el-select>
    </div>
    <div class="equip-main">
      <div class="room-table">
        <div class="room-table-th">
          <div class="th-title t-name">设备名称</div>
          <div class="th-title t-roomName">所在会议室</div>
          <div class="th-title t-extend">报损次数</div>
          <div class="th-title t-status">当前状态</div>
          <div class="th-title t-operate">操作</div>
        </div>
        <div class="room-table-main">
          <div class="room-table-tr" v-for="item in equipList">
            <div class="room-tr-cell t-name" @click="handleEditDevice(item)">
              <el-popover placement="bottom" :disabled="item.deviceName.length < 20" :width="130" trigger="hover"
                :content="item.deviceName">
                <template #reference>
                  {{ item.deviceName }}
                </template>
              </el-popover>
            </div>
            <div class="room-tr-cell t-roomName" @click="handleEditDevice(item)">
              <el-popover placement="bottom" :disabled="item.roomName?.length < 20" :width="200" trigger="hover"
                :content="item.roomName">
                <template #reference>
                  {{ item.roomName }}
                </template>
              </el-popover>
            </div>
            <div class="room-tr-cell t-extend" :class="changeColor(item.extent)">
              <el-tag size="large" :type="changeColor(item.extent)">{{ item.extent }}次</el-tag>
            </div>
            <div class="room-tr-cell t-status">{{ showStatus(item.status) }}</div>
            <div class="room-tr-cell t-operate">
              <el-button plain :type="item.status == 0 ? 'primary' : 'warning'" @click="handleBanEquip(item)">
                {{editStatus(item.status) }}
              </el-button>
              <el-button type="primary" @click="handleDetail(item)">详情</el-button>
              <el-button type="danger" @click="handleDeleteRoom(item)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
      <!-- 分页 -->
      <el-row type="flex" justify="center" style="margin-top: 20px;">
        <el-pagination @current-change="handleCurrentChange" :page-size="size" :current-page.sync="page"
          layout="total, prev, pager, next, jumper" :total="total">
        </el-pagination>
      </el-row>
    </div>

    <!-- 详情 弹窗 -->
    <el-dialog v-model="dialogTableVisible" :title="detailTitle" width="600" top="25vh">
      <el-table :data="gridData">
        <el-table-column property="userName" label="报损人" width="120" />
        <el-table-column property="breakInfo" label="报损信息" width="260" />
        <el-table-column property="breakTime" label="报损时间" />
      </el-table>
    </el-dialog>

  </div>
</template>

<script lang="ts" setup>
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue'

import { meetingStatus } from '@/stores/meeting-status'
import { getDeviceData, addDeviceData, editDeviceData, delDeviceData, setStatusData, getInfoData } from '@/request/api/manage'

const useMeetingStatus = meetingStatus();  // 获取所有会议室信息
const dialogAddVisible = ref(false)

// 搜索
const roomValue = ref('')  // 选择会议室
const roomOptions = useMeetingStatus.centerRoomName.map((item: any) => {
  const id = item.id
  const value = item.roomName
  return { id, value }
})

const equipValue = ref('')  // 输入设备
const stateValue = ref()  // 选择设备状态
const stateOptions = [  // 可选设备状态值
  {
    id: 0,
    value: '损坏',
  },
  {
    id: 1,
    value: '可用',
  }
]

// 分页
let size = ref(10); // 默认限制条数 10
let page = ref(1); // 默认页数 1
let total = ref(0); // 总条数
/**
 * @description 分页
 * @param value 当前页
 */
const handleCurrentChange = (value: any) => {
  page.value = value;
  getAllDevice();
}

const userInfo = ref<any>({})  // 用户信息

onMounted(() => {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')  // 用户信息
  useMeetingStatus.getCenterRoomName();  // 获取所有会议室及状态
  getAllDevice()  // 查询设备
})


const equipList = ref()  // 列表数据

const changeColor = computed(() => (extent: any) => {
  switch (extent) {
    case 0: return 'success'
    case 1: return 'primary'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'danger'
  }
})

// 展示状态
const showStatus = computed(() => (status: number) => {
  if (status === 0) {  // 0-损坏 1-可用 
    return '损坏'
  } else {
    return "可用"
  }
})

/**
 * @description 查询设备
 * @param {current} 当前页
 * @param {size} 每页显示条数
 * @param {roomId} 会议室id  非必填
 * @param {deviceName} 设备名称 非必填
 * @param {status} 设备状态（0-禁用(损坏) 1-启用(可用)） 非必填
*/
const getAllDevice = async () => {
  const params = {
    current: page.value,
    size: size.value,
    roomId: roomValue.value,
    deviceName: equipValue.value,
    status: stateValue.value
  }
  const res = await getDeviceData(params)
  equipList.value = res.data.records;
  total.value = res.data.total
}

// 搜索三个的 change事件
const handleChangeEquip = (value: any) => {
  equipValue.value = value
  getAllDevice()
}

const handleChangeRoom = () => {
  getAllDevice()
}

const handleChangeStatus = () => {
  getAllDevice()
}

let isNew = ref()  // 判断 新增/修改
// 切换提交事件
const submitTab = (isNew: boolean, formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (!valid) return
    if (isNew) {
      addEquipInfo()
    } else {
      editEquipInfo()
    }
  })
}

const ruleFormRef = ref<FormInstance>(); // 表单实例
const rules = reactive<FormRules>({
    deviceName: [
      { required: true, message: "请输入设备名", trigger: "blur" },
      { pattern: '[^ \x22]+', message: "不可以输入空白信息", trigger: "blur" }
    ],
    roomId: [
      { required: true, message: "请选择会议室", trigger: "change" },
      { type: 'array', min:1, message: "最少选择一个会议室", trigger: "change" }
    ],
    oneRoomId: [
      { required: true, message: "请选择会议室", trigger: "change" }
    ]
})

// 添加设备 表单数据
let addEquipForm = ref<any>({})

// 新增 编辑 取消 都将表单数据清空
const clearAddInfo = () => {
  addEquipForm.value = {}
  dialogAddVisible.value = false
}

// 取消事件 并提示
const cancelForm = () => {
  clearAddInfo()
  ElMessage.warning(`取消${ isNew.value ? '新增' : '修改' }设备`)
}

// 点击 新增设备 按钮
const handleAddDevice = () => {
  dialogAddVisible.value = true
  isNew.value = true
}

/**
 * @description 新增设备
 * @param {roomId} 会议室id
 * @param {deviceName} 设备名称
 * @param {userId} 创建人id
*/
const addEquipInfo = () => {
  addDeviceData({ roomId: addEquipForm.value.roomId, deviceName: addEquipForm.value.deviceName, userId: userInfo.value.userId })
    .then(() => {
      ElMessage.success('新增设备成功')
      getAllDevice();
    })
    .catch(() => {
      getAllDevice();
    })
  clearAddInfo()  // 新增设备后清空表单数据
}

/**
 * @description 将信息传给编辑
*/
const handleEditDevice = (item: any) => {
  isNew.value = false  // 切换为编辑
  dialogAddVisible.value = true
  addEquipForm.value = {
    id: item.id,
    deviceName: item.deviceName,  // 设备名称
    oneRoomId: item.roomId,   // 会议室id
  }
}
/**
 * @description 编辑设备
 * @param {roomId} 会议室id
 * @param {deviceName} 设备名称
 * @param {userId} 创建人id
 * @param {id} 设备id
*/
const editEquipInfo = () => {
  const { oneRoomId, deviceName, id } = addEquipForm.value
  editDeviceData({ roomId: oneRoomId, deviceName, userId: userInfo.value.userId, id })
    .then(() => {
      ElMessage.success('修改设备成功')
      getAllDevice();
    })
    .catch(() => { })
  clearAddInfo()  // 修改设备后清空表单数据
}


/*******************************************************************************************/
// 详情
const dialogTableVisible = ref(false)  // 详情弹窗 是否显示
const detailTitle = ref<any>()  // 详情弹窗 标题
const gridData = ref<any>()  // 详情弹窗 列表展示

/**
 * @description 详情
 * @param {deviceId} 设备id
*/
const handleDetail = async (item: any) => {
  dialogTableVisible.value = true
  detailTitle.value = '所在会议室：' + item.roomName
  const res = (await getInfoData({ deviceId: item.id })).data
  gridData.value = res.map((item: any) => {
    return {
      userName: item.userName,
      breakInfo: item.info,
      breakTime: item.gmtCreate
    }
  })
}

// 操作 禁用显示 // 0-禁用 1-修复 
const editStatus = computed(() => (status: any) => {
  if (status === 0) {
    return '修复'
  } else {
    return "禁用"
  }
})

/**
 * @description 修复设备（0-禁用 1-启用(修复)）
 * @param {id} 设备id
 */

// 禁用设备点击事件
const handleBanEquip = async (item: any) => {
  await setStatusData({ id: item.id })
  getAllDevice()
}

/**
 * @description 删除设备
 * @param {id} 设备id
*/
const handleDeleteRoom = (item: any) => {
  ElMessageBox.confirm('确认删除该设备吗？')
    .then(async() => {
      await delDeviceData({ id: item.id })
      ElMessage.success('删除设备成功')
      getAllDevice();
    })
    .catch(() => {
      ElMessage.warning('取消删除设备')
    })
}
</script>

<style scoped lang="scss">
.equip-container {
  display: flex;
  flex-direction: column;

  .equip-top {
    margin-bottom: 10px;
    .el-button {
      margin-right: 30px;
    }
  }

  .equip-main {
    .room-table {
      height: 33.9375rem;
      border: 2px solid rgba(18, 115, 219, 0.8);
      border-radius: .9375rem;
      padding: 10px 18px;

      // 表头与每行的 共同样式 设置宽
      .th-title, .room-tr-cell {
        width: 20%;
      }
      // 表内每个单元格共同样式
      // .t-name {
      //   width: 15rem !important;
      // }

      // .t-roomName {
      //   width: 18rem !important;
      // }

      // .t-extend {
      //   width: 7rem !important;
      // }

      // .t-status {
      //   width: 8rem !important;
      // }

      // .t-operate {
      //   width: 17rem !important;
      // }

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
        max-height: 30rem;
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

          .t-name, .t-roomName {
            cursor: pointer;

            &:hover {
              color: #1890ff;
            }
          }

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
  }
}
</style>