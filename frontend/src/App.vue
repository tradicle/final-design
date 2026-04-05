<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './store/user'
import { computed } from 'vue'


const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const user = computed(() => userStore.user)

const activeIndex = computed(() => route.path)

function logout() {
  userStore.logout()
  router.push('/')
}


</script>

<style>
/* Global style override for the adopt menu popup */
.adopt-submenu-popup {
  min-width: 100px !important;
  width: 100px !important;
  /* Removed left: auto !important to let Element Plus handle positioning correctly */
}
.adopt-submenu-popup .el-menu--popup {
  min-width: 100px !important;
  width: 100% !important;
  padding: 5px 0 !important;
}
.adopt-submenu-popup .el-menu-item {
  min-width: 100px !important;
  padding: 0 !important;
  justify-content: center;
  height: 40px;
  line-height: 40px;
  text-align: center;
}
</style>

<template>
  <el-container class="layout-container">
    <el-header height="80px" class="header">
      <div class="header-inner">
        <div class="brand" @click="router.push('/')">
          <img src="/hero.png" alt="Logo" class="logo" v-if="false"/> 
          <!-- Placeholder for logo -->
          <span class="brand-text">汪汪喵呜孤儿院</span>
        </div>
        
        <el-menu mode="horizontal" :ellipsis="false" router class="menu" :default-active="activeIndex">
          <el-menu-item index="/">首页</el-menu-item>
          
          <el-sub-menu index="/animals" class="adopt-menu" popper-class="adopt-submenu-popup">
            <template #title>
              <span @click="router.push('/animals')">领养</span>
            </template>
            <el-menu-item index="/animals">领养中心</el-menu-item>
            <el-menu-item index="/adoption/rules">领养须知</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/community">社区</el-menu-item>
          <el-menu-item index="/news">爱心活动</el-menu-item>
          <el-menu-item index="/donate">捐赠</el-menu-item>
          <el-menu-item index="/knowledge">小常识</el-menu-item>
          <el-menu-item index="/about">关于我们</el-menu-item>
        </el-menu>

        <div class="auth-actions">
          <template v-if="user">
            <span class="username">欢迎, {{ user.username }}</span>
            <el-button link @click="logout">退出</el-button>
            <el-button v-if="user.role === 'ADMIN'" type="warning" link @click="router.push('/admin')">管理后台</el-button>
          </template>
          <template v-else>
            <el-button class="login-btn" @click="router.push('/login')">登录</el-button>
            <el-button type="primary" class="register-btn" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <el-main class="main">
      <router-view />
    </el-main>

    <el-footer height="auto" class="footer">
      <div class="footer-inner">
        <div class="footer-col">
          <h3>联系我们</h3>
          <p>地址：某某市某某区流浪动物救助中心</p>
          <p>电话：010-12345678</p>
          <p>邮箱：contact@pohome.cn</p>
        </div>
        <div class="footer-col">
          <h3>友情链接</h3>
          <p>中国小动物保护协会</p>
          <p>北京领养日</p>
        </div>
        <div class="footer-col">
          <h3>关注我们</h3>
          <p>微信公众号：汪汪喵呜</p>
          <p>微博：@汪汪喵呜孤儿院</p>
        </div>
      </div>
      <div class="footer-bottom">
        &copy; 2026 汪汪喵呜孤儿院 版权所有
      </div>
    </el-footer>
  </el-container>
</template>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #eceff3;
  box-shadow: 0 6px 24px rgba(17, 24, 39, 0.06);
  position: sticky;
  top: 0;
  z-index: 200;
  padding: 0;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
}

.brand {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 28px;
}

.brand-text {
  font-size: 25px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 0.8px;
}

.menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

.menu :deep(.el-menu-item), .menu :deep(.el-sub-menu__title) {
  font-size: 15px;
  color: #2f3b4a;
  padding: 0 16px;
  height: 79px;
  line-height: 79px;
  font-weight: 500;
}

.menu :deep(.el-menu-item.is-active), .menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: var(--el-color-primary);
  border-bottom: 3px solid var(--el-color-primary);
  font-weight: 700;
}

.menu :deep(.el-menu-item:hover), .menu :deep(.el-sub-menu__title:hover) {
  background-color: transparent;
  color: var(--el-color-primary);
}

:deep(.el-menu--popup) {
  border: 1px solid #eceff3;
  border-radius: 12px;
  box-shadow: 0 16px 30px rgba(17, 24, 39, 0.12);
}

:deep(.el-menu--popup .el-menu-item) {
  font-size: 15px;
}

.auth-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 18px;
}

.username {
  font-size: 13px;
  color: #667284;
  background: #f8fafc;
  border: 1px solid #e6ebf1;
  border-radius: 999px;
  padding: 7px 14px;
}

.login-btn {
  border-radius: 999px;
  padding: 8px 18px;
  border-color: #d4dbe3;
}

.register-btn {
  border-radius: 999px;
  padding: 8px 18px;
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  box-shadow: 0 8px 16px rgba(207, 95, 63, 0.25);
}

.main {
  padding: 0;
  flex: 1;
  background: var(--page-bg);
}

.footer {
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  color: #fff;
  padding: 64px 0 18px;
  margin-top: 40px;
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  padding: 0 28px;
  flex-wrap: wrap;
  gap: 26px;
}

.footer-col h3 {
  font-size: 17px;
  margin-bottom: 14px;
  color: #fff;
  letter-spacing: 0.4px;
}

.footer-col p {
  color: #c5ccd6;
  margin-bottom: 8px;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.footer-col p:hover {
  color: #fffdf9;
}

.footer-bottom {
  text-align: center;
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  color: #9aa6b6;
  font-size: 12px;
  letter-spacing: 0.2px;
}
</style>
