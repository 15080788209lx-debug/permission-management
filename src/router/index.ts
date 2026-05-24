import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/layout/Layout.vue'),
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue')
        },
        {
          path: '/users',
          name: 'UserList',
          component: () => import('@/views/UserList.vue')
        },
        {
          path: '/users/create',
          name: 'UserCreate',
          component: () => import('@/views/UserForm.vue')
        },
        {
          path: '/users/:id',
          name: 'UserEdit',
          component: () => import('@/views/UserForm.vue')
        },
        {
          path: '/roles',
          name: 'RoleList',
          component: () => import('@/views/RoleList.vue')
        },
        {
          path: '/roles/create',
          name: 'RoleCreate',
          component: () => import('@/views/RoleForm.vue')
        },
        {
          path: '/roles/:id',
          name: 'RoleEdit',
          component: () => import('@/views/RoleForm.vue')
        },
        {
          path: '/permissions',
          name: 'PermissionList',
          component: () => import('@/views/PermissionList.vue')
        },
        {
          path: '/logs',
          name: 'SystemLog',
          component: () => import('@/views/SystemLog.vue')
        }
      ]
    }
  ]
})

export default router