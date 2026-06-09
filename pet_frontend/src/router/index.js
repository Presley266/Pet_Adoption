import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/HomeView.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue') },
  { path: '/pets', name: 'PetList', component: () => import('../views/PetListView.vue') },
  { path: '/pets/:id', name: 'PetDetail', component: () => import('../views/PetDetailView.vue') },
  // 个人中心
  { path: '/user/profile', name: 'UserProfile', component: () => import('../views/UserProfileView.vue'), meta: { requiresAuth: true } },
  // 我的收藏
  { path: '/user/favorites', name: 'UserFavorites', component: () => import('../views/UserFavoritesView.vue'), meta: { requiresAuth: true } },
  // 领养申请
  { path: '/applications/new/:petId', name: 'ApplicationForm', component: () => import('../views/ApplicationFormView.vue'), meta: { requiresAuth: true } },
  { path: '/user/applications', name: 'UserApplications', component: () => import('../views/UserApplicationsView.vue'), meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  userStore.initFromStorage()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router