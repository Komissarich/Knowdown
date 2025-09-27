import { createMemoryHistory, createRouter } from 'vue-router'


import Lobby from '@/components/game/Lobby.vue'
import SearchLobby from '@/components/game/SearchLobby.vue'
import Me from '@/components/user/Me.vue'
import Home from '@/components/misc/Home.vue'
import Register from '@/components/user/Register.vue'
import Statistics from '@/components/statistics/Statistics.vue'
import Login from '@/components/user/Login.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/search', component: SearchLobby },
  { path: '/me', component: Me},
  { path: '/create_game', component: Lobby},
  { path: '/register', component: Register},
  { path: '/login', component: Login},
  { path: '/statistics', component: Statistics},
  { path: '/lobby', component: Lobby},

]

const router = createRouter({
  history: createMemoryHistory(),
  routes,
})

export default router