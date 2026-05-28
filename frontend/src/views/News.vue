<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNewsList, getNewsTargetPath, type NewsItem } from '../api/news'
import { getAssetUrl } from '../utils/assets'

const router = useRouter()
const newsList = ref<NewsItem[]>([])

function goDetail(item: NewsItem) {
  router.push(getNewsTargetPath(item))
}

onMounted(async () => {
  try {
    const res = await getNewsList()
    if (res.code === 0) newsList.value = res.data
  } catch (e) { console.error(e) }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <h2 class="page-title">救助资讯</h2>

      <div class="news-list">
        <div class="news-item" v-for="item in newsList" :key="item.id" @click="goDetail(item)">
          <div class="news-img">
            <img :src="getAssetUrl(item.coverImage)" :alt="item.title" />
          </div>
          <div class="news-content">
            <h3 class="news-title">{{ item.title }}</h3>
            <p class="news-date">{{ (item.publishTime || '').replace('T', ' ') }}</p>
            <p class="news-summary">{{ item.summary }}</p>
            <el-button link type="primary">查看宠物档案</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  font-size: 32px;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.news-item {
  display: flex;
  gap: 30px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.2s;
  cursor: pointer;
}

.news-item:hover {
  transform: translateY(-2px);
}

.news-img {
  flex-shrink: 0;
  width: 320px;
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
}

.news-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.news-title {
  margin: 0 0 8px;
  font-size: 20px;
  color: #333;
}

.news-date {
  margin: 0 0 8px;
  font-size: 13px;
  color: #999;
}

.news-summary {
  margin: 0 0 12px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
