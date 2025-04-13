package com.example.sistemareportesincidentes.exception;

import jakarta.validation.constraints.NotBlank;

public class ResourceAlreadyExistsdException extends RuntimeException {

    public ResourceAlreadyExistsdException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsdException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s ya existe con %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
