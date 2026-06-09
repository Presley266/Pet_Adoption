import request from '../utils/request'

// ========== 认证相关 ==========
export const register = (data) => request.post('/auth/register', data)
export const login = (data) => request.post('/auth/login', data)

// ========== 宠物相关 ==========
export const getPets = (params) => request.get('/pets', { params })
export const getPetDetail = (id) => request.get(`/pets/${id}`)
export const getCategories = () => request.get('/categories')

// ========== 收藏相关 ==========
export const addFavorite = (petId) => request.post(`/favorites/${petId}`)
export const removeFavorite = (petId) => request.delete(`/favorites/${petId}`)
export const checkFavorite = (petId) => request.get(`/favorites/check/${petId}`)
export const getMyFavorites = (params) => request.get('/favorites/my', { params })

// ========== 个人中心相关 ==========
export const getUserProfile = () => request.get('/user/profile')
export const updateUserProfile = (data) => request.put('/user/profile', data)
// ========== 申请相关 ==========
export const submitApplication = (data) => request.post('/user/applications', data)
export const getUserApplications = (params) => request.get('/user/applications', { params })
export const cancelApplication = (id) => request.put(`/user/applications/${id}/cancel`)

// ========== 管理员相关 ==========
export const getStatistics = () => request.get('/admin/dashboard/statistics')
export const getAllPets = () => request.get('/admin/pets')
export const addPet = (data) => request.post('/admin/pets', data)
export const updatePet = (id, data) => request.put(`/admin/pets/${id}`, data)
export const deletePet = (id) => request.delete(`/admin/pets/${id}`)
export const getAllUsers = () => request.get('/admin/users')
export const updateUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status })
export const updateUserRole = (id, role) => request.put(`/admin/users/${id}/role`, { role })
export const getAllApplications = (params) => request.get('/admin/applications', { params })
export const reviewApplication = (id, status, reviewComment) =>
    request.put(`/admin/applications/${id}/review`, { status, reviewComment })