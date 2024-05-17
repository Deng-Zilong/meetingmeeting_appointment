import http from "@/request/http";

// 查询今日会议情况
export const getTodayMeetingRecordData = (data: {userId: number | string}) => {
    return http.get("/meeting/index/today-meeting-record", data)
}

// 删除会议记录
export const getDeleteMeetingRecordData = (data: { userId: string, meetingId: number }) => {
  return http.delete("/meeting/index/delete-meeting-record",data)
}

// 查询今日中心会议总次数
export const getCenterAllNumberData = () => {
  return http.get("/meeting/index/query-record-rumber")
}

// 查询当日时间段占用情况
export const getTimeBusyData = () => {
  return http.get("/meeting/index/is-busy")
}

// 查询会议室状态
export const getRoomStatusData = () => {
  return http.get("/meeting/index/meeting-room-status")
}

// 查询所有公告
export const getNoticeData = () => {
  return http.get("/meeting/meeting-notice/get-notice")
}