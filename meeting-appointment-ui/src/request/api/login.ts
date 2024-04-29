import http from "@/request/http";

export const qwLogin = (data: {code: string}) => {
    return http.get("http://114.116.235.18:8080/login/logininfo", data)
}