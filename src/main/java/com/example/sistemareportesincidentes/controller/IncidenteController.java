package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.IncidenteDTO;
import com.example.sistemareportesincidentes.dto.TecnicoDTO;
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
@RequestMapping("/api/v1//mesa-de-ayuda/incidentes")
public class IncidenteController {

    @Autowired
    private IncidenteService incidenteService;

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
    public ResponseEntity<List<IncidenteDTO>> obtenerIncidentesPorTecnicoyFecha(@PathVariable Long idTecnico, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
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
}
