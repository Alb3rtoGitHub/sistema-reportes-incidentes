package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.IncidenteDetalleDTO;
import com.example.sistemareportesincidentes.entity.Incidente;
import com.example.sistemareportesincidentes.entity.IncidenteDetalle;
import com.example.sistemareportesincidentes.entity.Servicio;
import com.example.sistemareportesincidentes.entity.TipoProblema;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.IncidenteDetalleRepository;
import com.example.sistemareportesincidentes.repository.IncidenteRepository;
import com.example.sistemareportesincidentes.repository.ServicioRepository;
import com.example.sistemareportesincidentes.repository.TipoProblemaRepository;
import com.example.sistemareportesincidentes.service.IncidenteDetalleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenteDetalleServiceImpl implements IncidenteDetalleService {

    @Autowired
    private IncidenteDetalleRepository incidenteDetalleRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TipoProblemaRepository tipoProblemaRepository;

    @Override
    public IncidenteDetalleDTO findIncidenteDetalleById(Long idIncidenteDetalle) {
        IncidenteDetalle incidenteDetalle = incidenteDetalleRepository.findById(idIncidenteDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de Incidente", "id", idIncidenteDetalle));
        return convertToIncidenteDetalleDTO(incidenteDetalle);
    }

    @Override
    public List<IncidenteDetalleDTO> findIncidentesDetalleByIncidenteId(Long idIncidente) {
        // Veridifcar que el incidente existe
        if (!incidenteRepository.existsById(idIncidente)) {
            throw new ResourceNotFoundException("Incidente", "id", idIncidente);
        }
        return incidenteDetalleRepository.findByIncidenteIdIncidente(idIncidente).stream()
                .map(this::convertToIncidenteDetalleDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IncidenteDetalleDTO saveIncidenteDetalle(IncidenteDetalleDTO incidenteDetalleDTO) {
        // Validar que el incidente existe
        if (incidenteDetalleDTO.getIdIncidente() == null) {
            throw new BadRequestException("El ID del Incidente es obligatorio");
        }

        // Validar que el incidente no esté resuelto
        Incidente incidente = incidenteRepository.findById(incidenteDetalleDTO.getIdIncidente())
                .orElseThrow(() -> new ResourceNotFoundException("Incidente", "id", incidenteDetalleDTO.getIdIncidente()));

        if (incidente.getEstado() == Incidente.Estado.RESUELTO) {
            throw new BadRequestException("No se pueden añadir detalles a un incidente resuelto");
        }

        // Validar servicio y tipo de problema
        Servicio servicio = servicioRepository.findById(incidenteDetalleDTO.getIdServicio())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", incidenteDetalleDTO.getIdServicio()));

        TipoProblema tipoProblema = tipoProblemaRepository.findById(incidenteDetalleDTO.getIdTipoProblema())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo Problema", "id", incidenteDetalleDTO.getIdTipoProblema()));

        // Crear y guardar el detalle
        IncidenteDetalle incidenteDetalle = IncidenteDetalle.builder()
                .descripcion(incidenteDetalleDTO.getDescripcion())
                .servicio(servicio)
                .tipoProblema(tipoProblema)
                .incidente(incidente)
                .build();

        // Actualizar el tiempo estimado de resolución del incidente si es necesario
        if (tipoProblema.getTiempoEstimadoResolucion() > incidente.getTiempoEstimadoResolucion()) {
            incidente.setTiempoEstimadoResolucion(tipoProblema.getTiempoEstimadoResolucion());
            incidenteRepository.save(incidente);
        }
        IncidenteDetalle incidenteDetalleGuardado = incidenteDetalleRepository.save(incidenteDetalle);
        return convertToIncidenteDetalleDTO(incidenteDetalle);
    }

    @Override
    @Transactional
    public IncidenteDetalleDTO updateIncidenteDetalle(Long idIncidenteDetalle, IncidenteDetalleDTO incidenteDetalleDTO) {
        // Validar que existe el detalle
        IncidenteDetalle incidenteDetalle = incidenteDetalleRepository.findById(idIncidenteDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de Incidente", "id", idIncidenteDetalle));

        // Validar que nos se cambie el incidente
        if (incidenteDetalleDTO.getIdIncidente() != null && !incidenteDetalle.getIncidente().getIdIncidente().equals(incidenteDetalleDTO.getIdIncidente())) {
            throw new BadRequestException("No se puede cambiar el incidente asociado a un detalle");
        }

        // Validar que el incidente no esté resuelto
        if (incidenteDetalle.getIncidente().getEstado() == Incidente.Estado.RESUELTO) {
            throw new BadRequestException("No se pueden modificar detalles de un incidente resuelto");
        }

        // Actualizar servicio si se proporciona
        if (incidenteDetalleDTO.getIdServicio() != null) {
            Servicio servicio = servicioRepository.findById(incidenteDetalleDTO.getIdServicio())
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", incidenteDetalleDTO.getIdServicio()));
            incidenteDetalle.setServicio(servicio);
        }

        // Actualizar tipo de problema si se proporciona
        if (incidenteDetalleDTO.getIdTipoProblema() != null) {
            TipoProblema tipoProblema = tipoProblemaRepository.findById(incidenteDetalleDTO.getIdTipoProblema())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo Problema", "id", incidenteDetalleDTO.getIdTipoProblema()));
            incidenteDetalle.setTipoProblema(tipoProblema);

            // Actualizar el tiempo estimado de resolución del incidente si es necesario
            Incidente incidente = incidenteDetalle.getIncidente();
            if (tipoProblema.getTiempoEstimadoResolucion() > incidente.getTiempoEstimadoResolucion()) {
                incidente.setTiempoEstimadoResolucion(tipoProblema.getTiempoEstimadoResolucion());
                incidenteRepository.save(incidente);
            }
        }

        // Actualizar la descripción si se proporciona
        if (incidenteDetalleDTO.getDescripcion() != null) {
            incidenteDetalle.setDescripcion(incidenteDetalleDTO.getDescripcion());
        }

        IncidenteDetalle incidenteDetalleActualizado = incidenteDetalleRepository.save(incidenteDetalle);
        return convertToIncidenteDetalleDTO(incidenteDetalleActualizado);
    }

    @Override
    @Transactional
    public void deleteIncidenteDetalle(Long idIncidenteDetalle) {
        IncidenteDetalle incidenteDetalle = incidenteDetalleRepository.findById(idIncidenteDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de Incidente", "id", idIncidenteDetalle));

        // Validar que el incidente no esté resuelto
        if (incidenteDetalle.getIncidente().getEstado() == Incidente.Estado.RESUELTO) {
            throw new BadRequestException("No se pueden eliminar detalles de un incidente resuelto");
        }

        // Validar que no sea el único detalle del incidente
        Incidente incidente = incidenteDetalle.getIncidente();
        if (incidente.getIncidentesDetalles().size() <= 1) {
            throw new BadRequestException("No se puede eliminar el único detalle de un incidente");
        }

        //Eliminar el detalle
        incidente.removeDetalle(incidenteDetalle);
        incidenteRepository.save(incidente);

        // Recalcular el tiempo estimado de resolución del incidente
        recalcularTiempoEstimadoResolucion(incidente);
    }

    // Metodo privado para recalcular el tiempo estimado de resolución
    private void recalcularTiempoEstimadoResolucion(Incidente incidente){
        int tiempoMaximo = 0;
        for (IncidenteDetalle incidenteDetalle : incidente.getIncidentesDetalles()) {
            int tiempoEstimado = incidenteDetalle.getTipoProblema().getTiempoEstimadoResolucion();
            if (tiempoEstimado > tiempoMaximo) {
                tiempoMaximo = tiempoEstimado;
            }
        }

        incidente.setTiempoEstimadoResolucion(tiempoMaximo);
        incidenteRepository.save(incidente);
    }

    // Metodo privado para convertir entidad a DTO
    private IncidenteDetalleDTO convertToIncidenteDetalleDTO(IncidenteDetalle incidenteDetalle) {
        IncidenteDetalleDTO incidenteDetalleDTO = new IncidenteDetalleDTO();
        incidenteDetalleDTO.setId(incidenteDetalle.getIdIncidenteDetalle());
        incidenteDetalleDTO.setDescripcion(incidenteDetalle.getDescripcion());
        incidenteDetalleDTO.setIdServicio(incidenteDetalle.getServicio().getIdServicio());
        incidenteDetalleDTO.setIdTipoProblema(incidenteDetalle.getTipoProblema().getIdTipoProblema());
        incidenteDetalleDTO.setIdIncidente(incidenteDetalle.getIncidente().getIdIncidente());
        return incidenteDetalleDTO;
    }
}
