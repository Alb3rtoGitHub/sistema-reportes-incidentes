package com.example.sistemareportesincidentes.dto;

import com.example.sistemareportesincidentes.entity.Tecnico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TecnicoDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    private String whatsapp;

    @NotNull(message = "El medio preferido de notificación es obligatorio")
    private Tecnico.MedioNotificacion medioPreferido;

    private List<Long> especialidadesIds;
}
