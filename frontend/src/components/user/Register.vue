<template>
  <v-dialog v-model="isActive" max-width="500" persistent>
    <v-card title="Ошибка">
      <v-card-text class="text-center py-6">
        <v-icon icon="mdi-alert" color="error" size="64"></v-icon>
        <p class="text-h6 mt-4">
          Ошибка регистрации: попробуйте другой никнейм, возможно ваш уже занят
        </p>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="isActive = false"> Понял </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-parallax src="https://cdn.vuetifyjs.com/images/parallax/material.jpg">
    <div class="d-flex justify-center align-center" style="height: 60vh">
      <VSheet width="350" height="485" :elevation="16">
        <VForm fast-fail ref="form" @submit.prevent="submit">
          <VTextField
            class="mt-10 mx-auto"
            width="320"
            v-model="userName"
            :rules="userNameRules"
            label="Username"
            style="min-width: 50px; display: block"
          ></VTextField>

          <VTextField
            class="mx-auto"
            width="320"
            v-model="userEmail"
            :rules="emailRules"
            style="min-width: 50px; display: block"
            label="Email"
          ></VTextField>

          <VTextField
            class="mx-auto"
            width="320"
            v-model="userPassword"
            :rules="passwordRules"
            style="min-width: 50px; display: block"
            label="Password"
          ></VTextField>

          <VBtn
            :loading="loading"
            class="mt-2 mx-auto"
            color="green-lighten-1"
            text="Register"
            type="submit"
            style="min-width: 140px; display: block"
            rounded="lg"
            block
          ></VBtn>
        </VForm>
        <div class="d-flex flex-column justify-center align-center">
          <p class="ma-4">Already have an account?</p>
          <VBtn
            class="text-none mb-4"
            color="indigo-darken-3"
            variant="flat"
            style="min-width: 140px"
            rounded="lg"
            @click="router.push('/auth/login')"
          >
            LOG IN
          </VBtn>
        </div>
      </VSheet>
    </div>
  </v-parallax>
</template>

<script setup>
import router from "@/router/router";
import { useUserStore } from "@/stores/user";
import { ref } from "vue";
import { VBtn, VForm, VSheet, VTextField } from "vuetify/components";

const form = ref();
const loading = ref(false);
const userName = ref("");
const userPassword = ref("");
const userEmail = ref("");
const store = useUserStore();
const isActive = ref(false);
async function submit(event) {
  const { valid } = await form.value.validate();
  console.log(valid);
  if (valid) {
    loading.value = true;

    try {
      await store.register(userName.value, userEmail.value, userPassword.value);
      loading.value = false;
      router.push("/");
    } catch (error) {
      console.log(error);
      isActive.value = true;
      loading.value = false;
    }
  }
}

const userNameRules = [
  (value) => {
    if (value?.length >= 3) return true;
    return "userName must be at least 3 characters.";
  },
];

const emailRules = [
  (value) => {
    if (/.+@.+\..+/.test(value)) return true;
    return "E-mail must be valid.";
  },
];
const passwordRules = [
  (value) => {
    if (value?.length >= 6) return true;
    return "Password must be at least 6 characters.";
  },
];
</script>
