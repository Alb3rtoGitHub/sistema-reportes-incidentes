package com.example.sistemareportesincidentes.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ClienteResponseDTO {
    private Long id;
    private String razonSocial;
    private String cuit;
    private String email;
    private Set<ServicioDTO> serviciosContratados;
}
