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
@Table(name = "incidente_detalle")
public class IncidenteDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIncidenteDetalle;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idServicio", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idIncidente", nullable = false)
    private Incidente incidente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTipoProblema", nullable = false)
    private TipoProblema tipoProblema;

}
