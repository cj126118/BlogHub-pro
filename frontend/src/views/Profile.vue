<template>
  <div class="profile">
    <el-card shadow="never" class="profile-card">
      <template #header>
        <h2>个人中心</h2>
      </template>
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="设置昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="设置邮箱" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="介绍一下自己" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api/userApi'
import { ElMessage } from 'element-plus'

const saving = ref(false)
const form = reactive({ username: '', nickname: '', email: '', description: '' })

async function loadProfile() {
  try {
    const res = await userApi.getProfile()
    form.username = res.username || ''
    form.nickname = res.nickname || ''
    form.email = res.email || ''
    form.description = res.description || ''
  } catch {}
}

async function save() {
  saving.value = true
  try {
    await userApi.updateProfile({
      nickname: form.nickname,
      email: form.email,
      description: form.description,
    })
    ElMessage.success('保存成功')
  } catch {}
  finally { saving.value = false }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile { max-width: 600px; margin: 0 auto; }
.profile-card h2 { margin: 0; font-size: 20px; }
</style>
