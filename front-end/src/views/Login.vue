<template>
  <div class="scroll-container">
    <div class="login-stage" :style="{ width: (1440 * scale) + 'px', height: (900 * scale) + 'px' }">
      <div
          id="5316_19325"
          class="Pixso-frame-5316_19325 pixso-relative-no-shrink"
          :style="{ transform: 'scale(' + scale + ')', transformOrigin: 'top left' }"
      >
            <div id="5316_19326" class="Pixso-rectangle-5316_19326"></div>
            <p id="5316_19327" class="Pixso-paragraph-5316_19327 pixso-flex">
                {{ "OA" }}
            </p>
            <p
                id="5316_19328"
                class="Pixso-paragraph-5316_19328 pixso-auto-size"
            >
                {{ "企业核心业务管理系统" }}
            </p>
            <p
                id="5316_19329"
                class="Pixso-paragraph-5316_19329 pixso-auto-size"
            >
                {{ "OA Office System · Modern Blue" }}
            </p>
            <div id="5316_19330" class="Pixso-frame-5316_19330" :style="mode==='register'?{height:'560px'}:{}">
                <div id="5316_19332" class="Pixso-rectangle-5316_19332"></div>
                <div v-if="mode==='login'" id="5316_19710" class="Pixso-rectangle-5316_19710"></div>
                <div id="5316_19711" class="Pixso-rectangle-5316_19711"></div>
                <div id="5316_19709" class="Pixso-rectangle-5316_19709"></div>
                <p id="5316_19712" class="Pixso-paragraph-5316_19712 pixso-flex cursor" @click="mode='login'" :style="mode==='register'?{color:'rgba(97,97,97,1)'}:{}">登 录</p>
                <p id="5316_19713" class="Pixso-paragraph-5316_19713 pixso-flex cursor" @click="switchToRegister" :style="mode==='register'?{color:'rgba(10,89,247,1)',fontWeight:600}:{}">注 册</p>
                <p
                    id="5316_20126"
                    class="Pixso-paragraph-5316_20126 pixso-auto-size"
                >
                    {{ "登录身份" }}
                </p>
                <div id="5316_20127" class="stroke-wrapper-5316_20127 cursor" @click="role='admin'">
                    <div
                        class="Pixso-rectangle-5316_20127 pixso-position-relative"
                    ></div>
                    <div class="stroke-5316_20127" :style="{borderColor: role==='admin'?'rgba(10,89,247,1)':'rgba(224,224,224,1)'}"></div>
                </div>
                <div id="5316_20128" class="Pixso-rectangle-5316_20128"></div>
                <p
                    id="5316_20129"
                    class="Pixso-paragraph-5316_20129 pixso-flex"
                >
                    {{ "管" }}
                </p>
                <p
                    id="5316_20130"
                    class="Pixso-paragraph-5316_20130 pixso-auto-size"
                >
                    {{ "管理员" }}
                </p>
                <p
                    id="5316_20131"
                    class="Pixso-paragraph-5316_20131 pixso-auto-size"
                >
                    {{ "管理全部数据" }}
                </p>
                <div id="5316_20132" class="Pixso-vector-5316_20132"></div>
                <div id="5316_20133" class="Pixso-vector-5316_20133" :style="{opacity: role==='admin'?1:0.35}"></div>
                <div id="5316_20134" class="stroke-wrapper-5316_20134 cursor" @click="role='staff'">
                    <div
                        class="Pixso-rectangle-5316_20134 pixso-position-relative"
                    ></div>
                    <div class="stroke-5316_20134" :style="{borderColor: role==='staff'?'rgba(10,89,247,1)':'rgba(224,224,224,1)'}"></div>
                </div>
                <div id="5316_20135" class="Pixso-rectangle-5316_20135"></div>
                <p
                    id="5316_20136"
                    class="Pixso-paragraph-5316_20136 pixso-flex"
                >
                    {{ "员" }}
                </p>
                <p
                    id="5316_20137"
                    class="Pixso-paragraph-5316_20137 pixso-auto-size"
                >
                    {{ "员工" }}
                </p>
                <p
                    id="5316_20138"
                    class="Pixso-paragraph-5316_20138 pixso-auto-size"
                >
                    {{ "个人自助服务" }}
                </p>
                <div id="5316_20139" class="Pixso-vector-5316_20139" :style="{opacity: role==='staff'?1:0.35}"></div>
                <div v-if="role==='staff'" style="position:absolute;left:370px;top:130px;width:8px;height:8px;border-radius:8px;background:rgba(10,89,247,1)"></div>
                <p
                    id="5316_20140"
                    class="Pixso-paragraph-5316_20140 pixso-auto-size"
                >
                    {{ "员工工号" }}
                </p>
                <div id="5316_20141" class="Pixso-rectangle-5316_20141"></div>
                <input v-model="loginForm.empId" class="d2c-input" style="left:44px;top:210px;width:336px;height:40px" placeholder="请输入员工工号" />
                <p
                    id="5316_20143"
                    class="Pixso-paragraph-5316_20143 pixso-auto-size"
                >
                    {{ "登录密码" }}
                </p>
                <div id="5316_20144" class="Pixso-rectangle-5316_20144"></div>
                <input v-model="loginForm.password" type="password" class="d2c-input" :style="mode==='login'?'left:44px;top:286px;width:336px;height:40px':'display:none'" placeholder="请输入登录密码" @keyup.enter="onLogin" />
                <template v-if="mode==='register'">
                    <p class="d2c-label" style="left:28px;top:256px">员工姓名</p>
                    <input v-model="regForm.name" class="d2c-input" style="left:44px;top:276px;width:336px;height:40px" placeholder="请输入员工登记姓名" />
                    <p class="d2c-label" style="left:28px;top:322px">设置密码（不少于 6 位）</p>
                    <input v-model="regForm.password" type="password" class="d2c-input" style="left:44px;top:342px;width:336px;height:40px" placeholder="请设置登录密码" />
                    <p class="d2c-label" style="left:28px;top:388px">确认密码</p>
                    <input v-model="regForm.confirm" type="password" class="d2c-input" style="left:44px;top:408px;width:336px;height:40px" placeholder="请再次输入密码" />
                </template>
                <div v-if="mode==='login'" id="5316_20146" class="stroke-wrapper-5316_20146">
                    <div
                        class="Pixso-rectangle-5316_20146 pixso-position-relative"
                    ></div>
                    <div class="stroke-5316_20146"></div>
                </div>
                <p v-if="mode==='login'" id="5316_20147" class="Pixso-paragraph-5316_20147 pixso-auto-size">记住密码</p>
                <p v-if="mode==='login'" id="5316_20148" class="Pixso-paragraph-5316_20148 pixso-auto-size">忘记密码？</p>
                <div class="d2c-btn cursor" style="left:28px;top:372px;width:364px;height:44px" :style="mode==='register'?{top:'464px'}:{}" @click="mode==='login'?onLogin():onRegister()">
                    <div class="Pixso-rectangle-5316_20149 d2c-fill"></div>
                    <p class="Pixso-paragraph-5316_20150 pixso-flex">{{ mode==='login' ? '登 录' : '注册并保存密码' }}</p>
                </div>
                                <p id="5316_20151" class="Pixso-paragraph-5316_20151 pixso-auto-size" :style="mode==='register'?{top:'524px'}:{}">{{ mode==='login' ? '管理员可管理全部业务，员工仅可查看与自助申请' : '注册成功后将自动切换到登录页签' }}</p>
            </div>
            <p
                id="5316_19344"
                class="Pixso-paragraph-5316_19344 pixso-auto-size"
            >
                {{
                    "Modern Blue 设计规范 · 主色 #0A59F7 · 圆角 16px · Noto Sans SC"
                }}
            </p>
      </div>
    </div>
  </div>
</template>
<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register, getUserInfo } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const mode = ref('login')
const scale = ref(1)
function fit() {
  scale.value = Math.min(1, window.innerWidth / 1440, window.innerHeight / 900)
}
onMounted(() => { fit(); window.addEventListener('resize', fit) })
onUnmounted(() => window.removeEventListener('resize', fit))
const role = ref('admin')
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
    const data = await login({ empId: Number(loginForm.empId), password: loginForm.password })
    store.setLogin(data.token, { empId: data.empId, role: data.role, name: data.name })
    const info = await getUserInfo()
    store.setLogin(data.token, { ...info, role: data.role, empId: data.empId })
    ElMessage.success('欢迎回来，' + data.name)
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
<style>
.scroll-container {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    width: 100%;
    overflow: auto;
    background-color: rgba(238, 243, 251, 1);
}

.login-stage { position: relative; flex-shrink: 0; }

.Pixso-frame-5316_19325 {
    width: 1440px;
    height: 900px;
    overflow: hidden;
    background-color: rgba(238, 243, 251, 1);
}

.Pixso-rectangle-5316_19326,
.Pixso-paragraph-5316_19327 {
    width: 96px;
    height: 96px;
    position: absolute;
    left: 672px;
    top: 90px;
}

.Pixso-rectangle-5316_19326 {
    border-radius: 24px 24px 24px 24px;
    background-color: rgba(10, 89, 247, 1);
}

.Pixso-paragraph-5316_19327,
.Pixso-paragraph-5316_19328,
.Pixso-paragraph-5316_19712,
.Pixso-paragraph-5316_20129,
.Pixso-paragraph-5316_20130,
.Pixso-paragraph-5316_20136,
.Pixso-paragraph-5316_20137,
.Pixso-paragraph-5316_20150 {
    font-family: "Noto Sans SC-Bold";
    font-weight: 700;
}

.Pixso-paragraph-5316_19327,
.Pixso-paragraph-5316_19334,
.Pixso-paragraph-5316_19713,
.Pixso-paragraph-5316_19712,
.Pixso-paragraph-5316_20129,
.Pixso-paragraph-5316_20136,
.Pixso-paragraph-5316_20150 {
    text-align: center;
    flex-direction: column;
    justify-content: center;
}

.Pixso-paragraph-5316_19327,
.Pixso-paragraph-5316_19328,
.Pixso-paragraph-5316_19329,
.Pixso-paragraph-5316_19334,
.Pixso-paragraph-5316_19713,
.Pixso-paragraph-5316_19712,
.Pixso-paragraph-5316_20126,
.Pixso-paragraph-5316_20129,
.Pixso-paragraph-5316_20130,
.Pixso-paragraph-5316_20131,
.Pixso-paragraph-5316_20136,
.Pixso-paragraph-5316_20137,
.Pixso-paragraph-5316_20138,
.Pixso-paragraph-5316_20140,
.Pixso-paragraph-5316_20142,
.Pixso-paragraph-5316_20143,
.Pixso-paragraph-5316_20145,
.Pixso-paragraph-5316_20147,
.Pixso-paragraph-5316_20148,
.Pixso-paragraph-5316_20150,
.Pixso-paragraph-5316_20151,
.Pixso-paragraph-5316_19344 {
    letter-spacing: 0px;
    text-decoration: none;
}

.Pixso-paragraph-5316_19327 {
    font-size: 34px;
    color: rgba(255, 255, 255, 1);
}

.Pixso-paragraph-5316_19328,
.Pixso-paragraph-5316_19329,
.Pixso-paragraph-5316_20126,
.Pixso-paragraph-5316_20130,
.Pixso-paragraph-5316_20131,
.Pixso-paragraph-5316_20137,
.Pixso-paragraph-5316_20138,
.Pixso-paragraph-5316_20140,
.Pixso-paragraph-5316_20142,
.Pixso-paragraph-5316_20143,
.Pixso-paragraph-5316_20145,
.Pixso-paragraph-5316_20147,
.Pixso-paragraph-5316_20148,
.Pixso-paragraph-5316_20151,
.Pixso-paragraph-5316_19344 {
    text-align: left;
    white-space: pre;
    flex-grow: 0;
}

.Pixso-paragraph-5316_19328,
.Pixso-paragraph-5316_19329,
.Pixso-vector-5316_20132,
.Pixso-vector-5316_20133,
.Pixso-paragraph-5316_20142,
.Pixso-paragraph-5316_20143 {
    position: absolute;
}

.Pixso-paragraph-5316_19328 {
    font-size: 30px;
    color: rgba(17, 17, 17, 1);
    left: 570px;
    top: 206px;
}

.Pixso-paragraph-5316_19329,
.Pixso-paragraph-5316_19334,
.Pixso-paragraph-5316_19713,
.Pixso-paragraph-5316_20126,
.Pixso-paragraph-5316_20131,
.Pixso-paragraph-5316_20138,
.Pixso-paragraph-5316_20140,
.Pixso-paragraph-5316_20142,
.Pixso-paragraph-5316_20143,
.Pixso-paragraph-5316_20145,
.Pixso-paragraph-5316_20147,
.Pixso-paragraph-5316_20148,
.Pixso-paragraph-5316_20151,
.Pixso-paragraph-5316_19344 {
    font-family: "Noto Sans SC-Regular";
    font-weight: 400;
}

.Pixso-paragraph-5316_19329 {
    font-size: 13px;
    color: rgba(148, 148, 151, 1);
    left: 620.5px;
    top: 252px;
}

.Pixso-frame-5316_19330 {
    width: 420px;
    height: 464px;
    overflow: hidden;
    position: absolute;
    left: 510px;
    top: 276px;
    border-radius: 16px 16px 16px 16px;
    box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.06);
    background-color: rgba(255, 255, 255, 1);
}

.Pixso-rectangle-5316_19332,
.Pixso-rectangle-5316_19711,
.Pixso-paragraph-5316_19334,
.Pixso-paragraph-5316_19713,
.Pixso-paragraph-5316_19712 {
    width: 182px;
    height: 36px;
    top: 28px;
}

.Pixso-rectangle-5316_19332,
.Pixso-rectangle-5316_19711 {
    position: absolute;
    left: 28px;
    border-radius: 8px 8px 8px 8px;
    background-color: rgba(255, 255, 255, 1);
}

.Pixso-rectangle-5316_19710,
.Pixso-rectangle-5316_19709 {
    width: 372px;
    height: 44px;
    position: absolute;
    left: 24px;
    top: 24px;
    border-radius: 10px 10px 10px 10px;
    background-color: rgba(241, 243, 245, 1);
}

.Pixso-paragraph-5316_19334,
.Pixso-paragraph-5316_19713,
.Pixso-paragraph-5316_19712 {
    font-size: 14px;
    position: absolute;
}

.Pixso-paragraph-5316_19334,
.Pixso-paragraph-5316_19713 {
    color: rgba(97, 97, 97, 1);
    left: 210px;
}

.Pixso-paragraph-5316_19712 {
    color: rgba(10, 89, 247, 1);
    left: 28px;
}

.Pixso-paragraph-5316_20126 {
    font-size: 12px;
    color: rgba(97, 97, 97, 1);
    position: absolute;
    left: 28px;
    top: 84px;
}

.Pixso-rectangle-5316_20127 {
    width: 100%;
    height: 100%;
    border-radius: 10px 10px 10px 10px;
    z-index: 0;
    background-color: rgba(245, 248, 255, 1);
}

.stroke-wrapper-5316_20127,
.stroke-wrapper-5316_20134 {
    top: 106px;
    width: 178px;
    height: 64px;
}

.stroke-wrapper-5316_20127 {
    position: absolute;
    isolation: isolate;
    left: 28px;
}

.stroke-5316_20127,
.stroke-5316_20134,
.stroke-5316_20146 {
    inset: 0px;
    pointer-events: none;
    z-index: 1;
    border-width: 1px 1px 1px 1px;
    border-style: solid;
    box-sizing: border-box;
}

.stroke-5316_20127 {
    position: absolute;
    border-radius: 10px 10px 10px 10px;
    border-color: rgba(10, 89, 247, 1);
}

.Pixso-rectangle-5316_20128,
.Pixso-paragraph-5316_20129,
.Pixso-rectangle-5316_20135,
.Pixso-paragraph-5316_20136 {
    width: 32px;
    height: 32px;
    top: 122px;
}

.Pixso-rectangle-5316_20128,
.Pixso-paragraph-5316_20129 {
    position: absolute;
    left: 42px;
}

.Pixso-rectangle-5316_20128 {
    border-radius: 16px 16px 16px 16px;
    background-color: rgba(232, 240, 254, 1);
}

.Pixso-paragraph-5316_20129 {
    font-size: 12px;
    color: rgba(10, 89, 247, 1);
}

.Pixso-paragraph-5316_20130,
.Pixso-paragraph-5316_20131 {
    position: absolute;
    left: 84px;
}

.Pixso-paragraph-5316_20130 {
    font-size: 13px;
    color: rgba(10, 89, 247, 1);
    top: 118px;
}

.Pixso-paragraph-5316_20131,
.Pixso-paragraph-5316_20138 {
    font-size: 10px;
    top: 138px;
}

.Pixso-paragraph-5316_20131,
.Pixso-paragraph-5316_20138 {
    color: rgba(148, 148, 151, 1);
}

.Pixso-vector-5316_20132,
.Pixso-vector-5316_20133,
.Pixso-vector-5316_20139 {
    background-size: 100% 100%;
    background-repeat: no-repeat;
}

.Pixso-vector-5316_20132,
.Pixso-vector-5316_20139 {
    width: 16px;
    height: 16px;
    top: 126px;
}

.Pixso-vector-5316_20132 {
    background-image: url("./assets/Ellipse_5316_20132.svg");
    left: 180px;
}

.Pixso-vector-5316_20133 {
    width: 8px;
    height: 8px;
    background-image: url("./assets/Ellipse_5316_20133.svg");
    left: 184px;
    top: 130px;
}

.Pixso-rectangle-5316_20134 {
    width: 100%;
    height: 100%;
    border-radius: 10px 10px 10px 10px;
    z-index: 0;
    background-color: rgba(255, 255, 255, 1);
}

.stroke-wrapper-5316_20134 {
    position: absolute;
    isolation: isolate;
    left: 214px;
}

.stroke-5316_20134 {
    position: absolute;
    border-radius: 10px 10px 10px 10px;
    border-color: rgba(224, 224, 224, 1);
}

.Pixso-rectangle-5316_20135,
.Pixso-paragraph-5316_20136 {
    position: absolute;
    left: 228px;
}

.Pixso-rectangle-5316_20135 {
    border-radius: 16px 16px 16px 16px;
    background-color: rgba(230, 247, 238, 1);
}

.Pixso-paragraph-5316_20136 {
    font-size: 12px;
    color: rgba(97, 97, 97, 1);
}

.Pixso-paragraph-5316_20137,
.Pixso-paragraph-5316_20138 {
    position: absolute;
    left: 270px;
}

.Pixso-paragraph-5316_20137 {
    font-size: 13px;
    color: rgba(17, 17, 17, 1);
    top: 118px;
}

.Pixso-vector-5316_20139 {
    background-image: url("./assets/Ellipse_5316_20139.svg");
    position: absolute;
    left: 366px;
}

.Pixso-paragraph-5316_20140 {
    font-size: 12px;
    color: rgba(97, 97, 97, 1);
    position: absolute;
    left: 28px;
    top: 190px;
}

.Pixso-rectangle-5316_20141 {
    width: 364px;
    height: 40px;
    position: absolute;
    left: 28px;
    top: 210px;
    border-radius: 8px 8px 8px 8px;
    background-color: rgba(241, 243, 245, 1);
}

.Pixso-paragraph-5316_20142 {
    font-size: 13px;
    color: rgba(148, 148, 151, 1);
    left: 44px;
    top: 222px;
}

.Pixso-paragraph-5316_20143 {
    font-size: 12px;
    color: rgba(97, 97, 97, 1);
    left: 28px;
    top: 266px;
}

.Pixso-rectangle-5316_20144 {
    width: 364px;
    height: 40px;
    position: absolute;
    left: 28px;
    top: 286px;
    border-radius: 8px 8px 8px 8px;
    background-color: rgba(241, 243, 245, 1);
}

.Pixso-paragraph-5316_20145 {
    font-size: 13px;
    color: rgba(148, 148, 151, 1);
    position: absolute;
    left: 44px;
    top: 298px;
}

.Pixso-rectangle-5316_20146 {
    width: 100%;
    height: 100%;
    border-radius: 4px 4px 4px 4px;
    z-index: 0;
    background-color: rgba(255, 255, 255, 1);
}

.stroke-wrapper-5316_20146 {
    position: absolute;
    isolation: isolate;
    left: 28px;
    top: 342px;
    width: 14px;
    height: 14px;
}

.stroke-5316_20146 {
    position: absolute;
    border-radius: 4px 4px 4px 4px;
    border-color: rgba(148, 148, 151, 1);
}

.Pixso-paragraph-5316_20147,
.Pixso-paragraph-5316_20148 {
    font-size: 12px;
    position: absolute;
    top: 340px;
}

.Pixso-paragraph-5316_20147 {
    color: rgba(97, 97, 97, 1);
    left: 50px;
}

.Pixso-paragraph-5316_20148 {
    color: rgba(10, 89, 247, 1);
    left: 330px;
}

.Pixso-rectangle-5316_20149,
.Pixso-paragraph-5316_20150 {
    width: 364px;
    height: 44px;
    position: absolute;
    left: 28px;
    top: 372px;
}

.Pixso-rectangle-5316_20149 {
    border-radius: 8px 8px 8px 8px;
    background-color: rgba(10, 89, 247, 1);
}

.Pixso-paragraph-5316_20150 {
    font-size: 15px;
    color: rgba(255, 255, 255, 1);
}

.Pixso-paragraph-5316_20151,
.Pixso-paragraph-5316_19344 {
    font-size: 11px;
    color: rgba(148, 148, 151, 1);
    position: absolute;
}

.Pixso-paragraph-5316_20151 {
    left: 28px;
    top: 432px;
}

.Pixso-paragraph-5316_19344 {
    left: 548px;
    top: 856px;
}
</style>

.d2c-input {
    position: absolute;
    border: none;
    background: transparent;
    outline: none;
    font-family: 'Noto Sans SC', sans-serif;
    font-size: 13px;
    color: rgba(17, 17, 17, 1);
    box-sizing: border-box;
    z-index: 5;
}
.d2c-input::placeholder { color: rgba(148, 148, 151, 1); }
.d2c-label {
    position: absolute;
    font-family: 'Noto Sans SC', sans-serif;
    font-size: 12px;
    color: rgba(97, 97, 97, 1);
}
.d2c-btn { position: absolute; cursor: pointer; z-index: 5; }
.d2c-fill { width: 100%; height: 100%; }
.cursor { cursor: pointer; }
