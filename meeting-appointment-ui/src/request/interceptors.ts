import type { AxiosError, InternalAxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
// import router from "../router";
// 请求拦截
export function ReqResolve(config: InternalAxiosRequestConfig) {
  if (config.url === "/login" || config.url === "/codeImage") {
    return config;
  }
  const token = localStorage.getItem("token")
    ? localStorage.getItem("token")
    : "";
  // if (!token) {
  //   return Promise.reject({ code: 401, message: "登录已过期，请重新登录！" });
  // }
  /**
   * * 加上 token
   * ! 认证方案: JWT Bearer
  //  */
  config.headers.Authorization = token;
  return config;
}
// 请求拦截错误处理
export function ReqReject(error: AxiosError) {
  return error;
}
// 响应拦截
export function ResResolve(config: any) {
//   if (config.data.code == 401) {
//     // router.push("/403");
//   }
  if (config.status === 200) {
    if (config.data.code == 200) {
        return config;
    } else {
        ElMessage.warning(config.data.msg);
        return Promise.reject(config);
    }
  }
}
// 相应拦截错误处理
export function ResReject(error: AxiosError) {
  // router.push("/500");
  return Promise.reject(error);
}
