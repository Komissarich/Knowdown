<template>
  <div class="quiz-screen">
    <Quiztimer
      :duration="15"
      :start="!!question"
      @timeout="onTimeOut"
      :key="timerKey"
      class="fixed-timer"
    />

    <div class="quiz-content">
      <v-slide-y-transition appear>
        <v-card
          v-if="question"
          max-width="900"
          class="mx-auto pa-8 text-center mb-8"
          elevation="24"
          rounded="xl"
        >
          <v-card-text class="text-h4 text-md-h3 font-weight-black text-black">
            {{ question.text }}
          </v-card-text>
        </v-card>
      </v-slide-y-transition>

      <div class="answers-container mx-auto" style="max-width: 900px">
        <v-row justify="center" dense style="row-gap: 16px">
          <v-col
            v-for="(answer, i) in question?.answers"
            :key="i"
            cols="12"
            sm="6"
            class="py-0"
          >
            <v-slide-x-transition>
              <v-card
                v-if="question"
                height="100"
                class="d-flex align-center justify-center pa-4 text-center cursor-pointer"
                :color="getAnswerColor(i)"
                elevation="20"
                rounded="xl"
                @click="selectAnswer(i)"
              >
                <div>
                  <span
                    v-if="question.type === 'MULTIPLE_CHOICE'"
                    class="text-h4 font-weight-bold mr-4"
                  >
                    {{ String.fromCharCode(65 + i) }}.
                  </span>
                  <span class="text-h5 font-weight-bold text-white">
                    {{ answer }}
                  </span>
                </div>
              </v-card>
            </v-slide-x-transition>
          </v-col>
        </v-row>
      </div>
    </div>
  </div>

  <v-dialog v-model="dialogVisible" max-width="600" persistent>
    <v-card rounded="xl" elevation="24" class="pa-6">
      <v-card-title class="text-h5 font-weight-bold text-center mb-6">
        Результаты раунда
      </v-card-title>
      <div class="results-list-container">
        <v-list lines="three" class="mb-8 bg-transparent" z-index="300">
          <v-list-item
            v-for="[player, points] in sortedResults"
            :key="player"
            class="mb-3 rounded-lg"
            :color="points > 0 ? 'primary' : 'grey'"
          >
            <template v-slot:prepend>
              <v-avatar
                size="48"
                :color="points > 0 ? 'green' : 'grey-darken-2'"
              >
                <span class="text-white text-h5">{{
                  getPosition(player)
                }}</span>
              </v-avatar>
            </template>

            <v-list-item-title class="text-h6 font-weight-medium">
              {{ player }}
            </v-list-item-title>

            <v-list-item-subtitle class="text-h6 font-weight-bold text-primary">
              +{{ points }} очков
            </v-list-item-subtitle>
          </v-list-item>
        </v-list>
      </div>
      <div v-if="playerPoints > 0">
        <v-card-title class="text-h6 font-weight-bold text-center mb-4">
          Прокачай персонажа (+{{ playerPoints }} очков)
        </v-card-title>

        <v-row dense>
          <v-col v-for="stat in stats" :key="stat.name" cols="6" md="4" sm="6">
            <v-card variant="outlined" class="pa-4 text-center">
              <v-card-text class="d-flex align-center justify-space-between">
                <span class="text-subtitle-2">{{ stat.label }}</span>
                <v-btn
                  icon
                  size="small"
                  color="primary"
                  :disabled="playerPoints === 0"
                  @click="upgradeStat(stat.name)"
                >
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </v-card-text>
              <v-progress-linear
                :model-value="(stat.value / stat.max) * 100"
                color="primary"
                height="8"
                rounded
                striped
              ></v-progress-linear>
            </v-card>
          </v-col>
        </v-row>
      </div>

      <v-progress-linear
        v-model="progress"
        color="grey"
        height="4"
        class="mt-6"
      ></v-progress-linear>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from "vue";
import { useRoute } from "vue-router";

import Quiztimer from "./Quiztimer.vue";
import axios from "axios";
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import { Client, Stomp } from "@stomp/stompjs";
import router from "@/router/router";
const selectedAnswer = ref(null);
const waitTimer = ref(false);
const correctAnswerIndex = ref(null);
const route = useRoute();
const showResults = ref(false);
const progress = ref(100);
const playerPoints = ref(0);
const roundResults = ref({});
let timer = null;
const question = ref({});

const dialogVisible = computed(() => showResults.value && waitTimer.value);
let loaded = ref(false);
const userStore = useUserStore();
const question_index = ref(0);
const timerKey = ref(0);
const current_questions = ref([]);
const stats = ref([
  { name: "health", label: "Здоровье", value: 0, max: 30 },
  { name: "move_speed", label: "Скорость движения", value: 0, max: 20 },
  { name: "attack_speed", label: "Скорость атаки", value: 0, max: 20 },
  { name: "melee_power", label: "Сила удара", value: 0, max: 30 },
  { name: "melee_range", label: "Дальность удара", value: 0, max: 20 },
  { name: "knockback_power", label: "Сила отбрасывания", value: 0, max: 20 },
  {
    name: "knockback_friction",
    label: "Сопротивление отталкиванию",
    value: 0,
    max: 20,
  },
]);

const getAnswerColor = (i) => {
  if (!showResults.value) {
    if (selectedAnswer.value === i) return "teal-accent-3";
    return "primary";
  } else if (showResults.value === true) {
    if (i === correctAnswerIndex.value) return "green";
    if (selectedAnswer.value === i) return "orange-darken-3";
    return "red-darken-2";
  }
};
const sortedResults = computed(() =>
  Object.entries(roundResults.value).sort((a, b) => b[1] - a[1])
);
const getPosition = (player) => {
  return sortedResults.value.findIndex(([p]) => p === player) + 1;
};

const upgradeStat = (statName) => {
  if (playerPoints.value > 0) {
    const stat = stats.value.find((s) => s.name === statName);
    if (stat && stat.value < stat.max) {
      stat.value += 1;
      playerPoints.value--;
    }
  }
};
onMounted(() => {
  current_questions.value = JSON.parse(
    localStorage.getItem("current_questions")
  );
  setQuestion();
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
  console.log("lobby", route.params.quiz_id);

  setTimeout(() => {
    client.subscribe(
      "/topic/lobby/" + route.params.quiz_id + "/finish_question",
      (message) => {
        const data = JSON.parse(message.body);
        console.log(
          "nice results",
          data,
          data.type,
          data.type === "QUESTION_RESULTS"
        );
        if (data.type === "QUESTION_RESULTS") {
          question_index.value += 1;
          showResults.value = true;

          selectedAnswer.value = null;
          roundResults.value = data.points;

          playerPoints.value = data.points[userStore.username];
          progress.value = 100;
          timer = setInterval(() => {
            progress.value -= 100 / 150;
            if (progress.value <= 0) {
              showResults.value = false;
              waitTimer.value = false;
              setQuestion();
              timerKey.value += 1;
              clearInterval(timer);
            }
          }, 100);
        }
      }
    );
  }, 100);

  setTimeout(() => {
    client.subscribe(
      "/topic/lobby/" + route.params.quiz_id + "/finish_quiz",
      (message) => {
        const data = JSON.parse(message.body);
        console.log("finishing");
        if (data.type === "QUESTION_RESULTS") {
          question_index.value += 1;
          showResults.value = true;

          selectedAnswer.value = null;
          roundResults.value = data.points;

          playerPoints.value = data.points[userStore.username];
          progress.value = 100;
          timer = setInterval(() => {
            progress.value -= 100 / 150;
            if (progress.value <= 0) {
              showResults.value = false;
              waitTimer.value = false;
              setQuestion();
              timerKey.value += 1;
              clearInterval(timer);
            }
          }, 100);
        }
      }
    );
  }, 100);
};
client.activate();

onUnmounted(() => {
  console.log("Client deactivated");
  client.deactivate();
});
function setQuestion() {
  if (question_index.value < current_questions.value.length) {
    question.value.text =
      current_questions.value[question_index.value].question;
    question.value.category =
      current_questions.value[question_index.value].category;
    question.value.difficulty =
      current_questions.value[question_index.value].difficulty;
    question.value.answers =
      current_questions.value[question_index.value].incorrectAnswers;
    question.value.type = current_questions.value[question_index.value].type;
    if (
      current_questions.value[question_index.value].type == "MULTIPLE_CHOICE"
    ) {
      const index = Math.floor(Math.random() * 3);
      question.value.answers.splice(
        index,
        0,
        current_questions.value[question_index.value].correctAnswer
      );
      question.value.correct =
        current_questions.value[question_index.value].correctAnswer;
      loaded.value = true;

      correctAnswerIndex.value = question.value.answers.indexOf(
        current_questions.value[question_index.value].correctAnswer
      );
    } else {
      question.value.answers = ["True", "False"];

      question.value.correct =
        current_questions.value[question_index.value].incorrectAnswers[0];
      loaded.value = true;
      correctAnswerIndex.value = question.value.answers.indexOf(
        current_questions.value[question_index.value].correctAnswer
      );
    }
    console.log("RIGHT ANSWER INDEX", correctAnswerIndex.value);
  } else {
    axios({
      method: "post",
      url: "/api/lobby/upgrade",
      data: {
        username: userStore.username,
        lobbyId: route.params.quiz_id,
        upgradedStats: {
          health: stats.value[0].value,
          move_speed: stats.value[1].value,
          attack_speed: stats.value[2].value,
          melee_power: stats.value[3].value,
          melee_range: stats.value[4].value,
          knockback_power: stats.value[5].value,
          knockback_friction: stats.value[6].value,
        },
      },
    })
      .then(function (response) {
        console.log(response.data);

        router.push({
          path: "/arena/" + route.params.quiz_id,
          params: { arena_id: route.params.quiz_id },
        });
      })
      .catch(function (error) {
        console.log(error);
      });
  }
}

function selectAnswer(i) {
  if (!showResults.value && selectedAnswer.value === null) {
    selectedAnswer.value = i;
    console.log("SELECT", selectedAnswer.value);
    if (question.value.correct === question.value.answers[i]) {
      console.log("CORRECT");

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
}

const onTimeOut = () => {
  showResults.value = true;
  console.log("Время вышло!");
  const hostedLobbyId = localStorage.getItem("hostedLobby");
  console.log("current hosting", hostedLobbyId);

  setTimeout(() => {
    waitTimer.value = true;
  }, 4000);
  if (hostedLobbyId !== null && hostedLobbyId === route.params.quiz_id) {
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
  }
};
</script>

<style scoped>
.v-card-actions {
  justify-content: center;
}
.quiz-screen {
  min-height: 100vh;
  background: rgba(0, 0, 0, 0.85);
  padding: 40px 20px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.quiz-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  padding-top: 120px;
}

.fixed-timer {
  position: fixed;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
}

.cursor-pointer {
  cursor: pointer;
}
.results-list-container {
  max-height: 50vh; /* на десктопе — половина экрана */
  overflow-y: auto; /* скролл только внутри списка */
  padding-right: 8px; /* место для скроллбара */
}

/* На мобилке — список почти на весь диалог */
@media (max-width: 960px) {
  .results-list-container {
    max-height: 60vh; /* больше места для списка */
  }

  .v-list-item {
    min-height: 80px; /* выше строки */
    padding: 12px;
  }

  .v-list-item-title {
    font-size: 1.4rem !important;
  }

  .v-list-item-subtitle {
    font-size: 1.6rem !important;
  }
}
</style>
