<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="admin-sidebar">
      <div class="sidebar-header">毛茸茸之家·后台</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#5a3e2d"
        text-color="#eadfd5"
        active-text-color="#fff"
      >
        <el-menu-item index="/admin/dashboard">
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/animals">
          <span>宠物管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/news">
          <span>资讯管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/activities">
          <span>爱心活动</span>
        </el-menu-item>
        <el-menu-item index="/admin/community">
          <span>社区审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/adoption">
          <span>领养审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/donation-claim">
          <span>物资认领</span>
        </el-menu-item>
        <el-menu-item index="/admin/content">
          <span>内容运营</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="admin-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.admin-sidebar {
  background-color: #5a3e2d;
}

.sidebar-header {
  padding: 20px 16px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-main {
  background-color: #fdf8f3;
  padding: 24px;
}
</style>
