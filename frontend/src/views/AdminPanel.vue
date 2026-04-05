<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPostList, type Post } from '../api/community'

const active = ref('posts')
const posts = ref<Post[]>([])

async function loadPosts() {
  const res = await getPostList()
  if (res.code === 0) {
    posts.value = res.data
  }
}

function audit(post: Post, pass: boolean) {
  // In real app, call API to update status
  post.title = (pass ? '[已通过] ' : '[已拒绝] ') + post.title
}

onMounted(() => {
  loadPosts()
})
</script>

<template>
  <div class="page">
    <el-card>
      <template #header>管理员面板</template>
      <el-tabs v-model="active">
        <el-tab-pane label="帖子审核" name="posts">
          <el-table :data="posts" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="username" label="作者" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" type="success" @click="audit(scope.row, true)">通过</el-button>
                <el-button size="small" type="danger" @click="audit(scope.row, false)">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="评论审核" name="comments">
          <el-empty description="待接入：评论审核列表" />
        </el-tab-pane>
        <el-tab-pane label="动物管理" name="animals">
          <el-empty description="待接入：动物增删改查" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  padding: 16px;
}
</style>
