package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.TipoProblemaDTO;
import com.example.sistemareportesincidentes.dto.TipoProblemaResponseDTO;
import com.example.sistemareportesincidentes.service.TipoProblemaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesa-de-ayuda/tipos-problema")
public class TipoProblemaController {

    @Autowired
    private TipoProblemaService tipoProblemaService;

    @GetMapping
    public ResponseEntity<List<TipoProblemaResponseDTO>> listarTipoProblema() {
        return ResponseEntity.ok(tipoProblemaService.findAllTipoProblema());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProblemaResponseDTO> obtenerTipoProblemaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoProblemaService.findTipoProblemaById(id));
    }

    @PostMapping
    public ResponseEntity<TipoProblemaResponseDTO> guardarTipoProblema(@Valid @RequestBody TipoProblemaDTO tipoProblemaDTO) {
        TipoProblemaResponseDTO nuevoTipoProblema = tipoProblemaService.saveTipoProblema(tipoProblemaDTO);
        return new ResponseEntity<>(nuevoTipoProblema, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoProblemaResponseDTO> actualizarTipoProblema(@PathVariable Long id, @Valid @RequestBody TipoProblemaDTO tipoProblemaDTO) {
        return ResponseEntity.ok(tipoProblemaService.updateTipoProblema(id, tipoProblemaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoProblema(@PathVariable Long id) {
        tipoProblemaService.deleteTipoProblemaById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/especialidades")
        public ResponseEntity<TipoProblemaResponseDTO> asociarEspecialidades(@PathVariable Long id, @RequestBody List<Long> especialidadesIds) {
        return ResponseEntity.ok(tipoProblemaService.asociarEspecialidades(id, especialidadesIds));
    }

    @DeleteMapping("/{idTipoProblema}/especialidades/{idEspecialidad}")
    public ResponseEntity<TipoProblemaResponseDTO> desasociarEspecialidades(@PathVariable Long idTipoProblema, @PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(tipoProblemaService.desasociarEspecialidades(idTipoProblema, idEspecialidad));
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    public ResponseEntity<List<TipoProblemaResponseDTO>> obtenerTiposProblemaPorEspecialidadId(@PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(tipoProblemaService.findTiposProblemaByEspecialidadId(idEspecialidad));
    }














}
