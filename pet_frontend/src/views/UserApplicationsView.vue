<template>
  <div class="applications-container">
    <h2>我的领养申请</h2>

    <el-table :data="applications" v-loading="loading" stripe>
      <el-table-column prop="id" label="申请编号" width="100" />
      <el-table-column prop="petId" label="宠物ID" width="100" />
      <el-table-column prop="applicantName" label="姓名" width="120" />
      <el-table-column prop="applicantPhone" label="电话" width="130" />
      <el-table-column prop="applyReason" label="申请理由" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template v-slot:default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="170">
        <template v-slot:default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && applications.length === 0" description="暂无申请记录" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyAdoptions } from '../api'

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

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${d.getMonth()+1}-${d.getDate()} ${d.getHours()}:${d.getMinutes()}`
}

const loadApplications = async () => {
  loading.value = true
  try {
    const res = await getMyAdoptions()
    if (res.success) {
      applications.value = res.applications || []
    }
  } catch (error) {
    console.error('加载申请列表失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.applications-container {
  max-width: 1200px;
  margin: 80px auto 40px;
  padding: 0 20px;
}
.applications-container h2 {
  margin-bottom: 24px;
}
</style>