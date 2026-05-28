<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  deleteComment,
  deletePost,
  getAdminPostList,
  setCommentStatus,
  setPostStatus,
  type Comment,
  type Post
} from '../api/community'

type AdminCommentRow = Comment & {
  postTitle: string
  postAuthor: string
  parentContent: string
}

const loading = ref(false)
const posts = ref<Post[]>([])
const postDetailVisible = ref(false)
const commentDetailVisible = ref(false)
const selectedPost = ref<Post | null>(null)
const selectedComment = ref<AdminCommentRow | null>(null)

const comments = computed<AdminCommentRow[]>(() => {
  const rows: AdminCommentRow[] = []
  posts.value.forEach((post) => {
    const commentMap = new Map((post.comments || []).map((comment) => [comment.id, comment]))
    ;(post.comments || []).forEach((comment) => {
      const parent = comment.parentId ? commentMap.get(comment.parentId) : null
      rows.push({
        ...comment,
        postTitle: post.title,
        postAuthor: displayName(post.nickname, post.username),
        parentContent: parent?.content || ''
      })
    })
  })
  return rows
})

function displayName(nickname?: string, username?: string) {
  const value = String(nickname || username || '').trim()
  return value || '匿名用户'
}

function getStatusText(status?: number) {
  return status === 1 ? '已通过' : '已隐藏'
}

function getStatusType(status?: number) {
  return status === 1 ? 'success' : 'info'
}

function formatTime(value?: string) {
  return value ? value.replace('T', ' ') : '-'
}

function getImageUrl(path?: string) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

function getImages(images?: string) {
  if (!images) return []
  try {
    const list = JSON.parse(images)
    return Array.isArray(list) ? list : []
  } catch {
    return []
  }
}

function previewText(value?: string, max = 36) {
  const text = String(value || '').trim()
  if (!text) return '-'
  return text.length > max ? `${text.slice(0, max)}...` : text
}

async function load() {
  loading.value = true
  try {
    const res = await getAdminPostList()
    if (res.code === 0) {
      posts.value = res.data
    } else {
      ElMessage.error(res.message || '获取社区审核数据失败')
    }
  } finally {
    loading.value = false
  }
}

function openPostDetail(post: Post) {
  selectedPost.value = post
  postDetailVisible.value = true
}

function openCommentDetail(comment: AdminCommentRow) {
  selectedComment.value = comment
  commentDetailVisible.value = true
}

async function updatePostStatus(id: number, status: number) {
  const res = await setPostStatus(id, status)
  if (res.code === 0) {
    ElMessage.success(status === 1 ? '帖子已通过' : '帖子已隐藏')
    await load()
  } else {
    ElMessage.error(res.message || '帖子状态更新失败')
  }
}

async function updateCommentStatus(id: number, status: number) {
  const res = await setCommentStatus(id, status)
  if (res.code === 0) {
    ElMessage.success(status === 1 ? '评论已通过' : '评论已隐藏')
    await load()
  } else {
    ElMessage.error(res.message || '评论状态更新失败')
  }
}

async function removePost(id: number) {
  const res = await deletePost(id)
  if (res.code === 0) {
    ElMessage.success('帖子已删除')
    postDetailVisible.value = false
    await load()
  } else {
    ElMessage.error(res.message || '帖子删除失败')
  }
}

async function removeComment(id: number) {
  const res = await deleteComment(id)
  if (res.code === 0) {
    ElMessage.success('评论已删除')
    commentDetailVisible.value = false
    await load()
  } else {
    ElMessage.error(res.message || '评论删除失败')
  }
}

onMounted(load)
</script>

<template>
  <el-card shadow="never" v-loading="loading" class="community-review-card">
    <template #header>
      <div class="header-bar">
        <span>社区审核（帖子 / 评论）</span>
        <el-button text type="primary" @click="load">刷新</el-button>
      </div>
    </template>

    <el-tabs>
      <el-tab-pane :label="`帖子审核（${posts.length}）`">
        <el-table :data="posts" stripe>
          <el-table-column prop="title" label="帖子标题" min-width="220" show-overflow-tooltip />
          <el-table-column label="作者" width="120">
            <template #default="{ row }">
              {{ displayName(row.nickname, row.username) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="正文摘要" min-width="280" show-overflow-tooltip>
            <template #default="{ row }">
              {{ previewText(row.content, 60) }}
            </template>
          </el-table-column>
          <el-table-column label="图片数" width="80">
            <template #default="{ row }">
              {{ getImages(row.images).length }}
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="170">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="290" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openPostDetail(row)">查看详情</el-button>
              <el-button
                size="small"
                type="primary"
                plain
                @click="updatePostStatus(row.id, row.status === 1 ? 0 : 1)"
              >
                {{ row.status === 1 ? '隐藏' : '通过' }}
              </el-button>
              <el-button size="small" type="danger" @click="removePost(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane :label="`评论审核（${comments.length}）`">
        <el-table :data="comments" stripe>
          <el-table-column prop="postTitle" label="所属帖子" min-width="200" show-overflow-tooltip />
          <el-table-column prop="username" label="评论人" width="120">
            <template #default="{ row }">
              {{ displayName(row.nickname, row.username) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="评论内容" min-width="260" show-overflow-tooltip>
            <template #default="{ row }">
              {{ previewText(row.content, 60) }}
            </template>
          </el-table-column>
          <el-table-column label="图片" width="80">
            <template #default="{ row }">
              {{ row.image ? '有' : '无' }}
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="170">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="290" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openCommentDetail(row)">查看详情</el-button>
              <el-button
                size="small"
                type="primary"
                plain
                @click="updateCommentStatus(row.id, row.status === 1 ? 0 : 1)"
              >
                {{ row.status === 1 ? '隐藏' : '通过' }}
              </el-button>
              <el-button size="small" type="danger" @click="removeComment(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <el-drawer v-model="postDetailVisible" size="760px" title="帖子详情">
    <div v-if="selectedPost" class="detail-panel">
      <div class="detail-actions">
        <el-tag :type="getStatusType(selectedPost.status)">{{ getStatusText(selectedPost.status) }}</el-tag>
        <div class="detail-action-buttons">
          <el-button
            type="primary"
            plain
            @click="updatePostStatus(selectedPost.id!, selectedPost.status === 1 ? 0 : 1)"
          >
            {{ selectedPost.status === 1 ? '隐藏帖子' : '通过帖子' }}
          </el-button>
          <el-button type="danger" @click="removePost(selectedPost.id!)">删除帖子</el-button>
        </div>
      </div>

      <el-descriptions :column="1" border>
        <el-descriptions-item label="帖子标题">{{ selectedPost.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ displayName(selectedPost.nickname, selectedPost.username) }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatTime(selectedPost.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(selectedPost.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ selectedPost.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="正文内容">
          <div class="multiline-text">{{ selectedPost.content || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="图片预览">
          <div v-if="getImages(selectedPost.images).length" class="image-preview-list">
            <el-image
              v-for="img in getImages(selectedPost.images)"
              :key="img"
              :src="getImageUrl(img)"
              :preview-src-list="getImages(selectedPost.images).map(getImageUrl)"
              fit="cover"
              class="detail-image"
            />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item :label="`评论明细（${selectedPost.comments?.length || 0}）`">
          <div v-if="selectedPost.comments?.length" class="comment-detail-list">
            <div v-for="comment in selectedPost.comments" :key="comment.id" class="comment-detail-item">
              <div class="comment-detail-head">
                <span>{{ displayName(comment.nickname, comment.username) }}</span>
                <el-tag size="small" :type="getStatusType(comment.status)">{{ getStatusText(comment.status) }}</el-tag>
              </div>
              <div class="comment-detail-meta">
                <span>时间：{{ formatTime(comment.createTime) }}</span>
                <span>父评论 ID：{{ comment.parentId ?? '-' }}</span>
              </div>
              <div class="multiline-text">{{ comment.content || '-' }}</div>
              <div v-if="comment.image" class="comment-image-line">
                <div class="image-url-item">{{ getImageUrl(comment.image) }}</div>
                <el-image :src="getImageUrl(comment.image)" :preview-src-list="[getImageUrl(comment.image)]" class="comment-preview-image" />
              </div>
            </div>
          </div>
          <span v-else>暂无评论</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </el-drawer>

  <el-drawer v-model="commentDetailVisible" size="640px" title="评论详情">
    <div v-if="selectedComment" class="detail-panel">
      <div class="detail-actions">
        <el-tag :type="getStatusType(selectedComment.status)">{{ getStatusText(selectedComment.status) }}</el-tag>
        <div class="detail-action-buttons">
          <el-button
            type="primary"
            plain
            @click="updateCommentStatus(selectedComment.id!, selectedComment.status === 1 ? 0 : 1)"
          >
            {{ selectedComment.status === 1 ? '隐藏评论' : '通过评论' }}
          </el-button>
          <el-button type="danger" @click="removeComment(selectedComment.id!)">删除评论</el-button>
        </div>
      </div>

      <el-descriptions :column="1" border>
        <el-descriptions-item label="所属帖子">{{ selectedComment.postTitle }}</el-descriptions-item>
        <el-descriptions-item label="帖子作者">{{ selectedComment.postAuthor }}</el-descriptions-item>
        <el-descriptions-item label="评论人">{{ displayName(selectedComment.nickname, selectedComment.username) }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatTime(selectedComment.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="父评论 ID">{{ selectedComment.parentId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="父评论内容">
          <div class="multiline-text">{{ selectedComment.parentContent || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="评论内容">
          <div class="multiline-text">{{ selectedComment.content || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="图片地址">
          {{ selectedComment.image ? getImageUrl(selectedComment.image) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="图片预览">
          <el-image
            v-if="selectedComment.image"
            :src="getImageUrl(selectedComment.image)"
            :preview-src-list="[getImageUrl(selectedComment.image)]"
            class="comment-preview-image"
          />
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </el-drawer>
</template>

<style scoped>
.community-review-card {
  border-radius: 16px;
}

.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.detail-action-buttons {
  display: flex;
  gap: 10px;
}

.multiline-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
}

.image-url-list,
.comment-detail-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-url-item {
  padding: 8px 10px;
  border-radius: 10px;
  background: #f7f8fa;
  color: #606266;
  line-height: 1.7;
  word-break: break-all;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-image {
  width: 160px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
}

.comment-detail-item {
  padding: 12px;
  border-radius: 12px;
  background: #fbf6f1;
}

.comment-detail-head,
.comment-detail-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.comment-detail-head {
  margin-bottom: 8px;
  font-weight: 600;
}

.comment-detail-meta {
  margin-bottom: 8px;
  color: #909399;
  font-size: 12px;
}

.comment-image-line {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.comment-preview-image {
  width: 140px;
  height: 140px;
  border-radius: 10px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .detail-actions,
  .comment-detail-head,
  .comment-detail-meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .detail-action-buttons {
    width: 100%;
    flex-wrap: wrap;
  }
}
</style>
