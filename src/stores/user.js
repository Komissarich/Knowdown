import { defineStore } from 'pinia'
import { ref } from 'vue'
export const useUserStore = defineStore('user', () => {
  const username = ref(0)
  const email = ref('Eduardo')
  const isLogged = ref(false)
  const avatar = ref('')
  const token = ref('')
  
  function initializeAuth() {
    console.log("checkAuth")
    const token = localStorage.getItem('userToken')
    const username = localStorage.getItem('userName')
    if (token && username) {
        this.token = token
        this.isLogged = true
        this.username = username
    }
  }

  function register(name, email) {
        // Api register
        this.username = name
        this.email = email
        this.token = 'test'
        localStorage.setItem('userToken', this.token);
    }
    
    function login(name) {
        //Api login
        this.isLogged = true
        this.username = name
        this.token = 'test'
        localStorage.setItem('userToken', this.token);
        localStorage.setItem('userName', name);
    }
    function logout() {
        this.isLogged = false
       
        localStorage.removeItem('userName');
        localStorage.removeItem('userToken');
    }

    function checkJWT() {
        return true
        // const decoded = jwtDecode(this.token);
        // const isExpired = decoded.exp < Date.now() / 1000;
        
        // if (isExpired) {
        // console.log("token has expired", isExpired)
        // const auth = authState
        // auth.logout()
        // return next({ path: "/auth" })
        //}
    }
  
    return { username, email, isLogged, avatar, token, register, login, logout, initializeAuth, checkJWT}
})