<template>
    <div class="container group">
        <div class="theme">
            <div class="left">
                <el-input v-model="input" style="width: 240px" placeholder="请输入" />
                <div>请输入群组名称</div>
            </div>
            <div class="center">
                <el-icon class="plus-icon"><Plus /></el-icon>
                <span></span>
                <div>添加人员</div>
            </div>
            <div class="right"> 创建成功</div>
        </div>
        <div class="content">
            <div class="title">
                <div>创建人</div>
                <div>创建时间</div>
                <div>群组名称</div>
                <div>群组人员</div>
                <div>操作</div>
            </div>
            <div class="list-container" ref="listRef">
                <div class="timeline-item">
                    <div v-for="(item, index) in data" :key="index" class="card-item">
                        <div>{{ item.create }}</div>
                        <div>{{ item.time }}</div>
                        <div v-if="item.isEdit"> 
                            <el-icon @click="editGroupName(index)"> <Edit /></el-icon>
                            {{ item.groupName }}
                        </div>
                        <div v-else>
                            <el-input v-model="groupName" @blur="handleEditGroupName(index, groupName)"/>
                        </div>
                        <div><el-icon @click="editAttendees"> <Edit /></el-icon> <p class="ellipsis">{{ item.attendees }}</p></div>
                        <!-- <div @click="handleCancelMeeting('time', index)"><span v-show="item.status == '未开始'">{{ item.operate }}</span></div> -->
                        <div @click="handleDeleteMeeting(index)"><span>{{ item.operate }}</span></div>
                    </div>
                </div>
                <div class="loading" v-show="fetchingData">数据加载中......</div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { ref, reactive } from 'vue'
    import { useInfiniteScroll } from '@vueuse/core'
import { ElMessage, ElMessageBox } from 'element-plus';

    const input = ref('');
    const listRef = ref(null); // 获取dom节点
    let limit = ref(10); // 默认限制条数 10
    let page = ref(1); // 默认页数 1
    let fetchingData = ref(false); // 控制数据加载中是否显示
    let canLoadMore = ref(true); // 是否 继续请求数据
    // 数据接口
    interface IData {
        create: string;
        time: string;
        groupName: string;
        attendees: string;
        operate: string;
        isEdit: boolean
    }
    // 数据列表
    let data = reactive<IData[]>([
        {
            groupName: '哈哈', create: '哈哈', time: "15:00", attendees: "EN-2F-02 恰谈室会议室,", operate:"删除", isEdit: true,
        },
        {
            groupName: '哈哈', create: '哈哈', time: "15:00", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", operate:"删除", isEdit: true,
        },
        {
            groupName: '哈哈', create: '哈哈', time: "15:00", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", operate:"删除", isEdit: true,
        },
        {
            groupName: '哈哈', create: '哈哈', time: "15:00", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", operate:"删除", isEdit: true,
        },
        {
            groupName: '哈哈', create: '哈哈', time: "15:00", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", operate:"删除", isEdit: true,
        },
        {
            groupName: '哈哈', create: '哈哈', time: "15:00", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", operate:"删除", isEdit: true,
        },
        
    ])
    // 滚动加载
    const getDataOnScroll = async () => {
        // 中断处理
        if (!canLoadMore.value || fetchingData.value) return;
        console.log(11111)
        fetchingData.value = true;
        // 延迟请求
        await new Promise((resolve) => setTimeout(resolve, 2000));
        // 发送请求
        // const newPhotos = await getPhotos(page.value, limit.value);
        // 返回数据
        const newData = reactive<IData[]>([]);
        // 返回数组长度
        const length = ref<number>(newData.length)
        // 若返回数据长度小于限制 停止加载
        if(length < limit) {
            canLoadMore.value = false;
        }
        data = [...data, ...newData];
        page.value++;
        fetchingData.value = false;
    };
    // 滚动加载插件
    useInfiniteScroll(
        listRef, // 容器
        async () => {
            await getDataOnScroll();
        },
        {
            distance: 15, // 距离底部的长度
        }
    );

    let groupName = ref<string>('');
    /**
     * @description 修改群组名称
     * @param index 下标
     */
    const editGroupName = (index:number) => {
        data[index].isEdit = false;
    }
    const handleEditGroupName = (index: number, name: string) => {
        data[index].isEdit = true;
        data[index].groupName = name;
        ElMessage.success('修改成功');
        
    }
    const editAttendees = () => {}

    /**
     * @description 删除群组会议
     * @param index 要删除会议下标
     */
    const handleDeleteMeeting = (index: number) => {
        ElMessageBox.confirm('是否确定删除群组', {
            customClass: 'delete-box'
        })
            .then(() => {
                data.splice(index, 1);
                ElMessage.success('删除成功');
            })
            .catch(() => {})
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
                width: 55rem;
                div {
                    width: 12.5rem;
                }
            }
            .center {
                width: 32.5rem;
                padding-left: .625rem;
                .plus-icon {
                    width: 1.875rem;
                    height: 1.875rem;
                    color: #409EFF;
                    background: #ECF2FF;
                    cursor: pointer;
                }
            }
            .left, .center {
                height: 2.8125rem;
                border-radius: .5rem;
                background: #FFFFFF;
                box-shadow: 0 .1875rem .375rem 0 rgba(0, 0, 0, 0.08);
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .right {
                transition: transform 0.2s ease;
                user-select: none;
                &:active {
                    transform: scale(0.96);
                }
            }
            .right, .center div {
                width: 9.375rem;
            }
            .right, .left div, .center div {
                height: 2.8125rem;
                line-height: 2.8125rem;
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
                        &:nth-child(4) {
                            width: 28.1875rem;
                            flex: 3;
                        }
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
                    &:nth-child(4) {
                        width: 27.1875rem;
                        padding: 0 4rem;
                        flex: 3;
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