package com.example.lobby_service.Repositories.Entities;

public class Player {
    private String username;
    private int x;
    private int y;
    //статы персонажа
    private double health = 75;
    private double move_speed = 0.75;
    private double attack_speed = 80;
    private double melee_power = 20;
    private double melee_range = 20;
    private double knockback_power = 1.5;
    private double knockback_friction= 0.95;



    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getMove_speed() {
        return move_speed;
    }

    public void setMove_speed(double move_speed) {
        this.move_speed = move_speed;
    }

    public double getAttack_speed() {
        return attack_speed;
    }

    public void setAttack_speed(double attack_speed) {
        this.attack_speed = attack_speed;
    }

    public double getMelee_power() {
        return melee_power;
    }

    public void setMelee_power(double melee_power) {
        this.melee_power = melee_power;
    }

    public double getMelee_range() {
        return melee_range;
    }

    public void setMelee_range(double melee_range) {
        this.melee_range = melee_range;
    }

    public double getKnockback_power() {
        return knockback_power;
    }

    public void setKnockback_power(double knockback_power) {
        this.knockback_power = knockback_power;
    }


    public double getKnockback_friction() {
        return knockback_friction;
    }

    public void setKnockback_friction(double knockback_friction) {
        this.knockback_friction = knockback_friction;
    }
}
