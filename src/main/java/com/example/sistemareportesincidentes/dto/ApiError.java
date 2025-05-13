package com.example.sistemareportesincidentes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApiError {
    private String status;
    private int statusCode;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private String path;
    private List<String> errors = new ArrayList<>();

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(String status, int statusCode, String message) {
        this();
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
}
