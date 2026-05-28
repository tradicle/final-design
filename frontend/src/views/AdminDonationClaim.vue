<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteDonationClaim,
  listDonationClaims,
  reviewDonationClaim,
  type DonationClaimRow,
} from '../api/donation-claim'

const loading = ref(false)
const rows = ref<DonationClaimRow[]>([])
const total = ref(0)
const activeStatus = ref(-1)
const query = reactive({
  keyword: '',
  page: 1,
  pageSize: 10,
})
const reviewDialogVisible = ref(false)
const reviewNote = ref('')
const reviewTarget = ref<DonationClaimRow | null>(null)
const reviewStatus = ref<number>(1)

async function load() {
  loading.value = true
  try {
    const res = await listDonationClaims({
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

function statusText(status?: number) {
  if (status === 1) return '已通过'
  if (status === 2) return '已拒绝'
  return '待审核'
}

function openAudit(row: DonationClaimRow, status: number) {
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
  const res = await reviewDonationClaim(reviewTarget.value.id, reviewStatus.value, reviewNote.value.trim())
  if (res.code === 0) {
    ElMessage.success(reviewStatus.value === 1 ? '已通过' : '已拒绝')
    reviewDialogVisible.value = false
    load()
  }
}

async function remove(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该认领记录吗？', '删除确认', { type: 'warning' })
    const res = await deleteDonationClaim(id)
    if (res.code === 0) {
      ElMessage.success('已删除认领记录')
      load()
    }
  } catch {
    // cancel
  }
}

onMounted(load)
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>物资认领审核</template>
    <div class="toolbar">
      <el-radio-group v-model="activeStatus" size="small" @change="search">
        <el-radio-button :label="-1">全部</el-radio-button>
        <el-radio-button :label="0">待审核</el-radio-button>
        <el-radio-button :label="1">已通过</el-radio-button>
        <el-radio-button :label="2">已拒绝</el-radio-button>
      </el-radio-group>
      <div class="toolbar-right">
        <el-input v-model="query.keyword" placeholder="搜索物资/联系人/手机号" clearable @keyup.enter="search" />
        <el-button @click="search">查询</el-button>
      </div>
    </div>

    <el-table :data="rows">
      <el-table-column prop="needName" label="物资名称" min-width="150" />
      <el-table-column prop="needGap" label="当前缺口" width="120" />
      <el-table-column prop="quantity" label="认领数量" width="120" />
      <el-table-column prop="contactName" label="联系人" width="110" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="pickupDate" label="预计送达" width="130" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="170" />
      <el-table-column label="备注/说明" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.reviewNote || row.remark || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
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

  <el-dialog v-model="reviewDialogVisible" :title="reviewStatus === 1 ? '通过认领' : '拒绝认领'" width="500px">
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
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
