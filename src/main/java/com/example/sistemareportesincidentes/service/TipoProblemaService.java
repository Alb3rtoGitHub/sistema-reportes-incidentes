package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.TipoProblemaDTO;
import com.example.sistemareportesincidentes.dto.TipoProblemaResponseDTO;
import com.example.sistemareportesincidentes.entity.TipoProblema;

import java.util.List;

public interface TipoProblemaService {
    List<TipoProblemaResponseDTO> findAllTipoProblema();

    TipoProblemaResponseDTO findTipoProblemaById(Long id);

    TipoProblemaResponseDTO saveTipoProblema(TipoProblemaDTO tipoProblemaDTO);

    TipoProblemaResponseDTO updateTipoProblema(Long id, TipoProblemaDTO tipoProblemaDTO);

    void deleteTipoProblema(Long id);

    TipoProblemaResponseDTO asociarEspecialidades(Long idTipoProblema, List<Long> especialidadesIds);

    TipoProblemaResponseDTO desasociarEspecialidad(Long idTipoProblema, Long idEspecialidad);

    List<TipoProblema> findTiposProblemaPorEspecialidad(Long idEspecialidad);
}
