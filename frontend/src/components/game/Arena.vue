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
        @click="attack"
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
import { onMounted, onUnmounted, ref, useTemplateRef } from "vue";
import Arena from "./arena";
import Player from "./player";
import { routeLocationKey, useRoute } from "vue-router";
import { Client } from "@stomp/stompjs";
import { useUserStore } from "@/stores/user";
const canvas = useTemplateRef<HTMLCanvasElement>("canvas");
let app: PIXI.Application | null = null;
let playerSprite: PIXI.AnimatedSprite;
let joystick: any = null;
let player: Player;
const userStore = useUserStore();
const myPlayerId = crypto.randomUUID();
const myPlayerName = userStore.username;
const players = ref<Map<string, Player>>(new Map());

const route = useRoute();
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
    "/topic/arena/" + route.params.arena_id + "/positions",
    (message) => {
      const data = JSON.parse(message.body);
      // console.log("data", data);
      if (data.playerId !== myPlayerId) {
        let remotePlayer = players.value.get(data.playerId);
        if (!remotePlayer) {
          remotePlayer = new Player();
          remotePlayer.loadAnims().then((sprite) => {
            app?.stage.addChild(sprite);
          });
          players.value.set(data.playerId, remotePlayer);
          remotePlayer.playRun();
        }

        remotePlayer.x = data.x;
        remotePlayer.y = data.y;
        remotePlayer.direction = data.direction;
        remotePlayer.updateSprite();
      }
    }
  );
};
client.activate();

onUnmounted(() => {
  console.log("Client deactivated");
  client.deactivate();
});

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
  // createPlayer(myPlayerId, myPlayerName, true)

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

        // Урон
        // opponent.hp -= player.damage
      }
    }
    players.value.forEach((p) => {
      if (Math.abs(p.vx) > 0.1 || Math.abs(p.vy) > 0.1) {
        p.x += p.vx;
        p.y += p.vy;
        if (Math.abs(p.vx) < 0.1) p.vx = 0;
        if (Math.abs(p.vy) < 0.1) p.vy = 0;
      }

      p.updateSprite();
    });
    if (player.movement.active) {
      player.move();

      client.publish({
        destination: "/app/arena/" + route.params.arena_id + "/move",
        body: JSON.stringify({
          playerId: myPlayerId,
          playerName: myPlayerName,
          x: player.x,
          y: player.y,
          direction: player.direction,
        }),
      });
      player.updateSprite();
      arena.constrainPlayer(player);
      players.value.forEach((p, id) => {
        if (id !== myPlayerId) {
          p.updateSprite();
        }
      });
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

function attack() {
  player.attack();

  players.value.forEach((remotePlayer, id) => {
    if (id === myPlayerId) return;

    if (isInAttackRange(player, remotePlayer)) {
      remotePlayer.sprite.tint = 0xff0000;
      setTimeout(() => (remotePlayer.sprite.tint = 0xffffff), 200);

      // applyKnockback(remotePlayer, player.direction, 20);
    }
  });
}

function isInAttackRange(attacker, target) {
  const range = 90;
  const dx = target.x - attacker.x;
  const dy = target.y - attacker.y;
  console.log(attacker, target, dx, dy);
  switch (attacker.direction) {
    case "up":
      return dy < -10 && Math.abs(dx) < 20 && Math.abs(dy) < range;
    case "down":
      return dy > 10 && Math.abs(dx) < 20 && Math.abs(dy) < range;
    case "left":
      return dx < -10 && Math.abs(dy) < 20 && Math.abs(dx) < range;
    case "right":
      return dx > 10 && Math.abs(dy) < 20 && Math.abs(dx) < range;
  }
  return false;
}

function applyKnockback(target, direction, power = 20) {
  target.vx = 0;
  target.vy = 0;

  switch (direction) {
    case "up":
      target.vy = -power;
      break;
    case "down":
      target.vy = power;
      break;
    case "left":
      target.vx = -power;
      break;
    case "right":
      target.vx = power;
      break;
  }

  target.knockbackFriction = 0.92;
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
