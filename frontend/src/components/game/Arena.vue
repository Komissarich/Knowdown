<template>
  <div class="arena">
    <canvas ref="canvas"></canvas>
    <div id="joystick-zone" class="joystick-container"></div>
    <div class="button-container">
      <v-btn
        variant="elevated"
        color="blue-grey-lighten-1"
        style="opacity: 0.8"
        width="100"
        height="100"
        @click="Attack"
        icon-size="100"
        rounded="circle"
        ><v-icon icon="mdi-sword" size="80"></v-icon
      ></v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import * as PIXI from "pixi.js";
import nipplejs from "nipplejs";
import { onMounted, onUnmounted, useTemplateRef } from "vue";
import Arena from "./arena";
import Player from "./player";
const canvas = useTemplateRef<HTMLCanvasElement>("canvas");
let app: PIXI.Application | null = null;
let playerSprite: PIXI.AnimatedSprite;
let joystick: any = null;
let player: Player;
onMounted(async () => {
  if (!canvas.value) return;
  app = new PIXI.Application();
  await app.init({
    canvas: canvas.value,
    width: 800,
    height: 600,
    backgroundColor: 0x1099bb,
    antialias: true,
  });

  const opponentSprite = new PIXI.Sprite(PIXI.Texture.WHITE);
  opponentSprite.width = 64;
  opponentSprite.height = 64;
  opponentSprite.tint = 0xff0000;
  opponentSprite.x = 500;
  opponentSprite.y = 250;
  app.stage.addChild(opponentSprite);
  let opponent = {
    sprite: opponentSprite,
    x: 500,
    y: 300,
    vx: 0, // скорость отталкивания по X
    vy: 0, // по Y
    knockbackFriction: 0.92, // чем меньше — тем дольше летит (0.9–0.95)
  };
  const visualizeHitbox = new PIXI.Sprite(PIXI.Texture.WHITE);

  visualizeHitbox.width = 40;
  visualizeHitbox.height = 30;
  visualizeHitbox.x = 400;
  visualizeHitbox.y = 300;
  visualizeHitbox.tint = 0xff00ff;
  app.stage.addChild(visualizeHitbox);
  const arena = new Arena(app.screen.width / 2, app.screen.height / 2, 250);
  const arenaGraphics = new PIXI.Graphics();
  arena.draw(arenaGraphics);
  app.stage.addChild(arenaGraphics);
  player = new Player();

  const sprite = await player.loadAnims();
  app.stage.addChild(sprite);
  const zone = document.getElementById("joystick-zone");
  if (zone) {
    joystick = nipplejs.create({
      zone: zone,
      mode: "semi",
      color: "#00ff00",
      catchDistance: 30,
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
      const angle = data.angle.radian;

      player.changeMovement(
        Math.cos(angle) * 1.0,
        Math.sin(angle) * 1.0,
        true,
        vx,
        vy
      );
    });

    joystick.on("end", () => {
      player.changeMovement(0, 0, false, 0, 0);
      player.lastDirection = null;
      player.stop();
    });
  }

  app.ticker.add(() => {
    if (Math.abs(opponent.vx) > 0.1 || Math.abs(opponent.vy) > 0.1) {
      opponent.x += opponent.vx;
      opponent.y += opponent.vy;

      // Затухание (трение)
      opponent.vx *= opponent.knockbackFriction;
      opponent.vy *= opponent.knockbackFriction;

      // Останавливаем полностью, если слишком медленно
      if (Math.abs(opponent.vx) < 0.1) opponent.vx = 0;
      if (Math.abs(opponent.vy) < 0.1) opponent.vy = 0;
    }

    // Синхронизация спрайта
    opponent.sprite.x = opponent.x;
    opponent.sprite.y = opponent.y;

    if (player.hitbox && player.isAttacking) {
      // Позиционируем hitbox перед игроком
      console.log("here");

      const hitboxBounds = new PIXI.Rectangle();
      switch (player.direction) {
        case "up":
          hitboxBounds.set(player.x - 20, player.y - 20, 40, 30);
          break;
        case "down":
          hitboxBounds.set(player.x - 20, player.y + 10, 40, 30);
          break;
        case "left":
          hitboxBounds.set(player.x - 40, player.y - 10, 30, 40);
          break;
        case "right":
          hitboxBounds.set(player.x + 10, player.y - 10, 30, 40);
          break;
      }
      visualizeHitbox.x = hitboxBounds.x;
      visualizeHitbox.y = hitboxBounds.y;
      const opponentRect = new PIXI.Rectangle(
        opponentSprite.getBounds().x,
        opponentSprite.getBounds().y,
        opponentSprite.getBounds().width,
        opponentSprite.getBounds().height
      );

      hitboxBounds.intersects(opponentRect);
      // Проверяем пересечение
      if (hitboxBounds.intersects(opponentRect)) {
        console.log("HIT! Урон:", player.damage);

        // Отдача оппонента
        opponentSprite.x += 20;
        opponentSprite.y += 10;

        // Вспышка
        applyKnockback(opponent, player.direction, 14);
        opponentSprite.tint = 0xffff00; // жёлтый
        setTimeout(() => (opponentSprite.tint = 0xff0000), 100);

        // Урон (когда будет HP)
        // opponent.hp -= player.damage
      }
    }
    if (player.movement.active) {
      player.move();

      player.updateSprite();
      arena.constrainPlayer(player);

      // console.log(player.x, player.y, player.sprite.x, player.sprite.y);
    }
  });
  function applyKnockback(target, attackerDirection, power = 12) {
    let knockX = 0;
    let knockY = 0;

    switch (attackerDirection) {
      case "up":
        knockY = -power;
        break;
      case "down":
        knockY = power;
        break;
      case "left":
        knockX = -power;
        break;
      case "right":
        knockX = power;
        break;
    }

    // Диагональ — чуть слабее, чтобы не улетал в космос
    if (knockX !== 0 && knockY !== 0) {
      knockX *= 0.3;
      knockY *= 0.3;
    }

    target.vx = knockX;
    target.vy = knockY;
  }
});
function Attack() {
  player.attack();
}

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
  left: 40px;
  width: 200px;
  height: 200px;
  z-index: 10;
}

.button-container {
  position: absolute;
  bottom: 40px;
  right: 40px;
  width: 120px;
  height: 120 px;
  z-index: 10;
  opacity: 1;
}
</style>
