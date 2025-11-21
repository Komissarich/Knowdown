<template>
  <div v-if="show" class="countdown-overlay">
    <div class="countdown-number" :class="{ go: isGo }">
      {{ displayText }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Client, Stomp } from "@stomp/stompjs";

const route = useRoute();
const router = useRouter();

const show = ref(false);
const displayText = ref("");
const isGo = ref(false);

let timeout = null;

onMounted(() => {
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
    console.log("Timer Connected to STOMP broker!");
    client.subscribe(
      "/topic/lobby/" + route.params.lobby_id + "/countdown",
      function (message) {
        const data = JSON.parse(message.body);

        if (data.type === "COUNTDOWN") {
          show.value = true;
          isGo.value = false;
          displayText.value = data.seconds.toString();

          if (timeout) clearTimeout(timeout);
        }

        if (data.type === "START") {
          show.value = true;
          isGo.value = true;

          timeout = setTimeout(() => {
            router.push(`game/${route.params.lobby_id}`);
          }, 500);
        }
      }
    );
  };
  client.activate();
});

onUnmounted(() => {
  if (timeout) clearTimeout(timeout);
});
</script>

<style scoped>
.countdown-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(5px);
}

.countdown-number {
  font-size: 160px;
  font-weight: 900;
  color: white;
  text-shadow: 0 0 30px rgba(255, 255, 255, 0.8);
  animation: pulse 1s infinite alternate;
}

.go {
  color: #00ff41;
  font-size: 180px;
  animation: explode 0.8s ease-out forwards;
}

@keyframes pulse {
  from {
    transform: scale(0.9);
    opacity: 0.8;
  }
  to {
    transform: scale(1.1);
    opacity: 1;
  }
}

@keyframes explode {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(2.5);
    opacity: 0;
  }
}
</style>
