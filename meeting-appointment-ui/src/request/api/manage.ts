import http from '@/request/http'

// 添加/删除管理员
export const getAdmin = (data: {userId: string, level: number }) => {
  return http.get("/meeting/user/updateLevel", data)
}

// 查询所有的会议记录
export const getAllRecord = (data: { page: number, limit: number }) => {
  return http.get("/meeting/meetingRecord/selectAllMeetingRecord", data)
}
