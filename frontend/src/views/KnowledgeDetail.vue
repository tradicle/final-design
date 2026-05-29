<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getKnowledgeDetail, type KnowledgeItem } from '../api/knowledge'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<KnowledgeItem | null>(null)

async function load(id: string) {
  loading.value = true
  try {
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
}

onMounted(() => {
  const id = route.params.id as string
  if (id) load(id)
})

watch(() => route.params.id, (newId) => {
  if (newId) load(newId as string)
})
</script>

<template>
  <div class="detail-pane">
    <el-skeleton v-if="loading" :rows="10" animated />
    <template v-else-if="detail">
      <h1>{{ detail.title }}</h1>
      <div class="content" v-if="detail.content">{{ detail.content }}</div>
      <div class="content empty" v-else>暂无内容</div>
    </template>
  </div>
</template>

<style scoped>
.detail-pane {
  padding: 40px 48px;
}

h1 {
  margin: 0 0 24px;
  font-size: 28px;
  color: #1f2937;
  line-height: 1.4;
}

.content {
  white-space: pre-line;
  color: #374151;
  line-height: 2;
  font-size: 16px;
}

.content.empty {
  color: #9ca3af;
  font-style: italic;
}
</style>
