<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listWeeklyUpdates,
  createWeeklyUpdate,
  updateWeeklyUpdate,
  deleteWeeklyUpdate,
  listTransparency,
  createTransparency,
  updateTransparency,
  deleteTransparency,
  listUrgentNeeds,
  createUrgentNeed,
  updateUrgentNeed,
  deleteUrgentNeed,
  listDonationRecordsManage,
  createDonationRecord,
  updateDonationRecord,
  deleteDonationRecord,
  getDashboardMetrics,
  updateDashboardMetrics,
  type WeeklyUpdateRow,
  type TransparencyRowManage,
  type UrgentNeedRow,
  type DonationRecordRow,
  type DashboardMetricsRow
} from '../api/admin-content'

const active = ref('weekly')
const loading = ref(false)
const weeklyRows = ref<WeeklyUpdateRow[]>([])
const transparencyRows = ref<TransparencyRowManage[]>([])
const urgentRows = ref<UrgentNeedRow[]>([])
const donationRows = ref<DonationRecordRow[]>([])
const donationPage = ref(1)
const donationPageSize = 10
const pagedDonationRows = computed(() => {
  const start = (donationPage.value - 1) * donationPageSize
  return donationRows.value.slice(start, start + donationPageSize)
})
const metrics = ref<DashboardMetricsRow>({ totalRescueCount: '2680', adoptionSuccessBase: '1930' })

const weeklyForm = ref<WeeklyUpdateRow>({ title: '', description: '', sortOrder: 1 })
const transparencyForm = ref<TransparencyRowManage>({ month: '', income: '', expense: '', note: '', sortOrder: 1 })
const urgentForm = ref<UrgentNeedRow>({ name: '', gap: '', updatedAt: '', sortOrder: 1 })
const donationForm = ref<DonationRecordRow>({ date: '', donor: '', item: '', quantity: '', unit: '', remark: '', sortOrder: 1 })

function increaseSortValue(target: { sortOrder: number }) {
  target.sortOrder = Math.max(1, Number(target.sortOrder || 1) + 1)
}

function decreaseSortValue(target: { sortOrder: number }) {
  target.sortOrder = Math.max(1, Number(target.sortOrder || 1) - 1)
}

function sanitizeCurrencyInput(value: string) {
  const text = String(value ?? '')
  const normalized = text.replace(/[^\d.]/g, '')
  const firstDotIndex = normalized.indexOf('.')
  if (firstDotIndex === -1) {
    return normalized
  }
  return normalized.slice(0, firstDotIndex + 1) + normalized.slice(firstDotIndex + 1).replace(/\./g, '')
}

async function reload() {
  loading.value = true
  try {
    const [w, t, u, d] = await Promise.all([
      listWeeklyUpdates(),
      listTransparency(),
      listUrgentNeeds(),
      listDonationRecordsManage()
    ])
    if (w.code === 0) weeklyRows.value = w.data
    if (t.code === 0) transparencyRows.value = t.data
    if (u.code === 0) urgentRows.value = u.data
    if (d.code === 0) donationRows.value = d.data
    const m = await getDashboardMetrics()
    if (m.code === 0) metrics.value = m.data
  } finally {
    loading.value = false
  }
}

async function saveMetrics() {
  const res = await updateDashboardMetrics(metrics.value)
  if (res.code === 0) {
    ElMessage.success('统计指标已保存')
  }
}

async function saveWeekly(row?: WeeklyUpdateRow) {
  const payload = row || weeklyForm.value
  const res = payload.id ? await updateWeeklyUpdate(payload.id, payload) : await createWeeklyUpdate(payload)
  if (res.code === 0) {
    ElMessage.success('保存成功')
    if (!row) weeklyForm.value = { title: '', description: '', sortOrder: 1 }
    reload()
  }
}
async function removeWeekly(id: number) {
  const res = await deleteWeeklyUpdate(id)
  if (res.code === 0) {
    ElMessage.success('删除成功')
    reload()
  }
}

async function saveTransparency(row?: TransparencyRowManage) {
  const source = row || transparencyForm.value
  const payload = {
    ...source,
    expense: sanitizeCurrencyInput(source.expense),
  }
  const res = payload.id ? await updateTransparency(payload.id, payload) : await createTransparency(payload)
  if (res.code === 0) {
    ElMessage.success('保存成功')
    if (!row) transparencyForm.value = { month: '', income: '', expense: '', note: '', sortOrder: 1 }
    if (row) row.expense = payload.expense
    reload()
  }
}
async function removeTransparency(id: number) {
  const res = await deleteTransparency(id)
  if (res.code === 0) {
    ElMessage.success('删除成功')
    reload()
  }
}

async function saveUrgent(row?: UrgentNeedRow) {
  const payload = row || urgentForm.value
  const res = payload.id ? await updateUrgentNeed(payload.id, payload) : await createUrgentNeed(payload)
  if (res.code === 0) {
    ElMessage.success('保存成功')
    if (!row) urgentForm.value = { name: '', gap: '', updatedAt: '', sortOrder: 1 }
    reload()
  }
}
async function removeUrgent(id: number) {
  const res = await deleteUrgentNeed(id)
  if (res.code === 0) {
    ElMessage.success('删除成功')
    reload()
  }
}

async function saveDonation(row?: DonationRecordRow) {
  const payload = row || donationForm.value
  const res = payload.id ? await updateDonationRecord(payload.id, payload) : await createDonationRecord(payload)
  if (res.code === 0) {
    ElMessage.success('保存成功')
    if (!row) donationForm.value = { date: '', donor: '', item: '', quantity: '', unit: '', remark: '', sortOrder: 1 }
    reload()
  }
}
async function removeDonation(id: number) {
  const res = await deleteDonationRecord(id)
  if (res.code === 0) {
    ElMessage.success('删除成功')
    reload()
  }
}

onMounted(reload)
</script>

<template>
  <div class="page">
    <el-card v-loading="loading" shadow="never">
      <template #header>内容运营（每周更新/公示）</template>
      <el-card shadow="never" class="metrics-card">
        <template #header>首页核心统计</template>
        <div class="metrics-form">
          <div class="metric-field">
            <el-tag effect="plain">累计救助</el-tag>
            <el-input v-model="metrics.totalRescueCount" placeholder="例如 2680" />
          </div>
          <div class="metric-field">
            <el-tag effect="plain" type="success">成功领养</el-tag>
            <el-input v-model="metrics.adoptionSuccessBase" placeholder="例如 1930" />
          </div>
          <el-button type="primary" @click="saveMetrics">保存统计</el-button>
        </div>
      </el-card>
      <el-tabs v-model="active">
        <el-tab-pane label="每周更新" name="weekly">
          <div class="inline-form">
            <el-input v-model="weeklyForm.title" placeholder="标题" />
            <el-input v-model="weeklyForm.description" placeholder="描述" />
            <div class="sort-stepper">
              <el-button class="sort-btn" @click="decreaseSortValue(weeklyForm)">-</el-button>
              <el-input-number v-model="weeklyForm.sortOrder" :min="1" :controls="false" class="sort-input" />
              <el-button class="sort-btn" @click="increaseSortValue(weeklyForm)">+</el-button>
            </div>
            <el-button type="primary" @click="saveWeekly()">新增</el-button>
          </div>
          <el-table :data="weeklyRows">
            <el-table-column prop="title" label="标题"><template #default="{ row }"><el-input v-model="row.title" /></template></el-table-column>
            <el-table-column prop="description" label="描述"><template #default="{ row }"><el-input v-model="row.description" /></template></el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="170">
              <template #default="{ row }">
                <div class="sort-stepper">
                  <el-button class="sort-btn" @click="decreaseSortValue(row)">-</el-button>
                  <el-input-number v-model="row.sortOrder" :min="1" :controls="false" class="sort-input" />
                  <el-button class="sort-btn" @click="increaseSortValue(row)">+</el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="saveWeekly(row)">保存</el-button>
                <el-button size="small" type="danger" @click="removeWeekly(row.id!)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="透明公示" name="transparency">
          <div class="inline-form">
            <el-input v-model="transparencyForm.month" placeholder="月份，如 2026-04" />
            <el-input v-model="transparencyForm.income" placeholder="收入，如 ¥42,300" />
            <el-input v-model="transparencyForm.expense" placeholder="支出，如 39180" />
            <el-input v-model="transparencyForm.note" placeholder="说明" />
            <div class="sort-stepper">
              <el-button class="sort-btn" @click="decreaseSortValue(transparencyForm)">-</el-button>
              <el-input-number v-model="transparencyForm.sortOrder" :min="1" :controls="false" class="sort-input" />
              <el-button class="sort-btn" @click="increaseSortValue(transparencyForm)">+</el-button>
            </div>
            <el-button type="primary" @click="saveTransparency()">新增</el-button>
          </div>
          <el-table :data="transparencyRows">
            <el-table-column label="月份"><template #default="{ row }"><el-input v-model="row.month" /></template></el-table-column>
            <el-table-column label="收入"><template #default="{ row }"><el-input v-model="row.income" /></template></el-table-column>
            <el-table-column label="支出"><template #default="{ row }"><el-input v-model="row.expense" placeholder="只输入数字" /></template></el-table-column>
            <el-table-column label="说明"><template #default="{ row }"><el-input v-model="row.note" /></template></el-table-column>
            <el-table-column label="排序" width="170">
              <template #default="{ row }">
                <div class="sort-stepper">
                  <el-button class="sort-btn" @click="decreaseSortValue(row)">-</el-button>
                  <el-input-number v-model="row.sortOrder" :min="1" :controls="false" class="sort-input" />
                  <el-button class="sort-btn" @click="increaseSortValue(row)">+</el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="saveTransparency(row)">保存</el-button>
                <el-button size="small" type="danger" @click="removeTransparency(row.id!)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="急需物资" name="urgent">
          <div class="inline-form">
            <el-input v-model="urgentForm.name" placeholder="物资名称" />
            <el-input v-model="urgentForm.gap" placeholder="缺口，如 30 袋" />
            <el-input v-model="urgentForm.updatedAt" placeholder="更新日期，如 2026-04-05" />
            <div class="sort-stepper">
              <el-button class="sort-btn" @click="decreaseSortValue(urgentForm)">-</el-button>
              <el-input-number v-model="urgentForm.sortOrder" :min="1" :controls="false" class="sort-input" />
              <el-button class="sort-btn" @click="increaseSortValue(urgentForm)">+</el-button>
            </div>
            <el-button type="primary" @click="saveUrgent()">新增</el-button>
          </div>
          <el-table :data="urgentRows">
            <el-table-column label="物资"><template #default="{ row }"><el-input v-model="row.name" /></template></el-table-column>
            <el-table-column label="缺口"><template #default="{ row }"><el-input v-model="row.gap" /></template></el-table-column>
            <el-table-column label="更新"><template #default="{ row }"><el-input v-model="row.updatedAt" /></template></el-table-column>
            <el-table-column label="排序" width="170">
              <template #default="{ row }">
                <div class="sort-stepper">
                  <el-button class="sort-btn" @click="decreaseSortValue(row)">-</el-button>
                  <el-input-number v-model="row.sortOrder" :min="1" :controls="false" class="sort-input" />
                  <el-button class="sort-btn" @click="increaseSortValue(row)">+</el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="saveUrgent(row)">保存</el-button>
                <el-button size="small" type="danger" @click="removeUrgent(row.id!)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="捐助公示" name="donation">
          <div class="inline-form">
            <el-input v-model="donationForm.date" placeholder="日期，如 2026-04-01" />
            <el-input v-model="donationForm.donor" placeholder="捐助者" />
            <el-input v-model="donationForm.item" placeholder="捐助内容" />
            <el-input v-model="donationForm.quantity" placeholder="数量" />
            <el-input v-model="donationForm.unit" placeholder="单位" />
            <el-input v-model="donationForm.remark" placeholder="备注" />
            <div class="sort-stepper">
              <el-button class="sort-btn" @click="decreaseSortValue(donationForm)">-</el-button>
              <el-input-number v-model="donationForm.sortOrder" :min="1" :controls="false" class="sort-input" />
              <el-button class="sort-btn" @click="increaseSortValue(donationForm)">+</el-button>
            </div>
            <el-button type="primary" @click="saveDonation()">新增</el-button>
          </div>
          <el-table :data="pagedDonationRows">
            <el-table-column label="日期"><template #default="{ row }"><el-input v-model="row.date" /></template></el-table-column>
            <el-table-column label="捐助者"><template #default="{ row }"><el-input v-model="row.donor" /></template></el-table-column>
            <el-table-column label="内容"><template #default="{ row }"><el-input v-model="row.item" /></template></el-table-column>
            <el-table-column label="数量"><template #default="{ row }"><el-input v-model="row.quantity" /></template></el-table-column>
            <el-table-column label="单位"><template #default="{ row }"><el-input v-model="row.unit" /></template></el-table-column>
            <el-table-column label="备注"><template #default="{ row }"><el-input v-model="row.remark" /></template></el-table-column>
            <el-table-column label="排序" width="170">
              <template #default="{ row }">
                <div class="sort-stepper">
                  <el-button class="sort-btn" @click="decreaseSortValue(row)">-</el-button>
                  <el-input-number v-model="row.sortOrder" :min="1" :controls="false" class="sort-input" />
                  <el-button class="sort-btn" @click="increaseSortValue(row)">+</el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="saveDonation(row)">保存</el-button>
                <el-button size="small" type="danger" @click="removeDonation(row.id!)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="donation-pagination">
            <el-pagination
              v-model:current-page="donationPage"
              background
              layout="prev, pager, next"
              :page-size="donationPageSize"
              :total="donationRows.length"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.page { padding: 0; }
.inline-form {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 10px;
  margin-bottom: 12px;
}
.metrics-card {
  margin-bottom: 12px;
}
.metrics-form {
  display: grid;
  grid-template-columns: 1fr 1fr 120px;
  gap: 10px;
}
.metric-field {
  display: grid;
  grid-template-columns: 80px 1fr;
  gap: 8px;
  align-items: center;
}

.sort-stepper {
  display: flex;
  align-items: center;
  gap: 6px;
}

.sort-btn {
  width: 30px;
  min-width: 30px;
  height: 30px;
  padding: 0;
}

.sort-input {
  width: 72px;
}

.donation-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
