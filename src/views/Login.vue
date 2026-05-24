<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { Button, Form, Input, message } from 'ant-design-vue'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) {
    message.error('请输入用户名和密码')
    return
  }
  
  loading.value = true
  
  try {
    await authStore.login(username.value, password.value)
    await authStore.fetchUserInfo()
    message.success('登录成功')
    router.push('/')
  } catch (err: any) {
    message.error(err.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-8">
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
          <LockOutlined class="w-8 h-8 text-primary" />
        </div>
        <h1 class="text-2xl font-bold text-gray-800">权限管理系统</h1>
        <p class="text-gray-500 mt-2">请登录您的账户</p>
      </div>
      
      <Form @submit.prevent="handleLogin" class="space-y-4">
        <Form.Item>
          <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
          <Input
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            :prefix="UserOutlined"
            class="w-full"
          />
        </Form.Item>
        
        <Form.Item>
          <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
          <Input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            :prefix="LockOutlined"
            class="w-full"
          />
        </Form.Item>
        
        <Button
          type="primary"
          html-type="submit"
          :loading="loading"
          class="w-full"
          size="large"
        >
          登录
        </Button>
      </Form>
      
      <div class="mt-6 text-center text-sm text-gray-500">
        默认账户：admin / admin123
      </div>
    </div>
  </div>
</template>
