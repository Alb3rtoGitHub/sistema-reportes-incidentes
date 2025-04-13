package com.example.sistemareportesincidentes.dto;

import com.example.sistemareportesincidentes.validation.ValidCuit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ClienteDTO {

    private Long id;

    @NotBlank(message = "La Razón Social es obligatoria")
    @Size(min = 3, max =255, message = "La razón Social debe tener entre 3 y 255 caracteres")
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio")
    @ValidCuit(message = "El CUIT debe tener un formato válido (XX-XXXXXXXX-X) y ser un número válido")
    private String cuit;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El mail debe tener un formato válido")
    private String email;

    private List<Long> serviciosIds;
}
