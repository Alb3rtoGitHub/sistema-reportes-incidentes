package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.dto.IncidenteDTO;
import com.example.sistemareportesincidentes.dto.IncidenteDetalleDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.entity.*;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.*;
import com.example.sistemareportesincidentes.service.IncidenteService;
import com.example.sistemareportesincidentes.service.TecnicoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenteServiceImpl implements IncidenteService {

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TipoProblemaRepository tipoProblemaRepository;

    @Autowired
    private TecnicoService tecnicoService;

    @Override
    @Transactional
    public IncidenteDTO crearIncidente(IncidenteDTO incidenteDTO) {
        // Validar Cliente
        Cliente cliente = clienteRepository.findById(incidenteDTO.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", incidenteDTO.getIdCliente()));

        // // Validar técnico si se proporciona
        Tecnico tecnico = null;
        if (incidenteDTO.getIdTecnico() != null) {
            tecnico = tecnicoRepository.findById(incidenteDTO.getIdTecnico())
                    .orElseThrow(() -> new ResourceNotFoundException("Tecnico", "id", incidenteDTO.getIdTecnico()));
        }

        // Validar incidenteDetalle
        if (incidenteDTO.getIncidentesDetalles() == null || incidenteDTO.getIncidentesDetalles().isEmpty()) {
            throw new BadRequestException("El incidente debe tener al menos un detalle");
        }

        // Crear Incidente
        Incidente incidente = Incidente.builder()
                .cliente(cliente)
                .tecnicoAsignado(tecnico)
                .fechaCreacion(LocalDateTime.now())
                .estado(Incidente.Estado.ABIERTO)
                .tiempoEstimadoResolucion(calcularTiempoEstimadoResolucion(incidenteDTO.getIncidentesDetalles()))
                .incidentesDetalles(new ArrayList<>())
                .build();

        // Guardar el incidente para obtener su ID
        Incidente incidenteGuardado = incidenteRepository.save(incidente);

        // Crear incidente detalles
        for (IncidenteDetalleDTO detalleDTO : incidenteDTO.getIncidentesDetalles()) {
            Servicio servicio = servicioRepository.findById(detalleDTO.getIdServicio())
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", detalleDTO.getIdServicio()));

            TipoProblema tipoProblema = tipoProblemaRepository.findById(detalleDTO.getIdTipoProblema())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo problema", "id", detalleDTO.getIdTipoProblema()));

            IncidenteDetalle incidenteDetalle = IncidenteDetalle.builder()
                    .descripcion(detalleDTO.getDescripcion())
                    .servicio(servicio)
                    .tipoProblema(tipoProblema)
                    .build();

            incidenteGuardado.addDetalle(incidenteDetalle); // uso el metodo declarado público en Incidente
        }

        // Guardar el incidente con sus detalles
        incidenteGuardado = incidenteRepository.save(incidenteGuardado);
        return convertToDTO(incidenteGuardado);
    }

    @Override
    @Transactional
    public IncidenteDTO resolverIncidente(Long idIncidente) {
        Incidente incidente = incidenteRepository.findById(idIncidente)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente", "id", idIncidente));

        if (incidente.getEstado() == Incidente.Estado.RESUELTO) {
            throw new BadRequestException("El incidente ya está resuelto");
        }

        incidente.setEstado(Incidente.Estado.RESUELTO);
        incidente.setFechaResolucion(LocalDateTime.now());

        Incidente incidenteResuelto = incidenteRepository.save(incidente);
        return convertToDTO(incidenteResuelto);
    }

    @Override
    public List<IncidenteDTO> obtenerIncidentesPorTecnicoYFecha(Long idTecnico, LocalDateTime fecha) {
        // Validar Técnico
        if (!tecnicoRepository.existsById(idTecnico)) {
            throw new ResourceNotFoundException("Tecnico", "id", idTecnico);
        }

        return incidenteRepository.findIncidentesPorTecnicoYFecha(idTecnico, fecha).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TecnicoDTO> obtenerTecnicosDisponibles(Long idEspecialidad) {
        return tecnicoService.findTecnicosByEspecialidad(idEspecialidad).stream()
                .map(tecnicoResponseDTO -> {
                    TecnicoDTO tecnicoDTO = new TecnicoDTO();
                    tecnicoDTO.setId(tecnicoResponseDTO.getId());
                    tecnicoDTO.setNombre(tecnicoResponseDTO.getNombre());
                    tecnicoDTO.setEmail(tecnicoResponseDTO.getEmail());
                    tecnicoDTO.setWhatsapp(tecnicoResponseDTO.getWhatsapp());
                    tecnicoDTO.setMedioPreferido(tecnicoResponseDTO.getMedioPreferido());
                    tecnicoDTO.setEspecialidadesIds(
                            tecnicoResponseDTO.getEspecialidades().stream()
                                    .map(EspecialidadDTO::getId)
                                    .collect(Collectors.toList())
                    );
                    return tecnicoDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidenteDTO> obtenerIncidentesPorServicio(Long idServicio) {
        // Validar Servicio
        if (!servicioRepository.existsById(idServicio)) {
            throw new ResourceNotFoundException("Servicio", "id", idServicio);
        }

        return incidenteRepository.findIncidentesPorServicioId(idServicio).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidenteDTO> obtenerIncidentesPorTipoProblema(Long idTipoProblema) {
        // Validar Tipo Problema
        if (!tipoProblemaRepository.existsById(idTipoProblema)) {
            throw new ResourceNotFoundException("Tipo problema", "id", idTipoProblema);
        }
        return incidenteRepository.findIncidentesPorTipoProblemaId(idTipoProblema).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Metodo privado auxiliar para calcular tiempo estimado de resolución
    private Integer calcularTiempoEstimadoResolucion(@Valid List<IncidenteDetalleDTO> incidentesDetallesDTO) {
        int tiempoMaximo = 0;
        for (IncidenteDetalleDTO iDetalleDTO : incidentesDetallesDTO) {
            TipoProblema tipoProblema = tipoProblemaRepository.findById(iDetalleDTO.getIdTipoProblema())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo problema", "id", iDetalleDTO.getIdTipoProblema()));

            if (tipoProblema.getTiempoEstimadoResolucion() > tiempoMaximo) {
                tiempoMaximo = tipoProblema.getTiempoEstimadoResolucion();
            }
        }
        return tiempoMaximo;
    }

    // Metodo privado auxiliar para convertir entidad a DTO
    private IncidenteDTO convertToDTO(Incidente incidente) {
        IncidenteDTO incidenteDTO = new IncidenteDTO();
        incidenteDTO.setId(incidente.getIdIncidente());
        incidenteDTO.setIdCliente(incidente.getCliente().getIdCliente());

        if (incidente.getTecnicoAsignado() != null) {
            incidenteDTO.setIdTecnico(incidente.getTecnicoAsignado().getIdTecnico());
        }

        incidenteDTO.setFechaCreacion(incidente.getFechaCreacion());
        incidenteDTO.setFechaResolucion(incidente.getFechaResolucion());
        incidenteDTO.setTiempoEstimadoResolucion(incidente.getTiempoEstimadoResolucion());
        incidenteDTO.setEstado(incidente.getEstado().name());

        // Convertir IncidentesDetalles a DTOs
        List<IncidenteDetalleDTO> incidentesDetalleDTO = incidente.getIncidentesDetalles().stream()
                .map(incidenteDetalle -> {
                    IncidenteDetalleDTO incDetalleDTO = new IncidenteDetalleDTO();
                    incDetalleDTO.setId(incidenteDetalle.getIdIncidenteDetalle());
                    incDetalleDTO.setDescripcion(incidenteDetalle.getDescripcion());
                    incDetalleDTO.setIdServicio(incidenteDetalle.getServicio().getIdServicio());
                    incDetalleDTO.setIdTipoProblema(incidenteDetalle.getTipoProblema().getIdTipoProblema());
                    return incDetalleDTO;
                })
                .collect(Collectors.toList());

        incidenteDTO.setIncidentesDetalles(incidentesDetalleDTO);
        return incidenteDTO;
    }
}
