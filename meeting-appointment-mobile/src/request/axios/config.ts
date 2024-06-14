import axios from "axios";
import type { AxiosInstance } from "axios";
import { ReqResolve, ReqReject, ResResolve, ResReject } from "./interceptors";

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
//   baseURL: '/api',
  timeout: 10000,
  headers: {
    // 传参方式json
    "Content-Type": "application/json;charset=UTF-8",
    "access-control-allow-origin": "*",
  },
});
// 请求拦截
service.interceptors.request.use(ReqResolve, ReqReject);
// 响应拦截
service.interceptors.response.use(ResResolve, ResReject);

export default service;