<template>
  <nav class="navbar" :class="{ 'white-nav': !isHomePage, scrolled: isScrolled && isHomePage }">
    <div class="nav-container">
      <div class="logo" @click="goHome">
        PetHome
      </div>
      <div class="nav-links">
        <router-link to="/" class="nav-link" active-class="active">首页</router-link>
        <router-link to="/pets" class="nav-link" active-class="active">宠物列表</router-link>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click" class="user-dropdown" @command="handleCommand">
            <span class="nav-link user-name">
              {{ userStore.userInfo?.username }} <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="admin" v-if="userStore.isAdmin" divided>
                  <el-icon><Setting /></el-icon> 后台管理
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人资料
                </el-dropdown-item>
                <el-dropdown-item command="favorites" v-if="!userStore.isAdmin">
                  <el-icon><Star /></el-icon> 我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="applications" v-if="!userStore.isAdmin">
                  <el-icon><Document /></el-icon> 我的申请
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import {
  ArrowDown,
  User,
  Star,
  SwitchButton,
  Document,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isScrolled = ref(false)

// 只有首页才用透明导航栏
const isHomePage = computed(() => route.path === '/')

// 滚动监听（仅首页生效）
const handleScroll = () => {
  if (isHomePage.value) {
    isScrolled.value = window.scrollY > 50
  }
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'admin':
      router.push('/admin')
      break
    case 'profile':
      router.push('/user/profile')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'applications':
      router.push('/user/applications')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

// 跳转首页
const goHome = () => {
  router.push('/')
}

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
  transition: all 0.3s ease;
}

/* ========== 首页专用：透明导航栏 ========== */
.navbar {
  background: transparent;
}

.navbar .logo {
  color: white;
  -webkit-text-stroke: 1px white;
  text-stroke: 1px white;
}

.navbar .nav-link {
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.navbar .nav-link::after {
  background: white;
}

/* 首页滚动后变白 */
.navbar.scrolled {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.navbar.scrolled .logo {
  color: #333;
  -webkit-text-stroke: 1px #333;
  text-stroke: 1px #333;
}

.navbar.scrolled .nav-link {
  color: #333;
  text-shadow: none;
}

.navbar.scrolled .nav-link::after {
  background: #ff8c5a;
}

/* ========== 其他所有页面：白色导航栏 ========== */
.navbar.white-nav {
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.navbar.white-nav .logo {
  color: #333;
  -webkit-text-stroke: 1px #333;
  text-stroke: 1px #333;
}

.navbar.white-nav .nav-link {
  color: #333;
  text-shadow: none;
}

.navbar.white-nav .nav-link::after {
  background: #ff8c5a;
}

.navbar.white-nav .nav-link:hover {
  color: #ff8c5a;
}

.navbar.white-nav .nav-link.active {
  color: #ff8c5a;
}

.navbar.white-nav .nav-link:hover::after,
.navbar.white-nav .nav-link.active::after {
  width: 100%;
}

/* ========== 公共样式 ========== */
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
  transition: all 0.3s ease;
}

.nav-links {
  display: flex;
  gap: 32px;
  align-items: center;
}

.nav-link {
  text-decoration: none;
  font-size: 16px;
  padding: 8px 4px;
  position: relative;
  cursor: pointer;
  transition: color 0.3s;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
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