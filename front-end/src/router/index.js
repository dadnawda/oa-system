import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'dept', component: () => import('../views/DeptManage.vue'), meta: { title: '部门管理', admin: true } },
      { path: 'emp', component: () => import('../views/EmpManage.vue'), meta: { title: '员工管理', admin: true } },
      { path: 'notice', component: () => import('../views/NoticeCenter.vue'), meta: { title: '公告中心' } },
      { path: 'salary', component: () => import('../views/SalaryManage.vue'), meta: { title: '工资管理', admin: true } },
      { path: 'my-salary', component: () => import('../views/MySalary.vue'), meta: { title: '我的工资' } },
      { path: 'leave', component: () => import('../views/LeaveManage.vue'), meta: { title: '请假审批' } },
      { path: 'transfer', component: () => import('../views/TransferManage.vue'), meta: { title: '调岗审批' } },
      { path: 'profile', component: () => import('../views/Profile.vue'), meta: { title: '个人中心' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：登录校验 + 管理员页面校验
router.beforeEach(to => {
  const store = useUserStore()
  // token 格式合法性预检：防止残留的无效 token（如 "undefined"）放行后页面并发 401
  const tokenValid = store.isLoggedIn && /^\S+\.\S+$/.test(store.token)
  if (!to.meta.public && !tokenValid) {
    if (store.isLoggedIn) store.logout()
    return '/login'
  }
  if (to.path === '/login' && tokenValid) {
    return '/dashboard'
  }
  if (to.meta.admin && !store.isAdmin) {
    return '/dashboard'
  }
})

export default router
