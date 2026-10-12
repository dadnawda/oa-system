<template>
  <div>
    <!-- ============ 员工端个人工作台 ============ -->
    <template v-if="!user.isAdmin">
      <!-- 欢迎横幅 1196×110 -->
      <div class="banner">
        <div class="banner-main">
          <div class="banner-title">Hi，{{ user.userInfo?.name }}，今天也要元气满满！</div>
          <div class="banner-sub">{{ todayStr }} · 你有 {{ pendingCount }} 条申请正在审批中，本月工资待发放</div>
        </div>
        <div class="banner-right">
          <span class="banner-job">{{ user.userInfo?.job || '暂无职位' }} · {{ user.userInfo?.deptName || '未分配部门' }}</span>
          <el-avatar v-if="user.userInfo?.image" :size="64" :src="user.userInfo.image" class="banner-avatar" />
          <div v-else class="banner-avatar"><HmIcon name="hm-user" :size="24" color="#0A59F7" /></div>
        </div>
      </div>

      <!-- KPI 3 × 384×110 -->
      <div class="kpi-row cols-3">
        <div v-for="s in stats" :key="s.label" class="hm-card kpi-card kpi-staff">
          <div class="kpi-bubble" :style="{ background: s.bg }"><HmIcon :name="s.icon" :size="24" :color="s.color" /></div>
          <div class="kpi-text">
            <div class="kpi-value kpi-value-sm">{{ s.value }}<span class="kpi-unit">{{ s.unit }}</span></div>
            <div class="kpi-label">{{ s.label }}</div>
          </div>
        </div>
      </div>

      <!-- 行2：我的申请进度 720×400 + 快捷操作 452×400 -->
      <div class="row2">
        <div class="hm-card progress-card">
          <div class="card-title">我的申请进度</div>
          <div v-if="!progress.length" class="empty-tip">暂无申请记录</div>
          <div class="row-list">
            <div v-for="p in progress" :key="p.key" class="progress-row">
              <div class="pr-title">{{ p.title }}</div>
              <span class="hm-tag pr-tag" :class="p.tagClass">{{ p.statusText }}</span>
              <div class="pr-desc">{{ p.desc }}</div>
              <div class="pr-date">{{ p.date }}</div>
            </div>
          </div>
        </div>

        <div class="hm-card action-card">
          <div class="card-title">快捷操作</div>
          <div class="act-list">
            <div class="act-btn act-primary" @click="$router.push('/leave')">提交请假申请</div>
            <div class="act-btn act-normal" @click="$router.push('/transfer')">申请职位调整</div>
            <div class="act-btn act-light" @click="$router.push('/my-salary')">查看我的工资条</div>
          </div>
          <div class="card-title act-section">最新公告</div>
          <div v-if="!notices.length" class="empty-tip">暂无公告</div>
          <div class="row-list notice-list">
            <div v-for="n in notices" :key="n.id" class="notice-item">
              <div class="ni-title">{{ n.title }}</div>
              <div class="ni-date">{{ n.createTime?.slice(0, 10) }}</div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ============ 管理端工作台 ============ -->
    <template v-else>
      <!-- KPI 4 × 280×120 -->
      <div class="kpi-row cols-4">
        <div v-for="s in stats" :key="s.label" class="hm-card kpi-card kpi-admin">
          <div class="kpi-bubble" :style="{ background: s.bg }"><HmIcon :name="s.icon" :size="24" :color="s.color" /></div>
          <div class="kpi-text">
            <div class="kpi-value">{{ s.value }}<span class="kpi-unit">{{ s.unit }}</span></div>
            <div class="kpi-label">{{ s.label }}</div>
          </div>
        </div>
      </div>

      <!-- 行2：待审批列表 700×300 + 快捷入口 472×300 -->
      <div class="row2">
        <div class="hm-card approve-card">
          <div class="card-title">待审批</div>
          <div v-if="!pendingList.length" class="empty-tip">暂无待审批事项</div>
          <div class="row-list approve-list">
            <div v-for="p in pendingList" :key="p.key" class="approve-row">
              <div class="ar-title">{{ p.title }}</div>
              <span class="hm-tag pr-tag" :class="p.tagClass">{{ p.statusText }}</span>
              <div class="ar-sub">{{ p.sub }}</div>
            </div>
          </div>
        </div>

        <div class="hm-card quick-card">
          <div class="card-title">快捷入口</div>
          <div class="tile-grid">
            <router-link v-for="q in quickLinks" :key="q.path" :to="q.path" class="tile">
              <HmIcon :name="q.icon" :size="24" :color="q.color" />
              <span>{{ q.title }}</span>
            </router-link>
          </div>
        </div>
      </div>

      <!-- 行3：员工概览表 1196×320 -->
      <div class="hm-card overview-card">
        <div class="card-title">员工概览</div>
        <router-link to="/emp" class="card-link">进入管理</router-link>
        <div class="ov-head">
          <span>工号</span><span>姓名</span><span>部门</span><span>职位</span><span>入职日期</span><span>状态</span><span>联系方式</span>
        </div>
        <div class="ov-body">
          <div v-for="(e, i) in overview" :key="e.id" class="ov-row" :class="{ 'ov-first': i === 0 }">
            <span>{{ e.id }}</span>
            <span>{{ e.name }}</span>
            <span><span class="hm-tag hm-tag-info ov-tag">{{ e.deptName || '未分配' }}</span></span>
            <span>{{ e.job || '-' }}</span>
            <span>{{ e.entryDate || '-' }}</span>
            <span><span class="hm-tag ov-tag" :class="e.status === 1 ? 'hm-tag-success' : 'hm-tag-default'">{{ e.status === 1 ? '在职' : '离职' }}</span></span>
            <span class="ov-phone">{{ e.phone || '-' }}</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import HmIcon from '../components/HmIcon.vue'
import { useUserStore } from '../stores/user'
import { pageEmps, listDepts, pageNotices, mySalaries, myLeaves, myTransfers, pageLeaves, pageTransfers } from '../api'

const user = useUserStore()
const data = ref({
  empCount: '-', deptCount: '-', noticeCount: '-',
  myLeaveCount: '-', myTransferCount: '-', mySalaryLatest: '-',
  pendingLeave: 0, pendingTransfer: 0, myPending: 0
})
const pendingList = ref([])
const progress = ref([])
const overview = ref([])
const notices = ref([])

const todayStr = computed(() => {
  const d = new Date()
  const week = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()]
  const p = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} 星期${week}`
})
const pendingCount = computed(() => data.value.myPending)

const LEAVE_TYPE = { 1: '事假', 2: '病假', 3: '年假', 4: '调休', 5: '婚假' }
const statusMeta = s => (s === 0
  ? { text: '审批中', cls: 'hm-tag-warning' }
  : s === 1 ? { text: '已通过', cls: 'hm-tag-success' } : { text: '已驳回', cls: 'hm-tag-danger' })

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
    { label: '我的调岗', value: data.value.myTransferCount, unit: '条', icon: 'hm-my-swap', color: '#FF8D1A', bg: '#FFF3E5' },
    { label: '本月工资', value: data.value.mySalaryLatest, unit: '实发', icon: 'hm-my-card', color: '#00B34A', bg: '#E6F7EE' }
  ]
})

const quickLinks = [
  { path: '/emp', title: '员工管理', icon: 'hm-emp-star', color: '#00B34A' },
  { path: '/dept', title: '部门管理', icon: 'hm-dept', color: '#282F3E' },
  { path: '/notice', title: '查看公告', icon: 'hm-notice', color: '#282F3E' },
  { path: '/leave', title: '请假审批', icon: 'hm-leave', color: '#282F3E' },
  { path: '/salary', title: '工资管理', icon: 'hm-money', color: '#444444' },
  { path: '/profile', title: '个人中心', icon: 'hm-user', color: '#282F3E' }
]

onMounted(async () => {
  try {
    const [depts, noticePage] = await Promise.all([listDepts(), pageNotices({ page: 1, pageSize: 3 })])
    data.value.deptCount = depts.length
    data.value.noticeCount = noticePage.total
    notices.value = noticePage.rows || []

    if (user.isAdmin) {
      const [emps, pl, pt] = await Promise.all([
        pageEmps({ page: 1, pageSize: 4 }),
        pageLeaves({ status: 0, page: 1, pageSize: 1 }),
        pageTransfers({ status: 0, page: 1, pageSize: 1 })
      ])
      data.value.empCount = emps.total
      overview.value = emps.rows || []
      data.value.pendingLeave = pl.total
      data.value.pendingTransfer = pt.total
      // 待审批明细（请假 + 调岗，取最近 3 条）
      const [lv, tf] = await Promise.all([
        pageLeaves({ status: 0, page: 1, pageSize: 3 }),
        pageTransfers({ status: 0, page: 1, pageSize: 3 })
      ])
      const list = [
        ...(lv.rows || []).map(r => ({
          key: 'l' + r.id,
          title: `${r.empName || '员工'} 提交的请假申请`,
          statusText: '待审批', tagClass: 'hm-tag-warning',
          sub: `${(LEAVE_TYPE[r.leaveType] || '请假')} ${r.days ?? ''} 天 · ${r.beginDate || ''} 至 ${r.endDate || ''}`
        })),
        ...(tf.rows || []).map(r => ({
          key: 't' + r.id,
          title: `${r.empName || '员工'} 提交的调岗申请`,
          statusText: '待审批', tagClass: 'hm-tag-warning',
          sub: `${r.fromJob || '原职位'} → ${r.toJob || '新职位'}`
        }))
      ]
      pendingList.value = list.slice(0, 3)
    } else {
      const [sal, lv, tf] = await Promise.all([mySalaries(), myLeaves(), myTransfers()])
      data.value.myLeaveCount = lv.length
      data.value.myTransferCount = tf.length
      data.value.myPending = lv.filter(l => l.status === 0).length + tf.filter(t => t.status === 0).length
      const latest = [...sal].sort((a, b) => (b.salaryMonth || '').localeCompare(a.salaryMonth || ''))[0]
      data.value.mySalaryLatest = latest ? '¥' + Number(latest.actualPay ?? 0).toLocaleString() : '-'
      // 我的申请进度（请假 + 调岗，按时间倒序取 3 条）
      const merged = [
        ...lv.map(r => ({
          key: 'l' + r.id, title: '请假申请',
          statusText: statusMeta(r.status).text, tagClass: statusMeta(r.status).cls,
          desc: `${(LEAVE_TYPE[r.leaveType] || '请假')} ${r.days ?? ''} 天 · ${r.beginDate || ''} ~ ${r.endDate || ''}`,
          date: '提交于 ' + (r.createTime || '').slice(0, 10), sort: r.createTime || ''
        })),
        ...tf.map(r => ({
          key: 't' + r.id, title: '职位调整',
          statusText: statusMeta(r.status).text, tagClass: statusMeta(r.status).cls,
          desc: `${r.fromJob || '原职位'} → ${r.toJob || '新职位'}`,
          date: '提交于 ' + (r.createTime || '').slice(0, 10), sort: r.createTime || ''
        }))
      ].sort((a, b) => b.sort.localeCompare(a.sort))
      progress.value = merged.slice(0, 3)
    }
  } catch { /* 拦截器已提示 */ }
})
</script>

<style scoped>
/* ========== 通用 ========== */
/* 卡片标题：设计卡内 (24,22)，14px */
.card-title { position: absolute; left: 24px; top: 22px; font-size: 14px; font-weight: 600; color: var(--hm-text); }
.card-link { position: absolute; right: 24px; top: 24px; font-size: 12px; color: var(--hm-primary); text-decoration: none; }
.empty-tip { position: absolute; left: 24px; top: 56px; font-size: 12px; color: var(--hm-text-hint); }

/* ========== KPI 行 ========== */
.kpi-row { display: grid; gap: 20px; margin-bottom: 20px; }
.cols-4 { grid-template-columns: repeat(4, 280px); }
.cols-3 { grid-template-columns: repeat(3, 384px); }
.kpi-card { position: relative; }
.kpi-admin { height: 120px; }
.kpi-staff { height: 110px; }
/* 图标气泡 48×48 圆角 12，卡内 (24,36) / 员工端 (24,31) */
.kpi-bubble { position: absolute; width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.kpi-admin .kpi-bubble { left: 24px; top: 36px; }
.kpi-staff .kpi-bubble { left: 24px; top: 31px; }
/* 数值块：设计 x=89.3，数值顶 ≈+42.4（员工端 +39.4） */
.kpi-text { position: absolute; left: 89px; }
.kpi-admin .kpi-text { top: 40px; }
.kpi-staff .kpi-text { top: 37px; }
.kpi-value { font-size: 28px; font-weight: 600; color: var(--hm-text); line-height: 1; }
.kpi-value-sm { font-size: 24px; }
.kpi-unit { font-size: 12px; font-weight: 400; color: var(--hm-text-hint); margin-left: 4px; }
/* 说明文字：设计顶 +75.9（相对卡） */
.kpi-label { font-size: 13px; color: var(--hm-text-secondary); margin-top: 13px; }

/* ========== 员工端欢迎横幅 1196×110 ========== */
.banner {
  position: relative; height: 110px; border-radius: var(--hm-radius-card);
  background: linear-gradient(135deg, #0A59F7 0%, #2E6FF9 100%);
  box-shadow: 0 4px 16px rgba(10, 89, 247, 0.22); margin-bottom: 20px;
}
.banner-title { position: absolute; left: 34px; top: 30px; font-size: 20px; font-weight: 600; color: #fff; }
.banner-sub { position: absolute; left: 34px; top: 66px; font-size: 13px; color: #DCE8FD; }
.banner-right { position: absolute; right: 72px; top: 23px; display: flex; align-items: center; gap: 14px; }
.banner-job { font-size: 12px; color: #DCE8FD; }
.banner-avatar {
  width: 64px; height: 64px; border-radius: 50%; background: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  font-size: 22px; font-weight: 600; color: var(--hm-primary);
}

/* ========== 行2 布局 ========== */
.row2 { display: flex; gap: 8px; }

/* 员工端：我的申请进度 720×400（行 672×92，首行 y=52，步长 104） */
.progress-card { width: 720px; height: 400px; position: relative; }
.progress-card .row-list { position: absolute; left: 24px; top: 52px; width: 672px; }
.progress-row { position: relative; width: 672px; height: 92px; border-radius: 10px; background: #FAFBFC; }
.progress-row + .progress-row { margin-top: 12px; }
.pr-title { position: absolute; left: 16px; top: 18px; font-size: 13px; color: var(--hm-text); }
.pr-tag { position: absolute; right: 16px; top: 12px; width: 60px; justify-content: center; }
.pr-desc { position: absolute; left: 17px; top: 46px; font-size: 12px; color: var(--hm-text-secondary); }
.pr-date { position: absolute; left: 16px; top: 68px; font-size: 11px; color: var(--hm-text-hint); }

/* 员工端：快捷操作 452×400（按钮 y=48 步长 48；分区标题 y=208；列表 y=236 步长 52） */
.action-card { width: 452px; height: 400px; position: relative; }
.act-list { position: absolute; left: 24px; top: 48px; width: 404px; }
.act-btn {
  width: 404px; height: 40px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; cursor: pointer; transition: opacity .15s;
}
.act-btn:hover { opacity: .88; }
.act-btn + .act-btn { margin-top: 8px; }
.act-primary { background: var(--hm-primary); color: #fff; }
.act-normal { background: #F1F3F5; color: var(--hm-text); }
.act-light { background: var(--hm-primary-light); color: var(--hm-primary); }
.act-section { top: 206px; }
.notice-list { position: absolute; left: 24px; top: 236px; width: 404px; }
.notice-item { position: relative; width: 404px; height: 44px; border-radius: 10px; background: #FAFBFC; }
.notice-item + .notice-item { margin-top: 8px; }
.ni-title { position: absolute; left: 15px; top: 8px; font-size: 12px; color: var(--hm-text); }
.ni-date { position: absolute; left: 15px; top: 26px; font-size: 10px; color: var(--hm-text-hint); }

/* 管理端：待审批 700×300（行 652×64，首行 y=56，步长 76） */
.approve-card { width: 700px; height: 300px; position: relative; }
.approve-list { position: absolute; left: 24px; top: 56px; width: 652px; }
.approve-row { position: relative; width: 652px; height: 64px; border-radius: 10px; background: #FAFBFC; }
.approve-row + .approve-row { margin-top: 12px; }
.ar-title { position: absolute; left: 16px; top: 16px; font-size: 13px; color: var(--hm-text); }
.approve-row .pr-tag { right: 24px; top: 10px; width: 52px; }
.ar-sub { position: absolute; left: 16px; top: 39px; font-size: 11px; color: var(--hm-text-hint); }

/* 管理端：快捷入口 472×300（tiles 132×96，起始 (24,52)，gap 12/16） */
.quick-card { width: 472px; height: 300px; position: relative; }
.tile-grid {
  position: absolute; left: 24px; top: 52px;
  display: grid; grid-template-columns: repeat(3, 132px); gap: 16px 12px;
}
.tile {
  height: 96px; border-radius: 12px; background: #F8FAFC;
  display: flex; flex-direction: column; align-items: center; padding-top: 28px;
  text-decoration: none; color: var(--hm-text); font-size: 12px; transition: background .15s;
}
.tile:hover { background: var(--hm-primary-light); }
.tile span { margin-top: 10px; }

/* 管理端：员工概览 1196×320（表头 y=50 高 40；数据行高 52，首行 y=90） */
.overview-card { width: 1196px; height: 320px; margin-top: 20px; position: relative; }
.ov-head, .ov-row {
  display: grid; grid-template-columns: 70px 75px 94px 200px 136px 144px 1fr;
  align-items: center; box-sizing: border-box; width: 1148px; padding-left: 41px;
}
.ov-head {
  position: absolute; left: 24px; top: 50px; height: 40px;
  border-radius: 8px; background: #FAFBFC; font-size: 12px; color: var(--hm-text-hint);
}
.ov-body { position: absolute; left: 24px; top: 90px; width: 1148px; }
.ov-row { height: 52px; font-size: 12px; color: var(--hm-text); border-top: 1px solid #F1F3F5; }
.ov-row.ov-first { border-top: none; }
.ov-tag { height: 22px; line-height: 22px; padding: 0 10px; display: inline-flex; align-items: center; width: fit-content; }
.ov-phone { color: var(--hm-text-secondary); }
</style>
