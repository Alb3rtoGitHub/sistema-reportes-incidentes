package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.EspecialidadDTO;
import com.example.sistemareportesincidentes.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rrhh/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> listarEspecialidades() {
        return ResponseEntity.ok(especialidadService.findAllEspecialidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> obtenerEspecialidadPorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.findEspecialidadById(id));
    }

    @PostMapping
    public ResponseEntity<EspecialidadDTO> guardarEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO) {
        EspecialidadDTO nuevaEspecialidad = especialidadService.saveEspecialidad(especialidadDTO);
        return new ResponseEntity<>(nuevaEspecialidad, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> actualizarEspecialidad(@PathVariable Long id, @Valid @RequestBody EspecialidadDTO especialidadDTO) {
        return ResponseEntity.ok(especialidadService.updateEspecialidad(id, especialidadDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable Long id) {
        especialidadService.deleteEspecialidadById(id);
        return ResponseEntity.noContent().build();
    }
}
