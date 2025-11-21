package com.zelda.codex.exceptions;

public class WeaponAlreadyExistsException extends RuntimeException {
    
    private final String weaponName;

    public WeaponAlreadyExistsException(String weaponName) {
        super("Ya existe un arma con el nombre '" + weaponName + "' en el Codex");
        this.weaponName = weaponName;
    }

    public String getWeaponName() {
        return weaponName;
    }
}