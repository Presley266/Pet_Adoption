<template>
  <div class="pet-detail-container" v-loading="loading">
    <div v-if="pet" class="pet-detail">
      <div class="pet-image-section">
        <img :src="pet.imageUrl || 'https://picsum.photos/500/400'" :alt="pet.name" class="pet-main-image">
      </div>
      <div class="pet-info-section">
        <h1>{{ pet.name }}</h1>
        <div class="tags">
          <el-tag type="danger" size="large">{{ getCategoryText(pet.category) }}</el-tag>
          <el-tag type="info" size="large">{{ pet.gender === 'MALE' ? '公' : '母' }}</el-tag>
          <el-tag size="large">{{ pet.age }}个月</el-tag>
        </div>
        <div class="info-row">
          <span class="label">品种：</span>
          <span>{{ pet.breed || '未知' }}</span>
        </div>
        <div class="info-row">
          <span class="label">健康状况：</span>
          <span>{{ pet.healthStatus || '健康' }}</span>
        </div>
        <div class="info-row">
          <span class="label">收藏人数：</span>
          <span>{{ pet.viewCount || 0 }} 人</span>
        </div>
        <div class="description">
          <h3>宠物介绍</h3>
          <p>{{ pet.description }}</p>
        </div>
        <div class="actions">
          <el-button
              v-if="pet.status === 'AVAILABLE' && userStore.isLoggedIn"
              type="primary"
              @click="goToApply">
            申请领养
          </el-button>
          <el-button v-else-if="pet.status !== 'AVAILABLE'" type="info" disabled>已领养</el-button>
          <el-button v-else type="warning" @click="$router.push('/login')">登录后申请领养</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetDetail, addFavorite, removeFavorite, checkFavorite } from '../api'
import { useUserStore } from '../stores/user'
import { Star } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const pet = ref(null)
const isFavorited = ref(false)
const favoriteLoading = ref(false)

const getCategoryText = (category) => {
  const map = { DOG: '狗狗', CAT: '猫咪', BIRD: '鸟类', OTHER: '其他' }
  return map[category] || category
}

const loadPetDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getPetDetail(id)
    if (res.success) {
      pet.value = res.pet
    } else {
      ElMessage.error('宠物不存在')
      router.push('/pets')
    }
  } catch (error) {
    console.error('加载宠物详情失败', error)
  } finally {
    loading.value = false
  }
}

const checkIsFavorited = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await checkFavorite(pet.value.id)
    isFavorited.value = res.isFavorited || false
  } catch (error) {
    console.error('检查收藏状态失败', error)
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      await removeFavorite(pet.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(pet.value.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const goToApply = () => {
  // 暂时提示功能开发中
  ElMessage.info('领养申请功能开发中，敬请期待')
}

onMounted(() => {
  loadPetDetail()
})
</script>

<style scoped>
.pet-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 20px 40px;
}

.pet-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.pet-main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 12px;
}

.pet-info-section h1 {
  font-size: 32px;
  margin-bottom: 20px;
  color: #333;
}

.tags {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.info-row {
  margin-bottom: 16px;
  font-size: 16px;
}

.label {
  font-weight: 500;
  color: #666;
  width: 100px;
  display: inline-block;
}

.description {
  margin: 24px 0;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
}

.description h3 {
  margin-bottom: 12px;
  font-size: 18px;
  color: #333;
}

.description p {
  line-height: 1.6;
  color: #666;
}

.actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .pet-detail {
    grid-template-columns: 1fr;
    gap: 24px;
    padding: 20px;
  }
  .pet-detail-container {
    padding: 80px 16px 32px;
  }
  .pet-main-image {
    height: 300px;
  }
  .pet-info-section h1 {
    font-size: 24px;
  }
}
</style>