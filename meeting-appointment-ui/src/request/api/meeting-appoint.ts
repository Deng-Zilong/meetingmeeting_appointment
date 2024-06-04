import http from '@/request/http'

// 创建会议
export const addMeetingRecord = (data: {createdBy: string, meetingRoomId: string, title: string, status: string, description: string,startTime: string, endTime: string, users: any[]}) => {
  return http.post("/meeting/index/meeting-record", data)
}
// 修改会议
export const updateMeetingRecord = (data: {id: string, createdBy: string, meetingRoomId: string, title: string, status: string, description: string,startTime: string, endTime: string, users: any[]}) => {
    return http.put("/meeting/index/meeting-record", data)
}

// 根据时间段查询可用会议室
export const availableMeetingRooms = (data: {startTime: string, endTime: string}) => {
    return http.get("/meeting/create-meeting/available-meeting-rooms", data)
}

// 提示最近三次创建人创建会议的信息
export const meetingRecordPrompt = (data: {userId: string}) => {
    return http.get("/meeting/index/meeting-record-prompt", data)
}