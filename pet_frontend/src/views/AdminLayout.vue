<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <div class="logo">管理后台</div>
      <el-menu :default-active="activeMenu" router>
        <el-menu-item index="/admin/pets">
          <el-icon><Goods /></el-icon>
          <span>宠物管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/applications">
          <el-icon><Document /></el-icon>
          <span>申请审核</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Goods, User, Document } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: calc(100vh - 60px);
  background: #f0f2f5;
}
.admin-sidebar {
  width: 220px;
  background: #304156;
  color: white;
  position: fixed;
  left: 0;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
}
.admin-sidebar .logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #4a5a6e;
}
.admin-sidebar :deep(.el-menu) {
  border-right: none;
  background: #304156;
}
.admin-sidebar :deep(.el-menu-item) {
  color: #bfcbd9;
}
.admin-sidebar :deep(.el-menu-item:hover) {
  background: #263445;
  color: white;
}
.admin-sidebar :deep(.el-menu-item.is-active) {
  background: #409eff;
  color: white;
}
.admin-content {
  flex: 1;
  margin-left: 220px;
  padding: 20px;
  margin-top: 60px;
}
</style>