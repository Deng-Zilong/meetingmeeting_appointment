import type { AxiosError, InternalAxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
// import router from "../router";
// 请求拦截
export function ReqResolve(req: InternalAxiosRequestConfig) {
    if (req.url === "/meeting/user/login" || req.url === "/meeting/user/captcha.jpg" || req.url === "/meeting/user/info") { 
        return req;
    }
    const userInfo = JSON.parse(localStorage.getItem("userInfo") as string) 
    const token = userInfo.accessToken
        ? userInfo.accessToken
        : "";

    // if (!token) {
    //   return Promise.reject({ code: 401, message: "登录已过期，请重新登录！" });
    // }
    /**
     * * 加上 token
     * ! 认证方案: JWT Bearer
     */
    req.headers.Authorization = token;
    return req;
}
// 请求拦截错误处理
export function ReqReject(error: AxiosError) {
    return error;
}
// 响应拦截
export function ResResolve(res: any) {
    // 验证码不判断状态码
    if (res.config.url === "/meeting/user/captcha.jpg") {
        return res;
    }

    if (res.status === 200) {
        if (res.data.code === '00000') {
            return res.data;
        } else {
            ElMessage.error(res.data.msg);
            return Promise.reject(res);
        }
    }
}
// 响应拦截错误处理
export function ResReject(error: AxiosError) {
    // router.push("/500");
    return Promise.reject(error);
}
