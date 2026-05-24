<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '@/api'
import { 
  UserOutlined, 
  TeamOutlined, 
  LockOutlined,
  BarChartOutlined
} from '@ant-design/icons-vue'

const stats = ref({
  totalUsers: 0,
  totalRoles: 0,
  totalPermissions: 0,
  activeUsers: 0
})

const recentActivities = ref<any[]>([])

onMounted(async () => {
  await fetchStats()
  await fetchRecentActivities()
})

async function fetchStats() {
  try {
    const response = await api.get('/dashboard/stats')
    stats.value = response.data.data
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
}

async function fetchRecentActivities() {
  try {
    const response = await api.get('/logs?page=0&size=5')
    recentActivities.value = response.data.data.content || []
  } catch (error) {
    console.error('Failed to fetch activities:', error)
  }
}

const statCards = [
  { label: '用户总数', value: 'totalUsers', icon: UserOutlined, color: 'bg-blue-500' },
  { label: '角色总数', value: 'totalRoles', icon: TeamOutlined, color: 'bg-green-500' },
  { label: '权限总数', value: 'totalPermissions', icon: LockOutlined, color: 'bg-purple-500' },
  { label: '活跃用户', value: 'activeUsers', icon: BarChartOutlined, color: 'bg-orange-500' }
]
</script>

<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div 
        v-for="card in statCards" 
        :key="card.label"
        class="bg-white rounded-xl shadow-sm p-6 flex items-center"
      >
        <div :class="[card.color, 'w-12 h-12 rounded-lg flex items-center justify-center mr-4']">
          <component :is="card.icon" class="w-6 h-6 text-white" />
        </div>
        <div>
          <p class="text-sm text-gray-500">{{ card.label }}</p>
          <p class="text-2xl font-bold text-gray-800">{{ stats[card.value as keyof typeof stats] }}</p>
        </div>
      </div>
    </div>
    
    <div class="bg-white rounded-xl shadow-sm p-6">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">最近活动</h2>
      <div class="space-y-3">
        <div 
          v-for="activity in recentActivities" 
          :key="activity.id"
          class="flex items-center justify-between py-3 border-b border-gray-100 last:border-0"
        >
          <div class="flex items-center">
            <div 
              class="w-8 h-8 rounded-full flex items-center justify-center mr-3 text-white text-sm"
              :class="{
                'bg-blue-500': activity.type === 'LOGIN',
                'bg-green-500': activity.type === 'OPERATION',
                'bg-red-500': activity.type === 'ERROR'
              }"
            >
              {{ activity.type?.charAt(0) }}
            </div>
            <div>
              <p class="text-sm font-medium text-gray-800">{{ activity.operation }}</p>
              <p class="text-xs text-gray-500">
                {{ activity.target }} · {{ activity.user?.username || '未知用户' }}
              </p>
            </div>
          </div>
          <span class="text-xs text-gray-400">
            {{ new Date(activity.createdAt).toLocaleString('zh-CN') }}
          </span>
        </div>
        
        <div v-if="recentActivities.length === 0" class="text-center py-8 text-gray-400">
          暂无活动记录
        </div>
      </div>
    </div>
  </div>
</template>