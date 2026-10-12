<template>
  <div>
    <div class="hm-page-header"><span class="hm-page-title">我的工资</span></div>
    <div class="hm-card">
      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="salaryMonth" label="月份" width="110" />
        <el-table-column label="基本工资" width="120">
          <template #default="{ row }">¥ {{ row.baseSalary?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="奖金/绩效" width="120">
          <template #default="{ row }">¥ {{ row.bonus?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="扣款" width="110">
          <template #default="{ row }">¥ {{ row.deduction?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="实发工资" width="140">
          <template #default="{ row }"><b style="color:#0A59F7">¥ {{ row.actualPay?.toFixed(2) }}</b></template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="hm-tag" :class="row.status === 1 ? 'hm-tag-success' : 'hm-tag-warning'">
              {{ row.status === 1 ? '已发放' : '待发放' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="payDate" label="发放日期" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { mySalaries } from '../api'

const rows = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try { rows.value = await mySalaries() } finally { loading.value = false }
})
</script>
