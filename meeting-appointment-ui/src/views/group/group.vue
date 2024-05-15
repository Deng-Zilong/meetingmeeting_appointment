<template>
    <div class="container group">
        <div class="theme">
            <div class="left">
                <el-input v-model="groupTitle" style="width: 240px" placeholder="请输入" />
                <div>群组名称</div>
            </div>
            <div class="center">
                <el-input class="meeting-people" v-model="groupPeopleNames" clearable readonly :prefix-icon="Plus" placeholder="请添加群组人员"
              @click="handleAddPerson" />
                <div>添加人员</div>
            </div>
            <div class="right" @click="handleCreateGroup">创 建</div>
        </div>
        <div class="content">
            <div class="title">
                <div>创建人</div>
                <div>创建时间</div>
                <div>群组名称</div>
                <div>群组人员</div>
                <div>操作</div>
            </div>
            <div class="list-container">
                <div class="timeline-item" ref="timelineRef">
                    <div v-for="(item, index) in data" :key="index" class="card-item">
                        <div>{{ item.userName }}</div>
                        <div>{{ item.gmtCreate }}</div>
                        <div v-if="item.isEdit"> 
                            <el-icon @click="item.isEdit = false"> <Edit /></el-icon>
                            {{ item.groupName }}
                        </div>
                        <div v-else>
                            <el-input v-model="item.groupName" @blur="handleEditGroupName(index, item)"/>
                        </div>
                        <div><el-icon @click="editAttendees(item)"> <Edit /></el-icon> <p class="ellipsis">{{ item.members }}</p></div>
                        <div @click="handleDeleteMeeting(item.id)"><span>删除</span></div>
                    </div>
                </div>
                <div class="loading" v-show="isLoading">数据加载中......</div>
            </div>
        </div>
        <!-- 添加群组成员弹窗 -->
        <personTreeDialog
            v-model="addGroupVisible"
            title="添加群组人员"
            :type="type"
            :peopleIds="peopleIds"
            @close-dialog="closeAddGroupDialog"
            @submit-dialog="handleCheckedNodes"
        />
    </div>
</template>
<script lang="ts" setup>
    import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
    import { ElMessage, ElMessageBox, ElTree  } from 'element-plus'
    import { useUserStore } from '@/stores/user'
    import { Plus } from '@element-plus/icons-vue'
    import { getMeetingGroupList, deleteMeetingGroup, updateMeetingGroup, addMeetingGroup, getGroupUserTree } from '@/request/api/group'
    import personTreeDialog from "@/components/person-tree-dialog.vue";
    import { useInfiniteScroll } from '@vueuse/core'

    const userStore = useUserStore(); // 获取用户信息
    const userInfo = ref<any>({});
    onMounted(async() => {
        userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '');
        // 初始化数据
        data.value = await handleGroupList({userId: userInfo.value.userId, pageSize: page.value, pageNum: limit.value});
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
        // 获取被选中成员的id
        peopleIds.value = form.peopleIds;

        // 获取被选中人员的 信息并去重
        groups.value = form.groups;
    
        // // 创建群组
        if (type == 1) {
            // 处理被选中成员的名称 用，隔开
            groupPeopleNames.value = form.groupPeopleNames;
        }
        // 修改群组成员
        if (type == 2) {
            updateGroupInfo({id: groupInfo.value.id, groupName: groupInfo.value.groupName, users: groups.value});
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
                groups.value = [];
            })
            .catch(err => {
                console.log(err, "err");
            })
    }
    /************************** 创建群组结束 **************************/

    let data = ref<any>([]); // 列表数据
    let groupInfo = ref<any>({}); // 群组信息
    // 数据接口
    interface IData {
        id: string; // id
        gmtCreate: string;   // 创建时间
        gmtModified: string; // 修改时间
        createdBy: string;   // 创建人id
        isDeleted: boolean;  // 是否删除
        userName: string;    // 创建人名称
        groupName: string;   // 群组名
        users: [],            // 群组成员
        member?: string,
    }

    /**
     * @description 获取群组列表
     */
    const handleGroupList = async(data: {userId: string, pageSize: number, pageNum: number}) =>{
        let list:any = [];
        await getMeetingGroupList(data)
            .then(res => {
                list = res.data.map((item: any) => {
                    // 获取成员名称并用逗号连接
                    item.members = item.users.map((user: any) => user.userName).join(',');
                    // 群组名称是否可编辑
                    item.isEdit = true;
                    return item
                });
            })
            .catch((err) => {})
            return list;
    }
    // 滚动加载
    const getDataOnScroll = async () => {
        // 中断处理
        if (!canLoadMore.value || isLoading.value) return;
        // 打开loading
        isLoading.value = true;
        // 延迟请求
        await new Promise((resolve) => setTimeout(resolve, 2000));
        // 发送请求
        const newData = await handleGroupList({userId: userInfo.value.userId, pageSize: page.value, pageNum: limit.value});
        // 若返回数据长度小于限制 停止加载
        if(newData.length < limit.value) {
            canLoadMore.value = false;
        }
        // 合并数据
        data.value = [...data.value, ...newData];
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

    /**
     * @description: 更新群组信息
     * @param {object} data 群组信息 {id: 群组id, groupName: 群组名称, users: 群组成员}
     * @return {*}
     */
    const updateGroupInfo = (data:{id: string; groupName: string; users: []}) =>{
        updateMeetingGroup(data)
            .then(res => {
                ElMessage.success('修改成功');
            })
            .catch(err => {})
            .finally(() =>{
                handleGroupList({userId: userInfo.value.userId, pageSize: page.value, pageNum: limit.value});
            })
    }
    /**
     * @description 修改群组名称
     * @param index 下标
     */
    const handleEditGroupName = (index: number, item: any) => {
        data.value[index].isEdit = true;
        updateGroupInfo({id: item.id, groupName: item.groupName, users: item.users});
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
            groupName: row.groupName,
        }
        addGroupVisible.value = true;
    }

    /**
     * @description 删除群组会议
     * @param id 要删除群组的id
     */
    const handleDeleteMeeting = (id: string,) => {
        ElMessageBox.confirm('是否确定删除群组', {
            customClass: 'delete-box'
        })
            .then(() => {
                deleteMeetingGroup({id}).then(res => {
                    handleGroupList({userId: userInfo.value.userId, pageSize: page.value, pageNum: limit.value});
                    ElMessage.success('删除成功!');
                }).catch(err => {});
            })
            .catch(() => {
                ElMessage.info('已取消删除！');
            })
    }

</script>
<style scoped lang="scss">
    .group {
        background-color: #f5f5f5;
        .theme {
            display: flex;
            justify-content: space-around;
            align-items: center;
            .left {
                width: 47rem;
                div {
                    width: 7.5rem;
                }
            }
            .center {
                width: 38.5rem;
                // 参会人 input内部样式
                .meeting-people {
                    width: 32rem;
                    ::v-deep(.el-input__prefix .el-input__icon) {
                        width: 20px;
                        height: 20px;
                        color: #3268DC;
                        background: #ECF2FF;
                    }

                    ::v-deep(.el-input__inner) {
                        font-size: .875rem;
                        letter-spacing: .04rem;
                        font-weight: 200;
                        cursor: pointer;
                        --el-input-placeholder-color: #3268DC;
                    }
                }
            }
            .left, .center {
                height: 2.5rem;
                border-radius: .5rem;
                background: #FFFFFF;
                box-shadow: 0 .1875rem .375rem 0 rgba(0, 0, 0, 0.08);
                display: flex;
                justify-content: space-between;
                align-items: center;
                /* 群组管理 修改群组名称输入框样式 */
                ::v-deep(.el-input__wrapper ){
                    box-shadow: none !important;
                }
            }
            .right {
                transition: transform 0.2s ease;
                user-select: none;
                &:active {
                    transform: scale(0.96);
                }
            }
            .right, .center div {
                width: 6.375rem;
            }
            .right, .left div, .center div {
                height: 2.5rem;
                line-height: 2.5rem;
                text-align: center;
                color: #FFFFFF;
                border-radius: .5rem;
                background: #409EFF;
                cursor: pointer;
            }
        }
        .content {
            width: 97.9375rem;
            height: 40.0625rem;
            border-radius: .9375rem;
            box-sizing: border-box;
            border: .1875rem solid rgba(18, 115, 219, 0.8);
            margin: 1.25rem auto;
            .title {
                height: 3rem;;
                div {
                    font-size: 1.2rem;
                    line-height: 1.75rem;
                }
            }
            .list-container {
                overflow-y: scroll;
                max-height: 34.4375rem;
                margin-right: 15px;
                .loading {
                    color: #666666;
                    font-size: 1.25rem;
                    text-align: center;
                    font-weight: 600;
                }
                &::-webkit-scrollbar {
                    width: 1.1rem;
                    border-radius: .9375rem;
                }
                /* 自定义滚动条轨道 */
                &::-webkit-scrollbar-track {
                    // background: #FFFFFF;
                    border-radius: .9375rem;
                }
                
                /* 自定义滚动条的滑块（thumb） */
                &::-webkit-scrollbar-thumb {
                    background: #EDEBEB;
                    border-radius: .9375rem;
                }
                .card-item {
                    height: 4.75rem;
                    border-radius: .625rem;
                    background: #FFFFFF;
                    box-shadow: 0 .1875rem .125rem 0 rgba(0, 0, 0, 0.04);
                    div {
                        position: relative;
                        font-size: 1rem;
                        font-weight: 350;
                        line-height: 1.25rem;
                        color: #666666;
                        &:last-child {
                            color: #409EFF;
                            cursor: pointer;
                        }
                        .ellipsis {
                            text-overflow: ellipsis;
                            overflow: hidden;
                            white-space: nowrap;
                        }
                        .el-icon {
                            margin-right: .625rem;
                            color: #409EFF;
                            font-size: 1.1rem;
                            cursor: pointer;
                        }
                        // 群组名称样式修改
                        .el-input {
                            flex: 0.7;
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
                    &:nth-child(3) {
                        width: 15.1875rem;
                        padding: 0 1rem;
                        flex: 1.5;
                    }
                    &:nth-child(4) {
                        width: 20.1875rem;
                        padding: 0 2rem;
                        flex: 1.5;
                    }
                }
            }
        }
        .delete-dialog { 
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