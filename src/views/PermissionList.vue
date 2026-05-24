<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '@/api'

const permissions = ref<any[]>([])
const loading = ref(false)

onMounted(() => {
  fetchPermissions()
})

async function fetchPermissions() {
  loading.value = true
  try {
    const response = await api.get('/permissions/tree')
    permissions.value = response.data.data || []
  } catch (error) {
    console.error('Failed to fetch permissions:', error)
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm">
    <div class="p-6 border-b border-gray-100">
      <h2 class="text-lg font-semibold text-gray-800">权限列表</h2>
      <p class="text-sm text-gray-500 mt-1">系统权限管理</p>
    </div>
    
    <div class="p-6">
      <div v-if="loading" class="text-center py-8">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
      </div>
      
      <div v-else class="space-y-4">
        <div 
          v-for="permission in permissions" 
          :key="permission.id"
          class="border border-gray-200 rounded-lg overflow-hidden"
        >
          <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
            <div class="flex items-center">
              <span class="font-medium text-gray-800">{{ permission.name }}</span>
              <span class="text-xs text-gray-400 ml-3">({{ permission.code }})</span>
            </div>
            <span class="text-xs text-gray-500">{{ formatDate(permission.createdAt) }}</span>
          </div>
          
          <div v-if="permission.children && permission.children.length > 0" class="divide-y divide-gray-100">
            <div 
              v-for="child in permission.children" 
              :key="child.id"
              class="px-8 py-3 flex items-center justify-between hover:bg-gray-50"
            >
              <div>
                <span class="text-gray-700">{{ child.name }}</span>
                <span class="text-xs text-gray-400 ml-3">({{ child.code }})</span>
              </div>
              <span class="text-xs text-gray-500">{{ formatDate(child.createdAt) }}</span>
            </div>
          </div>
          
          <div v-if="!permission.children || permission.children.length === 0" class="px-8 py-3 text-gray-400 text-sm">
            无子权限
          </div>
        </div>
        
        <div v-if="permissions.length === 0" class="text-center py-12 text-gray-400">
          暂无权限数据
        </div>
      </div>
    </div>
  </div>
</template>