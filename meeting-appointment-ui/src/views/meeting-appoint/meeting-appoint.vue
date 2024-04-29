<template>
  <!-- 会议室预约 -->
  <div class="container appoint-container">
    <header>
      <el-divider direction="vertical" />会议室预约
    </header>
    <main>
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules">
        <div class="appoint-row">
          <el-form-item label="会议主题" prop="meetingTheme">
            <el-input v-model="meetingTheme" placeholder="请输入" />
          </el-form-item>
          <el-form-item label="参会人" prop="meetingPeople">
            <el-input v-model="meetingPeople" />
          </el-form-item>
        </div>
        <div class="appoint-row">
          <el-form-item label="开始时间" prop="startTime">
            <el-time-select v-model="startTime" :start="timeStart" step="00:15" :end="timeEnd" placeholder="请选择" />
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-time-select v-model="endTime" :min-time="startTime" :start="timeStart" step="00:15" :end="timeEnd"
              placeholder="请选择" />
          </el-form-item>
        </div>
        <div class="appoint-row">
          <el-form-item label="日期" prop="date" class="row-date">
            <el-date-picker v-model="date" type="date" class="date" :disabled-date="disabledDate" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="当前可选地点" prop="meetingRoom">
            <el-select v-model="meetingRoom" placeholder="请选择">
              <el-option v-for="item in roomArr" :key="item.id" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <div class="fourth-row">
          <el-form-item>
            <el-checkbox v-model="checked" label="添加为群组" />
          </el-form-item>
          <el-form-item label="群组名称" prop="groupName">
            <el-input v-model="groupName" placeholder="请输入" />
          </el-form-item>
        </div>
        <div class="fifth-row">
          <span>会议概要</span>
          <el-input v-model="meetingSummary" maxlength="100" show-word-limit type="text" placeholder="请输入" />
        </div>

        <div class="last-row">
          <el-form-item>
            <el-button type="primary" @click="submitForm(ruleFormRef)">
              创建会议
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </main>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { ComponentSize, FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const meetingTheme = ref('')
const meetingPeople = ref('')
const startTime = ref('')
const timeStart = ref('08:00')
const timeEnd = ref('22:30')
const endTime = ref('')
const checked = ref(false)
const groupName = ref('')
const meetingSummary = ref('')

const date = ref(new Date())
// 禁止选择今日之前的日期
const disabledDate = (date: any) => {
  return date.getTime() < Date.now() - 8.64e7
}
const meetingRoom = ref('')
const roomArr = reactive([
  {
    id: 1,
    label: '会议室A',
    value: '会议室A'
  },
  {
    id: 2,
    label: '会议室B',
    value: '会议室B'
  },
  {
    id: 3,
    label: '会议室C',
    value: '会议室C'
  }
])

// 表单验证
const formSize = ref<ComponentSize>('large')
const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive({
  meetingTheme: '',
  meetingPeople: '',
  startTime: '',
  endTime: '',
  date: '',
  meetingRoom: ''
})
const rules = reactive<FormRules<typeof ruleForm>>({
  meetingTheme: [
    { required: true, message: '请输入会议主题', trigger: 'blur' }
  ],
  meetingPeople: [
    { required: true, message: '请选择参会人', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  date: [
    { required: true, type: 'date', message: '请选择日期', trigger: 'change' }
  ],
  meetingRoom: [
    { required: true, message: '请选择会议室', trigger: 'change' }
  ]
})

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      // router.push('/home')
      ElMessage.success('登陆成功')
    } else {
      return false
    }
  })
}
</script>

<style lang="scss" scoped>
.appoint-container {
  padding: 2.5rem 3.5rem;

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
    justify-content: center;
    padding: 1.6rem;

    .el-form {
      width: 38rem;

      .appoint-row {
        display: flex;
        justify-content: space-between;

        .el-form-item {
          flex-direction: column;
          align-items: flex-start;
          /* 内部label与input样式 在main.css中 */
        }
      }

      .fourth-row {
        display: flex;
        justify-content: space-between;
        margin-top: 10px;

        /* 多选样式 在main.css中 */

        // 群组名称input宽度
        .el-form-item {
          .el-input {
            width: 23rem;
          }
        }
      }

      .fifth-row {
        .el-input {
          height: 5.75rem;
          margin-top: 8px;
        }
      }

      .last-row {
        display: flex;
        justify-content: flex-end;

        .el-button {
          margin-top: 20px;
        }
      }
    }
  }

}
</style>