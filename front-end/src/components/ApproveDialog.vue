<template>
  <el-dialog v-model="visible" title="审批" width="480px">
    <template v-if="record">
      <el-descriptions :column="2" border size="small" style="margin-bottom:16px">
        <el-descriptions-item label="申请人">{{ record.empName }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ fmt(record.createTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="type === 'leave'" label="请假类型">{{ typeName(record.leaveType) }}</el-descriptions-item>
        <el-descriptions-item v-if="type === 'leave'" label="请假天数">{{ record.days }} 天</el-descriptions-item>
        <el-descriptions-item v-if="type === 'leave'" label="起止日期">{{ record.beginDate }} ~ {{ record.endDate }}</el-descriptions-item>
        <el-descriptions-item v-if="type === 'transfer'" label="现职位">{{ record.currentJob || '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="type === 'transfer'" label="目标职位">{{ record.targetJob }}</el-descriptions-item>
        <el-descriptions-item v-if="type === 'transfer'" label="目标部门">{{ record.targetDeptName || '不换部门' }}</el-descriptions-item>
        <el-descriptions-item :label="type === 'leave' ? '请假原因' : '申请原因'" :span="2">{{ record.reason || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="70px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="approved">
            <el-radio :value="true">通过</el-radio>
            <el-radio :value="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="remark" type="textarea" :rows="3" placeholder="请输入审批意见（可选）" maxlength="200" />
        </el-form-item>
      </el-form>
    </template>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="onSubmit">提交审批</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 请假 / 调岗共用审批对话框
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { approveLeave, approveTransfer } from '../api'

const visible = ref(false)
const record = ref(null)
const type = ref('leave')
const approved = ref(true)
const remark = ref('')
const saving = ref(false)

let onDone = null

const TYPE_NAMES = { 1: '事假', 2: '病假', 3: '年假', 4: '婚假', 5: '其他' }
const typeName = t => TYPE_NAMES[t] || '其他'
const fmt = t => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

function open(row, t, done) {
  record.value = row
  type.value = t
  approved.value = true
  remark.value = ''
  onDone = done
  visible.value = true
}

async function onSubmit() {
  saving.value = true
  try {
    const payload = { approved: approved.value, remark: remark.value || null }
    if (type.value === 'leave') await approveLeave(record.value.id, payload)
    else await approveTransfer(record.value.id, payload)
    ElMessage.success(approved.value ? '已通过' : '已驳回')
    visible.value = false
    onDone?.()
  } finally { saving.value = false }
}

defineExpose({ open })
</script>
