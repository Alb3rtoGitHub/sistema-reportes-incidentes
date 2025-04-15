package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.entity.Especialidad;

import java.util.List;

public interface EspecialidadService {
    List<EspecialidadDTO> findAllEspecialidades();

    EspecialidadDTO findEspecialidadById(Long id);

    EspecialidadDTO saveEspecialidad(EspecialidadDTO especialidadDTO);

    EspecialidadDTO updateEspecialidad(Long idEspecialidad, EspecialidadDTO especialidadDTO);

    void deleteEspecialidadById(Long id);

    // Metodo para obtener entidades por IDs
    List<Especialidad> obtenerEspecialidadesPorIds(List<Long> ids);
}
