<template>
    <div class="login-container">
        <div class="left-background"></div>
        <div class="right-login">
            <!-- 账号登录 -->
            <div class="login-number" v-if="isShow">
                <div class="logo"></div>
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
                        <el-button type="primary" @click="submitForm(ruleFormRef)" v-loading="loginBtnLoading">
                            登录
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 扫码登录 -->
            <div id="login-scan" v-else>
            </div>

            <el-button type="primary" @click="changeLogin">
                点击{{ isShow == false ? '账号' : '扫码' }}登录
            </el-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, h } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { v4 as uuidv4 } from 'uuid';
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/request/api/login'
import { Md5 } from 'ts-md5';

// 用户信息
const userStore = useUserStore();
const ruleFormRef = ref<FormInstance>();
let loginBtnLoading = ref<boolean>(false);
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

// 账号切换
const isShow = ref(false)
const changeLogin = () => {
    isShow.value = !isShow.value;
    if (!isShow.value) {
        return nextTick(() => {
            code();
        })
    }
    changeCaptcha();
}
// 账号登录 
const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (!valid) return; 
        const { name, captcha } = ruleForm;
        const md5:any = new Md5();
        // md5 加密密码
        md5.appendAsciiStr(ruleForm.password);
        const password = md5.end();
        
        userStore.getUserInfo({ name, password, code: captcha, uuid: uuid.value });
        loginBtnLoading.value;
    })
}

onMounted(() => {
    code();
});

const code = () => {
    //@ts-ignore
    new WwLogin({
        "id": "login-scan",
        appid: 'ww942086e6c44abc4b',
        agentid: '1000002',
        login_type: 'CorpApp',
        redirect_uri:  'http%3A%2F%2F172.17.34.48%3A32375%2F%23%2Fhome',
        state: 'WWLogin',
        "lang": "zh",
    });
}
</script>

<style scoped lang="scss">
.login-container {
    display: flex;
    width: 100vw;
    height: 100vh;

    .left-background {
        width: 70vw;
        height: 100%;
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
            .logo {
                width: 14.3125rem;
                height: 4.6875rem;
                margin-right: -4.5rem;
                background: url(@/assets/img/logo.png) no-repeat;
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
}
</style>