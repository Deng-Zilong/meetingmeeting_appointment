import http from '@/request/http'

// 查询未被禁用的会议室
export const getUsableRoomData = (data: { currentLevel: number }) => {
  return http.get("/meeting/index/allRoom", data)
}

// 会议室禁用
export const getMeetingBanData = (data: {id: number, status: number, currentLevel: number}) => {
  return http.put("/meeting/updateStatus", data)
}

// 上传公告
export const addNoticeData = (data: { currentLevel: number, currentUserId: string, substance: string }) => {
  return http.post("/meetingNotice/addNotice", data)
}

// 查询所有管理员
export const getSelectAdminData = (data: { currentLevel: number }) => {
  return http.get("/meeting/user/selectAdmin", data)
}

// 删除管理员
export const deleteAdminData = (data: { userId: string }) => {
  return http.put("/meeting/user/deleteAdmin", data)
}

// 新增管理员
export const addAdminData = (data: { userIds: any[] }) => {
  return http.put("/meeting/user/addAdmin", data)
}

// 查询所有的会议记录
export const getAllRecordData = (data: { pageNum?: number, pageSize?: number, currentLevel: number }) => {
  return http.get("/meeting/meetingRecord/selectAllMeetingRecord", data)
}
