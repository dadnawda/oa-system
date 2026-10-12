<template>
  <div class="layout">
    <!-- 侧栏：悬浮液态玻璃（可拖动 + 鼠标跟随高光/折射；双击复位） -->
    <aside ref="panelRef" class="side" :class="{ hovered }">
      <!-- 鼠标跟随的镜面高光（lerp 缓动 → 拖尾感） -->
      <div class="glass-specular" aria-hidden="true"></div>
      <!-- 精细颗粒（磨砂玻璃表面的微噪点） -->
      <div class="glass-grain" aria-hidden="true"></div>
      <div class="side-inner">
        <div class="brand">
          <div class="brand-logo"><AppIcon name="grid" :size="16" color="#fff" /></div>
          <span class="brand-name">OA 系统</span>
        </div>
        <div class="side-role">{{ user.viewIsAdmin ? '管理员端' : '员工端' }}</div>
        <nav ref="menuRef" class="menu" @pointermove="onMenuMove" @pointerleave="onMenuLeave">
          <!-- 选择胶囊：跟随鼠标移动（无轨道、无融合滤镜 → 干净单色，无拉伸/无残影） -->
          <div class="menu-liquid" aria-hidden="true">
            <div class="blob" :style="indicatorStyle"></div>
          </div>
          <router-link v-for="m in menus" :key="m.path" :to="m.path" class="menu-item" active-class="active">
            <AppIcon :name="m.icon" :size="16" />
            <span>{{ m.title }}</span>
          </router-link>
        </nav>
        <button class="side-logout" @click="onLogout">
          <AppIcon name="logout" :size="15" />
          <span>退出登录</span>
        </button>
      </div>
    </aside>

    <!-- 主区域（顶栏高 64，内容区 padding 24/20/24/24） -->
    <div class="main">
      <header class="topbar">
        <div class="top-left">
          <span class="top-dot" :class="{ 'dot-admin': user.viewIsAdmin }"></span>
          <span class="top-title">{{ route.meta.title || '工作台' }}</span>
        </div>
        <div class="top-right">
          <div class="user-chip" :class="{ 'chip-admin': user.viewIsAdmin }">
            <span class="user-name">{{ user.userInfo?.name }}</span>
            <span class="user-sep"></span>
            <span class="user-role">{{ user.viewIsAdmin ? '管理员' : '员工' }}</span>
            <el-avatar v-if="user.userInfo?.image" :size="30" :src="user.userInfo.image" class="avatar" />
            <div v-else class="avatar" :class="user.viewIsAdmin ? 'avatar-admin' : 'avatar-staff'">
              <AppIcon name="user" :size="15" :color="user.viewIsAdmin ? '#0A59F7' : '#00B34A'" />
            </div>
          </div>
        </div>
      </header>
      <main class="content">
        <!-- 路由切换动画：out-in 错开（旧页先退场再进新页），避免重叠跳动 -->
        <router-view v-slot="{ Component, route: cur }">
          <Transition name="page" mode="out-in">
            <component :is="Component" :key="cur.path" />
          </Transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import { useUserStore } from '../stores/user'
import { useLiquidGlass } from '../composables/useLiquidGlass'
import { useDockIndicator } from '../composables/useDockIndicator'

const route = useRoute()
const router = useRouter()
const user = useUserStore()

// 液态玻璃：鼠标跟随高光 + 折射强度脉动（基础折射强度 18，见 LiquidGlassFilter.vue）
const { panelRef, hovered } = useLiquidGlass({ baseScale: 18 })

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
  ALL_MENUS.filter(m => !m.admin || user.viewIsAdmin).filter(m => !m.staff || !user.viewIsAdmin)
)

// 选中菜单索引 → 驱动玻璃滑块位置（菜单项高 40 + 间距 6 = 步距 46）
const activeIndex = computed(() => {
  const i = menus.value.findIndex(m => m.path === route.path)
  return i < 0 ? 0 : i
})

// 菜单玻璃选择框：跟随鼠标 + 弹簧液体形变（未悬停时回到当前路由项）
const { menuRef, indicatorStyle, onMenuMove, onMenuLeave, syncActive } = useDockIndicator({
  activeIndex,
  itemHeight: 40,
  step: 46
})
watch(activeIndex, () => syncActive())

// 路由切换后回到顶部：配合切换动画，避免新页面停在上一页的滚动位置
watch(() => route.path, () => {
  window.scrollTo({ top: 0, behavior: 'auto' })
})

function onLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
    user.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
/* 流光边缘角度动画（Chromium 85+ 支持 @property） */
@property --glass-angle { syntax: '<angle>'; initial-value: 0deg; inherits: false; }

.layout { min-height: 100vh; }

/* ========== 侧栏：悬浮液态玻璃面板（固定位置） ========== */
.side {
  position: fixed; left: 12px; top: 12px; bottom: 12px;   /* 上下留 12px，中间贯通到底部 */
  width: 200px;
  border-radius: var(--lg-radius);
  background: var(--glass-bg);
  -webkit-backdrop-filter: url(#lg-refraction) blur(var(--lg-blur)) saturate(var(--lg-saturate));
  backdrop-filter: var(--glass-blur-refract);
  border: 1px solid rgba(255, 255, 255, var(--lg-border-opacity));
  /* 双层景深阴影 + 顶部内高光（精致感来源） */
  box-shadow:
    0 1px 1px rgba(16, 32, 72, 0.04),
    0 12px 40px rgba(16, 32, 72, 0.13),
    inset 0 1px 0 rgba(255, 255, 255, 0.85);
  overflow: hidden;          /* 裁掉跟随高光/颗粒的外溢，保持胶囊轮廓 */
  z-index: 20;
}

/* 内容层（唯一可滚动区域，避免覆盖层被滚动带走） */
.side-inner {
  position: relative; z-index: 2; height: 100%;
  padding: 22px 10px 20px;
  display: flex; flex-direction: column;
  overflow: hidden auto; scrollbar-width: none;
}
.side-inner::-webkit-scrollbar { display: none; }

/* 鼠标跟随镜面高光：坐标由 JS 写入 --mx/--my（lerp 缓动 → 拖尾） */
.glass-specular {
  position: absolute; left: 0; top: 0; width: 260px; height: 260px;
  border-radius: 50%; pointer-events: none; z-index: 1;
  background: radial-gradient(circle at 50% 50%,
    rgba(255, 255, 255, 0.55) 0%, rgba(255, 255, 255, 0.22) 32%, rgba(255, 255, 255, 0) 68%);
  mix-blend-mode: plus-lighter;
  opacity: calc(var(--lg-glow, 0) * 0.72);
  transform: translate3d(calc(var(--mx, 100px) - 50%), calc(var(--my, 160px) - 50%), 0);
  transition: opacity 0.28s ease;
  will-change: transform, opacity;
}

/* 微颗粒：磨砂玻璃表面的细腻噪点（低透明 + overlay） */
.glass-grain {
  position: absolute; inset: 0; border-radius: var(--lg-radius);
  pointer-events: none; z-index: 1; opacity: 0.22; mix-blend-mode: overlay;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3'/%3E%3C/filter%3E%3Crect width='120' height='120' filter='url(%23n)' opacity='0.55'/%3E%3C/svg%3E");
}

/* 流光边缘（彩虹镶边） */
.side::after {
  content: ''; position: absolute; inset: 0; border-radius: var(--lg-radius);
  padding: 1px; pointer-events: none; z-index: 3; opacity: var(--lg-aurora);
  background: conic-gradient(from var(--glass-angle),
    rgba(124, 255, 178, 0.45), rgba(88, 217, 249, 0.45), rgba(175, 126, 235, 0.42),
    rgba(255, 184, 108, 0.40), rgba(124, 255, 178, 0.45));
  -webkit-mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
  -webkit-mask-composite: xor; mask-composite: exclude;
  animation: glass-flow 9s linear infinite;
}
@keyframes glass-flow { to { --glass-angle: 360deg; } }

/* 品牌区（拖动把手） */
.brand {
  display: flex; align-items: center; gap: 10px; padding-left: 10px; margin-bottom: 6px;  flex-shrink: 0;
}
.brand-logo {
  width: 28px; height: 28px; border-radius: 8px; background: #0A59F7;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(10, 89, 247, 0.35);
}
.brand-name { font-size: 14px; font-weight: 600; color: var(--hm-text); }

.side-role { font-size: 10px; color: var(--hm-text-hint); padding-left: 10px; margin-bottom: 10px; flex-shrink: 0; }

/* 菜单 + 玻璃滑块 */
.menu { position: relative; display: flex; flex-direction: column; gap: 6px; margin-top: 16px; }
/* 液滴层：只承载跟随鼠标的胶囊（不再用 goo 融合 → 不会被拉长、无边缘毛刺） */
.menu-liquid {
  position: absolute; inset: 0; pointer-events: none; z-index: 1;
}

/* 选择胶囊：单一背景色（一体化），无渐变分界；1px 白色内描边定形 */
.blob {
  position: absolute; left: 0; top: 0; width: 180px; height: 40px;
  border-radius: 12px; will-change: transform; transform-origin: center center;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.75), 0 1px 4px rgba(16, 32, 72, 0.06);
}
.menu-item {
  position: relative; z-index: 2; display: flex; align-items: center; gap: 10px; height: 40px;
  padding: 0 20px; border-radius: 10px;
  color: var(--hm-text-secondary); text-decoration: none; font-size: 13px;
  transition: color 0.18s, background 0.18s;
}
.menu-item:hover { background: rgba(255, 255, 255, 0.62); }
.menu-item.active { color: var(--hm-primary); font-weight: 600; }

.side-logout {
  margin-top: auto; height: 40px; border: none; border-radius: 10px;
  background: rgba(245, 247, 250, 0.72); color: var(--hm-text-secondary); font-size: 13px;
  display: flex; align-items: center; gap: 10px; padding: 0 20px;
  cursor: pointer; font-family: inherit; flex-shrink: 0;
  transition: background 0.18s;
}
.side-logout:hover { background: rgba(237, 239, 242, 0.92); }

/* ========== 主区域 ========== */
.main { margin-left: 224px; display: flex; flex-direction: column; min-height: 100vh; min-width: 0; }

/* ========== 顶栏：悬浮玻璃胶囊（与侧栏同款材质，内容从下方滚过时透出模糊折射） ========== */
.topbar {
  position: fixed; left: 224px; right: 20px; top: 12px; height: 56px;
  z-index: 30;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 14px 0 20px;
  border-radius: 16px;
  background: var(--glass-bg);
  -webkit-backdrop-filter: url(#lg-refraction) blur(var(--lg-blur)) saturate(var(--lg-saturate));
  backdrop-filter: var(--glass-blur-refract);
  border: 1px solid rgba(255, 255, 255, var(--lg-border-opacity));
  box-shadow:
    0 6px 24px rgba(16, 32, 72, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.85);
}
.top-left { display: flex; align-items: center; gap: 9px; }
/* 标题前的小色点：管理员蓝 / 员工绿，给顶栏一个视觉锚点 */
.top-dot {
  width: 6px; height: 6px; border-radius: 50%; background: #00B34A;
  box-shadow: 0 0 0 3px rgba(0, 179, 74, 0.14);
}
.top-dot.dot-admin { background: var(--hm-primary); box-shadow: 0 0 0 3px rgba(10, 89, 247, 0.14); }
.top-title { font-size: 15px; font-weight: 600; color: var(--hm-text); }

/* 右侧用户信息：一枚玻璃小胶囊（替代原本平铺的文字+头像） */
.top-right { display: flex; align-items: center; }
.user-chip {
  display: flex; align-items: center; gap: 8px;
  height: 40px; padding: 0 5px 0 14px; border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.7);
  transition: background 0.18s, box-shadow 0.18s;
}
.user-chip:hover { background: rgba(255, 255, 255, 0.78); box-shadow: 0 2px 10px rgba(16, 32, 72, 0.08); }
.chip-admin { background: rgba(232, 240, 254, 0.62); }
.user-name { font-size: 12px; color: var(--hm-text); }
.user-sep { width: 1px; height: 12px; background: rgba(17, 17, 17, 0.12); }
.user-role { font-size: 11px; color: var(--hm-text-secondary); }
.avatar {
  width: 30px; height: 30px; border-radius: 50%; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
}
.avatar-admin { background: #E8F0FE; }
.avatar-staff { background: #E6F7EE; }

/* 内容区：顶栏改为 fixed 悬浮后，上内边距补回原来的 88px 起始位置 */
.content { padding: 88px 20px 24px 24px; flex: 1; min-width: 0; }

/* ========== 降级与无障碍 ========== */
@supports not (backdrop-filter: url(#lg-refraction) blur(1px)) {
  .side, .topbar { backdrop-filter: var(--glass-blur); -webkit-backdrop-filter: var(--glass-blur); }
}
@supports not (backdrop-filter: blur(1px)) {
  .side, .topbar { background: rgba(255, 255, 255, 0.92); }
}
@media (prefers-reduced-motion: reduce) {
  .side::after { animation: none; opacity: 0; }
  .glass-specular { transition: none; }
}

/* ========== 右侧内容区路由切换动画 ==========
   进场：淡入 + 上浮 + 轻微模糊（呼应液态玻璃的凝聚感）
   退场：更快、更轻（横向/纵向位移方向相反，形成推开→聚拢的连贯感） */
.page-enter-active {
  transition: opacity 0.26s ease, transform 0.32s cubic-bezier(0.22, 0.61, 0.36, 1), filter 0.26s ease;
}
.page-leave-active {
  transition: opacity 0.16s ease, transform 0.18s ease, filter 0.16s ease;
}
.page-enter-from { opacity: 0; transform: translateY(10px) scale(0.996); filter: blur(4px); }
.page-leave-to { opacity: 0; transform: translateY(-6px) scale(0.998); filter: blur(3px); }

/* 尊重系统减少动态效果设置 */
@media (prefers-reduced-motion: reduce) {
  .page-enter-active, .page-leave-active { transition: none; }
  .page-enter-from, .page-leave-to { opacity: 1; transform: none; filter: none; }
}
</style>
