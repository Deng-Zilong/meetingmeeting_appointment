<template>
    <div id="container" class="login">
        <div class="login-form">
            <van-form @submit="onSubmit" class="form" ref="formRef" >
                <van-cell-group inset>
                    <van-field
                        class="form-item"
                        v-model="username"
                        name="username"
                        placeholder="请输入用户名"
                        :rules="[{ required: true, message: '请填写用户名' }]"
                    />
                    <van-field
                        v-model="password"
                        type="password"
                        name="password"
                        placeholder="请输入密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                    />
                    <div class="captcha">
                        <van-field
                            v-model="captcha"
                            name="captcha"
                            placeholder="请输入验证码"
                            style="width: 8rem;"
                            :rules="[{ required: true, message: '请填写验证码' }]"
                        />
                        <div class="captcha-img" @click="handleCaptcha">
                            <img :src="imgUrl" alt="">
                        </div>
                    </div>
                </van-cell-group>
                <!-- <div style="margin: 16px;" class="form-btn">
                    <van-button @click="resetForm">
                        重 置
                    </van-button>
                    <van-button type="primary" native-type="submit">
                        提 交
                    </van-button>
                </div> -->
                <div style="margin: 16px;" >
                    <van-button round block type="primary" native-type="submit">
                        提 交
                    </van-button>
                    <van-button round block @click="resetForm" style="margin-top: .625rem">
                        重 置
                    </van-button>
                </div>
            </van-form>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from 'vue';
    import { useRouter } from 'vue-router';
    import { v4 as uuidv4 } from "uuid";
    import { Md5 } from 'ts-md5';
    import { showSuccessToast, showToast } from 'vant';
    import type { FormInstance } from 'vant';
    import { getCaptcha, Login } from '@/request/api/login';

    const router = useRouter();

    const formRef = ref<FormInstance>();
    const username = ref('');
    const password = ref('');
    const captcha = ref('');
    const imgUrl = ref('');
    let uuid = ref(uuidv4());

    /**
     * @description 获取验证码
     */
    const handleCaptcha = async() => {
        uuid.value = uuidv4();
        const res = await getCaptcha({uuid: uuid.value});
        if (res.data) {
            // 处理验证码
            imgUrl.value = 'data:image/png;base64,' + btoa(new Uint8Array(res.data).reduce((data, byte) => data + String.fromCharCode(byte), ''))
        } else {
            // 获取验证码失败
            imgUrl.value = '';
            showToast("获取验证码失败!");
        }
    }

    /**
     * @description 提交
     * @param values 提交数据
     */
    const onSubmit = async (values: any) => {
        const { username, captcha } = values;
        console.log(values);
        
        // md5 加密密码
        const md5:any = new Md5();
        // console.log(md5.appendAsciiStr(),"md5加密前:");
        
        md5.appendAsciiStr(values.password);
        const password = md5.end();
        console.log({name: username, password, uuid: uuid.value, code: captcha});
        
        const res = await Login({name: username, password, uuid: uuid.value, code: captcha});
        try {
            // await formRef.value?.validate()
            showSuccessToast("登陆成功");
            localStorage.setItem('userInfo',JSON.stringify(res.data));
            router.replace("/home2");
        } catch (error) {
            // ElMessage.error('登录失败')
        }
        
    };
    const resetForm = () => {
        formRef.value?.resetValidation();
        handleCaptcha();
        username.value = '';
        password.value = '';
        captcha.value = '';
    }
    onMounted(() => {
        handleCaptcha();
    })
</script>

<style lang="scss" scoped>
    .login {
        width: 100%;
        height: 100vh;
        background: url(@/assets/imgs/login.png) no-repeat top;
        display: flex;
        margin: auto 0;
        justify-content: center;
        align-items: center;
        // background-size: 100% 100%;
        
        // background-size: contain;
        // background-size: auto 100;
        // background-position: center;
        .login-form {
            height: 25rem;
            width: 80%;
            display: flex;
            justify-content: center;
            align-items: center;
            
            border-radius: 10px;
            // background: rgba(255, 255, 255, 0.1);
            box-sizing: border-box;
            -webkit-backdrop-filter: blur(.3125rem);
            backdrop-filter: blur(.3125rem);
            border: .0625rem solid rgba(230, 233, 239, .5);
            box-shadow: inset 0 0 1.875rem #1b7ef24d;
            ::v-deep(.van-cell-group--inset){
                background: transparent;
                .van-cell {
                    // background: transparent;
                    // color: #333;
                    margin: 10px 0 0;
                    border-radius: .3125rem;
                }
                .captcha {
                    display: flex;
                    justify-content: space-around;
                    align-items: center;
                    .captcha-img {
                        width: 5.45rem;
                        height: 2.75rem;
                        margin-top: .625rem;
                        cursor: pointer;
                        img {
                            width: 100%;
                            height: 100%;
                            border-radius: .3125rem;
                        }
                    }
                }
            }
            ::v-deep(.form-btn) {
                display: flex;
                justify-content: space-around;
                align-items: center;
            }
        }
    }
</style>