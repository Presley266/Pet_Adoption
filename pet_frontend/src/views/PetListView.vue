<template>
  <div class="pet-list-container">
    <div class="filters">
      <div class="filter-group">
        <el-radio-group v-model="filters.category" @change="loadPets">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="CAT">猫咪</el-radio-button>
          <el-radio-button value="DOG">狗狗</el-radio-button>
          <el-radio-button value="OTHER">其他</el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-group">
        <span class="filter-label">排序：</span>
        <el-select v-model="filters.sortBy" placeholder="排序方式" @change="loadPets" clearable class="sort-select">
          <el-option label="最新发布" value="createTime" />
          <el-option label="最受欢迎" value="favoriteCount" />
          <el-option label="年龄最小" value="age" />
        </el-select>
      </div>
    </div>

    <div class="pet-grid" v-loading="loading">
      <div v-for="pet in pets" :key="pet.id" class="pet-card" @click="$router.push(`/pets/${pet.id}`)">
        <img :src="pet.imageUrl || 'https://picsum.photos/300/200?random='+pet.id" :alt="pet.name" class="pet-image">
        <div class="pet-info">
          <h3>{{ pet.name }}</h3>
          <p class="breed">{{ pet.breed }} · {{ pet.age }}个月</p>
          <p class="desc">{{ pet.description?.slice(0, 50) }}...</p>
          <div class="stats">
            <span class="favorite-btn" @click.stop="toggleFavorite(pet)">
              <span class="heart" :class="{ liked: pet.isFavorited }">♥</span>
              {{ pet.favoriteCount || 0 }}
            </span>
            <span class="status" :class="pet.status === 'AVAILABLE' ? 'available' : 'adopted'">
              {{ pet.status === 'AVAILABLE' ? '可领养' : '已领养' }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="totalPages > 0">
      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage + 1"
          @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPets } from '../api'
import { addFavorite, removeFavorite, checkFavorite } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const favoriteLoading = ref({})
const loading = ref(false)
const pets = ref([])
const total = ref(0)
const totalPages = ref(0)
const currentPage = ref(0)
const pageSize = ref(9)

const filters = reactive({
  category: '',
  sortBy: ''
})

// 检查每只宠物是否已收藏（批量）
const checkFavoritesStatus = async (pets) => {
  if (!userStore.isLoggedIn) return
  for (const pet of pets) {
    try {
      const res = await checkFavorite(pet.id)
      pet.isFavorited = res.isFavorited || false
    } catch (error) {
      console.error('检查收藏状态失败', error)
    }
  }
}

// 切换收藏
const toggleFavorite = async (pet) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }

  favoriteLoading.value[pet.id] = true
  try {
    if (pet.isFavorited) {
      await removeFavorite(pet.id)
      pet.isFavorited = false
      pet.favoriteCount = Math.max(0, (pet.favoriteCount || 0) - 1)
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(pet.id)
      pet.isFavorited = true
      pet.favoriteCount = (pet.favoriteCount || 0) + 1
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value[pet.id] = false
  }
}

// 在 loadPets 成功后调用 checkFavoritesStatus
const loadPets = async () => {
  loading.value = true
  try {
    const res = await getPets({
      page: currentPage.value,
      size: pageSize.value,
      category: filters.category || '',
      sortBy: filters.sortBy || ''
    })
    if (res.success) {
      pets.value = res.pets || []
      total.value = res.total || 0
      totalPages.value = res.totalPages || 0
      // 检查收藏状态
      await checkFavoritesStatus(pets.value)
    }
  } catch (error) {
    console.error('加载宠物列表失败', error)
  } finally {
    loading.value = false
  }
}

const onPageChange = (page) => {
  currentPage.value = page - 1
  loadPets()
}

onMounted(() => {
  loadPets()
})
</script>

<style scoped>
.pet-list-container {
  max-width: 1200px;
  margin: 50px auto 0;
  padding: 20px;
}
.filters {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}
.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.filter-label {
  font-weight: 500;
  color: #333;
}
.sort-select {
  width: 130px;
}
.pet-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}
.pet-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.pet-card:hover {
  transform: translateY(-5px);
}
.pet-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}
.pet-info {
  padding: 16px;
}
.pet-info h3 {
  font-size: 18px;
  margin-bottom: 8px;
}
.breed {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}
.desc {
  color: #999;
  font-size: 13px;
  margin-bottom: 12px;
}
.stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.favorite-btn {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.heart {
  font-size: 16px;
  color: #ccc;
  transition: none;
}

.heart.liked {
  color: #ff4d4f;
}
</style>