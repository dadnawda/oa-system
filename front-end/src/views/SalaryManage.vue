<template>
  <div>
    <div class="hm-page-header">
      <span class="hm-page-title">工资管理</span>
      <el-button type="primary" @click="openAdd"><AppIcon name="plus" :size="13" style="margin-right:4px" />录入工资</el-button>
    </div>

    <div class="hm-card" style="margin-bottom:14px">
      <div class="search-bar">
        <el-input v-model="query.empId" placeholder="员工工号" clearable style="width:140px" @keyup.enter="load" />
        <el-date-picker v-model="month" type="month" value-format="YYYY-MM" placeholder="工资月份" style="width:150px" />
        <el-button type="primary" @click="load"><AppIcon name="search" :size="13" style="margin-right:4px" />查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </div>

    <div class="hm-card">
      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="id" label="单号" width="70" />
        <el-table-column prop="empId" label="工号" width="80" />
        <el-table-column prop="empName" label="姓名" width="100" />
        <el-table-column prop="salaryMonth" label="月份" width="100" />
        <el-table-column label="基本工资" width="110">
          <template #default="{ row }">¥ {{ row.baseSalary?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="奖金" width="110">
          <template #default="{ row }">¥ {{ row.bonus?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="扣款" width="110">
          <template #default="{ row }">¥ {{ row.deduction?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="实发工资" width="130">
          <template #default="{ row }"><b style="color:#0A59F7">¥ {{ row.actualPay?.toFixed(2) }}</b></template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="hm-tag" :class="row.status === 1 ? 'hm-tag-success' : 'hm-tag-warning'">
              {{ row.status === 1 ? '已发放' : '待发放' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="right">
          <template #default="{ row }">
            <el-tooltip v-if="row.empId === user.userInfo?.empId" content="不能操作本人工资单" placement="top">
              <span style="font-size:12px;color:var(--hm-text-hint)">本人工资单</span>
            </el-tooltip>
            <template v-else>
              <el-button link type="primary" size="small" :disabled="row.status === 1" @click="openEdit(row)">调整</el-button>
              <el-button link type="success" size="small" :disabled="row.status === 1" @click="onPay(row)">发放</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:12px">
        <el-pagination background layout="total, prev, pager, next"
          :total="total" v-model:current-page="query.page" :page-size="query.pageSize" @current-change="load" />
      </div>
    </div>

    <el-dialog v-model="editor.visible" :title="editor.isEdit ? '调整工资' : '录入工资'" width="440px">
      <el-form label-width="80px">
        <el-form-item label="员工工号" v-if="!editor.isEdit" required>
          <el-input v-model.number="editor.form.empId" placeholder="请输入员工工号" />
        </el-form-item>
        <el-form-item label="工资月份" v-if="!editor.isEdit" required>
          <el-date-picker v-model="editor.form.salaryMonth" type="month" value-format="YYYY-MM" style="width:100%" />
        </el-form-item>
        <el-form-item label="基本工资">
          <el-input-number v-model="editor.form.baseSalary" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="奖金/绩效">
          <el-input-number v-model="editor.form.bonus" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="扣款">
          <el-input-number v-model="editor.form.deduction" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="实发工资">
          <b style="color:#0A59F7">¥ {{ actualPayPreview }}</b>
          <span style="font-size:11px;color:var(--hm-text-hint);margin-left:8px">（后端自动计算）</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editor.visible = false">取消</el-button>
        <el-button type="primary" :loading="editor.saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import { useUserStore } from '../stores/user'
import { pageSalaries, addSalary, updateSalary, paySalary } from '../api'

const user = useUserStore()

const query = reactive({ empId: '', salaryMonth: '', page: 1, pageSize: 10 })
const month = ref(null)
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const editor = reactive({ visible: false, isEdit: false, saving: false, form: {} })

const actualPayPreview = computed(() => {
  const f = editor.form
  return ((f.baseSalary || 0) + (f.bonus || 0) - (f.deduction || 0)).toFixed(2)
})

async function load() {
  loading.value = true
  try {
    const params = { ...query, empId: query.empId || undefined, salaryMonth: month.value || undefined }
    const page = await pageSalaries(params)
    rows.value = page.rows
    total.value = page.total
  } finally { loading.value = false }
}

function reset() { query.empId = ''; month.value = null; query.page = 1; load() }

function openAdd() {
  editor.isEdit = false
  editor.form = { empId: null, salaryMonth: '', baseSalary: 0, bonus: 0, deduction: 0 }
  editor.visible = true
}

function openEdit(row) {
  editor.isEdit = true
  editor.form = { id: row.id, baseSalary: row.baseSalary, bonus: row.bonus, deduction: row.deduction }
  editor.visible = true
}

async function onSave() {
  editor.saving = true
  try {
    if (editor.isEdit) {
      const { id, baseSalary, bonus, deduction } = editor.form
      await updateSalary(id, { baseSalary, bonus, deduction })
    } else {
      if (!editor.form.empId) { ElMessage.warning('请输入员工工号'); return }
      if (!editor.form.salaryMonth) { ElMessage.warning('请选择工资月份'); return }
      await addSalary(editor.form)
    }
    ElMessage.success('保存成功')
    editor.visible = false
    await load()
  } finally { editor.saving = false }
}

async function onPay(row) {
  await ElMessageBox.confirm(`确认为员工「${row.empName}」发放 ${row.salaryMonth} 月工资（¥ ${row.actualPay?.toFixed(2)}）吗？`, '发放确认', { type: 'warning' })
  await paySalary(row.id)
  ElMessage.success('发放成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.search-bar { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
</style>
