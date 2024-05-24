import { getRoomStatusData } from "@/request/api/home";
import { defineStore } from "pinia";
import { ref } from "vue";
// 会议室状态管理
export const meetingStatus = defineStore('meetingStatus', () => {
    let centerRoomName = ref<any>([]); // 会议室名称目录
    const getCenterRoomName = () => {
        getRoomStatusData()
            .then((res) => {
                centerRoomName.value = res.data
            })
            .catch((err) => {})
    }
    return {
        centerRoomName,
        getCenterRoomName,
    }
},
{
    // 持久化存储
    persist: true
})