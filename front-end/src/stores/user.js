import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('oa_token') || '',
    userInfo: JSON.parse(localStorage.getItem('oa_user') || 'null')
  }),
  getters: {
    isAdmin: state => state.userInfo?.role === 'admin',
    isLoggedIn: state => !!state.token
  },
  actions: {
    setLogin(token, userInfo) {
      this.token = token
      this.userInfo = userInfo
      localStorage.setItem('oa_token', token)
      localStorage.setItem('oa_user', JSON.stringify(userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('oa_token')
      localStorage.removeItem('oa_user')
    }
  }
})
