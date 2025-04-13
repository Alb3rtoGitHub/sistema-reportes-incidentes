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
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @Column(nullable = false, length = 255)
    private String razonSocial;

    @Column(nullable = false, length = 20, unique = true)
    private String cuit;

    @Column(nullable = false, length = 255)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cliente_servicio",
            joinColumns = @JoinColumn(name = "idCliente"),
            inverseJoinColumns = @JoinColumn(name = "idServicio")
    )
    private Set<Servicio> serviciosContratados = new HashSet<>();

    // relacion con incidentes?
}
