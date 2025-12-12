import { Texture, Assets, Rectangle, AnimatedSprite } from "pixi.js";
import runImage from "./swordsman_run.png";
import idleImage from "./swordsman_idle.png";
import attackImage from "./swordsman_attack.png";

export default class Player {
  constructor() {
    this.x = 400;
    this.vx = 0;
    this.vy = 0;
    this.y = 300;
    this.direction = "down";
    this.speed = 1;
    this.movement = {
      x: 0,
      y: 0,
      active: true,
    };
    this.frameWidth = 64;
    this.frameHeight = 64;
    this.anims = new Map();
    this.lastDirection = this.direction;
    this.isAttacking = false;
    this.canAttack = true;
    this.hitbox = null;
    this.damage = 25;
  }

  async loadAnims() {
    await this.setAnims();
    this.sprite.x = 400;
    this.sprite.y = 300;
    return this.sprite;
  }

  getSprites(row, texture, framesCount) {
    let sprites = [];

    for (let i = 0; i < framesCount; i++) {
      let frame = new Rectangle(
        i * this.frameWidth,
        row * this.frameHeight,
        this.frameWidth,
        this.frameHeight
      );
      let new_sprite = new Texture({ source: texture.source, frame });
      sprites.push(new_sprite);
    }
    return sprites;
  }

  async setAnims() {
    this.idleTexture = await Assets.load(idleImage);
    this.runTexture = await Assets.load(runImage);
    this.attackTexture = await Assets.load(attackImage);

    let idleAnim = this.getSprites(0, this.idleTexture, 11);

    let runDownAnim = this.getSprites(0, this.runTexture, 7);
    let runUpAnim = this.getSprites(3, this.runTexture, 7);
    let runLeftAnim = this.getSprites(1, this.runTexture, 7);
    let runRightAnim = this.getSprites(2, this.runTexture, 7);

    let attackDownAnim = this.getSprites(0, this.attackTexture, 7);
    let attackLeftAnim = this.getSprites(1, this.attackTexture, 7);
    let attackRightAnim = this.getSprites(2, this.attackTexture, 7);
    let attackUpAnim = this.getSprites(3, this.attackTexture, 7);

    let idleLeftAnim = this.getSprites(1, this.idleTexture, 11);
    let idleRightAnim = this.getSprites(2, this.idleTexture, 11);
    let idleUpAnim = this.getSprites(3, this.idleTexture, 3);
    let runAnims = new Map();
    runAnims.set("down", runDownAnim);
    runAnims.set("up", runUpAnim);
    runAnims.set("left", runLeftAnim);
    runAnims.set("right", runRightAnim);

    let idleAnims = new Map();
    idleAnims.set("down", idleAnim);
    idleAnims.set("left", idleLeftAnim);
    idleAnims.set("right", idleRightAnim);
    idleAnims.set("up", idleUpAnim);

    let attackAnims = new Map();
    attackAnims.set("down", attackDownAnim);
    attackAnims.set("left", attackLeftAnim);
    attackAnims.set("right", attackRightAnim);
    attackAnims.set("up", attackUpAnim);

    this.anims.set("idle", idleAnims);
    this.anims.set("run", runAnims);
    this.anims.set("attack", attackAnims);

    this.sprite = new AnimatedSprite(idleAnim);
    this.sprite.anchor.set(0.5);
    this.sprite.animationSpeed = 0.08;
    this.sprite.play();
  }

  changeMovement(x, y, status, vx, vy) {
    if (status == true) {
      if (Math.abs(vy) > Math.abs(vx)) {
        this.direction = vy > 0 ? "up" : "down";
      } else {
        this.direction = vx > 0 ? "right" : "left";
      }
    }
    this.movement.active = status;
    this.movement.x = x;
    this.movement.y = y;
    if (status == true && this.direction !== this.lastDirection) {
      this.lastDirection = this.direction;
      this.sprite.animationSpeed = 0.2;
      this.playRun();
    }
  }
  move() {
    if (this.isAttacking) return;
    this.x += this.movement.x * this.speed;
    this.y -= this.movement.y * this.speed;
  }

  stop() {
    if (!this.isAttacking) this.playIdle();
  }

  attack() {
    if (this.isAttacking || !this.canAttack) return;

    this.isAttacking = true;
    this.canAttack = false;

    this.playAttack();
    this.hitbox = new Rectangle(0, 0, 40, 30);

    setTimeout(() => {
      this.isAttacking = false;
      this.canAttack = true;
      this.stop();
      this.hitbox = null;
    }, 500);
  }

  playIdle() {
    this.sprite.textures = this.anims.get("idle").get(this.direction);
    this.sprite.animationSpeed = 0.04;
    this.sprite.play();
  }

  playRun() {
    this.sprite.textures = this.anims.get("run").get(this.direction);

    this.sprite.play();
  }

  playAttack() {
    this.sprite.textures = this.anims.get("attack").get(this.direction);
    this.sprite.animationSpeed = 0.2;
    this.sprite.play();
  }

  updateSprite() {
    this.sprite.x = this.x;
    this.sprite.y = this.y;
  }

  getSprite() {
    return this.sprite;
  }
}
