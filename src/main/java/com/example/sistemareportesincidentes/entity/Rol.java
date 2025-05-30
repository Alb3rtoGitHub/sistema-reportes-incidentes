package com.example.sistemareportesincidentes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private nombreRol nombre;

    public enum nombreRol {
        ROLE_ADMIN,     // Gestion completa
        ROLE_RRHH,      // Gestiona técnicos
        ROLE_COMERCIAL, // Gestiona clientes/servicios
        ROLE_MESA_AYUDA // Gestiona incidentes
    }
}
