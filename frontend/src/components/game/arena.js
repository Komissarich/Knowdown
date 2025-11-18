class Arena {
  constructor(centerX, centerY, radius) {
    this.centerX = centerX;
    this.centerY = centerY;
    this.radius = radius;
  }
  constrainPlayer(player) {
    const dx = player.x - this.centerX;
    const dy = player.y - this.centerY;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance > this.radius) {
      const angle = Math.atan2(dy, dx);
      player.x = this.centerX + Math.cos(angle) * this.radius;
      player.y = this.centerY + Math.sin(angle) * this.radius;
    }
  }

  draw(graphics) {
    graphics.clear();

    graphics.beginFill(0x00ff00, 0.3);
    graphics.drawCircle(this.centerX, this.centerY, this.radius);
    graphics.endFill();
    graphics.lineStyle(2, 0x00ff00);
    graphics.drawCircle(this.centerX, this.centerY, this.radius);
  }
}

export default Arena;
