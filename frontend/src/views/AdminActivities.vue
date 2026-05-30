<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { uploadFile } from '../api/file'
import {
  createActivity,
  deleteActivity,
  getAdminActivityList,
  importActivityWord,
  type ActivityItem,
  updateActivity,
} from '../api/activity'
import { getAssetUrl } from '../utils/assets'
import RichTextEditor from '../components/RichTextEditor.vue'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const importingWord = ref(false)
const rows = ref<ActivityItem[]>([])
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const form = reactive<ActivityItem>({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  publishTime: '',
})

function resetForm() {
  editingId.value = null
  form.title = ''
  form.summary = ''
  form.content = ''
  form.coverImage = ''
  form.publishTime = ''
}

function fillForm(row: ActivityItem) {
  editingId.value = row.id || null
  form.title = row.title || ''
  form.summary = row.summary || ''
  form.content = row.content || ''
  form.coverImage = row.coverImage || ''
  form.publishTime = row.publishTime || ''
}

async function load() {
  loading.value = true
  try {
    const res = await getAdminActivityList()
    if (res.code === 0) rows.value = res.data
  } finally {
    loading.value = false
  }
}

async function save() {
  if (!form.title?.trim() || !form.summary?.trim() || !form.content?.trim()) {
    ElMessage.warning('请先填写标题、摘要和正文')
    return
  }
  saving.value = true
  try {
    const payload: ActivityItem = {
      title: form.title.trim(),
      summary: form.summary.trim(),
      content: form.content.trim(),
      coverImage: form.coverImage?.trim(),
      publishTime: form.publishTime?.trim(),
    }
    const res = editingId.value ? await updateActivity(editingId.value, payload) : await createActivity(payload)
    if (res.code === 0) {
      ElMessage.success(editingId.value ? '活动已更新' : '活动已创建')
      dialogVisible.value = false
      await load()
    }
  } finally {
    saving.value = false
  }
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: ActivityItem) {
  fillForm(row)
  dialogVisible.value = true
}

async function remove(id: number) {
  try {
    await ElMessageBox.confirm('确定删除这条爱心活动吗？', '删除确认', { type: 'warning' })
    const res = await deleteActivity(id)
    if (res.code === 0) {
      ElMessage.success('活动已删除')
      await load()
    }
  } catch {
    // cancel
  }
}

async function uploadCover(options: UploadRequestOptions) {
  uploading.value = true
  try {
    const res = await uploadFile(options.file, 'activities')
    if (res.code === 0) {
      form.coverImage = res.data
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } finally {
    uploading.value = false
  }
}

async function importWord(options: UploadRequestOptions) {
  importingWord.value = true
  try {
    const res = await importActivityWord(options.file as File)
    if (res.code === 0) {
      form.title = res.data.title
      form.summary = res.data.summary
      form.content = res.data.content
      ElMessage.success('Word 内容已导入，请校对后保存')
      if (!dialogVisible.value) dialogVisible.value = true
    } else {
      ElMessage.error(res.message || 'Word 导入失败')
    }
  } finally {
    importingWord.value = false
  }
}

onMounted(load)
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="header-bar">
        <span>爱心活动管理</span>
        <div class="header-actions">
          <el-upload action="" :show-file-list="false" accept=".doc,.docx" :http-request="importWord">
            <el-button :loading="importingWord">导入 Word 初稿</el-button>
          </el-upload>
          <el-button type="primary" @click="openCreate">新增活动</el-button>
        </div>
      </div>
    </template>

    <div class="activity-grid">
      <div class="activity-card" v-for="row in rows" :key="row.id" @click="openEdit(row)">
        <div class="cover-wrap">
          <img class="cover" :src="getAssetUrl(row.coverImage)" :alt="row.title" />
        </div>
        <div class="content">
          <p class="time">{{ (row.publishTime || '').replace('T', ' ') }}</p>
          <h3>{{ row.title }}</h3>
          <p class="summary">{{ row.summary }}</p>
          <div class="actions">
            <el-button size="small" @click.stop="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="remove(row.id!)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="editingId ? '编辑爱心活动' : '新增爱心活动'" width="960px" top="5vh" class="edit-dialog">
    <div class="form-grid">
      <el-input v-model="form.title" placeholder="活动标题" />
      <el-input v-model="form.publishTime" placeholder="发布时间，例如 2026-06-01T10:00:00" />
      <div class="cover-panel">
        <el-upload action="" :show-file-list="false" :http-request="uploadCover">
          <el-button :loading="uploading">上传封面</el-button>
        </el-upload>
        <div class="cover-preview">
          <img v-if="form.coverImage" :src="getAssetUrl(form.coverImage)" alt="封面预览" class="cover-preview-image" />
          <div v-else class="cover-preview-empty">上传后在这里预览封面</div>
        </div>
      </div>
      <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="活动摘要" />
      <div class="editor-field">
        <RichTextEditor :model-value="form.content || ''" @update:model-value="form.content = $event" upload-folder="activities" />
      </div>
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">{{ editingId ? '保存修改' : '创建活动' }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.activity-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eadfd5;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.08);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.activity-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 28px rgba(17, 24, 39, 0.12);
}

.cover-wrap {
  width: 100%;
  aspect-ratio: 16 / 9;
  min-height: 180px;
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.content {
  padding: 14px;
}

.time {
  margin: 0 0 8px;
  color: #9f8b7d;
  font-size: 12px;
}

.content h3 {
  margin: 0 0 8px;
  font-size: 16px;
  line-height: 1.5;
  color: #2e241e;
}

.summary {
  margin: 0 0 12px;
  color: #66584f;
  font-size: 13px;
  line-height: 1.7;
}

.actions {
  display: flex;
  gap: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.cover-panel {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cover-preview {
  width: 100%;
  max-width: 640px;
  aspect-ratio: 16 / 9;
  background: #f5f7fa;
  overflow: hidden;
  border-radius: 12px;
}

.cover-preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-preview-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #98a2b3;
}

.form-grid :deep(.el-textarea) {
  grid-column: 1 / -1;
}

.editor-field {
  grid-column: 1 / -1;
}

.edit-dialog :deep(.el-dialog__body) {
  max-height: calc(88vh - 130px);
  overflow-y: auto;
}
</style>
