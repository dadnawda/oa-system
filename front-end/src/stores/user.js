import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('oa_token') || '',
    userInfo: JSON.parse(localStorage.getItem('oa_user') || 'null'),
    // 登录身份（界面视角）：admin / staff —— 与真实角色分开，管理员也可用员工身份进入
    identity: localStorage.getItem('oa_identity') || ''
  }),
  getters: {
    // 真实角色（权限用）
    isAdmin: state => state.userInfo?.role === 'admin',
    // 界面视角（菜单/工作台分支用）
    viewIsAdmin: state => (state.identity || state.userInfo?.role) === 'admin',
    isLoggedIn: state => !!state.token
  },
  actions: {
    setLogin(token, userInfo, identity) {
      this.token = token
      this.userInfo = userInfo
      // 关键：未显式传身份时**保留当前身份**，不要回落到真实角色
      // （否则"管理员以员工身份进入"后在个人中心刷新用户信息，会被真实角色 admin 覆盖 → 界面翻成管理端）
      this.identity = identity || this.identity || userInfo?.role || ''
      localStorage.setItem('oa_token', token)
      localStorage.setItem('oa_user', JSON.stringify(userInfo))
      localStorage.setItem('oa_identity', this.identity)
    },
    /** 仅刷新用户资料，不改动登录身份（个人中心等页面用） */
    updateUserInfo(userInfo) {
      if (!userInfo) return
      this.userInfo = { ...this.userInfo, ...userInfo }
      localStorage.setItem('oa_user', JSON.stringify(this.userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      this.identity = ''
      localStorage.removeItem('oa_token')
      localStorage.removeItem('oa_user')
      localStorage.removeItem('oa_identity')
    }
  }
})
