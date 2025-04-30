package com.example.sistemareportesincidentes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TipoProblemaDTO {
    private Long id;

    @NotBlank(message = "El nombre del tipo de problema es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El tiempo estimado de resolución es obligatorio")
    @Min(value = 1, message = "El tiempo estimado debe ser mayor a 0")
    private Integer tiempoEstimadoResolucion;

    @NotNull(message = "El tiempo máximo de resolución es obligatorio")
    @Min(value = 1, message = "El tiempo máximo debe ser mayor a 0")
    private Integer tiempoMaximoResolucion;

    private List<Long> especialidadesIds;
}
