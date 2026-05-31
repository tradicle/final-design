<script setup lang="ts">
import { onMounted, onBeforeUnmount, reactive, ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAnimal, deleteAnimal, getAdminAnimalPage, type Animal, updateAnimal } from '../api/animal'
import { uploadFile } from '../api/file'
import type { UploadRequestOptions } from 'element-plus'
import { Location, Search } from '@element-plus/icons-vue'
import { getAssetUrl } from '../utils/assets'
import RichTextEditor from '../components/RichTextEditor.vue'

interface LandmarkCandidate {
  title: string
  address: string
  lng: number
  lat: number
  distance: number
}

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const rows = ref<Animal[]>([])
const total = ref(0)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const mapStatus = ref<{ type: 'info' | 'success' | 'warning'; text: string } | null>(null)
const mapSearchKeyword = ref('')
const landmarkCandidates = ref<LandmarkCandidate[]>([])
const showCandidatePanel = ref(false)
const locationInputRef = ref<any>(null)
const mapId = 'animal-location-map'
let map: any = null
let mapMarker: any = null
let candidateSearchTimer: number | null = null
const query = reactive({
  keyword: '',
  category: 'ALL',
  status: -1,
  page: 1,
  pageSize: 36,
})

const form = reactive<Partial<Animal>>({
  animalNo: '',
  name: '',
  category: 'DOG',
  sex: 'MALE',
  age: 1,
  bodySize: 'MEDIUM',
  avatar: '',
  description: '',
  detailContent: '',
  activityScope: '深圳市',
  location: '',
  latitude: 22.543096,
  longitude: 114.057865,
  isSterilized: false,
  status: 1
})

function resetForm() {
  editingId.value = null
  form.animalNo = ''
  form.name = ''
  form.category = 'DOG'
  form.sex = 'MALE'
  form.age = 1
  form.bodySize = 'MEDIUM'
  form.avatar = ''
  form.description = ''
  form.detailContent = ''
  form.activityScope = '深圳市'
  form.location = ''
  form.latitude = 22.543096
  form.longitude = 114.057865
  mapSearchKeyword.value = ''
  landmarkCandidates.value = []
  showCandidatePanel.value = false
  mapStatus.value = null
  form.isSterilized = false
  form.status = 1
  form.updateTime = undefined
}


function escapeHtml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
}

function normalizeDetailContent(content?: string) {
  const value = (content || '').trim()
  if (!value) return ''
  if (/<[a-z][\s\S]*>/i.test(value)) return value
  return value
    .split(/\n{2,}/)
    .map((block) => `<p>${escapeHtml(block).replace(/\n/g, '<br>')}</p>`)
    .join('')
}

function fillForm(row: Animal) {
  editingId.value = row.id
  form.animalNo = row.animalNo
  form.name = row.name
  form.category = row.category
  form.sex = row.sex
  form.age = row.age
  form.bodySize = row.bodySize
  form.avatar = row.avatar
  form.description = row.description
  form.detailContent = row.detailContent || ''
  form.activityScope = row.activityScope
  form.location = row.location
  form.latitude = row.latitude
  form.longitude = row.longitude
  mapSearchKeyword.value = row.location || row.activityScope || ''
  landmarkCandidates.value = []
  showCandidatePanel.value = false
  form.isSterilized = row.isSterilized
  form.status = row.status
  form.updateTime = row.updateTime
}

async function load() {
  loading.value = true
  try {
    const res = await getAdminAnimalPage({
      keyword: query.keyword || undefined,
      category: query.category === 'ALL' ? undefined : query.category,
      status: query.status,
      page: query.page,
      pageSize: query.pageSize,
    })
    if (res.code === 0) {
      rows.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  load()
}

function openCreate() {
  resetForm()
  drawerVisible.value = true
  setTimeout(initMap, 200)
}

function openEdit(row: Animal) {
  fillForm(row)
  drawerVisible.value = true
  setTimeout(initMap, 200)
}

async function save() {
  if (!form.name || !form.category) {
    ElMessage.warning('请先填写必填项')
    return
  }
  saving.value = true
  try {
    const payload = {
      ...form,
      detailContent: normalizeDetailContent(form.detailContent)
    }
    const res = editingId.value
      ? await updateAnimal(editingId.value, payload)
      : await createAnimal(payload)
    if (res.code === 0) {
      ElMessage.success(editingId.value ? '更新成功' : '新增成功')
      drawerVisible.value = false
      load()
    }
  } finally {
    saving.value = false
  }
}

async function remove(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该宠物档案吗？', '删除确认', { type: 'warning' })
    const res = await deleteAnimal(id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      load()
    }
  } catch {
    // cancel
  }
}

async function uploadAvatar(options: UploadRequestOptions) {
  uploading.value = true
  try {
    const res = await uploadFile(options.file, 'animals')
    if (res.code === 0) {
      form.avatar = res.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } finally {
    uploading.value = false
  }
}

function setMapStatus(type: 'info' | 'success' | 'warning', text: string) {
  mapStatus.value = { type, text }
}

function buildPreciseLocation(point: any, result: any) {
  if (!map || typeof (window as any).BMap === 'undefined') {
    return { locationText: result?.address || '', nearestPoint: null, nearestDistance: Number.MAX_SAFE_INTEGER, candidates: [] as LandmarkCandidate[] }
  }
  const pois = Array.isArray(result?.surroundingPois) ? result.surroundingPois : []
  const candidates: LandmarkCandidate[] = []
  let nearestPoi: any = null
  let nearestDistance = Number.MAX_SAFE_INTEGER
  for (const poi of pois) {
    if (!poi?.point) continue
    const distance = map.getDistance(point, poi.point)
    candidates.push({
      title: poi.title || '未命名地标',
      address: poi.address || '',
      lng: Number(poi.point.lng.toFixed(6)),
      lat: Number(poi.point.lat.toFixed(6)),
      distance
    })
    if (distance < nearestDistance) {
      nearestDistance = distance
      nearestPoi = poi
    }
  }
  candidates.sort((a, b) => a.distance - b.distance)
  const topCandidates = candidates.slice(0, 5)
  if (nearestPoi) {
    const parts = [nearestPoi.title, nearestPoi.address].filter(Boolean)
    return {
      locationText: parts.join(' · '),
      nearestPoint: nearestPoi.point,
      nearestDistance,
      candidates: topCandidates
    }
  }
  return {
    locationText: result?.address || '',
    nearestPoint: null,
    nearestDistance: Number.MAX_SAFE_INTEGER,
    candidates: topCandidates
  }
}

function updateAddressByPoint(lng: number, lat: number, lockToNearest: boolean) {
  if (!map || typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  const geocoder = new BMap.Geocoder()
  const sourcePoint = new BMap.Point(lng, lat)
  geocoder.getLocation(sourcePoint, (result: any) => {
    const { locationText, nearestPoint, nearestDistance, candidates } = buildPreciseLocation(sourcePoint, result)
    landmarkCandidates.value = candidates
    form.location = locationText || form.location
    if (lockToNearest && nearestPoint && nearestDistance <= 120) {
      form.longitude = Number(nearestPoint.lng.toFixed(6))
      form.latitude = Number(nearestPoint.lat.toFixed(6))
      if (mapMarker) mapMarker.setPosition(nearestPoint)
      map?.panTo?.(nearestPoint)
    }
  }, { poiRadius: 1000, numPois: 20 })
}

function placeMapMarker(lng: number, lat: number, center = true) {
  if (!map || typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  const point = new BMap.Point(lng, lat)
  if (mapMarker) {
    map.removeOverlay(mapMarker)
  }
  mapMarker = new BMap.Marker(point)
  mapMarker.enableDragging()
  mapMarker.addEventListener('dragend', (event: any) => {
    form.longitude = Number(event.point.lng.toFixed(6))
    form.latitude = Number(event.point.lat.toFixed(6))
    updateAddressByPoint(form.longitude, form.latitude, false)
  })
  map.addOverlay(mapMarker)
  if (center) {
    map.panTo(point)
  }
  form.longitude = Number(lng.toFixed(6))
  form.latitude = Number(lat.toFixed(6))
  updateAddressByPoint(form.longitude, form.latitude, true)
}

function convertGpsToBaidu(lng: number, lat: number): Promise<{ lng: number; lat: number }> {
  return new Promise((resolve) => {
    if (typeof (window as any).BMap === 'undefined') {
      resolve({ lng, lat })
      return
    }
    const BMap = (window as any).BMap
    const convertor = new BMap.Convertor()
    const point = new BMap.Point(lng, lat)
    convertor.translate([point], 1, 5, (result: any) => {
      if (result?.status === 0 && result.points?.[0]) {
        resolve({ lng: result.points[0].lng, lat: result.points[0].lat })
        return
      }
      resolve({ lng, lat })
    })
  })
}

function locateCurrentPosition() {
  if (typeof (window as any).BMap === 'undefined') {
    ElMessage.error('百度地图加载失败')
    setMapStatus('warning', '百度地图加载失败，请稍后重试。')
    return
  }
  const BMap = (window as any).BMap
  const geolocation = new BMap.Geolocation()
  geolocation.enableSDKLocation()
  geolocation.getCurrentPosition(
    function (this: any, result: any) {
      if (this.getStatus() === 0 && result?.point) {
        placeMapMarker(result.point.lng, result.point.lat, true)
        ElMessage.success('定位成功')
        setMapStatus('success', '定位成功，已自动匹配附近地标，可继续拖动微调。')
        return
      }
      if (!navigator.geolocation) {
        ElMessage.error('定位失败，请检查浏览器定位权限')
        setMapStatus('warning', '定位失败，请检查浏览器定位权限。')
        return
      }
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const converted = await convertGpsToBaidu(position.coords.longitude, position.coords.latitude)
          placeMapMarker(converted.lng, converted.lat, true)
          ElMessage.success('定位成功')
          setMapStatus('success', '定位成功，已自动匹配附近地标。')
        },
        () => {
          ElMessage.error('定位失败，请检查浏览器定位权限')
          setMapStatus('warning', '定位失败，请检查浏览器定位权限。')
        },
        { enableHighAccuracy: true, timeout: 12000, maximumAge: 0 }
      )
    },
    { enableHighAccuracy: true }
  )
}

function searchAddressOnMap() {
  if (!map || !mapSearchKeyword.value.trim() || typeof (window as any).BMap === 'undefined') return
  const BMap = (window as any).BMap
  const localSearch = new BMap.LocalSearch(map, {
    onSearchComplete: (results: any) => {
      if (!results || results.getCurrentNumPois() === 0) {
        ElMessage.warning('未找到该地址')
        setMapStatus('warning', '未找到匹配地址，请尝试更具体的关键字。')
        return
      }
      const poi = results.getPoi(0)
      if (!poi?.point) {
        ElMessage.warning('未找到该地址')
        setMapStatus('warning', '未找到匹配地址，请尝试更具体的关键字。')
        return
      }
      placeMapMarker(poi.point.lng, poi.point.lat, true)
      ElMessage.success('地址定位成功')
      setMapStatus('success', '地址定位成功，已匹配附近地标。')
    }
  })
  localSearch.search(mapSearchKeyword.value.trim())
}

function searchCandidatesByKeyword(keyword: string) {
  if (!map || typeof (window as any).BMap === 'undefined') return
  const value = keyword.trim()
  if (!value) return
  const BMap = (window as any).BMap
  const localSearch = new BMap.LocalSearch(map, {
    onSearchComplete: (results: any) => {
      if (!showCandidatePanel.value) return
      if (!results || results.getCurrentNumPois() === 0) {
        landmarkCandidates.value = []
        return
      }
      const total = Math.min(results.getCurrentNumPois(), 5)
      const nextCandidates: LandmarkCandidate[] = []
      for (let i = 0; i < total; i++) {
        const poi = results.getPoi(i)
        if (!poi?.point) continue
        const centerPoint = map.getCenter()
        nextCandidates.push({
          title: poi.title || '未命名地标',
          address: poi.address || '',
          lng: Number(poi.point.lng.toFixed(6)),
          lat: Number(poi.point.lat.toFixed(6)),
          distance: map.getDistance(centerPoint, poi.point)
        })
      }
      landmarkCandidates.value = nextCandidates
    }
  })
  localSearch.search(value)
}

function onLocationFocus() {
  showCandidatePanel.value = true
  searchCandidatesByKeyword(form.location?.trim() || mapSearchKeyword.value.trim())
}

function onLocationInput() {
  if (!showCandidatePanel.value) return
  if (candidateSearchTimer) {
    window.clearTimeout(candidateSearchTimer)
  }
  candidateSearchTimer = window.setTimeout(() => {
    searchCandidatesByKeyword(form.location || '')
  }, 250)
}

function onLocationBlur() {
  window.setTimeout(() => {
    showCandidatePanel.value = false
  }, 120)
}

function selectLandmarkCandidate(candidate: LandmarkCandidate) {
  form.location = [candidate.title, candidate.address].filter(Boolean).join(' · ')
  placeMapMarker(candidate.lng, candidate.lat, true)
  showCandidatePanel.value = false
  locationInputRef.value?.blur?.()
}

function onDocumentPointerDown(event: MouseEvent) {
  const target = event.target as Node | null
  if (!target) return
  const inputWrap = document.querySelector('.animal-location-suggest')
  if (inputWrap && !inputWrap.contains(target)) {
    showCandidatePanel.value = false
  }
}

function initMap() {
  nextTick(() => {
    if (!drawerVisible.value || typeof (window as any).BMap === 'undefined') return
    const BMap = (window as any).BMap
    const container = document.getElementById(mapId)
    if (!container) return
    container.innerHTML = ''
    map = new BMap.Map(mapId)
    const lng = Number(form.longitude ?? 114.057865)
    const lat = Number(form.latitude ?? 22.543096)
    const point = new BMap.Point(lng, lat)
    map.centerAndZoom(point, 15)
    map.enableScrollWheelZoom(true)
    placeMapMarker(lng, lat, true)
    map.addEventListener('click', function (e: any) {
      placeMapMarker(e.point.lng, e.point.lat, false)
    })
  })
}

onMounted(() => {
  load()
  document.addEventListener('pointerdown', onDocumentPointerDown)
})
onBeforeUnmount(() => {
  document.removeEventListener('pointerdown', onDocumentPointerDown)
})
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <template #header>
      <div class="header-bar">
        <span>宠物档案管理</span>
        <el-button type="primary" @click="openCreate">新增宠物卡片</el-button>
      </div>
    </template>

    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索名字/编号/活动范围" clearable @keyup.enter="search" />
      <el-select v-model="query.category" @change="search">
        <el-option label="全部分类" value="ALL" />
        <el-option label="狗" value="DOG" />
        <el-option label="猫" value="CAT" />
      </el-select>
      <el-select v-model="query.status" @change="search">
        <el-option label="全部状态" :value="-1" />
        <el-option label="待领养" :value="1" />
        <el-option label="已领养" :value="0" />
      </el-select>
      <el-button @click="search">查询</el-button>
    </div>

    <div class="animal-grid">
      <div class="animal-card" v-for="row in rows" :key="row.id" @click="openEdit(row)">
        <div class="animal-image-wrap">
          <img :src="getAssetUrl(row.avatar)" :alt="row.name" class="animal-image" />
          <span class="status-tag">{{ row.status === 1 ? '待领养' : '已领养' }}</span>
        </div>
        <div class="animal-info">
          <h3>{{ row.name }}</h3>
          <p>{{ row.category === 'CAT' ? '猫咪' : '狗狗' }} · {{ row.age }}岁</p>
          <div class="tags">
            <el-tag size="small" :type="row.sex === 'FEMALE' ? 'danger' : 'primary'">
              {{ row.sex === 'FEMALE' ? '女' : '男' }}
            </el-tag>
            <el-tag size="small" type="info">
              {{ row.bodySize === 'SMALL' ? '小型' : row.bodySize === 'LARGE' ? '大型' : '中型' }}
            </el-tag>
          </div>
          <div class="actions">
            <el-button size="small" @click.stop="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="remove(row.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        background
        layout="total, prev, pager, next"
        :total="total"
        @current-change="load"
      />
    </div>
  </el-card>

  <el-dialog v-model="drawerVisible" :title="editingId ? '编辑宠物档案' : '新增宠物档案'" width="980px" class="edit-dialog">
    <div class="form-grid dialog-form">
      <el-input v-model="form.animalNo" placeholder="档案编号" />
      <el-input v-model="form.name" placeholder="名字" />
      <el-select v-model="form.category"><el-option label="狗" value="DOG" /><el-option label="猫" value="CAT" /></el-select>
      <el-select v-model="form.sex"><el-option label="男孩" value="MALE" /><el-option label="女孩" value="FEMALE" /></el-select>
      <el-input-number v-model="form.age" :min="0" :max="30" />
      <el-select v-model="form.bodySize"><el-option label="小型" value="SMALL" /><el-option label="中型" value="MEDIUM" /><el-option label="大型" value="LARGE" /></el-select>
      <el-input v-model="form.activityScope" placeholder="活动范围，如 深圳市南山区" />
      <el-select v-model="form.status"><el-option label="待领养" :value="1" /><el-option label="已领养" :value="0" /></el-select>
      <el-select v-model="form.isSterilized"><el-option label="已绝育" :value="true" /><el-option label="未绝育" :value="false" /></el-select>
      <div class="location-map-field">
        <div class="map-actions">
          <el-button type="primary" plain @click="locateCurrentPosition">
            <el-icon><Location /></el-icon>
            一键定位
          </el-button>
          <el-input v-model="mapSearchKeyword" placeholder="输入地址关键字，如“深圳湾公园”" @keyup.enter="searchAddressOnMap">
            <template #append>
              <el-button @click="searchAddressOnMap">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="animal-location-suggest">
          <el-input
            ref="locationInputRef"
            v-model="form.location"
            placeholder="搜索或手动编辑发现位置"
            @focus="onLocationFocus"
            @input="onLocationInput"
            @blur="onLocationBlur"
          />
          <div v-if="mapStatus" class="map-status" :class="`is-${mapStatus.type}`">{{ mapStatus.text }}</div>
          <div v-if="showCandidatePanel && landmarkCandidates.length" class="candidate-dropdown">
            <button
              v-for="(item, index) in landmarkCandidates"
              :key="`${item.lng}-${item.lat}-${index}`"
              type="button"
              class="candidate-item"
              @mousedown.prevent
              @click="selectLandmarkCandidate(item)"
            >
              <span class="candidate-main">{{ item.title }}</span>
              <span class="candidate-sub">{{ item.address || '附近地点' }}</span>
            </button>
          </div>
        </div>
        <div :id="mapId" class="map-container"></div>
      </div>
      <div class="upload-row">
        <el-upload action="" :show-file-list="false" :http-request="uploadAvatar">
          <el-button :loading="uploading">上传图片</el-button>
        </el-upload>
        <div class="upload-preview-box">
          <el-image
            v-if="form.avatar"
            :src="getAssetUrl(form.avatar)"
            class="upload-preview-image"
            fit="cover"
            :preview-src-list="[getAssetUrl(form.avatar)]"
          />
          <div v-else class="upload-preview-empty">上传后在此预览图片</div>
        </div>
      </div>
      <el-input v-model="form.description" type="textarea" :rows="3" placeholder="简短描述" />
      <div class="editor-field">
        <label class="field-label">详情内容</label>
        <RichTextEditor :model-value="form.detailContent || ''" @update:model-value="form.detailContent = $event" upload-folder="animals" />
      </div>
      <div v-if="editingId && form.updateTime" class="update-time-row">
        <span class="update-time-label">最后更新：</span>
        <span>{{ form.updateTime?.replace('T', ' ') }}</span>
      </div>
    </div>

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">{{ editingId ? '保存修改' : '创建卡片' }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.toolbar {
  display: grid;
  grid-template-columns: 1.6fr 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 14px;
}
.animal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
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
  position: relative;
}
.animal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.status-tag {
  position: absolute;
  right: 8px;
  top: 8px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 12px;
  border-radius: 999px;
  padding: 3px 8px;
}
.animal-info {
  padding: 11px;
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
  line-height: 1.6;
}
.actions {
  display: flex;
  gap: 8px;
}
.tags {
  display: flex;
  gap: 6px;
  margin-bottom: 6px;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.dialog-form {
  padding-top: 6px;
}
.form-grid :deep(.el-textarea) {
  grid-column: 1 / -1;
}
.edit-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}
.edit-dialog :deep(.el-dialog__footer) {
  padding-top: 8px;
}
.editor-field {
  grid-column: 1 / -1;
}
.field-label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}
.update-time-row {
  grid-column: 1 / -1;
  font-size: 13px;
  color: #909399;
  padding-top: 4px;
}
.update-time-label {
  margin-right: 4px;
}
.upload-row {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: 90px 1fr;
  gap: 8px;
}
.upload-preview-box {
  min-height: 120px;
  background: #faf7f3;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
}
.upload-preview-image {
  width: 180px;
  height: 104px;
  display: block;
}
.upload-preview-empty {
  color: #9b8d82;
  font-size: 13px;
}
.location-map-field {
  grid-column: 1 / -1;
}
.map-container {
  width: 100%;
  height: 220px;
  border: 1px solid #d8dce3;
  margin-top: 8px;
}
.map-actions {
  display: grid;
  grid-template-columns: 130px 1fr;
  gap: 8px;
  margin-bottom: 8px;
}
.animal-location-suggest {
  position: relative;
  width: 100%;
  max-width: 100%;
}
.candidate-dropdown {
  position: absolute;
  left: 0;
  right: 0;
  top: calc(100% + 6px);
  z-index: 20;
  background: #fff;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  max-height: 260px;
  overflow: auto;
}
.candidate-item {
  width: 100%;
  border: none;
  background: transparent;
  text-align: left;
  padding: 10px 12px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.candidate-item:hover {
  background: #fff1e6;
}
.candidate-main {
  color: #303133;
  font-size: 14px;
}
.candidate-sub {
  color: #909399;
  font-size: 12px;
}
.map-status {
  margin-top: 6px;
  font-size: 12px;
  border-radius: 8px;
  padding: 6px 10px;
}
.map-status.is-info {
  background: #fff3e8;
  color: #7f5e49;
}
.map-status.is-success {
  background: #edf9f1;
  color: #3f8a57;
}
.map-status.is-warning {
  background: #fff1f0;
  color: #b4534d;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
@media (max-width: 1200px) {
  .animal-grid {
    grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  }
}
@media (max-width: 900px) {
  .toolbar {
    grid-template-columns: 1fr;
  }
  .animal-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .form-grid {
    grid-template-columns: 1fr;
  }
  .map-actions {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 640px) {
  .animal-grid {
    grid-template-columns: 1fr;
  }
}
</style>
