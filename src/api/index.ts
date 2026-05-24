import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { useAuthStore } from '@/stores/auth'

const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const authStore = useAuthStore()
  const token = authStore.accessToken
  
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  
  return config
}, (error) => {
  return Promise.reject(error)
})

api.interceptors.response.use((response: AxiosResponse) => {
  return response
}, async (error) => {
  const originalRequest = error.config
  const authStore = useAuthStore()
  
  if (error.response?.status === 401 && !originalRequest._retry) {
    originalRequest._retry = true
    
    try {
      await authStore.refreshTokenFn()
      return api(originalRequest)
    } catch {
      authStore.logout()
      window.location.href = '/login'
    }
  }
  
  return Promise.reject(error)
})

export { api }