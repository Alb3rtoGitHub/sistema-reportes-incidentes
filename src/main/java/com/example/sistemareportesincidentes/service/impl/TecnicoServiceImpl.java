package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.dto.TecnicoResponseDTO;
import com.example.sistemareportesincidentes.entity.Especialidad;
import com.example.sistemareportesincidentes.entity.Tecnico;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.exception.ResourceAlreadyExistsException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.TecnicoRepository;
import com.example.sistemareportesincidentes.service.EspecialidadService;
import com.example.sistemareportesincidentes.service.TecnicoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
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
        // Verificar si ya existe un técnico con el mismo nombre
        Optional<Tecnico> tecnicoExistente = tecnicoRepository.findTecnicoByNombre(tecnicoDTO.getNombre());
        if (tecnicoExistente.isPresent()) {
            throw new ResourceAlreadyExistsException("Tecnico", "nombre", tecnicoDTO.getNombre());
        }

        Tecnico tecnico = Tecnico.builder()
                .nombre(tecnicoDTO.getNombre())
                .email(tecnicoDTO.getEmail())
                .whatsapp(tecnicoDTO.getWhatsapp())
                .medioPreferido(tecnicoDTO.getMedioPreferido())
                .especialidades(new HashSet<>())
                .build();

        // Asociar especialidades si se proporcionaron IDs
        if (tecnicoDTO.getEspecialidadesIds() != null && !tecnicoDTO.getEspecialidadesIds().isEmpty()) {
            List<Especialidad> especialidades = especialidadService.obtenerEspecialidadesPorIds(tecnicoDTO.getEspecialidadesIds());
            tecnico.setEspecialidades(new HashSet<>(especialidades));
        }

        Tecnico tecnicoGuardado = tecnicoRepository.save(tecnico);
        return convertToResponseDTO(tecnicoGuardado);
    }

    @Override
    @Transactional
    public TecnicoResponseDTO updateTecnico(Long id, TecnicoDTO tecnicoDTO) {
        Tecnico tecnico = tecnicoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tecnico", "id", id));

        // Verificar si el nuevo nombre ya existe en otro técnico
        Optional<Tecnico> tecnicoConMismoNombre = tecnicoRepository.findTecnicoByNombre(tecnicoDTO.getNombre());
        if (tecnicoConMismoNombre.isPresent() && !tecnicoConMismoNombre.get().getIdTecnico().equals(id)) {
            throw new ResourceAlreadyExistsException("Tecnico", "nombre", tecnicoDTO.getNombre());
        }

        tecnico.setNombre(tecnicoDTO.getNombre());
        tecnico.setEmail(tecnicoDTO.getEmail());
        tecnico.setWhatsapp(tecnicoDTO.getWhatsapp());
        tecnico.setMedioPreferido(tecnicoDTO.getMedioPreferido());

        // Actualizar especialidades si se proporcionaron IDs
        if (tecnicoDTO.getEspecialidadesIds() != null) {
            List<Especialidad> especialidades = especialidadService.obtenerEspecialidadesPorIds(tecnicoDTO.getEspecialidadesIds());
            tecnico.setEspecialidades(new HashSet<>(especialidades));
        }

        Tecnico tecnicoActualizado = tecnicoRepository.save(tecnico);
        return convertToResponseDTO(tecnicoActualizado);
    }

    @Override
    @Transactional
    public void deleteTecnicoById(Long id) {
        if (!tecnicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tecnico", "id", id);
        }
        tecnicoRepository.deleteById(id);
    }

    @Override
    public List<TecnicoResponseDTO> findTecnicosByEspecialidad(Long idEspecialidad) {
        return tecnicoRepository.findTecnicosByEspecialidadId(idEspecialidad).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public TecnicoResponseDTO asociarEspecialidades(Long idTecnico, List<Long> especialidadesIds) {
        Tecnico tecnico = tecnicoRepository.findById(idTecnico).orElseThrow(() -> new ResourceNotFoundException("Tecnico", "id", idTecnico));

        List<Especialidad> especialidades = especialidadService.obtenerEspecialidadesPorIds(especialidadesIds);

        // Añadir nuevas especialidades a las existentes
        tecnico.getEspecialidades().addAll(especialidades);

        Tecnico tecnicoActualizado = tecnicoRepository.save(tecnico);
        return convertToResponseDTO(tecnicoActualizado);
    }

    @Override
    @Transactional
    public TecnicoResponseDTO desasociarEspecialidades(Long idTecnico, Long idEspecialidad) {
        Tecnico tecnico = tecnicoRepository.findById(idTecnico).orElseThrow(() -> new ResourceNotFoundException("Tecnico", "id", idTecnico));

        // Verificar si la especialidad existe
        if (!tecnico.getEspecialidades().removeIf( especialidad -> especialidad.getIdEspecialidad().equals(idEspecialidad))) {
            throw new ResourceNotFoundException("Especialidad", "idEspecialidad", idEspecialidad);
        }

        // Verificar que el técnico no se quede sin especialidades
        if (tecnico.getEspecialidades().isEmpty()) {
            throw new BadRequestException("El técnico debe tener al menos una especialidad");
        }

        Tecnico tecnicoActualizado = tecnicoRepository.save(tecnico);
        return convertToResponseDTO(tecnicoActualizado);
    }

    @Override
    public List<Object[]> obtenerTecnicosConMasIncidentesResueltos(int dias) {
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(dias);
        return tecnicoRepository.findTecnicosConMasIncidentesResueltos(fechaInicio);
    }

    @Override
    public List<Object[]> obtenerTecnicosConMasIncidentesResueltosporEspecialidad(int dias, Long especialidadId) {
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(dias);
        return tecnicoRepository.findTecnicosConMasIncidentesResueltosporEspecialidad(fechaInicio, especialidadId);
    }

    @Override
    public List<Object[]> obtenerTecnicosConMenorTiempoResolucion() {
        return tecnicoRepository.findTecnicosConMenorTiempoResolucion();
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
