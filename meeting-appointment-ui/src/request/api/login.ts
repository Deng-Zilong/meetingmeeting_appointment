import http from "@/request/http";

// 企业微信扫码登录
export const qwLogin = (data: {code: string}) => {
    return http.get("http://114.116.235.18:8080/meeting/user/info", data)
}

// 账号密码登录
export const Login = (data: {username: string, password: string, uuid: string, code: string}) => {
    return http.post("/meeting/user/login", data)
}

// 获取验证码
export const getCaptcha = (data: {uuid: string}) => {
    return http.get("/meeting/user/captcha.jpg", data, 'arraybuffer',)
}