import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref<any>(localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')!) : null)
  
  function login(userData: any) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function logout() {
    user.value = null
    localStorage.removeItem('user')
  }

  return { user, login, logout }
})
