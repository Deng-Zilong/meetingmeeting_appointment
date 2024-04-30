// 管理用户数据
import {defineStore} from 'pinia'
import {reactive, ref} from 'vue'
import { Login, qwLogin } from '@/request/api/login'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
 
export const useUserStore = defineStore('user',()=>{
    // 声明路由
    const router = useRouter()
    interface IUserInfo{
        userId: string | number,
        access_token: string,
        username: '',
        img: '',
    }
    // 管理用户数据的state
    let userInfo = reactive<IUserInfo>({
        userId:'',
        access_token: '',
        username: '',
        img: '',
    })

    /**
     * @description 企业微信扫码登录
     * @param code 企业微信返回code
     */
    const getQWUserInfo= (code: string)=>{
        qwLogin({code})
                .then((res: any) => {
                    console.log(res, "request, 企业微信扫码登录");
                    userInfo = res.data.data;
                    ElMessage.success('登陆成功!');
                })
                .catch((err: any) => {
                })
    }

    /**
     * @description 账号密码登录
     * @param username 用户名
     * @param password 密码
     */
    const getUserInfo= (username: string, password: string)=>{
        Login({username, password})
                .then((res: any) => {
                    console.log(res.data.data, "request, 账号密码登录");
                    userInfo = res.data.data;
                    ElMessage.success('登陆成功!'); 
                    router.push('/home');
                })
                .catch((err: any) => {
                    // 临时使用
                    userInfo.access_token = '11111';
                    router.push('/home');
                    return err;
                })
    }
    // 以对象的格式把state和action return 出去
    return {
        userInfo,
        getQWUserInfo,
        getUserInfo
    }
},
    {
        // 持久化存储
        persist: true
    }
)
