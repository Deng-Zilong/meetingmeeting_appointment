<template>
    <div 
        class="login-container"
        v-loading="allLoading"
        element-loading-text="正在登录..."
        element-loading-background="rgba(100, 100, 100, 0.8)"
    >
        <div class="left-background"></div>
        <div class="right-login">
            <!-- 账号登录 -->
            <div class="login-number" v-if="isShow">
                <div class="logo-box">
                    <div class="logo"></div>
                    <!-- 切换二维码登录 -->
                    <div class="right-icon" @click="changeLogin"></div>
                </div>
                <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules">
                    <!-- 域名 -->
                    <el-form-item prop="name">
                        <el-input v-model="ruleForm.name" placeholder="账号" />
                    </el-form-item>
                    <!-- 密码 -->
                    <el-form-item prop="password">
                        <el-input v-model="ruleForm.password" type="password" placeholder="密码" />
                    </el-form-item>
                    <!-- 验证码 -->
                    <el-form-item prop="captcha">
                        <el-row :gutter="20">
                            <el-col :span="12"><el-input v-model="ruleForm.captcha" @keyup.enter="submitForm(ruleFormRef)"></el-input></el-col>
                            <el-col :span="12">
                                <div class="captcha" @click="changeCaptcha">
                                    <el-image :src="imgUrl" draggable="false" />
                                </div>
                            </el-col>
                        </el-row>
                    </el-form-item>
                    <!-- 登录 -->
                    <el-form-item>
                        <el-button type="primary" @click="submitForm(ruleFormRef)" :loading="loginBtnLoading">
                            登录
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 扫码登录 -->
            <div id="login-scan" class="login-qrcode" v-else>
                <!-- <img src="@/assets/img/switch-arrow.png" alt="" > -->
                <!-- <div class="switch-arrow" @click="changeLogin"></div> -->
                <iframe :src="iframeSrc" frameborder="0" width="100%" height="100%" :element-loading-text="iframeLoading"></iframe>

                <!-- <iframe :src="iframeSrc" frameborder="0" width="100%" height="100%" :element-loading-text="iframeLoading" v-if="isIframeSrc"></iframe> -->
                <!-- <el-skeleton style="width: 260px" v-else >
                    <template #template>
                        <el-skeleton-item variant="p" style="width: 10%; margin-right: .625rem" />
                        <el-skeleton-item variant="p" style="width: 65%" />
                        <el-skeleton-item variant="image" style="width: 260px; height: 260px" />
                        <div style="padding: 10px">
                            <el-skeleton-item variant="text" style="width: 85%" />
                            <el-skeleton-item variant="text" style="width: 55%" />
                        </div>
                    </template>
                </el-skeleton> -->
            </div>

            <el-button type="primary" @click="changeLogin" :loading="loginBtnLoading" v-show="!isShow">
                点击账号登录
            </el-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, h } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import { v4 as uuidv4 } from 'uuid';
import { useUserStore } from '@/stores/user'
import { Login, getCaptcha, getQrCode, qwLogin } from '@/request/api/login'
import { Md5 } from 'ts-md5';
import { useRouter, useRoute  } from 'vue-router';

const router = useRouter();
const route = useRoute();

// 用户信息
const userStore = useUserStore();
const ruleFormRef = ref<FormInstance>();
let loginBtnLoading = ref<boolean>(false);

let iframeSrc = ref(''); // 二维码地址
let iframeLoading = ref(true); // 二维码loading
let isIframeSrc = ref(true); // 是否获取到二维码

// 登录验证
const ruleForm = reactive({
    name: '',
    password: '',
    captcha: '',
})

const rules = reactive<FormRules<typeof ruleForm>>({
    name: [
        { required: true, message: "请输入域名", trigger: "blur" },
        { min: 1, message: "域名不可为空", trigger: "blur" }
    ],
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 1,  message: "密码不可为空", trigger: "blur" },
    ],
    captcha: [
        { required: true, message: "请输入验证码", trigger: "blur" },
        { min: 4, max: 4, message: "验证码必须是4位字符", trigger: "blur" }
    ]
})


let imgUrl = ref(); // 验证码
let uuid = ref(''); // uuid
/**
 * @description 获取验证码
 */
const changeCaptcha = async() => {
    uuid.value = uuidv4();
    const res = await getCaptcha({ uuid: uuid.value });
    if (res.data) {
        // 处理验证码
        imgUrl.value = 'data:image/png;base64,' + btoa(
            new Uint8Array(res.data).reduce((data, byte) => data + String.fromCharCode(byte), '')
        )
    } else {
        ElMessage.error('获取验证码失败!')
    }
}

const isShow = ref(false); // 登录方式切换 false扫码 true账号
/**
 * @description 切换登录方式
 */
const changeLogin = () => {
    isShow.value = !isShow.value;
    if (!isShow.value) {
        return nextTick(() => {
            getCode();
        })
    }
    changeCaptcha();
}

// 账号登录 
const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate(async(valid) => {
        if (!valid) return;
        // 打开loading
        loginBtnLoading.value = true;
        // 解构数据
        const { name, captcha } = ruleForm;
        // md5 加密密码
        const md5:any = new Md5();
        md5.appendAsciiStr(ruleForm.password);
        const password = md5.end();
        // 登录请求
        await Login({ name, password, code: captcha, uuid: uuid.value })
                .then((res: any) => {
                    localStorage.setItem('userInfo', JSON.stringify(res.data));
                    ElMessage.success('登陆成功!'); 
                    router.push('/home');
                })
                .catch((err: any) => {
                    changeCaptcha();
                })
                .finally(() => {
                    // 关闭 button loading
                    loginBtnLoading.value = false;
                })
        
    })
}


let allLoading = ref<boolean>(false);
onMounted(() => {
    // 展示扫码登录
    getCode();

    /********************* 免密登录 ***************************/

    allLoading.value = true; // 开启loading
    
    const urlParams = new URLSearchParams(window.location.search); // 获取url参数
    const code = urlParams?.get('code') as string; // 获取code
    const loginMethod: number = 1; // 测试登录

    // 若code不存在 关闭loading
    if (!code) {
        return allLoading.value = false;
    }
    
    qwLogin({code, loginMethod})
    .then(res => {
        localStorage.setItem('userInfo', JSON.stringify(res.data));
        setTimeout(() => {
            location.href = `/#/home`
        }, 1000);
    })
    .catch(error => {})
    .finally(() => {
        allLoading.value = false;
    })
});
// 获取二维码
const getCode = () => {
    // 前端生成二维码
    //@ts-ignore
    // new WwLogin({
    //     "id": "login-scan",
    //     appid: 'ww942086e6c44abc4b',
    //     agentid: '1000002',
    //     login_type: 'CorpApp',
    //     redirect_uri:  'http%3A%2F%2F172.17.34.48%3A32375%2F%23%2Fhome',
    //     state: 'WWLogin',
    //     "lang": "zh",
    // });

    // 后端返回二维码
    getQrCode()
        .then(res => {
            iframeSrc.value = res.data.url;
        })
        .catch(err => {
            isIframeSrc.value = false;
        })
        .finally(() => {
            iframeLoading.value = false;
        });
    
}
</script>

<style scoped lang="scss">
.login-container {
    display: flex;
    width: 100vw;
    height: 100vh;
    ::v-deep(.el-loading-spinner .path) {
        stroke: #fff;
        opacity: 0.7;
    }
    ::v-deep(.el-loading-text){
        color: #fff;
        font-size: 1.2rem;
        letter-spacing: .2rem;
        opacity: 0.7;
    }

    .left-background {
        width: 70vw;
        height: 100%;
        display: inline-block;
        background: url(@/assets/img/login.png) no-repeat;
        background-size: 100% 100%;
    }

    .right-login {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 30vw;
        height: 100%;

        // 账号登录
        .login-number {
            .logo-box {
                display: flex;
                justify-content: space-between;
                // align-items:first baseline;
                .logo {
                    width: 14.3125rem;
                    height: 4.6875rem;
                    margin-right: -4.5rem;
                    background: url(@/assets/img/logo.png) no-repeat;
                }
                .right-icon {
                    width: 1.875rem;
                    height: 1.875rem;
                    background: url(@/assets/img/qrcode.png) no-repeat;
                    cursor: pointer;
                    &:hover {
                        background: url(@/assets/img/qr-code1.png) no-repeat;
                    }
                }
            }

            .captcha {
                .el-image {
                    width: 100%;
                    height: 2.125rem;
                    cursor: pointer;
                }
            }

            .el-button {
                width: 100%;
            }
        }

    }
    .login-qrcode {
          width: 400px;
          height: 400px;
          display: flex;
          justify-content: center;
          align-items: center;
          position: relative;
          .el-skeleton {
            text-align: center;
          }
        //   .switch-arrow {
        //     width: 1.25rem;
        //     height: 20px;
        //     position: absolute;
        //     top: -0.6875rem;
        //     right: 4.2rem;
        //     cursor: pointer;
        //     background: url(@/assets/img/switch-arrow.png) no-repeat;
        //     &:hover {
        //         background: url(@/assets/img/switch-arrow1.png) no-repeat;
        //     }
        //   }
      }
}
</style>