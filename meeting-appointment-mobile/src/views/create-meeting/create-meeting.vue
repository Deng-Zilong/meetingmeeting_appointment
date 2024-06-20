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
    <div id="container" class="create-meeting">
        <van-sticky>
            <van-nav-bar title="创建会议" left-text="返回" left-arrow @click-left="goBack" />
        </van-sticky>
        <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="80px">
            <el-form-item label="会议主题" prop="title">
                <el-input v-model="formData.title" placeholder="请输入" />
            </el-form-item>
            <el-form-item label="参会人" prop="meetingPeople">
                <!-- <el-popover placement="bottom" trigger="hover" width="270">
                    <template #reference>
                        <el-input class="meeting-people" v-model="formData.meetingPeople" readonly placeholder="添加参会人"
                            @click="handleAddPerson" :prefix-icon="Plus"/>
                    </template>
                    <div>
                        <p class="prompt" @click="handlePromptPerson">{{ popoverObj.meetingPeople }}</p>
                    </div>
                </el-popover> -->
                <!-- <el-dropdown @command="handlePromptPerson">
                    <span class="el-dropdown-link">
                        <el-input class="meeting-people" v-model="formData.meetingPeople" readonly :prefix-icon="Plus"
                    placeholder="添加参会人" @click="handleAddPerson" /> <span> 最近</span>
                    </span>
                    <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="0">{{ popoverObj.meetingPeople }}</el-dropdown-item>
                    </el-dropdown-menu>
                    </template>
                </el-dropdown> -->
                <div class="meeting-people-row">
                    <el-input class="meeting-people" v-model="formData.meetingPeople" readonly :prefix-icon="Plus"
                        placeholder="添加参会人" @click="handleAddPerson" /> <van-button class="lately" plain type="primary" size="small" @click="handlePromptPerson">最近</van-button>
                </div>
            </el-form-item>
            <el-form-item label="开始时间" prop="startTime">
                <el-time-select v-model="formData.startTime" :start="timeStart" :min-time="minStartTime" step="00:15"
                    :end="timeEnd" placeholder="请选择" @change="handleChangeStartTime" />
            </el-form-item>
            <el-form-item label="结束时间" prop="endTime">
                <el-time-select v-model="formData.endTime" :min-time="minEndTime || timeStart"
                    :start="minEndTime || timeStart" step="00:15" :end="timeEnd" placeholder="请选择"
                    @change="handleChangeEndTime" />
            </el-form-item>
            <el-form-item label="日期" prop="date" class="row-date">
                <el-date-picker v-model="formData.date" type="date" class="date" :disabled-date="disabledDate"
                    placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="可选地点" prop="meetingRoomId">
                <el-select v-model.string="formData.meetingRoomId" placeholder="请选择" @change="handleChangeRoom">
                    <el-option v-for="item in roomArr" :key="item.id" :label="item.roomName" :value="String(item.id)" />
                </el-select>
            </el-form-item>
            <div class="fourth-row">
                <el-form-item label="群组名称" prop="groupName">
                    <el-input v-model="formData.groupName" placeholder="请输入" />
                </el-form-item>
                <el-form-item label-width="10">
                    <el-checkbox v-model="formData.checked" label="添加为群组" />
                </el-form-item>
            </div>
            <el-form-item label="会议概要" prop="description">
                <el-input type="textarea" :autosize="{ minRows: 4 }" v-model="formData.description" maxlength="100"
                    show-word-limit placeholder="请输入" />
            </el-form-item>

            <div class="last-row">
                <el-button type="primary" @click="submitForm(ruleFormRef)" v-if="isCreate">
                    创建会议
                </el-button>
                <el-button type="primary" @click="submitForm(ruleFormRef)" v-else>
                    修改会议
                </el-button>
            </div>
        </el-form>
        <!-- 添加群组成员弹窗 -->
        <PersonTreeDialog 
            v-model="addPersonForm.visible" 
            :title="addPersonForm.title"
            :type="addPersonForm.type"
            :list="addPersonForm.list" 
            :groupList="addPersonForm.list" 
            :peopleIds="addPersonForm.personIds"
            :groupPersonIds="addPersonForm.personIds"
            @close-dialog="closeAddPersonDialog" 
            @submit-dialog="handleCheckedPerson" />
    </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch, onUnmounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { ComponentSize, FormInstance, FormRules } from 'element-plus'
import { ElMessage, dayjs } from 'element-plus'
import { Plus, ArrowDown  } from '@element-plus/icons-vue'
import PersonTreeDialog from '@/views/group/components/person-tree-dialog.vue'
import { addMeetingGroup, getMeetingGroupList } from '@/request/api/group'
import { addMeetingRecord, availableMeetingRooms, meetingRecordPrompt, updateMeetingRecord } from '@/request/api/meeting-appoint'
import { meetingStatus } from '@/stores/meeting-status'
import { useThrottle } from '@/utils/methods'
import { meetingAppointTime } from '@/utils/type'

const routes = useRoute();
const router = useRouter();
const useMeetingStatus = meetingStatus();
const userInfo = ref<any>(JSON.parse(localStorage.getItem('userInfo') as string) || '');
const currentUserId = ref<string>(userInfo.value.userId);

const currentDate: string = dayjs(new Date()).format('YYYY-MM-DD');  // 当前日期 yy-mm-dd
const currentTime: string = dayjs(new Date()).format('HH:mm'); // 当前时间 HH:mm

const isCreate = ref(true); // 是否是创建会议 true创建 false修改

onMounted(() => {
    // 获取会议相关参数
    const meetingInfo = JSON.parse(sessionStorage.getItem('meetingInfo') as string);

    // 历史记录修改
    if (meetingInfo?.id) {
        // 修改会议
        isCreate.value = false;
        // 解构所需数据
        const { id, meetingRoomId, title, description, meetingRoomName, status, createdBy, adminUserName, startTime, endTime, users, date } = meetingInfo;

        const meetingPeople = Array.from(new Set(users?.map((el: any) => el.userName))).join(','); // 获取参会人名字并去重
        addPersonForm.value.personIds = Array.from(new Set(users.map((el: any) => el.userId))); // 获取参会人id并去重 用于成员树回显
        // 重组 form 表单数据
        formData.value = {
            id: id,             // 会议id
            meetingRoomId: String(meetingRoomId),      // 会议室id
            title,              // 会议名称
            description,        // 会议描述
            startTime: String(dayjs(startTime as string).format('HH:mm')), // 会议开始时间
            endTime: String(dayjs(endTime as string).format('HH:mm')),     // 会议结束时间
            meetingRoomName,     // 会议室名称
            meetingPeople,       // 参会人名字
            status,              // 会议状态
            createdBy,           // 创建人id
            adminUserName,       // 创建人名字
            users,               // 参会人列表
            date,                // 会议日期
            groupName: '',       // 群组名称
        }
    }

    if (!meetingInfo?.id) {
        handleMeetingRecordPrompt(); // 获取最近1次创建人创建会议的信息
        // 预约会议室 处理传参数据
        if ((meetingInfo?.meetingRoomId || meetingInfo?.startTime)) {
            const { date, meetingRoomId, startTime, endTime } = meetingInfo;

            formData.value.meetingRoomId = meetingRoomId ? meetingRoomId : ''; // 会议室id
            formData.value.startTime = startTime ? startTime as string : '';   // 开始时间
            formData.value.endTime = endTime ? endTime as string : '';   // 结束时间
            formData.value.date = date ? date : currentDate;  // 日期
        }
    }

    // 获取当前开始时间和结束时间的可选时间段
    minStartTime.value = currentTime;
    minEndTime.value = formData.value.startTime;

    // 获取群组列表
    getGroupList();
    handleAvailableMeetingRooms(formData.value.startTime, formData.value.endTime); // 获取可用会议室
})

/**
 * @description 返回上一页
 */
const goBack = () => {
    window.history.go(-1)
}

// 禁止选择今日之前的日期
const disabledDate = (date: any) => {
    return date.getTime() < Date.now() - 8.64e7
}

let timeStart = ref('8:00'); // 开始时间
let timeEnd = ref('22:30'); // 结束时间
let minStartTime = ref('8:00'); // 开始最小可选时间
let minEndTime = ref('8:00'); // 结束最小可选时间
const seconds = ('00'); // 获取当前时间的秒

const formSize = ref<ComponentSize>('large'); // 表单尺寸
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
// 添加参会人员弹窗表单数据
let addPersonForm = ref<any>({
    visible: false,        // 弹窗开关
    type: 3,               // 弹窗类型 1: 创建群组 2: 修改群组人员 3: 添加参会人员 4: 添加管理员
    title: '添加参会人员',  // 弹窗标题
    list: [],              // 弹窗数据
    personIds: [],         // 选中的人员ids，用于节点树回显
})
/**
 * @description 添加参会人员
 */
const handleAddPerson = () => {
    addPersonForm.value.type = 3;
    addPersonForm.value.visible = true;
}

/**
 * @description 获取群组列表
 */
const getGroupList = () => {
    getMeetingGroupList({ userId: userInfo.value.userId, pageNum: 1, pageSize: 1000 })
        .then(res => {
            addPersonForm.value.list = res.data.map((item: any) => {
                item.userName = item.groupName;
                return item
            });
        })
        .catch((err) => { })
}


/**
 * @description 获取添加参会人员
 * @param type 1: 创建群组 2: 修改群组人员 3: 添加参会人员 4: 添加管理员
 */
const handleCheckedPerson = (type: number, tab: number, userIds: any, userNames: any, userInfo: any) => {
    // 用逗号拼接选中人员的名字
    formData.value.meetingPeople = userNames.join(',');
    // 选中人员的id
    addPersonForm.value.personIds = userIds;
    formData.value.users = userInfo;
    closeAddPersonDialog();
}

/**
 * @description 关闭添加人员弹窗
 */
const closeAddPersonDialog = () => {
    addPersonForm.value.visible = false;
}

/**
 * @description 根据开始时间获取可用会议室
 * @param value 选中的开始时间
 */
const handleChangeStartTime = (value: any) => {
    minEndTime.value = value;
    handleAvailableMeetingRooms(value, formData.value.endTime);
}

/**
 * @description 根据结束时间获取可用会议室
 * @param value 选中的结束时间
 */
const handleChangeEndTime = (value: any) => {
    // if (!formData.value.startTime) {
    //     formData.value.endTime = '';
    //     return ElMessage.warning('请先选择开始时间');
    // }
    // if (formData.value.startTime >= value) {
    //     formData.value.endTime = '';
    //     return ElMessage.warning('结束时间不能小于或等于开始时间');
    // }
    handleAvailableMeetingRooms(formData.value.startTime, value);
}

const roomArr = ref<any>(useMeetingStatus.centerRoomName); // 会议室数组
/**
 * @description 根据开始时间和结束时间获取可用会议室
 * @param startTime 开始时间
 * @param endTime 结束时间
 */
const handleAvailableMeetingRooms = useThrottle((start: string, end: string) => {
    if (!start || !end) return;
    const date = dayjs(formData.value.date).format('YYYY-MM-DD');
    const startTime: string = date + ` ${start}:${seconds}`;
    const endTime: string = date + ` ${end}:${seconds}`;
    availableMeetingRooms({
        startTime,
        endTime,
    })
        .then(res => {
            roomArr.value = res.data;
        })
        .catch(err => { })
}, 500)

/**
 * @description 获取选中会议室的id
 * @param value 会议室id
 */
const handleChangeRoom = (value: any) => {
    formData.value.meetingRoomId = value;
}

// 监听日期变化
watch(() => formData.value.date, (newValue) => {
    // 如果选中的日期大于今天的日期 则默认最小可选时间为8:00
    handleAvailableMeetingRooms(formData.value.startTime, formData.value.endTime);
    if (new Date(newValue).getTime() > new Date().getTime()) {
        timeStart.value = '8:00';
        return minStartTime.value = '7:59';
    }
    
    timeStart.value = meetingAppointTime.value.find((item: any) => item > currentTime) ?? ''; // 设置时间段的开始时间
    minStartTime.value = currentTime;// 重置开始时间的最小可选时间

    // 清空开始和结束时间 (注释原因：影响了历史记录和今日会议记录修改传参的开始时间和结束时间)
    // formData.value.startTime = '';
    // formData.value.endTime = '';
}, {immediate: true, deep: true})

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
const rules = reactive<FormRules<typeof formData>>({
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
        { trigger: 'blur', validator: validateGroupName }
    ]
})
/**
 * @description 提交表单
 * @param formEl 表单实例
 */
const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (!valid) return;
        if (!formData.value.createdBy && !formData.value.adminUserName) {
            formData.value.createdBy = userInfo.value.userId;
            formData.value.adminUserName = userInfo.value.userName;
        }
        // 解构所需数据
        const { createdBy, adminUserName, meetingRoomId, title, status, description, startTime, endTime, users, groupName, checked } = formData.value;
        // 重组参数
        const params = ref<any>({
            createdBy,
            meetingRoomId,
            title,
            status,
            description,
            startTime: dayjs(formData.value.date).format('YYYY-MM-DD') + ` ${startTime}:${seconds}`,
            endTime: dayjs(formData.value.date).format('YYYY-MM-DD') + ` ${endTime}:${seconds}`,
            users,
            groupName,
        })
        // 判断开时间是否大于结束时间
        if (startTime >= endTime) {
            return ElMessage.error("开始时间不能大于或等于结束时间！")
        }
        // 是否添加为群组
        if (checked) {
            addMeetingGroup({
                groupName,
                createdBy,
                userName: adminUserName,
                users: users,
            })
                .then(res => {
                    ElMessage.success("添加群组成功！")
                })
                .catch(err => { })
        }
        // 创建会议
        if (isCreate.value) {
            addMeetingRecord(params.value)
                .then((res) => {
                    ElMessage.success('会议创建成功！');
                    setTimeout(() => {
                        router.push('/home');
                    }, 1000)
                }).catch((err) => { })
        } else {
            // 修改会议
            params.value.id = formData.value.id;
            updateMeetingRecord(params.value)
                .then((res) => {
                    ElMessage.success('会议修改成功！');
                    setTimeout(() => {
                        router.push('/home');
                    }, 1000)
                }).catch((err) => { })
        }
    })
}
const popoverObj = ref<any>({
    users: [],
    meetingPeople: '',
})
/**
 * @description 提示最近1次创建人创建会议的信息
 */
const handleMeetingRecordPrompt = () => {
    meetingRecordPrompt({ userId: userInfo.value.userId })
        .then((res: any) => {
            const { users } = res.data;
            popoverObj.value.users = users;
            popoverObj.value.meetingPeople = Array.from(new Set(users.map((el: any) => el.userName))).join(",");
        })
        .catch((err: any) => { })
}
const handlePromptPerson = () => {
    const { users, meetingPeople } = popoverObj.value;
    formData.value.users = users;
    formData.value.meetingPeople = meetingPeople;
    addPersonForm.value.personIds = Array.from(new Set(users.map((el: any) => el.userId))); // 获取参会人id并去重 用于成员树回显
}
// 离开页面
onBeforeUnmount(() => {
    // 清除sessionStorage
    sessionStorage.clear();
})

</script>

<style lang="scss" scoped>
.create-meeting {
    height: 100%;
    width: 100%;
    margin: 0 auto;
    background: #fff;

    :deep(.el-form) {
        width: 100%;
        padding: 0 10px;

        .row-date {
            .el-date-editor.el-input {
                width: 100%;
            }
        }

        .fourth-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .last-row {
            text-align: right;
        }

        .el-form-item {
            margin-bottom: 35px;
            .meeting-people-row {
                display: flex;
                justify-content: space-between;
                align-items: center;
                .lately {
                    width: 60px;
                    margin-left: 10px;
                }
            }
        }
    }
}
</style>