<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="nav-container">
      <div class="logo" @click="$router.push('/')">
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { ArrowDown, User, Star, SwitchButton } from '@element-plus/icons-vue'
import { Document } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
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
  background: transparent;
  transition: all 0.3s ease;
}

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
}

.navbar.scrolled .logo {
  color: #333;
  -webkit-text-stroke: 1px #333;
  text-stroke: 1px #333;
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
  background: #ff8c5a;
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