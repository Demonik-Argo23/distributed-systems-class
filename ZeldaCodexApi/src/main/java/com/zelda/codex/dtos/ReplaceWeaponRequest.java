package com.zelda.codex.dtos;

import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request para reemplazo completo de un arma")
public class ReplaceWeaponRequest {

    @NotBlank(message = "El nombre del arma es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del arma", example = "Master Sword", required = true)
    private String name;

    @NotNull(message = "El tipo de arma es obligatorio")
    @Schema(description = "Tipo de arma", example = "ONE_HANDED_SWORD", required = true)
    private WeaponType weaponType;

    @NotNull(message = "El daño es obligatorio")
    @Min(value = 1, message = "El daño debe ser al menos 1")
    @Max(value = 999, message = "El daño no puede exceder 999")
    @Schema(description = "Puntos de daño del arma", example = "30", required = true)
    private Integer damage;

    @NotNull(message = "La durabilidad es obligatoria")
    @Min(value = 1, message = "La durabilidad debe ser al menos 1")
    @Max(value = 9999, message = "La durabilidad no puede exceder 9999")
    @Schema(description = "Puntos de durabilidad del arma", example = "200", required = true)
    private Integer durability;

    @Schema(description = "Elemento del arma (opcional)", example = "FIRE")
    private Element element;

    // Constructor vacío
    public ReplaceWeaponRequest() {}

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