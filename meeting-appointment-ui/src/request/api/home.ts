import http from "@/request/http";

// 查询今日会议情况
export const getTodayMeetingRecordData = (data: {userId: number | string}) => {
    return http.get("/meeting/index/todayMeetingRecord", data)
}