<template>
  <div class="home">
    <!-- 自定义淡入淡出轮播 -->
    <div class="hero-carousel">
      <div v-for="(item, index) in banners"
           :key="index"
           class="carousel-slide"
           :class="{ active: currentIndex === index, fade: true }"
           :style="{ backgroundImage: `url(${item.image})` }">
        <div class="carousel-caption">
          <h1>{{ item.title }}</h1>
          <p>{{ item.subtitle }}</p>
        </div>
      </div>
      <!-- 指示点 -->
      <div class="carousel-dots">
        <span v-for="(item, index) in banners"
              :key="index"
              class="dot"
              :class="{ active: currentIndex === index }"
              @click="goToSlide(index)"></span>
      </div>
    </div>

    <!-- 可领养宠物列表 -->
    <div class="pet-section">
      <div class="section-header">
        <h2>待领养小伙伴</h2>
        <router-link to="/pets" class="more-link">查看更多 →</router-link>
      </div>
      <div class="pet-grid">
        <div v-for="pet in availablePets" :key="pet.id" class="pet-card" @click="$router.push(`/pets/${pet.id}`)">
          <div class="pet-image-wrapper">
            <img :src="pet.imageUrl || 'https://picsum.photos/300/200'" :alt="pet.name" class="pet-image">
            <div class="pet-status" :class="pet.status === 'AVAILABLE' ? 'available' : 'adopted'">
              {{ pet.status === 'AVAILABLE' ? '可领养' : '已领养' }}
            </div>
          </div>
          <div class="pet-info">
            <h3>{{ pet.name }}</h3>
            <p>{{ pet.breed || '未知品种' }} · {{ pet.age }}个月 · {{ pet.gender === 'MALE' ? '♂' : '♀' }}</p>
          </div>
        </div>
      </div>
    </div>

    <footer class="copyright">
      <p>© 2026 PetHome </p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getPets } from '../api'

const banners = ref([
  { title: '给流浪动物一个家', subtitle: '领养代替购买', image: '/images/banner1.jpg' },
  { title: '爱心领养', subtitle: '让爱不再流浪', image: '/images/banner2.jpg' },
  { title: '加入我们', subtitle: '成为志愿者', image: '/images/banner3' +
        '.jpg' }
])

const currentIndex = ref(0)
let timer = null

const availablePets = ref([])

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % banners.value.length
}

const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + banners.value.length) % banners.value.length
}

const goToSlide = (index) => {
  currentIndex.value = index
}

// 自动轮播
const startAutoPlay = () => {
  timer = setInterval(() => {
    nextSlide()
  }, 2600)
}

const stopAutoPlay = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

const loadAvailablePets = async () => {
  try {
    const res = await getPets({ page: 0, size: 6 })
    if (res.success && res.pets) {
      availablePets.value = res.pets.filter(pet => pet.status === 'AVAILABLE')
    }
  } catch (error) {
    console.error('获取宠物列表失败', error)
  }
}

onMounted(() => {
  loadAvailablePets()
  startAutoPlay()
})

onUnmounted(() => {
  stopAutoPlay()
})
</script>

<style scoped>
.home {
  background: #f8f9fa;
}

/* ========== 淡入淡出轮播 ========== */
.hero-carousel {
  position: relative;
  height: 100vh;
  overflow: hidden;
}

.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 1s ease-in-out;
  z-index: 1;
}

.carousel-slide.active {
  opacity: 1;
  z-index: 2;
}

/* 指示点 */
.carousel-dots {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 10;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  width: 24px;
  border-radius: 5px;
  background: #ff8c5a;
}

/* 轮播文字 */
.carousel-caption {
  position: absolute;
  bottom: 20%;
  left: 10%;
  color: white;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
  z-index: 5;
}

.carousel-caption h1 {
  font-size: 48px;
  margin-bottom: 12px;
}

.carousel-caption p {
  font-size: 20px;
  opacity: 0.9;
}

/* 宠物区块 */
.pet-section {
  max-width: 1200px;
  margin: 60px auto;
  padding: 0 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 32px;
  padding-bottom: 12px;
  border-bottom: 2px solid #eee;
}

.section-header h2 {
  font-size: 24px;
  font-weight: 500;
  color: #333;
}

.more-link {
  color: #ff8c5a;
  text-decoration: none;
  font-size: 14px;
}

.more-link:hover {
  text-decoration: underline;
}

/* 网格布局 */
.pet-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.pet-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.pet-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
}

.pet-image-wrapper {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.pet-card:hover .pet-image {
  transform: scale(1.02);
}

.pet-status {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(0,0,0,0.6);
  color: white;
}

.pet-status.available {
  background: #52c41a;
}

.pet-status.adopted {
  background: #ff4d4f;
}

.pet-info {
  padding: 16px;
}

.pet-info h3 {
  font-size: 18px;
  margin-bottom: 6px;
  color: #333;
}

.pet-info p {
  font-size: 13px;
  color: #888;
}

/* 版权 */
.copyright {
  text-align: center;
  padding: 32px;
  background: white;
  border-top: 1px solid #eee;
  color: #999;
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 768px) {
  .pet-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  .carousel-caption h1 {
    font-size: 28px;
  }
  .carousel-arrow {
    width: 32px;
    height: 32px;
    font-size: 20px;
  }
}
</style>