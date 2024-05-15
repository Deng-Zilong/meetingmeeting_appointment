import http from '@/request/http'

// 会议室禁用
export const addMeetingRecord = (data: {id: number, status: number}) => {
  return http.put("/meeting/index/addMeetingRecord", data)
}