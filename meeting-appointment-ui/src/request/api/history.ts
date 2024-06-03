import http from "@/request/http";
// 获取历史会议记录
export const getHistoryList = (data: {userId: number | string, page: number, limit: number}) => {
    return http.get("/meeting/meeting-record/all-meeting-record", data)
}
// 取消会议
export const cancelMeetingRecord = (data: {userId: string, meetingId: number}) => {
    return http.put("/meeting/index/cancel-meeting-record", data)
}

// 新增/编辑会议纪要
export const addMeetingMinutes = (data: {id?: number, userId: string, minutes: string, meetingRecordId: number}) => {
    return http.post("/meeting/minutes", data)
}

// 获取会议纪要
export const getMeetingMinutes = (data: {userId: string, meetingRecordId: number}) => {
    return http.get("/meeting/minutes", data)
}