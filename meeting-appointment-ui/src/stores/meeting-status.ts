import { getRoomStatusData } from "@/request/api/home";
import { defineStore } from "pinia";
import { ref } from "vue";
// 会议室状态管理
export const meetingStatus = defineStore('meetingStatus', () => {
  let centerRoomName = ref<any>([]); // 会议室名称目录
  const meetingRoomState = ref<any>([  // 后台会议室使用情况
    {value: 0, label: '暂停使用'},
    {value: 1, label: '空闲'},
    {value: 2, label: '使用中'},
  ]);
    const getCenterRoomName = () => {
        getRoomStatusData()
            .then((res) => {
                centerRoomName.value = res.data.map((item: any) => {
                  item.roomStatusLabel = meetingRoomState.value[item.status].label;
                  return item;
                });
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