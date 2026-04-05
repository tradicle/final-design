<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { getPostList, createPost, createComment, type Post } from '../api/community'
import { uploadFile } from '../api/file'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { Plus, Location, Search } from '@element-plus/icons-vue'

interface LandmarkCandidate {
  title: string
  address: string
  lng: number
  lat: number
  distance: number
}

interface CommentDraft {
  content: string
  image: string
  parentId?: number
  replyToName?: string
}

const userStore = useUserStore()
const posts = ref<Post[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)

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
const commentDrafts = reactive<Record<number, CommentDraft>>({})

async function load() {
  loading.value = true
  try {
    const res = await getPostList()
    if (res.code === 0) {
      posts.value = res.data
    }
  } finally {
    loading.value = false
  }
}

async function customUpload(options: UploadRequestOptions) {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 0) {
      form.images.push(res.data)
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('上传异常')
  }
}

async function handlePost() {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
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

function ensureCommentDraft(postId: number): CommentDraft {
  if (!commentDrafts[postId]) {
    commentDrafts[postId] = { content: '', image: '', parentId: undefined, replyToName: '' }
  }
  return commentDrafts[postId]
}

function startReply(post: Post, username: string, parentId?: number) {
  if (!post.id) return
  const draft = ensureCommentDraft(post.id)
  draft.parentId = parentId
  draft.replyToName = username
}

function clearReply(postId: number) {
  const draft = ensureCommentDraft(postId)
  draft.parentId = undefined
  draft.replyToName = ''
}

async function uploadCommentImage(postId: number, options: UploadRequestOptions) {
  const draft = ensureCommentDraft(postId)
  try {
    const res = await uploadFile(options.file)
    if (res.code === 0) {
      draft.image = res.data
    } else {
      ElMessage.error(res.message || '评论图片上传失败')
    }
  } catch (e) {
    ElMessage.error('评论图片上传异常')
  }
}

function commentUploadRequest(postId: number) {
  return (options: UploadRequestOptions) => uploadCommentImage(postId, options)
}

function topLevelComments(post: Post) {
  return (post.comments || []).filter((item) => !item.parentId)
}

function childComments(post: Post, parentId: number | undefined) {
  if (!parentId) return []
  return (post.comments || []).filter((item) => item.parentId === parentId)
}

function formatCommentTime(time?: string) {
  if (!time) return ''
  return time.replace('T', ' ')
}

function commentAvatar(avatar?: string) {
  if (!avatar) return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  return avatar
}

async function handleComment(post: Post) {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    return
  }
  if (!post.id) return
  const draft = ensureCommentDraft(post.id)
  if (!draft.content && !draft.image) {
    ElMessage.warning('请填写评论内容或上传图片')
    return
  }
  
  try {
    const res = await createComment({
      postId: post.id,
      userId: userStore.user.id,
      content: draft.content,
      image: draft.image,
      parentId: draft.parentId
    })
    if (res.code === 0) {
      ElMessage.success('评论成功')
      draft.content = ''
      draft.image = ''
      draft.parentId = undefined
      draft.replyToName = ''
      load()
    } else {
      ElMessage.error(res.message || '评论失败')
    }
  } catch (e) {
    ElMessage.error('评论异常')
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
}

function getImageUrl(path: string) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

function getImages(json: string | undefined): string[] {
  if (!json) return []
  try {
    return JSON.parse(json)
  } catch {
    return []
  }
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
    ElMessage.warning('请先登录')
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

    <div v-else class="post-list">
      <el-card v-for="post in posts" :key="post.id" class="post-card">
        <div class="post-header">
          <img :src="post.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="avatar" />
          <div class="user-info">
            <span class="username">{{ post.username || '匿名用户' }}</span>
            <span class="time">{{ post.createTime?.replace('T', ' ') }}</span>
          </div>
        </div>
        
        <div class="post-content">
          <h3>{{ post.title }}</h3>
          <p>{{ post.content }}</p>
          
          <div class="post-images" v-if="getImages(post.images).length">
            <el-image 
              v-for="(img, idx) in getImages(post.images)" 
              :key="idx" 
              :src="getImageUrl(img)" 
              :preview-src-list="getImages(post.images).map(getImageUrl)"
              class="post-img"
            />
          </div>
          
          <div class="post-location" v-if="post.location">
            <el-icon><Location /></el-icon> {{ post.location }}
          </div>
        </div>

        <div class="post-footer">
          <div class="comments-section">
            <div class="comment-list" v-if="post.comments && post.comments.length">
              <div class="comment-item" v-for="comment in topLevelComments(post)" :key="comment.id">
                <div class="comment-meta">
                  <img :src="commentAvatar(comment.avatar)" class="comment-avatar" />
                  <div class="comment-meta-text">
                    <span class="comment-user">{{ comment.username || '匿名用户' }}</span>
                    <span class="comment-time">{{ formatCommentTime(comment.createTime) }}</span>
                  </div>
                </div>
                <div class="comment-main">
                  <span class="comment-content">{{ comment.content }}</span>
                </div>
                <el-image v-if="comment.image" :src="getImageUrl(comment.image)" class="comment-image" :preview-src-list="[getImageUrl(comment.image)]" />
                <div class="comment-actions">
                  <el-button text size="small" @click="startReply(post, comment.username || '用户', comment.id)">回复</el-button>
                </div>
                <div class="comment-replies" v-if="childComments(post, comment.id).length">
                  <div class="comment-item child" v-for="child in childComments(post, comment.id)" :key="child.id">
                    <div class="comment-meta">
                      <img :src="commentAvatar(child.avatar)" class="comment-avatar" />
                      <div class="comment-meta-text">
                        <span class="comment-user">{{ child.username || '匿名用户' }}</span>
                        <span class="comment-time">{{ formatCommentTime(child.createTime) }}</span>
                      </div>
                    </div>
                    <div class="comment-main">
                      <span class="comment-content">{{ child.content }}</span>
                    </div>
                    <el-image v-if="child.image" :src="getImageUrl(child.image)" class="comment-image" :preview-src-list="[getImageUrl(child.image)]" />
                  </div>
                </div>
              </div>
            </div>
            
            <div class="reply-box">
              <div class="reply-target" v-if="post.id && ensureCommentDraft(post.id).replyToName">
                正在回复 {{ ensureCommentDraft(post.id).replyToName }}
                <el-button text size="small" @click="clearReply(post.id)">取消</el-button>
              </div>
              <el-input 
                v-if="post.id"
                v-model="ensureCommentDraft(post.id).content" 
                placeholder="写下你的评论..." 
                size="small"
                type="textarea"
                :rows="2"
              />
              <div class="reply-tools" v-if="post.id">
                <el-upload action="" :show-file-list="false" :http-request="commentUploadRequest(post.id)">
                  <el-button size="small">上传评论图片</el-button>
                </el-upload>
                <el-image
                  v-if="ensureCommentDraft(post.id).image"
                  :src="getImageUrl(ensureCommentDraft(post.id).image)"
                  class="comment-upload-preview"
                  :preview-src-list="[getImageUrl(ensureCommentDraft(post.id).image)]"
                />
                <el-button type="primary" size="small" @click="handleComment(post)">发送</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>
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
          <el-upload
            action=""
            list-type="picture-card"
            :http-request="customUpload"
            :show-file-list="true"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="位置">
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

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #999;
}

.post-content h3 {
  margin: 0 0 10px;
  font-size: 18px;
}

.post-content p {
  color: #333;
  margin-bottom: 10px;
  line-height: 1.6;
}

.post-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.post-img {
  width: 100px;
  height: 100px;
  border-radius: 4px;
}

.post-location {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 10px;
}

.comments-section {
  background: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
}

.comment-list {
  margin-bottom: 10px;
}

.comment-item {
  font-size: 13px;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ebeef5;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.comment-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}
.comment-meta-text {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.comment-time {
  color: #909399;
  font-size: 12px;
}
.comment-main {
  display: flex;
  align-items: baseline;
  gap: 6px;
  flex-wrap: wrap;
}

.comment-user {
  color: var(--el-color-primary);
  font-weight: bold;
}
.comment-content {
  color: #303133;
}
.comment-actions {
  margin-top: 4px;
}
.comment-replies {
  margin-top: 6px;
  margin-left: 18px;
  padding-left: 10px;
  border-left: 2px solid #e9edf3;
}
.comment-item.child {
  border-bottom: none;
  padding-bottom: 0;
}
.comment-image {
  width: 84px;
  height: 84px;
  border-radius: 4px;
  margin-top: 6px;
}
.reply-target {
  margin-bottom: 8px;
  color: #606266;
  font-size: 12px;
}
.reply-tools {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.comment-upload-preview {
  width: 48px;
  height: 48px;
  border-radius: 4px;
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
@media (max-width: 768px) {
  .location-suggest {
    width: 100%;
    max-width: 100%;
  }
}
</style>
