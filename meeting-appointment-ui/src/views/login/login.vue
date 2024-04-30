<template>
  <div class="login-container">
    <div class="left-background"></div>
    <div class="right-login">
      <!-- 账号登录 -->
      <div class="login-number" v-if="isShow">
        <div class="logo"></div>
        <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules">
          <!-- 域名 -->
          <el-form-item prop="username">
            <el-input v-model="ruleForm.username" placeholder="账号" />
          </el-form-item>
          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input v-model="ruleForm.password" type="password" placeholder="密码" />
          </el-form-item>
          <!-- 验证码 -->
          <el-form-item prop="captcha">
            <el-row :gutter="20">
              <el-col :span="12"><el-input v-model="ruleForm.captcha"></el-input></el-col>
              <el-col :span="12">
                <div class="captcha">
                  <el-image :src="imgUrl" @click="changeCaptcha" draggable="false" />
                </div>
              </el-col>
            </el-row>
          </el-form-item>
          <!-- 登录 -->
          <el-form-item>
            <el-button type="primary" @click="submitForm(ruleFormRef)">
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
import { nextTick, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { h } from 'vue'
import { ElMessage } from 'element-plus'
import errorImg from '@/assets/img/error.png'
import { useUserStore } from '@/stores/user'

// 用户信息
const userStore = useUserStore();
const ruleFormRef = ref<FormInstance>()
// 登录验证
const ruleForm = reactive({
  username: '',
  password: '',
  captcha: '',
})

const rules = reactive<FormRules<typeof ruleForm>>({
  username: [
    { required: true, message: "请输入域名", trigger: "blur" },
    { min: 5, max: 18, message: "域名必须是5-18位字符", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 24, message: "密码必须是6-24位字符", trigger: "blur" },
  ],
  captcha: [
    { required: true, message: "请输入验证码", trigger: "blur" },
    { min: 4, max: 4, message: "验证码必须是4位字符", trigger: "blur" }
  ]
})

// 验证码
let imgUrl = ref()
const changeCaptcha = () => {
  imgUrl.value = ''
}

// 登录错误弹窗
const openVn = () => {
  ElMessage({
    icon: 'none',
    message: h('div',
      { style: 'display: flex; align-items: center; justify-content: center; width: 26rem; height: 4.25rem; font-size: 1.375rem' }, [
      h('img', {
        src: errorImg,
        style: 'width: 4.375rem; height: 4.375rem;'
      }),
      h('span', null, '域账号输入错误！')
    ]),
    duration: 1000
  })
}

// 账号切换
const isShow = ref(false)
const changeLogin = () => {
  isShow.value = !isShow.value;
  if (!isShow.value) {
    nextTick(() => {
      code();
    })
  }
}
// 账号登录 
const router = useRouter()
const submitForm = async(formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
        const {username, password} = ruleForm;
        userStore.getUserInfo(username, password);     
    } else {
      openVn()
      return false
    }
  })
}

// // 扫码登录
// onMounted(() => {
//   const wwLogin = ww.createWWLoginPanel({
//     el: '#qr_login',
//     params: {
//       appid: 'ww942086e6c44abc4b',
//     agentid: '1000002',
//      redirect_uri: 'ggssyy.cn/login/logininfo',
//       state: 'WWLogin',
//       // redirect_type: 'callback'
//     },
//     onCheckWeComLogin({ isWeComLogin }) {
//       console.log(isWeComLogin)
//     },
//     onLoginSuccess({ code }) {
//       console.log({ code })
//     },
//     onLoginFail(err) {
//       console.log(err)
//     }
//   })
// })

onMounted(() => {
  // changeCaptcha();
  code();
});
const code = () => {
  //@ts-ignore
  new WwLogin({
    "id": "login-scan",
    appid: 'ww942086e6c44abc4b',
    agentid: '1000002',
    login_type: 'CorpApp',
    redirect_uri: 'http%3A%2F%2Fggssyy.cn%2F%23%2Fhome',
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
        }
      }

      .el-button {
        width: 100%;
      }
    }

  }
}
</style>