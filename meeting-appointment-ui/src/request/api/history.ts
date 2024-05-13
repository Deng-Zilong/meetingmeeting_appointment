import http from "@/request/http";
// 获取历史会议记录
export const getHistoryList = (data: {userId: number | string, page: number, limit: number}) => {
    return http.get("/meeting/meetingRecord/allMeetingRecord", data)
}
// 取消会议
export const cancelMeetingRecord = (data: {userId: string, meetingId: number}) => {
    return http.put("/meeting/index/cancelMeetingRecord", data)
}