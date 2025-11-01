<template>
  <div class="d-flex justify-center align-center" style="height: 60vh">
    <div class="text-center">
      <p>Insert a code to join the game</p>
      <v-text-field
        v-model:model-value="room_code"
        clearable
        width="300"
        label="Room code"
        variant="solo"
        density="compact"
        class="mt-5"
      ></v-text-field>

      <v-btn
        class=""
        color="indigo-darken-3"
        variant="flat"
        style="min-width: 140px"
        rounded="lg"
        @click="router.push('/lobby/' + room_code)"
      >
        Play
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import router from "@/router/router";
import { ref } from "vue";
const room_code = ref("");
</script>
