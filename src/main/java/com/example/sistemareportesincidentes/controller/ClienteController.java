package com.example.sistemareportesincidentes.controller;

import com.example.sistemareportesincidentes.dto.ClienteDTO;
import com.example.sistemareportesincidentes.dto.ClienteResponseDTO;
import com.example.sistemareportesincidentes.dto.ServicioDTO;
import com.example.sistemareportesincidentes.entity.Cliente;
import com.example.sistemareportesincidentes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comercial/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.findAllClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findClienteById(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> guardarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO nuevoCliente = clienteService.saveCliente(clienteDTO);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(clienteService.updateCliente(id, clienteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.deleteClienteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/servicios")
    public ResponseEntity<ClienteResponseDTO> asociarServicios(@PathVariable Long id, @RequestBody List<Long> serviciosIds) {
        return ResponseEntity.ok(clienteService.asociarServicios(id, serviciosIds));
    }

    @DeleteMapping("/{idCliente}/servicios/{idServicio}")
    public ResponseEntity<ClienteResponseDTO> desasociarServicios(@PathVariable Long idCliente, @PathVariable Long idServicio) {
        return ResponseEntity.ok(clienteService.desasociarServicios(idCliente, idServicio));
    }
}
