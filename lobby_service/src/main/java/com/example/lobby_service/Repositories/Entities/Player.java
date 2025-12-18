package com.example.lobby_service.Repositories.Entities;

public class Player {
    private String username;
    private int x;
    private int y;
    //статы персонажа
    private float health;
    private float move_speed;
    private float attack_speed;
    private float melee_power;
    private float melee_range;
    private float knockback_power;
    private float vampirism;
    private float heal_rate;
    private float dodge_chance;


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

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMove_speed() {
        return move_speed;
    }

    public void setMove_speed(float move_speed) {
        this.move_speed = move_speed;
    }

    public float getAttack_speed() {
        return attack_speed;
    }

    public void setAttack_speed(float attack_speed) {
        this.attack_speed = attack_speed;
    }

    public float getMelee_power() {
        return melee_power;
    }

    public void setMelee_power(float melee_power) {
        this.melee_power = melee_power;
    }

    public float getMelee_range() {
        return melee_range;
    }

    public void setMelee_range(float melee_range) {
        this.melee_range = melee_range;
    }

    public float getKnockback_power() {
        return knockback_power;
    }

    public void setKnockback_power(float knockback_power) {
        this.knockback_power = knockback_power;
    }

    public float getVampirism() {
        return vampirism;
    }

    public void setVampirism(float vampirism) {
        this.vampirism = vampirism;
    }

    public float getHeal_rate() {
        return heal_rate;
    }

    public void setHeal_rate(float heal_rate) {
        this.heal_rate = heal_rate;
    }

    public float getDodge_chance() {
        return dodge_chance;
    }

    public void setDodge_chance(float dodge_chance) {
        this.dodge_chance = dodge_chance;
    }
}
