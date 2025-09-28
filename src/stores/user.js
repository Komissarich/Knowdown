import { defineStore } from 'pinia'
import { ref } from 'vue'
export const useUserStore = defineStore('counter', () => {
  const username = ref(0)
  const email = ref('Eduardo')
  const isLogged = ref(false)
  const avatar = ref('')
  
  function register(name, email) {
        // Api register
        this.username = name
        this.email = email
    }
    
    function login(name) {
        //Api login
        this.isLogged = true
        this.username = name
    }
    function logout(name) {
        this.isLogged = false
    }
  
    return { username, email, isLogged, avatar, register, login, logout}
})