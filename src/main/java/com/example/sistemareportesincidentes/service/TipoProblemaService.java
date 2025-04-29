package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.TipoProblemaDTO;
import com.example.sistemareportesincidentes.dto.TipoProblemaResponseDTO;

import java.util.List;

public interface TipoProblemaService {
    List<TipoProblemaResponseDTO> findAllTipoProblema();

    TipoProblemaResponseDTO findTipoProblemaById(Long id);

    TipoProblemaResponseDTO saveTipoProblema(TipoProblemaDTO tipoProblemaDTO);

    TipoProblemaResponseDTO updateTipoProblema(Long id, TipoProblemaDTO tipoProblemaDTO);

    void deleteTipoProblemaById(Long id);

    TipoProblemaResponseDTO asociarEspecialidades(Long idTipoProblema, List<Long> especialidadesIds);

    TipoProblemaResponseDTO desasociarEspecialidades(Long idTipoProblema, Long idEspecialidad);

    List<TipoProblemaResponseDTO> findTiposProblemaByEspecialidadId(Long idEspecialidad);
}
