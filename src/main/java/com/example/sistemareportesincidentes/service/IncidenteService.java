package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.IncidenteDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidenteService {
    List<IncidenteDTO> findAllIncidentes();
    IncidenteDTO findIncidenteById(Long id);
    IncidenteDTO crearIncidente(IncidenteDTO incidenteDTO);
    IncidenteDTO asignarTecnico(Long idIncidente, Long idTecnico);
    IncidenteDTO resolverIncidente(Long idIncidente);
    List<IncidenteDTO> obtenerIncidentesPorTecnicoYFecha(Long idTecnico, LocalDateTime fecha);
    List<TecnicoDTO> obtenerTecnicosDisponibles(Long idEspecialidad);
    List<IncidenteDTO> obtenerIncidentesPorServicio(Long idServicio);
    List<IncidenteDTO> obtenerIncidentesPorTipoProblema(Long idTipoProblema);

}
