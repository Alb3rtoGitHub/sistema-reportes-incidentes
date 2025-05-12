package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.IncidenteDetalleDTO;

import java.util.List;

public interface IncidenteDetalleService {
    IncidenteDetalleDTO findIncidenteDetalleById(Long idIncidenteDetalle);

    List<IncidenteDetalleDTO> findIncidentesDetalleByIncidenteId(Long idIncidente);

    IncidenteDetalleDTO saveIncidenteDetalle(IncidenteDetalleDTO incidenteDetalleDTO);

    IncidenteDetalleDTO updateIncidenteDetalle(Long idIncidenteDetalle, IncidenteDetalleDTO incidenteDetalleDTO);

    void deleteIncidenteDetalle(Long idIncidenteDetalle);
}
