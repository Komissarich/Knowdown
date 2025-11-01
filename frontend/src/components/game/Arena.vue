<template>
  <div class="arena">
    <canvas ref="canvas"></canvas>
    <div id="joystick-zone" class="joystick-container"></div>
  </div>
</template>

<script setup lang="ts">
import * as PIXI from 'pixi.js'
import nipplejs from 'nipplejs'  // ES module импорт
import { onMounted, onUnmounted, useTemplateRef } from 'vue'
import {Assets, Sprite, Spritesheet, AnimatedSprite} from "pixi.js";
import swordsmanImage from './swordsman.png';


const canvas = useTemplateRef<HTMLCanvasElement>('canvas')
let app: PIXI.Application | null = null
let joystick: any = null  // Типизация nipplejs
const playerPosition = { x: 400, y: 300 }  // Позиция игрока на арене


onMounted(async () => {
  if (!canvas.value) return

  // Инициализация PixiJS (как в прошлом примере)
  app = new PIXI.Application()
  await app.init({
    canvas: canvas.value,
    width: 800,
    height: 600,
    backgroundColor: 0x1099bb,
    antialias: true
  })
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
  const frameWidth = 64;   // Ширина одного кадра
  const frameHeight = 64;  // Высота одного кадра
  const framesCount = 3;   // Количество кадров анимации

  // Создаем массив текстур для анимации
  const runTextures = [];
  for (let i = 0; i < framesCount; i++) {
    // Создаем прямоугольник для каждого кадра
    const frame = new PIXI.Rectangle(i * frameWidth, 0, frameWidth, frameHeight);
    const runTexture = new PIXI.Texture({ source: texture.source, frame });
    runTextures.push(runTexture);
  }

  // const atlasData = {
  //   frames: {
  //     run1: {
  //       frame: {x:0,y:0, w:40, h:50},
  //       sourceSize: {w: 40, h: 40},
  //       spriteSourceSize: {x:0, y: 0, w:40, h:50},
  //     },
  //     run2: {
  //       frame: {x:60,y:0, w:40, h:50},
  //       sourceSize: {w: 40, h: 40},
  //       spriteSourceSize: {x:60, y: 0, w:40, h:50},
  //     },
  //     run3: {
  //       frame: {x:120,y:0, w:40, h:50},
  //       sourceSize: {w: 40, h: 40},
  //       spriteSourceSize: {x:120, y: 0, w:40, h:50},
  //     }
  //   },
  //   meta: {
  //     image: 'swordsman.png',
  //     size: {w:40, h:40}
  //   },
  //   animations: {
  //     run: ['run1', 'run2', 'run3']
  //   },
  // };
  //
  // // const texture = await Assets.load('swordsman.png');
  // // const spriteSheet = new Spritesheet(texture, atlasData);
  // // await spriteSheet.parse();
  //
  // const playerSprite = new AnimatedSprite(spriteSheet.animations.run)
  // app.stage.addChild(playerSprite);
  // playerSprite.play();
  const playerSprite = new PIXI.AnimatedSprite(runTextures);
  playerSprite.animationSpeed = 0.2;
  playerSprite.play();
  playerSprite.anchor.set(0.5)
  playerSprite.x = playerPosition.x
  playerSprite.y = playerPosition.y
  app.stage.addChild(playerSprite)

  const movement = {
    x: 0,
    y: 0,
    active: false
  }


  // Инициализация джойстика
  const zone = document.getElementById('joystick-zone')
  if (zone) {
    joystick = nipplejs.create({
      zone: zone,
      mode: 'semi',  // Джойстик появляется при касании
      color: '#00ff00',  // Зелёный
      size: 120,  // Размер
      threshold: 0.1,  // Чувствительность
      multitouch: false
    })

    // Слушаем события джойстика
    joystick.on('move', (evt, data) => {
      movement.x = data.vector.x
      movement.y = data.vector.y
      movement.active = true
    })

    joystick.on('end', () => {
      movement.x = 0
      movement.y = 0
      movement.active = false
    })
  }

  // Игровой цикл
  app.ticker.add(() => {
    if (movement.active) {
      const speed = 2
      playerPosition.x -= movement.x * speed * -1
      playerPosition.y -= movement.y * speed
      playerSprite.x = playerPosition.x
      playerSprite.y = playerPosition.y
      arena.constrainPlayer(playerSprite);
      // Отправляем позицию на бэк через WebSocket
      // ws.send(JSON.stringify({ type: 'move', x: playerPosition.x, y: playerPosition.y }))
    }
  })
})

onUnmounted(() => {
  if (app) {
    app.destroy(true)
    app = null
  }
  if (joystick) {
    joystick.destroy()
    joystick = null
  }
})


class Arena {
  constructor(centerX, centerY, radius) {
    this.centerX = centerX;
    this.centerY = centerY;
    this.radius = radius;
  }

  // Проверка и коррекция позиции игрока
  constrainPlayer(player) {
    const dx = player.x - this.centerX;
    const dy = player.y - this.centerY;
    const distance = Math.sqrt(dx * dx + dy * dy);

    // Если игрок за пределами арены
    if (distance > this.radius) {
      // Возвращаем его к границе арены
      const angle = Math.atan2(dy, dx);
      player.x = this.centerX + Math.cos(angle) * this.radius;
      player.y = this.centerY + Math.sin(angle) * this.radius;
    }
  }

  // Визуализация арены (опционально)
  draw(graphics) {
    graphics.clear();

    // Зеленая заливка
    graphics.beginFill(0x00FF00, 0.3); // Зеленый с прозрачностью 30%
    graphics.drawCircle(this.centerX, this.centerY, this.radius);
    graphics.endFill();

    // Граница
    graphics.lineStyle(2, 0x00FF00);
    graphics.drawCircle(this.centerX, this.centerY, this.radius);
  }
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
  bottom: 20px;
  right: 20px;
  width: 150px;
  height: 150px;
  z-index: 10;
}
</style>


