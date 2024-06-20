<template>
  <!-- <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field v-model="title" name="会议主题" label="会议主题" placeholder="会议主题"
        :rules="[{ required: true, message: '请填写会议主题' }]" />
      <van-field v-model="content" name="会议概要" label="会议概要" placeholder="会议概要"
        :rules="[{ required: true, message: '请填写会议概要' }]" />
      <van-field v-model="time" name="会议概要" label="会议概要" placeholder="会议概要"
        :rules="[{ required: true, message: '请填写会议概要' }]" />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form> -->
  <el-form ref="ruleFormRef" :model="formData" :rules="rules">
    <div class="appoint-row">
      <el-form-item label="会议主题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="参会人" prop="meetingPeople">
        <el-popover placement="bottom" trigger="hover" width="270">
          <template #reference>
            <el-input class="meeting-people" v-model="formData.meetingPeople" readonly
              placeholder="添加参会人" @click="handleAddPerson" />
          </template>
          <div>
            <p class="prompt" @click="handlePromptPerson">{{ popoverObj.meetingPeople }}</p>
          </div>
        </el-popover>
      </el-form-item>
    </div>
    <div class="appoint-row">
      <el-form-item label="开始时间" prop="startTime">
        <el-time-select v-model="formData.startTime" :start="timeStart" :min-time="minStartTime" step="00:15"
          :end="timeEnd" placeholder="请选择" @change="handleChangeStartTime" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-time-select v-model="formData.endTime" :min-time="minEndTime || timeStart" :start="minEndTime || timeStart"
          step="00:15" :end="timeEnd" placeholder="请选择" @change="handleChangeEndTime" />
      </el-form-item>
    </div>
    <div class="appoint-row">
      <el-form-item label="日期" prop="date" class="row-date">
        <el-date-picker v-model="formData.date" type="date" class="date" :disabled-date="disabledDate"
          placeholder="选择日期" />
      </el-form-item>
      <el-form-item label="当前可选地点" prop="meetingRoomId">
        <el-select v-model.string="formData.meetingRoomId" placeholder="请选择" @change="handleChangeRoom">
          <el-option v-for="item in roomArr" :key="item.id" :label="item.roomName" :value="String(item.id)" />
        </el-select>
      </el-form-item>
    </div>
    <div class="fourth-row">
      <el-form-item>
        <el-checkbox v-model="formData.checked" label="添加为群组" />
      </el-form-item>
      <el-form-item label="群组名称" prop="groupName">
        <el-input v-model="formData.groupName" placeholder="请输入" />
      </el-form-item>
    </div>
    <div class="fifth-row">
      <span>会议概要</span>
      <el-input v-model="formData.description" maxlength="100" show-word-limit type="text" placeholder="请输入" />
    </div>

    <div class="last-row">
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)" v-if="isCreate">
          创建会议
        </el-button>
        <el-button type="primary" @click="submitForm(ruleFormRef)" v-else>
          修改会议
        </el-button>
      </el-form-item>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { dayjs, type FormInstance, type FormRules } from 'element-plus';
import { ref } from 'vue';

const title = ref('');
const content = ref('');
const time = ref('');
const onSubmit = (values: any) => {
  console.log('submit', values);
};

// 禁止选择今日之前的日期
const disabledDate = (date: any) => {
    return date.getTime() < Date.now() - 8.64e7
}

let timeStart = ref('8:00'); // 开始时间
let timeEnd = ref('22:30'); // 结束时间
let minStartTime = ref('8:00'); // 开始最小可选时间
let minEndTime = ref('8:00'); // 结束最小可选时间
const seconds = ('00'); // 获取当前时间的秒
const isCreate = ref(true); // 是否是创建会议 true创建 false修改
const currentDate: string = dayjs(new Date()).format('YYYY-MM-DD');  // 当前日期 yy-mm-dd
const roomArr = ref<any>()

const ruleFormRef = ref<FormInstance>(); // 表单实例
const formData = ref<any>({
    // id: '',
    meetingRoomId: '',   // 会议室id
    title: '',           // 会议主题
    description: '',     // 会议描述
    startTime: '',       // 会议开始时间
    endTime: '',         // 会议结束时间
    meetingRoomName: '', // 会议室名称
    status: 0,           // 会议室状态 默认为0
    createdBy: '',       // 创建人id
    adminUserName: '',   // 创建人姓名
    users: [],           // 参会人员
    date: currentDate,   // 日期 yy-mm-dd
    checked: false,      // 是否添加为群组
    groupName: '',       // 群组名称
})
const popoverObj = ref<any>({
    users: [],
    meetingPeople: '',
})
// 验证群组名称
const validateGroupName = (rule: any, value: any, callback: any) => {
    if (formData.value.checked) {
        if (!formData.value.groupName) {
            callback(new Error('请填写群组名称'))
        } else {
            callback()
        }
    }
    callback();
}
const rules = ref<FormRules<typeof formData>>({
    title: [
        { required: true, message: '请输入会议主题', trigger: 'blur' }
    ],
    meetingPeople: [
        { required: true, message: '请选择参会人', trigger: 'change' }
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
    meetingRoomId: [
        { required: true, message: '请选择会议室', trigger: 'change' }
    ],
    groupName: [
        {trigger: 'blur', validator: validateGroupName }
    ]
})

const handleAddPerson= () => { }
const handlePromptPerson= () => { }
const handleChangeStartTime= () => { }
const handleChangeEndTime= () => { }
const handleChangeRoom= () => { }

const submitForm = (formEl: FormInstance | undefined) => {
  
}
</script>

<style lang="scss"></style>