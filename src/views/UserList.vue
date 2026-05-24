<script setup lang="ts">import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { api } from '@/api';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue';
const router = useRouter();
const users = ref<any[]>([]);
const pagination = ref({
 current: 1,
 pageSize: 10,
 total: 0
});
const searchKeyword = ref('');
const loading = ref(false);
onMounted(() => {
 fetchUsers();
});
async function fetchUsers() {
 loading.value = true;
 try {
 const response = await api.get('/users', {
 params: {
 page: pagination.value.current - 1,
 size: pagination.value.pageSize,
 keyword: searchKeyword.value
 }
 });
 const data = response.data.data;
 users.value = data.content || [];
 pagination.value.total = data.totalElements || 0;
 }
 catch (error) {
 console.error('Failed to fetch users:', error);
 }
 finally {
 loading.value = false;
 }
}
function handleSearch() {
 pagination.value.current = 1;
 fetchUsers();
}
function handlePageChange(page: number) {
 pagination.value.current = page;
 fetchUsers();
}
function handleCreate() {
 router.push('/users/create');
}
function handleEdit(id: string) {
 router.push(`/users/${id}`);
}
async function handleDelete(id: string, username: string) {
 if (!confirm(`确定要删除用户 ${username} 吗？`))
 return;
 try {
 await api.delete(`/users/${id}`);
 fetchUsers();
 }
 catch (error) {
 console.error('Failed to delete user:', error);
 }
}
function formatDate(dateStr: string) {
 return new Date(dateStr).toLocaleDateString('zh-CN');
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm">
    <div class="flex items-center justify-between p-6 border-b border-gray-100">
      <div>
        <h2 class="text-lg font-semibold text-gray-800">用户列表</h2>
        <p class="text-sm text-gray-500 mt-1">管理系统用户</p>
      </div>
      <button
        @click="handleCreate"
        class="flex items-center px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors"
      >
        <PlusOutlined class="w-4 h-4 mr-2" />
        新建用户
      </button>
    </div>
    
    <div class="p-6">
      <div class="flex mb-4">
        <div class="relative flex-1 max-w-md">
          <SearchOutlined class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索用户名或邮箱..."
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/50 focus:border-primary outline-none"
            @keyup.enter="handleSearch"
          />
        </div>
        <button
          @click="handleSearch"
          class="ml-3 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
        >
          搜索
        </button>
      </div>
      
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="bg-gray-50">
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">用户名</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">邮箱</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">角色</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">状态</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">创建时间</th>
              <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
              <td class="px-4 py-4">{{ user.username }}</td>
              <td class="px-4 py-4 text-gray-600">{{ user.email }}</td>
              <td class="px-4 py-4">
                <span 
                  v-for="role in user.roles" 
                  :key="role"
                  class="inline-block px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded mr-1"
                >
                  {{ role }}
                </span>
                <span v-if="!user.roles || user.roles.length === 0" class="text-xs text-gray-400">无角色</span>
              </td>
              <td class="px-4 py-4">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs rounded-full"
                  :class="user.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                >
                  {{ user.status === 'ACTIVE' ? '活跃' : '禁用' }}
                </span>
              </td>
              <td class="px-4 py-4 text-gray-500 text-sm">{{ formatDate(user.createdAt) }}</td>
              <td class="px-4 py-4 text-right">
                <button 
                  @click="handleEdit(user.id)"
                  class="inline-flex items-center px-2 py-1 text-blue-600 hover:text-blue-700"
                  title="编辑"
                >
                  <EditOutlined class="w-4 h-4" />
                </button>
                <button 
                  @click="handleDelete(user.id, user.username)"
                  class="inline-flex items-center px-2 py-1 text-red-600 hover:text-red-700 ml-2"
                  title="删除"
                >
                  <DeleteOutlined class="w-4 h-4" />
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-if="users.length === 0" class="text-center py-12 text-gray-400">
          暂无数据
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
</template>