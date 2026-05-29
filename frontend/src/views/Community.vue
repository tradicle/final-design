<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList, createPost, createComment, type Post } from '../api/community'
import { uploadFile } from '../api/file'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { Plus, Location, Search, ChatDotRound } from '@element-plus/icons-vue'
import { getAssetUrl } from '../utils/assets'
import { relativeTime } from '../utils/time'

const router = useRouter()

interface LandmarkCandidate {
  title: string
  address: string
  lng: number
  lat: number
  distance: number
}

const userStore = useUserStore()
const posts = ref<Post[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)

const expandedPosts = ref(new Set<number>())
const showAllReplies = ref(new Set<number>())
const replyDrafts = reactive<Record<number, { content: string; image: string }>>({})
const dialogLayout = ref<'4:3' | '3:4'>('4:3')
const replyLayouts = reactive<Record<number, '4:3' | '3:4'>>({})
const postLayouts = reactive<Record<number, '4:3' | '3:4'>>({})

let map: any = null
let mapMarker: any = null
const mapId = 'community-map-container'
const mapSearchKeyword = ref('')
const landmarkCandidates = ref<LandmarkCandidate[]>([])
const showCandidatePanel = ref(false)
let candidateSearchTimer: number | null = null
const locationInputRef = ref<any>(null)

const form = reactive({
  title: '',
  content: '',
  images: [] as string[],
  location: '',
  latitude: 39.915,
  longitude: 116.404
})

function getImageDimensions(file: File): Promise<{ width: number; height: number }> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => resolve({ width: img.naturalWidth, height: img.naturalHeight })
      img.onerror = reject
      img.src = e.target?.result as string
    }
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

async function load() {
  loading.value = true
  try {
    const res = await getPostList()
    if (res.code === 0) {
      posts.value = res.data
      for (const post of posts.value) {
        if (!post.id) continue
        if (!(post.id in postLayouts)) {
          postLayouts[post.id] = '4:3'
        }
        if (!(post.id in replyLayouts)) {
          replyLayouts[post.id] = '4:3'
        }
      }
    }
  } finally {
    loading.value = false
  }
}

async function customUpload(options: UploadRequestOptions) {
  if (form.images.length >= 3) {
    ElMessage.warning('最多上传3张图片')
    return
  }
  try {
    const file = options.file as File
    if (form.images.length === 0) {
      const dims = await getImageDimensions(file)
      dialogLayout.value = dims.width >= dims.height ? '4:3' : '3:4'
    }
    const res = await uploadFile(file)
    if (res.code === 0) {
      form.images.push(res.data)
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('上传异常')
  }
}

function removePostImage(idx: number) {
  form.images.splice(idx, 1)
  if (form.images.length === 0) {
    dialogLayout.value = '4:3'
  }
}

async function handlePost() {
  if (!userStore.user) {
    router.push('/login?redirect=/community')
    return
  }
  if (!form.title || !form.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  submitLoading.value = true
  try {
    const res = await createPost({
      userId: userStore.user.id,
      title: form.title,
      content: form.content,
      images: JSON.stringify(form.images),
      location: form.location,
      latitude: form.latitude,
      longitude: form.longitude
    })
    if (res.code === 0) {
      ElMessage.success('发布成功')
      dialogVisible.value = false
      load()
      resetForm()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布异常')
  } finally {
    submitLoading.value = false
  }
}

function ensureReplyDraft(postId: number) {
  if (!replyDrafts[postId]) {
    replyDrafts[postId] = { content: '', image: '' }
  }
  return replyDrafts[postId]
}

async function uploadReplyImage(postId: number, options: UploadRequestOptions) {
  const draft = ensureReplyDraft(postId)
  try {
    const file = options.file as File
    const dims = await getImageDimensions(file)
    replyLayouts[postId] = dims.width >= dims.height ? '4:3' : '3:4'
    const res = await uploadFile(file)
    if (res.code === 0) {
      draft.image = res.data
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('上传异常')
  }
}

function replyUploadRequest(postId: number) {
  return (options: UploadRequestOptions) => uploadReplyImage(postId, options)
}

function sortedReplies(post: Post) {
  return (post.comments || []).slice().sort((a, b) =>
    new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime()
  )
}

function replyCount(post: Post) {
  return (post.comments || []).length
}

function visibleReplies(post: Post) {
  const replies = sortedReplies(post)
  if (!post.id) return replies
  if (showAllReplies.value.has(post.id)) return replies
  return replies.slice(0, 2)
}

function hasMoreReplies(post: Post) {
  return post.id && sortedReplies(post).length > 2 && !showAllReplies.value.has(post.id)
}

function showAll(post: Post) {
  if (post.id) showAllReplies.value.add(post.id)
}

function toggleExpand(postId: number) {
  if (expandedPosts.value.has(postId)) {
    expandedPosts.value.delete(postId)
    showAllReplies.value.delete(postId)
    delete replyDrafts[postId]
  } else {
    expandedPosts.value.add(postId)
  }
}

async function handleReply(post: Post) {
  if (!userStore.user) {
    router.push('/login?redirect=/community')
    return
  }
  if (!post.id) return
  const draft = ensureReplyDraft(post.id)
  if (!draft.content && !draft.image) {
    ElMessage.warning('请填写回复内容或上传图片')
    return
  }
  try {
    const res = await createComment({
      postId: post.id,
      userId: userStore.user.id,
      content: draft.content,
      image: draft.image,
      parentId: undefined
    })
    if (res.code === 0) {
      ElMessage.success('回复成功')
      draft.content = ''
      draft.image = ''
      if (post.id) delete replyLayouts[post.id]
      load()
    } else {
      ElMessage.error(res.message || '回复失败')
    }
  } catch (e) {
    ElMessage.error('回复异常')
  }
}

function resetForm() {
  form.title = ''
  form.content = ''
  form.images = []
  form.location = ''
  form.latitude = 39.915
  form.longitude = 116.404
  mapSearchKeyword.value = ''
  landmarkCandidates.value = []
  showCandidatePanel.value = false
  dialogLayout.value = '4:3'
}

function getImages(json: string | undefined): string[] {
  if (!json) return []
  try {
    return JSON.parse(json)
  } catch {
    return []
  }
}

function onPostImgLoad(post: Post, event: Event) {
  if (!post.id) return
  if (post.id in postLayouts) return
  const img = event.target as HTMLImageElement
  if (img.naturalWidth && img.naturalHeight) {
    postLayouts[post.id] = img.naturalWidth >= img.naturalHeight ? '4:3' : '3:4'
  }
}

const dialogPreviewStyle = {
  '4:3': { width: '160px', height: '120px' },
  '3:4': { width: '120px', height: '160px' }
}

function postImgStyle(post: Post) {
  const layout = (post.id && postLayouts[post.id]) || '4:3'
  return layout === '4:3'
    ? { width: '160px', height: '120px' }
    : { width: '120px', height: '160px' }
}

function replyImgStyle(postId: number) {
  const layout = replyLayouts[postId] || '4:3'
  return layout === '4:3'
    ? { width: '160px', height: '120px' }
    : { width: '120px', height: '160px' }
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
      if (mapMarker) {
        mapMarker.setPosition(nearestPoint)
      }
      if (map) {
        map.panTo(nearestPoint)
      }
    }
  }, { poiRadius: 1000, numPois: 20 })
}

function selectLandmarkCandidate(candidate: LandmarkCandidate) {
  form.location = [candidate.title, candidate.address].filter(Boolean).join(' · ')
  placeMapMarker(candidate.lng, candidate.lat, true)
  showCandidatePanel.value = false
  locationInputRef.value?.blur?.()
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

function locateCurrentPosition() {
  if (typeof (window as any).BMap === 'undefined') {
    ElMessage.error('百度地图加载失败')
    return
  }
  const BMap = (window as any).BMap
  const geolocation = new BMap.Geolocation()
  geolocation.enableSDKLocation()
  geolocation.getCurrentPosition(
    function (this: any, result: any) {
      if (this.getStatus() === 0 && result?.point) {
        placeMapMarker(result.point.lng, result.point.lat, true)
        updateAddressByPoint(result.point.lng, result.point.lat, true)
        ElMessage.success('定位成功')
        return
      }
      if (!navigator.geolocation) {
        ElMessage.error('定位失败，请检查浏览器定位权限')
        return
      }
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const converted = await convertGpsToBaidu(position.coords.longitude, position.coords.latitude)
          placeMapMarker(converted.lng, converted.lat, true)
          updateAddressByPoint(converted.lng, converted.lat, true)
          ElMessage.success('定位成功')
        },
        () => {
          ElMessage.error('定位失败，请检查浏览器定位权限')
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
        return
      }
      const poi = results.getPoi(0)
      if (!poi?.point) {
        ElMessage.warning('未找到该地址')
        return
      }
      const centerPoint = map.getCenter()
      const distance = map.getDistance(centerPoint, poi.point)
      placeMapMarker(poi.point.lng, poi.point.lat, true)
      updateAddressByPoint(poi.point.lng, poi.point.lat, true)
      if (distance > 100) {
        ElMessage.warning(`已定位到最近结果，偏差约 ${Math.round(distance)} 米，可拖动标记微调`)
      } else {
        ElMessage.success('地址定位成功')
      }
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
  searchCandidatesByKeyword(form.location.trim() || mapSearchKeyword.value.trim())
}

function onLocationInput() {
  if (!showCandidatePanel.value) return
  if (candidateSearchTimer) {
    window.clearTimeout(candidateSearchTimer)
  }
  candidateSearchTimer = window.setTimeout(() => {
    searchCandidatesByKeyword(form.location)
  }, 250)
}

function onLocationBlur() {
  window.setTimeout(() => {
    showCandidatePanel.value = false
  }, 120)
}

function onDocumentPointerDown(event: MouseEvent) {
  const target = event.target as Node | null
  if (!target) return
  const inputWrap = document.querySelector('.location-suggest')
  if (inputWrap && !inputWrap.contains(target)) {
    showCandidatePanel.value = false
  }
}

function initMap() {
  nextTick(() => {
    if (typeof (window as any).BMap === 'undefined') return
    const BMap = (window as any).BMap
    map = new BMap.Map(mapId)
    const point = new BMap.Point(form.longitude, form.latitude)
    map.centerAndZoom(point, 15)
    map.enableScrollWheelZoom(true)
    placeMapMarker(form.longitude, form.latitude, true)
    map.addEventListener('click', function(e: any) {
      placeMapMarker(e.point.lng, e.point.lat, false)
    })
  })
}

function openDialog() {
  if (!userStore.user) {
    router.push('/login?redirect=/community')
    return
  }
  dialogVisible.value = true
  setTimeout(initMap, 200)
}

onMounted(load)
onMounted(() => {
  document.addEventListener('pointerdown', onDocumentPointerDown)
})
onBeforeUnmount(() => {
  document.removeEventListener('pointerdown', onDocumentPointerDown)
})
</script>

<template>
  <div class="page">
    <div class="header">
      <h2>社区交流</h2>
      <el-button type="primary" @click="openDialog">发布帖子</el-button>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else class="post-list-container">
      <template v-for="(post, pIdx) in posts" :key="post.id">
        <div class="post-item">
          <div class="post-header">
            <img :src="getAssetUrl(post.avatar) || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="post-avatar" />
            <span class="post-username">{{ post.nickname || post.username || '匿名用户' }}</span>
          </div>

          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-body">{{ post.content }}</p>

          <div class="post-images" v-if="getImages(post.images).length">
            <img
              v-for="(img, idx) in getImages(post.images)"
              :key="idx"
              :src="getAssetUrl(img)"
              :style="postImgStyle(post)"
              class="post-img"
              @load="(e: Event) => onPostImgLoad(post, e)"
            />
          </div>

          <div class="post-location" v-if="post.location">
            <el-icon><Location /></el-icon> {{ post.location }}
          </div>

          <div class="post-time">{{ relativeTime(post.createTime) }}</div>

          <div class="reply-toggle" @click="post.id && toggleExpand(post.id)">
            <el-icon><ChatDotRound /></el-icon>
            <span>回复 ({{ replyCount(post) }})</span>
          </div>

          <div class="reply-section" v-if="post.id && expandedPosts.has(post.id)">
            <div class="reply-list" v-if="sortedReplies(post).length">
              <div class="reply-item" v-for="reply in visibleReplies(post)" :key="reply.id">
                <img
                  :src="getAssetUrl(reply.avatar) || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
                  class="reply-avatar"
                />
                <div class="reply-content-area">
                  <div class="reply-meta">
                    <span class="reply-username">{{ reply.nickname || reply.username || '匿名用户' }}</span>
                    <span class="reply-time">{{ relativeTime(reply.createTime) }}</span>
                  </div>
                  <div class="reply-text">{{ reply.content }}</div>
                  <img
                    v-if="reply.image"
                    :src="getAssetUrl(reply.image)"
                    :style="replyImgStyle(post.id)"
                    class="reply-img"
                  />
                </div>
              </div>
            </div>

            <div class="show-more-wrap" v-if="hasMoreReplies(post)">
              <el-button text type="primary" @click="showAll(post)">
                共{{ replyCount(post) }}条回复，点击查看
              </el-button>
            </div>

            <div class="reply-input-area">
              <div class="reply-input-row">
                <el-input
                  v-model="ensureReplyDraft(post.id).content"
                  placeholder="写下你的回复..."
                  type="textarea"
                  :rows="2"
                  class="reply-textarea"
                />
                <el-upload
                  action=""
                  :show-file-list="false"
                  :http-request="replyUploadRequest(post.id)"
                  class="reply-upload-btn"
                >
                  <el-button size="small">上传图片</el-button>
                </el-upload>
                <el-button type="primary" size="small" @click="handleReply(post)">发送</el-button>
              </div>
              <img
                v-if="ensureReplyDraft(post.id).image"
                :src="getAssetUrl(ensureReplyDraft(post.id).image)"
                :style="replyImgStyle(post.id)"
                class="reply-upload-preview"
              />
            </div>
          </div>
        </div>
        <div v-if="pIdx < posts.length - 1" class="post-divider"></div>
      </template>
    </div>

    <el-dialog v-model="dialogVisible" title="发布帖子" width="600px">
      <el-form label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="分享你的故事..." />
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-area">
            <div
              v-for="(img, idx) in form.images"
              :key="idx"
              class="upload-preview"
              :style="dialogPreviewStyle[dialogLayout]"
            >
              <img :src="getAssetUrl(img)" class="upload-preview-img" />
              <span class="upload-preview-remove" @click="removePostImage(idx)">&times;</span>
            </div>
            <el-upload
              v-if="form.images.length < 3"
              action=""
              :show-file-list="false"
              :http-request="customUpload"
            >
              <div class="upload-trigger" :style="dialogPreviewStyle[dialogLayout]">
                <el-icon><Plus /></el-icon>
                <span>上传图片</span>
              </div>
            </el-upload>
          </div>
          <div class="upload-hint">最多3张 ({{ form.images.length }}/3)</div>
        </el-form-item>
        <el-form-item label="位置">
          <div class="map-actions">
            <el-button type="primary" plain @click="locateCurrentPosition">
              <el-icon><Location /></el-icon>
              一键定位
            </el-button>
            <el-input v-model="mapSearchKeyword" placeholder="输入地址关键字，如&ldquo;人民公园&rdquo;" @keyup.enter="searchAddressOnMap">
              <template #append>
                <el-button @click="searchAddressOnMap">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          <div class="location-suggest">
            <el-input
              ref="locationInputRef"
              v-model="form.location"
              placeholder="可手动编辑位置信息"
              @focus="onLocationFocus"
              @input="onLocationInput"
              @blur="onLocationBlur"
            />
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
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePost" :loading="submitLoading">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.post-list-container {
  background: #FFF8E7;
  border-radius: 8px;
  padding: 20px;
}

.post-item {
  padding: 8px 0;
}

.post-divider {
  height: 1px;
  background: #e0d8c4;
  margin: 12px 0;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.post-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.post-username {
  font-weight: bold;
  font-size: 14px;
}

.post-title {
  margin: 0 0 8px;
  font-size: 18px;
}

.post-body {
  color: #333;
  margin-bottom: 8px;
  line-height: 1.6;
}

.post-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.post-img {
  border-radius: 4px;
  object-fit: cover;
}

.post-location {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.post-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.reply-toggle {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #606266;
  font-size: 13px;
  user-select: none;
  padding: 4px 0;
  transition: color 0.2s;
}
.reply-toggle:hover {
  color: var(--el-color-primary);
}

.reply-section {
  margin-left: 2em;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #e9edf3;
}

.reply-item {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.reply-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.reply-content-area {
  flex: 1;
  min-width: 0;
}

.reply-meta {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.reply-username {
  color: #303133;
  font-weight: bold;
  font-size: 13px;
}

.reply-time {
  font-size: 11px;
  color: #999;
}

.reply-text {
  color: #303133;
  font-size: 13px;
  margin-top: 2px;
  line-height: 1.5;
  word-break: break-word;
}

.reply-img {
  border-radius: 4px;
  object-fit: cover;
  margin-top: 6px;
}

.show-more-wrap {
  margin-bottom: 12px;
}

.reply-input-area {
  margin-top: 8px;
}

.reply-input-row {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.reply-textarea {
  flex: 1;
}

.reply-upload-btn {
  flex-shrink: 0;
}

.reply-upload-preview {
  border-radius: 4px;
  object-fit: cover;
  margin-top: 8px;
}

.map-container {
  width: 100%;
  height: 200px;
  border: 1px solid #ccc;
  margin-top: 5px;
}

.map-actions {
  display: grid;
  grid-template-columns: 130px 1fr;
  gap: 8px;
  margin-bottom: 8px;
}

.location-suggest {
  position: relative;
  width: 200%;
  max-width: 1000px;
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
  background: #f5f7fa;
}

.candidate-main {
  color: #303133;
  font-size: 14px;
}

.candidate-sub {
  color: #909399;
  font-size: 12px;
}

.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: flex-start;
}

.upload-preview {
  position: relative;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.upload-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-preview-remove {
  position: absolute;
  top: 2px;
  right: 4px;
  cursor: pointer;
  color: #fff;
  background: rgba(0, 0, 0, 0.45);
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  line-height: 1;
}

.upload-trigger {
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  color: #909399;
  font-size: 12px;
  background: #fafafa;
  transition: border-color 0.2s;
}

.upload-trigger:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.upload-hint {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}

@media (max-width: 768px) {
  .location-suggest {
    width: 100%;
    max-width: 100%;
  }
}
</style>
