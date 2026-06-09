<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人资料</h2>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile } from '../api'

const formRef = ref()
const saving = ref(false)

const form = reactive({
  username: '',
  email: '',
  phone: '',
  address: ''
})

const rules = {
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const loadProfile = async () => {
  try {
    const res = await getUserProfile()
    if (res.success && res.user) {
      form.username = res.user.username || ''
      form.email = res.user.email || ''
      form.phone = res.user.phone || ''
      form.address = res.user.address || ''
    }
  } catch (error) {
    console.error('加载个人信息失败', error)
  }
}

const handleUpdate = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  saving.value = true
  try {
    await updateUserProfile({
      email: form.email,
      phone: form.phone,
      address: form.address
    })
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 80px auto 40px;
  padding: 0 20px;
}

.profile-card {
  border-radius: 16px;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
}
</style>