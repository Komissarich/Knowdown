import {
  createMemoryHistory,
  createRouter,
  createWebHistory,
} from "vue-router";

import Lobby from "@/components/game/Lobby.vue";
import SearchLobby from "@/components/game/SearchLobby.vue";
import Me from "@/components/user/Me.vue";
import Home from "@/components/misc/Home.vue";
import Register from "@/components/user/Register.vue";
import Statistics from "@/components/statistics/Statistics.vue";
import Login from "@/components/user/Login.vue";
import { useUserStore } from "@/stores/user";
import Profile from "@/components/user/Profile.vue";
import CreateLobby from "@/components/game/CreateLobby.vue";
import Arena from "@/components/game/Arena.vue";
import Game from "@/components/game/Game.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: { requireAuth: false, showNavbar: true },
  },
  { path: "/auth/register", component: Register, meta: { requireAuth: false } },
  { path: "/auth/login", component: Login, meta: { requireAuth: false } },
  { path: "/statistics", component: Statistics, meta: { requireAuth: true } },
  {
    path: "/lobby/:lobby_id",
    component: Lobby,
    meta: { requireAuth: true },
  },
  {
    path: "/lobby/search",
    component: SearchLobby,
    meta: { requireAuth: true },
  },
  {
    path: "/lobby/create",
    component: CreateLobby,
    meta: { requireAuth: true },
  },
  { path: "/users/me", component: Me, meta: { requireAuth: true } },
  { path: "/users/:id", component: Profile, meta: { requireAuth: true } },
  { path: "/game/:game_id", component: Game, meta: { requireAuth: true } },
  // { path: "/arena", component: Arena, meta: { requireAuth: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth == false) {
    next();
  } else {
    const store = useUserStore();
    if (to.meta.requireAuth == true && store.isLogged == false) {
      return next({ path: "/auth/login" });
    }
    if (!store.checkJWT()) {
      console.log("token expired");
      return next({ path: "/auth/login" });
    }
    next();
  }
});
export default router;
