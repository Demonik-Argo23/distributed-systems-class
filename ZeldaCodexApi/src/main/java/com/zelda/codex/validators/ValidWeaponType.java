package com.zelda.codex.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WeaponTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWeaponType {
    String message() default "Tipo de arma inválido. Los tipos válidos son: BAT, BOOMERANG, CLUB, HAMMER, ROD, SPEAR, ONE_HANDED_SWORD, TWO_HANDED_SWORD, OTHER";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}