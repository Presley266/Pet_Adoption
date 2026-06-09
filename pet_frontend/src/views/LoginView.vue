<template>
  <div class="login-page">
    <!-- 左侧背景图区域 -->
    <div class="bg-image"></div>

    <!-- 右侧表单区域 -->
    <div class="form-container">
      <div class="form-card">
        <h2>欢迎回来</h2>
        <p class="subtitle">登录你的账号</p>

        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="User"
                size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                size="large"
                @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
                type="primary"
                @click="handleLogin"
                :loading="loading"
                class="login-btn"
                size="large"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="footer-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  loading.value = true
  const result = await userStore.login(form.username, form.password)
  loading.value = false

  if (result.success) {
    ElMessage.success('登录成功')
    router.push('/')
  } else {
    ElMessage.error(result.message || '登录失败')
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  width: 100%;
  min-height: 100vh;
}

/* 左侧背景图 */
.bg-image {
  flex: 1.5;
  background-image: url('./images/login.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
}

.bg-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0.1) 100%);
}

/* 右侧表单 */
.form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 40px;
}

.form-card {
  width: 100%;
  max-width: 400px;
}

.form-card h2 {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.form-card .subtitle {
  color: #888;
  margin-bottom: 32px;
}

.login-btn {
  width: 100%;
  background: #ff8c5a;
  border-color: #ff8c5a;
}

.login-btn:hover {
  background: #e57a4a;
  border-color: #e57a4a;
}

.footer-link {
  text-align: center;
  margin-top: 24px;
  color: #666;
  font-size: 14px;
}

.footer-link a {
  color: #ff8c5a;
  text-decoration: none;
}

.footer-link a:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 768px) {
  .bg-image {
    display: none;
  }
  .form-container {
    flex: 1;
  }
}
</style>