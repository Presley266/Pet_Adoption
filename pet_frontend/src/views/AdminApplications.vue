<template>
  <div class="admin-applications">
    <h2>申请审核</h2>

    <el-table :data="applications" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="petId" label="宠物ID" width="80" />
      <el-table-column prop="applicantName" label="申请人" width="120" />
      <el-table-column prop="applicantPhone" label="电话" width="130" />
      <el-table-column prop="applyReason" label="申请理由" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING'">
            <el-button type="success" size="small" @click="review(row.id, 'APPROVED')">通过</el-button>
            <el-button type="danger" size="small" @click="review(row.id, 'REJECTED')">拒绝</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllApplications, reviewApplication } from '../api'

const loading = ref(false)
const applications = ref([])

const getStatusType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
  return map[status] || status
}

const loadApplications = async () => {
  loading.value = true
  try {
    const res = await getAllApplications({})
    if (res.success) {
      applications.value = res.applications || []
    }
  } catch (error) {
    console.error('加载申请列表失败', error)
  } finally {
    loading.value = false
  }
}

const review = async (id, status) => {
  const text = status === 'APPROVED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${text}这个申请吗？`, '提示')
    await reviewApplication(id, status, '')
    ElMessage.success('操作成功')
    loadApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.admin-applications h2 {
  margin-bottom: 20px;
}
</style>