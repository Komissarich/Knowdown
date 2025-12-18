package com.example.lobby_service.Repositories.Entities;

public class AttackResponse {
    private String  target_username;
    private String knockback_direction;
    private int  knockback_power;
    private int damage;

    public String getTarget_username() {
        return target_username;
    }

    public void setTarget_username(String target_username) {
        this.target_username = target_username;
    }

    public String getKnockback_direction() {
        return knockback_direction;
    }

    public void setKnockback_direction(String knockback_direction) {
        this.knockback_direction = knockback_direction;
    }

    public int getKnockback_power() {
        return knockback_power;
    }

    public void setKnockback_power(int knockback_power) {
        this.knockback_power = knockback_power;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
