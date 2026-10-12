<template>
  <div>
    <!-- 员工端：欢迎横幅 -->
    <div v-if="!user.isAdmin" class="welcome-banner">
      <div>
        <div class="wb-title">Hi，{{ user.userInfo?.name }}，今天也要元气满满！</div>
        <div class="wb-sub">{{ todayStr }} · 你有 {{ pendingCount }} 条申请正在审批中，本月工资待发放</div>
      </div>
      <div class="wb-right">
        <span class="wb-job">{{ user.userInfo?.job || '暂无职位' }} · {{ user.userInfo?.deptName || '未分配部门' }}</span>
        <el-avatar :size="56" :src="user.userInfo?.image || undefined" class="wb-avatar">{{ (user.userInfo?.name || '用')[0] }}</el-avatar>
      </div>
    </div>
    <!-- 管理端：页面标题 -->
    <div v-else class="hm-page-header">
      <span class="hm-page-title">工作台</span>
    </div>
    <div class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="hm-card stat-card">
        <div class="stat-icon" :style="{ background: s.bg }"><HmIcon :name="s.icon" :size="20" :color="s.color" /></div>
        <div>
          <div class="stat-value">{{ s.value }}<span class="stat-unit">{{ s.unit }}</span></div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>
    </div>

    <div class="hm-card" style="margin-top:16px">
      <div class="card-head">快捷入口</div>
      <div class="quick-grid">
        <router-link v-for="q in quickLinks" :key="q.path" :to="q.path" class="quick-item">
          <HmIcon :name="q.icon" :size="20" :color="q.color || '#111111'" />
          <span>{{ q.title }}</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import HmIcon from '../components/HmIcon.vue'
import { useUserStore } from '../stores/user'
import { pageEmps, listDepts, pageNotices, mySalaries, myLeaves, myTransfers, pageLeaves, pageTransfers } from '../api'

const user = useUserStore()
const data = ref({ empCount: '-', deptCount: '-', noticeCount: '-', mySalaryCount: '-', myLeaveCount: '-', myTransferCount: '-', pendingLeave: 0, pendingTransfer: 0, myPending: 0 })

// 员工端欢迎横幅
const todayStr = computed(() => {
  const d = new Date()
  const week = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()]
  const p = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} 星期${week}`
})
const pendingCount = computed(() => data.value.myPending)

const stats = computed(() => {
  if (user.isAdmin) {
    return [
      { label: '员工总数', value: data.value.empCount, unit: '人', icon: 'hm-people', color: '#0A59F7', bg: '#E8F0FE' },
      { label: '部门数量', value: data.value.deptCount, unit: '个', icon: 'hm-building', color: '#00B34A', bg: '#E6F7EE' },
      { label: '公告数量', value: data.value.noticeCount, unit: '条', icon: 'hm-chat', color: '#FF8D1A', bg: '#FFF3E5' },
      { label: '待审批', value: data.value.pendingLeave + data.value.pendingTransfer, unit: '件', icon: 'hm-doc', color: '#E02020', bg: '#FDECEC' }
    ]
  }
  return [
    { label: '我的请假', value: data.value.myLeaveCount, unit: '条', icon: 'hm-my-chat', color: '#0A59F7', bg: '#E8F0FE' },
    { label: '我的调岗', value: data.value.myTransferCount, unit: '条', icon: 'hm-my-swap', color: '#00B34A', bg: '#E6F7EE' },
    { label: '本月工资', value: data.value.mySalaryCount, unit: '条', icon: 'hm-my-card', color: '#FF8D1A', bg: '#FFF3E5' },
    { label: '公告数量', value: data.value.noticeCount, unit: '条', icon: 'hm-doc', color: '#E02020', bg: '#FDECEC' }
  ]
})

const quickLinks = computed(() => {
  const base = [
    { path: '/notice', title: '查看公告', icon: 'hm-notice', color: '#111111' },
    { path: '/leave', title: '请假申请', icon: 'hm-leave', color: '#111111' },
    { path: '/transfer', title: '职位调整', icon: 'hm-my-swap', color: '#111111' },
    { path: '/profile', title: '个人中心', icon: 'hm-user', color: '#111111' }
  ]
  return user.isAdmin
    ? [{ path: '/emp', title: '员工管理', icon: 'hm-emp-star', color: '#00B34A' }, { path: '/dept', title: '部门管理', icon: 'hm-dept', color: '#111111' }, ...base.slice(0, 2), { path: '/salary', title: '工资管理', icon: 'hm-money', color: '#111111' }]
    : base
})

onMounted(async () => {
  try {
    const [depts, notices] = await Promise.all([listDepts(), pageNotices({ page: 1, pageSize: 1 })])
    data.value.deptCount = depts.length
    data.value.noticeCount = notices.total
    if (user.isAdmin) {
      const [emps, pl, pt] = await Promise.all([
        pageEmps({ page: 1, pageSize: 1 }),
        pageLeaves({ status: 0, page: 1, pageSize: 1 }),
        pageTransfers({ status: 0, page: 1, pageSize: 1 })
      ])
      data.value.empCount = emps.total
      data.value.pendingLeave = pl.total
      data.value.pendingTransfer = pt.total
    } else {
      const [sal, lv, tf] = await Promise.all([mySalaries(), myLeaves(), myTransfers()])
      data.value.mySalaryCount = sal.length
      data.value.myLeaveCount = lv.length
      data.value.myTransferCount = tf.length
      data.value.myPending = lv.filter(l => l.status === 0).length + tf.filter(t => t.status === 0).length
    }
  } catch { /* 拦截器已提示 */ }
})
</script>

<style scoped>
/* 员工端欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #0A59F7 0%, #3375F8 100%);
  border-radius: var(--hm-radius-card);
  color: #fff;
  padding: 26px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(10, 89, 247, 0.25);
}
.wb-title { font-size: 20px; font-weight: 600; }
.wb-sub { font-size: 13px; opacity: 0.85; margin-top: 8px; }
.wb-right { display: flex; align-items: center; gap: 14px; }
.wb-job { font-size: 12px; opacity: 0.85; }
.wb-avatar { background: rgba(255,255,255,0.2); color: #fff; font-size: 20px; font-weight: 600; flex-shrink: 0; }
.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.stat-card { display: flex; align-items: center; gap: 14px; }
.stat-icon { width: 42px; height: 42px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-value { font-size: 22px; font-weight: 600; line-height: 1.2; }
.stat-unit { font-size: 12px; font-weight: 400; color: var(--hm-text-hint); margin-left: 4px; }
.stat-label { font-size: 12px; color: var(--hm-text-secondary); margin-top: 2px; }
.card-head { font-size: 14px; font-weight: 600; margin-bottom: 14px; }
.quick-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 12px; }
.quick-item {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  background: #F8FAFC; border-radius: 12px; padding: 16px 8px;
  text-decoration: none; color: var(--hm-text); font-size: 12px;
  transition: background 0.15s;
}
.quick-item:hover { background: var(--hm-primary-light); }
</style>
