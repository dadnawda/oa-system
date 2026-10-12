<template>
  <div class="layout">
    <!-- 侧边导航（设计稿：宽 200，菜单项 180×40 圆角 10，步距 46） -->
    <aside class="side">
      <div class="brand">
        <div class="brand-logo"><AppIcon name="grid" :size="16" color="#fff" /></div>
        <span class="brand-name">OA 系统</span>
      </div>
      <div v-if="!user.isAdmin" class="side-role">员工端</div>
      <nav class="menu">
        <router-link v-for="m in menus" :key="m.path" :to="m.path" class="menu-item" active-class="active">
          <AppIcon :name="m.icon" :size="16" />
          <span>{{ m.title }}</span>
        </router-link>
      </nav>
      <button class="side-logout" @click="onLogout">
        <AppIcon name="logout" :size="15" />
        <span>退出登录</span>
      </button>
    </aside>

    <!-- 主区域（顶栏高 64，内容区 padding 24/20/24/24） -->
    <div class="main">
      <header class="topbar">
        <span class="top-title">{{ route.meta.title || '工作台' }}</span>
        <div class="top-right">
          <span class="welcome">{{ user.userInfo?.name }} · {{ user.isAdmin ? '管理员' : '员工' }}</span>
          <el-avatar v-if="user.userInfo?.image" :size="32" :src="user.userInfo.image" class="avatar" />
          <div v-else class="avatar" :class="user.isAdmin ? 'avatar-admin' : 'avatar-staff'">
            <AppIcon name="user" :size="15" :color="user.isAdmin ? '#0A59F7' : '#00B34A'" />
          </div>
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
  { path: '/emp', title: '员工管理', icon: 'users', admin: true },
  { path: '/notice', title: '公告中心', icon: 'megaphone' },
  { path: '/salary', title: '工资管理', icon: 'wallet', admin: true },
  { path: '/my-salary', title: '我的工资', icon: 'receipt', staff: true },
  { path: '/leave', title: '请假审批', icon: 'calendar-check' },
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

/* ===== 侧栏：200 宽 ===== */
.side {
  width: 200px; background: #fff; padding: 22px 10px 20px;
  border-right: 0.5px solid var(--hm-border);
  display: flex; flex-direction: column; flex-shrink: 0;
}
.brand { display: flex; align-items: center; gap: 10px; padding-left: 10px; margin-bottom: 6px; }
.brand-logo {
  width: 28px; height: 28px; border-radius: 8px;
  background: #0A59F7;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.brand-name { font-size: 14px; font-weight: 600; color: var(--hm-text); }
.side-role { font-size: 10px; color: var(--hm-text-hint); padding-left: 10px; margin-bottom: 10px; }
.menu { display: flex; flex-direction: column; gap: 6px; margin-top: 16px; }
.menu-item {
  display: flex; align-items: center; gap: 10px; height: 40px;
  padding: 0 20px; border-radius: 10px;
  color: var(--hm-text-secondary); text-decoration: none; font-size: 13px;
  transition: background 0.15s, color 0.15s;
}
.menu-item:hover { background: #F5F7FA; }
.menu-item.active { background: var(--hm-primary-light); color: var(--hm-primary); font-weight: 600; }
.side-logout {
  margin-top: auto; height: 40px; border: none; border-radius: 10px;
  background: #F5F7FA; color: var(--hm-text-secondary); font-size: 13px;
  display: flex; align-items: center; gap: 10px; padding: 0 20px;
  cursor: pointer; font-family: inherit;
}
.side-logout:hover { background: #EDEFF2; }

/* ===== 主区域 ===== */
.main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.topbar {
  height: 64px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; padding: 0 20px 0 24px;
  border-bottom: 0.5px solid var(--hm-border);
  position: sticky; top: 0; z-index: 10;
}
.top-title { font-size: 14px; color: var(--hm-text-secondary); }
.top-right { display: flex; align-items: center; gap: 12px; }
.welcome { font-size: 12px; color: var(--hm-text); }
.avatar {
  width: 32px; height: 32px; border-radius: 50%; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
}
.avatar-admin { background: #E8F0FE; }
.avatar-staff { background: #E6F7EE; }
.content { padding: 24px 20px 24px 24px; flex: 1; min-width: 0; }
</style>
