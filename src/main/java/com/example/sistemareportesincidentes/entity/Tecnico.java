package com.example.sistemareportesincidentes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tecnico")
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTecnico;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 20)
    private String whatsapp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MedioNotificacion medioPreferido;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tecnico_especialidad",
            joinColumns = @JoinColumn(name = "idTecnico"),
            inverseJoinColumns = @JoinColumn(name = "idEspecialidad")
    )
    private Set<Especialidad> especialidades = new HashSet<>();

    public enum MedioNotificacion {
        EMAIL,
        WHATSAPP
    }
}
