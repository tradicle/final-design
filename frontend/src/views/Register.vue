<script setup lang="ts">
import { reactive, ref } from 'vue'
import { register } from '../api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

async function onSubmit() {
  if (form.password !== form.confirmPassword) {
    ElMessage.error('两次输入密码不一致')
    return
  }
  loading.value = true
  try {
    const res = await register({
      username: form.username,
      password: form.password
    })
    if (res.code === 0) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (e) {
    ElMessage.error('注册异常')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <el-card class="card">
      <template #header>注册</template>
      <el-form label-width="90px">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">注册</el-button>
          <el-button link @click="router.push('/login')">已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  min-height: calc(100vh - 80px);
  display: grid;
  place-items: center;
  padding: 24px;
}
.card {
  width: min(460px, 100%);
}
</style>
