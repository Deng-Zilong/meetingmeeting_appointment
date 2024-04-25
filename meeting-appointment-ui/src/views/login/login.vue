<template>
  <div class="container">
    <div class="leftBackground"></div>
    <div class="rightLogin">
      <div class="logo"></div>
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules">
        <!-- 域名 -->
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" placeholder="账号"/>
        </el-form-item>
        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input v-model="ruleForm.password" type="password" placeholder="密码"/>
        </el-form-item>
        <!-- 验证码 -->
        <el-form-item prop="captcha">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-input v-model="ruleForm.captcha"></el-input>
            </el-col>
            <el-col :span="12">
              <div class="captcha">
                <el-image :src="imgUrl" @click="changeCaptcha" draggable="false"/>
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
  </div>
</template>

<script setup lang="ts">
import {h, reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage} from 'element-plus'

// const checkAge = (rule: any, value: any, callback: any) => {
//   if (!value) {
//     return callback(new Error('Please input the age'))
//   }
//   setTimeout(() => {
//     if (!Number.isInteger(value)) {
//       callback(new Error('Please input digits'))
//     } else {
//       if (value < 18) {
//         callback(new Error('Age must be greater than 18'))
//       } else {
//         callback()
//       }
//     }
//   }, 1000)
// }

// const validatePass = (rule: any, value: any, callback: any) => {
//   if (value === '') {
//     callback(new Error('Please input the password'))
//   } else {
//     if (ruleForm.checkPass !== '') {
//       if (!ruleFormRef.value) return
//       ruleFormRef.value.validateField('checkPass', () => null)
//     }
//     callback()
//   }
// }
// const validatePass2 = (rule: any, value: any, callback: any) => {
//   if (value === '') {
//     callback(new Error('Please input the password again'))
//   } else if (value !== ruleForm.pass) {
//     callback(new Error("Two inputs don't match!"))
//   } else {
//     callback()
//   }
// }

const ruleFormRef = ref<FormInstance>()

const ruleForm = reactive({
  username: '',
  password: '',
  captcha: '',
})

const rules = reactive<FormRules<typeof ruleForm>>({
  // username: [{ validator: validatePass2, trigger: 'blur' }],
  // password: [{ validator: validatePass, trigger: 'blur' }],
  // captcha: [{ validator: checkAge, trigger: 'blur' }],
  username: [
    {required: true, message: "请输入域名", trigger: "blur"},
    {min: 5, max: 18, message: "域名必须是5-18位字符", trigger: "blur"}
  ],
  password: [
    {required: true, message: "请输入密码", trigger: "blur"},
    {min: 6, max: 24, message: "密码必须是6-24位字符", trigger: "blur"},
  ],
  captcha: [
    {required: true, message: "请输入验证码", trigger: "blur"},
    {min: 5, max: 18, message: "验证码必须是4位字符", trigger: "blur"}
  ]
})

// 验证码
let imgUrl = ref()
const changeCaptcha = () => {
  imgUrl.value = ''
}

// 弹窗
const openVn = () => {
  ElMessage({
    icon: '',
    message: h('div', {style: 'width: 550px; height: 100px; border-radius: 150px; font-size: 1.375rem'}, [
      h('img', {
        src: '../src/assets/img/error.png',
        style: 'width: 4.375rem; height: 4.375rem;'
      }),
      h('span', null, 'Message can be '),
      h('i', {style: 'color: teal'}, 'VNode')
    ]),
    duration: 0
  })
}

// 登录 
const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!')
    } else {
      openVn()
      console.log('error submit!')
      return false
    }
  })
}

// const resetForm = (formEl: FormInstance | undefined) => {
//   if (!formEl) return
//   formEl.resetFields()
// }
</script>

<style scoped lang="scss">
.container {
  display: flex;
  width: 100vw;
  height: 100vh;

  .leftBackground {
    width: 70vw;
    height: 100%;
    background: url(@/assets/img/login.png) no-repeat;
    background-size: 100% 100%;
  }

  .rightLogin {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 30vw;
    height: 100%;

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
</style>