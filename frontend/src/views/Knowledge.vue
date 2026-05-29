<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getKnowledgeList, type KnowledgeItem } from '../api/knowledge'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const list = ref<KnowledgeItem[]>([])

const groupedList = computed(() => {
  const groups: Record<string, KnowledgeItem[]> = {}
  for (const item of list.value) {
    const cat = item.category || '其他'
    if (!groups[cat]) groups[cat] = []
    groups[cat].push(item)
  }
  return groups
})

const activeId = computed(() => {
  const id = route.params.id as string | undefined
  return id ? Number(id) : null
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await getKnowledgeList()
    if (res.code === 0 && res.data.length > 0) {
      list.value = res.data
      // Auto-redirect to first article if no id in route
      if (!route.params.id) {
        const first = res.data[0]!
        router.replace(`/knowledge/${first.id}`)
      }
    }
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <div class="layout" v-loading="loading">
      <aside class="sidebar">
        <div class="sidebar-header">
          <h1>小常识</h1>
          <p>养宠科普与到访信息</p>
        </div>
        <nav class="directory">
          <div v-for="(items, category) in groupedList" :key="category" class="dir-group">
            <div class="dir-group-title">{{ category }}</div>
            <div
              v-for="item in items"
              :key="item.id"
              class="dir-item"
              :class="{ active: activeId === item.id }"
              @click="router.push(`/knowledge/${item.id}`)"
            >
              {{ item.title }}
            </div>
          </div>
        </nav>
      </aside>
      <main class="detail">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
}

.layout {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid #eee;
  padding: 32px 0;
  overflow-y: auto;
}

.sidebar-header {
  padding: 0 24px 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 12px;
}

.sidebar-header h1 {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
}

.sidebar-header p {
  margin: 6px 0 0;
  color: #9ca3af;
  font-size: 13px;
}

.directory {
  padding: 0 12px;
}

.dir-group {
  margin-bottom: 16px;
}

.dir-group-title {
  font-size: 12px;
  font-weight: 700;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 8px 12px 6px;
}

.dir-item {
  padding: 10px 12px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.15s, color 0.15s;
  line-height: 1.5;
}

.dir-item:hover {
  background: #f3f4f6;
}

.dir-item.active {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  font-weight: 600;
}

.detail {
  flex: 1;
  min-width: 0;
}
</style>
