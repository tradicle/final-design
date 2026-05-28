<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getNewsList, parseNewsAnimalNo, type NewsItem } from '../api/news'

const route = useRoute()
const router = useRouter()

const article = ref<NewsItem | null>(null)

const articleId = computed(() => Number(route.params.id))

function getImageUrl(path?: string) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

function goBack() {
  router.push('/news')
}

async function loadArticle() {
  article.value = null
  try {
    const res = await getNewsList()
    if (res.code === 0) {
      const target = res.data.find((item) => item.id === articleId.value) || null
      if (!target) return
      const animalNo = parseNewsAnimalNo(target.content)
      if (animalNo) {
        router.replace(`/pet/${animalNo}`)
        return
      }
      article.value = target
    }
  } catch (e) { console.error(e) }
}

onMounted(() => loadArticle())

watch(
  () => route.params.id,
  () => loadArticle()
)

watch(
  () => route.fullPath,
  () => {
    window.scrollTo({ top: 0, behavior: 'auto' })
  },
  { immediate: true }
)
</script>

<template>
  <div class="page">
    <div class="container">
      <div v-if="article" class="detail-layout">
        <el-button class="back-button" text @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回活动列表
        </el-button>

        <article class="detail-card">
          <img class="detail-cover" :src="getImageUrl(article.coverImage)" :alt="article.title" />

          <header class="detail-header">
            <h1 class="detail-title">{{ article.title }}</h1>
            <p class="detail-summary">{{ article.summary }}</p>
          </header>

          <div class="detail-content" v-html="article.content || ''"></div>
        </article>
      </div>

      <div v-else class="empty-wrap">
        <el-empty description="活动不存在或已被移除" />
        <el-button type="primary" @click="goBack">返回爱心活动</el-button>
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
  border-radius: 0;
  background: #fff;
  box-shadow: none;
}

.detail-cover {
  width: 100%;
  max-height: 420px;
  border-radius: 22px;
  object-fit: cover;
}

.detail-header {
  padding: 28px 4px 14px;
}

.detail-title {
  margin: 0;
  color: #2e241e;
  font-size: 36px;
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

.detail-content :deep(figure) {
  margin: 18px 0;
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

@media (max-width: 768px) {
  .page {
    padding: 24px 14px 56px;
  }

  .detail-card {
    padding: 16px;
    border-radius: 0;
  }

  .detail-title {
    font-size: 28px;
  }

  .detail-header {
    padding: 22px 2px 10px;
  }
}
</style>
