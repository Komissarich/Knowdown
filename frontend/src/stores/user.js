import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";

export const useUserStore = defineStore(
  "user",
  () => {
    const username = ref("");
    const email = ref("");
    const isLogged = ref(false);
    const avatar = ref("");
    const token = ref("");

    async function register(name, mail, password) {
      await axios
        .post("/api/auth/register", {
          username: name,
          password: password,
        })
        .then(function (response) {
          console.log(mail);
          username.value = name;
          email.value = mail.value;
          token.value = response.data.token;
          console.log(response.data);
          isLogged.value = true;
          if (token.value === null) {
            throw new Error("error");
          }
        })
        .catch(function (error) {
          if (error.response) {
            console.log(error.response.data); // => the response payload
          }
          throw error;
        });
    }

    async function login(name, password) {
      await axios
        .post("/api/auth/login", {
          username: name,
          password: password,
        })
        .then(function (response) {
          isLogged.value = true;
          username.value = name;
          token.value = response.data.token;
          console.log(response.data);
          if (token.value === null) {
            throw new Error("error");
          }
        })
        .catch(function (error) {
          if (error.response) {
            console.log(error.response.data); // => the response payload
          }

          throw error;
        });
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
