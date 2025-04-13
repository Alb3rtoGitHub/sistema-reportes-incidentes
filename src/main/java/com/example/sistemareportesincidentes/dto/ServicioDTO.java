package com.example.sistemareportesincidentes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServicioDTO {
    private Long id;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String nombre;
}
