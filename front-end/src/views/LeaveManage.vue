<template>
  <div>
    <div class="hm-page-header">
      <span class="hm-page-title">请假审批</span>
      <el-button v-if="!user.viewIsAdmin" type="primary" @click="openSubmit">
        <AppIcon name="plus" :size="13" style="margin-right:4px" />提交请假
      </el-button>
    </div>

    <!-- 管理员：审批列表 -->
    <template v-if="user.viewIsAdmin">
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
          <el-table-column label="类型" width="90">
            <template #default="{ row }">{{ typeName(row.leaveType) }}</template>
          </el-table-column>
          <el-table-column label="起止日期" width="200">
            <template #default="{ row }">{{ row.beginDate }} ~ {{ row.endDate }}</template>
          </el-table-column>
          <el-table-column prop="days" label="天数" width="80" />
          <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <span class="hm-tag" :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="approverName" label="审批人" width="100" />
          <el-table-column label="操作" width="100" align="right">
            <template #default="{ row }">
              <el-tooltip v-if="row.status === 0 && row.empId === user.userInfo?.empId" content="不能审批自己提交的请假单" placement="top">
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
          <el-table-column label="类型" width="90">
            <template #default="{ row }">{{ typeName(row.leaveType) }}</template>
          </el-table-column>
          <el-table-column label="起止日期" width="200">
            <template #default="{ row }">{{ row.beginDate }} ~ {{ row.endDate }}</template>
          </el-table-column>
          <el-table-column prop="days" label="天数" width="80" />
          <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
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

    <!-- 提交请假对话框 -->
    <el-dialog v-model="submitDlg.visible" title="提交请假申请" width="440px">
      <el-form label-width="80px">
        <el-form-item label="请假类型" required>
          <el-select v-model="submitDlg.form.leaveType" style="width:100%">
            <el-option v-for="(n, i) in ['事假', '病假', '年假', '婚假', '其他']" :key="i" :label="n" :value="i + 1" />
          </el-select>
        </el-form-item>
        <el-form-item label="起止日期" required>
          <el-date-picker v-model="submitDlg.range" type="daterange" value-format="YYYY-MM-DD"
                          start-placeholder="开始日期" end-placeholder="结束日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="请假天数" required>
          <el-input-number v-model="submitDlg.form.days" :min="0.5" :step="0.5" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="请假原因">
          <el-input v-model="submitDlg.form.reason" type="textarea" :rows="3" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDlg.visible = false">取消</el-button>
        <el-button type="primary" :loading="submitDlg.saving" @click="onSubmit">提交</el-button>
      </template>
    </el-dialog>

    <ApproveDialog ref="approveRef" type="leave" />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import ApproveDialog from '../components/ApproveDialog.vue'
import { useUserStore } from '../stores/user'
import { myLeaves, submitLeave, pageLeaves } from '../api'

const user = useUserStore()
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const activeTab = ref('0')
const query = reactive({ status: 0, page: 1, pageSize: 10 })
const submitDlg = reactive({ visible: false, saving: false, range: null, form: { leaveType: 1, days: 1, reason: '' } })
const approveRef = ref(null)

const TYPE_NAMES = ['事假', '病假', '年假', '婚假', '其他']
const typeName = t => TYPE_NAMES[t - 1] || '其他'
const statusText = s => ({ 0: '待审批', 1: '已通过', 2: '已驳回' })[s]
const statusClass = s => ({ 0: 'hm-tag-warning', 1: 'hm-tag-success', 2: 'hm-tag-danger' })[s]

const effectiveStatus = computed(() => (activeTab.value === 'all' ? null : Number(activeTab.value)))

async function load() {
  loading.value = true
  try {
    if (user.viewIsAdmin) {
      query.status = effectiveStatus.value
      const page = await pageLeaves(query)
      rows.value = page.rows
      total.value = page.total
    } else {
      rows.value = await myLeaves()
    }
  } finally { loading.value = false }
}

function openSubmit() {
  submitDlg.range = null
  submitDlg.form = { leaveType: 1, days: 1, reason: '' }
  submitDlg.visible = true
}

async function onSubmit() {
  const f = submitDlg.form
  if (!submitDlg.range) { ElMessage.warning('请选择起止日期'); return }
  submitDlg.saving = true
  try {
    await submitLeave({ ...f, beginDate: submitDlg.range[0], endDate: submitDlg.range[1] })
    ElMessage.success('提交成功，等待审批')
    submitDlg.visible = false
    await load()
  } finally { submitDlg.saving = false }
}

function openApprove(row) {
  approveRef.value.open(row, 'leave', load)
}

onMounted(load)
</script>
