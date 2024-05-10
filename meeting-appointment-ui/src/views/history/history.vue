<template>
    <div class="container history">
        <div class="theme" > 会议历史记录查看 </div>
        <div class="content">
            <div class="title">
                <div>会议室名称</div>
                <div>发起人</div>
                <div>会议时间</div>
                <div>会议主题</div>
                <div>参会人员</div>
                <div>会议状态</div>
                <div>操作</div>
            </div>
            <div class="list-container" >
                <el-timeline ref="timelineRef">
                    <el-timeline-item timestamp="4/12" placement="top" class="timeline-item">
                        <div v-for="(item, index) in data" :key="index" class="card-item">
                            <div>
                                <img src="@/assets/img/transmit-icon.png" alt="" class="transmit-icon" @click="transmitMeeting" v-show="item.status == '已结束'">
                                {{ item.name }}
                            </div>
                            <div>{{ item.create }}</div>
                            <div>{{ item.time }}</div>
                            <div><p class="ellipsis">{{ item.theme }}</p></div>
                            <div><p class="ellipsis">{{ item.attendees }}</p></div>
                            <div>{{ item.status }}</div>
                            <!-- <div @click="handleCancelMeeting('time', index)"><span v-show="item.status == '未开始'">{{ item.operate }}</span></div> -->
                            <div @click="handleCancelMeeting('time', index)"><span>{{ item.operate }}</span></div>
                        </div>
                    </el-timeline-item>
                    <div class="loading" v-show="fetchingData">数据加载中......</div>
                </el-timeline>
            </div>
        </div>
        <!-- <div class="meeting-dialog">
            <el-dialog
                v-model="isCancelMeeting"
                align-center
                :show-close="false"
                :close-on-click-modal="false"
            >
                <p class="meeting-text">是否确定取消会议？</p>
                <template #footer>
                <div class="dialog-footer">
                    <el-button size="large" @click="isCancelMeeting = false"> 取 消 </el-button>
                    <el-button size="large" type="primary" @click="handleConfirm"> 确 认 </el-button>
                </div>
                </template>
            </el-dialog>
        </div> -->
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
    </div>
</template>
<script setup lang="ts">
    import { ElMessage, ElMessageBox } from 'element-plus';
    import { reactive, ref } from 'vue';
    import { useInfiniteScroll } from '@vueuse/core'
    
    const timelineRef = ref(null); // 获取dom节点
    let limit = ref(10); // 默认限制条数 10
    let page = ref(1); // 默认页数 1
    let fetchingData = ref(false); // 控制数据加载中是否显示
    let canLoadMore = ref(true); // 是否 继续请求数据
    // 数据接口
    interface IData {
        name: string;
        create: string;
        time: string;
        theme: string;
        attendees: string;
        status: string;
        operate: string;
    }
    // 数据列表
    let data = reactive<IData[]>([
        {
            name: '哈哈', create: '哈哈', time: "15:00", theme: "EN-2F-02 恰谈室会议室", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", status:"已结束", operate:"取消会议"
        },
        {
            name: '哈哈', create: '哈哈', time: "15:00", theme: "EN-2F-02 恰谈室会议室", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", status:"已结束", operate:"取消会议"
        },
        {
            name: '哈哈', create: '哈哈', time: "15:00", theme: "EN-2F-02 恰谈室会议室", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", status:"已结束", operate:"取消会议"
        },
        {
            name: '哈哈', create: '哈哈', time: "15:00", theme: "EN-2F-02 恰谈室会议室", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", status:"已结束", operate:"取消会议"
        },
        {
            name: '哈哈', create: '哈哈', time: "15:00", theme: "EN-2F-02 恰谈室会议室", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", status:"已结束", operate:"取消会议"
        },
        {
            name: '哈哈', create: '哈哈', time: "15:00", theme: "EN-2F-02 恰谈室会议室", attendees: "EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室, EN-2F-02 恰谈室会议室", status:"已结束", operate:"取消会议"
        },
        
    ])
    // 滚动加载
    const getDataOnScroll = async () => {
        // 中断处理
        if (!canLoadMore.value || fetchingData.value) return;
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
        timelineRef, // 容器
        async () => {
            await getDataOnScroll();
        },
        {
            distance: 15, // 距离底部的长度
        }
    );


    /**
     * @description 打开取消会议弹窗
     * @param time 
     * @param index 
     */
    const handleCancelMeeting = (time: string, index: number) => {
        // isCancelMeeting.value = true;
        ElMessageBox.confirm('是否确定取消会议？', {
            // customClass: 'm-box'
        })
        .then(() => {
            ElMessage.success('取消成功');
        })
        .catch(() => {})
    }
    
    // // 取消会议弹窗开关
    // let isCancelMeeting = ref<boolean>(false);
    // /**
    //  * @description 确认取消会议
    //  */
    // const handleConfirm = () =>{}

    let isTransmitMeeting = ref<boolean>(false);
    let message = ref<string>('已成功复制到剪贴板');
    let address = ref<string>(`会议主题: 测试
发起人: 周颖
会议日期: 2024-04-26
会议时间: 16:00-16:15
会议地点: EN-2F-02 恰谈室
URL: https://it-web.jifuinfo.com/meeting_h5/#/login `)
    /**
     * @description 转发会议
     */
    const transmitMeeting = () => {
        isTransmitMeeting.value = true;
        navigator.clipboard.writeText(address.value).then(() => {
            ElMessage.success('复制成功！');
        })
        .catch(() => {
            message.value = "复制失败！"
            ElMessage.warning("复制失败！")
        })
    }

</script>

<style scoped lang="scss">
    .container {
        background-color: #f5f5f5;
        .theme {
            width: 101rem;
            height: 4.375rem;
            font-size: 1.5625rem;
            font-weight: 500;
            text-align: center;
            line-height: 4.375rem;
            letter-spacing: 0.05em;
            color: #3268DC;
            border-radius: .9375rem;
            background: #FFFFFF;
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
                    color: #3A3A3A;
                    font-size: 1.2rem;
                    line-height: 1.75rem;
                }
            }
            .list-container {
                .el-timeline {
                    overflow-y: scroll;
                    max-height: 34.4375rem;
                    margin-right: 15px;
                    .loading {
                        color: #666666;
                        font-size: 1.25rem;
                        text-align: center;
                        font-weight: 600;
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
                    height: 4.75rem;
                    border-radius: .625rem;
                    background: #FFFFFF;
                    box-shadow: 0 .1875rem .125rem 0 rgba(0, 0, 0, 0.04);
                    div {
                        font-size: 1rem;
                        font-weight: 350;
                        line-height: 1.25rem;
                        color: #666666;
                        &:nth-child(1) {
                            position: relative;
                        }
                        &:nth-child(7) {
                            color: #F56C6C;
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
                    &:nth-child(4) {
                        padding: 0 .375rem;
                        width: 11.5rem;
                    }
                    &:nth-child(5) {
                        width: 28.1875rem;
                        flex: 3;
                    }
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