package com.zelda.codex.dtos;

import com.zelda.codex.models.Element;
import com.zelda.codex.models.WeaponType;
import com.zelda.codex.validators.ValidElement;
import com.zelda.codex.validators.ValidWeaponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request para crear una nueva arma")

public class CreateWeaponRequest {
    
    @NotBlank(message = "El nombre del arma es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-']+$", message = "El nombre solo puede contener letras, números, espacios, guiones y apostrofes")
    @Schema(description = "Nombre del arma", example = "Master Sword", required = true)
    private String name;
    
    @NotNull(message = "El tipo de arma es obligatorio")
    @ValidWeaponType
    @Schema(description = "Tipo de arma", example = "ONE_HANDED_SWORD", required = true)
    private WeaponType weaponType;
    
    @NotNull(message = "El daño del arma es obligatorio")
    @Min(value = 1, message = "El daño debe ser al menos 1")
    @Max(value = 999, message = "El daño no puede exceder 999")
    @Schema(description = "Puntos de daño del arma", example = "30", required = true, minimum = "1", maximum = "999")
    private Integer damage;
    
    @NotNull(message = "La durabilidad del arma es obligatoria")
    @Min(value = 1, message = "La durabilidad debe ser al menos 1")
    @Max(value = 9999, message = "La durabilidad no puede exceder 9999")
    @Schema(description = "Puntos de durabilidad del arma", example = "200", required = true, minimum = "1", maximum = "9999")
    private Integer durability;
    
    @ValidElement
    @Schema(description = "Elemento del arma (opcional)", example = "FIRE")
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