<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '@/api'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  status: 'ACTIVE',
  roleIds: [] as string[]
})

const roles = ref<any[]>([])
const loading = ref(false)

onMounted(async () => {
  await fetchRoles()
  
  if (isEdit.value) {
    await fetchUser(route.params.id as string)
  }
})

async function fetchRoles() {
  try {
    const response = await api.get('/roles')
    roles.value = response.data.data.content || []
  } catch (error) {
    console.error('Failed to fetch roles:', error)
  }
}

async function fetchUser(id: string) {
  try {
    const response = await api.get(`/users/${id}`)
    const user = response.data.data
    const userRoleNames = user.roles || []
    const selectedRoleIds = roles.value
      .filter(role => userRoleNames.includes(role.name))
      .map(role => role.id)
    
    form.value = {
      username: user.username,
      email: user.email,
      password: '',
      confirmPassword: '',
      status: user.status,
      roleIds: selectedRoleIds
    }
  } catch (error) {
    console.error('Failed to fetch user:', error)
  }
}

async function handleSubmit() {
  if (!form.value.username || !form.value.email) {
    alert('请填写用户名和邮箱')
    return
  }
  
  if (form.value.username.length < 3 || form.value.username.length > 50) {
    alert('用户名长度必须在3-50个字符之间')
    return
  }
  
  if (!isEdit.value && (!form.value.password || form.value.password !== form.value.confirmPassword)) {
    alert('密码必须填写且两次输入一致')
    return
  }
  
  if (!isEdit.value && form.value.password && form.value.password.length < 8) {
    alert('密码长度不能少于8位')
    return
  }
  
  if (!form.value.roleIds || form.value.roleIds.length === 0) {
    alert('请至少选择一个角色')
    return
  }
  
  loading.value = true
  
  try {
    const data = {
      username: form.value.username,
      email: form.value.email,
      password: form.value.password || undefined,
      status: form.value.status,
      roleIds: form.value.roleIds
    }
    
    if (isEdit.value) {
      await api.put(`/users/${route.params.id}`, data)
    } else {
      await api.post('/users', data)
    }
    
    router.push('/users')
  } catch (error) {
    console.error('Failed to save user:', error)
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  router.push('/users')
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm">
    <div class="flex items-center justify-between p-6 border-b border-gray-100">
      <div class="flex items-center">
        <button 
          @click="handleCancel"
          class="mr-4 p-1 hover:bg-gray-100 rounded transition-colors"
        >
          <ArrowLeftOutlined class="w-5 h-5 text-gray-600" />
        </button>
        <div>
          <h2 class="text-lg font-semibold text-gray-800">{{ isEdit ? '编辑用户' : '新建用户' }}</h2>
          <p class="text-sm text-gray-500 mt-1">{{ isEdit ? '修改用户信息' : '创建新用户' }}</p>
        </div>
      </div>
    </div>
    
    <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
            :disabled="isEdit"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">邮箱</label>
          <input
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
          />
        </div>
        
        <div v-if="!isEdit">
          <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
          />
        </div>
        
        <div v-if="!isEdit">
          <label class="block text-sm font-medium text-gray-700 mb-2">确认密码</label>
          <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">状态</label>
          <select
            v-model="form.status"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
          >
            <option value="ACTIVE">活跃</option>
            <option value="INACTIVE">禁用</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">角色</label>
          <div class="flex flex-wrap gap-2">
            <label 
              v-for="role in roles" 
              :key="role.id"
              class="flex items-center px-3 py-2 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50 hover:border-primary transition-colors"
              :class="{ 'bg-primary/10 border-primary text-primary': form.roleIds.includes(role.id) }"
            >
              <input 
                type="checkbox" 
                :value="role.id"
                v-model="form.roleIds"
                class="mr-2 w-4 h-4 rounded border-gray-300 text-primary focus:ring-primary/50"
              />
              <span class="text-sm">{{ role.name }}</span>
            </label>
          </div>
          <p v-if="roles.length === 0" class="text-xs text-gray-400 mt-2">暂无角色数据</p>
        </div>
      </div>
      
      <div class="flex justify-end space-x-3 pt-4 border-t border-gray-100">
        <button
          type="button"
          @click="handleCancel"
          class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
        >
          取消
        </button>
        <button
          type="submit"
          :loading="loading"
          class="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors"
        >
          {{ isEdit ? '保存修改' : '创建用户' }}
        </button>
      </div>
    </form>
  </div>
</template>