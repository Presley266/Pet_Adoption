import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getUserProfile } from '../api'

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: null,
        token: localStorage.getItem('token') || null,
        isLoggedIn: !!localStorage.getItem('token')
    }),

    getters: {
        isAdmin: (state) => state.userInfo?.role === 'ADMIN'
    },

    actions: {
        async login(username, password) {
            try {
                const res = await loginApi({ username, password })
                if (res.success) {
                    this.token = res.token
                    this.userInfo = {
                        id: res.userId,
                        username: res.username,
                        role: res.role
                    }
                    localStorage.setItem('token', res.token)
                    localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
                    this.isLoggedIn = true
                    return { success: true }
                } else {
                    return { success: false, message: res.message }
                }
            } catch (error) {
                return { success: false, message: '登录失败' }
            }
        },

        async register(userData) {
            try {
                const res = await registerApi(userData)
                if (res.success) {
                    return { success: true }
                } else {
                    return { success: false, message: res.message }
                }
            } catch (error) {
                return { success: false, message: '注册失败' }
            }
        },

        async fetchUserInfo() {
            try {
                const res = await getUserProfile()
                if (res.success) {
                    this.userInfo = res.user
                    localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
                }
            } catch (error) {
                console.error('获取用户信息失败', error)
            }
        },

        logout() {
            this.token = null
            this.userInfo = null
            this.isLoggedIn = false
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
        },

        initFromStorage() {
            const token = localStorage.getItem('token')
            const userInfo = localStorage.getItem('userInfo')
            if (token && userInfo) {
                this.token = token
                this.userInfo = JSON.parse(userInfo)
                this.isLoggedIn = true
            }
        }
    }
})