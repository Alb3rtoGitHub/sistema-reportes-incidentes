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
@Table(name = "tipo_problema")
public class TipoProblema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoProblema;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false)
    private Integer tiempoEstimadoResolucion;

    @Column(nullable = false)
    private Integer tiempoMaximoResolucion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tipo_problema_especialidad",
            joinColumns = @JoinColumn(name = "idTipoProblema"),
            inverseJoinColumns = @JoinColumn(name = "idEspecialidad")
    )
    private Set<Especialidad> especialidadesRequeridas = new HashSet<>();
}
