<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getDashboardCharts,
  getDashboardSummary,
  getWeeklyUpdates,
  type DashboardChartItem,
  type DashboardCharts,
  type DashboardSummary,
  type WeeklyUpdate,
} from '../api/dashboard'
import { getAnimalList } from '../api/animal'
import { getNewsList, type NewsItem } from '../api/news'

const router = useRouter()
const loading = ref(false)
const summary = ref<DashboardSummary>({
  totalRescueCount: 0,
  adoptionSuccessCount: 0,
  activeAnimals: 0,
  totalProfiles: 0,
  monthlyPublicBudget: '0'
})
const weeklyUpdates = ref<WeeklyUpdate[]>([])
const news = ref<NewsItem[]>([])
const charts = ref<DashboardCharts>({
  animalCategoryStats: [],
  animalStatusStats: [],
  adoptionStatusStats: [],
  donationClaimStatusStats: [],
  overviewStats: [],
})

const animalCount = ref(0)
const catCount = ref(0)
const dogCount = ref(0)
const recentAnimals = ref<{ name: string; category: string; updateTime: string }[]>([])

function formatCurrencyDisplay(value: string | number) {
  const text = String(value ?? '').trim()
  if (!text) return '0元'
  if (text.endsWith('元')) return text
  const normalized = text.replace(/[^\d.,]/g, '')
  return `${normalized || '0'}元`
}

const panels = computed(() => [
  { title: '累计救助', value: summary.value.totalRescueCount, tip: '历史累计规模' },
  { title: '成功领养', value: summary.value.adoptionSuccessCount, tip: '已进入稳定家庭' },
  { title: '待领养档案', value: summary.value.activeAnimals, tip: '当前可申请' },
  { title: '月度公开支出', value: formatCurrencyDisplay(summary.value.monthlyPublicBudget), tip: '透明公示口径' },
  { title: '资讯总数', value: news.value.length, tip: 'News 管理模块' },
  { title: '宠物总档案', value: animalCount.value, tip: '包含猫狗及历史档案' }
])

onMounted(async () => {
  loading.value = true
  try {
    const [summaryRes, updatesRes, animalRes, newsRes, chartRes] = await Promise.all([
      getDashboardSummary(),
      getWeeklyUpdates(),
      getAnimalList(),
      getNewsList(),
      getDashboardCharts(),
    ])
    if (summaryRes.code === 0) summary.value = summaryRes.data
    if (updatesRes.code === 0) weeklyUpdates.value = updatesRes.data
    if (newsRes.code === 0) news.value = newsRes.data
    if (chartRes.code === 0) charts.value = chartRes.data
    if (animalRes.code === 0) {
      const rows = animalRes.data
      animalCount.value = rows.length
      catCount.value = rows.filter((i) => i.category === 'CAT').length
      dogCount.value = rows.filter((i) => i.category === 'DOG').length
      recentAnimals.value = rows
        .slice()
        .sort((a, b) => (b.updateTime || '').localeCompare(a.updateTime || ''))
        .slice(0, 5)
        .map((i) => ({ name: i.name, category: i.category === 'CAT' ? '猫' : '狗', updateTime: (i.updateTime || '').replace('T', ' ') }))
    }
  } finally {
    loading.value = false
  }
})

function maxValue(items: DashboardChartItem[]) {
  return Math.max(...items.map((item) => item.value), 1)
}

function barWidth(value: number, max: number) {
  return `${Math.max((value / max) * 100, value > 0 ? 12 : 0)}%`
}
</script>

<template>
  <div class="page" v-loading="loading">
    <el-card class="hero-card" shadow="never">
      <div class="hero">
        <div>
          <h2>后台仪表盘</h2>
          <p>今日可快速处理资讯发布、宠物档案更新、社区审核与内容运营维护。</p>
        </div>
        <div class="quick-actions">
          <el-button type="primary" @click="router.push('/admin/news')">发布资讯</el-button>
          <el-button @click="router.push('/admin/animals')">管理宠物</el-button>
          <el-button @click="router.push('/admin/community')">去审核</el-button>
        </div>
      </div>
    </el-card>

    <div class="stats-grid">
      <el-card v-for="item in panels" :key="item.title" class="stat-card" shadow="never">
        <p class="k">{{ item.title }}</p>
        <p class="v">{{ item.value }}</p>
        <p class="tip">{{ item.tip }}</p>
      </el-card>
    </div>

    <div class="two-col">
      <el-card shadow="never">
        <template #header>业务概况</template>
        <div class="mini">
          <div class="mini-item"><span>猫咪档案</span><b>{{ catCount }}</b></div>
          <div class="mini-item"><span>狗狗档案</span><b>{{ dogCount }}</b></div>
          <div class="mini-item"><span>本周更新条目</span><b>{{ weeklyUpdates.length }}</b></div>
          <div class="mini-item"><span>资讯条目</span><b>{{ news.length }}</b></div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>最近更新宠物</template>
        <div class="recent-list">
          <div class="row" v-for="item in recentAnimals" :key="`${item.name}-${item.updateTime}`">
            <span>{{ item.name }}（{{ item.category }}）</span>
            <span>{{ item.updateTime }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <div class="chart-grid">
      <el-card shadow="never" class="chart-card">
        <template #header>宠物档案统计</template>
        <div class="bar-list">
          <div class="bar-row" v-for="item in charts.animalCategoryStats" :key="item.label">
            <div class="bar-meta">
              <span>{{ item.label }}</span>
              <b>{{ item.value }}</b>
            </div>
            <div class="bar-track"><span class="bar-fill warm" :style="{ width: barWidth(item.value, maxValue(charts.animalCategoryStats)) }" /></div>
          </div>
          <div class="bar-row" v-for="item in charts.animalStatusStats" :key="item.label">
            <div class="bar-meta">
              <span>{{ item.label }}</span>
              <b>{{ item.value }}</b>
            </div>
            <div class="bar-track"><span class="bar-fill soft" :style="{ width: barWidth(item.value, maxValue(charts.animalStatusStats)) }" /></div>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="chart-card">
        <template #header>领养申请状态</template>
        <div class="bar-list">
          <div class="bar-row" v-for="item in charts.adoptionStatusStats" :key="item.label">
            <div class="bar-meta">
              <span>{{ item.label }}</span>
              <b>{{ item.value }}</b>
            </div>
            <div class="bar-track"><span class="bar-fill accent" :style="{ width: barWidth(item.value, maxValue(charts.adoptionStatusStats)) }" /></div>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="chart-card">
        <template #header>物资认领状态</template>
        <div class="bar-list">
          <div class="bar-row" v-for="item in charts.donationClaimStatusStats" :key="item.label">
            <div class="bar-meta">
              <span>{{ item.label }}</span>
              <b>{{ item.value }}</b>
            </div>
            <div class="bar-track"><span class="bar-fill green" :style="{ width: barWidth(item.value, maxValue(charts.donationClaimStatusStats)) }" /></div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 14px; }
.hero-card { border: 1px solid #eadfd5; background: #fffaf6; }
.hero { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.hero h2 { margin: 0 0 8px; color: #5a3e2d; }
.hero p { margin: 0; color: #725f52; }
.quick-actions { display: flex; gap: 10px; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.stat-card { border: 1px solid #eadfd5; background: #fff; }
.k { margin: 0; color: #8a7668; font-size: 13px; }
.v { margin: 6px 0; color: #5a3e2d; font-size: 30px; font-weight: 700; line-height: 1; }
.tip { margin: 0; color: #9a887b; font-size: 12px; }
.two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.mini { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.mini-item { border: 1px solid #eee2d8; border-radius: 10px; padding: 10px; display: flex; justify-content: space-between; color: #6d594c; }
.recent-list { display: flex; flex-direction: column; gap: 8px; }
.row { display: flex; justify-content: space-between; border-bottom: 1px dashed #eee1d7; padding-bottom: 8px; color: #6b5a4f; font-size: 13px; }
.chart-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.chart-card { border: 1px solid #eadfd5; }
.bar-list { display: flex; flex-direction: column; gap: 12px; }
.bar-row { display: flex; flex-direction: column; gap: 6px; }
.bar-meta { display: flex; justify-content: space-between; color: #6f5c50; font-size: 13px; }
.bar-track { height: 10px; border-radius: 999px; background: #f3e8df; overflow: hidden; }
.bar-fill { display: block; height: 100%; border-radius: 999px; }
.bar-fill.warm { background: linear-gradient(90deg, #d18b62 0%, #b9694a 100%); }
.bar-fill.soft { background: linear-gradient(90deg, #f0b68d 0%, #e08c66 100%); }
.bar-fill.accent { background: linear-gradient(90deg, #8d7bff 0%, #6b5ce7 100%); }
.bar-fill.green { background: linear-gradient(90deg, #7cc59b 0%, #4cae78 100%); }
@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .two-col { grid-template-columns: 1fr; }
  .chart-grid { grid-template-columns: 1fr; }
}
</style>
