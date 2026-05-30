<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteAdoptionApplication,
  listAdoptionApplications,
  reviewAdoptionApplication,
  type AdoptionApplicationRow
} from '../api/adoption-application'

const loading = ref(false)
const rows = ref<AdoptionApplicationRow[]>([])
const total = ref(0)
const activeStatus = ref(-1)
const query = reactive({
  keyword: '',
  page: 1,
  pageSize: 10,
})
const reviewDialogVisible = ref(false)
const reviewNote = ref('')
const reviewTarget = ref<AdoptionApplicationRow | null>(null)
const reviewStatus = ref<number>(1)
const detailDialogVisible = ref(false)
const detailRow = ref<AdoptionApplicationRow | null>(null)

async function load() {
  loading.value = true
  try {
    const res = await listAdoptionApplications({
      status: activeStatus.value === -1 ? undefined : activeStatus.value,
      keyword: query.keyword || undefined,
      page: query.page,
      pageSize: query.pageSize,
    })
    if (res.code === 0) {
      rows.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  load()
}

function openAudit(row: AdoptionApplicationRow, status: number) {
  reviewTarget.value = row
  reviewStatus.value = status
  reviewNote.value = status === 1 ? '审核通过' : ''
  reviewDialogVisible.value = true
}

async function submitAudit() {
  if (!reviewTarget.value) return
  if (reviewStatus.value === 2 && !reviewNote.value.trim()) {
    ElMessage.warning('拒绝时请填写审核理由')
    return
  }
  const res = await reviewAdoptionApplication(reviewTarget.value.id, reviewStatus.value, reviewNote.value.trim())
  if (res.code === 0) {
    ElMessage.success(reviewStatus.value === 1 ? '已通过' : '已拒绝')
    reviewDialogVisible.value = false
    load()
  }
}

async function remove(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该申请记录吗？', '删除确认', { type: 'warning' })
    const res = await deleteAdoptionApplication(id)
    if (res.code === 0) {
      ElMessage.success('已删除申请')
      load()
    }
  } catch {
    // cancel
  }
}

function statusText(status?: number) {
  if (status === 1) return '已通过'
  if (status === 2) return '已拒绝'
  return '待审核'
}

function incomeText(income?: string) {
  if (income === 'LEVEL1') return '3000以下'
  if (income === 'LEVEL2') return '3000-8000'
  if (income === 'LEVEL3') return '8000-15000'
  if (income === 'LEVEL4') return '15000以上'
  return income || '-'
}

function housingText(housing?: string) {
  if (housing === 'OWN') return '自有住房'
  if (housing === 'RENT') return '租房（已获房东同意）'
  if (housing === 'OTHER') return '其他'
  return housing || '-'
}

function experienceText(exp?: string) {
  if (exp === 'NONE') return '无经验'
  if (exp === 'HAD') return '曾经养过'
  if (exp === 'HAVE') return '目前正在养'
  return exp || '-'
}

function showDetail(row: AdoptionApplicationRow) {
  detailRow.value = row
  detailDialogVisible.value = true
}

onMounted(load)
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>领养申请审核</template>
    <div class="toolbar">
      <el-radio-group v-model="activeStatus" size="small" @change="search">
        <el-radio-button :label="-1">全部</el-radio-button>
        <el-radio-button :label="0">待审核</el-radio-button>
        <el-radio-button :label="1">已通过</el-radio-button>
        <el-radio-button :label="2">已拒绝</el-radio-button>
      </el-radio-group>
      <div class="toolbar-right">
        <el-input v-model="query.keyword" placeholder="搜索申请人/手机/地址" clearable @keyup.enter="search" />
        <el-button @click="search">查询</el-button>
      </div>
    </div>
    <el-table :data="rows">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="expand-wrap">
            <el-descriptions :column="3" border>
              <el-descriptions-item label="申请人">{{ row.applicantName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ row.age ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="手机">{{ row.phone || '-' }}</el-descriptions-item>
              <el-descriptions-item label="微信">{{ row.wechat || '-' }}</el-descriptions-item>
              <el-descriptions-item label="职业">{{ row.job || '-' }}</el-descriptions-item>
              <el-descriptions-item label="收入">{{ incomeText(row.income) }}</el-descriptions-item>
              <el-descriptions-item label="住房">{{ housingText(row.housing) }}</el-descriptions-item>
              <el-descriptions-item label="养宠经验">{{ experienceText(row.experience) }}</el-descriptions-item>
              <el-descriptions-item label="家庭成员">{{ row.familyMembers || '-' }}</el-descriptions-item>
              <el-descriptions-item label="地址" :span="3">{{ row.address || '-' }}</el-descriptions-item>
              <el-descriptions-item label="申请理由" :span="3">{{ row.reason || '-' }}</el-descriptions-item>
              <el-descriptions-item label="宠物名称">{{ row.animalName || '该宠物已删除' }}</el-descriptions-item>
              <el-descriptions-item label="申请用户ID">{{ row.userId ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="提交时间">{{ row.createTime || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="applicantName" label="申请人" width="110" />
      <el-table-column prop="phone" label="手机" width="130" />
      <el-table-column label="宠物名称" width="120">
        <template #default="{ row }">
          {{ row.animalName || '该宠物已删除' }}
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
      <el-table-column prop="reason" label="申请理由" min-width="220" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="170" />
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="openAudit(row, 1)">通过</el-button>
          <el-button size="small" type="warning" @click="openAudit(row, 2)">拒绝</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        background
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10, 20, 30]"
        :total="total"
        @current-change="load"
        @size-change="search"
      />
    </div>
  </el-card>

  <el-dialog v-model="reviewDialogVisible" :title="reviewStatus === 1 ? '通过申请' : '拒绝申请'" width="500px">
    <el-input
      v-model="reviewNote"
      type="textarea"
      :rows="4"
      :placeholder="reviewStatus === 1 ? '可填写通过说明（可选）' : '请填写拒绝理由（必填）'"
    />
    <template #footer>
      <el-button @click="reviewDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAudit">确认</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="detailDialogVisible" title="申请详情" width="680px">
    <div v-if="detailRow" class="detail-grid">
      <div><b>申请人：</b>{{ detailRow.applicantName }}</div>
      <div><b>年龄：</b>{{ detailRow.age }}</div>
      <div><b>手机：</b>{{ detailRow.phone }}</div>
      <div><b>微信：</b>{{ detailRow.wechat || '-' }}</div>
      <div><b>职业：</b>{{ detailRow.job || '-' }}</div>
      <div><b>收入：</b>{{ incomeText(detailRow.income) }}</div>
      <div><b>住房：</b>{{ housingText(detailRow.housing) }}</div>
      <div><b>经验：</b>{{ experienceText(detailRow.experience) }}</div>
      <div class="full"><b>地址：</b>{{ detailRow.address }}</div>
      <div class="full"><b>家庭成员：</b>{{ detailRow.familyMembers || '-' }}</div>
      <div class="full"><b>申请理由：</b>{{ detailRow.reason || '-' }}</div>
      <div><b>宠物名称：</b>{{ detailRow.animalName || '该宠物已删除' }}</div>
      <div><b>申请用户ID：</b>{{ detailRow.userId ?? '-' }}</div>
      <div><b>提交时间：</b>{{ detailRow.createTime || '-' }}</div>
      <div><b>更新时间：</b>{{ detailRow.updateTime || '-' }}</div>
      <div class="full"><b>审核备注：</b>{{ detailRow.reviewNote || '-' }}</div>
    </div>
  </el-dialog>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.toolbar-right {
  display: flex;
  gap: 8px;
  min-width: min(100%, 360px);
}
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 16px;
  color: #4b5563;
}
.full {
  grid-column: 1 / -1;
}
.expand-wrap {
  padding: 6px 8px;
  background: #faf7f4;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
