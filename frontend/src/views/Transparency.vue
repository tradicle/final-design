<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getTransparencyRows, type TransparencyRow } from '../api/dashboard'

const loading = ref(false)
const rows = ref<TransparencyRow[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getTransparencyRows()
    if (res.code === 0) rows.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header">
        <h1>透明公示</h1>
      </div>
      <el-card shadow="never" class="card">
        <el-table v-loading="loading" :data="rows" style="width: 100%">
          <el-table-column prop="month" label="月份" width="140" />
          <el-table-column prop="income" label="收入/元" width="160" />
          <el-table-column prop="expense" label="支出/元" width="160" />
          <el-table-column prop="note" label="说明" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.page { padding: 40px 0 60px; min-height: calc(100vh - 80px); }
.container { max-width: 1200px; margin: 0 auto; padding: 0 28px; }
.header { margin-bottom: 20px; }
.header h1 { margin: 0 0 8px; font-size: 32px; color: #3b2c24; }
.header p { margin: 0; color: #7a6458; }
.card { border-radius: 0; border: none; box-shadow: none; background: #fffaf6; min-height: min(1880px, calc(100vh - 170px)); }
</style>
