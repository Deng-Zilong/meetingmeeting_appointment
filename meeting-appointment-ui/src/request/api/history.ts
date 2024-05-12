import http from "@/request/http";

export const getHistoryList = (data: {userId: number | string, page: number, limit: number}) => {
    return http.get("/meeting/meetingRecord/allMeetingRecord", data)
}