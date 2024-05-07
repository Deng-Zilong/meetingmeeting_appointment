import http from "@/request/http";

export const qwLogin = (data: {code: string}) => {
    return http.get("http://114.116.235.18:8080/meeting/user/info", data)
}
export const Login = (data: {username: string, password: string}) => {
    return http.get("http://114.116.235.18:8080/meeting/user/info", data)
}