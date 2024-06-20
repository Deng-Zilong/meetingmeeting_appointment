<template>
    <div id="container" class="home">
        <van-notice-bar
            left-icon="volume-o"
            :text="notice"
            :scrollable="isNoticeRoll"
            @mouseenter="mouseenter"
            @mouseleave="mouseleave"
        />
        <div class="screen-box">
          <h1 class="title">{{ gaugeData.name}}: {{ gaugeData.value }}</h1>
          <div class="main-table my-main-scrollbar">
            <div class="room-items" :class="item.status === 1 ? 'pointer' : 'ban'" v-for="item in useMeetingStatus.centerRoomName" @click="handleRoomClick(item)">
                <p class="name">{{ item.roomName }}</p>
                <p class="state">{{ item.roomStatusLabel }}</p>
            </div>
          </div>
        </div>
        <div class="bottom-box">
            <TodyMeeting/>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { getCenterAllNumberData, getNoticeData } from '@/request/api/home';
    import { ref, onMounted } from 'vue';
    import { useRouter } from 'vue-router'
    import { meetingStatus } from '@/stores/meeting-status'
    import TodyMeeting from '@/views/home/components/today-meeting.vue'

    const router = useRouter();
    const useMeetingStatus = meetingStatus();
    

    onMounted(() => {
        getNotice();
        getCenterAllNumber();
        useMeetingStatus.getCenterRoomName();
    })

    /******************************************* 公告 ***********************************/

    let notice = ref<string>('');
    /**
     * @description 查询所有公告
     */
    const getNotice = async () => {
        const res = await getNoticeData()
        notice.value = res?.data[0] ?? '';
    }
    const isNoticeRoll = ref<boolean>(true); // 通知栏是否开启滚动
    /**
     * @description 鼠标移入停止滚动
     */
    const mouseenter = () => {
        // touchstart
        isNoticeRoll.value = false;
    }
    /**
     * @description 鼠标移出开始滚动
     */
    const mouseleave = () => {
        // touchend
        isNoticeRoll.value = true;
    }

    /******************************************* 会议室大屏 ***********************************/
    const gaugeData = ref<any>({});
    /**
     * @description 今日中心会议总次数
    */
    const getCenterAllNumber = () => {
        getCenterAllNumberData()
            .then((res) => {
                gaugeData.value = {
                    value: res.data,
                    name: '中心会议总次数'
                }
            })
            .catch((err) => {})
    }
    const handleRoomClick = (item: any) => {
        if(item.status !== 0) {
            sessionStorage.setItem('meetingInfo', JSON.stringify({meetingRoomId: String(item.id)}));
            // router.push('/meetingAppoint')
            router.push({path: '/createMeeting'})

        }
        
    }
</script>

<style lang="scss" scoped>
    .home {
        display: flex;
        flex-direction: column;
        justify-content: center;
        background-color: #F7F8FA;
        padding-bottom: 0 !important;
        ::v-deep(.van-notice-bar) {
            color: #1273DB;
            border-radius: 5px;
            border: 1px solid rgba(18, 115, 219, 0.6);
            background-color: #fff;
            .van-notice-bar__content {
                font-size: .9rem;
            }
        }
        .screen-box {
            height: 22rem;
            padding-top: 10px;
            margin: .625rem 0;
            // background-image: url('@/assets/imgs/screen-bg.png');
            // background-size: 110% 110%;
            // background-position: center;
            background-color: aliceblue;
            border-radius: .3125rem;
            box-shadow: 0 0 1rem #6869694d;
            .title {
                font-size: 1.2rem;
                text-align: center;
                font-weight: bold;
                color: #1273DB;
            }
            .main-table {
                height: 85%;
                display: grid;
                grid-template-columns: 10rem 10rem;
                gap: 5px;
                justify-content: space-around;
                padding: 10px 0;
                overflow: auto;
                .room-items {
                    height: 4.6875rem;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-around;
                    align-items: center;
                    padding: .325rem 5px;
                    margin-bottom: .625rem;
                    border: .0625rem solid rgba(0, 102, 255, .5);
                    border-radius: 5px;
                    box-shadow: inset 0 0 1.875rem #1b7ef24d;
                    backdrop-filter: blur(.3125rem);
                    &:hover {
                        backdrop-filter: blur(.1rem);
                        filter: opacity(0.8); /* 鼠标悬停时模糊效果 */
                    }
                    .name {
                        font-size: .7rem;
                        color: #6A6A6A;
                    }
                    .state {
                        font-size: 1rem;
                        font-weight: bold;
                        color: #1273DB;
                    }
                }
                // main-items 会议室状态为空闲1 为小手，其他为 禁止
                .pointer {
                    cursor: pointer;
                }
                .ban {
                    cursor: not-allowed;
                }
            }
        }
    }
</style>