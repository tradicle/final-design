<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { uploadFile } from '../api/file'
import { createNews, deleteNews, getNewsList, type NewsItem, updateNews } from '../api/news'
import { getAnimalList, type Animal } from '../api/animal'
import type { UploadRequestOptions } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const rows = ref<NewsItem[]>([])
const animals = ref<Animal[]>([])
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({
  title: '',
  summary: '',
  coverImage: '',
  publishTime: '',
  animalNo: '',
})

function buildLinkContent(animalNo: string) {
  return `PET:${animalNo}`
}

function parseAnimalNo(content?: string) {
  if (!content) return ''
  if (!content.startsWith('PET:')) return ''
  return content.slice(4)
}

function getAnimalSexLabel(animal: Animal) {
  return animal.sex === 'FEMALE' ? '女' : animal.sex === 'MALE' ? '男' : '未知'
}

function getAnimalSterilizedLabel(animal: Animal) {
  return animal.isSterilized ? '已绝育' : '未绝育'
}

function buildNewsTitle(animal: Animal) {
  return animal.name || ''
}

function buildNewsSummary(animal: Animal) {
  return `${animal.age ?? '-'}岁，${getAnimalSexLabel(animal)}，${getAnimalSterilizedLabel(animal)}`
}

async function load() {
  loading.value = true
  try {
    const res = await getNewsList()
    if (res.code === 0) rows.value = res.data
  } finally {
    loading.value = false
  }
}

async function loadAnimals() {
  const res = await getAnimalList()
  if (res.code === 0) animals.value = res.data
}

function resetForm() {
  editingId.value = null
  form.title = ''
  form.summary = ''
  form.coverImage = ''
  form.publishTime = ''
  form.animalNo = ''
}

function fillForm(row: NewsItem) {
  editingId.value = row.id || null
  form.title = row.title || ''
  form.summary = row.summary || ''
  form.coverImage = row.coverImage || ''
  form.publishTime = row.publishTime || ''
  form.animalNo = parseAnimalNo(row.content)
}

function openCreate() {
  resetForm()
  drawerVisible.value = true
}

function openEdit(row: NewsItem) {
  fillForm(row)
  drawerVisible.value = true
}

async function save() {
  if (!form.title || !form.animalNo) {
    ElMessage.warning('请先填写标题并选择关联宠物')
    return
  }
  saving.value = true
  try {
    const payload: NewsItem = {
      title: form.title,
      summary: form.summary,
      content: buildLinkContent(form.animalNo),
      coverImage: form.coverImage,
      publishTime: form.publishTime
    }
    const res = editingId.value ? await updateNews(editingId.value, payload) : await createNews(payload)
    if (res.code === 0) {
      ElMessage.success(editingId.value ? '更新成功' : '新增成功')
      drawerVisible.value = false
      load()
    }
  } finally {
    saving.value = false
  }
}

async function remove(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该资讯吗？', '删除确认', { type: 'warning' })
    const res = await deleteNews(id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      load()
    }
  } catch {
    // cancel
  }
}

function pickAnimal(animalNo: string) {
  const target = animals.value.find((item) => item.animalNo === animalNo)
  if (!target) return
  form.title = buildNewsTitle(target)
  form.summary = buildNewsSummary(target)
  form.coverImage = target.avatar || ''
}

async function generateFromAnimals() {
  const picked = animals.value.slice(0, 5)
  if (picked.length < 5) {
    ElMessage.warning('宠物档案不足 5 条，无法生成')
    return
  }
  try {
    await ElMessageBox.confirm('将删除当前全部 News，并按宠物档案生成 5 条，确认继续？', '覆盖发布确认', { type: 'warning' })
    saving.value = true
    for (const row of rows.value) {
      if (row.id) await deleteNews(row.id)
    }
    for (const animal of picked) {
      await createNews({
        title: buildNewsTitle(animal),
        summary: buildNewsSummary(animal),
        content: buildLinkContent(animal.animalNo || ''),
        coverImage: animal.avatar || '',
        publishTime: new Date().toISOString().slice(0, 19),
      })
    }
    ElMessage.success('已发布 5 条宠物 News')
    await load()
  } catch {
    // cancel
  } finally {
    saving.value = false
  }
}

function getImageUrl(path?: string) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

async function uploadCover(options: UploadRequestOptions) {
  uploading.value = true
  try {
    const res = await uploadFile(options.file)
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

onMounted(async () => {
  await Promise.all([load(), loadAnimals()])
})
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="header-bar">
        <span>News 管理</span>
        <div class="header-actions">
          <el-button type="warning" :loading="saving" @click="generateFromAnimals">从宠物档案发布 5 条</el-button>
          <el-button type="primary" @click="openCreate">新增资讯卡片</el-button>
        </div>
      </div>
    </template>

    <div class="news-grid">
      <div class="news-card" v-for="row in rows" :key="row.id" @click="openEdit(row)">
        <div class="news-cover-wrap">
          <img :src="getImageUrl(row.coverImage)" :alt="row.title" class="news-cover" />
        </div>
        <div class="news-info">
          <h3>{{ row.title }}</h3>
          <p class="time">{{ (row.publishTime || '').replace('T', ' ') }}</p>
          <p class="summary">{{ row.summary }}</p>
          <p class="link-no">关联宠物：{{ parseAnimalNo(row.content) || '未关联' }}</p>
          <div class="actions">
            <el-button size="small" @click.stop="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="remove(row.id!)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
  </el-card>

  <el-dialog v-model="drawerVisible" :title="editingId ? '编辑资讯' : '新增资讯'" width="920px" top="6vh" class="edit-dialog">
    <div class="form-grid dialog-form">
      <el-input v-model="form.title" placeholder="标题（关联宠物后自动同步为宠物名称）" />
      <el-input v-model="form.publishTime" placeholder="发布时间，例如 2026-04-09T10:00:00" />
      <el-select v-model="form.animalNo" placeholder="选择关联宠物档案" @change="pickAnimal">
        <el-option v-for="item in animals" :key="item.id" :label="`${item.name}（${item.animalNo || item.id}）`" :value="item.animalNo || ''" />
      </el-select>
      <div class="cover-panel">
        <el-upload action="" :show-file-list="false" :http-request="uploadCover" class="cover-upload">
          <el-button :loading="uploading">上传封面</el-button>
        </el-upload>
        <div class="cover-preview">
          <img v-if="form.coverImage" :src="getImageUrl(form.coverImage)" alt="封面预览" class="cover-preview-image" />
          <div v-else class="cover-preview-empty">上传后在这里预览封面</div>
        </div>
      </div>
      <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="正文摘要（自动填充年龄、性别、绝育状态）" />
    </div>

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">{{ editingId ? '保存修改' : '创建卡片' }}</el-button>
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
.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}
.news-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e8edf3;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.08);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.news-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 28px rgba(17, 24, 39, 0.12);
}
.news-cover-wrap {
  width: 100%;
  aspect-ratio: 16 / 9;
  min-height: 180px;
}
.news-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.news-info {
  padding: 12px;
}
.news-info h3 {
  margin: 0 0 8px;
  color: #1f2937;
  font-size: 16px;
  line-height: 1.5;
}
.time {
  margin: 0 0 8px;
  color: #8b95a4;
  font-size: 12px;
}
.summary {
  margin: 0 0 10px;
  color: #5f6b7a;
  font-size: 13px;
  line-height: 1.6;
}
.link-no {
  margin: 0 0 10px;
  color: #7c4f2d;
  font-size: 12px;
}
.actions {
  display: flex;
  gap: 8px;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.dialog-form {
  padding-top: 6px;
}
.cover-panel {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}
.cover-preview {
  width: 100%;
  max-width: 640px;
  aspect-ratio: 16 / 9;
  background: #f5f7fa;
  overflow: hidden;
}
.cover-preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.cover-preview-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #98a2b3;
  font-size: 14px;
}
.form-grid :deep(.el-textarea) {
  grid-column: 1 / -1;
}
.form-grid :deep(.el-textarea__inner) {
  min-height: 140px !important;
}
.edit-dialog :deep(.el-dialog) {
  max-height: 88vh;
}
.edit-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
  max-height: calc(88vh - 130px);
  overflow-y: auto;
}
.edit-dialog :deep(.el-dialog__footer) {
  padding-top: 8px;
}
@media (max-width: 1200px) {
  .news-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}
@media (max-width: 900px) {
  .news-grid {
    grid-template-columns: 1fr;
  }
  .form-grid {
    grid-template-columns: 1fr;
  }
  .cover-preview {
    max-width: 100%;
  }
}
</style>
