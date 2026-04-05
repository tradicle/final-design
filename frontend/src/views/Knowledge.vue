<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getKnowledgeList, type KnowledgeItem } from '../api/knowledge'

const router = useRouter()
const loading = ref(false)
const list = ref<KnowledgeItem[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getKnowledgeList()
    if (res.code === 0) {
      list.value = res.data
    }
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <el-card class="knowledge-card" shadow="never">
        <template #header>
          <div class="header">
            <h1>小常识</h1>
            <p>养宠科普与到访信息</p>
          </div>
        </template>
        <el-skeleton v-if="loading" :rows="6" animated />
        <div v-else class="items">
          <div v-for="item in list" :key="item.id" class="item" @click="router.push(`/knowledge/${item.id}`)">
            <h3>{{ item.title }}</h3>
          </div>
        </div>
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

.knowledge-card {
  border: 1px solid #e8edf3;
  border-radius: 14px;
  background: #f2f4f6;
}

.header h1 {
  margin: 0;
  font-size: 30px;
  color: #1f2937;
}

.header p {
  margin: 6px 0 0;
  color: #6b7280;
}

.items {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.item {
  background: #fff;
  border: 1px solid #e6ebf2;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 18px rgba(17, 24, 39, 0.08);
}

.item h3 {
  margin: 0 0 10px;
  color: #1f2937;
  font-size: 18px;
}

@media (max-width: 768px) {
  .container {
    padding: 0 14px;
  }
  .items {
    grid-template-columns: 1fr;
  }
}
</style>
