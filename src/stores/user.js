import { defineStore } from 'pinia'

export const useUserStore = defineStore('counter', {
  state: () => ({username: '', email: '',  isLogged: false, avatar: '' }),
  getters: {
    doubleCount: (state) => state.count * 2,
  },
  actions: {
    register(name, email) {
        // Api register
        this.username = name
        this.email = email
    },
    login(name, email) {
        //Api login
        this.isLogged = true
        this.username = name
        this.email = email
    },
    logout(name) {
        this.isLogged = false
    }
  },
})