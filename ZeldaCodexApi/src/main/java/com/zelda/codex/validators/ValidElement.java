package com.zelda.codex.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ElementValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidElement {
    String message() default "Elemento inválido. Los elementos válidos son: FIRE, ICE, SHOCK, NONE o null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}