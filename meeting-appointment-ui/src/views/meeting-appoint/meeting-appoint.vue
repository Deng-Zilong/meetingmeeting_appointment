<template>
    <!-- 会议室预约 -->
    <div class="container appoint-container">
        <header>
            <div class="header-title">
                <el-divider direction="vertical" />会议室预约
            </div>
            <div class="header-back" @click="goBack">
                <el-icon>
                    <ArrowLeft />
                </el-icon>
                返回
            </div>
        </header>
        <main>
            <el-form ref="ruleFormRef" :model="formData" :rules="rules">
                <div class="appoint-row">
                    <el-form-item label="会议主题" prop="title">
                        <el-input v-model="formData.title" placeholder="请输入" />
                    </el-form-item>
                    <el-form-item label="参会人" prop="meetingPeople">
                        <el-input class="meeting-people" v-model="formData.meetingPeople" readonly :prefix-icon="Plus"
                            placeholder="添加参会人" @click="handleAddPerson" />
                    </el-form-item>
                </div>
                <div class="appoint-row">
                    <el-form-item label="开始时间" prop="startTime">
                        <el-time-select v-model="formData.startTime" :start="timeStart" :min-time="formData.startTime" step="00:15" :end="timeEnd"
                            placeholder="请选择" />
                    </el-form-item>
                    <el-form-item label="结束时间" prop="endTime">
                        <el-time-select 
                            v-model="formData.endTime" 
                            :min-time="formData.startTime" 
                            :start="timeStart"
                            step="00:15" 
                            :end="timeEnd" 
                            placeholder="请选择"
                            @change="handleChangeEndTime"
                            />
                    </el-form-item>
                </div>
                <div class="appoint-row">
                    <el-form-item label="日期" prop="date" class="row-date">
                        <el-date-picker v-model="formData.date" type="date" class="date" :disabled-date="disabledDate"
                            placeholder="选择日期" />
                    </el-form-item>
                    <el-form-item label="当前可选地点" prop="meetingRoom">
                        <el-select v-model="formData.meetingRoomId" placeholder="请选择">
                            <el-option v-for="item in roomArr" :key="item.meetingRoomId" :label="item.roomName" :value="item.meetingRoomId" />
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
                    <el-input v-model="formData.description" maxlength="100" show-word-limit type="text"
                        placeholder="请输入" />
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
        </main>
        <!-- 添加群组成员弹窗 -->
        <personTreeDialog 
            v-model="addPersonForm.visible" 
            :title="addPersonForm.title"
            :type="addPersonForm.type"
            :list="addPersonForm.list" 
            :groupList="addPersonForm.list" 
            :peopleIds="addPersonForm.PeopleIds"
            :groupPersonIds="addPersonForm.PeopleIds"
            @close-dialog="closeAddPersonDialog" 
            @submit-dialog="handleCheckedPerson" />
    </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { ComponentSize, FormInstance, FormRules } from 'element-plus'
import { ElMessage, dayjs } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import personTreeDialog from '@/components/person-tree-dialog.vue'
import { addMeetingGroup, getMeetingGroupList } from '@/request/api/group'
import { addMeetingRecord, availableMeetingRooms, updateMeetingRecord } from '@/request/api/meeting-appoint'

const routes = useRoute();
const userInfo = ref<any>(JSON.parse(localStorage.getItem('userInfo') as string) || '');
const currentUserId = userInfo.value.userId;

const isCreate = ref(true); // 是否是创建会议 true创建 false修改

onMounted(() => {
    if (routes.query?.id) {
        isCreate.value = false;
        const { id, meetingRoomId, title, description, meetingRoomName, status, createdBy, adminUserName } = routes.query;
        const startTime: any = routes.query.startTime;
        const endTime: any = routes.query.endTime;
    
        const users:any = JSON.parse(routes.query.users as string);
        const meetingPeople = Array.from(new Set(users?.map((el: any) => el.userName))).join(',');
        const meetingUserIds = Array.from(new Set(users.map((el: any) => el.userId)));
        
        formData.value = {
            id: id,
            meetingRoomId: Number(meetingRoomId),
            title: title || '',
            description,
            startTime: dayjs(startTime).format('HH:mm'),
            endTime: dayjs(endTime).format('HH:mm'),
            meetingRoomName,
            meetingPeople,
            meetingUserIds,
            status,
            createdBy,
            adminUserName,
            users,
            date: dayjs(new Date()).format('YYYY-MM-DD'),
            groupName: '',
        }
    }
    // 会议室
    if (routes.query?.meetingRoomId || routes.query?.startTime) {
        formData.value.meetingRoomId = routes.query?.meetingRoomId ? Number(routes.query.meetingRoomId) : '';
        formData.value.startTime = routes.query?.startTime ? routes.query?.startTime : dayjs(new Date()).format('HH:mm');
    }
    // 获取群组列表
    getGroupList();
})

/**
 * @description 返回上一页
 */
const goBack = () => {
    window.history.go(-1)
}
/**
 * @description 添加参会人员
 */
const handleAddPerson = () => {
    addPersonForm.value.type = 3;
    addPersonForm.value.visible = true;
}

// 禁止选择今日之前的日期
const disabledDate = (date: any) => {
    return date.getTime() < Date.now() - 8.64e7
}
const timeStart = ref(dayjs(new Date()).format('HH:mm')); // 开始时间
const timeEnd = ref('22:30'); // 结束时间

// 会议室数组
const roomArr = ref<any>([
    {
        meetingRoomId: 1,
        roomName: '广政通宝',
        address: '西南裙一 3 F 一 广政通宝'
    },
    {
        meetingRoomId: 2,
        roomName: 'EN-2F-02 恰谈室',
        address: '西南裙一 3 F 一 EN-2F-02 恰谈室'
    },
    {
        meetingRoomId: 3,
        roomName: 'EN-2F-03 恰谈室',
        address: '西南裙一 3 F 一 EN-2F-03 恰谈室'
    },
    {
        meetingRoomId: 4,
        roomName: 'EN-3F-02 恰谈室',
        address: '西南裙一 3 F 一 EN-3F-02 恰谈室'
    },
    {
        meetingRoomId: 5,
        roomName: 'EN-3F-03 恰谈室',
        address: '西南裙一 3 F 一 EN-3F-03 恰谈室'
    }
])

// 添加参会人员弹窗表单数据
let addPersonForm = ref<any>({
    visible: false,
    type: 3,
    title: '添加参会人员',
    list: [],
    personIds: [],
})

/**
 * @description 获取群组列表
 */
const getGroupList = () => {
    getMeetingGroupList({userId: userInfo.value.userId})
            .then(res => {
                addPersonForm.value.list = res.data.map((item: any) => {
                    item.userName = item.groupName;
                    return item
                });
            })
            .catch((err) => {})
}

/**
 * @description 获取添加参会人员
 * @param type 1: 创建群组 2: 修改群组人员 3: 从群组添加人员 4: 添加管理员
 */
const handleCheckedPerson = (type: number, tab: number, userIds: any, userNames: any, userInfo: any) => {
    formData.value.meetingPeople = userNames.join(',');
    addPersonForm.value.personIds = userIds.join(',');
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
 * @description 根据结束时间获取可用会议室
 * @param value 选中的结束时间
 */
const handleChangeEndTime = (value: any) => {
    availableMeetingRooms({
        startTime: dayjs(formData.value.date).format('YYYY-MM-DD') + ' ' + formData.value.startTime,
        endTime: dayjs(formData.value.date).format('YYYY-MM-DD') + ' ' + value,
    })
    .then(res => {
        roomArr.value = res.data;
    })
    .catch(err => {})
}

// 表单验证
const formSize = ref<ComponentSize>('large')
const ruleFormRef = ref<FormInstance>()
const formData = ref<any>({
    // id: '',
    meetingRoomId: '',   // 会议室id
    title: '',           // 会议主题
    description: '',
    startTime: '',
    endTime: '',
    meetingRoomName: '',
    status: 0,
    createdBy: '',
    adminUserName: '',
    users: [],
    date: dayjs(new Date()).format('YYYY-MM-DD'),
    checked: false,
    groupName: '',
})
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
        { required: true, message: '请选择会议室', trigger: 'blur' }
    ]
})

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (!valid) return;
        if (!formData.value.createdBy && !formData.value.adminUserName) {
            formData.value.createdBy = userInfo.value.userId;
            formData.value.adminUserName = userInfo.value.userName;
        }
        const {createdBy, adminUserName, meetingRoomId, title, status, description,startTime, endTime, users, groupName, checked} = formData.value;
        const params = ref<any>({
            createdBy,
            meetingRoomId,
            title,
            status,
            description,
            startTime: dayjs(new Date()).format('YYYY-MM-DD') + ' ' + startTime + ':00' ,
            endTime: dayjs(new Date()).format('YYYY-MM-DD') + ' ' + endTime + ':00',
            users,
            groupName,
        })
        if (checked) {
            if (!groupName) {
                return ElMessage.warning('请输入群组名称！');
            }
            addMeetingGroup({
            groupName, 
            createdBy, 
            userName: adminUserName, 
            users: users,
        })
            .then(res => {})
            .catch(err => {})
        }
        if (isCreate.value) {
            addMeetingRecord(params.value)
                .then((res) => {
                   ElMessage.success('创建成功！');
                }).catch((err) => {
                    console.log(err)
                })
        } else {
            params.value.id = formData.value.id;
            updateMeetingRecord(params.value)
                .then((res) => {
                   ElMessage.success('创建成功！');
                }).catch((err) => {
                    console.log(err)
                })
        }
        getGroupList();
        // location.reload();
    })
}

</script>

<style lang="scss" scoped>
.appoint-container {
    padding: 2.5rem 3.5rem;

    header {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .header-title {
            font-size: 1.5rem;

            .el-divider {
                height: 3.125rem;
                border: .25rem solid #1273DB;
                border-radius: .25rem;
                margin-right: 1.5rem;
            }
        }

        .header-back {
            display: flex;
            align-items: center;
            font-size: 1.1rem;
            font-weight: 100;

            .el-icon {
                color: #3268DC;
            }
        }
    }

    main {
        display: flex;
        justify-content: center;
        padding: 1.6rem;

        .el-form {
            width: 38rem;

            /* 统一调整预约表单中的label字体大小 */
            ::v-deep(.el-form-item__label) {
                font-size: 1.03rem;
            }

            .appoint-row {
                display: flex;
                justify-content: space-between;

                .el-form-item {
                    flex-direction: column;
                    align-items: flex-start;

                    ::v-deep(.el-form-item__content) {
                        width: 17rem;
                    }
                }

                // 参会人 input内部样式
                .meeting-people {
                    ::v-deep(.el-input__icon) {
                        width: 20px;
                        height: 20px;
                        color: #3268DC;
                        background: #ECF2FF;
                    }

                    ::v-deep(.el-input__inner) {
                        --el-input-placeholder-color: #3268DC;
                    }
                }

                /* 日期选择框 内部宽度(不包含prefix与suffix宽度) */
                .row-date {
                    ::v-deep(.el-input__inner) {
                        width: 14.25rem;
                    }
                }
            }

            .fourth-row {
                display: flex;
                justify-content: space-between;
                margin-top: 10px;

                ::v-deep(.el-checkbox__label) {
                    font-size: 1.03rem;
                    font-weight: bold;
                }

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

                    ::v-deep(.el-input__wrapper) {
                        align-items: normal;
                    }

                    ::v-deep(.el-input__suffix) {
                        align-items: flex-end;
                    }
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