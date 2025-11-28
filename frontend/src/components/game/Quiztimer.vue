```vue
<!-- src/components/QuizTimer.vue -->
<template>
  <div class="quiz-timer">
    <svg viewBox="0 0 100 100" class="timer-svg">
      <!-- Фоновый круг (серый, тонкий) -->
      <circle
        cx="50"
        cy="50"
        r="46"
        fill="none"
        stroke="#444"
        stroke-width="6"
      />

      <!-- Прогресс-круг -->
      <circle
        cx="50"
        cy="50"
        r="46"
        fill="none"
        stroke-width="7"
        stroke-linecap="round"
        :stroke="progressColor"
        stroke-dasharray="288.7"
        :stroke-dashoffset="dashoffset"
        class="progress-ring"
      />

      <!-- Цифра по центру -->
      <text
        x="50"
        y="40"
        dy="0.32em"
        text-anchor="middle"
        dominant-baseline="middle"
        class="timer-text"
      >
        {{ Math.ceil(timeLeft) }}
      </text>
    </svg>
  </div>
</template>

<script setup>
// ← тот же скрипт, без изменений
import { ref, computed, watch, onMounted, onUnmounted } from "vue";

const props = defineProps({
  duration: { type: Number, default: 15 },
  start: { type: Boolean, default: false },
});
const emit = defineEmits(["timeout"]);

const timeLeft = ref(props.duration);
let interval = null;

const dashoffset = computed(
  () => 288.7 * (1 - timeLeft.value / props.duration)
);

const progressColor = computed(() => {
  const r = timeLeft.value / props.duration;
  if (r > 0.5) return "#00e676";
  if (r > 0.2) return "#ffb300";
  return "#ff1744";
});

watch(
  () => props.start,
  (v) => (v ? startTimer() : clearInterval(interval))
);

function startTimer() {
  clearInterval(interval);
  timeLeft.value = props.duration;
  interval = setInterval(() => {
    timeLeft.value -= 0.05;
    if (timeLeft.value <= 0) {
      timeLeft.value = 0;
      clearInterval(interval);
      emit("timeout");
    }
  }, 50);
}

onMounted(() => props.start && startTimer());
onUnmounted(() => clearInterval(interval));
</script>

<style scoped>
/* ТАЙМЕР НАД ВОПРОСОМ ПО ЦЕНТРУ, НЕ НАЛЕЗАЕТ! */
.quiz-timer {
  position: fixed;
  top: -100px; /* отступ от верха */
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  width: 100px; /* маленький и аккуратный */
  height: 100px;
  pointer-events: none; /* не мешает кликам */
}

/* Только прогресс-круг крутится */
.progress-ring {
  transform: rotate(-90deg);
  transform-origin: center;
  transition: stroke-dashoffset 0.05s linear, stroke 0.3s ease;
  /* УБРАЛИ ВСЮ ТЕНЬ! */
}

.timer-text {
  font-size: 42px;
  margin-top: 100px;
  font-weight: 900;
  fill: white;
}

.danger {
  fill: #ff1744 !important;
  animation: blink 0.6s infinite;
}

@keyframes blink {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}
</style>
