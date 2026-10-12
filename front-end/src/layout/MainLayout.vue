<template>
  <div class="layout">
    <!-- 侧边导航 -->
    <aside class="side">
      <div class="brand">
        <div class="brand-logo"><AppIcon name="grid" :size="15" color="#fff" /></div>
        <span class="brand-name">OA 系统</span>
      </div>
      <nav class="menu">
        <router-link v-for="m in menus" :key="m.path" :to="m.path" class="menu-item" active-class="active">
          <AppIcon :name="m.icon" :size="15" />
          <span>{{ m.title }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- 主区域 -->
    <div class="main">
      <header class="topbar">
        <span class="top-title">{{ route.meta.title || '工作台' }}</span>
        <div class="top-right">
          <span class="welcome">{{ user.userInfo?.name }} · {{ user.isAdmin ? '管理员' : '员工' }}</span>
          <div class="avatar">{{ (user.userInfo?.name || '用')[0] }}</div>
          <el-button text size="small" @click="onLogout">
            <AppIcon name="logout" :size="14" style="margin-right:4px" />退出
          </el-button>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const user = useUserStore()

const ALL_MENUS = [
  { path: '/dashboard', title: '工作台', icon: 'home' },
  { path: '/dept', title: '部门管理', icon: 'building', admin: true },
  { path: '/emp', title: '员工管理', icon: 'people', admin: true },
  { path: '/notice', title: '公告中心', icon: 'doc' },
  { path: '/salary', title: '工资管理', icon: 'money', admin: true },
  { path: '/my-salary', title: '我的工资', icon: 'clock', staff: true },
  { path: '/leave', title: '请假审批', icon: 'leave' },
  { path: '/transfer', title: '调岗审批', icon: 'swap' },
  { path: '/profile', title: '个人中心', icon: 'user' }
]

const menus = computed(() =>
  ALL_MENUS.filter(m => !m.admin || user.isAdmin).filter(m => !m.staff || !user.isAdmin)
)

function onLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
    user.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.side {
  width: 180px; background: #fff; padding: 14px 10px;
  border-right: 0.5px solid var(--hm-border);
  display: flex; flex-direction: column; gap: 2px; flex-shrink: 0;
}
.brand { display: flex; align-items: center; gap: 8px; padding: 6px 10px; margin-bottom: 10px; }
.brand-logo {
  width: 26px; height: 26px; border-radius: 8px;
  background: linear-gradient(135deg, #0A59F7, #3D8BFF);
  display: flex; align-items: center; justify-content: center;
}
.brand-name { font-size: 13px; font-weight: 600; }
.menu { display: flex; flex-direction: column; gap: 2px; }
.menu-item {
  display: flex; align-items: center; gap: 9px;
  padding: 9px 12px; border-radius: 10px;
  color: var(--hm-text-secondary); text-decoration: none; font-size: 13px;
  transition: background 0.15s, color 0.15s;
}
.menu-item:hover { background: #F5F7FA; }
.menu-item.active { background: var(--hm-primary-light); color: var(--hm-primary); font-weight: 600; }
.main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.topbar {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; padding: 12px 20px;
  border-bottom: 0.5px solid var(--hm-border);
  position: sticky; top: 0; z-index: 10;
}
.top-title { font-size: 14px; color: var(--hm-text-secondary); }
.top-right { display: flex; align-items: center; gap: 10px; }
.welcome { font-size: 12px; }
.avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: var(--hm-primary-light); color: var(--hm-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
}
.content { padding: 16px 20px; flex: 1; }
</style>
