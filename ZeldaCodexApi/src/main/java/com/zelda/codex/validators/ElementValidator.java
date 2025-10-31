package com.zelda.codex.validators;

import com.zelda.codex.models.Element;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ElementValidator implements ConstraintValidator<ValidElement, Element> {

    @Override
    public void initialize(ValidElement constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Element value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Element is optional
        }

        // Verificar que el valor sea uno de los elementos válidos
        try {
            Element.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}