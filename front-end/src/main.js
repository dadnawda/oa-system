import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus, { ElMessage } from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './styles/design.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')

// ===== 全局错误显示器：任何被吞掉的 JS 错误直接弹窗，便于定位 =====
const showErr = msg => ElMessage({ message: String(msg).slice(0, 160), type: 'error', duration: 6000, grouping: true })

window.addEventListener('error', e => {
  if (e.message) showErr('JS错误: ' + e.message)
})

window.addEventListener('unhandledrejection', e => {
  const reason = e.reason
  // 业务失败已由 axios 拦截器提示过（res.msg 存在），这里只显示真正未处理的异常
  if (reason && !reason.msg) showErr('未处理异常: ' + (reason.message || reason))
})

app.config.errorHandler = err => {
  // 兼容对象形式的错误（如 axios 拦截器 reject 的 {code,msg,data}）
  const msg = err?.message || err?.msg || (typeof err === 'object' ? JSON.stringify(err).slice(0, 120) : err)
  showErr('应用错误: ' + msg)
  console.error(err)
}
