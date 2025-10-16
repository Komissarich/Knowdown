<template>
  <div class="d-flex justify-center align-center" style="height: 60vh">
    <v-card style="width: 390px" color="blue-grey-lighten-5">
      <v-card-title>Game settings</v-card-title>
      <v-text-field
        label="Room name"
        max-width="365"
        class="ml-2"
        variant="outlined"
        v-model="lobbyName"
      ></v-text-field>
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
      <v-switch
        label="Private"
        class="ml-3"
        color="indigo"
        v-model="isPrivate"
      ></v-switch>
      <v-btn
        @click="createLobby"
        color="green"
        class="mx-auto mb-10"
        style="min-width: 140px; display: block"
        >CREATE</v-btn
      >
    </v-card>
  </div>
</template>

<script setup>
import router from "@/router/router";
import { useLobbyStore } from "@/stores/lobby";
import { useUserStore } from "@/stores/user";
import axios from "axios";
import { ref } from "vue";
const isPrivate = ref(false);
const maxPlayers = ref(30);
const userStore = useUserStore();
const lobbyStore = useLobbyStore();
const lobbyName = ref("");

function createLobby() {
  console.log(
    maxPlayers.value[1],
    lobbyName.value,
    userStore.username,
    isPrivate.value
  );
  axios({
    method: "post",
    url: "http://localhost:8081/game/lobby/create",
    data: {
      isPrivate: isPrivate.value,
      maxPlayers: maxPlayers.value[1],
      name: lobbyName.value,
      creator: userStore.username,
    },
  })
    .then(function (response) {
      console.log(response.data.lobbyId);
      lobbyStore.createLobby(
        response.data.lobbyId,
        lobbyName.value,
        isPrivate.value,
        maxPlayers.value[1]
      );
      router.push({ path: "/game/lobby/" + response.data.lobbyId });
    })
    .catch(function (error) {
      console.log(error);
    });
}
</script>
