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
import com.example.sistemareportesincidentes.service.NotificacionService;
import com.example.sistemareportesincidentes.service.TecnicoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private NotificacionService notificacionService;

    @Override
    public List<IncidenteDTO> findAllIncidentes() {
        return incidenteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IncidenteDTO findIncidenteById(Long id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente", "id", id));
        return convertToDTO(incidente);
    }

    @Override
    @Transactional
    public IncidenteDTO crearIncidente(IncidenteDTO incidenteDTO) {
        // Validar Cliente
        Cliente cliente = clienteRepository.findById(incidenteDTO.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", incidenteDTO.getIdCliente()));

        // Validar técnico si se proporciona
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

        // Si hay técnico asignado, aplicar el "colchón" si es necesario
        if (tecnico != null) {
            // Verificar si el incidente es complejo
            boolean esComplejo = incidenteDTO.getIncidentesDetalles().size() > 1;

            if (esComplejo) {
                // Añadir un 20% adicional como "colchón"
                int tiempoBase = incidenteGuardado.getTiempoEstimadoResolucion();
                incidenteGuardado.setTiempoEstimadoResolucion((int) (tiempoBase * 1.2));
            }
        }

        // Guardar el incidente con sus detalles
        incidenteGuardado = incidenteRepository.save(incidenteGuardado);
        return convertToDTO(incidenteGuardado);
    }

    @Override
    @Transactional
    public IncidenteDTO asignarTecnico(Long idIncidente, Long idTecnico) {
        // Buscar el incidente
        Incidente incidente = incidenteRepository.findById(idIncidente)
                .orElseThrow(() -> new ResourceNotFoundException("Incidente", "id", idIncidente));

        // Verificar que el incidente no esté resuelto
        if (incidente.getEstado() == Incidente.Estado.RESUELTO) {
            throw new BadRequestException("No se puede asignar un técnico a un incidente resuelto");
        }

        // Buscar el técnico
        Tecnico tecnico = tecnicoRepository.findById(idTecnico)
                .orElseThrow(() -> new ResourceNotFoundException("Tecnico", "id", idTecnico));

        // Verificar que el técnico tenga las especialidades requeridas
        Set<Long> especialidadesRequeridasIds = new HashSet<>();
        for (IncidenteDetalle incidenteDetalle : incidente.getIncidentesDetalles()) { // por cada elemento (incidenteDetalle) de la lista de IncidenteDetalles...
            TipoProblema tipoProblema = incidenteDetalle.getTipoProblema();  // veo el tipo de problema...
            tipoProblema.getEspecialidadesRequeridas().forEach(especialidadeRequerida -> // el tipo de problema tiene un set de Especialidad que son requeridas...por cada elemento de esa lista...
                    especialidadesRequeridasIds.add(especialidadeRequerida.getIdEspecialidad())); // tomo el idEspecialidad y lo agrego la lista de especialidadesRequeridasIds...
        }

        Set<Long> especialidadesTecnico = tecnico.getEspecialidades().stream()
                .map(Especialidad::getIdEspecialidad)
                .collect(Collectors.toSet());

        // Crear una copia para realizar la intersección de los dos Set y encontrar los que son comunes
        Set<Long> especialidadesComunes = new HashSet<>(especialidadesTecnico);
        // Realizar la intersección con las especialidades requeridas
        especialidadesComunes.retainAll(especialidadesRequeridasIds); //retainAll destruye el listado original y arma uno nuevo con los elementos comunes de los dos listados

        // Verificar si hay al menos una especialidad en común
        if (especialidadesComunes.isEmpty()) {
            throw new BadRequestException("El técnico no tiene ninguna de las especialidades requeridas para este incidente");
        }

        // Asignar el técnico al incidente
        incidente.setTecnicoAsignado(tecnico);

        // Recalcular el tiempo estimado de resolución considerando un "colchón" para problemas complejos
        int tiempoBase = calcularTiempoBase(incidente.getIncidentesDetalles());

        // Aplicar "colchón" si el problema es complejo (más de un detalle o tipos de problema complejos)
        boolean esComplejo = incidente.getIncidentesDetalles().size() > 1 || incidente.getIncidentesDetalles().stream()
                .anyMatch(d -> d.getTipoProblema().getTiempoMaximoResolucion() > 120);

        if (esComplejo) {
            // Añadir un 20% adicional como "colchón"
            tiempoBase = (int) (tiempoBase * 1.2);
        }

        incidente.setTiempoEstimadoResolucion(tiempoBase);

        // Guardar y devolver el incidente actualizado
        Incidente incidenteActualizado = incidenteRepository.save(incidente);

        // Notificar al técnico sobre la asignación
        notificacionService.notificarTecnicoAsignacion(tecnico, incidenteActualizado);

        return convertToDTO(incidenteActualizado);
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

        // Notificar al cliente sobre la resolución
        notificacionService.notificarClienteResolucion(incidenteResuelto.getCliente(), incidenteResuelto);

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

    // Metodo privado auxiliar para calcular tiempo estimado de resolución (DTO)
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

    // Metodo privado auxiliar para calcular tiempo base (Entidades)
    private Integer calcularTiempoBase(@Valid List<IncidenteDetalle> incidentesDetalles) {
        int tiempoMaximo = 0;

        for (IncidenteDetalle iDetalle : incidentesDetalles) {
            TipoProblema tipoProblema = iDetalle.getTipoProblema();
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
                    incDetalleDTO.setIdIncidente(incidenteDetalle.getIncidente().getIdIncidente());
                    return incDetalleDTO;
                })
                .collect(Collectors.toList());

        incidenteDTO.setIncidentesDetalles(incidentesDetalleDTO);
        return incidenteDTO;
    }
}
