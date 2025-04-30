package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.entity.Especialidad;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.exception.ResourceAlreadyExistsException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.EspecialidadRepository;
import com.example.sistemareportesincidentes.service.EspecialidadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Override
    public List<EspecialidadDTO> findAllEspecialidades() {
        return especialidadRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EspecialidadDTO findEspecialidadById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", id));
        return convertToDTO(especialidad);
    }

    @Override
    @Transactional
    public EspecialidadDTO saveEspecialidad(EspecialidadDTO especialidadDTO) {
        // Verificar si ya existe una especialidad con el mismo nombre
        if (especialidadRepository.existsEspecialidadByNombre(especialidadDTO.getNombre())) {
            throw new ResourceAlreadyExistsException("Especialidad", "nombre", especialidadDTO.getNombre());
        }

        Especialidad especialidad = Especialidad.builder()
                .nombre(especialidadDTO.getNombre())
                .build();

        Especialidad especialidadGuardada = especialidadRepository.save(especialidad);
        return convertToDTO(especialidadGuardada);
    }

    @Override
    @Transactional
    public EspecialidadDTO updateEspecialidad(Long idEspecialidad, EspecialidadDTO especialidadDTO) {
        Especialidad especialidad = especialidadRepository.findById(idEspecialidad).orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", idEspecialidad));

        // Verificar si el nuevo nombre ya existe en otra especialidad
        if (!especialidad.getNombre().equals(especialidadDTO.getNombre()) && especialidadRepository.existsEspecialidadByNombre(especialidadDTO.getNombre())) {
            throw new ResourceAlreadyExistsException("Especialidad", "nombre", especialidadDTO.getNombre());
        }

        especialidad.setNombre(especialidadDTO.getNombre());

        Especialidad especialidadActualizada = especialidadRepository.save(especialidad);
        return convertToDTO(especialidadActualizada);
    }

    @Override
    @Transactional
    public void deleteEspecialidadById(Long idEspecialidad) {
        if (!especialidadRepository.existsById(idEspecialidad)) {
            throw new ResourceNotFoundException("Especialidad", "id", idEspecialidad);
        }

        // Verificar si la especialidad está siendo utilizada por técnicos
        if (especialidadRepository.existsTecnicoByEspecialidadId(idEspecialidad)) {
            throw new BadRequestException("No se puede eliminar la especialidad porque está asignada a uno o más técnicos");
        }

        // Verificar si la especialidad está siendo utilizada por tipos de problemas
        if (especialidadRepository.existsTipoProblemaByEspecialidadId(idEspecialidad)) {
            throw new BadRequestException("No se puede eliminar la especialidad porque está asociada a uno o más tipos de problemas");
        }

        especialidadRepository.deleteById(idEspecialidad);
    }

    @Override
    public List<Especialidad> obtenerEspecialidadesPorIds(List<Long> ids) {
        List<Especialidad> especialidades = especialidadRepository.findAllById(ids);
        if (especialidades.size() != ids.size()) {
            throw new ResourceNotFoundException("Una o más especialidades no fueron encontradas");
        }
        return especialidades;
    }

    // Metodo privado auxiliar para convertir entidad a DTO
    private EspecialidadDTO convertToDTO(Especialidad especialidad) {
        EspecialidadDTO especialidadDTO = new EspecialidadDTO();
        especialidadDTO.setId(especialidad.getIdEspecialidad());
        especialidadDTO.setNombre(especialidad.getNombre());
        return especialidadDTO;
    }
}
