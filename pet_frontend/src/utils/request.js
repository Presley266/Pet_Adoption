import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        if (error.response) {
            const { status, data } = error.response
            if (status === 401) {
                ElMessage.error('请先登录')
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')
                window.location.href = '/login'
            } else if (status === 403) {
                ElMessage.error('没有权限访问')
            } else {
                ElMessage.error(data?.message || '请求失败')
            }
        } else {
            ElMessage.error('网络错误')
        }
        return Promise.reject(error)
    }
)

export default request