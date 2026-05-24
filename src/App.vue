<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

router.beforeEach((to, _from, next) => {
  const isAuthenticated = authStore.isAuthenticated
  
  if (to.path === '/login') {
    if (isAuthenticated) {
      next('/')
    } else {
      next()
    }
  } else {
    if (isAuthenticated) {
      next()
    } else {
      next('/login')
    }
  }
})
</script>

<template>
  <router-view />
</template>