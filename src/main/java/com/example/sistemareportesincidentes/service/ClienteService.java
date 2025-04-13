package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.ClienteDTO;
import com.example.sistemareportesincidentes.dto.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    List<ClienteResponseDTO> findAllClientes();

    ClienteResponseDTO findClienteById(Long id);

    ClienteResponseDTO saveCliente(ClienteDTO clienteDTO);

    ClienteResponseDTO updateCliente(Long id, ClienteDTO clienteDTO);

    void deleteClienteById(Long id);

    ClienteResponseDTO asociarServicios(Long idCliente, List<Long> serviciosIds);

    ClienteResponseDTO desasociarServicios(Long idCliente, Long idServicio);
}
