import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  timeout: 15000,
  withCredentials: true,
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem('user')
        window.location.href = '/login'
      } else if (error.response.status === 403) {
        alert('无权限访问')
      }
    }
    return Promise.reject(error)
  }
)
