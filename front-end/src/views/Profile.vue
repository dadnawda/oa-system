<template>
  <div>
    <div class="hm-page-header"><span class="hm-page-title">个人中心</span></div>
    <div class="profile-wrap">
      <div class="hm-card profile-card">
        <el-avatar v-if="info.image" :size="64" :src="info.image" class="avatar" />
        <div v-else class="avatar">{{ initial }}</div>
        <div class="name">{{ info.name }}</div>
        <span class="hm-tag" :class="user.viewIsAdmin ? 'hm-tag-info' : 'hm-tag-default'">
          {{ user.viewIsAdmin ? '管理员' : '普通员工' }}
        </span>
      </div>
      <div class="hm-card" style="flex:1">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="员工工号">{{ info.empId }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ info.name }}</el-descriptions-item>
          <el-descriptions-item label="所属部门">{{ info.deptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{ info.job || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ info.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入职日期">{{ info.entryDate || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getUserInfo } from '../api'
import { useUserStore } from '../stores/user'

const user = useUserStore()
const info = ref({})

const initial = computed(() => (info.value.name || '用')[0])

onMounted(async () => {
  info.value = await getUserInfo()
  // 只刷新资料，不动登录身份（否则"员工身份"会被真实角色覆盖 → 界面翻成管理端）
  user.updateUserInfo({ ...info.value, role: user.userInfo?.role, empId: info.value.empId })
})
</script>

<style scoped>
.profile-wrap { display: flex; gap: 14px; align-items: stretch; }
.profile-card {
  width: 200px; display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 10px; padding: 28px 16px;
}
.avatar {
  width: 64px; height: 64px; border-radius: 50%;
  background: var(--hm-primary-light); color: var(--hm-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 24px; font-weight: 600;
}
.name { font-size: 16px; font-weight: 600; }
</style>
