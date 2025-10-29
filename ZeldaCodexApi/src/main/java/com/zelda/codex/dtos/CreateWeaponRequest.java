package com.zelda.codex.dtos;

import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateWeaponRequest {
    
    @NotBlank(message = "El nombre del arma es obligatorio")
    private String name;
    
    @NotNull(message = "El tipo de arma es obligatorio")
    private WeaponType weaponType;
    
    @NotNull(message = "El daño es obligatorio")
    @Min(value = 1, message = "El daño debe ser mayor a 0")
    private Integer damage;
    
    @NotNull(message = "La durabilidad es obligatoria")
    @Min(value = 1, message = "La durabilidad debe ser mayor a 0")
    private Integer durability;
    
    private Element element;

    // Constructor vacío
    public CreateWeaponRequest() {}

    // Constructor completo
    public CreateWeaponRequest(String name, WeaponType weaponType, Integer damage, Integer durability, Element element) {
        this.name = name;
        this.weaponType = weaponType;
        this.damage = damage;
        this.durability = durability;
        this.element = element;
    }

    // Getters y Setters
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