<template>
  <v-container class="fill-height">
    <v-overlay
      :model-value="loaded"
      class="align-center justify-center"
      opacity="0.7"
    >
      <Quiztimer :duration="10" :start="question" @timeout="onTimeOut" />
      <v-slide-y-transition appear>
        <v-card
          v-if="question"
          max-width="700"
          class="mx-auto pa-8 text-center mt-10"
          elevation="24"
          rounded="xl"
        >
          <v-card-text class="text-h4 text-black font-weight-black">
            {{ question?.text }}
          </v-card-text>
        </v-card>
      </v-slide-y-transition>

      <v-slide-x-transition :appear="true" :delay="3000 + i * 150" group>
        <div
          v-for="(answer, i) in question?.answers"
          :key="i"
          :class="`answer-${i}`"
          class="mt-8"
        >
          <v-card
            class="pa-4 text-h6"
            color="primary"
            elevation="16"
            rounded="xl"
            @click="selectAnswer(i)"
          >
            <div class="d-flex align-center justify-center">
              <span class="text-h4 mr-4">{{
                String.fromCharCode(65 + i)
              }}</span>
              {{ answer }}
            </div>
          </v-card>
        </div>
      </v-slide-x-transition>
    </v-overlay>
  </v-container>
</template>

<script setup>
import { onMounted } from "vue";
import { useRoute } from "vue-router";

import Quiztimer from "./Quiztimer.vue";
import axios from "axios";
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import { Client, Stomp } from "@stomp/stompjs";

const route = useRoute();

const question = ref({});
let loaded = ref(false);
const userStore = useUserStore();
onMounted(() => {
  axios({
    method: "post",
    url: "/api/questions/question",

    data: {
      difficulty: "EASY",
      type: "MULTIPLE_CHOICE",
      category: "GENERAL_KNOWLEDGE",
    },
  })
    .then(function (response) {
      console.log(response.data);
      question.value.text = response.data.question;
      question.value.category = response.data.category;
      question.value.difficulty = response.data.difficulty;
      question.value.answers = response.data.incorrectAnswers;

      const index = Math.floor(Math.random() * 3);

      question.value.answers.splice(index, 0, response.data.correctAnswer);
      question.value.correct = response.data.correctAnswer;
      loaded.value = true;
      console.log(question.value);
    })
    .catch(function (error) {
      console.log(error);
    });

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
      "/topic/quiz/" + route.params.lobby_id + "/finish_question",
      function (message) {
        const data = JSON.parse(message.body);

        if (data.type === "QUESTION_RESULTS") {
          console.log(data);
        }
      }
    );
  };
  client.activate();
});

function selectAnswer(i) {
  if (question.value.correct === question.value.answers[i]) {
    console.log("CORRECT");
    console.log(route.params);
    axios({
      method: "post",
      url: "/api/lobby/answer",
      data: {
        name: userStore.username,
        lobbyId: route.params.quiz_id,
        timestamp: Date.now(),
      },
    })
      .then(function (response) {
        console.log(response.data);
      })
      .catch(function (error) {
        console.log(error);
      });
  }
}

const onTimeOut = () => {
  console.log("Время вышло!");
  axios({
    method: "post",
    url: "/api/lobby/finish",
    params: {
      lobbyId: route.params.quiz_id,
    },
  })
    .then(function (response) {
      console.log(response.data);
    })
    .catch(function (error) {
      console.log(error);
    });
};
</script>

<style scoped>
.v-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  max-width: 1000px;
}
</style>
