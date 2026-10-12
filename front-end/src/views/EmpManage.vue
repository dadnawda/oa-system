<template>
  <div>
    <div class="hm-page-header"><span class="hm-page-title">员工管理</span></div>

    <!-- 搜索栏 -->
    <div class="hm-card" style="margin-bottom:14px">
      <div class="search-bar">
        <el-button class="back-btn" @click="() => $router.back()"><AppIcon name="arrow-left" :size="14" /></el-button>
        <el-input v-model="query.name" placeholder="搜索姓名 / 工号" clearable style="width:280px" @keyup.enter="load" @clear="load">
          <template #prefix><AppIcon name="search" :size="14" /></template>
        </el-input>
        <el-button class="scan-btn" @click="load"><AppIcon name="scan" :size="16" /></el-button>
        <el-select v-model="query.gender" placeholder="性别" clearable style="width:110px" @change="load">
          <el-option label="男" :value="1" /><el-option label="女" :value="2" />
        </el-select>
        <el-select v-model="query.status" placeholder="在职状态" style="width:110px" @change="load">
          <el-option label="在职" :value="1" /><el-option label="离职" :value="0" /><el-option label="全部" :value="null" />
        </el-select>
        <div style="flex:1" />
        <el-button type="primary" @click="openAdd"><AppIcon name="plus" :size="13" style="margin-right:4px" />新增员工</el-button>
      </div>
    </div>

    <!-- 状态筛选行 -->
    <div class="hm-card chips-bar">
      <span class="chips-label">在职状态</span>
      <span class="hm-tag cursor" :class="query.status === 1 ? 'hm-tag-info' : 'hm-tag-default'" @click="setStatus(1)">在职</span>
      <span class="hm-tag cursor" :class="query.status === 0 ? 'hm-tag-warning' : 'hm-tag-default'" @click="setStatus(0)">离职</span>
      <span class="hm-tag cursor" :class="query.status === null ? 'hm-tag-info' : 'hm-tag-default'" @click="setStatus(null)">全部</span>
      <div style="flex:1" />
      <el-button v-if="selection.length" link type="danger" @click="onBatchDelete">批量离职({{ selection.length }})</el-button>
    </div>

    <!-- 表格 -->
    <div class="hm-card">
      <el-table :data="rows" v-loading="loading" stripe @selection-change="s => selection = s">
        <el-table-column type="selection" width="45" />
        <el-table-column prop="id" label="工号" width="70" />
        <el-table-column label="姓名" width="130">
          <template #default="{ row }">
            <div class="name-cell">
              <el-avatar :size="30" :src="row.image || undefined" style="background:#E8F0FE;color:#0A59F7;font-weight:600;flex-shrink:0">{{ row.name[0] }}</el-avatar>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            <span class="hm-tag" :class="row.gender === 1 ? 'hm-tag-info' : 'hm-tag-danger'">{{ row.gender === 1 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="job" label="职位" min-width="130" />
        <el-table-column prop="deptName" label="部门" width="110" />
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="hm-tag" :class="row.status === 1 ? 'hm-tag-success' : 'hm-tag-default'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="right" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="danger" size="small" @click="onDeleteOne(row)">离职</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:12px">
        <el-pagination background layout="total, prev, pager, next, sizes"
          :total="total" v-model:current-page="query.page" v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]" @change="load" />
      </div>
    </div>

    <!-- 新增/编辑抽屉 -->
    <el-drawer v-model="drawer.visible" :title="drawer.isEdit ? '编辑员工' : '新增员工'" size="480px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="姓名" required><el-input v-model="drawer.form.name" maxlength="32" /></el-form-item>
        <el-form-item label="性别" required>
          <el-radio-group v-model="drawer.form.gender">
            <el-radio :value="1">男</el-radio><el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="drawer.form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="drawer.form.job" maxlength="50" /></el-form-item>
        <el-form-item label="所属部门" required>
          <el-select v-model="drawer.form.deptId" placeholder="请选择部门（工号按部门自动生成）" style="width:100%">
            <el-option v-for="d in depts" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
          <div style="font-size:11px;color:var(--hm-text-hint);margin-top:4px">工号自动生成：部门号×100 + 部门在职人数 + 1</div>
        </el-form-item>
        <el-form-item label="入职日期">
          <el-date-picker v-model="drawer.form.entryDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>

        <el-divider content-position="left">工作经历</el-divider>
        <div v-for="(e, i) in drawer.form.exprList" :key="i" class="expr-row">
          <el-date-picker v-model="e.range" type="daterange" value-format="YYYY-MM-DD"
                          start-placeholder="开始" end-placeholder="结束" style="width:220px" />
          <el-input v-model="e.job" placeholder="职位" style="width:110px" />
          <el-select v-model="e.deptId" placeholder="部门" style="width:100px">
            <el-option v-for="d in depts" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
          <el-button link type="danger" @click="drawer.form.exprList.splice(i, 1)">
            <AppIcon name="close" :size="14" />
          </el-button>
        </div>
        <el-button plain style="width:100%" @click="addExprRow">＋ 添加一条工作经历</el-button>
      </el-form>
      <template #footer>
        <el-button @click="drawer.visible = false">取消</el-button>
        <el-button type="primary" :loading="drawer.saving" @click="onSave">保存</el-button>
      </template>
    </el-drawer>

    <!-- 详情对话框 -->
    <el-dialog v-model="detail.visible" title="员工详情" width="560px">
      <template v-if="detail.data">
        <!-- 照片区：展示 + 上传 -->
        <div class="photo-area">
          <el-image v-if="detail.data.image" :src="detail.data.image" fit="cover" class="photo"
                    :preview-src-list="[detail.data.image]" />
          <div v-else class="photo photo-empty">{{ detail.data.name[0] }}</div>
          <div class="photo-info">
            <div class="photo-name">{{ detail.data.name }} <span class="hm-tag hm-tag-info">{{ detail.data.id }}</span></div>
            <div class="photo-job">{{ detail.data.job || '暂无职位' }} · {{ detail.data.deptName || '未分配部门' }}</div>
            <el-upload v-if="rowEditable" :show-file-list="false" accept="image/jpeg,image/png,image/webp"
                       :http-request="doUploadPhoto" style="margin-top:8px">
              <el-button type="primary" plain size="small" :loading="uploading">
                <AppIcon name="edit" :size="12" style="margin-right:4px" />{{ detail.data.image ? '更换照片' : '上传照片' }}
              </el-button>
            </el-upload>
            <div v-else style="font-size:11px;color:var(--hm-text-hint);margin-top:8px">离职员工档案，照片不可修改</div>
          </div>
        </div>
        <el-divider style="margin:12px 0" />
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="性别">{{ detail.data.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.data.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{ detail.data.job || '-' }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ detail.data.deptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入职日期">{{ detail.data.entryDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="在职状态">
            <span class="hm-tag" :class="detail.data.status === 1 ? 'hm-tag-success' : 'hm-tag-default'">
              {{ detail.data.status === 1 ? '在职' : '离职' }}
            </span>
          </el-descriptions-item>
        </el-descriptions>
        <el-divider content-position="left">工作经历</el-divider>
        <el-table :data="detail.data.exprList || []" size="small">
          <el-table-column prop="beginDate" label="开始" width="110" />
          <el-table-column prop="endDate" label="结束" width="110" />
          <el-table-column prop="deptName" label="部门" width="110" />
          <el-table-column prop="job" label="职位" />
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import HmIcon from '../components/HmIcon.vue'
import { pageEmps, getEmp, addEmp, updateEmp, deleteEmps, listDepts, uploadPhoto } from '../api'

const query = reactive({ name: '', gender: null, status: 1, page: 1, pageSize: 10 })
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const selection = ref([])
const depts = ref([])
const drawer = reactive({ visible: false, isEdit: false, saving: false, form: null })
const detail = reactive({ visible: false, data: null })

async function load() {
  loading.value = true
  try {
    const params = { ...query, name: query.name || undefined, gender: query.gender ?? undefined,
      status: query.status ?? undefined }
    const page = await pageEmps(params)
    rows.value = page.rows
    total.value = page.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.name = ''; query.gender = null; query.status = 1; query.page = 1
  load()
}

function setStatus(v) {
  query.status = v
  query.page = 1
  load()
}

function emptyForm() {
  return { id: null, name: '', gender: 1, phone: '', job: '', entryDate: '', deptId: null, exprList: [] }
}

function openAdd() {
  drawer.isEdit = false
  drawer.form = emptyForm()
  drawer.visible = true
}

async function openEdit(row) {
  const full = await getEmp(row.id)
  drawer.isEdit = true
  drawer.form = {
    id: full.id, name: full.name, gender: full.gender, phone: full.phone, job: full.job,
    entryDate: full.entryDate, deptId: full.deptId,
    exprList: (full.exprList || []).map(e => ({ ...e, range: [e.beginDate, e.endDate] }))
  }
  drawer.visible = true
}

function addExprRow() {
  drawer.form.exprList.push({ range: null, deptId: null, job: '' })
}

async function onSave() {
  const f = drawer.form
  if (!f.name?.trim()) { ElMessage.warning('姓名不能为空'); return }
  if (!f.deptId) { ElMessage.warning('请选择所属部门（用于生成工号）'); return }
  drawer.saving = true
  try {
    const exprList = (f.exprList || []).filter(e => e.range || e.job).map(e => ({
      beginDate: e.range?.[0] || null, endDate: e.range?.[1] || null, deptId: e.deptId || null, job: e.job || null
    }))
    const payload = { ...f, exprList }
    if (drawer.isEdit) await updateEmp(f.id, payload)
    else await addEmp(payload)
    ElMessage.success('保存成功')
    drawer.visible = false
    await load()
  } finally { drawer.saving = false }
}

async function openDetail(row) {
  detail.data = await getEmp(row.id)
  detail.visible = true
}

async function doDelete(ids) {
  await ElMessageBox.confirm(`确定将选中的 ${ids.length} 名员工标记为离职吗？（档案保留，默认列表不再显示）`, '离职确认', { type: 'warning' })
  await deleteEmps(ids)
  ElMessage.success('已标记离职，档案保留')
  await load()
}

const onDeleteOne = row => doDelete([row.id])
const onBatchDelete = () => doDelete(selection.value.map(r => r.id))

// ===== 照片上传 =====
const uploading = ref(false)
// 离职员工档案不允许改照片（与后端规则呼应：在职才可编辑）
const rowEditable = computed(() => detail.data?.status === 1)

async function doUploadPhoto({ file }) {
  uploading.value = true
  try {
    await uploadPhoto(detail.data.id, file)
    ElMessage.success('照片上传成功')
    // 重新拉取详情与列表，展示新照片
    detail.data = await getEmp(detail.data.id)
    await load()
  } finally { uploading.value = false }
}

onMounted(async () => {
  depts.value = await listDepts()
  await load()
})
</script>

<style scoped>
.search-bar { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.back-btn, .scan-btn { width: 36px; padding: 0; }
.chips-bar { display: flex; gap: 12px; align-items: center; margin-bottom: 14px; padding: 12px 20px; }
.chips-label { font-size: 12px; color: var(--hm-text-secondary); }
.cursor { cursor: pointer; }
.expr-row { display: flex; gap: 6px; align-items: center; margin-bottom: 8px; }
.name-cell { display: flex; align-items: center; gap: 8px; }
.photo-area { display: flex; gap: 16px; align-items: center; }
.photo { width: 88px; height: 88px; border-radius: 12px; object-fit: cover; flex-shrink: 0; }
.photo-empty {
  background: var(--hm-primary-light); color: var(--hm-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 32px; font-weight: 600;
}
.photo-name { font-size: 16px; font-weight: 600; color: var(--hm-text); display: flex; align-items: center; gap: 8px; }
.photo-job { font-size: 12px; color: var(--hm-text-secondary); margin-top: 4px; }
</style>
