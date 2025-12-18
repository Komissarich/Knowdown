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
import axios from "axios";
import { consoleError } from "vuetify/lib/util/console.mjs";
const canvas = useTemplateRef<HTMLCanvasElement>("canvas");
let app: PIXI.Application | null = null;
let joystick: any = null;
let player: Player;
const userStore = useUserStore();
const myPlayerName = userStore.username;
const players = ref<Map<string, Player>>(new Map());
const players_start = ref([]);
let doneSetup = false;
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
      // console.log("data", data, myPlayerName);
      if (data.playerName !== myPlayerName) {
        let remotePlayer = players.value.get(data.playerName);
        // console.log("Changing animation of another player");
        remotePlayer.changeMovement(data.x, data.y, true, data.direction, 0, 0);
        remotePlayer.animationSpeed = 0.1;
        remotePlayer.updateSprite();
      }
    }
  );

  client.subscribe(
    "/topic/arena/" + route.params.arena_id + "/starting_positions",
    async (message) => {
      const data = JSON.parse(message.body);
      console.log("Get starting positions: ", data);
      players_start.value = data;
      await createPlayers();
    }
  );

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
        1,
        100
      );
      console.log("Created Player", currentPlayer);
      let sprite = await currentPlayer.loadAnims();
      console.log("Player sprite", sprite);
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
        myPlayerName,
        "down",
        1,
        100
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
  console.log("starting knockback", target, attackerDirection, power);
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

  if (localStorage.getItem("hostedLobby") === route.params.arena_id) {
    await axios({
      method: "post",
      url: "/api/lobby/" + route.params.arena_id + "/getPlayers",
      params: {
        lobbyId: route.params.arena_id,
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
        null,
        vx,
        vy
      );
      player.animationSpeed = 0.2;
    });

    joystick.on("end", async () => {
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

      p.textName.x = p.sprite.x - 25;
      p.textName.y = p.sprite.y - 30;

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
      }
    });
    if (player && player.hitbox && player.isAttacking) {
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
    if (player && player.movement.active) {
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
