<template>
  <div>
    <div class="hm-page-header">
      <span class="hm-page-title">工作台</span>
    </div>
    <div class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="hm-card stat-card">
        <div class="stat-icon" :style="{ background: s.bg }"><AppIcon :name="s.icon" :size="18" :color="s.color" /></div>
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
          <AppIcon :name="q.icon" :size="18" color="#0A59F7" />
          <span>{{ q.title }}</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { useUserStore } from '../stores/user'
import { pageEmps, listDepts, pageNotices, mySalaries, myLeaves, myTransfers, pageLeaves, pageTransfers } from '../api'

const user = useUserStore()
const data = ref({ empCount: '-', deptCount: '-', noticeCount: '-', mySalaryCount: '-', myLeaveCount: '-', myTransferCount: '-', pendingLeave: 0, pendingTransfer: 0 })

const stats = computed(() => {
  if (user.isAdmin) {
    return [
      { label: '员工总数', value: data.value.empCount, unit: '人', icon: 'people', color: '#0A59F7', bg: '#E8F0FE' },
      { label: '部门数量', value: data.value.deptCount, unit: '个', icon: 'building', color: '#00B34A', bg: '#E6F7EE' },
      { label: '公告数量', value: data.value.noticeCount, unit: '条', icon: 'doc', color: '#FF8D1A', bg: '#FFF3E5' },
      { label: '待审批', value: data.value.pendingLeave + data.value.pendingTransfer, unit: '件', icon: 'check', color: '#E02020', bg: '#FDECEC' }
    ]
  }
  return [
    { label: '我的请假申请', value: data.value.myLeaveCount, unit: '条', icon: 'leave', color: '#0A59F7', bg: '#E8F0FE' },
    { label: '我的调岗申请', value: data.value.myTransferCount, unit: '条', icon: 'swap', color: '#00B34A', bg: '#E6F7EE' },
    { label: '我的工资记录', value: data.value.mySalaryCount, unit: '条', icon: 'clock', color: '#FF8D1A', bg: '#FFF3E5' },
    { label: '公告数量', value: data.value.noticeCount, unit: '条', icon: 'doc', color: '#E02020', bg: '#FDECEC' }
  ]
})

const quickLinks = computed(() => {
  const base = [
    { path: '/notice', title: '查看公告', icon: 'doc' },
    { path: '/leave', title: '请假申请', icon: 'leave' },
    { path: '/transfer', title: '职位调整', icon: 'swap' },
    { path: '/profile', title: '个人中心', icon: 'user' }
  ]
  return user.isAdmin
    ? [{ path: '/emp', title: '员工管理', icon: 'people' }, { path: '/dept', title: '部门管理', icon: 'building' }, ...base.slice(0, 2), { path: '/salary', title: '工资管理', icon: 'money' }]
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
    }
  } catch { /* 拦截器已提示 */ }
})
</script>

<style scoped>
.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.stat-card { display: flex; align-items: center; gap: 14px; }
.stat-icon { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
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
