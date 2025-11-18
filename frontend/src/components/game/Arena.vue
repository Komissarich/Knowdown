<template>
  <div class="arena">
    <canvas ref="canvas"></canvas>
    <div id="joystick-zone" class="joystick-container"></div>
  </div>
</template>

<script setup lang="ts">
import * as PIXI from "pixi.js";
import nipplejs from "nipplejs";
import { onMounted, onUnmounted, useTemplateRef } from "vue";
import runImage from "./swordsman_run.png";
import idleImage from "./swordsman_idle.png";
import attackImage from "./swordsman_attack.png";
import Arena from "./arena";
const canvas = useTemplateRef<HTMLCanvasElement>("canvas");
let app: PIXI.Application | null = null;
let joystick: any = null;
const frameWidth = 64;
const frameHeight = 64;
const playerPosition = { x: 400, y: 300 };
let lastDirection;

const playerAnimations = {
  idle: [],
  downRun: [],
  upRun: [],
  leftRun: [],
  rightRun: [],
};

function getSprites(row, texture, framesCount) {
  let sprites = [];

  for (let i = 0; i < framesCount; i++) {
    const frame = new PIXI.Rectangle(
      i * frameWidth,
      row * frameHeight,
      frameWidth,
      frameHeight
    );
    const new_sprite = new PIXI.Texture({ source: texture.source, frame });
    sprites.push(new_sprite);
  }
  return sprites;
}
onMounted(async () => {
  if (!canvas.value) return;
  const idleTexture = await PIXI.Assets.load(idleImage);
  const runTexture = await PIXI.Assets.load(runImage);
  const attackTexture = await PIXI.Assets.load(attackImage);
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

  const idleAnim = getSprites(0, idleTexture, 11);
  const runDownAnim = getSprites(0, runTexture, 7);
  const runUpAnim = getSprites(3, runTexture, 7);
  const runLeftAnim = getSprites(1, runTexture, 7);
  const runRightAnim = getSprites(2, runTexture, 7);

  const attackDownAnim = getSprites(0, attackTexture, 7);

  const playerSprite = new PIXI.AnimatedSprite(attackDownAnim);
  playerSprite.animationSpeed = 0.3;
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
      mode: "semi",
      color: "#00ff00",
      size: 135,
      threshold: 0.005,
      restJoystick: true,
      restOpacity: 0.5,
      fadeTime: 100,
      multitouch: false,
    });

    joystick.on("move", (evt: any, data: any) => {
      const vx = data.vector.x;
      const vy = data.vector.y;

      // Определяем направление
      let direction: string;
      if (Math.abs(vy) > Math.abs(vx)) {
        // Вертикаль приоритетнее
        direction = vy > 0 ? "up" : "down";
      } else {
        // Горизонталь
        direction = vx > 0 ? "right" : "left";
      }

      // Меняем анимацию только если направление новое
      if (direction !== lastDirection) {
        lastDirection = direction;
        switch (direction) {
          case "up":
            playerSprite.textures = runUpAnim;
            break;
          case "down":
            playerSprite.textures = runDownAnim;
            break;
          case "left":
            playerSprite.textures = runLeftAnim;
            break;
          case "right":
            playerSprite.textures = runRightAnim;
            break;
        }
      }
      playerSprite.animationSpeed = 0.15;
      playerSprite.play();

      const angle = data.angle.radian;
      console.log(data);

      movement.x = Math.cos(angle) * 1.0;
      movement.y = Math.sin(angle) * 1.0;
      movement.active = true;
    });

    joystick.on("end", () => {
      if (playerSprite.textures !== idleAnim) {
        playerSprite.textures = idleAnim;
        playerSprite.play();
        lastDirection = null;
        playerSprite.animationSpeed = 0.08;
      }
      movement.x = 0;
      movement.y = 0;
      movement.active = false;
    });
  }

  app.ticker.add(() => {
    if (movement.active) {
      const speed = 0.7;
      playerPosition.x += movement.x * speed;
      playerPosition.y -= movement.y * speed;

      arena.constrainPlayer(playerPosition);

      playerSprite.x = playerPosition.x;
      playerSprite.y = playerPosition.y;
      // console.log(playerPosition.y, playerSprite.x);
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
  bottom: 40px;
  right: 40px;
  width: 200px;
  height: 200px;
  z-index: 10;
}
</style>
