<template>
  <div class="login-page">
    <div class="login-logo">
      <div class="logo-box"><AppIcon name="grid" :size="30" color="#fff" /></div>
      <div class="sys-name">企业核心业务管理系统</div>
      <div class="sys-sub">OA Office System</div>
    </div>

    <div class="login-card">
      <!-- 页签切换 -->
      <div class="mode-tabs">
        <div class="tab" :class="{ active: mode === 'login' }" @click="mode = 'login'">登 录</div>
        <div class="tab" :class="{ active: mode === 'register' }" @click="switchToRegister">注 册</div>
      </div>

      <!-- 登录表单 -->
      <el-form v-if="mode === 'login'" @submit.prevent="onLogin">
        <div class="field-label">员工工号</div>
        <el-input v-model="loginForm.empId" placeholder="请输入员工工号" size="large" clearable>
          <template #prefix><AppIcon name="user" color="#949497" /></template>
        </el-input>
        <div class="field-label">登录密码</div>
        <el-input v-model="loginForm.password" type="password" placeholder="请输入登录密码" size="large" show-password>
          <template #prefix><AppIcon name="check" color="#949497" /></template>
        </el-input>
        <el-button type="primary" class="submit-btn" size="large" :loading="loading" @click="onLogin">
          登 录
        </el-button>
        <div class="form-hint">
          还没有账号？<el-link type="primary" :underline="false" @click="switchToRegister">立即注册</el-link>
          （首次使用需凭工号+姓名注册密码）
        </div>
      </el-form>

      <!-- 注册表单：首次使用，设置密码 -->
      <el-form v-else @submit.prevent="onRegister">
        <div class="register-tip">首次使用请凭「工号 + 姓名」核验身份并设置登录密码</div>
        <div class="field-label">员工工号</div>
        <el-input v-model="regForm.empId" placeholder="请输入员工工号" size="large" clearable>
          <template #prefix><AppIcon name="user" color="#949497" /></template>
        </el-input>
        <div class="field-label">姓名</div>
        <el-input v-model="regForm.name" placeholder="请输入员工登记姓名" size="large" clearable>
          <template #prefix><AppIcon name="people" color="#949497" /></template>
        </el-input>
        <div class="field-label">设置密码（不少于 6 位）</div>
        <el-input v-model="regForm.password" type="password" placeholder="请设置登录密码" size="large" show-password>
          <template #prefix><AppIcon name="check" color="#949497" /></template>
        </el-input>
        <div class="field-label">确认密码</div>
        <el-input v-model="regForm.confirm" type="password" placeholder="请再次输入密码" size="large" show-password>
          <template #prefix><AppIcon name="check" color="#949497" /></template>
        </el-input>
        <el-button type="primary" class="submit-btn" size="large" :loading="loading" @click="onRegister">
          注册并保存密码
        </el-button>
        <div class="form-hint">
          已有账号？<el-link type="primary" :underline="false" @click="mode = 'login'">返回登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import { login, register, getUserInfo } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const mode = ref('login')
const loading = ref(false)

const loginForm = reactive({ empId: '', password: '' })
const regForm = reactive({ empId: '', name: '', password: '', confirm: '' })

function switchToRegister() {
  mode.value = 'register'
  // 把登录页已输入的工号带过去
  regForm.empId = loginForm.empId
}

async function onLogin() {
  if (!loginForm.empId || !loginForm.password) {
    ElMessage.warning('请输入工号与密码')
    return
  }
  loading.value = true
  try {
    const data = await login({ empId: Number(loginForm.empId), password: loginForm.password })
    // 先存 token（后续请求才能自动携带），再拉取完整用户信息
    store.setLogin(data.token, { empId: data.empId, role: data.role, name: data.name })
    const info = await getUserInfo()
    store.setLogin(data.token, { ...info, role: data.role, empId: data.empId })
    ElMessage.success('欢迎回来，' + data.name)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}

async function onRegister() {
  const f = regForm
  if (!f.empId || !f.name?.trim()) { ElMessage.warning('请输入工号与姓名'); return }
  if (!f.password || f.password.length < 6) { ElMessage.warning('密码长度不能少于 6 位'); return }
  if (f.password !== f.confirm) { ElMessage.warning('两次输入的密码不一致'); return }
  loading.value = true
  try {
    await register({ empId: Number(f.empId), name: f.name.trim(), password: f.password })
    ElMessage.success('注册成功，请使用新密码登录')
    // 注册完成 → 跳回登录页签并预填工号
    loginForm.empId = f.empId
    loginForm.password = ''
    mode.value = 'login'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #EAF1FE 0%, #F1F3F5 45%, #F6F7F9 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
.login-logo { text-align: center; margin-bottom: 24px; }
.logo-box {
  width: 56px; height: 56px; border-radius: 16px; margin: 0 auto 12px;
  background: linear-gradient(135deg, #0A59F7, #3D8BFF);
  display: flex; align-items: center; justify-content: center;
}
.sys-name { font-size: 20px; font-weight: 600; color: var(--hm-text); }
.sys-sub { font-size: 12px; color: var(--hm-text-hint); margin-top: 4px; }
.login-card {
  width: 380px; background: #fff; border-radius: var(--hm-radius-card);
  padding: 24px 32px 28px; box-shadow: var(--hm-shadow);
}
.mode-tabs {
  display: flex; background: #F1F3F5; border-radius: 10px; padding: 4px; margin-bottom: 20px;
}
.tab {
  flex: 1; text-align: center; padding: 8px 0; font-size: 14px;
  color: var(--hm-text-secondary); border-radius: 8px; cursor: pointer;
  transition: all 0.15s;
}
.tab.active { background: #fff; color: var(--hm-primary); font-weight: 600; box-shadow: var(--hm-shadow); }
.register-tip {
  font-size: 12px; color: var(--hm-text-secondary);
  background: var(--hm-primary-light); border-radius: 8px;
  padding: 8px 12px; margin-bottom: 4px;
}
.field-label { font-size: 12px; color: var(--hm-text-secondary); margin: 14px 0 6px; }
.submit-btn { width: 100%; margin-top: 20px; font-weight: 500; }
.form-hint { font-size: 11px; color: var(--hm-text-hint); margin-top: 14px; text-align: center; }
</style>
