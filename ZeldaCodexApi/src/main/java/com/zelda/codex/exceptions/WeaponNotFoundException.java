package com.zelda.codex.exceptions;

import java.util.UUID;

public class WeaponNotFoundException extends RuntimeException {
    
    private final UUID weaponId;

    public WeaponNotFoundException(UUID weaponId) {
        super("Arma con ID " + weaponId + " no encontrada en el Codex");
        this.weaponId = weaponId;
    }

    public WeaponNotFoundException(String message) {
        super(message);
        this.weaponId = null;
    }

    public UUID getWeaponId() {
        return weaponId;
    }
}