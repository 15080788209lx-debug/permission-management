<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  MenuFoldOutlined, 
  MenuUnfoldOutlined, 
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  LockOutlined,
  FileTextOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { api } from '@/api'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const collapsed = ref(false)
const userInfo = ref<any>(null)

const menuItems = [
  { key: '/', label: '仪表盘', icon: DashboardOutlined },
  { key: '/users', label: '用户管理', icon: UserOutlined },
  { key: '/roles', label: '角色管理', icon: TeamOutlined },
  { key: '/permissions', label: '权限管理', icon: LockOutlined },
  { key: '/logs', label: '系统日志', icon: FileTextOutlined }
]

onMounted(async () => {
  try {
    const response = await api.get('/auth/me')
    userInfo.value = response.data.data
  } catch (error) {
    console.error('Failed to fetch user info:', error)
  }
})

function toggleSidebar() {
  collapsed.value = !collapsed.value
}

function handleLogout() {
  authStore.logout().then(() => {
    router.push('/login')
  })
}
</script>

<template>
  <div class="flex h-screen bg-gray-100">
    <aside 
      class="bg-white border-r border-gray-200 transition-all duration-300 flex flex-col"
      :class="collapsed ? 'w-16' : 'w-64'"
    >
      <div class="h-16 flex items-center justify-between px-4 border-b border-gray-200">
        <div v-if="!collapsed" class="text-lg font-bold text-primary">权限管理</div>
        <div v-else class="text-lg font-bold text-primary">PM</div>
        <button 
          @click="toggleSidebar"
          class="p-1 hover:bg-gray-100 rounded transition-colors"
        >
          <MenuUnfoldOutlined v-if="collapsed" />
          <MenuFoldOutlined v-else />
        </button>
      </div>
      
      <nav class="flex-1 py-4">
        <ul class="space-y-1">
          <li v-for="item in menuItems" :key="item.key">
            <router-link
              :to="item.key"
              class="flex items-center px-4 py-3 text-sm transition-colors"
              :class="route.path === item.key 
                ? 'bg-primary/10 text-primary' 
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
            >
              <component :is="item.icon" class="w-5 h-5 mr-3" />
              <span v-if="!collapsed">{{ item.label }}</span>
            </router-link>
          </li>
        </ul>
      </nav>
      
      <div class="p-4 border-t border-gray-200">
        <div 
          v-if="!collapsed" 
          class="flex items-center justify-between cursor-pointer hover:bg-gray-50 p-2 rounded transition-colors"
          @click="handleLogout"
        >
          <div class="flex items-center">
            <div class="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center text-primary">
              {{ userInfo?.username?.charAt(0) || 'U' }}
            </div>
            <span class="ml-2 text-sm text-gray-600">{{ userInfo?.username }}</span>
          </div>
          <LogoutOutlined class="w-4 h-4 text-gray-400" />
        </div>
        <button 
          v-else 
          @click="handleLogout"
          class="w-full p-2 hover:bg-gray-50 rounded transition-colors"
          title="退出登录"
        >
          <LogoutOutlined class="w-5 h-5 text-gray-400 mx-auto" />
        </button>
      </div>
    </aside>
    
    <main class="flex-1 overflow-auto">
      <header class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
        <h1 class="text-xl font-semibold text-gray-800">
          {{ menuItems.find(item => item.key === route.path)?.label || '仪表盘' }}
        </h1>
        <div class="text-sm text-gray-500">
          {{ new Date().toLocaleDateString('zh-CN') }}
        </div>
      </header>
      
      <div class="p-6">
        <router-view />
      </div>
    </main>
  </div>
</template>