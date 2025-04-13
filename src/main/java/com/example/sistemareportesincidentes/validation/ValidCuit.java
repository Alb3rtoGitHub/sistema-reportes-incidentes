package com.example.sistemareportesincidentes.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CuitValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCuit {
    String message() default "CUIT no válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
