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
        userId: string,
        accessToken: string,
        name: string,
        level: number,
    }
    // 用户信息初始化
    const initUserInfo = {
        userId:'',
        accessToken: '',
        name: '',
        level: 0,
    }
    // 用户信息
    let userInfo = reactive<IUserInfo>({...initUserInfo})

    /**
     * @description 企业微信扫码登录
     * @param code 企业微信返回code
     */
    const getQWUserInfo= (code: string)=>{
        qwLogin({code})
                .then((res: any) => {
                    userInfo = res.data;
                    ElMessage.success('登陆成功!');
                })
                .catch((err: any) => {
                    router.replace('/login');
                    return err;
                })
    }

    /**
     * @description 账号密码登录
     * @param username 用户名
     * @param password 密码
     */
    const getUserInfo= (data:{name: string, password: string, uuid: string, code: string})=>{
        Login(data)
                .then((res: any) => {
                    localStorage.setItem('userInfo', JSON.stringify(res.data));
                    userInfo = res.data;
                    ElMessage.success('登陆成功!'); 
                    router.push('/home');
                })
                .catch((err: any) => {
                    // 临时使用
                    return err;
                })
    }
    // 重置用户信息
    const resetUserInfo = () => {
        Object.assign(userInfo, initUserInfo);
        delete localStorage.userInfo;
    }
    // 以对象的格式把state和action return 出去
    return {
        userInfo,
        getQWUserInfo,
        getUserInfo,
        resetUserInfo,
    }
},
    {
        // 持久化存储
        persist: true
    }
)
