<template>
  <div class="d-flex justify-center align-center" style="height: 30vh;">
    <div class="text-center">
        <p>Insert a code to join the game</p>
      <v-text-field
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
        style="min-width: 140px;"
        rounded="lg"
        @click="router.push('/lobby')"
      >
        Play
      </v-btn>
    </div>
  </div>
      <div class="d-flex justify-center align-center" style="height: 10vh;">
    <div class="text-center">
      <p>.....Or you can create a your own game</p>

        <v-btn
        class="mt-3"
        color="green-lighten-1"
        variant="flat"
        style="min-width: 140px;"
        rounded="lg"
        @click="router.push('/lobby')"
      >
        CREATE
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import router from '@/router/router';
import { useUserStore } from '@/stores/user';
const store = useUserStore()
onMounted(() => {
    store.initializeAuth()
  })
</script>