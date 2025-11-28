<template>
  <v-container style="max-width: 900px">
    <v-row>
      <v-col cols="12" md="6">
        <v-card
          id="players_list"
          title="Players"
          style="height: 400px; overflow-y: auto"
        >
          <v-card-text>
            <template v-for="(player, index) in players" :key="player.id">
              <v-list-item
                :title="player.username"
                prepend-avatar="https://randomuser.me/api/portraits/men/78.jpg"
                v-bind="props"
              ></v-list-item>

              <v-divider v-if="index < players.length - 1" />
            </template>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card style="width: 300px">
          <div style="text-align: center">
            <h2 class="mx-auto">{{ route.params.lobbyName }}</h2>
          </div>

          <v-card-text class="medium font text-white-600">
            Room id: {{ route.params.lobby_id }}
            <hr />
            Link:
            <a href=""
              >https://knowdown/game/lobby/{{ route.params.lobby_id }}</a
            >
            <v-img
              :src="`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${route.params.lobby_id}`"
            ></v-img>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" md="6">
        <v-card style="height: 480px" class="opacity-90" id="chat">
          <v-card-title>Game chat</v-card-title>
          <v-card-text>
            <v-textarea
              append-inner-icon="mdi-comment"
              readonly
              v-model:model-value="chat"
              variant="solo"
              rows="12"
              class="mt-3"
              no-resize
            ></v-textarea>
            <div class="d-flex flex-row">
              <v-text-field
                v-model="message"
                label="Enter message"
                variant="outlined"
              ></v-text-field>
              <v-btn
                label="send"
                class="mt-2 ml-2"
                color="green"
                icon="mdi-send"
                @click="sendChatMessage"
              ></v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card style="width: 300px">
          <v-card-title>Actions</v-card-title>
          <v-card-actions>
            <v-btn color="primary" @click="playGame">START</v-btn>
            <v-btn color="error">EXIT</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
    <CountDown />
  </v-container>
</template>

<script setup>
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { onMounted, onUnmounted, ref } from "vue";
import { Client, Stomp } from "@stomp/stompjs";
import CountDown from "./CountDown.vue";
import axios from "axios";
import router from "@/router/router.js";
import { useLobbyStore } from "@/stores/lobby.js";

const userStore = useUserStore();

const players = ref([]);
const route = useRoute();
var chat = ref("");
const message = ref("");

async function playGame() {
  console.log(route.params.lobby_id, userStore.username);
  axios({
    method: "post",
    url: "/api/lobby/isCreator",
    data: {
      lobby_name: route.params.lobby_id,
      username: userStore.username,
    },
  })
    .then(function (response) {
      if (response.data == true) {
        axios({
          method: "post",
          url: "/api/lobby/start",
          params: {
            lobby_name: route.params.lobby_id,
            username: userStore.username,
          },
        }).catch(function (error) {
          console.log(error);
        });
      }
    })
    .catch(function (error) {
      console.log(error);
    });
}
const client = new Client({
  brokerURL: "/ws/",
  debug: function (str) {
    // console.log(str);
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
});

client.onStompError = function (frame) {
  console.error("Broker reported error:", frame.headers["message"]);
  console.error("Additional details:", frame.body);
};
client.onConnect = function (frame) {
  console.log("Connected to STOMP broker!");

  client.subscribe(
    "/topic/lobby/" + route.params.lobby_id + "/messages",
    function (message) {
      console.log("Received message:", message.body);
      var parsed_message = JSON.parse(message.body);
      chat.value +=
        parsed_message.author + ": " + parsed_message.message + "\n";
      console.log();
    }
  );

  client.subscribe(
    "/topic/lobby/" + route.params.lobby_id + "/player_list",
    function (message) {
      players.value = [];
      console.log("Received message:", message.body);
      var parsed_body = JSON.parse(message.body);
      for (let i = 0; i < parsed_body.length; i++) {
        var player = parsed_body[i];
        players.value.push(player);
      }
    }
  );
  client.subscribe(
    "/topic/lobby/" + route.params.lobby_id + "/countdown",
    function (message) {
      console.log("Received countdown:", message.body);
      var parsed_message = JSON.parse(message.body);
    }
  );
  updatePlayerList();
};
client.activate();

onUnmounted(() => {
  console.log("Client deactivated");
  client.deactivate();
});

function sendChatMessage() {
  console.log("try to send " + message.value);

  client.publish({
    destination: "/app/" + route.params.lobby_id + "/send_message",
    body: JSON.stringify({
      author: userStore.username,
      message: message.value,
    }),
  });
}

function updatePlayerList() {
  console.log("new_player " + message.value);
  client.publish({
    destination: "/app/" + route.params.lobby_id + "/join",
    body: JSON.stringify({ username: userStore.username }),
  });
}
</script>

<style lang="css" scoped>
#chat {
  background-color: rgb(123, 150, 190);
}

#chat:hover {
  background-color: rgb(80, 120, 180);
  transition: 400ms;
}

#players_list {
  padding: 10px;
  background-color: rgb(151, 204, 207);
}

#players_list:hover {
  background-color: rgb(76, 175, 175);
  box-shadow: 3px 3px 3px rgb(168, 168, 168);
  transition: 400ms;
}

.v-card .v-list-item {
  border: 2px solid #000000;
  border-radius: 7px !important;

  margin-top: 10px;
  background-color: aquamarine;
}

.v-card .v-list-item:hover {
  background-color: rgb(89, 202, 202);
  box-shadow: 10px 5px 10px 1px rgba(0, 0, 0, 0.2);
  transition: box-shadow 300ms;
}
</style>
