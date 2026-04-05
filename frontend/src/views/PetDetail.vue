<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnimalByNo, type Animal } from '../api/animal'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import { Male, Female } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const animal = ref<Animal | null>(null)
const loading = ref(false)

// Map related
let map: any = null
const detailMapId = 'pet-detail-map'

function getImageUrl(path: string) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

async function load() {
  const animalNo = route.params.id as string
  if (!animalNo) return
  loading.value = true
  try {
    const res = await getAnimalByNo(animalNo)
    if (res.code === 0 && res.data) {
      animal.value = res.data
      // Init map after data loaded
      setTimeout(initDetailMap, 200)
    } else {
      ElMessage.error('未找到该宠物档案')
      router.replace('/animals')
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function initDetailMap() {
  if (!animal.value) return
  if (typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  map = new BMap.Map(detailMapId)
  
  const lat = animal.value.latitude || 39.915
  const lng = animal.value.longitude || 116.404
  const point = new BMap.Point(lng, lat)
  
  map.centerAndZoom(point, 14)
  map.enableScrollWheelZoom(true)
  
  const marker = new BMap.Marker(point)
  map.addOverlay(marker)
  const label = new BMap.Label(animal.value.name, { offset: new BMap.Size(20, -10) })
  marker.setLabel(label)
  
  // Add a circle for range
  const circle = new BMap.Circle(point, 500, {
    strokeColor: "var(--el-color-primary)",
    strokeWeight: 2,
    strokeOpacity: 0.5,
    fillColor: "var(--el-color-primary)",
    fillOpacity: 0.1
  })
  map.addOverlay(circle)
}

function handleApply() {
  if (!animal.value) return
  if (!userStore.user) {
    ElMessage.warning('请先登录后申请领养')
    router.push('/login')
    return
  }
  // User logged in, redirect to rules first
  ElMessage.info('领养前请先阅读领养须知')
  router.push({ path: '/adoption/rules', query: { from: 'apply', animalId: animal.value.id } })
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else-if="animal" class="detail-container">
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/animals' }">领养</el-breadcrumb-item>
          <el-breadcrumb-item>宠物详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- Top Section: Image & Basic Info -->
      <div class="top-section">
        <div class="left-image">
          <el-image 
            :src="getImageUrl(animal.avatar)" 
            class="main-avatar" 
            :preview-src-list="[getImageUrl(animal.avatar)]"
            fit="cover"
          />
        </div>
        <div class="right-info">
          <div class="info-header">
            <h1 class="name">{{ animal.name }}</h1>
            <span class="no">编号：{{ animal.animalNo }}</span>
          </div>
          
          <div class="tags-row">
            <el-tag :type="animal.status === 1 ? 'success' : 'info'" effect="dark">
              {{ animal.status === 1 ? '待领养' : '已领养' }}
            </el-tag>
            <el-tag effect="plain" type="warning">{{ animal.category === 'CAT' ? '猫' : '狗' }}</el-tag>
            <el-tag effect="plain">{{ animal.age }}岁</el-tag>
          </div>

          <div class="props-grid">
            <div class="prop-item">
              <span class="label">性别</span>
              <span class="value">
                <el-icon v-if="animal.sex === 'MALE'" color="#409eff"><Male /></el-icon>
                <el-icon v-else-if="animal.sex === 'FEMALE'" color="#f56c6c"><Female /></el-icon>
                {{ animal.sex === 'MALE' ? '男孩' : (animal.sex === 'FEMALE' ? '女孩' : '未知') }}
              </span>
            </div>
            <div class="prop-item">
              <span class="label">体型</span>
              <span class="value">
                {{ animal.bodySize === 'SMALL' ? '小型' : (animal.bodySize === 'LARGE' ? '大型' : '中型') }}
              </span>
            </div>
            <div class="prop-item">
              <span class="label">绝育情况</span>
              <span class="value">{{ animal.isSterilized ? '已绝育' : '未绝育' }}</span>
            </div>
            <div class="prop-item">
              <span class="label">活动范围</span>
              <span class="value">{{ animal.activityScope }}</span>
            </div>
            <div class="prop-item full-width">
              <span class="label">更新时间</span>
              <span class="value">{{ animal.updateTime?.replace('T', ' ') }}</span>
            </div>
          </div>

          <div class="actions">
            <el-button type="primary" size="large" round class="action-btn" @click="handleApply" :disabled="animal.status !== 1">
              {{ animal.status === 1 ? '申请领养' : '已被领养' }}
            </el-button>
            <el-button size="large" round class="action-btn">咨询与联系</el-button>
          </div>
        </div>
      </div>

      <!-- Detail Content -->
      <div class="content-section">
        <div class="section-title">
          <h2>宠物故事</h2>
        </div>
        
        <!-- Render detail content (Rich Text) or fallback to description -->
        <div class="rich-content" v-if="animal.detailContent" v-html="animal.detailContent"></div>
        <div class="rich-content" v-else>
          <p>{{ animal.description }}</p>
        </div>
      </div>

      <!-- Location Map -->
      <div class="map-section">
        <div class="section-title">
          <h2>发现位置</h2>
        </div>
        <div :id="detailMapId" class="map-container"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.detail-container {
  max-width: 1100px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 20px;
}

.top-section {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.left-image {
  width: 50%;
  max-width: 500px;
  height: 400px;
}

.main-avatar {
  width: 100%;
  height: 100%;
}

.right-info {
  flex: 1;
  padding: 30px 40px;
  display: flex;
  flex-direction: column;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.name {
  font-size: 32px;
  color: #333;
  margin: 0;
}

.no {
  color: #999;
  font-size: 14px;
}

.tags-row {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
}

.props-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.prop-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.prop-item .label {
  font-size: 12px;
  color: #999;
}

.prop-item .value {
  font-size: 16px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.actions {
  margin-top: auto;
  display: flex;
  gap: 20px;
}

.action-btn {
  flex: 1;
}

.content-section, .map-section {
  background: #fff;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.section-title {
  margin-bottom: 20px;
  border-left: 4px solid var(--el-color-primary);
  padding-left: 15px;
}

.section-title h2 {
  font-size: 20px;
  margin: 0;
  color: #333;
}

.rich-content {
  color: #555;
  line-height: 1.8;
  font-size: 16px;
}

.rich-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 15px 0;
}

.rich-content :deep(p) {
  margin-bottom: 15px;
}

.map-container {
  width: 100%;
  height: 350px;
  border-radius: 8px;
  border: 1px solid #eee;
}

@media (max-width: 768px) {
  .top-section {
    flex-direction: column;
  }
  .left-image {
    width: 100%;
    max-width: 100%;
    height: 300px;
  }
  .right-info {
    padding: 20px;
  }
}
</style>
