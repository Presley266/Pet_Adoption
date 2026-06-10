<template>
  <div class="admin-pets">
    <div class="header">
      <h2>宠物管理</h2>
      <el-button type="primary" @click="openDialog()">添加宠物</el-button>
    </div>

    <el-table :data="pets" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" width="120" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img :src="row.imageUrl || '/placeholder.jpg'" class="table-img" />
        </template>
      </el-table-column>
      <el-table-column prop="category" label="种类" width="100">
        <template #default="{ row }">{{ getCategoryText(row.category) }}</template>
      </el-table-column>
      <el-table-column prop="breed" label="品种" width="120" />
      <el-table-column prop="age" label="年龄(月)" width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">{{ row.gender === 'MALE' ? '公' : '母' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'AVAILABLE' ? 'success' : 'danger'">
            {{ row.status === 'AVAILABLE' ? '可领养' : '已领养' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="种类" required>
          <el-select v-model="form.category">
            <el-option label="猫咪" value="CAT" />
            <el-option label="狗狗" value="DOG" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="form.breed" />
        </el-form-item>
        <el-form-item label="年龄(月)">
          <el-input-number v-model="form.age" :min="0" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="MALE">公</el-radio>
            <el-radio label="FEMALE">母</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="健康状态">
          <el-input v-model="form.healthStatus" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="/images/pets/xxx.jpg" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllPets, addPet, updatePet, deletePet } from '../api'

const loading = ref(false)
const pets = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)

const form = reactive({
  name: '',
  category: 'CAT',
  breed: '',
  age: 0,
  gender: 'MALE',
  description: '',
  healthStatus: '健康',
  imageUrl: ''
})

const dialogTitle = computed(() => editingId.value ? '编辑宠物' : '添加宠物')

const getCategoryText = (category) => {
  const map = { DOG: '狗狗', CAT: '猫咪', OTHER: '其他' }
  return map[category] || category
}

const loadPets = async () => {
  loading.value = true
  try {
    const res = await getAllPets()
    if (res.success) {
      pets.value = res.pets || []
    }
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  if (row) {
    editingId.value = row.id
    Object.assign(form, row)
  } else {
    editingId.value = null
    Object.assign(form, {
      name: '', category: 'CAT', breed: '', age: 0,
      gender: 'MALE', description: '', healthStatus: '健康', imageUrl: ''
    })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (editingId.value) {
      await updatePet(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await addPet(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadPets()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除吗？', '提示')
    await deletePet(id)
    ElMessage.success('删除成功')
    loadPets()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadPets()
})
</script>

<style scoped>
.admin-pets .header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.admin-pets h2 {
  margin: 0;
}
.table-img {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 8px;
  background: #f0f0f0;
}
</style>