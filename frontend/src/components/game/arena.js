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
    graphics.beginFill(0x00ff00, 0.3); // Зеленый с прозрачностью 30%
    graphics.drawCircle(this.centerX, this.centerY, this.radius);
    graphics.endFill();

    // Граница
    graphics.lineStyle(2, 0x00ff00);
    graphics.drawCircle(this.centerX, this.centerY, this.radius);
  }
}

export default Arena;
