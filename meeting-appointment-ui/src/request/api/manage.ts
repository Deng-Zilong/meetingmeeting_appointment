import http from '@/request/http'

// 会议室禁用
export const getMeetingBan = (data: {id: number, status: number}) => {
  return http.put("/meeting/updateStatus", data)
}

// 查询所有管理员
export const getSelectAdminData = () => {
  return http.get("/meeting/user/selectAdmin")
}

// 添加/删除管理员
// export const updateLevelData = (data: {userId: string, level: number }) => {
//   return http.put("/meeting/user/updateLevel", data)
// }

// 查询所有的会议记录
export const getAllRecordData = (data: { page: number, limit: number }) => {
  return http.get("/meeting/meetingRecord/selectAllMeetingRecord", data)
}
