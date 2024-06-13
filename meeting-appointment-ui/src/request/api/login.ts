import http from "@/request/http";

// 获取企业微信二维码
export const getQrCode = () => {
    return http.get("/meeting/user/qr-code")
}

// 企业微信扫码登录
export const qwLogin = (data: {code: string, loginMethod: number}) => {
    return http.get("/meeting/user/info", data)
}

// 企业微信扫码登录
// export const qwLogins = (data: {code: string}) => {
//     return http.get("/meeting/user/infos", data)
// }

// 账号密码登录
export const Login = (data: {name: string, password: string, uuid: string, code: string}) => {
    return http.post("/meeting/user/login", data)
}

// 获取验证码
export const getCaptcha = (data: {uuid: string}) => {
    return http.get("/meeting/user/captcha.jpg", data, 'arraybuffer',)
}

// 退出登录
export const deleteInfo = (userId: string) => {
    return http.get(`/meeting/user/delete/${userId}`)
}