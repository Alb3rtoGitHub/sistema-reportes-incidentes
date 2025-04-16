package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.dto.TecnicoResponseDTO;
import com.example.sistemareportesincidentes.entity.Tecnico;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.TecnicoRepository;
import com.example.sistemareportesincidentes.service.EspecialidadService;
import com.example.sistemareportesincidentes.service.TecnicoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private EspecialidadService especialidadService;

    @Override
    public List<TecnicoResponseDTO> findAllTecnicos() {
        return tecnicoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TecnicoResponseDTO findTecnicoById(Long id) {
        Tecnico tecnico = tecnicoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Tecnico", "id", id));
        return convertToResponseDTO(tecnico);
    }

    @Override
    @Transactional
    public TecnicoResponseDTO saveTecnico(TecnicoDTO tecnicoDTO) {
        // Verificar si ya existe un técnico con el mismo email nombre
        Optional<Tecnico> tecnicoExistente = tecnicoRepository.findTecnicoByNombre(tecnicoDTO.getNombre());


        return null;
    }

    @Override
    public TecnicoResponseDTO updateTecnico(Long id, TecnicoDTO tecnicoDTO) {
        return null;
    }

    @Override
    public void deleteTecnicoById(Long id) {

    }

    @Override
    public TecnicoResponseDTO asociarEspecialidades(Long idTecnico, List<Long> especialidadesIds) {
        return null;
    }

    @Override
    public TecnicoResponseDTO desasociarEspecialidades(Long idTecnico, Long idEspecialidad) {
        return null;
    }

    // Metodo privado auxiliar para convertir entidad a DTO de respuesta
    private TecnicoResponseDTO convertToResponseDTO(Tecnico tecnico) {
        TecnicoResponseDTO tecnicoResponseDTO = new TecnicoResponseDTO();
        tecnicoResponseDTO.setId(tecnico.getIdTecnico());
        tecnicoResponseDTO.setNombre(tecnico.getNombre());
        tecnicoResponseDTO.setEmail(tecnico.getEmail());
        tecnicoResponseDTO.setWhatsapp(tecnico.getWhatsapp());
        tecnicoResponseDTO.setMedioPreferido(tecnico.getMedioPreferido());

        // Convertir especialidades a DTOs
        Set<EspecialidadDTO> especialidadesDTO = tecnico.getEspecialidades().stream()
                .map(especialidad -> {
                    EspecialidadDTO especialidadDTO = new EspecialidadDTO();
                    especialidadDTO.setId(especialidad.getIdEspecialidad());
                    especialidadDTO.setNombre(especialidad.getNombre());
                    return especialidadDTO;
                })
                .collect(Collectors.toSet());
        tecnicoResponseDTO.setEspecialidades(especialidadesDTO);

        return tecnicoResponseDTO;
    }
}
