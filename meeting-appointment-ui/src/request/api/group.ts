import http from "@/request/http";

export const getGroupList = (data: {userId: string}) => {
    return http.get("/meeting/meetingGroup/getMeetingGroupList", data)
}

export const deleteMeetingGroup = (data: {id: number | string}) => {
    return http.delete("/meeting/meetingGroup/deleteMeetingGroup", data)
}

export const updateMeetingGroup = (data: {id: string, groupName: string, users: []}) => {
    return http.put("/meeting/meetingGroup/updateMeetingGroup", data)
}