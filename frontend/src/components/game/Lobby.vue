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
        <v-card style="width: 350px">
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

          <v-range-slider
            v-model="questionCount"
            color="light-green-lighten-4"
            max-width="350"
            :max="20"
            :min="5"
            prepend-icon="mdi-account"
            step="1"
            label="Question count"
            thumb-label="true"
          ></v-range-slider>
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
        <v-card style="width: 350px">
          <v-card-title>Actions</v-card-title>
          <v-card-actions>
            <v-btn color="primary" @click="playGame">START</v-btn>
            <v-btn color="error">EXIT</v-btn>
          </v-card-actions>
          <v-combobox
            v-model="chips"
            :items="items"
            label="Categories: "
            variant="solo"
            chips
            clearable
            closable-chips
            multiple
            class="mx-4"
          >
            <template v-slot:chip="{ props, item }">
              <v-chip v-bind="props">
                <strong>{{ item.raw }}</strong
                >&nbsp;
              </v-chip>
            </template>
          </v-combobox>
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
const questionCount = ref(2);

const players = ref([]);
const route = useRoute();
var chat = ref("");
const message = ref("");
const items = [
  "General Knowledge",
  "Books",
  "Movies",
  "Music",
  "Television",
  "Video Games",
  "Board Games",
  "Science & Nature",
  "Mathematics",
  "Mythology",
  "Sports",
  "Geography",
  "History",
  "Politics",
  "Art",
  "Animals",
  "Celebrities",
  "Vehicles",
  "Celebrities",
  "Anime",
  "Cartoons",
];

const chips = ref([
  "General Knowledge",
  "Books",
  "Movies",
  "Music",
  "Theatres",
  "Television",
  "Video Games",
  "Board Games",
  "Science & Nature",
  "Computers",
  "Mathematics",
  "Mythology",
  "Sports",
  "Geography",
  "History",
  "Politics",
  "Art",
  "Animals",
  "Gadgets",
  "Celebrities",
  "Vehicles",
  "Celebrities",
  "Anime",
  "Cartoons",
]);

const map = new Map();
map.set("General Knowledge", "General Knowledge");
map.set("Books", "Entertainment: Books");
map.set("Movies", "Entertainment: Film");
map.set("Music", "Entertainment: Music");
map.set("Theatres", "Entertainment: Musicals & Theatres");
map.set("Television", "Entertainment: Television");
map.set("Board Games", "Entertainment: Board Games");
map.set("Video Games", "Entertainment: Video Games");
map.set("Science & Nature", "Science & Nature");
map.set("Computers", "Science: Computers");
map.set("Mathematics", "Science: Mathematics");
map.set("Gadgets", "Science: Gadgets");
map.set("Anime", "Entertainment: Japanese Anime & Manga");
map.set("Cartoons", "Entertainment: Cartoon & Animations");

async function playGame() {
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
        const categories = chips.value.map((cat) => map.get(cat) || cat);
        console.log("cats", categories, "amount", questionCount);
        axios({
          method: "post",
          url: "/api/lobby/" + route.params.lobby_id + "/get_questions",
          data: {
            categories: chips.value.map((cat) => map.get(cat) || cat),
            amount: parseInt(questionCount.value),
          },
          params: {
            lobbyId: route.params.lobby_id,
          },
        })
          .then(function (response) {
            console.log(response.data);
            localStorage.setItem(
              "question_count",
              parseInt(questionCount.value)
            );
          })
          .catch(function (error) {
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

  client.subscribe(
    "/topic/lobby/" + route.params.lobby_id + "/get_questions",
    function (message) {
      console.log("Received questions:", message.body);
      var parsed_message = JSON.parse(message.body);
      localStorage.setItem(
        "current_questions",
        JSON.stringify(parsed_message.questions.quiz_questions)
      );
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
  client.publish({
    destination: "/app/" + route.params.lobby_id + "/send_message",
    body: JSON.stringify({
      author: userStore.username,
      message: message.value,
    }),
  });
}

function updatePlayerList() {
  client.publish({
    destination: "/app/" + route.params.lobby_id + "/join",
    body: JSON.stringify({ username: userStore.username }),
  });
}
</script>

<style lang="css" scoped>
#chat {
  /* background-color: rgb(123, 150, 190); */
}

#chat:hover {
  background-color: rgb(80, 120, 180);
  transition: 400ms;
}

#players_list {
  padding: 10px;
  /* background-color: rgb(151, 204, 207); */
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
