import PIXI from 'pixi.js';
const Application = PIXI.Application;

const app = new Application({
  width: 800,
  height: 500,
  transparent: true,
  antialias: true
});

app.renderer.background.color = 0x7684aaff
document.body.appendChild(app.view);

