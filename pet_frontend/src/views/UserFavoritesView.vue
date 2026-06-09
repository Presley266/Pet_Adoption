<template>
  <div class="favorites-container">
    <h2>我的收藏</h2>

    <div class="pet-grid" v-loading="loading">
      <div v-for="fav in favorites" :key="fav.id" class="pet-card" @click="$router.push(`/pets/${fav.petId}`)">
        <div class="pet-image-wrapper">
          <img :src="fav.pet?.imageUrl || 'https://picsum.photos/300/200'" class="pet-image">
        </div>
        <div class="pet-info">
          <h3>{{ fav.pet?.name || '宠物' }}</h3>
          <p>{{ fav.pet?.breed || '未知品种' }} · {{ fav.pet?.age || 0 }}个月</p>
          <el-button size="small" type="danger" @click.stop="removeFavorite(fav.petId)">取消收藏</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && favorites.length === 0" description="还没有收藏任何宠物" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyFavorites, removeFavorite as removeFavoriteApi } from '../api'

const loading = ref(false)
const favorites = ref([])

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getMyFavorites({ page: 0, size: 20 })
    if (res.success) {
      favorites.value = res.favorites || []
    }
  } catch (error) {
    console.error('加载收藏失败', error)
  } finally {
    loading.value = false
  }
}

const removeFavorite = async (petId) => {
  try {
    await removeFavoriteApi(petId)
    ElMessage.success('已取消收藏')
    loadFavorites()

  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-container {
  max-width: 1200px;
  margin: 80px auto 40px;
  padding: 0 20px;
}

.favorites-container h2 {
  margin-bottom: 24px;
  font-size: 24px;
  color: #333;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.pet-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.pet-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
}

.pet-image-wrapper {
  aspect-ratio: 4 / 3;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pet-info {
  padding: 16px;
}

.pet-info h3 {
  font-size: 16px;
  margin-bottom: 6px;
  color: #333;
}

.pet-info p {
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .pet-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}
</style>