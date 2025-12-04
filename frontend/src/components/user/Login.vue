<template>
  <v-dialog v-model="isActive" max-width="500" persistent>
    <v-card title="Ошибка">
      <v-card-text class="text-center py-6">
        <v-icon icon="mdi-alert" color="error" size="64"></v-icon>
        <p class="text-h6 mt-4">Ошибка входа: неправильный пароль или логин</p>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="isActive = false"> Понял </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <div class="d-flex justify-center align-center" style="height: 60vh">
    <v-sheet width="350" height="450" :elevation="16">
      <v-form ref="form" fast-fail @submit.prevent="submit">
        <v-text-field
          class="mt-10 mx-auto"
          width="320"
          v-model="userName"
          :rules="userNameRules"
          label="Username"
          style="min-width: 50px; display: block"
        ></v-text-field>

        <v-text-field
          class="mx-auto"
          width="320"
          v-model="userPassword"
          :rules="passwordRules"
          style="min-width: 50px; display: block"
          label="Password"
        ></v-text-field>

        <v-btn
          :loading="loading"
          class="mt-2 mx-auto"
          text="Login"
          type="submit"
          color="green-lighten-1"
          style="min-width: 140px; display: block"
          rounded="lg"
        ></v-btn>
      </v-form>
      <div class="d-flex flex-column justify-center align-center">
        <p class="ma-4">Doesn't have an account?</p>
        <v-btn
          class="text-none mb-4"
          color="indigo-darken-3"
          variant="flat"
          style="min-width: 140px"
          rounded="lg"
          @click="router.push('/auth/register')"
        >
          REGISTER
        </v-btn>
      </div>
    </v-sheet>
  </div>
</template>

<script setup>
import router from "@/router/router";
import { useUserStore } from "@/stores/user";
import { onMounted, ref } from "vue";
const form = ref();
const logAlert = ref(false);
const loading = ref(false);
const userName = ref("");
const userPassword = ref("");
const isActive = ref(false);
const store = useUserStore();
async function submit(event) {
  const { valid } = await form.value.validate();

  if (valid) {
    loading.value = true;
    const results = await event;

    try {
      await store.login(userName.value, userPassword.value);
      router.push("/");
      loading.value = false;
    } catch (error) {
      console.log(error);
      isActive.value = true;
      loading.value = false;
    }
  } else {
    logAlert.value = !logAlert.value;
  }
}

const userNameRules = [
  (value) => {
    if (value?.length >= 3) return true;
    return "Username must be at least 3 characters.";
  },
];

const passwordRules = [
  (value) => {
    if (value?.length >= 3) return true;
    return "Password must be at least 3 characters.";
  },
];
</script>
