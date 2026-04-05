<script setup lang="ts">
import { onMounted, reactive, ref, watch, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { createAnimal, getAnimalList, type Animal } from '../api/animal'
import { uploadFile } from '../api/file'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { Plus, Location, Search } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const animals = ref<Animal[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)

const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

const filters = reactive({
  category: 'ALL',
  sex: 'ALL',
  bodySize: 'ALL'
})

const form = reactive({
  animalNo: '',
  name: '',
  category: 'DOG',
  sex: 'MALE',
  bodySize: 'MEDIUM',
  age: 0,
  avatar: '',
  isSterilized: false,
  activityScope: '',
  description: '',
  detailContent: '',
  latitude: 39.915,
  longitude: 116.404
})

let map: any = null
let uploadMarker: any = null
const uploadMapId = 'animal-upload-map-container'
const mapSearchKeyword = ref('')
const mapAddress = ref('')

function getImageUrl(path: string) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

async function load() {
  loading.value = true
  try {
    const res = await getAnimalList({
      category: filters.category === 'ALL' ? undefined : filters.category,
      sex: filters.sex === 'ALL' ? undefined : filters.sex,
      bodySize: filters.bodySize === 'ALL' ? undefined : filters.bodySize
    })
    if (res.code === 0) {
      animals.value = res.data
    } else {
      ElMessage.error(res.message || '获取动物列表失败')
    }
  } catch (e) {
    ElMessage.error('无法连接后端')
  } finally {
    loading.value = false
  }
}

watch(() => ({ ...filters }), () => {
  load()
})

function handleAdopt(animal: Animal) {
  // If adopted, disable
  if (animal.status !== 1) return
  if (!userStore.user) {
    ElMessage.warning('请先登录后申请领养')
    router.push('/login')
    return
  }
  // User logged in, redirect to rules first
  ElMessage.info('领养前请先阅读领养须知')
  router.push({ path: '/adoption/rules', query: { from: 'apply', animalId: animal.id } })
}

function handleCardClick(animal: Animal) {
  // Prefer using animalNo for URL if available, else id
  const id = animal.animalNo || String(animal.id)
  router.push(`/pet/${id}`)
}

async function customUpload(options: UploadRequestOptions) {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 0) {
      form.avatar = res.data
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('上传异常')
  }
}

function reverseGeocode(lng: number, lat: number) {
  if (!map || typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  const geocoder = new BMap.Geocoder()
  geocoder.getLocation(new BMap.Point(lng, lat), (result: any) => {
    mapAddress.value = result?.address || ''
    if (!form.activityScope) {
      form.activityScope = mapAddress.value
    }
  })
}

function placeUploadMarker(lng: number, lat: number, center = true) {
  if (!map || typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  const point = new BMap.Point(lng, lat)
  if (uploadMarker) {
    map.removeOverlay(uploadMarker)
  }
  uploadMarker = new BMap.Marker(point)
  uploadMarker.enableDragging()
  uploadMarker.addEventListener('dragend', (event: any) => {
    form.longitude = Number(event.point.lng.toFixed(6))
    form.latitude = Number(event.point.lat.toFixed(6))
    reverseGeocode(form.longitude, form.latitude)
  })
  map.addOverlay(uploadMarker)
  if (center) {
    map.panTo(point)
  }
  form.longitude = Number(lng.toFixed(6))
  form.latitude = Number(lat.toFixed(6))
  reverseGeocode(form.longitude, form.latitude)
}

function locateCurrentPosition() {
  if (!navigator.geolocation) {
    ElMessage.warning('当前浏览器不支持定位')
    return
  }
  navigator.geolocation.getCurrentPosition(
    (position) => {
      placeUploadMarker(position.coords.longitude, position.coords.latitude, true)
      ElMessage.success('定位成功')
    },
    () => {
      ElMessage.error('定位失败，请检查定位权限')
    },
    { enableHighAccuracy: true, timeout: 10000 }
  )
}

function searchAddressOnMap() {
  if (!map || !mapSearchKeyword.value.trim() || typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  const localSearch = new BMap.LocalSearch(map, {
    onSearchComplete: (results: any) => {
      if (!results || results.getCurrentNumPois() === 0) {
        ElMessage.warning('未找到该地址')
        return
      }
      const poi = results.getPoi(0)
      if (!poi?.point) {
        ElMessage.warning('未找到该地址')
        return
      }
      placeUploadMarker(poi.point.lng, poi.point.lat, true)
      mapAddress.value = poi.title || mapSearchKeyword.value
    }
  })
  localSearch.search(mapSearchKeyword.value.trim())
}

function initUploadMap() {
  nextTick(() => {
    if (typeof (window as any).BMap === 'undefined') {
      ElMessage.error('百度地图加载失败')
      return
    }
    const BMap = (window as any).BMap
    map = new BMap.Map(uploadMapId)
    const point = new BMap.Point(form.longitude, form.latitude)
    map.centerAndZoom(point, 15)
    map.enableScrollWheelZoom(true)
    placeUploadMarker(form.longitude, form.latitude, true)
    map.addEventListener('click', (event: any) => {
      placeUploadMarker(event.point.lng, event.point.lat, false)
    })
  })
}

function openUploadDialog() {
  dialogVisible.value = true
  setTimeout(() => {
    initUploadMap()
  }, 200)
}

function resetForm() {
  form.animalNo = ''
  form.name = ''
  form.category = 'DOG'
  form.sex = 'MALE'
  form.bodySize = 'MEDIUM'
  form.age = 0
  form.avatar = ''
  form.isSterilized = false
  form.activityScope = ''
  form.description = ''
  form.detailContent = ''
  form.latitude = 39.915
  form.longitude = 116.404
  mapSearchKeyword.value = ''
  mapAddress.value = ''
}

async function handleSubmit() {
  if (!form.name || !form.avatar) {
    ElMessage.warning('请填写名字并上传头像')
    return
  }
  submitLoading.value = true
  try {
    const res = await createAnimal({
      ...form,
      status: 1
    })
    if (res.code === 0) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      resetForm()
      load()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加异常')
  } finally {
    submitLoading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="header-actions">
      <h2>领养中心</h2>
      <div class="header-buttons">
        <el-button type="primary" plain @click="load" :loading="loading">刷新列表</el-button>
        <el-button v-if="isAdmin" type="primary" @click="openUploadDialog">上传宠物信息</el-button>
      </div>
    </div>

    <div class="filters">
      <el-select v-model="filters.category" placeholder="种类" style="width: 150px">
        <el-option label="全部种类" value="ALL" />
        <el-option label="猫" value="CAT" />
        <el-option label="狗" value="DOG" />
      </el-select>
      <el-select v-model="filters.sex" placeholder="性别" style="width: 150px">
        <el-option label="全部性别" value="ALL" />
        <el-option label="男" value="MALE" />
        <el-option label="女" value="FEMALE" />
      </el-select>
      <el-select v-model="filters.bodySize" placeholder="体型" style="width: 150px">
        <el-option label="全部体型" value="ALL" />
        <el-option label="小型" value="SMALL" />
        <el-option label="中型" value="MEDIUM" />
        <el-option label="大型" value="LARGE" />
      </el-select>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <el-empty v-else-if="animals.length === 0" description="暂无待领养动物" />

    <div v-else class="animal-grid">
      <el-card
        v-for="animal in animals"
        :key="animal.id"
        class="animal-card"
        :body-style="{ padding: '0px' }"
        @click="handleCardClick(animal)"
      >
        <div class="image-container">
          <img :src="getImageUrl(animal.avatar)" class="image" :alt="animal.name"/>
          <div class="status-tag" :class="{ adopted: animal.status === 0 }">
            {{ animal.status === 1 ? '待领养' : '已领养' }}
          </div>
        </div>
        <div class="content">
          <div class="title-row">
            <span class="name">{{ animal.name }}</span>
            <span class="age">{{ animal.age }} 岁</span>
          </div>
          <div class="tags">
            <el-tag size="small">{{ animal.category === 'CAT' ? '猫' : '狗' }}</el-tag>
            <el-tag size="small" :type="animal.sex === 'FEMALE' ? 'danger' : 'primary'">
              {{ animal.sex === 'FEMALE' ? '女' : '男' }}
            </el-tag>
            <el-tag size="small" type="info">
              {{ animal.bodySize === 'SMALL' ? '小型' : animal.bodySize === 'LARGE' ? '大型' : '中型' }}
            </el-tag>
          </div>
          <p class="description">{{ animal.description || '暂无描述' }}</p>
          <div class="actions">
            <el-button type="primary" block @click.stop="handleAdopt(animal)" :disabled="animal.status === 0">
              {{ animal.status === 1 ? '申请领养' : '已由好心人领养' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="上传宠物信息" width="800px" top="5vh">
      <el-form label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名字">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编号(选填)">
              <el-input v-model="form.animalNo" placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="种类">
              <el-radio-group v-model="form.category">
                <el-radio label="DOG">狗</el-radio>
                <el-radio label="CAT">猫</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.sex">
                <el-radio label="MALE">男</el-radio>
                <el-radio label="FEMALE">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体型">
              <el-radio-group v-model="form.bodySize">
                <el-radio label="SMALL">小</el-radio>
                <el-radio label="MEDIUM">中</el-radio>
                <el-radio label="LARGE">大</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="form.age" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="绝育">
              <el-switch v-model="form.isSterilized" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动范围">
              <el-input v-model="form.activityScope" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="简短描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="展示在卡片上的简短介绍" />
        </el-form-item>
        
        <el-form-item label="详细介绍">
          <el-input 
            v-model="form.detailContent" 
            type="textarea" 
            :rows="6" 
            placeholder="支持HTML格式的详细介绍，可包含多段文字" 
          />
          <div class="tip">提示：可在详细介绍中输入 &lt;p&gt;段落&lt;/p&gt; 或 &lt;img src="..." /&gt; 来排版</div>
        </el-form-item>

        <el-form-item label="封面头像">
          <el-upload action="" :show-file-list="false" :http-request="customUpload">
            <img v-if="form.avatar" :src="getImageUrl(form.avatar)" class="upload-avatar" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="发现位置">
          <div class="map-actions">
            <el-button type="primary" plain @click="locateCurrentPosition">
              <el-icon><Location /></el-icon>
              一键定位
            </el-button>
            <el-input v-model="mapSearchKeyword" placeholder="输入地址关键字，如“人民公园”" @keyup.enter="searchAddressOnMap">
              <template #append>
                <el-button @click="searchAddressOnMap">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          <el-input v-model="mapAddress" placeholder="定位地址" />
          <div :id="uploadMapId" class="map-container"></div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-buttons {
  display: flex;
  gap: 10px;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}
.animal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}
.animal-card {
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
  cursor: pointer;
}
.animal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}
.image-container {
  height: 200px;
  overflow: hidden;
  position: relative;
  background-color: #f5f7fa;
}
.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  background-color: rgba(64, 158, 255, 0.9);
  color: white;
  font-size: 12px;
  font-weight: bold;
}
.status-tag.adopted {
  background-color: rgba(144, 147, 153, 0.9);
}
.content {
  padding: 16px;
}
.title-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 12px;
}
.name {
  font-size: 18px;
  font-weight: bold;
}
.age {
  font-size: 14px;
  color: #909399;
}
.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.description {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 42px;
}
.upload-avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
}
.upload-icon {
  font-size: 24px;
  width: 100px;
  height: 100px;
  border: 1px dashed var(--el-border-color);
  display: flex;
  align-items: center;
  justify-content: center;
}
.map-actions {
  display: grid;
  grid-template-columns: 130px 1fr;
  gap: 8px;
  margin-bottom: 8px;
}
.map-container {
  width: 100%;
  height: 220px;
  border: 1px solid #dcdfe6;
  margin-top: 8px;
}
.tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
