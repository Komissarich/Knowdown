<template>
  <div class="arena">
    <canvas ref="canvas"></canvas>
    <div id="joystick-zone" class="joystick-container"></div>
  </div>
</template>

<script setup lang="ts">
import * as PIXI from "pixi.js";
import nipplejs from "nipplejs"; // ES module импорт
import { onMounted, onUnmounted, useTemplateRef } from "vue";
import swordsmanImage from "./swordsman.png";
import Arena from "./arena";
const canvas = useTemplateRef<HTMLCanvasElement>("canvas");
let app: PIXI.Application | null = null;
let arenaGraphics: PIXI.Graphics | null = null;
let joystick: any = null; // Типизация nipplejs
const playerPosition = { x: 400, y: 300 }; // Позиция игрока на арене
const playerVelocity = { x: 0, y: 0 };
const PLAYER_SPEED = 0.0005;

onMounted(async () => {
  if (!canvas.value) return;

  // Инициализация PixiJS (как в прошлом примере)
  app = new PIXI.Application();
  await app.init({
    canvas: canvas.value,
    width: 800,
    height: 600,
    backgroundColor: 0x1099bb,
    antialias: true,
  });
  const arena = new Arena(app.screen.width / 2, app.screen.height / 2, 250);
  const arenaGraphics = new PIXI.Graphics();
  arena.draw(arenaGraphics);
  app.stage.addChild(arenaGraphics);
  // Тестовый спрайт игрока
  // const texture = await Assets.load('bunny.png');
  // const playerSprite = new Sprite(texture);
  const texture = await PIXI.Assets.load(swordsmanImage);

  // Если у вас горизонтальный спрайтлист (все кадры в один ряд)
  // Нужно знать размер одного кадра и количество кадров
  const frameWidth = 64; // Ширина одного кадра
  const frameHeight = 64; // Высота одного кадра
  const framesCount = 3; // Количество кадров анимации

  // Создаем массив текстур для анимации
  const runTextures = [];
  for (let i = 0; i < framesCount; i++) {
    // Создаем прямоугольник для каждого кадра
    const frame = new PIXI.Rectangle(
      i * frameWidth,
      0,
      frameWidth,
      frameHeight
    );
    const runTexture = new PIXI.Texture({ source: texture.source, frame });
    runTextures.push(runTexture);
  }

  const playerSprite = new PIXI.AnimatedSprite(runTextures);
  playerSprite.animationSpeed = 0.2;
  playerSprite.play();
  playerSprite.anchor.set(0.5);
  playerSprite.x = playerPosition.x;
  playerSprite.y = playerPosition.y;
  app.stage.addChild(playerSprite);

  const movement = {
    x: 0,
    y: 0,
    active: false,
  };
  const zone = document.getElementById("joystick-zone");
  if (zone) {
    joystick = nipplejs.create({
      zone: zone,
      mode: "semi", // Джойстик появляется при касании
      color: "#00ff00", // Зелёный
      size: 120, // Размер
      threshold: 0.005,
      restJoystick: true,
      restOpacity: 0.5,
      fadeTime: 100,
      multitouch: false,
    });

    joystick.on("move", (evt: any, data: any) => {
      if (data.distance > 10) {
        // Минимальное расстояние от центра
        const angle = data.angle.radian;

        // Направление ВСЕГДА максимальное
        movement.x = Math.cos(angle) * 1.0; // 1.0 = 100% скорость
        movement.y = Math.sin(angle) * 1.0;
        movement.active = true;
      }
    });

    joystick.on("end", () => {
      movement.x = 0;
      movement.y = 0;
      movement.active = false;
    });
  }

  // Игровой цикл
  app.ticker.add(() => {
    if (movement.active) {
      const speed = 0.7;
      playerPosition.x += movement.x * speed;
      playerPosition.y -= movement.y * speed;

      // КОЛЛИЗИЯ — с playerPosition!
      arena.constrainPlayer(playerPosition);

      // ОБНОВЛЕНИЕ СПРАЙТА — ПОСЛЕ коллизии!
      playerSprite.x = playerPosition.x;
      playerSprite.y = playerPosition.y;
      console.log(playerPosition.y, playerSprite.x);
      // Отправляем позицию на бэк через WebSocket
      // ws.send(JSON.stringify({ type: 'move', x: playerPosition.x, y: playerPosition.y }))
    }
  });
});

onUnmounted(() => {
  if (app) {
    app.destroy(true);
    app = null;
  }
  if (joystick) {
    joystick.destroy();
    joystick = null;
  }
});
</script>

<style scoped>
.arena {
  position: relative;
  width: 800px;
  height: 600px;
  margin: auto;
}
canvas {
  position: absolute;

  top: 0;
  left: 0;
  border: 1px solid #ccc;
}
.joystick-container {
  position: absolute;
  bottom: 20px;
  right: 20px;
  width: 150px;
  height: 150px;
  z-index: 10;
}
</style>
