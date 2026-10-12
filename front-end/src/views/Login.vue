<template>
  <div class="login-page">
    <div class="login-box">
      <!-- 品牌区 -->
      <div class="logo">OA</div>
      <h1 class="title">企业核心业务管理系统</h1>
      <div class="subtitle">OA Office System · Modern Blue</div>

      <!-- 登录卡片 420 宽 -->
      <div class="login-card">
        <!-- 页签：条 372×44 r10，白色胶囊 182×36 r8 -->
        <div class="tabs">
          <div class="tab-pill" :class="{ right: mode === 'register' }"></div>
          <div class="tab" :class="{ active: mode === 'login' }" @click="mode = 'login'">登 录</div>
          <div class="tab" :class="{ active: mode === 'register' }" @click="switchToRegister">注 册</div>
        </div>

        <!-- ===== 登录态 ===== -->
        <template v-if="mode === 'login'">
          <div class="field-label mt-16">登录身份</div>
          <div class="roles">
            <div class="role-card" :class="{ selected: role === 'admin' }" @click="role = 'admin'">
              <div class="role-bubble bubble-admin">管</div>
              <div class="role-text">
                <div class="role-name">管理员</div>
                <div class="role-desc">管理全部数据</div>
              </div>
              <div class="radio" :class="{ checked: role === 'admin' }"></div>
            </div>
            <div class="role-card" :class="{ selected: role === 'staff' }" @click="role = 'staff'">
              <div class="role-bubble bubble-staff">员</div>
              <div class="role-text">
                <div class="role-name">员工</div>
                <div class="role-desc">个人自助服务</div>
              </div>
              <div class="radio" :class="{ checked: role === 'staff' }"></div>
            </div>
          </div>

          <div class="field-label mt-24">员工工号</div>
          <input v-model="loginForm.empId" class="input" placeholder="请输入员工工号" />

          <div class="field-label mt-20">登录密码</div>
          <input v-model="loginForm.password" type="password" class="input" placeholder="请输入登录密码" @keyup.enter="onLogin" />

          <div class="row-between mt-16">
            <label class="checkbox" @click.prevent="remember = !remember">
              <span class="box" :class="{ on: remember }"></span>
              <span>记住密码</span>
            </label>
            <span class="link">忘记密码？</span>
          </div>

          <button class="submit mt-16" :disabled="loading" @click="onLogin">登 录</button>
          <div class="hint">管理员可管理全部业务，员工仅可查看与自助申请</div>
        </template>

        <!-- ===== 注册态（首次使用：核验工号+姓名并设置密码） ===== -->
        <template v-else>
          <div class="field-label mt-16">员工工号</div>
          <input v-model="regForm.empId" class="input" placeholder="请输入员工工号" />

          <div class="field-label mt-20">员工姓名</div>
          <input v-model="regForm.name" class="input" placeholder="请输入员工登记姓名" />

          <div class="field-label mt-20">设置密码（不少于 6 位）</div>
          <input v-model="regForm.password" type="password" class="input" placeholder="请设置登录密码" />

          <div class="field-label mt-20">确认密码</div>
          <input v-model="regForm.confirm" type="password" class="input" placeholder="请再次输入密码" @keyup.enter="onRegister" />

          <button class="submit mt-24" :disabled="loading" @click="onRegister">注册并保存密码</button>
          <div class="hint">注册成功后将自动切换到登录页签</div>
        </template>
      </div>

      <div class="footer">Modern Blue 设计规范 · 主色 #0A59F7 · 圆角 16px · Noto Sans SC</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register, getUserInfo } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const mode = ref('login')
const role = ref('admin')
const remember = ref(false)
const loading = ref(false)

const loginForm = reactive({ empId: '', password: '' })
const regForm = reactive({ empId: '', name: '', password: '', confirm: '' })

function switchToRegister() {
  mode.value = 'register'
  regForm.empId = loginForm.empId
}

async function onLogin() {
  if (!loginForm.empId || !loginForm.password) { ElMessage.warning('请输入工号与密码'); return }
  loading.value = true
  try {
    const data = await login({ empId: Number(loginForm.empId), password: loginForm.password, role: role.value })
    // 先存 token（后续请求自动携带），再拉取完整用户信息；身份即进入后的界面视角
    store.setLogin(data.token, { empId: data.empId, role: data.role, name: data.name }, data.identity || role.value)
    const info = await getUserInfo()
    store.setLogin(data.token, { ...info, role: data.role, empId: data.empId }, data.identity || role.value)
    ElMessage.success('欢迎回来，' + data.name + '（' + ((data.identity || role.value) === 'admin' ? '管理员' : '员工') + '身份）')
    router.push('/dashboard')
  } finally { loading.value = false }
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
    loginForm.empId = f.empId
    loginForm.password = ''
    mode.value = 'login'
  } finally { loading.value = false }
}
</script>

<style scoped>
/* ===== 页面：居中、矮窗口可滚动 ===== */
.login-page {
  min-height: 100vh;
  background: #EEF3FB;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  box-sizing: border-box;
}
.login-box { display: flex; flex-direction: column; align-items: center; }

/* ===== 品牌区 ===== */
.logo {
  width: 96px; height: 96px; border-radius: 24px; background: #0A59F7;
  color: #fff; font-size: 34px; font-weight: 600;
  display: flex; align-items: center; justify-content: center;
}
.title { margin: 20px 0 0; font-size: 30px; font-weight: 600; color: #111111; line-height: 1.2; }
.subtitle { margin-top: 18px; font-size: 14px; color: #949497; }

/* ===== 登录卡片 420 宽 ===== */
.login-card {
  width: 420px; margin-top: 24px; padding: 28px;
  background: #fff; border-radius: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  box-sizing: border-box;
}

/* 页签 */
.tabs {
  position: relative; width: 364px; height: 44px; border-radius: 10px;
  background: #F1F3F5; display: flex;
}
.tab-pill {
  position: absolute; top: 4px; left: 4px; width: 178px; height: 36px;
  border-radius: 8px; background: #fff; box-shadow: 0 0 3px rgba(0,0,0,.08);
  transition: transform .18s ease;
}
.tab-pill.right { transform: translateX(178px); }
.tab {
  position: relative; flex: 1; display: flex; align-items: center; justify-content: center;
  font-size: 14px; color: #616161; cursor: pointer; user-select: none;
}
.tab.active { color: #0A59F7; font-weight: 600; }

/* 字段 */
.field-label { font-size: 12px; color: #616161; line-height: 12px; }
.mt-16 { margin-top: 16px; }
.mt-20 { margin-top: 20px; }
.mt-24 { margin-top: 24px; }
.field-label + .input, .field-label + .roles { margin-top: 6px; }

.input {
  display: block; width: 364px; height: 40px; box-sizing: border-box;
  padding: 0 16px; border: none; border-radius: 8px; background: #F1F3F5;
  font-family: inherit; font-size: 13px; color: #111111; outline: none;
}
.input::placeholder { color: #949497; }
.input:focus { box-shadow: 0 0 0 1.5px rgba(10, 89, 247, 0.45) inset; }

/* 身份选择 */
.roles { display: flex; gap: 8px; }
.role-card {
  position: relative; width: 178px; height: 64px; box-sizing: border-box;
  border: 1px solid #E0E0E0; border-radius: 10px; background: #fff;
  display: flex; align-items: center; gap: 8px; padding-left: 14px;
  cursor: pointer; transition: background .15s, border-color .15s;
}
.role-card.selected { background: #F5F8FF; border: 1.5px solid #0A59F7; }
.role-bubble {
  width: 32px; height: 32px; border-radius: 16px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center; font-size: 12px;
}
.bubble-admin { background: #E8F0FE; color: #0A59F7; }
.bubble-staff { background: #E6F7EE; color: #616161; }
.role-card.selected .bubble-staff { color: #0A59F7; }
.role-name { font-size: 13px; color: #111111; line-height: 1.2; }
.role-card.selected .role-name { color: #0A59F7; font-weight: 600; }
.role-desc { font-size: 10px; color: #949497; margin-top: 4px; }
.radio {
  position: absolute; right: 12px; top: 24px; width: 16px; height: 16px;
  box-sizing: border-box; border: 1.5px solid #D0D0D0; border-radius: 50%; background: #fff;
}
.radio.checked { border-color: #0A59F7; }
.radio.checked::after {
  content: ''; position: absolute; left: 2.5px; top: 2.5px;
  width: 8px; height: 8px; border-radius: 50%; background: #0A59F7;
}

/* 记住密码行 */
.row-between { display: flex; align-items: center; justify-content: space-between; width: 364px; }
.checkbox { display: flex; align-items: center; gap: 8px; font-size: 12px; color: #616161; cursor: pointer; }
.box {
  width: 14px; height: 14px; box-sizing: border-box;
  border: 1.5px solid #949497; border-radius: 4px; background: #fff; flex-shrink: 0;
}
.box.on { background: #0A59F7; border-color: #0A59F7; position: relative; }
.box.on::after {
  content: ''; position: absolute; left: 3px; top: 1px; width: 4px; height: 7px;
  border-right: 1.5px solid #fff; border-bottom: 1.5px solid #fff; transform: rotate(40deg);
}
.link { font-size: 12px; color: #0A59F7; cursor: pointer; }

/* 主按钮 */
.submit {
  display: block; width: 364px; height: 44px; border: none; border-radius: 8px;
  background: #0A59F7; color: #fff; font-family: inherit; font-size: 15px;
  cursor: pointer; transition: opacity .15s;
}
.submit:hover { opacity: .9; }
.submit:disabled { opacity: .6; cursor: default; }

/* 提示与页脚 */
.hint { margin-top: 20px; font-size: 11px; color: #949497; text-align: center; }
.footer { margin-top: 40px; font-size: 11px; color: #949497; }
</style>
