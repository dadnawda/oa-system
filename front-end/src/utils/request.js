import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 401 提示去重：1.5 秒窗口内只弹一次
let last401At = 0

function clearSession() {
  localStorage.removeItem('oa_token')
  localStorage.removeItem('oa_user')
}

// 请求拦截：自动携带 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('oa_token')
  if (token) config.headers.Authorization = token
  return config
})

// 响应拦截：统一处理业务码
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 0) return res.data
    if (res.code === 401) {
      // token 无效/过期：静默清理会话；登录页上不再弹窗跳转，其余页面 1.5s 去重提示
      clearSession()
      const now = Date.now()
      const onLogin = router.currentRoute.value.path === '/login'
      if (!onLogin && now - last401At > 1500) {
        last401At = now
        ElMessage({ message: res.msg || '请先登录', type: 'error', grouping: true })
        router.push('/login')
      }
      return Promise.reject(res)
    }
    ElMessage({ message: res.msg || '操作失败', type: 'error', grouping: true })
    return Promise.reject(res)
  },
  error => {
    ElMessage({ message: '网络异常，请检查后端服务是否启动', type: 'error', grouping: true })
    return Promise.reject(error)
  }
)

export default request
