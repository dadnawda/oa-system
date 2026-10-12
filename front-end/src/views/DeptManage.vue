<template>
  <div>
    <div class="hm-page-header">
      <span class="hm-page-title">部门管理</span>
      <el-button type="primary" @click="openAdd"><AppIcon name="plus" :size="13" style="margin-right:4px" />新增部门</el-button>
    </div>

    <div class="hm-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="部门名称" min-width="140" />
        <el-table-column prop="description" label="部门描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑部门' : '新增部门'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="部门名称" required>
          <el-input v-model="dialog.form.name" placeholder="请输入部门名称" maxlength="32" />
        </el-form-item>
        <el-form-item label="部门描述">
          <el-input v-model="dialog.form.description" type="textarea" :rows="3" placeholder="请输入部门描述" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import { listDepts, addDept, updateDept, deleteDept } from '../api'

const list = ref([])
const loading = ref(false)
const dialog = reactive({ visible: false, isEdit: false, saving: false, form: { id: null, name: '', description: '' } })

const fmtTime = t => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

async function load() {
  loading.value = true
  try { list.value = await listDepts() } finally { loading.value = false }
}

function openAdd() {
  dialog.isEdit = false
  dialog.form = { id: null, name: '', description: '' }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog.form = { id: row.id, name: row.name, description: row.description }
  dialog.visible = true
}

async function onSave() {
  if (!dialog.form.name?.trim()) { ElMessage.warning('部门名称不能为空'); return }
  dialog.saving = true
  try {
    if (dialog.isEdit) await updateDept(dialog.form.id, dialog.form)
    else await addDept(dialog.form)
    ElMessage.success('保存成功')
    dialog.visible = false
    await load()
  } finally { dialog.saving = false }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除部门「${row.name}」吗？删除后将留档隐藏，不再显示。`, '删除确认', { type: 'warning' })
  await deleteDept(row.id)
  ElMessage.success('已删除（留档隐藏）')
  await load()
}

onMounted(load)
</script>
