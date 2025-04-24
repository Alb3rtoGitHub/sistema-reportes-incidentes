package com.example.sistemareportesincidentes.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TipoProblemaResponseDTO {
    private Long id;
    private String nombre;
    private Integer tiempoEstimadoResolucion;
    private Integer tiempoMaximoResolucion;
    private Set<EspecialidadDTO> especialidadesRequeridas;
}
