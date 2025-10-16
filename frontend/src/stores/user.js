import { defineStore } from "pinia";
import { ref } from "vue";
export const useUserStore = defineStore(
  "user",
  () => {
    const username = ref("");
    const email = ref("");
    const isLogged = ref(false);
    const avatar = ref("");
    const token = ref("");

    function register(name, email) {
      // Api register
      this.username = name;
      this.email = email;
      this.token = "test";
    }

    function login(name) {
      //Api login
      this.isLogged = true;
      this.username = name;
      this.token = "test";
    }
    function logout() {
      this.isLogged = false;
      token.value = null;
      username.value = null;
    }

    function checkJWT() {
      return true;
      // const decoded = jwtDecode(this.token);
      // const isExpired = decoded.exp < Date.now() / 1000;

      // if (isExpired) {
      // console.log("token has expired", isExpired)
      // const auth = authState
      // auth.logout()
      // return next({ path: "/auth" })
      //}
    }

    return {
      username,
      email,
      isLogged,
      avatar,
      token,
      register,
      login,
      logout,
      checkJWT,
    };
  },
  {
    persist: {
      storage: localStorage,
    },
  }
);
