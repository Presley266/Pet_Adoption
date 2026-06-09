<template>
  <div class="form-container">
    <el-card>
      <template #header>
        <h2>申请领养 - {{ petName }}</h2>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="申请人姓名" prop="applicantName">
          <el-input v-model="form.applicantName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="applicantPhone">
          <el-input v-model="form.applicantPhone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="家庭地址" prop="applicantAddress">
          <el-input v-model="form.applicantAddress" type="textarea" rows="2" placeholder="请填写详细地址" />
        </el-form-item>

        <el-form-item label="申请理由" prop="applyReason">
          <el-input v-model="form.applyReason" type="textarea" rows="4" placeholder="请告诉我们为什么想领养这只宠物" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetDetail, submitAdoption } from '../api'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const petName = ref('')

const form = reactive({
  applicantName: '',
  applicantPhone: '',
  applicantAddress: '',
  applyReason: ''
})

const rules = {
  applicantName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  applicantPhone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  applicantAddress: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  applyReason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitting.value = true
  try {
    const res = await submitAdoption({
      petId: parseInt(route.params.petId),
      ...form
    })
    if (res.success) {
      ElMessage.success('申请提交成功，请等待审核')
      router.push('/user/applications')
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getPetDetail(route.params.petId)
    if (res.success) {
      petName.value = res.pet.name
    }
  } catch (error) {
    console.error('获取宠物信息失败', error)
  }
})
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 80px auto 40px;
  padding: 0 20px;
}
h2 {
  margin: 0;
}
</style>