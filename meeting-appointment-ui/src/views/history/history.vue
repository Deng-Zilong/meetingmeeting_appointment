<template>
    <div class="history">
        <div class="theme" > 会议历史记录查看 </div>
        <!-- <div class="theme" >  </div> -->
        <div class="content" v-loading="loading" element-loading-background="rgba(122, 122, 122, 0.1)">
            <div class="title">
                <div>会议主题</div>
                <div>会议室名称</div>
                <div>发起人</div>
                <div>会议时间</div>
                <div>参会人员</div>
                <div>会议状态</div>
                <div>操作</div>
            </div>
            <div class="list-container" >
                <el-timeline ref="timelineRef">
                    <el-timeline-item  placement="top" class="timeline-item" v-for="(value, index) in data" :key="index">
                        <div class="timestamp">
                            <p>{{value.month}}月</p>
                            <p>{{value.day}}</p>
                        </div>
                        <div v-for="(item, key) in value.list" :key="index" class="card-item">
                            <!-- 会议主题 -->
                            <div>
                                <img src="@/assets/img/transmit-icon.png" alt="" class="transmit-icon" @click="transmitMeeting(item)">
                                <el-popover
                                    placement="bottom"
                                    :disabled="item.title.length < 11"
                                    :width="100"
                                    trigger="hover"
                                    :content="item.title"
                                >
                                <template #reference>
                                        <p class="ellipsis" @click="handleMeetingSummary(item)">{{ item.title }}</p>
                                    </template>
                                </el-popover>
                            </div>
                            <!-- 会议室名称 -->
                            <div>
                                <el-popover
                                placement="bottom"
                                :disabled="item.meetingRoomName?.length < 16"
                                :width="150"
                                trigger="hover"
                                :content="item.meetingRoomName"
                                >
                                <template #reference>
                                        <p class="ellipsis">{{ item.meetingRoomName }}</p>
                                    </template>
                                </el-popover>
                            </div>
                            <!-- 发起人 -->
                            <div>{{ item.adminUserName }}</div>
                            <!-- 会议时间 -->
                            <div>{{ item.time }}</div>
                            <!-- 参会人员 -->
                            <div>
                                <el-popover
                                    placement="bottom"
                                    :disabled="item.attendees.length < 18"
                                    :width="300"
                                    trigger="hover"
                                    :content="item.attendees"
                                >
                                    <template #reference>
                                        <p class="ellipsis">{{ item.attendees }}</p>
                                    </template>
                                </el-popover>
                            </div>
                            <!-- 会议状态 -->
                            <div>{{ item.stateValue }}</div>
                            <!-- 操作 -->
                            <div>
                                <p v-show="item.status == 0 && item.createdBy == currentUserId">
                                    <span @click="handleEditMeeting(item)" >修改</span>
                                    <span @click="handleCancelMeeting(item.id)">取消</span>
                                </p>
                                <span class="edit-meeting-summary" @click="handleEditMeetingSummary(item)">会议纪要</span>
                                <el-tooltip
                                    class="box-item"
                                    effect="light"
                                    content="导出会议内容"
                                    placement="top-start"
                                >
                                    <span class="download-excel" @click="handleOpenExport(item)"><el-icon><Download /></el-icon></span>
                                </el-tooltip>
                            </div>
                        </div>
                    </el-timeline-item>
                    <div class="loading" v-show="isLoading">数据加载中......</div>
                </el-timeline>
            </div>
        </div>
        <!-- 转发会议 -->
        <div class="transmit-dialog" >
            <el-dialog
                v-model="isTransmitMeeting"
                align-center
                width="25%"
                title="it-web,jifuinfo.com 显示"
                :show-close="false"
                :close-on-click-modal="false"
            >
                <p>{{message}}</p>
                <template #footer>
                    <el-button type="primary" @click="isTransmitMeeting = false"> 确 认 </el-button>
                </template>
            </el-dialog>
        </div>
        <!-- 会议纪要 -->
        <el-dialog 
            class="meeting-summary" 
            v-model="meetingSummaryVisible" 
            :title="meetingSummaryTitle" 
            :before-close="handleCancelMeetingSummary" 
            width="35%" 
            top="8vh"
            :close-on-click-modal="false"
        >
            <template #header="{titleId, titleClass }">
                <div class="my-header">
                    <h4 :id="titleId" :class="titleClass">{{meetingSummaryTitle}}</h4>
                    <el-button type="primary" plain @click="handleSwitchMeetingSummary" v-show="meetingSummaryRow.createdBy == currentUserId">{{ isExcel ? '切换word内容' : '切换excel内容' }}</el-button>
                </div>
            </template>
            <el-scrollbar max-height="500px">
                <!-- excel内容 -->
                <div class="summary-detail" v-if="isExcel">
                    <el-form
                        ref="ruleFormRef"
                        :model="summaryDetailForm"
                        label-width="10px"
                        :disabled="isMeetingSummaryDetail"
                        >
                        <el-form-item
                            class="plan"
                            label=""
                            prop="minutes"
                            :rules="{required: true,message: '不可为空',trigger: 'blur',}" >
                            <el-input
                                v-model="summaryDetailForm.minutes"
                                :autosize="{ minRows: 10}"
                                maxlength="10000"
                                :show-word-limit="true"
                                type="textarea"
                                placeholder="请输入会议纪要"/>
                        </el-form-item>
                        <el-form-item
                            class="plan"
                            label=""
                            prop="minutes"
                        >
                            <el-input
                                v-model="summaryDetailForm.remark"
                                :autosize="{ minRows: 5}"
                                maxlength="1000"
                                :show-word-limit="true"
                                type="textarea"
                                placeholder="请输入备注"/>
                        </el-form-item>
                        <div v-if="meetingSummaryRow.createdBy == currentUserId">
                            <p class="title">目标与工作内容：</p>
                            <div class="target" v-for="(plan, index) in summaryDetailForm.minutesPlans" :key="index">
                                <div class="target-item">
                                    <el-form-item
                                        class="plan"
                                        :label="`${index+1}`"
                                        :prop="'minutesPlans.' + index + '.plan'"
                                        :rules="{required: true, message: '不可为空', trigger: 'blur',}"  
                                    >
                                        <el-input placeholder="请输入目标或工作内容" v-model="plan.plan"/>
                                    </el-form-item>
                                    <el-form-item
                                        label=""
                                        :prop="'minutesPlans.' + index + '.status'"
                                        :rules="{required: true,message: '不可为空',trigger: 'change',}"
                                    >
                                        <el-select
                                            v-model="plan.status"
                                            placeholder="请选择状态"
                                            style="width: 150px"
                                        >
                                            <el-option
                                                v-for="item in summaryDetailTypeList"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value"
                                            />
                                        </el-select>
                                    </el-form-item>
                                    <div class="operation-btn" v-show="!isMeetingSummaryDetail">
                                        <el-icon @click="handleAddSummaryInput()" class="circle-plus"><CirclePlus /></el-icon>
                                        <span>
                                            <el-icon @click="handleDeleteSummaryInput(index, plan.id)" v-if="summaryDetailForm.minutesPlans?.length > 1" class="remove"  ><Remove /></el-icon>
                                            <el-icon class="remove-ban" v-else><Remove /></el-icon>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-form>
                </div>
                <!-- word内容 -->
                <div class="work-detail" v-else>
                    <el-form
                        ref="ruleFormRef"
                        :model="workDetailForm"
                        label-width="auto"
                        class="demo-dynamic"
                        :disabled="isMeetingSummaryDetail"
                    >
                        <p class="title target-title">1.本次目标</p>
                        <div class="target" v-for="(target, index) in workDetailForm.target" :key="index">
                            <el-form-item
                                :label="'目标' + `${index+1}`"
                                :prop="'target.' + index + '.content'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="target.content" style="width: 460px;"/>
                                <div class="operation-btn" v-show="!isMeetingSummaryDetail">
                                    <el-icon @click="handleAddInput(1)" class="circle-plus"><CirclePlus /></el-icon>
                                    <span>
                                        <el-icon @click="handleDeleteInput(1, index, target.id)" class="remove" v-if="workDetailForm.target?.length > 1"><Remove /></el-icon>
                                            <el-icon class="remove-ban" v-else><Remove /></el-icon>
                                    </span>
                                </div>
                            </el-form-item>
                        </div>
                        <p class="title">2.问题</p>
                        <div class="problem" v-for="(problem, index) in workDetailForm.problem" :key="index">
                            <el-form-item
                                :label="'问题' + `${index+1}`"
                                :prop="'problem.' + index + '.content'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="problem.content" style="width: 460px;"/>
                                <div class="operation-btn" v-show="!isMeetingSummaryDetail">
                                    <el-icon @click="handleAddInput(2)" class="circle-plus"><CirclePlus /></el-icon>
                                    <span>
                                        <el-icon @click="handleDeleteInput(2, index, problem.id)" class="remove" v-if="workDetailForm.problem?.length > 1"><Remove /></el-icon>
                                        <el-icon class="remove-ban" v-else><Remove /></el-icon>
                                    </span>
                                </div>
                            </el-form-item>
                        </div>
                        <p class="title">3.项目未来的优化方向</p>
                        <div class="future-direction" v-for="(futureDirection, index) in workDetailForm.futureDirection" :key="index">
                            <el-form-item
                                :label="'方向' + (index + 1)"
                                :prop="'futureDirection.' + index + '.content'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="futureDirection.content" style="width: 460px;"/>
                                <div class="operation-btn" v-show="!isMeetingSummaryDetail">
                                    <el-icon @click="handleAddInput(3)" class="circle-plus"><CirclePlus /></el-icon>
                                    <span>
                                        <el-icon @click="handleDeleteInput(3, index, futureDirection.id)" class="remove" v-if="workDetailForm.futureDirection?.length > 1"><Remove /></el-icon>
                                        <el-icon class="remove-ban" v-else><Remove /></el-icon>
                                    </span>
                                </div>
                            </el-form-item>
                        </div>
                        <p class="title">4.迭代需求</p>
                        <div class="requirement" v-for="(requirement, index) in workDetailForm.requirement" :key="index">
                            <el-form-item
                                :label="'需求' + `${index+1}`"
                                :prop="'requirement.' + index + '.content'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="requirement.content" style="width: 460px;"/>
                                <div class="operation-btn" v-show="!isMeetingSummaryDetail">
                                    <el-icon @click="handleAddInput(4)" class="circle-plus"><CirclePlus /></el-icon>
                                    <span>
                                        <el-icon @click="handleDeleteInput(4, index, requirement.id)" class="remove" v-if="workDetailForm.requirement?.length > 1"><Remove /></el-icon>
                                        <el-icon class="remove-ban" v-else><Remove /></el-icon>
                                    </span>
                                </div>
                            </el-form-item>
                        </div>
                    </el-form>
                </div>
            </el-scrollbar>
            <template #footer>
            <div class="dialog-footer" v-show="!isMeetingSummaryDetail">
                <el-button @click="handleCancelMeetingSummary">取 消</el-button>
                <el-button type="primary" @click="handleConfirmMeetingSummary(ruleFormRef)"> 确 认 </el-button>
            </div>
            </template>
        </el-dialog>
        <!-- 选择导出类型 -->
        <el-dialog 
            class="meeting-summary downLoad—dialog" 
            v-model="downloadVisible" 
            title="请选择导出类型" 
            :before-close="handleCancelExport" 
            width="20%" 
            top="30vh" 
            :close-on-click-modal="false"
        >
            <img src="@/assets/img/excel.png" alt="" @click="handleExportMeeting(0)">
            <img src="@/assets/img/word.png" alt="" @click="handleExportMeeting(1)">
        </el-dialog>
    </div>
</template>
<script setup lang="ts">
    import { ElMessage, ElMessageBox, dayjs, type FormInstance } from 'element-plus'
    import { Download } from '@element-plus/icons-vue'
    import { onMounted, reactive, ref } from 'vue';
    import { useRouter } from 'vue-router';
    import { useInfiniteScroll } from '@vueuse/core'
    import { addMeetingMinutes, addMeetingWord, cancelMeetingRecord, getHistoryList, getMeetingMinutes, recordExport, getMeetingWord, deleteWordOrPlan } from '@/request/api/history'
    import { meetingState } from '@/utils/types'
    
    const router = useRouter();
    
    const userInfo = ref<any>(JSON.parse(localStorage.getItem('userInfo') as string));
    const currentUserId = userInfo.value?.userId; // 当前登录人id

    const timelineRef = ref(null); // 获取dom节点
    let loading = ref(true); // 页面loading
    let data = ref<any>([]);
    let limit = ref(10); // 默认限制条数 10
    let page = ref(1); // 默认页数 1
    let isLoading = ref(false); // 控制数据加载中是否显示
    let canLoadMore = ref(true); // 是否 继续请求数据
    // 数据接口
    // interface IData {
    //     id: string;
    //     // title: string; // 会议名称
    //     // description: string; // 会议描述
    //     // name: string; // 会议内容
    //     name: string;
    //     create: string;
    //     time: string;
    //     theme: string;
    //     attendees: string;
    //     status: string;
    //     operate: string;
    // }
    
    onMounted(async() => {
        await getDataOnScroll();
    })

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
     * @description 获取列表数据
     * @param {number | string} userId 用户id
     * @param {number} page 页码
     * @param {number} limit 每页条数
     */
    const getData = async(data: {userId: number | string, page: number, limit: number}) => {
        let list:any = [];
        let total:number = 0;
        await getHistoryList(data)
            .then((res) => {
                list = processData(res.data);
                total = res.data?.length;
            })
            .catch(err => {})
            .finally(() => {
                loading.value = false;
            });
                
        return {
            data: list,
            total,
        };
    }

    // 滚动加载
    const getDataOnScroll = async () => {
        // 中断处理
        if (!canLoadMore.value || isLoading.value) return;

        // 打开loading
        isLoading.value = true;
        // 发送请求
        const {data: newData, total} = await getData({userId: userInfo.value?.userId, page: page.value, limit: limit.value});
        
        // 若返回数据长度小于限制 停止加载
        if(total < limit.value) {
            canLoadMore.value = false;
        }
        // 合并数据
        data.value = await [ ...data.value, ...newData].reduce((acc: any, cur: any) => {
            
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
            distance: 15, // 距离底部的长度
        }
    );

    const handleEditMeeting = (item: any) => {
        sessionStorage.setItem('meetingInfo', JSON.stringify(item))
        router.push('/meeting-appoint')
    }

    /**
     * @description 打开取消会议弹窗
     * @param time 
     * @param index 
     */
    const handleCancelMeeting = async(id: number) => {
        // isCancelMeeting.value = true;
       await ElMessageBox.confirm('是否确定取消会议？', {
            buttonSize: 'small',
        })
        .then(() => {
            cancelMeetingRecord({userId: userInfo.value.userId, meetingId: id})
                .then((res) => {
                    ElMessage.success('取消成功!');
                })
                .catch(err => {})
                .finally(() => {
                    setTimeout(() => {
                        location.reload();
                    }, 1000);
                })
        })
        .catch(() => {})
    }

    let isTransmitMeeting = ref<boolean>(false);
    let message = ref<string>('已成功复制到剪贴板');
    let address = ref<string>('');
    /**
     * @description 转发会议
     */
    const transmitMeeting = (item: any) => {
        address.value = 
        `会议主题: ${item.title}\n发起人: ${item.adminUserName}\n会议日期: ${item.date}\n会议时间: ${item.time}\n会议地点: ${item.meetingRoomName}\nURL:${decodeURIComponent(userInfo.value.url)}/#/login`;
        isTransmitMeeting.value = true;

        // navigator.clipboard.writeText(address.value).then(() => {})
        // .catch(() => {
        //     message.value = "复制失败！";
        // })

        // 创建text area
        let textArea = document.createElement("textarea");
        textArea.value = address.value;
        // 使text area不在viewport，同时设置不可见
        textArea.style.position = "absolute";
        textArea.style.opacity = '0';
        textArea.style.left = "-999999px";
        textArea.style.top = "-999999px";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        if (!document.execCommand('copy')) {
            message.value = "复制失败！";
        }
        textArea.remove();
    }


    const ruleFormRef = ref<FormInstance>();
    let meetingSummaryVisible = ref<boolean>(false); // 会议纪要弹窗
    let meetingRecordId = ref<number>(0);  // 会议记录id
    let meetingSummaryId = ref<number | undefined>(undefined); // 会议纪要id
    let isMeetingSummaryDetail = ref<boolean>(true); // 是否是查看会议纪要详情 true 查看 false 编辑/新增
    let meetingSummaryTitle = ref<string>('查看会议纪要'); // 弹窗标题
    let meetingSummaryRow = ref<any>(); // 行数据
    let isExcel = ref<boolean>(true); // 是否是excel true:excel false:word
    // 会议纪要 excel 详情
    let summaryDetailForm = ref<any>({
        minutes: '',
        remark: '',
        minutesPlans:[
        {
            plan: '',
            status: '', // 状态 1：待优化、2：研发需求
        }
    ]});
    // excel 状态列表
    let summaryDetailTypeList = ref<any[]>([
        { label: '待优化', value: 1 },
        { label: '研发需求', value: 2 },
    ]);
    /**
     * @description 查看会议纪要弹窗
     */
    const handleMeetingSummary = (item: any) => {
        meetingSummaryRow.value = item;
        meetingRecordId.value = item.id;
        meetingSummaryVisible.value = true;
        isMeetingSummaryDetail.value = true;
        meetingSummaryTitle.value = `查看会议纪要（${item.date} / ${item.title}）`;
        getMeetingMinutesReq();
    }
    /**
     * @description 获取会议纪要请求 excel
     * @param meetingRecordId 会议记录id
     */
    const getMeetingMinutesReq = () => {
        getMeetingMinutes({userId: userInfo.value.userId, meetingRecordId: meetingRecordId.value})
        .then((res) => {
            const {minutes, id, minutesPlans, remark} = res.data;
            summaryDetailForm.value.minutes = minutes;
            meetingSummaryId.value = id;
            summaryDetailForm.value.minutesPlans = minutesPlans?.length ? minutesPlans : summaryDetailForm.value.minutesPlans;
            summaryDetailForm.value.remark = remark;
        })
        .catch(err => {})
    }
    /*
     * @description 编辑会议纪要 excel
     */
    const handleEditMeetingSummary = (item: any) => {
        meetingSummaryRow.value = item;
        meetingRecordId.value = item.id;
        isMeetingSummaryDetail.value = false;
        meetingSummaryTitle.value = `编辑会议纪要（${item.date} / ${item.title}）`;
        getMeetingMinutesReq();
        meetingSummaryVisible.value = true;
    }
    /**
     * @description 添加会议纪要 excel 输入框
     */
    const handleAddSummaryInput = () => {
        summaryDetailForm.value.minutesPlans.push({
            plan: '',
            status: '', // 状态 1：待优化、2：研发需求
        })
    }
    /**
     * @description 删除会议纪要 excel 输入框
     * @param index 输入框索引
     */
    const handleDeleteSummaryInput = (index: number, id: number) => {
        if (!id) {
            return summaryDetailForm.value.minutesPlans.splice(index, 1);
        }
        handelDeleteWordOrPlan({planId: id});
    }

    // 会议纪要 word 表单
    const workDetailForm = ref<any>({
        target: [{
            content: "",
            type: 1,
        }],
        problem:[{
            content: "",
            type: 2,
        }],
        futureDirection: [{
            content: "",
            type: 3,
        }],
        requirement:[{
            content: "",
            type: 4,
        },],
        
    })
    /**
     * @description: 添加输入框
     * @param {number} type 输入框类型 1: 目标 2：问题 4：需求
     * @return {*}
     */
    const handleAddInput = (type: number) => {
        switch(type) {
            case 1:
                return workDetailForm.value.target.push({
                    content: "",
                    type: 1,
                });
            case 2:
                return workDetailForm.value.problem.push({
                    content: "",
                    type: 2,
                });
            case 3:
                return workDetailForm.value.futureDirection.push({
                    content: "",
                    type: 3,
                });
            case 4:
                return workDetailForm.value.requirement.push({
                    content: "",
                    type: 4,
                });
        }
    }
    /**
     * @description 删除输入框
     * @param type 输入框类型 1: 目标 2：问题 4：需求
     * @param index 输入框索引
     */
    const handleDeleteInput = (type: number, index: number, id: number ) => {
        if (!id) {
            switch(type) {
                case 1:
                    return workDetailForm.value.target.splice(index, 1);
                case 2:
                    return workDetailForm.value.problem.splice(index, 1);
                case 3:
                    return workDetailForm.value.futureDirection.splice(index, 1);
                case 4:
                    return workDetailForm.value.requirement.splice(index, 1);
            }
        }
        handelDeleteWordOrPlan({contentId: id});
    }
    const handelDeleteWordOrPlan = (data: {contentId?: any, planId?: any}) => {
        deleteWordOrPlan(data)
            .then(res => {
                ElMessage.success('删除成功！');
                data.contentId ? getMeetingWordDetail() : getMeetingMinutesReq();
            })
            .catch(err => {
                console.log(err);
            })
    }
    
    /**
     * @description 切换会议纪要
     */
    const handleSwitchMeetingSummary = () => {
        const { date, title } = meetingSummaryRow.value;
        isExcel.value = !isExcel.value;
        
        // 明确命名操作类型，提高代码可读性
        const operationName = isMeetingSummaryDetail.value ? '查看' : '编辑';
        
        // 使用三元运算符直接在赋值时进行条件判断
        meetingSummaryTitle.value = isExcel.value
            ? `${operationName}会议纪要（${date} / ${title}）`
            : `${operationName}本周内容（${date} / ${title}）`;
  
        if (!isExcel.value) {
            getMeetingWordDetail();
        }
    }
    /**
     * @description 获取会议纪要word详情
     */
    const getMeetingWordDetail = () => {
        
        getMeetingWord(meetingRecordId.value)
            .then(res => {

                const target = res.data.filter((item: any) => item.type == 1);
                const problem = res.data.filter((item: any) => item.type == 2);
                const futureDirection = res.data.filter((item: any) => item.type == 3);
                const requirement = res.data.filter((item: any) => item.type == 4);

                workDetailForm.value.target = target.length ? target : workDetailForm.value.target;
                workDetailForm.value.problem = problem.length ? problem : workDetailForm.value.problem;
                workDetailForm.value.futureDirection = futureDirection.length ? futureDirection : workDetailForm.value.futureDirection;
                workDetailForm.value.requirement = requirement.length ? requirement : workDetailForm.value.requirement;
            })
            .catch(error => {})
    }

    /**
     * @description 提交会议纪要
     */
     const handleConfirmMeetingSummary = (formEl: FormInstance | undefined) => {
        if (!formEl) return
        formEl.validate((valid) => {
            if (!valid) return;
            if (isExcel.value) {
                let params:any = {
                    id: meetingSummaryId.value, 
                    userId: userInfo.value.userId, 
                    minutes: summaryDetailForm.value.minutes,
                    remark: summaryDetailForm.value.remark,
                    meetingRecordId: meetingRecordId.value,
                }
                if (meetingSummaryRow.value.createdBy == currentUserId) {
                    params.minutesPlan = summaryDetailForm.value.minutesPlans;
                }
                addMeetingMinutes(params)
                .then((res) => {
                    ElMessage.success('提交成功!');
                })
                .catch(err => {})
                .finally(() => {
                    handleCancelMeetingSummary();
                })
            } else {
                const { target, problem, futureDirection, requirement} = workDetailForm.value;
                const meetingWordDTOList = [...target, ...problem, ...futureDirection, ...requirement];
                const params = {
                    userId: userInfo.value.userId,
                    meetingRecordId: meetingRecordId.value,
                    meetingWordDTOList
                }
                addMeetingWord(params)
                .then((res: any) => {
                    ElMessage.success(res.msg);
                })
                .catch(error => {})
                .finally(() => {
                    handleCancelMeetingSummary();
                })
            }
        })
    }

    /**
     * @description 取消会议纪要
     */
     const handleCancelMeetingSummary = () => {
        meetingSummaryVisible.value = false;
        meetingRecordId.value = 0;
        meetingSummaryId.value = undefined;
        isExcel.value = true;
        meetingSummaryRow.value = {};
        resetSummaryDetailForm();
        resetWorkDetailForm();
    }
    /**
     * @description 重置会议纪要 excel 表单
     */
    const resetSummaryDetailForm = () => {
        summaryDetailForm.value = {
            minutes: '',
            minutesPlans:[
            {
                plan: '',
                status: '', // 状态 1：待优化、2：研发需求
            }
        ]};
        ruleFormRef.value?.resetFields();
    }
    /**
     * @description 重置会议纪要 work 表单
     */
    const resetWorkDetailForm = () => {
        workDetailForm.value = {
            target: [{
                content: "",
                type: 1,
            }],
            problem:[{
                content: "",
                type: 2,
            }],
            futureDirection: [{
                content: "",
                type: 3,
            }],
            requirement:[{
                content: "",
                type: 4,
            },],
        }
        ruleFormRef.value?.resetFields();
    }
    /*************************************************** 导出会议内容 **********************************/
    let downloadVisible = ref<boolean>(false); // 选择导出类型弹窗
    let downloadRow = ref<any>(); // 行数据
    /**
     * @description 打开选择导出类型弹窗
     * @param item 行数据
     */
    const handleOpenExport = (item: any) => {
        downloadVisible.value = true;
        downloadRow.value = item;
    }

    /**
     * @description 关闭选择导出类型弹窗
     */
    const handleCancelExport = () => {
        downloadVisible.value = false;
        downloadRow.value = '';
    }

    /**
     * @description 导出会议记录excel
     * @param type 0:excel 1:word
     */
    const handleExportMeeting = async(type: number) => {
        const {id, title, description, createdBy, adminUserName, meetingRoomId, meetingRoomName, location, meetingNumber, attendees, users, startTime, endTime, status, isDeleted} = downloadRow.value;
        // 重组参数
        const params = {id, title, description, createdBy, adminUserName, meetingRoomId, meetingRoomName, location, meetingNumber, attendees, users, startTime, endTime, status, isDeleted};
        
        const res:any = await recordExport(type, [params]);
        if (res?.data.type != "application/json") {
            ElMessage.success('导出成功!');
            downloadExcel(res.data, '会议纪要');
        } else {
            ElMessage.error("导出失败！");
        }
        handleCancelExport();
    }
    const downloadExcel = (blobData: Blob | MediaSource , fileName: string) => {
        // 创建一个虚拟链接
        var link = document.createElement("a");
        link.href = window.URL.createObjectURL(blobData);
        
        link.download = fileName;

        // 模拟点击链接进行下载
        link.click();
    }

</script>

<style scoped lang="scss">
    .history {
        .theme {
            width: 1568px;
            height: 70px;
            font-size: 25px;
            font-weight: 500;
            text-align: center;
            line-height: 70px;
            letter-spacing: 0.05em;
            color: #3268DC;
            border-radius: 15px;
            background: #FFFFFF;
            margin: 0 auto;
        }
        .content {
            width: 1567px;
            height: 641px;
            border-radius: 15px;
            box-sizing: border-box;
            border: 3px solid rgba(18, 115, 219, 0.8);
            margin: 20px auto;
            .title {
                height: 48px;
                div {
                    color: #3A3A3A;
                    font-size: 17.6px;
                    font-weight: 400;
                    line-height: 28px;
                }
            }
            .list-container {
                .el-timeline {
                    overflow-y: scroll;
                    max-height: 551px;
                    margin-right: 15px;
                    .timestamp {
                        position: absolute;
                        top: 0;
                        left: 32px;
                        text-align: center;
                        p:first-child {
                            font-size: 16px;
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
                    .loading {
                        color: #666666;
                        font-size: 20px;
                        text-align: center;
                        font-weight: 300;
                    }
                }
                ::-webkit-scrollbar {
                    width: 17.6px;
                    border-radius: 15px;
                }
                /* 自定义滚动条轨道 */
                ::-webkit-scrollbar-track {
                    // background: #FFFFFF;
                    border-radius: 15px;
                }
                
                /* 自定义滚动条的滑块（thumb） */
                ::-webkit-scrollbar-thumb {
                    background: #EDEBEB;
                    border-radius: 15px;
                }
                .card-item {
                    width: 1401px;
                    height: 60px;
                    border-radius: 10px;
                    background: #FFFFFF;
                    box-shadow: 0 3px 2px 0 rgba(0, 0, 0, 0.04);
                    div {
                        font-size: 16px;
                        line-height: 20px;
                        color: #666666;
                        &:nth-child(1) {
                            position: relative;
                        }
                        &:nth-child(7) {
                            &>p>span:nth-child(1) {
                                color: #409EFF;
                            }
                            &>p>span:nth-child(2) {
                                margin: 0 10px;
                                color: #F56C6C;
                            }
                            .edit-meeting-summary {
                                color: #67C23A;
                            }
                            .download-excel {
                                font-size: 18px;
                                color: #409EFF;
                                margin: 0 0 0 10px;
                                transition: transform 0.2s ease;
                                &:hover {
                                    color:#3268dc;
                                    transform: scale(1.1);
                                }
                            }
                            cursor: pointer;
                        }
                        .ellipsis {
                            text-overflow: ellipsis;
                            overflow: hidden;
                            white-space: nowrap;
                        }
                        .transmit-icon {
                            position: absolute;
                            left: 0;
                            cursor: pointer;
                        }
    
                    }
                }
            }
            .title, .card-item {
                width: 1401px;
                padding: 0 10px;
                margin: 12.8px 80px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                div {
                    letter-spacing: 0;
                    display: flex;
                    flex: 1;
                    justify-content: center;
                    &:nth-child(1) {
                        width: 184px;
                        flex: 1.2;
                        padding: 0 6px 0 58px;
                        cursor: pointer;
                    }
                    &:nth-child(2) {
                        width: 184px;
                        flex: 1.2;
                        padding: 0 6px;
                    }
                    &:nth-child(5) {
                        width: 243px;
                        padding: 0 48px;
                        flex: 1.5;
                    }
                    &:nth-child(7) {
                        flex: 1.3;
                    }
                }
            }
        }
        :deep().meeting-summary {
            border-radius: 15px;
            padding: 20px;
            .my-header {
                display: flex;
                // align-items: space-between;
                justify-content: space-between;
                .el-dialog__title {
                    font-size: 16px;
                    font-weight: 500;
                }
            }
            .summary-detail {
                .title {
                    font-weight: bold;
                    color: #303133;
                    margin: 25px 0 10px;
                }
                 .target-item {
                    display: flex;
                    justify-content: space-between;
                    .plan {
                        flex: .8;
                    }
                }
                .operation-btn {
                    text-align: right;
                    line-height: 3;
                }
            }
            .work-detail{
                .el-form {
                    padding: 10px;
                    .title {
                        font-weight: bold;
                        color: #303133;
                        margin: 45px 0 10px;
                    }
                    .target-title {
                        margin-top: 0;
                    }
                    
                    .operation-btn {
                        text-align: right;
                    }
                }
            }
            .work-detail, .summary-detail {
                .el-form-item__content {
                    justify-content: space-between;
                    align-items: center;
                    line-height: 0;
                }
                .remove-ban {
                    font-size: 22.4px;
                    cursor: no-drop;
                }
                .circle-plus {
                    margin-right: 10px;
                    color: #409EFF;
                }
                .remove {
                    color: #F56C6C;
                }
                .circle-plus, .remove {
                    font-size: 22.4px;
                    cursor: pointer;
                }
            }
        }
        :deep().downLoad—dialog {
            .el-dialog__body {
                display: flex;
                justify-content: space-around;
                align-items: center;
                padding: 15px 0;
                img {
                    cursor: pointer;
                }
            }
        }
    }
</style>