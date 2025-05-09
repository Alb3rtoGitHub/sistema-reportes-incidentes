package com.example.sistemareportesincidentes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidenteDetalleDTO {
    private Long id;
    private String descripcion;

    @NotNull(message = "El servicio es obligatorio")
    private Long idServicio;

    @NotNull(message = "El tipo de problema es obligatorio")
    private Long idTipoProblema;

    // Puede ser null cuando se crea como parte de un nuevo incidente
    private Long idIncidente;
}
