package com.zelda.weapons.validator;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.zelda.weapons.model.Weapon;

@Component
public class WeaponValidator {

    public void validateWeaponId(UUID weaponId) {
        if (weaponId == null) {
            throw new WeaponValidationException("El ID del arma es obligatorio");
        }
    }

    public void validateWeaponData(Weapon weapon) {
        if (weapon == null) {
            throw new WeaponValidationException("Los datos del arma son obligatorios");
        }

        if (weapon.getName() == null || weapon.getName().trim().isEmpty()) {
            throw new WeaponValidationException("El nombre del arma es obligatorio");
        }

        if (weapon.getDamage() == null || weapon.getDamage() <= 0) {
            throw new WeaponValidationException("El daño debe ser mayor a 0");
        }

        if (weapon.getDurability() == null || weapon.getDurability() <= 0) {
            throw new WeaponValidationException("La durabilidad debe ser mayor a 0");
        }

        if (weapon.getWeaponType() == null) {
            throw new WeaponValidationException("El tipo de arma es obligatorio");
        }

        if (weapon.getDamage() > 999) {
            throw new WeaponValidationException("El daño no puede ser mayor a 999");
        }

        if (weapon.getDurability() > 999) {
            throw new WeaponValidationException("La durabilidad no puede ser mayor a 999");
        }

        if (weapon.getName().length() > 50) {
            throw new WeaponValidationException("El nombre del arma no puede tener más de 50 caracteres");
        }
    }
}