package com.zelda.codex.validators;

import com.zelda.codex.models.WeaponType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WeaponTypeValidator implements ConstraintValidator<ValidWeaponType, WeaponType> {

    @Override
    public void initialize(ValidWeaponType constraintAnnotation) {
    }

    @Override
    public boolean isValid(WeaponType value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            WeaponType.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}