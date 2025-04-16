package com.example.sistemareportesincidentes.dto;

import com.example.sistemareportesincidentes.entity.Tecnico;
import lombok.Data;

import java.util.Set;

@Data
public class TecnicoResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String whatsapp;
    private Tecnico.MedioNotificacion medioPreferido;
    private Set<EspecialidadDTO> especialidades;
}
