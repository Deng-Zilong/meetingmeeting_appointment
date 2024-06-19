<template>
    <div class="history">
        <div class="theme" > 会议历史记录查看 </div>
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
                                    :disabled="item.title.length < 18"
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
                <div v-if="isExcel">
                    <el-input
                        v-model="meetingSummary"
                        :autosize="{ minRows: 15}"
                        maxlength="10000"
                        :show-word-limit="true"
                        type="textarea"
                        placeholder="请输入会议纪要"
                    />
                    <div class="summary-detail" v-show="meetingSummaryRow.createdBy == currentUserId">
                        <el-form
                            ref="formRef"
                            :model="summaryDetailForm"
                            label-width="auto"
                            >
                            <p class="title">目标与工作内容：</p>
                            <div class="target" v-for="(plan, index) in summaryDetailForm.list" :key="index">
                                <div class="target-item">
                                    <el-form-item
                                        class="plan"
                                        :label="`${index+1}`"
                                        :prop="'plan.' + index + '.value'"
                                        :rules="{required: true,message: '不可为空',trigger: 'blur',}"  
                                    >
                                        <el-input placeholder="请输入目标或工作内容" v-model="plan.plan"/>
                                    </el-form-item>
                                    <el-form-item
                                        :prop="'plan.' + index + '.value'"
                                        :rules="{required: true,message: '不可为空',trigger: 'blur',}"
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
                                    <div class="operation-btn">
                                        <el-icon @click="handleAddSummaryInput()" class="circle-plus"><CirclePlus /></el-icon>
                                        <el-icon @click="handleDeleteSummaryInput(index)" v-show="summaryDetailForm.list?.length > 1" class="remove"  ><Remove /></el-icon>
                                    </div>
                                </div>
                            </div>
                        </el-form>
                    </div>
                </div>
                <div class="work-detail" v-else>
                    <el-form
                        ref="formRef"
                        :model="workDetailForm"
                        label-width="auto"
                        class="demo-dynamic"
                    >
                        <p class="title">1.本次目标</p>
                        <div class="target" v-for="(target, index) in workDetailForm.target" :key="index">
                            <el-form-item
                                :label="'目标' + `${index+1}`"
                                :prop="'target.' + index + '.value'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="target.value" style="width: 460px;"/>
                                <div class="operation-btn">
                                    <el-icon @click="handleAddInput(1)" class="circle-plus"><CirclePlus /></el-icon>
                                    <el-icon @click="handleDeleteInput(1, index)" class="remove" v-show="workDetailForm.target?.length > 1"><Remove /></el-icon>
                                </div>
                            </el-form-item>
                        </div>
                        <p class="title">2.问题</p>
                        <div class="problem" v-for="(problem, index) in workDetailForm.problem" :key="index">
                            <el-form-item
                                :label="'问题' + `${index+1}`"
                                :prop="'problem.' + index + '.value'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="problem.value" style="width: 460px;"/>
                                <div class="operation-btn">
                                    <el-icon @click="handleAddInput(2)" class="circle-plus"><CirclePlus /></el-icon>
                                    <el-icon @click="handleDeleteInput(2, index)" class="remove" v-show="workDetailForm.problem?.length > 1"><Remove /></el-icon>
                                </div>
                            </el-form-item>
                        </div>
                        <p class="title">3.项目未来的优化方向</p>
                        <div class="future-direction">
                            <el-form-item
                                label="方向"
                                prop="futureDirection"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="workDetailForm.futureDirection" style="width: 460px;"/>
                            </el-form-item>
                        </div>
                        <p class="title">4.迭代需求</p>
                        <div class="requirement" v-for="(require, index) in workDetailForm.requirement" :key="index">
                            <el-form-item
                                :label="'需求' + `${index+1}`"
                                :prop="'require.' + index + '.value'"
                                :rules="{
                                    required: true,
                                    message: '不可为空',
                                    trigger: 'blur',
                                }"
                            >
                                <el-input v-model="require.value" style="width: 460px;"/>
                                <div class="operation-btn">
                                    <el-icon @click="handleAddInput(4)" class="circle-plus"><CirclePlus /></el-icon>
                                    <el-icon @click="handleDeleteInput(4, index)" class="remove" v-show="workDetailForm.requirement?.length > 1"><Remove /></el-icon>
                                </div>
                            </el-form-item>
                        </div>
                    </el-form>
                </div>
            </el-scrollbar>
            <template #footer>
            <div class="dialog-footer" v-show="!isMeetingSummaryDetail">
                <el-button @click="handleCancelMeetingSummary">取 消</el-button>
                <el-button type="primary" @click="handleConfirmMeetingSummary"> 确 认 </el-button>
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
    import { addMeetingMinutes, cancelMeetingRecord, getHistoryList, getMeetingMinutes, recordExport } from '@/request/api/history'
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


    let meetingSummaryVisible = ref<boolean>(false); // 会议纪要弹窗
    let meetingSummary = ref<string>('');  // 会议纪要内容
    let meetingRecordId = ref<number>(0);  // 会议记录id
    let meetingSummaryId = ref<number>(0); // 会议纪要id
    let isMeetingSummaryDetail = ref<boolean>(true); // 是否是查看会议纪要详情 true 查看 false 编辑/新增
    let meetingSummaryTitle = ref<string>('查看会议纪要'); // 弹窗标题
    let meetingSummaryRow = ref<any>(); // 行数据
    let isExcel = ref<boolean>(true);
    let summaryDetailTypeList = ref<any[]>([
      { label: '待优化', value: 1 },
      { label: '研发需求', value: 2 },
    ]);
    let summaryDetailForm = ref<any>(
        {
            list: [
                {
                    plan: '',
                    status: '', // 状态 1：待优化、2：研发需求
                }
            ]
        }
    );
    /**
     * @description 获取会议纪要
     */
    const handleMeetingSummary = (item: any) => {
        meetingSummaryRow.value = item;
        meetingSummaryVisible.value = true;
        isMeetingSummaryDetail.value = true;
        meetingSummaryTitle.value = `查看会议纪要（${item.date} / ${item.title}）`;
        getMeetingMinutesReq(item.id);
    }
    /**
     * @description 获取会议纪要请求
     * @param meetingRecordId 会议记录id
     */
    const getMeetingMinutesReq = (meetingRecordId: number) => {
        getMeetingMinutes({userId: userInfo.value.userId, meetingRecordId})
        .then((res) => {
            meetingSummary.value = res.data.minutes;
            meetingSummaryId.value = res.data.id;
        })
        .catch(err => {})
    }
    /*
     * @description 编辑会议纪要
     */
    const handleEditMeetingSummary = (item: any) => {
        meetingSummaryRow.value = item;
        meetingRecordId.value = item.id;
        isMeetingSummaryDetail.value = false;
        meetingSummaryTitle.value = `编辑会议纪要（${item.date} / ${item.title}）`;
        getMeetingMinutesReq(item.id);
        meetingSummaryVisible.value = true;
    }
    const handleAddSummaryInput = () => {
        summaryDetailForm.value.list.push({
            plan: '',
            status: '', // 状态 1：待优化、2：研发需求
        })
    }
    const handleDeleteSummaryInput = (index: number) => {
        summaryDetailForm.value.list.splice(index, 1)
    }
    /**
     * @description 提交会议纪要
     */
    const handleConfirmMeetingSummary = () => {
        if (!meetingSummary.value) {
            ElMessage.warning('请输入会议纪要!');
            return;
        }
        addMeetingMinutes({id: meetingSummaryId.value, userId: userInfo.value.userId, minutes: meetingSummary.value, meetingRecordId: meetingRecordId.value})
        .then((res) => {
            ElMessage.success('提交成功!');
        })
        .catch(err => {})
        .finally(() => {
            handleCancelMeetingSummary();
        })
    }
    /**
     * @description 取消会议纪要
     */
    const handleCancelMeetingSummary = () => {
        meetingSummaryVisible.value = false;
        meetingSummary.value = '';
    }
    const formRef = ref<FormInstance>()
    const workDetailForm = ref<any>({
        target: [{
            value: "",
        }],
        problem:[{
            value: "",
        }],
        futureDirection: '',
        requirement:[{
            value: "",
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
                    value: "",
                });
            case 2:
                return workDetailForm.value.problem.push({
                    value: "",
                });
            case 4:
                return workDetailForm.value.requirement.push({
                    value: "",
                });
        }
    }
    /**
     * @description 删除输入框
     * @param type 输入框类型 1: 目标 2：问题 4：需求
     * @param index 输入框索引
     */
    const handleDeleteInput = (type: number, index: number) => {
        switch(type) {
            case 1:
                return workDetailForm.value.target.splice(index, 1);
            case 2:
                return workDetailForm.value.problem.splice(index, 1);
            case 4:
                return workDetailForm.value.requirement.splice(index, 1);
        }
    }

const submitForm = (formEl: FormInstance | undefined) => {
    console.log(workDetailForm.value);
    
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!')
    } else {
      console.log('error submit!')
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
    /**
     * @description 切换会议纪要
     */
    const handleSwitchMeetingSummary = () => {
        const {date, title} = meetingSummaryRow.value;
        isExcel.value = !isExcel.value;
        if (isExcel.value) {
            meetingSummaryTitle.value = `编辑会议纪要（${date} / ${title}）`;
        } else {
            meetingSummaryTitle.value = `编辑本周内容（${date} / ${title}）`;
        }
    }

    let downloadVisible = ref<boolean>(false);
    let downloadRow = ref<any>();
    const handleOpenExport = (item: any) => {
        downloadVisible.value = true;
        downloadRow.value = item;
    }
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
        if (res?.data.type == "application/json") {
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
            width: 98rem;
            height: 4.375rem;
            font-size: 1.5625rem;
            font-weight: 500;
            text-align: center;
            line-height: 4.375rem;
            letter-spacing: 0.05em;
            color: #3268DC;
            border-radius: .9375rem;
            background: #FFFFFF;
            margin: 0 auto;
        }
        .content {
            width: 97.9375rem;
            height: 40.0625rem;
            border-radius: .9375rem;
            box-sizing: border-box;
            border: .1875rem solid rgba(18, 115, 219, 0.8);
            margin: 1.25rem auto;
            .title {
                height: 3rem;
                div {
                    color: #3A3A3A;
                    font-size: 1.1rem;
                    font-weight: 400;
                    line-height: 1.75rem;
                }
            }
            .list-container {
                .el-timeline {
                    overflow-y: scroll;
                    max-height: 34.4375rem;
                    margin-right: 15px;
                    .timestamp {
                        position: absolute;
                        top: 0;
                        left: 2rem;
                        text-align: center;
                        p:first-child {
                            font-size: 1rem;
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
                    .loading {
                        color: #666666;
                        font-size: 1.25rem;
                        text-align: center;
                        font-weight: 300;
                    }
                }
                ::-webkit-scrollbar {
                    width: 1.1rem;
                    border-radius: .9375rem;
                }
                /* 自定义滚动条轨道 */
                ::-webkit-scrollbar-track {
                    // background: #FFFFFF;
                    border-radius: .9375rem;
                }
                
                /* 自定义滚动条的滑块（thumb） */
                ::-webkit-scrollbar-thumb {
                    background: #EDEBEB;
                    border-radius: .9375rem;
                }
                .card-item {
                    width: 87.5625rem;
                    height: 3.75rem;
                    border-radius: .625rem;
                    background: #FFFFFF;
                    box-shadow: 0 .1875rem .125rem 0 rgba(0, 0, 0, 0.04);
                    div {
                        font-size: 1rem;
                        line-height: 1.25rem;
                        color: #666666;
                        &:nth-child(1) {
                            position: relative;
                        }
                        &:nth-child(7) {
                            &>p>span:nth-child(1) {
                                color: #409EFF;
                            }
                            &>p>span:nth-child(2) {
                                margin: 0 .625rem;
                                color: #F56C6C;
                            }
                            .edit-meeting-summary {
                                color: #67C23A;
                            }
                            .download-excel {
                                font-size: 1.125rem;
                                color: #409EFF;
                                margin: 0 0 0 .625rem;
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
                width: 87.5625rem;
                padding: 0 .625rem;
                margin: 0.8rem 5rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
                div {
                    letter-spacing: 0;
                    display: flex;
                    flex: 1;
                    justify-content: center;
                    &:nth-child(1) {
                        width: 11.5rem;
                        flex: 1.2;
                        padding: 0 .375rem 0 3.625rem;
                        cursor: pointer;
                    }
                    &:nth-child(2) {
                        width: 11.5rem;
                        flex: 1.2;
                        padding: 0 .375rem;
                    }
                    &:nth-child(5) {
                        width: 15.1875rem;
                        padding: 0 3rem;
                        flex: 1.5;
                    }
                    &:nth-child(7) {
                        flex: 1.3;
                    }
                }
            }
        }
        :deep().meeting-summary {
            border-radius: 0.9375rem;
            padding: 1.25rem;
            .my-header {
                display: flex;
                // align-items: space-between;
                justify-content: space-between;
                .el-dialog__title {
                    font-size: 1rem;
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
                    .circle-plus {
                        margin-right: .625rem;
                        color: #409EFF;
                    }
                    .remove {
                        color: #F56C6C;
                    }
                    .circle-plus, .remove {
                        font-size: 1.4rem;
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
       
        .meeting-dialog { 
            .meeting-text {
                padding: 1.575rem 1.25rem;
                font-size: 1.4625rem;
                text-align: center;
            }
            .dialog-footer {
                display: flex;
                justify-content: space-around;
            }
        }
    }
</style>