<template>
  <div>
    <!-- 搜索栏卡（设计：1196×64，内边距 12/16，元素 40 高、gap 8） -->
    <div class="hm-card search-card">
      <div class="search-bar">
        <el-button class="sq-btn" @click="() => $router.back()"><AppIcon name="arrow-left" :size="18" /></el-button>
        <el-input v-model="query.name" placeholder="搜索姓名 / 工号" clearable class="search-input" @keyup.enter="load" @clear="load">
          <template #prefix><AppIcon name="search" :size="16" /></template>
        </el-input>
        <el-button class="sq-btn" @click="load"><AppIcon name="scan" :size="18" /></el-button>
        <el-select v-model="query.gender" placeholder="性别" clearable class="mini-select" @change="load">
          <el-option label="男" :value="1" /><el-option label="女" :value="2" />
        </el-select>
        <el-select v-model="query.status" placeholder="在职状态" class="mini-select" @change="load">
          <el-option label="在职" :value="1" /><el-option label="离职" :value="0" /><el-option label="全部" :value="null" />
        </el-select>
        <div style="flex:1" />
        <el-button type="primary" class="add-btn" @click="openAdd">＋ 新增员工</el-button>
      </div>
    </div>

    <!-- 状态筛选行（设计：1196×56） -->
    <div class="hm-card chips-bar">
      <span class="chips-label">在职状态</span>
      <span class="hm-tag chip cursor" :class="query.status === 1 ? 'hm-tag-info' : 'hm-tag-default'" @click="setStatus(1)">在职</span>
      <span class="hm-tag chip cursor" :class="query.status === 0 ? 'hm-tag-warning' : 'hm-tag-default'" @click="setStatus(0)">离职</span>
      <span class="hm-tag chip cursor" :class="query.status === null ? 'hm-tag-info' : 'hm-tag-default'" @click="setStatus(null)">全部</span>
      <div style="flex:1" />
      <span class="batch-link" :class="{ disabled: !selection.length }" @click="selection.length && onBatchDelete()">批量离职</span>
    </div>

    <!-- 表格卡（设计：1196×520，内边距左右 24，表头 1148×40） -->
    <div class="hm-card table-card">
      <div class="card-title">员工列表</div>
      <div class="card-count">共 {{ total }} 条</div>
      <el-table :data="rows" v-loading="loading" class="hm-table" :row-class-name="rowClass" @row-click="toggleRow">
        <el-table-column prop="id" label="工号" width="70" />
        <el-table-column prop="name" label="姓名" width="75" />
        <el-table-column label="性别" width="94">
          <template #default="{ row }">
            <span class="hm-tag hm-tag-info col-tag">{{ row.gender === 1 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="job" label="职位" width="200" show-overflow-tooltip />
        <el-table-column prop="deptName" label="部门" width="136" />
        <el-table-column label="状态" width="144">
          <template #default="{ row }">
            <span class="hm-tag col-tag" :class="row.status === 1 ? 'hm-tag-success' : 'hm-tag-default'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入职日期" width="180" />
        <el-table-column label="操作" min-width="230">
          <template #default="{ row }">
            <span class="op-link" @click.stop="openDetail(row)">详情</span>
            <span v-if="row.status === 1" class="op-link" @click.stop="openEdit(row)">编辑</span>
            <span v-if="row.status === 1" class="op-link op-danger" @click.stop="onDeleteOne(row)">离职</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
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

// ===== 无复选框列时的行点击多选（支撑"批量离职"） =====
function toggleRow(row) {
  const i = selection.value.findIndex(r => r.id === row.id)
  if (i >= 0) selection.value.splice(i, 1)
  else selection.value.push(row)
}
function rowClass({ row }) {
  return selection.value.some(r => r.id === row.id) ? 'row-selected' : ''
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
/* ===== 搜索卡 1196×64 ===== */
.search-card { height: 64px; padding: 12px 16px; margin-bottom: 16px; }
.search-bar { display: flex; gap: 8px; align-items: center; height: 40px; }
.sq-btn { width: 40px; height: 40px; padding: 0; border-radius: 8px; }
.search-input { width: 204px; }
.mini-select { width: 95px; }
.add-btn { width: 120px; height: 40px; font-size: 14px; }
.search-bar :deep(.el-input__wrapper),
.search-bar :deep(.el-select__wrapper) { height: 40px; border-radius: 8px; }
.search-input { margin-right: 0; }
.search-bar > .el-select:nth-of-type(1) { margin-left: 8px; }
.search-bar > .el-select + .el-select { margin-left: 8px; }

/* ===== 状态行 1196×56 ===== */
.chips-bar {
  height: 56px; padding: 0 24px; margin-bottom: 16px;
  display: flex; align-items: center; gap: 12px;
}
.chips-label { font-size: 12px; color: var(--hm-text-secondary); margin-right: 4px; }
.chip { height: 22px; line-height: 22px; padding: 0 15px; display: inline-flex; align-items: center; }
.batch-link { font-size: 12px; color: var(--hm-danger); cursor: pointer; }
.batch-link.disabled { color: var(--hm-text-hint); }

/* ===== 表格卡 1196×520 ===== */
.table-card { height: 520px; position: relative; }
.card-title { padding: 22px 24px 0; font-size: 14px; font-weight: 600; color: var(--hm-text); }
.card-count { position: absolute; right: 24px; top: 24px; font-size: 11px; color: var(--hm-text-hint); }
.hm-table { margin: 12px 24px 0; }
.col-tag { height: 22px; line-height: 22px; padding: 0 10px; display: inline-flex; align-items: center; }
.cursor { cursor: pointer; }
.op-link { font-size: 12px; color: var(--hm-primary); cursor: pointer; margin-right: 24px; }
.op-link.op-danger { color: var(--hm-danger); margin-right: 0; }
.table-footer { display: flex; justify-content: flex-end; margin-top: 20px; }

/* 表头：40 高 / #FAFBFC / 12px 灰字 */
.hm-table :deep(th.el-table__cell) {
  height: 40px; background: #FAFBFC; color: var(--hm-text-hint);
  font-size: 12px; font-weight: 400; border-bottom: none; padding: 0;
}
.hm-table :deep(.el-table__header th:first-child) { border-radius: 8px 0 0 8px; padding-left: 29px; }
.hm-table :deep(.el-table__header th:last-child) { border-radius: 0 8px 8px 0; }
.hm-table :deep(.el-table__header-wrapper) { border-radius: 8px; overflow: hidden; }
/* 数据行：56 高 / 分隔线 #F1F3F5 */
.hm-table :deep(td.el-table__cell) {
  height: 56px; padding: 0; border-bottom: 1px solid #F1F3F5;
  font-size: 12px; color: var(--hm-text);
}
.hm-table :deep(td.el-table__cell:first-child) { padding-left: 29px; }
.hm-table :deep(.el-table__row:last-child td.el-table__cell) { border-bottom: none; }
.hm-table :deep(.el-table__body-wrapper) { overflow: visible; }
.hm-table :deep(tr.row-selected td.el-table__cell) { background: var(--hm-primary-light) !important; }
.hm-table :deep(.el-table__row) { cursor: pointer; }
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