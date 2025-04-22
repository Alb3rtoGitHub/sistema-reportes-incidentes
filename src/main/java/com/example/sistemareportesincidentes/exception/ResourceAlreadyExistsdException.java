package com.example.sistemareportesincidentes.exception;

public class ResourceAlreadyExistsdException extends RuntimeException {

    public ResourceAlreadyExistsdException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsdException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s ya existe con %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
