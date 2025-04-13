package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.ClienteDTO;
import com.example.sistemareportesincidentes.dto.ClienteResponseDTO;
import com.example.sistemareportesincidentes.dto.ServicioDTO;
import com.example.sistemareportesincidentes.entity.Cliente;
import com.example.sistemareportesincidentes.entity.Servicio;
import com.example.sistemareportesincidentes.exception.ResourceAlreadyExistsdException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.ClienteRepository;
import com.example.sistemareportesincidentes.service.ClienteService;
import com.example.sistemareportesincidentes.service.ServicioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

//    private final ClienteRepository clienteRepository;
//    private final ServicioService servicioService;
//
//    public ClienteServiceImpl(ClienteRepository clienteRepository, ServicioService servicioService) {
//        this.clienteRepository = clienteRepository;
//        this.servicioService = servicioService;
//    }

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicioService servicioService;

    @Override
    public List<ClienteResponseDTO> findAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO findClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cliente", "id", id));
        return convertToResponseDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO saveCliente(ClienteDTO clienteDTO) {
        // Verificar si ya existe un cliente con el mismo CUIT
        Optional<Cliente> clienteExistente = clienteRepository.findClienteByCuit(clienteDTO.getCuit());
        if (clienteExistente.isPresent()) {
            throw new ResourceAlreadyExistsdException("Cliente", "CUIT", clienteDTO.getCuit());
        }

        Cliente cliente = Cliente.builder()
                .razonSocial(clienteDTO.getRazonSocial())
                .cuit(clienteDTO.getCuit())
                .email(clienteDTO.getEmail())
                .serviciosContratados(new HashSet<>())
                .build();

        // Asociar servicios si se proporcionaron IDs
        if (clienteDTO.getServiciosIds() != null && !clienteDTO.getServiciosIds().isEmpty()) {
            List<Servicio> servicios = servicioService.obtenerServiciosPorIds(clienteDTO.getServiciosIds());
            cliente.setServiciosContratados(new HashSet<>(servicios));
        }

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertToResponseDTO(clienteGuardado);
    }

    @Override
    @Transactional
    public ClienteResponseDTO updateCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cliente", "id", id));

        // Verificar si el nuevo CUIT ya existe en otro cliente
        Optional<Cliente> clienteConMismoCuit = clienteRepository.findClienteByCuit(clienteDTO.getCuit());
        if (clienteConMismoCuit.isPresent() && !clienteConMismoCuit.get().getIdCliente().equals(id)) {
            throw new ResourceAlreadyExistsdException("Cliente", "CUIT", clienteDTO.getCuit());
        }

        cliente.setRazonSocial(clienteDTO.getRazonSocial());
        cliente.setCuit(clienteDTO.getCuit());
        cliente.setEmail(clienteDTO.getEmail());

        // Actualizar servicios si se proporcionaron IDs
        if (clienteDTO.getServiciosIds() != null) {
            List<Servicio> servicios = servicioService.obtenerServiciosPorIds(clienteDTO.getServiciosIds());
            cliente.setServiciosContratados(new HashSet<>(servicios));
        }

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToResponseDTO(clienteActualizado);
    }

    @Override
    @Transactional
    public void deleteClienteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", "id", id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    public ClienteResponseDTO asociarServicios(Long idCliente, List<Long> serviciosIds) {
        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow(()-> new ResourceNotFoundException("Cliente", "id", idCliente));

        List<Servicio> servicios = servicioService.obtenerServiciosPorIds(serviciosIds);

        // Añadir nuevos servicios a los existentes
        cliente.getServiciosContratados().addAll(servicios);

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToResponseDTO(clienteActualizado);
    }

    @Override
    public ClienteResponseDTO desasociarServicios(Long idCliente, Long idServicio) {
        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow(()-> new ResourceNotFoundException("Cliente", "id", idCliente));

        // Verificar si el servicio existe
        if (!cliente.getServiciosContratados().removeIf(servicio -> servicio.getIdServicio().equals(idServicio))) {
            throw new ResourceNotFoundException("Servicio", "idServicio", idServicio);
        }

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToResponseDTO(clienteActualizado);
    }

    // Metodo privado auxiliar para convertir entidad a DTO de respuesta
    private ClienteResponseDTO convertToResponseDTO(Cliente cliente) {
        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO();
        clienteResponseDTO.setId(cliente.getIdCliente());
        clienteResponseDTO.setRazonSocial(cliente.getRazonSocial());
        clienteResponseDTO.setCuit(cliente.getCuit());
        clienteResponseDTO.setEmail(cliente.getEmail());

        // Convertir servicios a DTOs para agregarlo al atributo serviciosContratados del ClienteResponseDTO
        Set<ServicioDTO> serviciosDTO = cliente.getServiciosContratados().stream()
                .map(servicio -> {
                    ServicioDTO servicioDTO = new ServicioDTO();
                    servicioDTO.setId(servicio.getIdServicio());
                    servicioDTO.setNombre(servicio.getNombre());
                    return servicioDTO;
                })
                .collect(Collectors.toSet());
        clienteResponseDTO.setServiciosContratados(serviciosDTO);

        return clienteResponseDTO;
    }
}
