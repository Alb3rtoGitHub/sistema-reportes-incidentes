package com.example.sistemareportesincidentes.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CuitValidator implements ConstraintValidator<ValidCuit, String> {
    @Override
    public void initialize(ValidCuit constraintAnnotation) {
        // TODO
        ConstraintValidator.super.initialize(constraintAnnotation); // No se si esto funciona ver
    }

    @Override
    public boolean isValid(String cuit, ConstraintValidatorContext constraintValidatorContext) {
        if (cuit == null || cuit.isEmpty()) {
            return false;
        }

        // Verificar formato XX-XXXXXXXX-X
        if (!cuit.matches("^\\d{2}-\\d{8}-\\d{1}$")) {
            return false;
        }

        // Eliminar guiones
        String cuitSinGuiones = cuit.replace("-", "");

        // Verificar digito verificador
        int[] multiplicadores = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

        int suma = 0;
        for (int i = 0; i < 10; i++) {
            suma += Character.getNumericValue(cuitSinGuiones.charAt(i)) * multiplicadores[i];
        }

        int resto = suma % 11;
        int digitoVerificador = 11 - resto;

        // Si el dígito verificador es 11, se reemplaza por 0
        if (digitoVerificador == 11) {
            digitoVerificador = 0;
        }

        // Comparar con el último dígito del CUIT
        return digitoVerificador == Character.getNumericValue(cuitSinGuiones.charAt(10));
    }
}
