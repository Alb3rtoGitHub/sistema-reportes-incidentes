package com.example.sistemareportesincidentes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IncidenteDTO {
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    private Long idTecnico;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private Integer tiempoEstimadoResolucion;
    private String estado;

    @Valid
    private List<IncidenteDetalleDTO> incidentesDetalles;
}
