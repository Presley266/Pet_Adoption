<template>
  <div class="admin-users">
    <h2>用户管理</h2>

    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
            {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
            {{ row.status === 'NORMAL' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="toggleRole(row)">
            {{ row.role === 'ADMIN' ? '设为普通用户' : '设为管理员' }}
          </el-button>
          <el-button :type="row.status === 'NORMAL' ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
            {{ row.status === 'NORMAL' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllUsers, updateUserRole, updateUserStatus } from '../api'

const loading = ref(false)
const users = ref([])

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    if (res.success) {
      users.value = res.users || []
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const toggleRole = async (user) => {
  const newRole = user.role === 'ADMIN' ? 'USER' : 'ADMIN'
  try {
    await updateUserRole(user.id, newRole)
    ElMessage.success('角色修改成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 'NORMAL' ? 'DISABLED' : 'NORMAL'
  try {
    await updateUserStatus(user.id, newStatus)
    ElMessage.success('状态修改成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users h2 {
  margin-bottom: 20px;
}
</style>