package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.dto.TecnicoResponseDTO;

import java.util.List;

public interface TecnicoService {
    List<TecnicoResponseDTO> findAllTecnicos();

    TecnicoResponseDTO findTecnicoById(Long id);

    TecnicoResponseDTO saveTecnico(TecnicoDTO tecnicoDTO);

    TecnicoResponseDTO updateTecnico(Long id, TecnicoDTO tecnicoDTO);

    void deleteTecnicoById(Long id);

    TecnicoResponseDTO asociarEspecialidades(Long idTecnico, List<Long> especialidadesIds);

    TecnicoResponseDTO desasociarEspecialidades(Long idTecnico, Long idEspecialidad);

    List<TecnicoResponseDTO> findTecnicosByEspecialidad(Long idEspecialidad);

    List<Object[]> obtenerTecnicosConMasIncidentesResueltos(int dias);

    List<Object[]> obtenerTecnicosConMasIncidentesResueltosporEspecialidad(int dias, Long idEspecialidad);

    List<Object[]> obtenerTecnicosConMenorTiempoResolucion();
}
