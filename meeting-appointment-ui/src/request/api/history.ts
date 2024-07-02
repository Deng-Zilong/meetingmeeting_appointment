import http from "@/request/http";
// 获取历史会议记录
export const getHistoryList = (data: any) => {
    return http.get("/meeting/meeting-record/all-meeting-record", data)
}
// 取消会议
export const cancelMeetingRecord = (data: {userId: string, meetingId: number}) => {
    return http.put("/meeting/index/cancel-meeting-record", data)
}

// 查询部门列表
export const getDepartmentList = () => {
    return http.get("/meeting/index/meeting-record-department")
}

// 新增/编辑会议纪要 excel
export const addMeetingMinutes = (data: {id?: number | undefined, userId: string, minutes: string, meetingRecordId: number, remark: string}) => {
    return http.post("/meeting/minutes", data)
}

// 获取会议纪要 excel
export const getMeetingMinutes = (data: {userId: string, meetingRecordId: number}) => {
    return http.get("/meeting/minutes", data)
}

// 获取查询会议纪要 Word
export const getMeetingWord = (data: {userId: string, meetingRecordId: number}) => {
    return http.get("/meeting/word", data)
}

// 新增/编辑会议纪要 Word
export const addMeetingWord = (data: {userId: string, meetingRecordId: number, meetingWordDTOList: any[]} ) => {
    return http.post("/meeting/word", data)
}

// 删除会议纪要 Word或excel的计划
export const deleteWordOrPlan = (data: {contentId?: any, planId?: any} ) => {
    return http.delete("/meeting/wordOrPlan", data)
}

// 导出历史会议记录（单条，后可能批量导出）
export const recordExport = (type: number, operation: number, data: any[]) => {
    const userId = JSON.parse(localStorage.getItem("userInfo") as string).userId;
    return http.post(`/meeting/meeting-record/record-export/${userId}/${type}/${operation}`, data, "blob")
}

// 导出历史会议记录（单条，后可能批量导出）
export const recordPreview = (type: number, operation: number, data: any[]) => {
    return http.post(`/meeting/meeting-record/wordOrExcelPreview/${type}/${operation}`, data)
}
