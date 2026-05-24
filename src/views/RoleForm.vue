<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '@/api'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  name: '',
  description: '',
  permissionCodes: [] as string[]
})

const permissions = ref<any[]>([])
const loading = ref(false)

onMounted(async () => {
  await fetchPermissions()
  
  if (isEdit.value) {
    await fetchRole(route.params.id as string)
  }
})

async function fetchPermissions() {
  try {
    const response = await api.get('/permissions/tree')
    permissions.value = response.data.data || []
  } catch (error) {
    console.error('Failed to fetch permissions:', error)
  }
}

async function fetchRole(id: string) {
  try {
    const response = await api.get(`/roles/${id}`)
    const role = response.data.data
    form.value = {
      name: role.name,
      description: role.description || '',
      permissionCodes: role.permissions?.map((p: any) => p.code) || []
    }
  } catch (error) {
    console.error('Failed to fetch role:', error)
  }
}

function togglePermission(code: string) {
  const index = form.value.permissionCodes.indexOf(code)
  if (index > -1) {
    form.value.permissionCodes.splice(index, 1)
  } else {
    form.value.permissionCodes.push(code)
  }
}

function toggleAllChildren(_parentCode: string, children: any[]) {
  const hasAll = children.every(child => form.value.permissionCodes.includes(child.code))
  
  if (hasAll) {
    children.forEach(child => {
      const index = form.value.permissionCodes.indexOf(child.code)
      if (index > -1) {
        form.value.permissionCodes.splice(index, 1)
      }
      if (child.children) {
        removeAllChildren(child.children)
      }
    })
  } else {
    children.forEach(child => {
      if (!form.value.permissionCodes.includes(child.code)) {
        form.value.permissionCodes.push(child.code)
      }
      if (child.children) {
        addAllChildren(child.children)
      }
    })
  }
}

function addAllChildren(children: any[]) {
  children.forEach(child => {
    if (!form.value.permissionCodes.includes(child.code)) {
      form.value.permissionCodes.push(child.code)
    }
    if (child.children) {
      addAllChildren(child.children)
    }
  })
}

function removeAllChildren(children: any[]) {
  children.forEach(child => {
    const index = form.value.permissionCodes.indexOf(child.code)
    if (index > -1) {
      form.value.permissionCodes.splice(index, 1)
    }
    if (child.children) {
      removeAllChildren(child.children)
    }
  })
}

async function handleSubmit() {
  if (!form.value.name) {
    alert('请填写角色名称')
    return
  }
  
  loading.value = true
  
  try {
    const data = {
      name: form.value.name,
      description: form.value.description,
      permissionCodes: form.value.permissionCodes
    }
    
    if (isEdit.value) {
      await api.put(`/roles/${route.params.id}`, data)
    } else {
      await api.post('/roles', data)
    }
    
    router.push('/roles')
  } catch (error) {
    console.error('Failed to save role:', error)
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  router.push('/roles')
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
          <h2 class="text-lg font-semibold text-gray-800">{{ isEdit ? '编辑角色' : '新建角色' }}</h2>
          <p class="text-sm text-gray-500 mt-1">{{ isEdit ? '修改角色信息' : '创建新角色' }}</p>
        </div>
      </div>
    </div>
    
    <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">角色名称</label>
          <input
            v-model="form.name"
            type="text"
            placeholder="请输入角色名称"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
            :disabled="isEdit"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">描述</label>
          <input
            v-model="form.description"
            type="text"
            placeholder="请输入角色描述"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
          />
        </div>
      </div>
      
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">权限列表</label>
        <div class="border border-gray-300 rounded-lg p-4 max-h-96 overflow-y-auto">
          <div v-for="permission in permissions" :key="permission.code" class="mb-2">
            <div 
              class="flex items-center p-2 rounded hover:bg-gray-50 cursor-pointer"
              :class="form.permissionCodes.includes(permission.code) ? 'bg-gray-50' : ''"
              @click="toggleAllChildren(permission.code, permission.children || [])"
            >
              <input 
                type="checkbox" 
                :checked="form.permissionCodes.includes(permission.code)"
                @change.stop="togglePermission(permission.code)"
                class="mr-3"
              />
              <span class="font-medium">{{ permission.name }}</span>
              <span class="text-xs text-gray-400 ml-2">({{ permission.code }})</span>
            </div>
            
            <div v-if="permission.children && permission.children.length > 0" class="ml-6">
              <div 
                v-for="child in permission.children" 
                :key="child.code"
                class="flex items-center p-2 rounded hover:bg-gray-50 cursor-pointer"
                :class="form.permissionCodes.includes(child.code) ? 'bg-gray-50' : ''"
              >
                <input 
                  type="checkbox" 
                  :checked="form.permissionCodes.includes(child.code)"
                  @change="togglePermission(child.code)"
                  class="mr-3"
                />
                <span>{{ child.name }}</span>
                <span class="text-xs text-gray-400 ml-2">({{ child.code }})</span>
              </div>
            </div>
          </div>
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
          {{ isEdit ? '保存修改' : '创建角色' }}
        </button>
      </div>
    </form>
  </div>
</template>