package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.IncidenteDTO;
import com.example.sistemareportesincidentes.dto.IncidenteDetalleDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.exception.BadRequestException;
import com.example.sistemareportesincidentes.service.IncidenteDetalleService;
import com.example.sistemareportesincidentes.service.IncidenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mesa-de-ayuda/incidentes")
public class IncidenteController {

    @Autowired
    private IncidenteService incidenteService;

    @Autowired
    private IncidenteDetalleService incidenteDetalleService;

    @PostMapping
    public ResponseEntity<IncidenteDTO> crearIncidente(@Valid @RequestBody IncidenteDTO incidenteDTO) {
        IncidenteDTO nuevoIncidenteDTO = incidenteService.crearIncidente(incidenteDTO);
        return new ResponseEntity<>(nuevoIncidenteDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{idIncidente}/resolver")
    public ResponseEntity<IncidenteDTO> resolverIncidente(@PathVariable Long idIncidente) {
        return ResponseEntity.ok(incidenteService.resolverIncidente(idIncidente));
    }

    @GetMapping("/tecnico/{idTecnico}/fecha/{fecha}")
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorTecnicoyFecha(
            @PathVariable Long idTecnico,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        return ResponseEntity.ok(incidenteService.obtenerIncidentesPorTecnicoYFecha(idTecnico, fecha));
    }

    @GetMapping("/tecnicos-disponibles/{idEspecialidad}")
    public ResponseEntity<List<TecnicoDTO>> obtenerTecnicosDisponibles(@PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(incidenteService.obtenerTecnicosDisponibles(idEspecialidad));
    }

    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorServicio(@PathVariable Long idServicio) {
        return ResponseEntity.ok(incidenteService.obtenerIncidentesPorServicio(idServicio));
    }

    @GetMapping("/tipo-problema/{idTipoProblema}")
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorTipoProblema(@PathVariable Long idTipoProblema) {
        return ResponseEntity.ok(incidenteService.obtenerIncidentesPorTipoProblema(idTipoProblema));
    }

    // Endpoints para gestionar detalles de incidentes
    @GetMapping("/{idIncidente}/incidentes-detalles")
    public ResponseEntity<List<IncidenteDetalleDTO>> obtenerIncidentesDetallesPorIncidenteId(@PathVariable Long idIncidente) {
        return ResponseEntity.ok(incidenteDetalleService.findIncidentesDetalleByIncidenteId(idIncidente));
    }

    @PostMapping("/{idIncidente}/incidentes-detalles")
    public ResponseEntity<IncidenteDetalleDTO> agregarDetalleAIncidente(
            @PathVariable Long idIncidente,
            @Valid @RequestBody IncidenteDetalleDTO incidenteDetalleDTO) {
        incidenteDetalleDTO.setIdIncidente(idIncidente);
        IncidenteDetalleDTO nuevoIncidenteDetalle = incidenteDetalleService.saveIncidenteDetalle(incidenteDetalleDTO);
        return new ResponseEntity<>(nuevoIncidenteDetalle, HttpStatus.CREATED);
    }

    @PutMapping("/{idIncidente}/incidentes-detalles/{idIncidenteDetalle}")
    public ResponseEntity<IncidenteDetalleDTO> actualizarDetalleDeIncidente(
            @PathVariable Long idIncidente,
            @PathVariable Long idIncidenteDetalle,
            @Valid @RequestBody IncidenteDetalleDTO incidenteDetalleDTO) {
        // Verificar que el detalle pertenece al incidente
        IncidenteDetalleDTO incidenteDetalleExistente = incidenteDetalleService.findIncidenteDetalleById(idIncidenteDetalle);
        if (!incidenteDetalleExistente.getIdIncidente().equals(idIncidente)) {
            throw new BadRequestException("El detalle no pertenece al incidente especificado");
        }

        return ResponseEntity.ok(incidenteDetalleService.updateIncidenteDetalle(idIncidenteDetalle, incidenteDetalleDTO));
    }

    @DeleteMapping("/{idIncidente}/incidentes-detalles/{idIncidenteDetalle}")
    public ResponseEntity<Void> eliminarDetalleDeIncidente(
            @PathVariable Long idIncidente,
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