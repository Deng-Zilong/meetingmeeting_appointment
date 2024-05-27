import http from '@/request/http'

// 查询未被禁用的会议室
export const getUsableRoomData = (data: { currentLevel: number }) => {
  return http.get("/meeting/index/all-room", data)
}

// 会议室禁用
export const getMeetingBanData = (data: {id: number, status: number, currentLevel: number}) => {
  return http.put("/meeting/update-status", data)
}

// 新增会议室
export const addRoomData = (data: {createdBy: string, roomName: string, location: string, capacity: number}) => {
  return http.post("/meeting/add-meeting-room", data)
}

// 删除会议室
export const deleteRoomDate = (data: { currentLevel: number, id: number }) => {
  return http.delete("/meeting/delete-meeting-room", data)
}

// 上传公告
export const addNoticeData = (data: { currentLevel: number, currentUserId: string, substance: string }) => {
  return http.post("/meeting/meeting-notice/add-notice", data)
}

// 查询所有管理员
export const getSelectAdminData = (data: { currentLevel: number }) => {
  return http.get("/meeting/user/select-admin", data)
}

// 删除管理员
export const deleteAdminData = (data: { userId: string }) => {
  return http.put("/meeting/user/delete-admin", data)
}

// 新增管理员
export const addAdminData = (data: { userIds: any[] }) => {
  return http.put("/meeting/user/add-admin", data)
}

// 查询所有的会议记录
export const getAllRecordData = (data: { pageNum?: number, pageSize?: number, currentLevel: number }) => {
  return http.get("/meeting/meeting-record/select-all-meeting-record", data)
}
