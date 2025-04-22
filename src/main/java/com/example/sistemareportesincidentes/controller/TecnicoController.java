package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.TecnicoDTO;
import com.example.sistemareportesincidentes.dto.TecnicoResponseDTO;
import com.example.sistemareportesincidentes.service.TecnicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rrhh/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    @GetMapping
    public ResponseEntity<List<TecnicoResponseDTO>> listarTecnicos() {
        return ResponseEntity.ok(tecnicoService.findAllTecnicos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> obtenerTecnicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicoService.findTecnicoById(id));
    }

    @PostMapping
    public ResponseEntity<TecnicoResponseDTO> guardarTecnico(@Valid @RequestBody TecnicoDTO tecnicoDTO) {
        TecnicoResponseDTO nuevoTecnico = tecnicoService.saveTecnico(tecnicoDTO);
        return new ResponseEntity<>(nuevoTecnico, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> actualizarTecnico(@PathVariable Long id, @Valid @RequestBody TecnicoDTO tecnicoDTO) {
        return ResponseEntity.ok(tecnicoService.updateTecnico(id, tecnicoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTecnico(@PathVariable Long id) {
        tecnicoService.deleteTecnicoById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    public ResponseEntity<List<TecnicoResponseDTO>> obtenerTecnicosPorEspecialidad(@PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(tecnicoService.findTecnicosByEspecialidad(idEspecialidad));
    }

    @PostMapping("/{id}/especialidades")
    public ResponseEntity<TecnicoResponseDTO> asociarEspecialidades(@PathVariable Long id, @RequestBody List<Long> especialidadesIds) {
        return ResponseEntity.ok(tecnicoService.asociarEspecialidades(id, especialidadesIds));
    }

    @DeleteMapping("/{idTecnico}/especialidades/{idEspecialidad")
    public ResponseEntity<TecnicoResponseDTO> desasociarServicios(@PathVariable Long idTecnico, @PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(tecnicoService.desasociarEspecialidades(idTecnico, idEspecialidad));
    }


}
