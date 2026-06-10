<template>
  <nav class="navbar" :class="{ scrolled: isScrolled && !isAdminPage, 'admin-nav': isAdminPage }">
    <div class="nav-container">
      <div class="logo" @click="goHome">
        PetHome
      </div>
      <div class="nav-links">
        <router-link to="/" class="nav-link" active-class="active">首页</router-link>
        <router-link to="/pets" class="nav-link" active-class="active">宠物列表</router-link>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click" class="user-dropdown">
            <span class="nav-link user-name">
              {{ userStore.userInfo?.username }} <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="userStore.isAdmin" @click="goToAdmin">
                  <el-icon><Setting /></el-icon> 后台管理
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/profile')">
                  <el-icon><User /></el-icon> 个人资料
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/favorites')">
                  <el-icon><Star /></el-icon> 我的收藏
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/user/applications')">
                  <el-icon><Document /></el-icon> 我的申请
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link" active-class="active">登录</router-link>
          <router-link to="/register" class="nav-link" active-class="active">注册</router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { ArrowDown, User, Star, SwitchButton, Document, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isScrolled = ref(false)

// 判断是否是后台页面
const isAdminPage = computed(() => route.path.startsWith('/admin'))

// 滚动监听
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

// 跳转首页
const goHome = () => {
  router.push('/')
}

// 跳转后台
const goToAdmin = () => {
  router.push('/admin')
}

// 监听路由变化，如果是后台页面，确保导航栏样式正确
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/admin')) {
    // 后台页面不需要滚动监听效果
    isScrolled.value = false
  }
}, { immediate: true })

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: transparent;
  transition: all 0.3s ease;
}

/* 普通页面滚动后效果 */
.navbar.scrolled {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.navbar.scrolled .nav-link {
  color: #333;
}

.navbar.scrolled .logo {
  color: #333;
  -webkit-text-stroke: 1px #333;
  text-stroke: 1px #333;
}

/* 后台页面专用样式 */
.navbar.admin-nav {
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.navbar.admin-nav .logo {
  color: #333;
  -webkit-text-stroke: 1px #333;
  text-stroke: 1px #333;
}

.navbar.admin-nav .nav-link {
  color: #333;
  text-shadow: none;
}

.navbar.admin-nav .nav-link:hover {
  color: #ff8c5a;
}

.navbar.admin-nav .nav-link.active {
  color: #ff8c5a;
}

.navbar.admin-nav .nav-link::after {
  background: #ff8c5a;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: 900;
  cursor: pointer;
  margin-left: 16px;
  color: white;
  -webkit-text-stroke: 1px white;
  text-stroke: 1px white;
  transition: all 0.3s ease;
}

.nav-links {
  display: flex;
  gap: 32px;
  align-items: center;
}

.nav-link {
  text-decoration: none;
  color: white;
  font-size: 16px;
  padding: 8px 4px;
  position: relative;
  cursor: pointer;
  transition: color 0.3s;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: white;
  transition: width 0.3s;
}

.nav-link:hover::after,
.nav-link.active::after {
  width: 100%;
}

.nav-link:hover {
  color: #ff8c5a;
}

.nav-link.active {
  color: #ff8c5a;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 4px;
}

.user-dropdown {
  cursor: pointer;
}
</style>