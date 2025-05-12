package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.IncidenteDetalleDTO;
import com.example.sistemareportesincidentes.service.IncidenteDetalleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesa-de-ayuda/incidentes/incidentes-detalles")
public class IncidenteDetalleController {
    @Autowired
    private IncidenteDetalleService incidenteDetalleService;

    @GetMapping("/{idIncidenteDetalle}")
    public ResponseEntity<IncidenteDetalleDTO> obtenerIncidenteDetallePorId(@PathVariable Long idIncidenteDetalle) {
        return ResponseEntity.ok(incidenteDetalleService.findIncidenteDetalleById(idIncidenteDetalle));
    }

    @GetMapping("/incidente/{idIncidente}")
    public ResponseEntity<List<IncidenteDetalleDTO>> obtenerIncidentesDetallePorIdIncidente(@PathVariable Long idIncidente) {
        return ResponseEntity.ok(incidenteDetalleService.findIncidentesDetalleByIncidenteId(idIncidente));
    }

    @PostMapping
    public ResponseEntity<IncidenteDetalleDTO> guardarIncidenteDetalle (@Valid @RequestBody IncidenteDetalleDTO incidenteDetalleDTO) {
        IncidenteDetalleDTO nuevoIncidenteDetalle = incidenteDetalleService.saveIncidenteDetalle(incidenteDetalleDTO);
        return new ResponseEntity<>(nuevoIncidenteDetalle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidenteDetalleDTO> actualizarIncidenteDetalle (@PathVariable Long id, @Valid @RequestBody IncidenteDetalleDTO incidenteDetalleDTO) {
        return ResponseEntity.ok(incidenteDetalleService.updateIncidenteDetalle(id, incidenteDetalleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncidenteDetalle (@PathVariable Long id) {
        incidenteDetalleService.deleteIncidenteDetalle(id);
        return ResponseEntity.noContent().build();
    }
}
