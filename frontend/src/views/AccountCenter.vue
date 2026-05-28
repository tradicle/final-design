<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { changePassword, getDefaultAvatars, getProfile, updateProfile, type DefaultAvatar, type User } from '../api/user'
import { uploadFile } from '../api/file'
import { useUserStore } from '../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const passwordLoading = ref(false)
const avatarDialogVisible = ref(false)

const profile = ref<User | null>(null)
const profileForm = reactive({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const defaultAvatarOptions = ref<DefaultAvatar[]>([])

const currentUsername = computed(() => profile.value?.username || userStore.user?.username || profileForm.username || '--')
const displayAvatar = computed(() => profileForm.avatar || profile.value?.avatar || '')
const displayName = computed(() => profileForm.nickname || profileForm.username || '我的账号')
const isPasswordPage = computed(() => route.query.mode === 'password')

function getErrorMessage(error: any, fallback: string) {
  return error?.response?.data?.message || error?.message || fallback
}

function getImageUrl(path: string) {
  if (!path) return ''
  if (path.startsWith('http') || path.startsWith('data:image')) return path
  return `http://localhost:8080${path}`
}

function syncProfile(user: User) {
  profile.value = user
  profileForm.username = user.username || ''
  profileForm.nickname = user.nickname || user.username || ''
  profileForm.email = user.email || ''
  profileForm.avatar = user.avatar || ''
}

function openAvatarDialog() {
  avatarDialogVisible.value = true
}

function selectDefaultAvatar(avatar: DefaultAvatar) {
  profileForm.avatar = avatar.imageData
  avatarDialogVisible.value = false
  ElMessage.success({
    message: '已选择头像，点击保存生效',
    duration: 1000,
  })
}

async function loadProfile() {
  loading.value = true
  try {
    const res = await getProfile()
    if (res.code === 0) {
      syncProfile(res.data)
      userStore.setUser(res.data)
    } else {
      ElMessage.error(res.message || '获取账号信息失败')
    }
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '获取账号信息失败'))
  } finally {
    loading.value = false
  }
}

async function loadDefaultAvatars() {
  try {
    const res = await getDefaultAvatars()
    if (res.code === 0) {
      defaultAvatarOptions.value = res.data
    } else {
      ElMessage.error(res.message || '获取默认头像失败')
    }
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '获取默认头像失败'))
  }
}

async function customUpload(options: UploadRequestOptions) {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 0) {
      profileForm.avatar = res.data
      avatarDialogVisible.value = false
      ElMessage.success({
        message: '头像已上传，点击保存生效',
        duration: 1000,
      })
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '头像上传失败'))
  }
}

async function saveProfile(successMessage = '保存成功') {
  if (!profileForm.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }

  submitLoading.value = true
  try {
    const res = await updateProfile({
      nickname: profileForm.nickname.trim(),
      email: profileForm.email.trim(),
      avatar: profileForm.avatar.trim(),
    })
    if (res.code === 0) {
      syncProfile(res.data)
      userStore.setUser(res.data)
      ElMessage.success({
        message: successMessage,
        duration: 1000,
      })
    } else {
      ElMessage.error(res.message || '资料更新失败')
    }
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '资料更新失败'))
  } finally {
    submitLoading.value = false
  }
}

async function handlePasswordSubmit() {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    ElMessage.warning('请完整填写密码信息')
    return
  }

  passwordLoading.value = true
  try {
    const res = await changePassword(passwordForm)
    if (res.code === 0) {
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      ElMessage.success({
        message: '保存成功',
        duration: 1000,
      })
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '密码修改失败'))
  } finally {
    passwordLoading.value = false
  }
}

function goPasswordPage() {
  router.push({ path: '/account', query: { mode: 'password' } })
}

function goProfilePage() {
  router.push('/account')
}

onMounted(() => {
  loadProfile()
  loadDefaultAvatars()
})
</script>

<template>
  <div class="page">
    <div class="container" v-loading="loading">
      <div v-if="!isPasswordPage" class="account-layout">
        <button class="avatar-button" type="button" @click="openAvatarDialog">
          <div class="avatar-box">
            <el-avatar :size="120" :src="getImageUrl(displayAvatar)">
              {{ displayName.slice(0, 1) }}
            </el-avatar>
            <div class="avatar-mask">修改头像</div>
          </div>
        </button>

        <div class="info-row account-row">
          <span class="label">账号：</span>
          <span class="value">{{ currentUsername }}</span>
        </div>

        <div class="info-row">
          <span class="label">昵称：</span>
          <el-input v-model="profileForm.nickname" class="nickname-input" maxlength="20" />
        </div>

        <div class="info-row password-row">
          <span class="label">密码：</span>
          <span class="value">********</span>
          <el-button type="primary" plain @click="goPasswordPage">修改密码</el-button>
        </div>

        <div class="bottom-actions">
          <el-button type="primary" :loading="submitLoading" @click="saveProfile()">保存</el-button>
        </div>
      </div>

      <div v-else class="password-layout">
        <div class="password-title">修改密码</div>

        <div class="field-row">
          <span class="label">旧密码：</span>
          <el-input v-model="passwordForm.oldPassword" class="field-input" type="password" show-password />
        </div>

        <div class="field-row">
          <span class="label">新密码：</span>
          <el-input v-model="passwordForm.newPassword" class="field-input" type="password" show-password />
        </div>

        <div class="field-row">
          <span class="label">确认新密码：</span>
          <el-input v-model="passwordForm.confirmPassword" class="field-input" type="password" show-password />
        </div>

        <div class="bottom-actions">
          <el-button @click="goProfilePage">返回</el-button>
          <el-button type="primary" :loading="passwordLoading" @click="handlePasswordSubmit">保存</el-button>
        </div>
      </div>

      <el-dialog v-model="avatarDialogVisible" title="选择头像" width="560px" class="avatar-dialog">
        <div class="avatar-dialog-grid">
          <button
            v-for="item in defaultAvatarOptions"
            :key="item.id"
            type="button"
            class="avatar-option"
            @click="selectDefaultAvatar(item)"
          >
            <el-avatar :size="86" :src="item.imageData" />
            <span>{{ item.name }}</span>
          </button>

          <el-upload class="avatar-option upload-option" :show-file-list="false" :http-request="customUpload" accept="image/*">
            <div class="upload-inner">
              <span class="upload-plus">+</span>
              <span>自定义头像</span>
            </div>
          </el-upload>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 28px 0 64px;
  min-height: calc(100vh - 80px);
  background: #f1e6dc;
}

.container {
  max-width: 920px;
  margin: 0 auto;
  padding: 0 28px;
}

.account-layout,
.password-layout {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 22px;
  padding: 32px 0 0;
}

.avatar-button {
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
}

.avatar-box {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 14px 32px rgba(37, 28, 22, 0.12);
}

.avatar-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.avatar-box:hover .avatar-mask {
  opacity: 1;
}

.info-row,
.field-row {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
}

.label {
  width: 96px;
  flex-shrink: 0;
  color: #4b3a30;
  font-size: 16px;
  line-height: 40px;
}

.value {
  color: #33261f;
  font-size: 16px;
}

.nickname-input,
.field-input {
  max-width: 420px;
}

.password-row {
  gap: 16px;
}

.password-title {
  margin: 0;
  color: #32251e;
  font-size: 28px;
  font-weight: 700;
}

.bottom-actions {
  display: flex;
  justify-content: flex-end;
  width: 100%;
  gap: 12px;
  margin-top: 8px;
}

.avatar-dialog-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.avatar-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 16px 12px;
  border: 1px solid #eadfd7;
  border-radius: 18px;
  background: #fffaf7;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.avatar-option:hover {
  transform: translateY(-2px);
  border-color: #dca889;
  box-shadow: 0 14px 24px rgba(73, 52, 38, 0.08);
}

.avatar-option span {
  color: #584236;
  font-size: 14px;
}

.upload-option {
  width: 100%;
}

.upload-option :deep(.el-upload) {
  width: 100%;
}

.upload-inner {
  min-height: 134px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.upload-plus {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: #cf6f46;
  font-size: 28px;
  border: 1px dashed #dfb398;
}

@media (max-width: 768px) {
  .page {
    padding: 18px 0 44px;
  }

  .container {
    padding: 0 14px;
  }

  .info-row,
  .field-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .label {
    width: auto;
    line-height: 1.4;
  }

  .nickname-input,
  .field-input {
    max-width: 100%;
    width: 100%;
  }

  .password-row {
    align-items: flex-start;
  }

  .bottom-actions {
    justify-content: flex-start;
  }

  .avatar-dialog-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
