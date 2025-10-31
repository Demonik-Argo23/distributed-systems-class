package com.zelda.codex.validators;

import com.zelda.codex.models.WeaponType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WeaponTypeValidator implements ConstraintValidator<ValidWeaponType, WeaponType> {

    @Override
    public void initialize(ValidWeaponType constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(WeaponType value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // @NotNull should handle this
        }

        // Verificar que el valor sea uno de los tipos válidos
        try {
            WeaponType.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}