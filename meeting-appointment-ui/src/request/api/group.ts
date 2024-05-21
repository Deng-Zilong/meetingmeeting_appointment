import http from "@/request/http";
// 获取群组列表数据
export const getMeetingGroupList = (data: {userId: string, pageNum?: number, pageSize?: number, }) => {
    return http.get("/meeting/meeting-group/get-meeting-group-list", data)
}
// 删除群组
export const deleteMeetingGroup = (data: {id: number | string}) => {
    return http.delete("/meeting/meeting-group/delete-meeting-group", data)
}
// 修改群组信息（群组名称和群组人员）
export const updateMeetingGroup = (data: {id: string, groupName?: string, users?: []}) => {
    return http.put("/meeting/meeting-group/update-meeting-group", data)
}
// 添加群组
export const addMeetingGroup = (data: {groupName: string, createdBy: string, userName: string, users: []}) => {
    return http.post("/meeting/meeting-group/add-meeting-group", data)
}

// 获取群组成员数据
export const getGroupUserTree = () => {
    return http.get("/meeting/group-user-tree/get-group-user-tree")
}

// 根据群组成员名称模糊查询
export const likeByName = (data: {name: string}) => {
    return http.get("/meeting/group-user-tree/like-by-name", data)
}