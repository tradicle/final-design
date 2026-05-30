<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getActivityList, type ActivityItem } from '../api/activity'
import { getAssetUrl } from '../utils/assets'

const router = useRouter()
const loading = ref(false)
const activities = ref<ActivityItem[]>([])

async function load() {
  loading.value = true
  try {
    const res = await getActivityList()
    if (res.code === 0) activities.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="hero">
        <h1>爱心活动</h1>
      </div>

      <div class="activity-list" v-loading="loading">
        <div class="activity-card" v-for="item in activities" :key="item.id" @click="router.push(`/activities/${item.id}`)">
          <img class="cover" :src="getAssetUrl(item.coverImage)" :alt="item.title" />
          <div class="content">
            <p class="time">{{ (item.publishTime || '').replace('T', ' ') }}</p>
            <h3>{{ item.title }}</h3>
            <p class="summary">{{ item.summary }}</p>
            <el-button link type="primary">查看活动详情</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 32px 20px 56px;
  background: #faf7f2;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
}

.hero {
  padding: 18px 0 32px;
}

.hero h1 {
  margin: 0;
  font-size: 38px;
  color: #2e241e;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.activity-card {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 0;
  overflow: hidden;
  border-radius: 18px;
  border: 1px solid #f0dcc8;
  background: #fff;
  cursor: pointer;
  box-shadow: 0 10px 26px rgba(54, 36, 28, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.activity-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 30px rgba(54, 36, 28, 0.12);
}

.cover {
  width: 100%;
  height: 100%;
  min-height: 220px;
  object-fit: cover;
}

.content {
  padding: 24px 26px;
}

.time {
  margin: 0 0 8px;
  color: #9f8b7d;
  font-size: 13px;
}

.content h3 {
  margin: 0 0 12px;
  color: #2e241e;
  font-size: 24px;
  line-height: 1.45;
}

.summary {
  margin: 0 0 14px;
  color: #66584f;
  line-height: 1.85;
}

@media (max-width: 768px) {
  .page {
    padding: 24px 14px 42px;
  }

  .activity-card {
    grid-template-columns: 1fr;
  }

  .cover {
    min-height: 200px;
  }

  .hero h1 {
    font-size: 30px;
  }
}
</style>
