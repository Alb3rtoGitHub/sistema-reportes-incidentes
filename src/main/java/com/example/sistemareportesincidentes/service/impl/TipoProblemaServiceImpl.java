package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.dto.TipoProblemaDTO;
import com.example.sistemareportesincidentes.dto.TipoProblemaResponseDTO;
import com.example.sistemareportesincidentes.entity.Especialidad;
import com.example.sistemareportesincidentes.entity.TipoProblema;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.exception.ResourceAlreadyExistsException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.TipoProblemaRepository;
import com.example.sistemareportesincidentes.service.EspecialidadService;
import com.example.sistemareportesincidentes.service.TipoProblemaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TipoProblemaServiceImpl implements TipoProblemaService {

    @Autowired
    TipoProblemaRepository tipoProblemaRepository;

    @Autowired
    EspecialidadService especialidadService;

    @Override
    public List<TipoProblemaResponseDTO> findAllTipoProblema() {
        return tipoProblemaRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TipoProblemaResponseDTO findTipoProblemaById(Long id) {
        TipoProblema tipoProblema = tipoProblemaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TipoProblema", "id", id));
        return convertToResponseDTO(tipoProblema);
    }

    @Override
    @Transactional
    public TipoProblemaResponseDTO saveTipoProblema(TipoProblemaDTO tipoProblemaDTO) {
        // Verificar si ya existe un tipo de problema con el mismo nombre
        if (tipoProblemaRepository.existsTipoProblemaByNombre(tipoProblemaDTO.getNombre())) {
            throw new ResourceAlreadyExistsException("TipoProblema", "nombre", tipoProblemaDTO.getNombre());
        }

        // Validar tiempos
        if (tipoProblemaDTO.getTiempoMaximoResolucion() < tipoProblemaDTO.getTiempoEstimadoResolucion()){
            throw new BadRequestException("El tiempo máximo de resolución no puede ser menor al tiempo estimado");
        }

        TipoProblema tipoProblema = TipoProblema.builder()
                .nombre(tipoProblemaDTO.getNombre())
                .tiempoEstimadoResolucion(tipoProblemaDTO.getTiempoEstimadoResolucion())
                .tiempoMaximoResolucion(tipoProblemaDTO.getTiempoMaximoResolucion())
                .especialidadesRequeridas(new HashSet<>())
                .build();

        // Asociar especialidades si se proporcionaron IDs
        if (tipoProblemaDTO.getEspecialidadesIds() != null && !tipoProblemaDTO.getEspecialidadesIds().isEmpty()) {
            List<Especialidad> especialidades = especialidadService.obtenerEspecialidadesPorIds(tipoProblemaDTO.getEspecialidadesIds());
            tipoProblema.setEspecialidadesRequeridas(new HashSet<>(especialidades));
        }

        TipoProblema tipoProblemaGuardado = tipoProblemaRepository.save(tipoProblema);
        return convertToResponseDTO(tipoProblemaGuardado);
    }

    @Override
    @Transactional
    public TipoProblemaResponseDTO updateTipoProblema(Long id, TipoProblemaDTO tipoProblemaDTO) {
        TipoProblema tipoProblema = tipoProblemaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TipoProblema", "id", id));

        // Verificar si el nuevo nombre ya existe en otro tipo de problema
        if (!tipoProblema.getNombre().equals(tipoProblemaDTO.getNombre()) &&
                tipoProblemaRepository.existsTipoProblemaByNombre(tipoProblemaDTO.getNombre())) {
            throw new ResourceAlreadyExistsException("Tipo de Problema", "nombre", tipoProblemaDTO.getNombre());
        }

        // Validar tiempos
        if (tipoProblemaDTO.getTiempoMaximoResolucion() < tipoProblemaDTO.getTiempoEstimadoResolucion()) {
            throw new BadRequestException("El tiempo máximo de resolución no puede ser menor al tiempo estimado");
        }

        tipoProblema.setNombre(tipoProblemaDTO.getNombre());
        tipoProblema.setTiempoEstimadoResolucion(tipoProblemaDTO.getTiempoEstimadoResolucion());
        tipoProblema.setTiempoMaximoResolucion(tipoProblemaDTO.getTiempoMaximoResolucion());

        // Actualizar especialidades si se proporcionaron IDs
        if (tipoProblemaDTO.getEspecialidadesIds() != null) {
            List<Especialidad> especialidades = especialidadService.obtenerEspecialidadesPorIds(tipoProblemaDTO.getEspecialidadesIds());
            tipoProblema.setEspecialidadesRequeridas(new HashSet<>(especialidades));
        }

        TipoProblema tipoProblemaActualizado = tipoProblemaRepository.save(tipoProblema);
        return convertToResponseDTO(tipoProblemaActualizado);
    }

    @Override
    @Transactional
    public void deleteTipoProblemaById(Long id) {
        if (!tipoProblemaRepository.existsById(id)) {
            throw new ResourceNotFoundException("TipoProblema", "id", id);
        }

        // Aquí podría ser necesario verificar si el tipo de problema está siendo utilizado
        // en incidentes antes de eliminarlo

        tipoProblemaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TipoProblemaResponseDTO asociarEspecialidades(Long idTipoProblema, List<Long> especialidadesIds) {
        TipoProblema tipoProblema = tipoProblemaRepository.findById(idTipoProblema).orElseThrow(() -> new ResourceNotFoundException("TipoProblema", "id", idTipoProblema));

        List<Especialidad> especialidades = especialidadService.obtenerEspecialidadesPorIds(especialidadesIds);

        // Añadir nuevas especialidades a las existentes
        tipoProblema.getEspecialidadesRequeridas().addAll(especialidades);

        TipoProblema tipoProblemaActualizado = tipoProblemaRepository.save(tipoProblema);
        return convertToResponseDTO(tipoProblemaActualizado);
    }

    @Override
    @Transactional
    public TipoProblemaResponseDTO desasociarEspecialidades(Long idTipoProblema, Long idEspecialidad) {
        TipoProblema tipoProblema = tipoProblemaRepository.findById(idTipoProblema).orElseThrow(() -> new ResourceNotFoundException("TipoProblema", "id", idTipoProblema));

        // Verificar si la especialidad existe
        if (!tipoProblema.getEspecialidadesRequeridas().removeIf(especialidad -> especialidad.getIdEspecialidad().equals(idEspecialidad))) {
            throw new ResourceNotFoundException("Especialidad", "idEspecialidad", idEspecialidad);
        }

        // Verificar que el tipo de problema no se quede sin especialidades
        if (tipoProblema.getEspecialidadesRequeridas().isEmpty()) {
            throw new BadRequestException("El tipo de problema debe tener al menos una especialidad requerida");
        }

        TipoProblema tipoProblemaActualizado = tipoProblemaRepository.save(tipoProblema);
        return convertToResponseDTO(tipoProblemaActualizado);
    }

    @Override
    public List<TipoProblemaResponseDTO> findTiposProblemaByEspecialidadId(Long idEspecialidad) {
        // Este metodo podría requerir una consulta personalizada en el repositorio

        // Consulta personalizada en el repositorio
        return tipoProblemaRepository.findTiposProblemaByEspecialidadId(idEspecialidad).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        // Si no, por ahora, podemos filtrar en memoria
//        return tipoProblemaRepository.findAll().stream()
//                .filter(tp -> tp.getEspecialidadesRequeridas().stream()
//                        .anyMatch(esp -> esp.getIdEspecialidad().equals(idEspecialidad)))
//                .collect(Collectors.toList());
    }

    // Metodo privado auxiliar para convertir entidad a DTO de respuesta
    private TipoProblemaResponseDTO convertToResponseDTO(TipoProblema tipoProblema) {
        TipoProblemaResponseDTO tipoProblemaResponseDTO = new TipoProblemaResponseDTO();

        tipoProblemaResponseDTO.setId(tipoProblema.getIdTipoProblema());
        tipoProblemaResponseDTO.setNombre(tipoProblema.getNombre());
        tipoProblemaResponseDTO.setTiempoEstimadoResolucion(tipoProblema.getTiempoEstimadoResolucion());
        tipoProblemaResponseDTO.setTiempoMaximoResolucion(tipoProblema.getTiempoMaximoResolucion());

        // Convertir Especialidades a DTO
        Set<EspecialidadDTO> especialidadesDTO = tipoProblema.getEspecialidadesRequeridas().stream()
                .map(especialidad -> {
                    EspecialidadDTO especialidadDTO = new EspecialidadDTO();
                    especialidadDTO.setId(especialidad.getIdEspecialidad());
                    especialidadDTO.setNombre(especialidad.getNombre());
                    return especialidadDTO;
                    })
                .collect(Collectors.toSet());

        tipoProblemaResponseDTO.setEspecialidadesRequeridas(especialidadesDTO);

        return tipoProblemaResponseDTO;
    }
}
