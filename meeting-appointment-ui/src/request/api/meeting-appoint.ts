import http from '@/request/http'

// 创建会议
export const addMeetingRecord = (data: {createdBy: string, meetingRoomId: string, title: string, status: string, description: string,startTime: string, endTime: string, users: any[]}) => {
  return http.post("/meeting/index/addMeetingRecord", data)
}
// 修改会议
export const updateMeetingRecord = (data: {id: string, createdBy: string, meetingRoomId: string, title: string, status: string, description: string,startTime: string, endTime: string, users: any[]}) => {
    return http.put("/meeting/index/updateMeetingRecord", data)
}

// 根据时间段查询可用会议室
export const availableMeetingRooms = (data: {startTime: string, endTime: string}) => {
    return http.put("/meeting/createMeeting/availableMeetingRooms", data)
}