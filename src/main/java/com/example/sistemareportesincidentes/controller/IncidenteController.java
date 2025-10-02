package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.IncidenteDTO;
import com.example.sistemareportesincidentes.dto.IncidenteDetalleDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.service.IncidenteDetalleService;
import com.example.sistemareportesincidentes.service.IncidenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Gestión de Incidentes", description = "Endpoints para la gestión completa de incidentes y sus detalles")
@RestController
@RequestMapping("/api/v1/mesa-de-ayuda/incidentes")
@SecurityRequirement(name = "bearerAuth")
public class IncidenteController {

    @Autowired
    private IncidenteService incidenteService;

    @Autowired
    private IncidenteDetalleService incidenteDetalleService;

    @Operation(
            summary = "Listar todos los incidentes",
            description = "Obtiene una lista de todos los incidentes registrados en el sistema. Requiere rol MESA_AYUDA o ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de incidentes obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido o expirado")
    })
    @GetMapping
    public ResponseEntity<List<IncidenteDTO>> listarIncidentes() {
        return ResponseEntity.ok(incidenteService.findAllIncidentes());
    }

    @Operation(
            summary = "Obtener incidente por ID",
            description = "Obtiene los detalles de un incidente específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidente encontrado"),
            @ApiResponse(responseCode = "404", description = "Incidente no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IncidenteDTO> obtenerIncidentePorId(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(incidenteService.findIncidenteById(id));
    }

    @Operation(
            summary = "Crear nuevo incidente",
            description = "Crea un nuevo incidente en el sistema con sus detalles asociados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incidente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<IncidenteDTO> crearIncidente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo incidente",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = IncidenteDTO.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "clienteId": 1,
                            "tecnicoId": 2,
                            "detalles": [
                                {
                                    "descripcion": "Problema con el servidor de correo",
                                    "servicioId": 1,
                                    "tipoProblemaId": 1
                                }
                            ]
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody IncidenteDTO incidenteDTO) {
        IncidenteDTO nuevoIncidenteDTO = incidenteService.crearIncidente(incidenteDTO);
        return new ResponseEntity<>(nuevoIncidenteDTO, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Asignar técnico a incidente",
            description = "Asigna un técnico específico a un incidente existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Técnico asignado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Incidente o técnico no encontrado"),
            @ApiResponse(responseCode = "400", description = "El técnico no tiene las especialidades requeridas")
    })
    @PutMapping("/{id}/asignar-tecnico/{idTecnico}")
    public ResponseEntity<IncidenteDTO> asignarTecnico(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID del técnico", example = "2")
            @PathVariable Long idTecnico) {
        return ResponseEntity.ok(incidenteService.asignarTecnico(id, idTecnico));
    }

    @Operation(
            summary = "Resolver incidente",
            description = "Marca un incidente como resuelto y registra la fecha de resolución"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidente resuelto exitosamente"),
            @ApiResponse(responseCode = "404", description = "Incidente no encontrado"),
            @ApiResponse(responseCode = "400", description = "El incidente ya está resuelto")
    })
    @PutMapping("/{idIncidente}/resolver")
    public ResponseEntity<IncidenteDTO> resolverIncidente(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long idIncidente) {
        return ResponseEntity.ok(incidenteService.resolverIncidente(idIncidente));
    }

    @Operation(
            summary = "Obtener incidentes por técnico y fecha",
            description = "Obtiene todos los incidentes asignados a un técnico específico desde una fecha determinada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de incidentes obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Técnico no encontrado"),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido")
    })
    @GetMapping("/tecnico/{idTecnico}/fecha/{fecha}")
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorTecnicoyFecha(
            @Parameter(description = "ID del técnico", example = "1")
            @PathVariable Long idTecnico,
            @Parameter(description = "Fecha desde la cual buscar incidentes", example = "2023-01-01T00:00:00")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        return ResponseEntity.ok(incidenteService.obtenerIncidentesPorTecnicoYFecha(idTecnico, fecha));
    }

    @Operation(
            summary = "Obtener técnicos disponibles por especialidad",
            description = "Obtiene una lista de técnicos que tienen una especialidad específica y están disponibles para asignación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de técnicos disponibles obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Especialidad no encontrada")
    })
    @GetMapping("/tecnicos-disponibles/{idEspecialidad}")
    public ResponseEntity<List<TecnicoDTO>> obtenerTecnicosDisponibles(
            @Parameter(description = "ID de la especialidad", example = "1")
            @PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(incidenteService.obtenerTecnicosDisponibles(idEspecialidad));
    }

    @Operation(
            summary = "Obtener incidentes por servicio",
            description = "Obtiene todos los incidentes relacionados con un servicio específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de incidentes obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorServicio(
            @Parameter(description = "ID del servicio", example = "1")
            @PathVariable Long idServicio) {
        return ResponseEntity.ok(incidenteService.obtenerIncidentesPorServicio(idServicio));
    }

    @Operation(
            summary = "Obtener incidentes por tipo de problema",
            description = "Obtiene todos los incidentes que tienen un tipo de problema específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de incidentes obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de problema no encontrado")
    })
    @GetMapping("/tipo-problema/{idTipoProblema}")
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorTipoProblema(
            @Parameter(description = "ID del tipo de problema", example = "1")
            @PathVariable Long idTipoProblema) {
        return ResponseEntity.ok(incidenteService.obtenerIncidentesPorTipoProblema(idTipoProblema));
    }

    @Operation(
            summary = "Obtener detalles de un incidente",
            description = "Obtiene todos los detalles asociados a un incidente específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Incidente no encontrado")
    })
    // Endpoints para gestionar detalles de incidentes
    @GetMapping("/{idIncidente}/incidentes-detalles")
    public ResponseEntity<List<IncidenteDetalleDTO>> obtenerIncidentesDetallesPorIncidenteId(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long idIncidente) {
        return ResponseEntity.ok(incidenteDetalleService.findIncidentesDetalleByIncidenteId(idIncidente));
    }

    @Operation(
            summary = "Agregar detalle a incidente",
            description = "Agrega un nuevo detalle a un incidente existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle agregado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Incidente no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos del detalle inválidos o incidente ya resuelto")
    })
    @PostMapping("/{idIncidente}/incidentes-detalles")
    public ResponseEntity<IncidenteDetalleDTO> agregarDetalleAIncidente(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long idIncidente,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo detalle",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = IncidenteDetalleDTO.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "descripcion": "Error en la configuración del firewall",
                            "servicioId": 2,
                            "tipoProblemaId": 3
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody IncidenteDetalleDTO incidenteDetalleDTO) {
        incidenteDetalleDTO.setIdIncidente(idIncidente);
        IncidenteDetalleDTO nuevoIncidenteDetalle = incidenteDetalleService.saveIncidenteDetalle(incidenteDetalleDTO);
        return new ResponseEntity<>(nuevoIncidenteDetalle, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar detalle de incidente",
            description = "Actualiza un detalle específico de un incidente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Incidente o detalle no encontrado"),
            @ApiResponse(responseCode = "400", description = "El detalle no pertenece al incidente o incidente ya resuelto")
    })
    @PutMapping("/{idIncidente}/incidentes-detalles/{idIncidenteDetalle}")
    public ResponseEntity<IncidenteDetalleDTO> actualizarDetalleDeIncidente(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long idIncidente,
            @Parameter(description = "ID del incidente-detalle", example = "1")
            @PathVariable Long idIncidenteDetalle,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del detalle",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = IncidenteDetalleDTO.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                            "descripcion": "Error en la configuración del firewall - Actualizado",
                            "servicioId": 2,
                            "tipoProblemaId": 3
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody IncidenteDetalleDTO incidenteDetalleDTO) {
        // Verificar que el detalle pertenece al incidente
        IncidenteDetalleDTO incidenteDetalleExistente = incidenteDetalleService.findIncidenteDetalleById(idIncidenteDetalle);
        if (!incidenteDetalleExistente.getIdIncidente().equals(idIncidente)) {
            throw new BadRequestException("El detalle no pertenece al incidente especificado");
        }

        return ResponseEntity.ok(incidenteDetalleService.updateIncidenteDetalle(idIncidenteDetalle, incidenteDetalleDTO));
    }

    @Operation(
            summary = "Eliminar detalle de incidente",
            description = "Elimina un detalle específico de un incidente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Incidente o detalle no encontrado"),
            @ApiResponse(responseCode = "400", description = "El detalle no pertenece al incidente, es el único detalle, o el incidente ya está resuelto")
    })
    @DeleteMapping("/{idIncidente}/incidentes-detalles/{idIncidenteDetalle}")
    public ResponseEntity<Void> eliminarDetalleDeIncidente(
            @Parameter(description = "ID del incidente", example = "1")
            @PathVariable Long idIncidente,
            @Parameter(description = "ID del incidente-detalle", example = "1")
            @PathVariable Long idIncidenteDetalle) {
        // Verificar que el detalle pertenece al incidente
        IncidenteDetalleDTO incidenteDetalleExistente = incidenteDetalleService.findIncidenteDetalleById(idIncidenteDetalle);
        if (!incidenteDetalleExistente.getIdIncidente().equals(idIncidente)) {
            throw new BadRequestException("El detalle no pertenece al incidente especificado");
        }
        incidenteDetalleService.deleteIncidenteDetalle(idIncidenteDetalle);
        return ResponseEntity.noContent().build();
    }
}