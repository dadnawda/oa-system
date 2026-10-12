<template>
  <div>
    <div class="hm-page-header">
      <span class="hm-page-title">职位调整</span>
      <el-button v-if="!user.isAdmin" type="primary" @click="openSubmit">
        <AppIcon name="plus" :size="13" style="margin-right:4px" />申请调岗
      </el-button>
    </div>

    <!-- 管理员：审批列表 -->
    <template v-if="user.isAdmin">
      <div class="hm-card" style="margin-bottom:14px">
        <el-tabs v-model="activeTab" @tab-change="load">
          <el-tab-pane label="待审批" name="0" />
          <el-tab-pane label="已通过" name="1" />
          <el-tab-pane label="已驳回" name="2" />
          <el-tab-pane label="全部" name="all" />
        </el-tabs>
        <el-table :data="rows" v-loading="loading" stripe>
          <el-table-column prop="id" label="单号" width="70" />
          <el-table-column prop="empName" label="申请人" width="100" />
          <el-table-column prop="currentJob" label="现职位" width="130" />
          <el-table-column prop="targetJob" label="目标职位" width="140" />
          <el-table-column prop="targetDeptName" label="目标部门" width="110">
            <template #default="{ row }">{{ row.targetDeptName || '不换部门' }}</template>
          </el-table-column>
          <el-table-column prop="reason" label="申请原因" min-width="150" show-overflow-tooltip />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <span class="hm-tag" :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="approverName" label="审批人" width="100" />
          <el-table-column label="操作" width="100" align="right">
            <template #default="{ row }">
              <el-tooltip v-if="row.status === 0 && row.empId === user.userInfo?.empId" content="不能审批自己提交的调岗申请" placement="top">
                <span style="font-size:12px;color:var(--hm-text-hint)">本人申请</span>
              </el-tooltip>
              <el-button v-else-if="row.status === 0" link type="primary" size="small" @click="openApprove(row)">审批</el-button>
              <span v-else style="font-size:12px;color:var(--hm-text-hint)">已处理</span>
            </template>
          </el-table-column>
        </el-table>
        <div style="display:flex;justify-content:flex-end;margin-top:12px">
          <el-pagination background layout="total, prev, pager, next"
            :total="total" v-model:current-page="query.page" :page-size="query.pageSize" @current-change="load" />
        </div>
      </div>
    </template>

    <!-- 员工：我的申请 -->
    <template v-else>
      <div class="hm-card">
        <el-table :data="rows" v-loading="loading" stripe>
          <el-table-column prop="id" label="单号" width="70" />
          <el-table-column prop="currentJob" label="现职位" width="140" />
          <el-table-column prop="targetJob" label="目标职位" width="150" />
          <el-table-column prop="targetDeptName" label="目标部门" width="110">
            <template #default="{ row }">{{ row.targetDeptName || '不换部门' }}</template>
          </el-table-column>
          <el-table-column prop="reason" label="申请原因" min-width="150" show-overflow-tooltip />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <span class="hm-tag" :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="approverName" label="审批人" width="100" />
          <el-table-column prop="approveRemark" label="审批意见" min-width="140" show-overflow-tooltip />
        </el-table>
      </div>
    </template>

    <!-- 提交调岗对话框 -->
    <el-dialog v-model="submitDlg.visible" title="申请职位调整" width="440px">
      <el-form label-width="80px">
        <el-form-item label="目标职位" required>
          <el-input v-model="submitDlg.form.targetJob" maxlength="50" placeholder="请输入目标职位" />
        </el-form-item>
        <el-form-item label="目标部门">
          <el-select v-model="submitDlg.form.targetDeptId" placeholder="不选择表示只调职位不换部门" clearable style="width:100%">
            <el-option v-for="d in depts" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请原因">
          <el-input v-model="submitDlg.form.reason" type="textarea" :rows="3" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDlg.visible = false">取消</el-button>
        <el-button type="primary" :loading="submitDlg.saving" @click="onSubmit">提交</el-button>
      </template>
    </el-dialog>

    <ApproveDialog ref="approveRef" type="transfer" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import ApproveDialog from '../components/ApproveDialog.vue'
import { useUserStore } from '../stores/user'
import { myTransfers, submitTransfer, pageTransfers, listDepts } from '../api'

const user = useUserStore()
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const activeTab = ref('0')
const query = reactive({ status: 0, page: 1, pageSize: 10 })
const submitDlg = reactive({ visible: false, saving: false, form: { targetJob: '', targetDeptId: null, reason: '' } })
const depts = ref([])
const approveRef = ref(null)

const statusText = s => ({ 0: '待审批', 1: '已通过', 2: '已驳回' })[s]
const statusClass = s => ({ 0: 'hm-tag-warning', 1: 'hm-tag-success', 2: 'hm-tag-danger' })[s]

async function load() {
  loading.value = true
  try {
    if (user.isAdmin) {
      query.status = activeTab.value === 'all' ? null : Number(activeTab.value)
      const page = await pageTransfers(query)
      rows.value = page.rows
      total.value = page.total
    } else {
      rows.value = await myTransfers()
    }
  } finally { loading.value = false }
}

function openSubmit() {
  submitDlg.form = { targetJob: '', targetDeptId: null, reason: '' }
  submitDlg.visible = true
}

async function onSubmit() {
  if (!submitDlg.form.targetJob?.trim()) { ElMessage.warning('目标职位不能为空'); return }
  submitDlg.saving = true
  try {
    await submitTransfer({ ...submitDlg.form, targetDeptId: submitDlg.form.targetDeptId || null })
    ElMessage.success('提交成功，等待审批')
    submitDlg.visible = false
    await load()
  } finally { submitDlg.saving = false }
}

function openApprove(row) {
  approveRef.value.open(row, 'transfer', load)
}

onMounted(async () => {
  if (user.isAdmin) depts.value = await listDepts()
  await load()
})
</script>
