import { createMemoryHistory, createRouter } from 'vue-router'


import Lobby from '@/components/game/Lobby.vue'
import SearchLobby from '@/components/game/SearchLobby.vue'
import Me from '@/components/user/Me.vue'
import Home from '@/components/misc/Home.vue'
import Register from '@/components/user/Register.vue'
import Statistics from '@/components/statistics/Statistics.vue'
import Login from '@/components/user/Login.vue'
import { useUserStore } from '@/stores/user'
import Profile from '@/components/user/Profile.vue'
import CreateLobby from '@/components/game/CreateLobby.vue'

const routes = [
  { path: '/', component: Lobby, meta: {requireAuth: false}},
  { path: '/auth/register', component: Register, meta: {requireAuth: false}},
  { path: '/auth/login', component: Login, meta: {requireAuth: false}},
  { path: '/statistics', component: Statistics, meta: {requireAuth: true}},
  { path: '/game/lobby/:lobby_id', component: Lobby, meta: {requireAuth: true}},
  { path: '/game/search', component: SearchLobby, meta: {requireAuth: true} },
  { path: '/game/create_game', component: CreateLobby, meta: {requireAuth: true}},
  { path: '/users/me', component: Me, meta: {requireAuth: true}},
  { path: '/users/:id', component: Profile, meta: {requireAuth: true}},

]


const router = createRouter({
  history: createMemoryHistory(),
  routes,
})



router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth == false) {
        next()
    }
  else {
    next()
    const store = useUserStore();
   
    if (to.meta.requireAuth == true && store.isLogged == false) {
      
          return next({ path: "/login" })
    }
    next()
  }


})
export default router