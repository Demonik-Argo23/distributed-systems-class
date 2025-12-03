package com.zelda.codex.dtos;

import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import com.zelda.codex.validators.ValidElement;
import com.zelda.codex.validators.ValidWeaponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request para actualización parcial de un arma")
public class UpdateWeaponRequest {

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del arma", example = "Master Sword")
    private String name;

    @ValidWeaponType
    @Schema(description = "Tipo de arma", example = "ONE_HANDED_SWORD")
    private WeaponType weaponType;

    @Min(value = 1, message = "El daño debe ser al menos 1")
    @Max(value = 999, message = "El daño no puede exceder 999")
    @Schema(description = "Puntos de daño del arma", example = "30")
    private Integer damage;

    @Min(value = 1, message = "La durabilidad debe ser al menos 1")
    @Max(value = 9999, message = "La durabilidad no puede exceder 9999")
    @Schema(description = "Puntos de durabilidad del arma", example = "200")
    private Integer durability;

    @ValidElement
    @Schema(description = "Elemento del arma", example = "FIRE")
    private Element element;

    // Constructor vacío
    public UpdateWeaponRequest() {}

    // Getters y setters
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