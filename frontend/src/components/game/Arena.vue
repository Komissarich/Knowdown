<template>
  <div class="arena-wrapper">
    <div id="joystick-zone" class="joystick-container"></div>
    <canvas ref="canvas" class="arena-canvas"></canvas>

    <div class="button-container">
      <v-btn
        variant="elevated"
        color="blue-grey-lighten-1"
        style="opacity: 0.8"
        width="100"
        height="100"
        @click="attack"
        class="w-full h-full"
        icon-size="100"
        rounded="circle"
        ><v-icon icon="mdi-sword" size="250%"></v-icon
      ></v-btn>
    </div>
  </div>

  <v-dialog v-model="dialogVisible" max-width="600" persistent>
    <v-card rounded="xl" elevation="24" class="pa-6">
      <v-card-title class="text-h5 font-weight-bold text-center mb-6">
        Победитель: {{ winner_name }}
      </v-card-title>
    </v-card>
  </v-dialog>

  <v-dialog v-model="dialogVisible2" max-width="600" persistent>
    <v-card rounded="xl" elevation="24" class="pa-6">
      <v-card-title class="text-h5 font-weight-bold text-center mb-6">
        Подождите загрузку
      </v-card-title>
    </v-card>
  </v-dialog>
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
import axios from "axios";
import { consoleError } from "vuetify/lib/util/console.mjs";
import router from "@/router/router";
const canvas = useTemplateRef<HTMLCanvasElement>("canvas");
let app: PIXI.Application | null = null;
let joystick: any = null;
let player: Player;
const userStore = useUserStore();
const myPlayerName = userStore.username;
const players = ref<Map<string, Player>>(new Map());
const winner_name = ref("");
const players_start = ref([]);
let doneSetup = false;
const route = useRoute();

let arenaGraphics: PIXI.Graphics | null = null;
let arena: Arena | null = null;
const dialogVisible = ref(false);
const dialogVisible2 = ref(false);
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
  console.log("Connected to STOMP broker in arena!");

  client.subscribe(
    "/topic/arena/" + route.params.arena_id + "/positions",
    (message) => {
      const data = JSON.parse(message.body);
      if (data.playerName !== myPlayerName) {
        let remotePlayer = players.value.get(data.playerName);
        // console.log("Changing animation of another player");
        remotePlayer.changeMovement(data.x, data.y, true, data.direction, 0, 0);
        remotePlayer.animationSpeed = 0.1;
        remotePlayer.updateSprite();
      }
    }
  );

  setTimeout(() => {
    client.subscribe(
      "/topic/arena/" + route.params.arena_id + "/starting_positions",
      async (message) => {
        const data = JSON.parse(message.body);
        console.log("Get starting positions: ", data);
        players_start.value = data;
        dialogVisible2.value = false;
        await createPlayers();
      }
    );
  }, 200);
  client.subscribe(
    "/topic/arena/" + route.params.arena_id + "/stop_anim",
    (message) => {
      const data = message.body;

      if (data !== myPlayerName) {
        let remotePlayer = players.value.get(data);
        remotePlayer.changeMovement(0, 0, false, 0, 0);
        remotePlayer.lastDirection = null;
        remotePlayer.animationSpeed = 0.05;
        remotePlayer.stop();
      }
    }
  );

  client.subscribe(
    "/topic/arena/" + route.params.arena_id + "/attack_anim",
    (message) => {
      const data = message.body;
      if (data !== myPlayerName) {
        let remotePlayer = players.value.get(data);
        remotePlayer.animationSpeed = 0.1;
        remotePlayer.attack();
      }
    }
  );

  client.subscribe(
    "/topic/arena/" + route.params.arena_id + "/receive_attack",
    (message) => {
      const data = JSON.parse(message.body);
      if (data !== myPlayerName) {
        let remotePlayer = players.value.get(data.target_username);
        remotePlayer.sprite.tint = 0xff0000;
        remotePlayer.hp -= data.damage;

        if (remotePlayer.hp <= 0) {
          remotePlayer.death();
          players.value.delete(data.target_username);
          if (players.value.size == 1) {
            winner_name.value = players.value.keys().next().value;
            dialogVisible.value = true;
            setTimeout(() => router.push("/"), 8000);
          }
        }
        setTimeout(() => (remotePlayer.sprite.tint = 0xffffff), 200);
        remotePlayer.isKnocked = true;
        applyKnockback(
          remotePlayer,
          data.knockback_direction,
          data.knockback_power
        );
      }
    }
  );
};
client.activate();

onUnmounted(() => {
  console.log("Client deactivated");
  client.deactivate();
  if (app) {
    app.destroy(true);
    app = null;
  }
  if (joystick) {
    joystick.destroy();
    joystick = null;
  }
});

async function createPlayers() {
  for (let i = 0; i < players_start.value.length; i++) {
    if (players_start.value[i].username != myPlayerName) {
      let currentPlayer = new Player(
        players_start.value[i].x,
        players_start.value[i].y,
        0,
        0,
        players_start.value[i].username,
        "down",
        players_start.value[i].move_speed,
        players_start.value[i].health,
        players_start.value[i].melee_power,
        players_start.value[i].knockback_power,
        players_start.value[i].knockback_friction,
        players_start.value[i].melee_range,
        players_start.value[i].attack_speed
      );
      let sprite = await currentPlayer.loadAnims();
      app.stage.addChild(sprite);
      currentPlayer.updateSprite();
      client.publish({
        destination: "/app/arena/" + route.params.arena_id + "/move",
        body: JSON.stringify({
          playerName: currentPlayer.name,
          x: currentPlayer.x,
          y: currentPlayer.y,
          direction: "down",
        }),
      });

      let nameText = await currentPlayer.loadName();
      app.stage.addChild(nameText);

      let hpBarBg = await currentPlayer.loadHpBarBg();
      app.stage.addChild(hpBarBg);

      let hpBar = await currentPlayer.loadHpBar();
      app.stage.addChild(hpBar);

      players.value.set(players_start.value[i].username, currentPlayer);
    } else {
      player = new Player(
        players_start.value[i].x,
        players_start.value[i].y,
        0,
        0,
        players_start.value[i].username,
        "down",
        players_start.value[i].move_speed,
        players_start.value[i].health,
        players_start.value[i].melee_power,
        players_start.value[i].knockback_power,
        players_start.value[i].knockback_friction,
        players_start.value[i].melee_range,
        players_start.value[i].attack_speed
      );

      const sprite = await player.loadAnims();
      app.stage.addChild(sprite);
      player.updateSprite();
      players.value.set(myPlayerName, player);

      let nameText = await player.loadName();
      app.stage.addChild(nameText);

      let hpBarBg = await player.loadHpBarBg();
      app.stage.addChild(hpBarBg);

      let hpBar = await player.loadHpBar();
      app.stage.addChild(hpBar);
    }
  }

  console.log("All players: ", players);
  doneSetup = true;
}

function applyKnockback(target, attackerDirection, power) {
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

  target.vx = knockX;
  target.vy = knockY;
  console.log("Knock power: ", knockX, knockY);
}

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

  const arena = new Arena(400, 300, 250);
  const arenaGraphics = new PIXI.Graphics();
  arena.draw(arenaGraphics);
  app.stage.addChild(arenaGraphics);
  dialogVisible2.value = true;
  if (localStorage.getItem("hostedLobby") === route.params.arena_id) {
    setTimeout(async () => {
      try {
        await axios({
          method: "post",
          url: "/api/lobby/" + route.params.arena_id + "/getPlayers",
          params: {
            lobbyId: route.params.arena_id,
            username: myPlayerName,
          },
          timeout: 8000,
        })
          .then(function (response) {
            console.log(response.data);
            dialogVisible2.value = false;
          })
          .finally(() => {
            //alert("finally ended");
            dialogVisible2.value = false;
          });
      } catch (error) {
        console.log(error);
        dialogVisible2.value = false;
        //
      }
    }, 5000);
  }

  const zone = document.getElementById("joystick-zone");
  if (zone) {
    joystick = nipplejs.create({
      zone: zone,
      mode: "static",
      color: "#00ff00",
      size: 150,
      catchDistance: 40,
      position: { left: "50%", top: "50%" },
      threshold: 0.005,
      restJoystick: true,
      restOpacity: 1,
      fadeTime: 0,
      multitouch: false,
    });
    joystick.on("move", (evt: any, data: any) => {
      if (!player.isDead) {
        const isMoving = data.distance > 10;

        if (isMoving) {
          const angle = data.angle.radian;

          let mobileCompensation = window.innerWidth <= 768 ? 2.0 : 1.0;
          if (
            window.innerWidth > window.innerHeight &&
            window.innerWidth <= 1300
          ) {
            mobileCompensation *= 2; // дополнительно для портрета
          }
          const vx = Math.cos(angle);
          const vy = Math.sin(angle);

          player.changeMovement(
            vx * 1.0 * mobileCompensation,
            vy * 1.0 * mobileCompensation,
            true,
            null,
            vx,
            vy
          );

          player.animationSpeed = 0.2;
        } else {
          // Если палочка почти в центре — стоп
          player.changeMovement(0, 0, false, 0, 0);
          player.stop();
        }
        player.animationSpeed = 0.2;
      }
    });

    joystick.on("end", async () => {
      if (!player.isDead) {
        player.changeMovement(0, 0, false, 0, 0);
        player.lastDirection = null;
        player.stop();
        player.animationSpeed = 0.05;
        await axios({
          method: "post",
          url: "/api/lobby/arena/" + route.params.arena_id + "/stop",
          params: {
            arena_id: route.params.arena_id,
            username: myPlayerName,
          },
        })
          .then(function (response) {
            console.log(response.data);
          })
          .catch(function (error) {
            console.log(error);
          });
      }
    });
  }

  app.ticker.add(() => {
    players.value.forEach((p) => {
      const hpPercent = p.hp / p.maxHp;
      p.hpBarBg.clear();
      p.hpBarBg.rect(p.sprite.x - 30, p.sprite.y + 15, 60, 6);
      p.hpBarBg.fill(0x333333);

      p.hpBar.clear();
      p.hpBar.rect(p.sprite.x - 30, p.sprite.y + 15, 60 * hpPercent, 6);
      p.hpBar.fill(
        hpPercent > 0.5 ? 0x00ff00 : hpPercent > 0.2 ? 0xffaa00 : 0xff0000
      );

      p.textName.x = p.sprite.x;
      p.textName.y = p.sprite.y - 40;

      if (p.isKnocked) {
        if (Math.abs(p.vx) > 0.1 || Math.abs(p.vy) > 0.1) {
          p.x += p.vx;
          p.y += p.vy;

          p.vx *= p.knockback_friction;
          p.vy *= p.knockback_friction;
          if (Math.abs(p.vx) < 0.1) {
            p.vx = 0;
          }

          if (Math.abs(p.vy) < 0.1) {
            p.vy = 0;
          }

          if (Math.abs(p.vx) < 0.1 && Math.abs(p.vy) < 0.1) {
            p.isKnocked = false;
          }
        }
        p.updateSprite();
        arena.constrainPlayer(p);
      }
    });
    if (player && player.movement.active && !player.isDead) {
      player.move();

      client.publish({
        destination: "/app/arena/" + route.params.arena_id + "/move",
        body: JSON.stringify({
          playerName: myPlayerName,
          x: player.x,
          y: player.y,
          direction: player.direction,
        }),
      });
      player.updateSprite();
      arena.constrainPlayer(player);
      players.value.forEach((p, name) => {
        if (name !== myPlayerName) {
          p.updateSprite();
        }
      });
      // console.log(player.x, player.y, player.sprite.x, player.sprite.y);
    }
  });
});
async function attack() {
  if (!player.isDead) {
    player.animationSpeed = 0.2;
    player.attack();

    await axios({
      method: "post",
      url: "/api/lobby/arena/" + route.params.arena_id + "/attack",
      params: {
        arena_id: route.params.arena_id,
        username: myPlayerName,
      },
    })
      .then(function (response) {
        console.log(response.data);
      })
      .catch(function (error) {
        console.log(error);
      });

    players.value.forEach(async (remotePlayer, name) => {
      if (name === myPlayerName) return;
      if (isInAttackRange(player, remotePlayer)) {
        // player.hp +=
        //   player.hp + player.vampirism > player.maxHp ? 0 : player.vampirism;
        await axios({
          method: "post",
          url: "/api/lobby/arena/" + route.params.arena_id + "/receive_attack",
          data: {
            target_username: remotePlayer.name,
            knockback_direction: player.direction,
            knockback_power: player.knockback_power,
            damage: player.damage,
          },
          params: {
            arena_id: route.params.arena_id,
          },
        })
          .then(function (response) {
            console.log(response.data);
          })
          .catch(function (error) {
            console.log(error);
          });
      }
    });
  }
}

function isInAttackRange(attacker, target) {
  const range = 35 + player.attack_range;
  const dx = target.x - attacker.x;
  const dy = target.y - attacker.y;
  // console.log(attacker, target, dx, dy);
  switch (attacker.direction) {
    case "up":
      //уменьшить по y
      return dy < -10 && Math.abs(dx) < 20 && Math.abs(dy) < range;
    case "down":
      //уменьшить по y
      return dy > 10 && Math.abs(dx) < 20 && Math.abs(dy) < range;
    case "left":
      //уменьшить по x
      return dx < -10 && Math.abs(dy) < 20 && Math.abs(dx) < range;
    case "right":
      //уменьшить по x
      return dx > 10 && Math.abs(dy) < 20 && Math.abs(dx) < range;
  }
  return false;
}
</script>

<style scoped>
.arena-wrapper {
  position: relative;
  width: 100vw;
  height: 100vh;
  background: #1099bb;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: visible !important;
}

.arena-canvas {
  width: 800px;
  height: 600px;
  max-width: 100vw;
  max-height: 100vh;
  object-fit: contain;
  z-index: 1;
}
.joystick-container {
  position: absolute;
  bottom: max(20px, 5vh); /* минимум 20px, но не ближе 5% от низа */
  left: max(20px, 5vw); /* минимум 20px, но не ближе 5% от левого края */
  width: min(180px, 25vw); /* максимум 180px, но не больше 25% ширины */
  height: min(180px, 25vw);

  z-index: 9999 !important;
  pointer-events: auto;
}
.button-container {
  position: absolute;
  bottom: 5vh;
  bottom: max(20px, 5vh);
  right: max(20px, 5vw);
  width: min(120px, 18vw);
  height: min(120px, 18vw);
  max-width: 140px;
  max-height: 140px;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
