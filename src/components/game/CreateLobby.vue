<template> 
<div class="d-flex justify-center align-center" style="height: 60vh;">
<v-card style="width: 390px;" color="blue-grey-lighten-5">
   
    <v-card-title>Game settings</v-card-title>
    <v-text-field label="Room name" max-width="365" class="ml-2" variant="outlined" v-model="lobbyName"></v-text-field>
    <v-range-slider
    v-model="maxPlayers"
    color="light-green-lighten-4"
    max-width="350"
    :max="4"
    :min="1"
    prepend-icon="mdi-account"
    step="1"
    label="Player count"
    thumb-label="true"
  ></v-range-slider>
  <v-switch label="Private" class="ml-3" color="indigo" v-model="isPrivate"></v-switch>
  <v-btn @click="createLobby" color="green" class="mx-auto mb-10" style="min-width: 140px;display: block;">CREATE</v-btn>

</v-card>



</div>







</template>

<script setup>
  import router from '@/router/router'
import { ref } from 'vue'
  const isPrivate = ref(false)
  const maxPlayers = ref(30)
  
  const lobbyName = ref('')
  function createLobby() {
    const lobbyId = crypto.randomUUID().substring(0, 6)
    console.log(maxPlayers.value)
    router.push({path: '/game/lobby/' + lobbyId,
    query: {lobbyId: lobbyId, lobbyName: lobbyName.value, isPrivate: isPrivate.value, maxPlayers:maxPlayers.value[1] - 1}})
  }
</script>