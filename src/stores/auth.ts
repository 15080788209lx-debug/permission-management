import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/api'

export interface User {
  id: string
  username: string
  email: string
  roles: string[]
}

export interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  user: User | null
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const user = ref<User | null>(null)

  const isAuthenticated = computed(() => !!accessToken.value)

  async function login(username: string, password: string) {
    const response = await api.post('/auth/login', { username, password })
    const data = response.data.data
    
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    
    return data
  }

  async function logout() {
    await api.post('/auth/logout')
    
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  async function refreshTokenFn() {
    const response = await api.post('/auth/refresh', {
      refreshToken: refreshToken.value
    })
    const data = response.data.data
    
    accessToken.value = data.accessToken
    localStorage.setItem('accessToken', data.accessToken)
    
    return data
  }

  async function fetchUserInfo() {
    const response = await api.get('/auth/me')
    user.value = response.data.data
    return user.value
  }

  function setToken(token: string) {
    accessToken.value = token
    localStorage.setItem('accessToken', token)
  }

  return {
    accessToken,
    refreshToken,
    user,
    isAuthenticated,
    login,
    logout,
    refreshTokenFn,
    fetchUserInfo,
    setToken
  }
})