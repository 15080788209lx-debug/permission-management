<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '@/api'

const logs = ref<any[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})
const loading = ref(false)

onMounted(() => {
  fetchLogs()
})

async function fetchLogs() {
  loading.value = true
  try {
    const response = await api.get('/logs', {
      params: {
        page: pagination.value.current - 1,
        size: pagination.value.pageSize
      }
    })
    const data = response.data.data
    logs.value = data.content || []
    pagination.value.total = data.totalElements || 0
  } catch (error) {
    console.error('Failed to fetch logs:', error)
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  pagination.value.current = page
  fetchLogs()
}

function getTypeClass(type: string) {
  switch (type) {
    case 'LOGIN': return 'bg-blue-100 text-blue-800'
    case 'OPERATION': return 'bg-green-100 text-green-800'
    case 'ERROR': return 'bg-red-100 text-red-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

function getTypeLabel(type: string) {
  switch (type) {
    case 'LOGIN': return '登录'
    case 'OPERATION': return '操作'
    case 'ERROR': return '错误'
    default: return type
  }
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm">
    <div class="p-6 border-b border-gray-100">
      <h2 class="text-lg font-semibold text-gray-800">系统日志</h2>
      <p class="text-sm text-gray-500 mt-1">查看系统操作记录</p>
    </div>
    
    <div class="p-6">
      <div v-if="loading" class="text-center py-8">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
      </div>
      
      <div v-else>
        <div class="space-y-3">
          <div 
            v-for="log in logs" 
            :key="log.id"
            class="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors"
          >
            <div class="flex items-start justify-between">
              <div class="flex items-center">
                <span 
                  class="px-2 py-1 text-xs rounded-full mr-3"
                  :class="getTypeClass(log.type)"
                >
                  {{ getTypeLabel(log.type) }}
                </span>
                <span class="font-medium text-gray-800">{{ log.operation }}</span>
              </div>
              <span class="text-xs text-gray-400">{{ formatDate(log.createdAt) }}</span>
            </div>
            
            <div class="mt-2 flex flex-wrap gap-4 text-sm text-gray-500">
              <span v-if="log.target">
                目标: <span class="text-gray-700">{{ log.target }}</span>
              </span>
              <span v-if="log.user">
                用户: <span class="text-gray-700">{{ log.user.username }}</span>
              </span>
              <span v-if="log.ip">
                IP: <span class="text-gray-700">{{ log.ip }}</span>
              </span>
            </div>
          </div>
          
          <div v-if="logs.length === 0" class="text-center py-12 text-gray-400">
            暂无日志记录
          </div>
        </div>
        
        <div class="flex justify-end mt-4">
          <button
            v-for="page in Math.min(5, Math.ceil(pagination.total / pagination.pageSize))"
            :key="page"
            @click="handlePageChange(page)"
            class="px-3 py-1 text-sm rounded transition-colors"
            :class="pagination.current === page 
              ? 'bg-primary text-white' 
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>