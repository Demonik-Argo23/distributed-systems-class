package com.zelda.weapons.validator;

/**
 * Excepción lanzada cuando se intenta crear un arma que ya existe
 */
public class WeaponAlreadyExistsException extends RuntimeException {

    public WeaponAlreadyExistsException(String message) {
        super(message);
    }

    public WeaponAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}