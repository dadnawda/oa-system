<template>
  <div>
    <div class="hm-page-header">
      <span class="hm-page-title">公告中心</span>
      <el-button v-if="user.viewIsAdmin" type="primary" @click="openAdd">
        <AppIcon name="plus" :size="13" style="margin-right:4px" />新建公告
      </el-button>
    </div>

    <div class="hm-card">
      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="hm-tag" :class="row.status === 1 ? 'hm-tag-success' : 'hm-tag-default'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="170">
          <template #default="{ row }">{{ fmtTime(row.publishTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">查看</el-button>
            <template v-if="user.viewIsAdmin">
              <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="onDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:12px">
        <el-pagination background layout="total, prev, pager, next"
          :total="total" v-model:current-page="query.page" :page-size="query.pageSize" @current-change="load" />
      </div>
    </div>

    <!-- 新建/编辑 -->
    <el-dialog v-model="editor.visible" :title="editor.isEdit ? '编辑公告' : '新建公告'" width="560px">
      <el-form label-width="70px">
        <el-form-item label="标题" required>
          <el-input v-model="editor.form.title" maxlength="100" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editor.form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="editor.form.status">
            <el-radio :value="0">存为草稿</el-radio>
            <el-radio :value="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editor.visible = false">取消</el-button>
        <el-button type="primary" :loading="editor.saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情 -->
    <el-dialog v-model="detail.visible" :title="detail.data?.title" width="560px">
      <div class="detail-meta">发布人：{{ detail.data?.publisherName }} · {{ fmtTime(detail.data?.publishTime) }}</div>
      <div class="detail-content">{{ detail.data?.content }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'
import { useUserStore } from '../stores/user'
import { pageNotices, addNotice, updateNotice, deleteNotice } from '../api'

const user = useUserStore()
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10 })
const editor = reactive({ visible: false, isEdit: false, saving: false, form: { id: null, title: '', content: '', status: 1 } })
const detail = reactive({ visible: false, data: null })

const fmtTime = t => (t ? String(t).replace('T', ' ').slice(0, 19) : '')

async function load() {
  loading.value = true
  try {
    const page = await pageNotices(query)
    rows.value = page.rows
    total.value = page.total
  } finally { loading.value = false }
}

function openAdd() {
  editor.isEdit = false
  editor.form = { id: null, title: '', content: '', status: 1 }
  editor.visible = true
}

function openEdit(row) {
  editor.isEdit = true
  editor.form = { id: row.id, title: row.title, content: row.content, status: row.status }
  editor.visible = true
}

async function onSave() {
  if (!editor.form.title?.trim()) { ElMessage.warning('标题不能为空'); return }
  editor.saving = true
  try {
    if (editor.isEdit) await updateNotice(editor.form.id, editor.form)
    else await addNotice(editor.form)
    ElMessage.success('保存成功')
    editor.visible = false
    await load()
  } finally { editor.saving = false }
}

function openDetail(row) { detail.data = row; detail.visible = true }

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除公告「${row.title}」吗？`, '删除确认', { type: 'warning' })
  await deleteNotice(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.detail-meta { font-size: 12px; color: var(--hm-text-hint); margin-bottom: 12px; }
.detail-content { font-size: 13px; line-height: 1.8; white-space: pre-wrap; color: var(--hm-text); }
</style>
