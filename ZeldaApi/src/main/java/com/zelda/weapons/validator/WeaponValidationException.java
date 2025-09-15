package com.zelda.weapons.validator;

public class WeaponValidationException extends RuntimeException {
    
    public WeaponValidationException(String message) {
        super(message);
    }
    
    public WeaponValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}