import { getRoomStatusData } from "@/request/api/home";
import { defineStore } from "pinia";
import { ref } from "vue";
// 会议室状态管理
export const meetingStatus = defineStore('meetingStatus', () => {
    let centerRoomName = ref<any>([]); // 会议室名称目录
    // 地址目录
    const addressArr:any = [
        { 
          id: 1,
          title: '广政通宝会议室',
          address: '西南裙一 3 F 一 广政通宝会议室'
        },
        { 
          id: 2,
          title: 'EN-2F-02 恰谈室会议室',
          address: '西南裙一 3 F 一 EN-2F-02 恰谈室会议室'
        },
        { 
          id: 3,
          title: 'EN-2F-03 恰谈室会议室',
          address: '西南裙一 3 F 一 EN-2F-03 恰谈室会议室'
        },
        { 
          id: 4,
          title: 'EN-3F-02 恰谈室会议室',
          address: '西南裙一 3 F 一 EN-3F-02 恰谈室会议室'
        },
        { 
          id: 5,
          title: 'EN-3F-03 恰谈室会议室',
          address: '西南裙一 3 F 一 EN-3F-03 恰谈室会议室'
        }
     ]
    const getCenterRoomName = () => {
        getRoomStatusData()
            .then((res) => {
                centerRoomName.value = [...res.data.map((item: any) => {
                    item.address = addressArr.find((el: any) => item.id == el.id)?.address;
                    return item;
                })]
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