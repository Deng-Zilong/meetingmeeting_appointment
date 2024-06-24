<template>
    <!-- 今日会议室预约情况 模块 -->
    <div class="today-meeting">
        <p class="meeting-title">今日会议室预约情况</p>
        <div class="visit-records my-main-scrollbar" v-if="tableData?.length" >
                <div v-for="(item) in tableData" :key="item.id" class="record-item">
                    <div
                        :class="{ 'top-line': true, 'type1': item.status === 0, 'type2': item.status === 1, 'type3': item.status === 2 }">
                        <div :class="{ 'tag': true }">
                            <!-- 0 未开始; 1 进行中; 2 已结束 -->
                            {{ ['未开始', '进行中',  '已结束'][item.status] }}
                        </div>
                        <div class="date">{{ item.date }}</div>
                    </div>
                    <div class="card">
                        <van-icon name="edit" @click="toEdit(item)" v-show="item.status === 0 && userId === item.createdBy" />
                        <p class="title">会议主题：{{ item.title }}</p>
                        <p>预定人： {{ item.adminUserName }}</p>
                        <p>时间： {{ item.time }}</p>
                        <p>会议室：{{ item.meetingRoomName }}</p>
                    </div>
                </div>
            </div>
        <div class="visit-records my-main-scrollbar" v-else>
            <van-empty description="暂无会议记录" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from "vue-router";
import { getTodayMeetingRecordData } from '@/request/api/home'
import dayjs from "dayjs";

const router = useRouter();
const userId = JSON.parse(localStorage.getItem('userInfo') || '{}').userId;

let tableData = ref<any[]>([]);
/**
 * @description 查询今日会议情况
 * @param {userId} 用户id
 */
const getTodayRecord = () => {
    getTodayMeetingRecordData({ userId })
        .then((res) => {
            tableData.value = res.data.map((item: any) => {
                item.date = dayjs(item.startTime).format('HH:mm');
                item.time = `${dayjs(item.startTime).format('HH:mm')} - ${dayjs(item.endTime).format('HH:mm')}`;
                return item;
            })
        })
        .catch((err) => { })
}
/**
 * @description: 编辑
 * @param {*} 行数据
 * @return {*}
 */
const toEdit = (item: any): any => {
    if (item.status !== 0 || item.createdBy !== userId) return;
    item.date = dayjs(item.startTime).format('YYYY-MM-DD');  // 获取日期
    sessionStorage.setItem('meetingInfo', JSON.stringify(item));
    router.push({ path: '/createMeeting' })
}
onMounted(() => {
    getTodayRecord()
})
</script>
<style lang='scss' scoped>
.today-meeting {
    .meeting-title {
        font-size: 1.2rem;
        text-align: center;
        font-weight: bold;
    }
    .visit-records {
        // height: 27.6rem;
        height: 47.3vh;
        padding-left: 15px;
        padding-right: 15px;
        padding-top: 28px;
        // height: calc(100% - 108px);
        // margin-bottom: 20px;
        overflow-y: auto;
        .record-item {
            position: relative;
        
            &:not(:last-child):after {
                content: '';
                position: absolute;
                left: 10px;
                top: 30px;
                height: calc(100% + 18px);
                border-left: 1px solid #ccc;
            }
        
            .top-line {
                position: relative;
                display: flex;
                flex-wrap: nowrap;
                align-items: center;
                height: 40px;
                padding-left: 25px;
                font-size: 24px;
        
                &::after {
                    content: '';
                    position: absolute;
                    width: 13px;
                    height: 13px;
                    left: 4px;
                    top: 50%;
                    transform: translateY(-50%);
                    border-radius: 50%;
                    background: #3FA40E;
                }
        
                .tag {
                    width: 45px;
                    height: 25px;
                    line-height: 25px;
                    text-align: center;
                    font-size: .7rem;
                    border-radius: 5px;
                    color: #fff;
                    background: #3FA40E;
                }
        
                .date {
                    height: 40px;
                    line-height: 40px;
                    margin-left: 20px;
                    color: #424E67;
                }
            }
        
            .type2 {
              .tag {
                background: #3B8CFF;
              }
              &::after {
                background: #3B8CFF;
              }
            }
            .type3 {
              .tag {
                background: #A8ABB2;
              }
              &::after {
                background: #A8ABB2;
              }
            }
        
            .card {
                padding: 21px 20px;
                border-radius: 8px;
                background-color: #fff;
                line-height: 28px;
                color: #424E67;
                font-size: 28px;
                font-weight: bold;
                //   margin-bottom: 28px;
                //   margin-left: 40px;
                //   margin-top: 20px;
                margin-bottom: 15px;
                margin-left: 25px;
                margin-top: 10px;
                position: relative;
        
                //   .one-line {
                //     display: flex;
                //     flex-wrap: nowrap;
                //     justify-content: space-between;
                //     margin-bottom: 20px;
                //   }
                //   .doctor, .two-line {
                //     overflow: hidden;
                //     white-space: nowrap;
                //     text-overflow: ellipsis;
                //   }
                .title {
                    font-weight: bold;
                }
                :deep().van-icon-edit {
                    position: absolute;
                    top: 5px;
                    right: 8px;
                    font-size: 1.3rem;
                    color: #3B8CFF;
                    cursor: pointer;
                    transition: transform 0.2s ease;
                    &:hover{
                        transform: scale(1.1);
                    }
                }
            }
        }
    }
}

</style>