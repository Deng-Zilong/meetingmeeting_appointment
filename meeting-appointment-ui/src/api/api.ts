import http from "@/utils/http";

export const qwLogin = (data: {code: string}) => {
    return http.GET("http://114.116.235.18:8080/login/logininfo", data)
}