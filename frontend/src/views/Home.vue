<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { ref, onMounted, computed } from 'vue'
import { getAnimalList, type Animal } from '../api/animal'
import { getDashboardSummary, getWeeklyUpdates, type DashboardSummary, type WeeklyUpdate } from '../api/dashboard'
import { getNewsList, getNewsTargetPath, type NewsItem } from '../api/news'
import { getActivityList, type ActivityItem } from '../api/activity'
import { getAssetUrl } from '../utils/assets'

const router = useRouter()
const starAnimals = ref<Animal[]>([])
const newsList = ref<NewsItem[]>([])
const activityList = ref<ActivityItem[]>([])
const summary = ref<DashboardSummary>({ totalRescueCount: 0, adoptionSuccessCount: 0, activeAnimals: 0, totalProfiles: 0, monthlyPublicBudget: '0' })
const weeklyUpdates = ref<WeeklyUpdate[]>([])

const homeNews = computed(() => newsList.value.slice(0, 5))
const catAnimals = computed(() => starAnimals.value.filter((item) => item.category === 'CAT').slice(0, 5))
const dogAnimals = computed(() => starAnimals.value.filter((item) => item.category !== 'CAT').slice(0, 5))
const featuredActivities = computed(() => activityList.value.slice(0, 3))

function go(path: string) {
  router.push(path)
}

function toPet(animal: Animal) {
  const id = animal.animalNo || String(animal.id)
  router.push(`/pet/${id}`)
}

function toNewsTarget(item: NewsItem) {
  router.push(getNewsTargetPath(item))
}

onMounted(async () => {
  try {
    const res = await getAnimalList()
    if (res.code === 0) {
      starAnimals.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
  try {
    const s = await getDashboardSummary()
    if (s.code === 0) summary.value = s.data
  } catch (e) { console.error(e) }
  try {
    const u = await getWeeklyUpdates()
    if (u.code === 0) weeklyUpdates.value = u.data
  } catch (e) { console.error(e) }
  try {
    const newsRes = await getNewsList()
    if (newsRes.code === 0) newsList.value = newsRes.data
  } catch (e) { console.error(e) }
  try {
    const activityRes = await getActivityList()
    if (activityRes.code === 0) activityList.value = activityRes.data
  } catch (e) { console.error(e) }
})
</script>

<template>
  <div class="page">
    <section class="hero-section">
      <div class="container hero-grid">
        <el-card class="news-window" shadow="never">
          <template #header>
            <div class="block-header">
              <h2>news</h2>
            </div>
          </template>
          <el-carousel :interval="4200" height="360px" indicator-position="outside">
            <el-carousel-item v-for="item in homeNews" :key="item.id">
              <div class="news-slide" @click="toNewsTarget(item)">
                <img :src="getAssetUrl(item.coverImage)" :alt="item.title" />
                <div class="news-overlay">
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.summary || '点击查看对应宠物档案' }}</p>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-card>

        <el-card class="notice-window" shadow="never">
          <template #header>
            <div class="block-header">
              <h2>领养须知</h2>
            </div>
          </template>
          <div class="notice-content">
            <p>我们领养之家最初是几个喜欢小动物的朋友共同发起，长期致力于流浪猫狗的救助、护理和领养安置。</p>
            <p>申请领养需年满21岁，具备稳定住所和基本经济能力，愿意接受回访并承诺科学喂养与定期免疫。</p>
            <p>为保障动物安全，领养前需做好窗户、阳台等防护措施，避免坠楼和走失风险。</p>
            <p>更多流程和细则，请先阅读“领养须知”，确认后再进入“领养申请”页面提交资料。</p>
            <el-button type="primary" size="large" class="notice-btn" @click="go('/adoption/rules')">
              进入领养须知
            </el-button>
          </div>
        </el-card>
      </div>
    </section>

    <section class="stats-section">
      <div class="container stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ summary.totalRescueCount }}</div>
          <div class="stat-label">累计救助</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ summary.adoptionSuccessCount }}</div>
          <div class="stat-label">成功领养</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ summary.activeAnimals }}</div>
          <div class="stat-label">当前待领养</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ summary.monthlyPublicBudget }}元</div>
          <div class="stat-label">月度公开支出</div>
        </div>
      </div>
    </section>

    <section class="adopt-section">
      <div class="container">
        <div class="section-header">
          <h2>待领养的猫猫</h2>
          <el-button link @click="go('/animals')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="animal-grid">
          <div class="animal-card" v-for="animal in catAnimals" :key="`cat-${animal.id}`" @click="toPet(animal)">
            <div class="animal-image-wrap">
              <img :src="getAssetUrl(animal.avatar)" :alt="animal.name" class="animal-image" />
            </div>
            <div class="animal-info">
              <h3>{{ animal.name }}</h3>
              <p>{{ animal.category === 'CAT' ? '猫咪' : '狗狗' }} · {{ animal.age }}岁</p>
              <span class="animal-tag">{{ animal.status === 1 ? '待领养' : '已领养' }}</span>
            </div>
          </div>
        </div>

        <div class="section-header second-line">
          <h2>待领养的狗狗</h2>
        </div>
        <div class="animal-grid">
          <div class="animal-card" v-for="animal in dogAnimals" :key="`dog-${animal.id}`" @click="toPet(animal)">
            <div class="animal-image-wrap">
              <img :src="getAssetUrl(animal.avatar)" :alt="animal.name" class="animal-image" />
            </div>
            <div class="animal-info">
              <h3>{{ animal.name }}</h3>
              <p>{{ animal.category === 'CAT' ? '猫咪' : '狗狗' }} · {{ animal.age }}岁</p>
              <span class="animal-tag">{{ animal.status === 1 ? '待领养' : '已领养' }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="knowledge-section">
      <div class="container">
        <div class="section-header">
          <h2>小常识</h2>
        </div>
        <div class="knowledge-grid">
          <el-card class="knowledge-card" shadow="never">
            <h3>小常识</h3>
            <p>1. 猫不用遛。遛狗是为了它的健康，顺便解决下大小便。猫不用遛，让它自己呆着就舒服了。</p>
            <p>2. 小猫不要喂牛奶。部分猫天生乳糖不耐，喂了牛奶会拉稀。</p>
            <p>3. 猫咪洗澡之后一定要彻底吹干。贴身的绒毛极不容易吹干，频率最多一个月一次。</p>
          </el-card>
          <el-card class="knowledge-card about-card" shadow="never">
            <h3>关于我们</h3>
            <p>我们领养之家最初由几位热爱小动物的朋友发起，长期专注流浪猫狗救助、寄养、医疗和领养安置。</p>
            <p>我们坚持科学救助和规范回访，持续推动“领养代替购买”，帮助更多毛孩子获得稳定、温暖的新家。</p>
          </el-card>
        </div>
      </div>
    </section>

    <section class="weekly-section" v-if="weeklyUpdates.length > 0">
      <div class="container">
        <div class="section-header">
          <h2>每周更新</h2>
        </div>
        <div class="weekly-strip">
          <div class="weekly-item" v-for="item in weeklyUpdates" :key="item.title">
            <h4>{{ item.title }}</h4>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="activities-section" v-if="featuredActivities.length > 0">
      <div class="container">
        <div class="section-header">
          <h2>爱心活动</h2>
          <el-button link @click="go('/activities')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
        </div>
        <div class="activity-list">
          <div class="activity-card" v-for="item in featuredActivities" :key="item.id" @click="router.push(`/activities/${item.id}`)">
            <img v-if="item.coverImage" :src="getAssetUrl(item.coverImage)" :alt="item.title" class="activity-cover" />
            <div class="activity-text">
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary || '点击查看活动详情' }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page {
  width: 100%;
  background: #f5f6f8;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 28px;
}

.hero-section {
  padding: 40px 0 26px;
}

.hero-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 20px;
}

.news-window,
.notice-window {
  border: 1px solid #e9edf2;
  border-radius: 14px;
}

.notice-window {
  background: #f1f3f5;
}

.block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.block-header h2 {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
  text-transform: uppercase;
  letter-spacing: 0.8px;
}

.news-slide {
  position: relative;
  height: 360px;
  cursor: pointer;
}

.news-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.news-window :deep(.el-card__body) {
  background: #fff;
}

.news-overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 18px 20px;
  color: #fff;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.02) 0%, rgba(0, 0, 0, 0.62) 100%);
}

.news-overlay h3 {
  margin: 0 0 6px;
  font-size: 22px;
}

.news-overlay p {
  margin: 0;
  font-size: 14px;
  opacity: 0.95;
}

.notice-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-content p {
  margin: 0;
  line-height: 1.9;
  color: #4b5563;
}

.notice-btn {
  margin-top: 12px;
  align-self: flex-start;
}

.adopt-section {
  padding: 22px 0 22px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  margin: 0;
  font-size: 30px;
  color: #1f2937;
}

.second-line {
  margin-top: 28px;
}

.animal-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
}

.animal-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e8edf3;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.08);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.animal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 32px rgba(17, 24, 39, 0.14);
}

.animal-image-wrap {
  height: 168px;
  overflow: hidden;
}

.animal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.animal-info {
  padding: 11px;
  background: #fff;
}

.animal-info h3 {
  margin: 0 0 4px;
  color: #1f2937;
  font-size: 16px;
}

.animal-info p {
  margin: 0 0 8px;
  color: #6b7280;
  font-size: 13px;
}

.animal-tag {
  display: inline-block;
  padding: 4px 10px;
  background: #f7ece7;
  color: var(--el-color-primary);
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.knowledge-section {
  padding: 30px 0 56px;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.knowledge-card {
  border: 1px solid #e8edf3;
  border-radius: 14px;
  background: #f1f3f5;
}

.knowledge-card h3 {
  margin: 0 0 10px;
  color: #1f2937;
  font-size: 18px;
}

.knowledge-card p {
  margin: 0 0 8px;
  color: #4b5563;
  line-height: 1.8;
}

.about-card p {
  margin-bottom: 10px;
}

.stats-section {
  padding: 0 0 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  text-align: center;
  padding: 24px 16px;
  background: #fff;
  border: 1px solid #e8edf3;
  border-radius: 0;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #5a3e2d;
}

.stat-label {
  font-size: 14px;
  color: #8b7a6b;
  margin-top: 6px;
}

.weekly-section {
  padding: 10px 0 40px;
}

.weekly-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.weekly-item {
  background: #fff;
  border: 1px solid #e8edf3;
  border-radius: 0;
  padding: 18px;
}

.weekly-item h4 {
  margin: 0 0 8px;
  color: #5a3e2d;
  font-size: 16px;
}

.weekly-item p {
  margin: 0;
  color: #6b7280;
  line-height: 1.6;
}

.activities-section {
  padding: 10px 0 56px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.activity-card {
  display: flex;
  background: #fff;
  border: 1px solid #e8edf3;
  border-radius: 0;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.activity-card:hover {
  box-shadow: 0 8px 24px rgba(17, 24, 39, 0.1);
}

.activity-cover {
  width: 180px;
  height: 120px;
  object-fit: cover;
  flex-shrink: 0;
}

.activity-text {
  padding: 14px 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.activity-text h4 {
  margin: 0 0 6px;
  font-size: 16px;
  color: #1f2937;
}

.activity-text p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 1024px) {
  .hero-grid {
    grid-template-columns: 1fr;
  }
  .animal-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 14px;
  }
  .animal-grid,
  .knowledge-grid {
    grid-template-columns: 1fr;
  }
  .section-header h2 {
    font-size: 24px;
  }
}
</style>
