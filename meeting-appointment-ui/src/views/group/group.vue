<template>
    <div class="group">
        <div class="theme">
            <div class="left">
                <el-input v-model="groupTitle" style="width: 640px" maxlength="15" placeholder="请输入" />
                <div>群组名称</div>
            </div>
            <div class="center">
                <p @click="handleAddPerson"><el-input class="meeting-people" v-model="groupPeopleNames" clearable
                        readonly :prefix-icon="Plus" placeholder="请添加群组人员" /></p>
                <div>添加人员</div>
            </div>
            <div class="right" @click="handleCreateGroup">创 建</div>
        </div>
        <div class="content" v-loading="loading" element-loading-background="rgba(122, 122, 122, 0.1)">
            <div class="title">
                <div>创建人</div>
                <div>创建时间</div>
                <div>群组名称</div>
                <div>群组人员</div>
                <div>操作</div>
            </div>
            <el-timeline class="list-container" ref="timelineRef">
                <el-timeline-item class="timeline-item" v-for="(value, index) in dataList" :key="index">
                    <div class="timestamp">
                        <p>{{ value.month }}月</p>
                        <p>{{ value.day }}</p>
                    </div>
                    <div class="card-item" v-for="(item, key) in value.list" :key="index">
                        <div>{{ item.userName }}</div>
                        <div>{{ item.editTime }}</div>
                        <div v-if="item.isEdit">
                            <el-icon @click="item.isEdit = false" v-show="item.createdBy == currentUserId">
                                <Edit />
                            </el-icon>
                            {{ item.groupName }}
                        </div>
                        <div v-else>
                            <el-input v-model="item.groupName" @blur="handleEditGroupName(index, key, item)" />
                        </div>
                        <div>
                            <el-icon @click="editAttendees(item)" v-show="item.createdBy == currentUserId">
                                <Edit />
                            </el-icon>
                            <el-popover
                                placement="bottom"
                                :disabled="item.members?.length < 18"
                                :width="300"
                                trigger="hover"
                                :content="item.members"
                            >
                                <template #reference>
                                    <p class="ellipsis">{{ item.members }}</p>
                                </template>
                            </el-popover>
                        </div>
                        <div @click="handleDeleteMeeting(item.id)"><span
                                v-show="item.createdBy === currentUserId">删除</span></div>
                    </div>
                </el-timeline-item>
                <div class="loading" v-show="isLoading">数据加载中......</div>
            </el-timeline>
        </div>
        <!-- 添加群组成员弹窗 -->
        <personTreeDialog v-model="addGroupVisible" title="添加群组人员" :type="type" :peopleIds="peopleIds"
            @close-dialog="closeAddGroupDialog" @submit-dialog="handleCheckedNodes" />
    </div>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElTree, dayjs } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMeetingGroupList, deleteMeetingGroup, updateMeetingGroup, addMeetingGroup } from '@/request/api/group'
import personTreeDialog from "@/components/person-tree-dialog.vue";
import { useInfiniteScroll } from '@vueuse/core'

const userInfo = ref<any>(JSON.parse(localStorage.getItem('userInfo') || '')); // 获取用户信息
const currentUserId = userInfo.value?.userId; // 当前登录人id

let loading = ref(true); // 页面loading

onMounted(() => {
    // 初始化数据
    getDataOnScroll();
})

/************************** 创建群组开始 **************************/
let groupTitle = ref<string>(''); // 群组名称
let groupPeopleNames = ref<string>(''); // 群组成员名称
let peopleIds = ref<any>([]); // 群组成员id
let groups = ref<any>([]); // 选中的群组成员信息、
let type = ref<number>(1); // 1 创建群组 2 修改群组
const timelineRef = ref(null); // 获取dom节点
let limit = ref(10); // 默认限制条数 10
let page = ref(1); // 默认页数 1
let isLoading = ref(false); // 控制数据加载中是否显示
let canLoadMore = ref(true); // 是否 继续请求数据
let addGroupVisible = ref(false);
// 打开添加群组人员弹窗
const handleAddPerson = () => {
    if (type.value == 2) {
        peopleIds.value = [];
    }
    type.value = 1;
    addGroupVisible.value = true;
}

/**
 * @description 提交需要添加的群组人员
 */
const handleCheckedNodes = (type: number, active: number, form: any) => {
    // 创建人信息
    const creator = ref<any>({ userId: userInfo.value.userId, userName: userInfo.value.name });
    // 获取被选中成员的id
    peopleIds.value = form.peopleIds;
    // 获取被选中人员的 信息将创建人信息添加进去并去重
    groups.value = Array.from(new Set([...form.groups, creator.value].map((item: any) => JSON.stringify(item)))).map((item: any) => JSON.parse(item));

    // 创建群组
    if (type == 1) {
        // 处理被选中成员的名称 用，隔开
        groupPeopleNames.value = form.groupPeopleNames.join(',');
    }
    // 修改群组成员
    if (type == 2) {
        updateGroupInfo({ id: groupInfo.value.id, users: groups.value });
    }
    // 关闭弹窗
    closeAddGroupDialog();
}
/**
 * @description 关闭添加群组人员弹窗
 */
const closeAddGroupDialog = () => {
    addGroupVisible.value = false;
}
/**
 * 创建群组
 */
const handleCreateGroup = () => {
    if (groupTitle.value === '') {
        ElMessage.warning('请输入群组名称！')
        return
    }
    if (groupPeopleNames.value === '') {
        ElMessage.warning('请添加群组人员！')
        return
    }
    addMeetingGroup({
        groupName: groupTitle.value,
        createdBy: userInfo.value.userId,
        userName: userInfo.value.name,
        users: groups.value,
    })
        .then(res => {
            ElMessage.success('创建成功!');
            groupTitle.value = '';
            groupPeopleNames.value = '';
            // 重置页码
            page.value = 1;
            // 获取数据
            getDataOnScroll();
            groups.value = [];
            
        })
        .catch(err => { })
        .finally(() => {})
}
/************************** 创建群组结束 **************************/

let dataList = ref<any>([]); // 列表数据
let groupInfo = ref<any>({}); // 群组信息
// // 数据接口
// interface IData {
//     id: string; // id
//     gmtCreate: string;   // 创建时间
//     gmtModified: string; // 修改时间
//     createdBy: string;   // 创建人id
//     isDeleted: boolean;  // 是否删除
//     userName: string;    // 创建人名称
//     groupName: string;   // 群组名
//     users: [],            // 群组成员
//     member?: string,
// }

/**
 * @description 处理列表数据
 * @param data 获取的数据
 */
const processData = (data: any[]) => {
    return data.reduce((acc: any, current: any) => {
        current.date = dayjs(current.gmtCreate).format('YYYY-MM-DD');
        const obj = acc.find((group: any) => group.date === current.date);
        const month = dayjs(current.gmtCreate).format('M');
        const day = dayjs(current.gmtCreate).format('D');
        current.members = current.users.map((user: any) => user.userName).join(',');
        current.editTime = dayjs(current.gmtCreate).format('HH:mm:ss');
        // 群组名称是否可编辑
        current.isEdit = true;

        if (obj) {
            obj.list.push(current);
        } else {
            acc.push({ date: current.date, month: month, day: day, list: [current] });
        }
        return acc;
    }, []);
}

/**
 * @description 获取群组列表
 */
const handleGroupList = async (data: { userId: string, pageNum: number, pageSize: number }) => {
    let list: any = [];
    let total: number = 0;
    await getMeetingGroupList(data)
        .then(res => {
            total = res.data.length;
            list = processData(res.data);
        })
        .catch((err) => { })
        .finally(() => {
            loading.value = false;
        })
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
    const { data: newData, total } = await handleGroupList({ userId: userInfo.value.userId, pageNum: page.value, pageSize: limit.value });

    // 若返回数据长度小于限制 停止加载
    if (total < limit.value) {
        canLoadMore.value = false;
    }
    // 页码为1时 清空数组 重新获取数据
    if (page.value == 1) {
        dataList.value = [];
    }
    // 合并数据
    dataList.value = await [...dataList.value, ...newData].reduce((acc: any, cur: any) => {
        // 找出数组中日期一样的item
        const obj = acc.find((item: any) => cur.date == item.date);
        // 如果存在，将它的list push到已有日期的list中
        if (obj) {
            obj.list.push(...cur.list);
        } else {
            acc.push({...cur})
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

/**
 * @description: 更新群组信息
 * @param {object} data 群组信息 {id: 群组id, groupName: 群组名称, users: 群组成员}
 * @return {*}
 */
const updateGroupInfo = async (data: { id: string; groupName?: string; users?: [] }) => {
    await updateMeetingGroup(data)
        .then(res => {
            ElMessage.success('修改成功');
        })
        .catch(err => { })
        .finally(() => {
            // 重置页码
            page.value = 1;
            // 获取数据
            getDataOnScroll();
        })
}
/**
 * @description 修改群组名称
 * @param index 下标
 */
const handleEditGroupName = async (index: number, key: number, item: any) => {
    if (!item.groupName) {
        ElMessage.warning('群组名称不可为空！');
        return;
    }
    dataList.value[index].list[key].isEdit = true;
    updateGroupInfo({ id: item.id, groupName: item.groupName });
}
/**
 * @description 编辑群组人员
 */
const editAttendees = (row: any) => {
    if (type.value == 1) {
        peopleIds.value = [];
    }
    type.value = 2;
    peopleIds.value = Array.from(new Set(row.users.map((item: any) => item.userId)));
    groupInfo.value = {
        id: row.id,
    }

    addGroupVisible.value = true;
}

/**
 * @description 删除群组会议
 * @param id 要删除群组的id
 */
const handleDeleteMeeting = async (id: string,) => {
    await ElMessageBox.confirm('是否确定删除群组', {
        customClass: 'delete-box'
    })
        .then(() => {
            deleteMeetingGroup({ id })
                .then(res => {
                    ElMessage.success('删除成功!');
                    // 重置页码
                    page.value = 1;
                    // 获取数据
                    getDataOnScroll();
                }).catch(err => { })
                .finally(() => {
                    
                });
        })
        .catch(() => {
            ElMessage.info('已取消删除！');
        })
}

</script>
<style scoped lang="scss">
.group {
    height: 751px;
    // background-color: #f5f5f5;
    .theme {
        width: 1567px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 0 auto;

        .left {
            width: 752px;

            div {
                width: 120px;
            }
        }

        .center {
            width: 616px;

            // 参会人 input内部样式
            .meeting-people {
                width: 512px;

                ::v-deep(.el-input__prefix .el-input__icon) {
                    width: 20px;
                    height: 20px;
                    color: #3268DC;
                    background: #ECF2FF;
                    cursor: pointer;
                }

                ::v-deep(.el-input__inner) {
                    font-size: 14px;
                    letter-spacing: .64px;
                    font-weight: 200;
                    cursor: pointer;
                    --el-input-placeholder-color: #3268DC;
                }
            }
        }

        .left,
        .center {
            height: 40px;
            border-radius: 8px;
            background: #FFFFFF;
            box-shadow: 0 3px 6px 0 rgba(0, 0, 0, 0.08);
            display: flex;
            justify-content: space-between;
            align-items: center;

            /* 群组管理 修改群组名称输入框样式 */
            ::v-deep(.el-input__wrapper) {
                box-shadow: none !important;
            }
        }

        .right {
            transition: transform 0.2s ease;
            cursor: pointer;
            &:active {
                transform: scale(0.96);
            }
            &:hover {
                background: rgba(90, 149, 238, 0.6);
            }
        }

        .right,
        .center div {
            width: 102px;
        }

        .right,
        .left div,
        .center div {
            height: 40px;
            line-height: 40px;
            text-align: center;
            color: #FFFFFF;
            border-radius: 8px;
            background: #409EFF;
            user-select: none;
        }
    }

    .content {
        width: 1567px;
        height: 670.88px;
        border-radius: 15px;
        box-sizing: border-box;
        border: 3px solid rgba(18, 115, 219, 0.8);
        margin: 20px auto 0;

        .title {
            height: 48px;
            div {
                font-size: 17.6px;
                font-weight: 400;
                color: #3A3A3A;
                line-height: 28px;
            }
        }

        .list-container {
            overflow-y: scroll;
            max-height: 581px;
            margin-right: 15px;

            .loading {
                color: #666666;
                font-size: 20px;
                text-align: center;
                font-weight: 300;
            }

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

            &::-webkit-scrollbar {
                width: 17.6px;
                border-radius: 15px;
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

            .card-item {
                height: 60px;
                border-radius: 10px;
                background: #FFFFFF;
                box-shadow: 0 3px 2px 0 rgba(0, 0, 0, 0.04);

                div {
                    position: relative;
                    font-size: 16px;
                    line-height: 20px;
                    color: #666666;

                    &:last-child {
                        color: #F56C6C;
                        cursor: pointer;
                    }

                    .ellipsis {
                        text-overflow: ellipsis;
                        overflow: hidden;
                        white-space: nowrap;
                    }

                    .el-icon {
                        margin-right: 10px;
                        color: #409EFF;
                        font-size: 17.6px;
                        cursor: pointer;
                    }

                    // 群组名称样式修改
                    .el-input {
                        flex: 0.7;
                    }
                }
            }
        }

        .title,
        .card-item {
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

                &:nth-child(3) {
                    width: 243px;
                    padding: 0 16px;
                    flex: 1.5;
                }

                &:nth-child(4) {
                    width: 323px;
                    padding: 0 32px;
                    flex: 1.5;
                }
            }
        }
    }

    .delete-dialog {
        .meeting-text {
            padding: 25.2px 20px;
            font-size: 23.4px;
            text-align: center;
        }

        .dialog-footer {
            display: flex;
            justify-content: space-around;
        }
    }
}
</style>