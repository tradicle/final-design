<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getKnowledgeDetail, type KnowledgeItem } from '../api/knowledge'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<KnowledgeItem | null>(null)

onMounted(async () => {
  loading.value = true
  try {
    const id = route.params.id as string
    const res = await getKnowledgeDetail(id)
    if (res.code === 0 && res.data) {
      detail.value = res.data
    } else {
      ElMessage.error('未找到该小常识')
      router.replace('/knowledge')
    }
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <el-card class="card" shadow="never">
        <div class="toolbar">
          <el-button link @click="router.push('/knowledge')">返回小常识列表</el-button>
        </div>
        <el-skeleton v-if="loading" :rows="10" animated />
        <template v-else-if="detail">
          <h1>{{ detail.title }}</h1>
          <div class="content">{{ detail.content }}</div>
        </template>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 36px 0 56px;
}

.container {
  max-width: 1160px;
  margin: 0 auto;
  padding: 0 28px;
}

.card {
  border: 1px solid #e8edf3;
  border-radius: 14px;
  background: #fff;
}

.toolbar {
  margin-bottom: 10px;
}

h1 {
  margin: 0 0 16px;
  font-size: 30px;
  color: #1f2937;
}

.content {
  white-space: pre-line;
  color: #374151;
  line-height: 1.9;
}

@media (max-width: 768px) {
  .container {
    padding: 0 14px;
  }
}
</style>
