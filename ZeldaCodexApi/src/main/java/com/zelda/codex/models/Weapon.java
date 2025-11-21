package com.zelda.codex.models;

import java.util.UUID;

public class Weapon {
    private UUID id;
    private String name;
    private WeaponType weaponType;
    private Integer damage;
    private Integer durability;
    private Element element;

    // Constructor vacío
    public Weapon() {}

    // Constructor completo
    public Weapon(UUID id, String name, WeaponType weaponType, Integer damage, Integer durability, Element element) {
        this.id = id;
        this.name = name;
        this.weaponType = weaponType;
        this.damage = damage;
        this.durability = durability;
        this.element = element;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}