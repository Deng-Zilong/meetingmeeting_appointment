import http from '@/request/http'

/***************************************************操作会议室***********************************************************/

// 会议室禁用
export const getMeetingBanData = (data: {id: number, status: number, currentLevel: number}) => {
  return http.put("/meeting/update-status", data)
}

// 新增会议室
export const addRoomData = (data: { createdBy: string, roomName: string, location: string, capacity: number, status: number , equipment: string}) => {
  return http.post("/meeting/add-meeting-room", data)
}

// 修改会议室
export const updateRoomData = (params: any) => {
  return http.put("/meeting/update-room", params)
}

// 删除会议室
export const deleteRoomDate = (data: { currentLevel: number, id: number }) => {
  return http.delete("/meeting/delete-meeting-room", data)
}

/***************************************************柱状图统计***********************************************************/

// 时间段频次统计   不要的！！！
export const getTimePeriodDate = () => {
  return http.get("/meeting/statistics/time-period")
}

// 会议室占用率统计
export const getRoomOccupancyDate = (data: { startDate?: string, endDate?: string}) => {
  return http.get("/meeting/statistics/meeting-room-occupancy", data)
}

// 会议室选择率统计
export const getRoomSelectionRate = (data: { startDate?: string, endDate?: string}) => {
  return http.get("/meeting/statistics/meeting-room-selection", data)
}


/***************************************************原始头部***********************************************************/

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
export const getAllRecordData = (data: any) => {
  return http.get("/meeting/meeting-record/all-meeting-record", data)
}

/***************************************************管理设备***********************************************************/
// 查询设备
export const getDeviceData = (params: any) => {
  return http.get("/meeting/meetingDevice/device", params)
}
// 新增设备
export const addDeviceData = (params: any) => {
  return http.post("/meeting/meetingDevice/device", params)
}
// 修改设备
export const editDeviceData = (params: any) => {
  return http.put("/meeting/meetingDevice/device", params)
}
// 删除设备
export const delDeviceData = (params: any) => {
  return http.delete("/meeting/meetingDevice/device", params)
}
// 修复设备（禁用启用）
export const setStatusData = (params: any) => {
  return http.put("/meeting/meetingDevice/statusDevice", params)
}
// 查看设备报损 详情信息
export const getInfoData = (params: any) => {
  return http.get("/meeting/meetingDevice/info", params)
}
// 新增报损信息
export const addBreakInfoData = (params: any) => {
  return http.post("/meeting/meetingDevice/info", params)
}

// 批量删除设备
export const delAllDeviceData = (params: any) => {
  return http.delete("/meeting/meetingDevice/devices", params)
}