package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.ServicioDTO;
import com.example.sistemareportesincidentes.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comercial/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioDTO>> listarServicios() {
        return ResponseEntity.ok(servicioService.findAllServicios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioDTO> obtenerServicioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.findServicioById(id));
    }

    @PostMapping
    public ResponseEntity<ServicioDTO> guardarServicio(@Valid @RequestBody ServicioDTO servicioDTO) {
        ServicioDTO nuevoServicio = servicioService.saveServicio(servicioDTO);
        return new ResponseEntity<>(nuevoServicio, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioDTO> actualizarServicio(@PathVariable Long id, @Valid @RequestBody ServicioDTO servicioDTO) {
        return ResponseEntity.ok(servicioService.updateServicio(id, servicioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServicioDTO> eliminarServicio(@PathVariable Long id) {
        servicioService.deleteServicioById(id);
        return ResponseEntity.noContent().build();
    }
    }
