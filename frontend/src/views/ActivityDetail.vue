<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getActivityDetail, type ActivityItem } from '../api/activity'
import { getAssetUrl } from '../utils/assets'

const route = useRoute()
const router = useRouter()
const activity = ref<ActivityItem | null>(null)

async function load() {
  const id = Number(route.params.id)
  if (!id) {
    activity.value = null
    return
  }
  const res = await getActivityDetail(id)
  if (res.code === 0) activity.value = res.data
}

onMounted(load)
watch(() => route.params.id, load)
</script>

<template>
  <div class="page">
    <div class="container">
      <div v-if="activity" class="detail-layout">
        <el-button class="back-button" text @click="router.push('/activities')">
          <el-icon><ArrowLeft /></el-icon>
          返回活动列表
        </el-button>

        <article class="detail-card">
          <img class="detail-cover" :src="getAssetUrl(activity.coverImage)" :alt="activity.title" />
          <header class="detail-header">
            <p class="time">{{ (activity.publishTime || '').replace('T', ' ') }}</p>
            <h1 class="detail-title">{{ activity.title }}</h1>
            <p class="detail-summary">{{ activity.summary }}</p>
          </header>
          <div class="detail-content" v-html="activity.content || ''"></div>
        </article>
      </div>

      <div v-else class="empty-wrap">
        <el-empty description="活动不存在或已被删除" />
        <el-button type="primary" @click="router.push('/activities')">返回爱心活动</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 32px 20px 72px;
  background: linear-gradient(180deg, #fff7ef 0%, #ffffff 22%);
}

.container {
  max-width: 960px;
  margin: 0 auto;
}

.detail-layout {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.back-button {
  width: fit-content;
  color: #cf6b23;
  font-size: 15px;
}

.detail-card {
  padding: 24px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 12px 28px rgba(44, 30, 24, 0.08);
}

.detail-cover {
  width: 100%;
  max-height: 420px;
  border-radius: 20px;
  object-fit: cover;
}

.detail-header {
  padding: 28px 4px 14px;
}

.time {
  margin: 0 0 10px;
  color: #aa8e79;
  font-size: 13px;
}

.detail-title {
  margin: 0;
  color: #2e241e;
  font-size: 34px;
  line-height: 1.35;
}

.detail-summary {
  margin: 16px 0 0;
  padding: 18px 20px;
  border-radius: 18px;
  background: #fff4ea;
  color: #66584f;
  font-size: 15px;
  line-height: 1.9;
}

.detail-content {
  padding: 4px;
  color: #43362f;
  font-size: 15px;
  line-height: 1.95;
}

.detail-content :deep(img) {
  display: block;
  width: 100%;
  max-width: 100%;
  margin: 18px auto;
  border-radius: 18px;
}

.detail-content :deep(p),
.detail-content :deep(h3) {
  margin: 14px 0;
}

.detail-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  background: #fffdfa;
}

.detail-content :deep(td),
.detail-content :deep(th) {
  padding: 10px 12px;
  border: 1px solid #f0dcc8;
  vertical-align: top;
}

.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
}
</style>
