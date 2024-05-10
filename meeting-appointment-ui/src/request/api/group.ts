import http from "@/request/http";
// 获取群组列表数据
export const getGroupList = (data: {userId: string}) => {
    return http.get("/meeting/meetingGroup/getMeetingGroupList", data)
}
// 删除群组
export const deleteMeetingGroup = (data: {id: number | string}) => {
    return http.delete("/meeting/meetingGroup/deleteMeetingGroup", data)
}
// 修改群组信息（群组名称和群组人员）
export const updateMeetingGroup = (data: {id: string, groupName: string, users: []}) => {
    return http.put("/meeting/meetingGroup/updateMeetingGroup", data)
}
// 添加群组
export const addMeetingGroup = (data: {groupName: string, createdBy: string, userName: string, users: []}) => {
    return http.post("/meeting/meetingGroup/addMeetingGroup", data)
}

// 获取群组成员数据
export const getGroupUserTree = () => {
    return http.get("/meeting/groupUserTree/getGroupUserTree")
}

// 根据群组成员名称模糊查询
export const likeByName = (data: {name: string}) => {
    return http.get("/meeting/groupUserTree/likeByName", data)
}