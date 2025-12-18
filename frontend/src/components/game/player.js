import {
  Texture,
  Assets,
  Rectangle,
  AnimatedSprite,
  Text,
  Graphics,
} from "pixi.js";
import runImage from "./swordsman_run.png";
import idleImage from "./swordsman_idle.png";
import attackImage from "./swordsman_attack.png";

export default class Player {
  constructor(x, y, vx, vy, name, direction, speed, hp) {
    this.name = name;
    this.x = x;
    this.vx = vx;
    this.vy = vy;
    this.y = y;
    this.direction = direction;
    this.speed = speed;
    this.movement = {
      x: 0,
      y: 0,
      active: false,
    };
    this.frameWidth = 64;
    this.frameHeight = 64;
    this.anims = new Map();
    this.lastDirection = this.direction;
    this.isAttacking = false;
    this.canAttack = true;
    this.hitbox = null;
    this.damage = 25;
    this.sprite = null;
    this.animationSpeed = 0.2;
    this.maxHp = hp;
    this.hp = hp;
    this.knockback_power = 3;
    this.knockback_friction = 0.9;
    this.isKnocked = false;
  }

  async loadAnims() {
    this.sprite = await this.setAnims();
    return this.sprite;
  }

  async loadAnims() {
    this.sprite = await this.setAnims();
    return this.sprite;
  }

  async loadHpBar() {
    this.hpBar = await this.setHpBar();
    return this.hpBar;
  }

  async loadHpBarBg() {
    this.hpBarBg = await this.setHpBarBg();
    return this.hpBarBg;
  }

  async loadName() {
    this.textName = await this.setTextName();
    return this.textName;
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

  async setHpBar() {
    let hpBar = new Graphics();
    hpBar.zIndex = 10;
    let hpPercent = this.hp / this.maxHp;
    hpBar.clear();
    hpBar.rect(-30, -55, 60 * hpPercent, 6);
    hpBar.fill(
      hpPercent > 0.5 ? 0x00ff00 : hpPercent > 0.2 ? 0xffaa00 : 0xff0000
    );
    return hpBar;
  }

  async setHpBarBg() {
    let hpBarBg = new Graphics();
    hpBarBg.zIndex = 9;
    hpBarBg.clear();
    hpBarBg.rect(-30, -55, 60, 6);
    hpBarBg.fill(0x333333);
    return hpBarBg;
  }

  async setTextName() {
    let nameText = new Text({
      text: this.name,
      style: {
        fontFamily: "Arial",
        fontSize: 14,
        fill: 0x969696,
        stroke: { color: 0x000000, width: 3 },
        fontWeight: "bold",
      },
    });
    nameText.zIndex = 100;
    // nameText.anchor.set(0.5, 0.75);
    return nameText;
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

    let sprite = new AnimatedSprite(idleAnim);
    sprite.anchor.set(0.5);
    sprite.animationSpeed = this.animationSpeed;
    sprite.play();

    return sprite;
  }

  changeMovement(x, y, status, direction, vx, vy) {
    if (status == true) {
      if (direction === null) {
        if (Math.abs(vy) > Math.abs(vx)) {
          this.direction = vy > 0 ? "up" : "down";
        } else {
          this.direction = vx > 0 ? "right" : "left";
        }
      } else {
        this.direction = direction;
        this.x = x;
        this.y = y;
      }
    }
    this.movement.active = status;
    this.movement.x = x;
    this.movement.y = y;
    if (status == true && this.direction !== this.lastDirection) {
      this.lastDirection = this.direction; //0.2
      this.sprite.animationSpeed = this.animationSpeed;
      this.playRun();
    }
  }
  move() {
    if (this.isAttacking) return;
    this.x += this.movement.x * this.speed;
    this.y -= this.movement.y * this.speed;
  }

  stop() {
    this.animationSpeed = 0.08;
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
    //0.05
    this.sprite.animationSpeed = this.animationSpeed;
    this.sprite.play();
  }

  playRun() {
    this.sprite.textures = this.anims.get("run").get(this.direction);
    this.sprite.play();
  }

  playAttack() {
    this.sprite.textures = this.anims.get("attack").get(this.direction);
    //0.2
    this.sprite.animationSpeed = this.animationSpeed;
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
