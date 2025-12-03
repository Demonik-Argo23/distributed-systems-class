package com.zelda.codex.validators;

import com.zelda.codex.models.Element;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ElementValidator implements ConstraintValidator<ValidElement, Element> {

    @Override
    public void initialize(ValidElement constraintAnnotation) {
    }

    @Override
    public boolean isValid(Element value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Element.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}